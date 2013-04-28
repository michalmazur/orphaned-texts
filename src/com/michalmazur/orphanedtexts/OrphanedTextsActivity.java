package com.michalmazur.orphanedtexts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class OrphanedTextsActivity extends Activity {
	
	String uriString;
	Uri uri;
	private String output;

    public OrphanedTextsActivity() {
		super();

		uriString = "content://sms/raw";
		uri = Uri.parse(uriString);
		output = "";
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
//        RawSmsReader reader = new RawSmsReader(this.getApplicationContext());
        RawSmsReader reader = new RawSmsReader();
        
        ArrayList<Orphan> orphans = reader.getOrphans();
        setContentView(R.layout.main);
        output = new CsvConverter().convert(orphans);
        ListView lv = (ListView)findViewById(R.id.listView1);
    	ArrayList<String> items = new ArrayList<String>();
    	for (Orphan o: orphans) {
    		items.add(o.getAddress() + " on " + o.getDate().toLocaleString() + ":\n" + o.getMessageBody() + "\n");
    	}
    	lv.setAdapter(new ArrayAdapter<String>(this, R.layout.lvitem, items));
    }
    
    

    public void deleteAllRecords(View _)
    {
        getContentResolver().query(uri, null, null, null, null /* "_id limit 10" */);
        getContentResolver().delete(uri, null, null);
    }
    
   
    public void email(View _)
    {
    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("plain/text");
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Orphaned Texts");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, output);  
    	startActivity(emailIntent);	
    }    
}