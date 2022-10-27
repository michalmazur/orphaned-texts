package com.michalmazur.orphanedtexts;

import android.content.ActivityNotFoundException;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteException;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.FileProvider;

import com.google.android.material.dialog.MaterialAlertDialogBuilder;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

public class OrphanedTextsActivity extends AppCompatActivity {

    public final boolean DEBUG = false;
    Uri uri = Uri.parse("content://sms/raw");
    List<Orphan> orphans;
    private static final String PREFS_NAME = "preferences";
    private static final String DATABASE_LAST_EMPTIED = "database_last_emptied";

    private void getMessages() {
        try {
            orphans = getSmsReader().getOrphans(getContentResolver());
            displayOrphanList();
        } catch (SQLiteException e) {
            invalidateOptionsMenu();
        } catch (Exception e) {
            Log.e("Messages", "Error", e);
        }
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setTitle("");
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher_foreground);
            getSupportActionBar().setHomeButtonEnabled(true);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        getMessages();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.orphaned_texts, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.export) {
            export();
            return true;
        } else if (item.getItemId() == R.id.delete_all) {
            deleteAll();
            return true;
        } else if (item.getItemId() == R.id.refresh) {
            refresh();
            return true;
        } else {
            return super.onOptionsItemSelected(item);
        }
    }

    public String convertDateToString(Date date) {
        return new SimpleDateFormat("MMMM dd yyyy HH:mm:ss", Locale.ENGLISH).format(date.getTime());
    }

    public void displayOrphanList() {
        ListView lv = (ListView) findViewById(R.id.listView1);
        List<HashMap<String, String>> items = new ArrayList<>();
        for (Orphan o : orphans) {
            HashMap<String, String> map = new HashMap<>();
            map.put("sender", o.getContactName());
            map.put("datetime", convertDateToString(o.getDate()));
            map.put("message", o.getMessageBody());
            items.add(map);
        }
        Log.d("COUNT", String.valueOf(items.size()));

        ((TextView) findViewById(R.id.count)).setText(getString(R.string.total_number, items.size()));
        ((TextView) findViewById(R.id.database_last_emptied)).setText(getString(R.string.last_emptied, readDatabaseLastEmptiedPreference()));
        String[] from = new String[]{"sender", "datetime", "message"};
        int[] to = new int[]{R.id.sender, R.id.datetime, R.id.message};
        lv.setAdapter(new SimpleAdapter(this, items, R.layout.lvitem, from, to));

        lv.setOnItemLongClickListener((adapterView, view, i, l) -> {
            ClipboardManager clipboard = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
            ClipData clip = ClipData.newPlainText("Orphaned Texts", items.get(i).get("message"));
            clipboard.setPrimaryClip(clip);

            Toast.makeText(this, "Message copied", Toast.LENGTH_SHORT).show();
            Log.i("ListView", "Long click copy");
            return false;
        });
    }

    public RawSmsReader getSmsReader() {
        if (DEBUG) {
            return new RawSmsReader();
        } else {
            return new RawSmsReader(this.getApplicationContext());
        }
    }

    public void deleteAll() {
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                deleteAllRecords();
                displayOrphanList();
            }
        };

        new MaterialAlertDialogBuilder(this, R.style.AlertDialogTheme)
                .setTitle("Warning")
                .setIcon(R.drawable.ic_baseline_warning_amber_24)
                .setMessage("Are you sure you want to delete all orphaned messages?")
                .setPositiveButton("Yes", dialogClickListener)
                .setNegativeButton("No", dialogClickListener).show();
    }

    public void deleteAllRecords() {
        int deletedRecords = getContentResolver().delete(uri, null, null);
        Toast.makeText(this, deletedRecords + " orphaned texts deleted", Toast.LENGTH_SHORT).show();
        orphans = getSmsReader().getOrphans(getContentResolver());
        saveDatabaseLastEmptiedPreference();
    }

    public void refresh() {
        getMessages();
        Toast.makeText(this, "Refreshed", Toast.LENGTH_SHORT).show();
    }

    public void export() {
        try {
            File file = new File(getCacheDir(), "Orphaned Texts " + convertDateToString(new Date()) + ".csv");
            FileWriter fw = new FileWriter(file.getAbsoluteFile());
            BufferedWriter bw = new BufferedWriter(fw);
            bw.write(new CsvConverter().convert(orphans));
            bw.close();
            fw.close();

            Intent sendIntent = new Intent(Intent.ACTION_SEND);
            sendIntent.setType("text/csv");
            sendIntent.putExtra(Intent.EXTRA_SUBJECT, "Orphaned Texts");
            Uri uri = FileProvider.getUriForFile(this, getString(R.string.authorities), file);
            sendIntent.putExtra(Intent.EXTRA_STREAM, uri);

            startActivity(sendIntent);
        } catch (ActivityNotFoundException e) {
            new MaterialAlertDialogBuilder(getBaseContext())
                    .setMessage("Orphaned texts could not be exported, no supported apps found!")
                    .setPositiveButton(R.string.ok, null)
                    .create()
                    .show();
        } catch (Exception e) {
            Log.e("EXPORT", "Failed", e);
        }
    }

    private void saveDatabaseLastEmptiedPreference() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(DATABASE_LAST_EMPTIED, new Date().getTime());
        editor.apply();
    }

    private String readDatabaseLastEmptiedPreference() {
        SharedPreferences settings = getSharedPreferences(PREFS_NAME, MODE_PRIVATE);
        long milliseconds = settings.getLong(DATABASE_LAST_EMPTIED, 0);
        if (milliseconds > 0) {
            return convertDateToString(new Date(milliseconds));
        } else {
            return "never";
        }
    }
}