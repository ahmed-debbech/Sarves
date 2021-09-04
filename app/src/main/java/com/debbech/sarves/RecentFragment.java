package com.debbech.sarves;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.CallLog;
import android.provider.ContactsContract;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.CompoundButton;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link RecentFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class RecentFragment extends Fragment {
    private ContactsAdapter adapter;
    private RecyclerView contacts;
    private CheckBox reverse;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public RecentFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment RecentFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static RecentFragment newInstance(String param1, String param2) {
        RecentFragment fragment = new RecentFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recent, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //work
        contacts = (RecyclerView) getView().findViewById(R.id.list_recent);
        contacts.setHasFixedSize(true);
        contacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        reverse = getView().findViewById(R.id.reverse);
        SharedPreferences sharedPref1 = getActivity().getSharedPreferences("reverse", getActivity().MODE_PRIVATE);
        String ss1 = sharedPref1.getString("rev", "none");
        if(ss1.equals("none")){
            reverse.setChecked(false);
        }else{
            reverse.setChecked(true);
        }
        reverse.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if(isChecked == true){
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("reverse", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.putString("rev", "yes");
                    editor.commit();

                    show();
                }else{
                    SharedPreferences sharedPreferences = getActivity().getSharedPreferences("reverse", getActivity().MODE_PRIVATE);
                    SharedPreferences.Editor editor = sharedPreferences.edit();
                    editor.clear();
                    editor.commit();

                    show();
                }
            }
        });
        show();
    }
    private void show(){
        SharedPreferences sharedPref= getActivity().getSharedPreferences("keep", getActivity().MODE_PRIVATE);
        String ss = sharedPref.getString("ussd", "-1");
        ArrayList<Contact> hh = getCallDetails();
        if(hh != null) {
            adapter = new ContactsAdapter(getContext(), hh, ss);
        }
        contacts.setAdapter(adapter);
    }
    private ArrayList<Contact> getCallDetails() {
        class Holder{
            public Contact cont;
            public String date;
            public Holder(Contact c, String date){
                this.cont = c;
                this.date = date;
            }
        }
        StringBuffer sb = new StringBuffer();
        ArrayList<Holder> cc = new ArrayList<>();
        Cursor managedCursor = getActivity().managedQuery(CallLog.Calls.CONTENT_URI, null, null, null, null);
        int number = managedCursor.getColumnIndex(CallLog.Calls.NUMBER);
        int type = managedCursor.getColumnIndex(CallLog.Calls.TYPE);
        int date = managedCursor.getColumnIndex(CallLog.Calls.DATE);
        int duration = managedCursor.getColumnIndex(CallLog.Calls.DURATION);
        sb.append("Call Log :");
        int i=0;
        String holder = "";
        if(!reverse.isChecked()) {
            while ((managedCursor.moveToNext()) && (i <= 29)) {
                if(!holder.equals(managedCursor.getString(number))) {
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    Contact c;
                    String rr = contactExists(getActivity(), phNumber);
                    if (rr != "") {
                        c = new Contact(rr + "", phNumber + "");
                    } else {
                        c = new Contact(phNumber + "", phNumber + "");
                    }
                    cc.add(new Holder(c, callDate));
                    i++;
                    holder = phNumber;
                }
            }
        }else{
            managedCursor.moveToLast();
            while ((managedCursor.moveToPrevious()) && (i <= 29)) {
                if(!holder.equals(managedCursor.getString(number))) {
                    String phNumber = managedCursor.getString(number);
                    String callType = managedCursor.getString(type);
                    String callDate = managedCursor.getString(date);
                    Date callDayTime = new Date(Long.valueOf(callDate));
                    String callDuration = managedCursor.getString(duration);
                    Contact c;
                    String rr = contactExists(getActivity(), phNumber);
                    if (rr != "") {
                        c = new Contact(rr + "", phNumber + "");
                    } else {
                        c = new Contact(phNumber + "", phNumber + "");
                    }
                    cc.add(new Holder(c, callDate));
                    i++;
                    holder = phNumber;
                }
            }
        }
        //managedCursor.close();
        if(cc.size() > 0){
            Collections.sort(cc, new Comparator<Holder>() {
                @Override
                public int compare(Holder o1, Holder o2) {
                    return o2.date.compareTo(o1.date);
                }
            });
            ArrayList<Contact> cfc = new ArrayList<>();
            for(Holder h : cc){
                cfc.add(h.cont);
            }
            return cfc;
        }
        return null;
    }
    public String contactExists(Activity _activity, String number) {
        if (number != null) {
            Uri lookupUri = Uri.withAppendedPath(ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number));
            String[] mPhoneNumberProjection = { ContactsContract.PhoneLookup._ID, ContactsContract.PhoneLookup.NUMBER, ContactsContract.PhoneLookup.DISPLAY_NAME };
            Cursor cur = _activity.getContentResolver().query(lookupUri, mPhoneNumberProjection, null, null, null);
            try {
                if (cur.moveToFirst()) {
                    return cur.getString(cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME));
                }
            } finally {
                if (cur != null)
                    cur.close();
            }
            return "";
        } else {
            return "";
        }
    }
}