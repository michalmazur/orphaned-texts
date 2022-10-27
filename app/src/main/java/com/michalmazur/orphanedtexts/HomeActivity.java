package com.michalmazur.orphanedtexts;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.content.ContextCompat;

import com.google.android.material.button.MaterialButton;

public class HomeActivity extends AppCompatActivity {

    public final static int REQUEST_CODE = 11;

    private boolean checkPermission(String permission) {
        return ContextCompat.checkSelfPermission(
                this, permission) ==
                PackageManager.PERMISSION_GRANTED;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        if(getSupportActionBar() != null) {
            getSupportActionBar().setHomeAsUpIndicator(R.mipmap.ic_launcher_foreground);
            getSupportActionBar().setHomeButtonEnabled(false);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        MaterialButton allow = findViewById(R.id.allow);
        allow.setOnClickListener(view -> requestPermissions(new String[]{Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS},
                REQUEST_CODE));

        if (checkPermission(Manifest.permission.READ_SMS)
                && checkPermission(Manifest.permission.READ_CONTACTS)
        ) {
            startActivity();
        }

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_CODE) {// If request is cancelled, the result arrays are empty.
            if (grantResults.length > 0) {
                boolean notGranted = false;
                for (int grantResult : grantResults) {
                    if (grantResult != PackageManager.PERMISSION_GRANTED) {
                        notGranted = true;
                        break;
                    }
                }
                if (!notGranted) {
                    startActivity();
                }
            }
        }
    }

    private void startActivity() {
        startActivity(new Intent(HomeActivity.this, OrphanedTextsActivity.class));
        finish();
    }
}