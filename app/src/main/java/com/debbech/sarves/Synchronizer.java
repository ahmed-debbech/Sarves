package com.debbech.sarves;

import android.content.SharedPreferences;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Map;

public class Synchronizer {

    public static void syncLastSeen(String num){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users/" + num);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd 'at' HH:mm:ss z");
        String currentDateandTime = sdf.format(new Date());
        myRef.setValue(currentDateandTime);
    }
    public static void syncContacts(FragmentActivity act){
        FirebaseDatabase database = FirebaseDatabase.getInstance();
        DatabaseReference myRef = database.getReference("/users");
        myRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<Contact> contacts_list = SearchFragment.loadContacts(act);

                SharedPreferences sharedPref1 = act.getSharedPreferences("sarves_contacts", act.MODE_PRIVATE);
                Map<String, String> list = (Map<String, String>) sharedPref1.getAll();
                ArrayList<String> listcont = new ArrayList<String>();
                Log.d("goo0", "onDataChange: " + list.toString());
                for(Map.Entry<String, String> entry : list.entrySet()){
                    listcont.add(new String(entry.getKey()));
                }
                if(!listcont.isEmpty()){
                    SharedPreferences.Editor editor = null;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (contacts_list.contains(new Contact("0", snap.getKey()))) {
                            if(!listcont.contains(new Contact("0", snap.getKey()))) {
                                Log.d("goo0", "data " + snap.getKey());
                                editor = sharedPref1.edit();
                                editor.putString(snap.getKey(), snap.getKey());
                                editor.commit();
                            }
                        }
                    }
                }else {
                    SharedPreferences.Editor editor = null;
                    for (DataSnapshot snap : snapshot.getChildren()) {
                        if (contacts_list.contains(new Contact("0", snap.getKey()))) {
                            Log.d("goo0", "data " + snap.getKey());
                            editor = sharedPref1.edit();
                            editor.putString(snap.getKey(), snap.getKey());
                            editor.commit();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}
