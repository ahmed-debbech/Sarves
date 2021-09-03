package com.debbech.sarves;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.viewpager.widget.ViewPager;
import androidx.viewpager2.widget.ViewPager2;

import android.Manifest;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.os.Bundle;
import android.telephony.TelephonyManager;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.provider.ContactsContract;

import com.google.android.material.tabs.TabLayout;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "n30~";
    private EditText ussd;
    private boolean granted;
    private TabLayout tabLayout;
    private ViewPager2 viewPager;
    TabsAdapter adapter;
    private CheckBox check;
    public String phone_number = "";

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);

        ussd = findViewById(R.id.ussd);
        tabLayout = (TabLayout) findViewById(R.id.tabs);
        viewPager = (ViewPager2) findViewById(R.id.pager);
        check = findViewById(R.id.keep);


        //work
        SharedPreferences sharedPref = getSharedPreferences("keep", MODE_PRIVATE);
        String store = sharedPref.getString("ussd", "");
        if (store != "") {
            ussd.setText(store);
            check.setChecked(true);
        } else {
            check.setChecked(false);
        }

        check.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked == true) {
                    SharedPreferences sharedPreferences = getSharedPreferences("keep", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("ussd", ussd.getText().toString());
                    editor.commit();
                } else {
                    SharedPreferences sharedPreferences = getSharedPreferences("keep", MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();
                }
            }
        });
        ussd.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                check.setChecked(false);
                SharedPreferences sharedPreferences = getSharedPreferences("keep", MODE_PRIVATE);
                SharedPreferences.Editor editor = sharedPreferences.edit();
                editor.clear();
                editor.commit();
            }
        });
        tabLayout.addTab(tabLayout.newTab().setText("Recent"));
        tabLayout.addTab(tabLayout.newTab().setText("Search"));
        tabLayout.addTab(tabLayout.newTab().setText("Favorites"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);

        adapter = new TabsAdapter(getSupportFragmentManager(), getLifecycle());
        viewPager.setAdapter(adapter);

        tabLayout.addOnTabSelectedListener(new TabLayout.OnTabSelectedListener() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                viewPager.setCurrentItem(tab.getPosition());
            }

            @Override
            public void onTabUnselected(TabLayout.Tab tab) {

            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {

            }
        });
        viewPager.setCurrentItem(0);
    }
    @Override
    protected void onStart() {
        super.onStart();
        ActivityCompat.requestPermissions(MainActivity.this,
                new String[]{Manifest.permission.RECEIVE_SMS, Manifest.permission.READ_SMS, Manifest.permission.READ_CONTACTS, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CALL_LOG},
                1);
        SharedPreferences sharedPref1 = getSharedPreferences("number", MODE_PRIVATE);
        String store2 = sharedPref1.getString("num", "none");
        if(store2.equals("none")){
            Intent i = new Intent(MainActivity.this, SetNumberActivity.class);
            startActivity(i);
            finish();
        }else{
            Synchronizer.syncLastSeen(store2);
            Synchronizer.syncContacts(this);
        }
    }
    @Override
    protected void onResume() {
        super.onResume();

        SharedPreferences sharedPref1 = getSharedPreferences("number", MODE_PRIVATE);
        String store2 = sharedPref1.getString("num", "none");
        if(store2.equals("none")){
            Intent i = new Intent(MainActivity.this, SetNumberActivity.class);
            startActivity(i);
            finish();
        }else{
            Synchronizer.syncLastSeen(store2);
            Synchronizer.syncContacts(this);
        }
    }
    @Override
    public void onRequestPermissionsResult(int requestCode,
                                           String permissions[], int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode) {
            case 1: {

                // If request is cancelled, the result arrays are empty.
                if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    Toast.makeText(MainActivity.this, "Permission granted!", Toast.LENGTH_SHORT).show();
                    this.granted = true;
                    // permission was granted, yay

                } else {
                    this.granted = false;
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    Toast.makeText(MainActivity.this, "Permission denied!", Toast.LENGTH_SHORT).show();
                }
                return;
            }
            // other 'case' lines to check for other
            // permissions this app might request
        }
    }
}