package com.debbech.sarves;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;

public class ContactsAdapter extends RecyclerView.Adapter<ContactsAdapter.ContactsViewHolder> {

    Context ctx;
    ArrayList<Contact> list;
    String ussd;
    public ContactsAdapter(Context ctx, ArrayList<Contact> ct, String ussd) {
        this.ctx = ctx;
        this.list = ct;
        this.ussd = ussd;
    }

    @NonNull
    @Override
    public ContactsViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View vi  = LayoutInflater.from(ctx).inflate(R.layout.contact_item, parent, false);
        return new ContactsViewHolder(vi);
    }

    @Override
    public void onBindViewHolder(@NonNull ContactsViewHolder holder, int position) {
        Contact cc = list.get(position);
        holder.name.setText(cc.getName());
        holder.number.setText(cc.getNumber());
        SharedPreferences sharedPref = ctx.getSharedPreferences("users", Context.MODE_PRIVATE);
        String nn = sharedPref.getString(holder.number.getText().toString(), "none");
        Log.d("5r5", sharedPref.getAll().toString());
        if(!nn.equals("none")){
            holder.fav.setImageResource(R.drawable.ic_baseline_star_24);
            holder.isfav = true;
        }else{
            holder.fav.setImageResource(R.drawable.btn_star_off);
            holder.isfav = false;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    public void setList(ArrayList<Contact> listcont) {
        this.list = listcont;
    }

    public void setUSSD(String toString) {
        ussd = toString;
    }

    public class ContactsViewHolder extends RecyclerView.ViewHolder{
        private TextView name;
        private TextView number;
        private Button sendx;
        private ImageButton fav;
        private boolean isfav;

        public ContactsViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.contact_name);
            number = itemView.findViewById(R.id.contact_number);
            sendx = itemView.findViewById(R.id.send_x);
            fav = itemView.findViewById(R.id.fav_star);
            fav.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(isfav == false) {
                        SharedPreferences sharedPreferences = ctx.getSharedPreferences("users", ctx.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.putString(number.getText().toString(), name.getText().toString());
                        editor.commit();
                        isfav = true;
                        fav.setImageResource(R.drawable.ic_baseline_star_24);
                    }else{
                        SharedPreferences sharedPreferences = ctx.getSharedPreferences("users", ctx.MODE_PRIVATE);
                        SharedPreferences.Editor editor = sharedPreferences.edit();
                        editor.remove(number.getText().toString());
                        editor.commit();
                        isfav =false;
                        fav.setImageResource(R.drawable.btn_star_off);
                    }
                }
            });
            sendx.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    SharedPreferences sharedPref= itemView.getContext().getSharedPreferences("keep", itemView.getContext().MODE_PRIVATE);
                    String ss1 = sharedPref.getString("ussd", "none");
                    if(!ss1.equals("none")) {
                        Toast.makeText(itemView.getContext(), "USSD used: " + ss1, Toast.LENGTH_SHORT).show();
                        Toast.makeText(itemView.getContext(), "Sending to: " + name.getText(), Toast.LENGTH_SHORT).show();
                        Intent in = new Intent(Intent.ACTION_CALL);
                        String dd = number.getText().toString().replaceAll("\\D+", "");
                        in.setData(Uri.parse("tel:" + "*" + ussd + "*" + dd + Uri.encode("#")));
                        if (ActivityCompat.checkSelfPermission(ctx, Manifest.permission.CALL_PHONE) != PackageManager.PERMISSION_GRANTED) {
                            return;
                        }
                        ctx.startActivity(in);
                    }else{
                        Toast.makeText(itemView.getContext(), "Please enter the USSD Code", Toast.LENGTH_SHORT).show();
                    }
                }
            });
        }
    }
}
