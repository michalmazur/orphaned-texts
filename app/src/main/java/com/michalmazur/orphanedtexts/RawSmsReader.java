package com.michalmazur.orphanedtexts;

import java.util.ArrayList;
import java.util.Date;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;
import android.telephony.SmsMessage;
import android.util.Log;

public class RawSmsReader {
    private final ArrayList<Orphan> orphans = new ArrayList<>();
    private Context context;

    public RawSmsReader() {

    }

    public RawSmsReader(Context c) {
        this.context = c;
    }

    public ArrayList<Orphan> getOrphans(ContentResolver contentResolver) {
        if (this.context == null) {
            Log.d("ORPHAN", "No context. Will return fake data.");
            return getFakeOrphans();
        }

        String uriString = "content://sms/raw";
        Uri uri = Uri.parse(uriString);
        Cursor c = context.getContentResolver()
                .query(uri, null, null, null, null /* "_id limit 10" */);

        while (c.moveToNext()) {
            Orphan newOrphan = new Orphan();
            newOrphan.setId(c, c.getColumnIndex("_id"));
            newOrphan.setDate(c, c.getColumnIndex("date"));
            newOrphan.setReferenceNumber(c, c.getColumnIndex("reference_number"));
            newOrphan.setCount(c, c.getColumnIndex("count"));
            newOrphan.setSequence(c, c.getColumnIndex("sequence"));
            newOrphan.setDestinationPort(c, c.getColumnIndex("destination_port"));
            newOrphan.setAddress(c, c.getColumnIndex("address"));
            newOrphan.setContactName(getContactName(newOrphan.getAddress(), contentResolver));
            try {
                int pduColumnIndex = c.getColumnIndex("pdu");
                SmsMessage message;
                if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.P) {
                    message = SmsMessage.createFromPdu(this.hexStringToByteArray(c.getString(pduColumnIndex)), SmsMessage.FORMAT_3GPP);
                } else {
                    message = SmsMessage.createFromPdu(this.hexStringToByteArray(c.getString(pduColumnIndex)));
                }
                newOrphan.setMessageBody(message.getMessageBody());
            } catch (NullPointerException | StringIndexOutOfBoundsException e) {
                newOrphan.setMessageBody("<cannot read message body>");
            }
            orphans.add(newOrphan);
        }

        return orphans;
    }

    private ArrayList<Orphan> getFakeOrphans() {
        ArrayList<Orphan> orphans = new ArrayList<>();
        for (int i = 0; i < 10; i++) {
            Orphan o = new Orphan();
            o.setId(i);
            o.setDate(new Date());
            o.setReferenceNumber(0);
            o.setCount(1);
            o.setSequence(2);
            o.setDestinationPort(5);
            o.setAddress("+1 123 456 7890");
            o.setContactName("Scott");
            o.setMessageBody("this is my message");
            orphans.add(o);
        }
        return orphans;
    }


    public String getContactName(String phoneNumber, ContentResolver contentResolver) {
        Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
                Uri.encode(phoneNumber));
        String[] columns = {ContactsContract.PhoneLookup.DISPLAY_NAME};
        String displayName = phoneNumber;
        Cursor cursor = contentResolver.query(lookupUri, columns, null, null, null);
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                int columnIndex = cursor.getColumnIndex(ContactsContract.PhoneLookup.DISPLAY_NAME);
                if (columnIndex >= 0) {
                    displayName = cursor.getString(columnIndex);
                } else {
                    Log.e("DISPLAY NAME", "columnIndex=" + columnIndex);
                }
            }
            cursor.close();
        }
        return displayName;
    }

    // http://stackoverflow.com/questions/140131/convert-a-string-representation-of-a-hex-dump-to-a-byte-array-using-java
    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(
                    s.charAt(i + 1), 16));
        }
        return data;
    }
}