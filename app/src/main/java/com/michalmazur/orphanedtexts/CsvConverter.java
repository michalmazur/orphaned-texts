package com.michalmazur.orphanedtexts;

import java.text.SimpleDateFormat;
import java.util.List;
import java.util.Locale;

public class CsvConverter {
    public String convert(List<Orphan> orphans) {
        StringBuilder sb = new StringBuilder();
        sb.append("_id, date, reference_number, count, sequence, destination_port, contact_name, address, message_body\n");

        for (Orphan orphan : orphans) {
            sb.append(wrapInQuotes(orphan.getId()));
            sb.append(",");
            sb.append(wrapInQuotes(new SimpleDateFormat("MMMM dd yyyy HH:mm:ss", Locale.ENGLISH).format(orphan.getDate().getTime())));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getReferenceNumber()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getCount()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getSequence()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getDestinationPort()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getContactName()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getAddress()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getMessageBody()));
            sb.append("\n");
        }

        return sb.toString();

    }

    private String wrapInQuotes(Object o) {
        return "\"" + o.toString() + "\"";

    }

    private String wrapInQuotes(String s) {
        return "\"" + s + "\"";
    }
}