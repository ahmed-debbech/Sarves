package com.debbech.sarves;

import android.content.ContentResolver;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.provider.ContactsContract;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link SearchFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SearchFragment extends Fragment {

    private EditText names;
    private Button search;
    private TextView nores;
    private ArrayList<Contact> listcont;
    private ContactsAdapter adapter;
    private RecyclerView contacts;

    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    public SearchFragment() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SearchFragment.
     */
    // TODO: Rename and change types and number of parameters
    public static SearchFragment newInstance(String param1, String param2) {
        SearchFragment fragment = new SearchFragment();
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
        return inflater.inflate(R.layout.search_fragment, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        this.names = getView().findViewById(R.id.names);
        this.search = getView().findViewById(R.id.search);
        this.nores = getView().findViewById(R.id.nores);
        contacts = (RecyclerView) getView().findViewById(R.id.list);
        contacts.setHasFixedSize(true);
        contacts.setLayoutManager(new LinearLayoutManager(getActivity()));
        adapter = new ContactsAdapter(getContext(), listcont, "115");

        //work
        addListeners();
    }

    private void addListeners(){
        search.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                InputMethodManager inputMethodManager = (InputMethodManager)getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                inputMethodManager.hideSoftInputFromWindow(v.getApplicationWindowToken(),0);
                applyAdapter(names.getText().toString());
            }
        });
        names.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                applyAdapter(s.toString());
            }
        });
    }
    public void applyAdapter(String s){
        contacts.setAdapter(null);
        listcont = Utils.loadContacts(getActivity());
        if(!s.toString().equals("")){
            SharedPreferences sharedPref = getActivity().getSharedPreferences("keep", getActivity().MODE_PRIVATE);
            String ss = sharedPref.getString("ussd", "-1");
            adapter.setUSSD(ss);
            ArrayList<Contact> ll = new ArrayList<>();
            for(Contact c : listcont){
                if(c.getName().toLowerCase().startsWith(s.toString().toLowerCase())){
                    ll.add(c);
                }
            }
            if(ll.size() == 0){
                nores.setVisibility(View.VISIBLE);
            }else{
                nores.setVisibility(View.GONE);
            }
            adapter.setList(ll);
        }else {
            if(listcont.size() == 0){
                nores.setVisibility(View.VISIBLE);
            }else{
                nores.setVisibility(View.GONE);
            }
            adapter.setList(listcont);
        }
        contacts.setAdapter(adapter);
        adapter.notifyDataSetChanged();
    }
}