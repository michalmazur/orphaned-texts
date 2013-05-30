package com.michalmazur.orphanedtexts;

import java.util.ArrayList;

import android.app.Activity;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.app.AlertDialog.Builder;

public class OrphanedTextsActivity extends Activity {
	
	String uriString;
	Uri uri;
	private String output;
	ArrayList<Orphan> orphans;

    public OrphanedTextsActivity() {
		super();

		uriString = "content://sms/raw";
		uri = Uri.parse(uriString);
		output = "";
	}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        RawSmsReader reader = new RawSmsReader(this.getApplicationContext());
//        RawSmsReader reader = new RawSmsReader();
        
        orphans = reader.getOrphans();
        setContentView(R.layout.main);
        output = new CsvConverter().convert(orphans);
        displayOrphanList();
    }
    
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        return true;
    }
    
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.email:
                email();
            case R.id.delete_all:
                deleteAll();
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    
    public void displayOrphanList() {
    	ListView lv = (ListView)findViewById(R.id.listView1);
    	ArrayList<String> items = new ArrayList<String>();
    	for (Orphan o: orphans) {
    		items.add(o.getAddress() + " on " + o.getDate().toLocaleString() + ":\n" + o.getMessageBody() + "\n");
    	}
    	lv.setAdapter(new ArrayAdapter<String>(this, R.layout.lvitem, items));
    }    
    
    public void deleteAll() {
    	DialogInterface.OnClickListener dialogClickListener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch (which) {
				case DialogInterface.BUTTON_POSITIVE:
					deleteAllRecords();
					displayOrphanList();
					break;
				}
			}
		};
		
		Builder builder = new Builder(this);
		builder.setMessage("Are you sure you want to delete all orphaned messages?")
			.setPositiveButton("Yes", dialogClickListener)
			.setNegativeButton("No", dialogClickListener)
			.show();
    }
    

    public void deleteAllRecords()
    {
        getContentResolver().query(uri, null, null, null, null /* "_id limit 10" */);
        getContentResolver().delete(uri, null, null);
        orphans.clear();
    }
    
   
    public void email()
    {
    	Intent emailIntent = new Intent(android.content.Intent.ACTION_SEND);
    	emailIntent.setType("plain/text");
    	emailIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "Orphaned Texts");
    	emailIntent.putExtra(android.content.Intent.EXTRA_TEXT, output);  
    	startActivity(emailIntent);	
    }    
}
