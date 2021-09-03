package com.debbech.sarves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.util.Log;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

public class SMSReceiver extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        //Toast.makeText(context, "sms" , Toast.LENGTH_LONG).show();

        Cursor cursor = context.getContentResolver().query(Uri.parse("content://sms/inbox"), null, null,null,null);
        cursor.moveToFirst();
        //Log.d("go9", "onReceive: " + msgData);
        String cong = cursor.getString(2);
        cong = cong.replaceAll("[^0-9]", "");
        if(cong.length() > 8){
            cong = cong.substring(3);
        }

        Log.d("go9", "onReceive: " + cursor.getString(2));
        Toast.makeText(context, "Sarves: Last Sms is " + cong , Toast.LENGTH_LONG).show();
        SharedPreferences sharedPref1 = context.getSharedPreferences("sarves_contacts", context.MODE_PRIVATE);
        Map<String, String> list = (Map<String, String>) sharedPref1.getAll();
        ArrayList<String> listcont = new ArrayList<String>();
        for(Map.Entry<String, String> entry : list.entrySet()){
            listcont.add(new String(entry.getKey()));
        }
        if(!listcont.isEmpty()){
            for(String contact : listcont){
                if(contact.equals(cong)){
                    Toast.makeText(context, "Sarves: your received SMS from " + cong , Toast.LENGTH_LONG).show();
                    Uri notification = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_RINGTONE);
                    Ringtone r = RingtoneManager.getRingtone(context, notification);
                    r.play();
                    TimerTask task = new TimerTask() {
                        @Override
                        public void run() {
                            r.stop();
                        }
                    };
                    Timer timer = new Timer();
                    timer.schedule(task, 25000);
                    Intent io = new Intent(context, CallPopUpActivity.class);
                    io.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(io);
                }
            }
        }
    }
}
