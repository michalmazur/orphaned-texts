package com.michalmazur.orphanedtexts;
import com.michalmazur.orphanedtexts.Orphan;
import java.util.ArrayList;

public class CsvConverter {
    public String convert(ArrayList<Orphan> orphans) {
        StringBuilder sb = new StringBuilder();
        sb.append("_id, date, reference_number, count, sequence, destination_port, address, message_body\n");

        for (Orphan orphan : orphans) {
            sb.append(wrapInQuotes(orphan.getId()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getDate().toLocaleString()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getReferenceNumber()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getCount()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getSequence()));
            sb.append(",");
            sb.append(wrapInQuotes(orphan.getDestinationPort()));
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