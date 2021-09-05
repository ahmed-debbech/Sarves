package com.debbech.sarves;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.media.Ringtone;
import android.media.RingtoneManager;
import android.net.Uri;
import android.os.Build;
import android.util.Log;
import android.widget.Toast;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

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
                    timer.schedule(task, 20000);

                    Intent i = new Intent(context, StopActivity.class);
                    PendingIntent pen = PendingIntent.getActivity(context, 100, i, PendingIntent.FLAG_CANCEL_CURRENT);

                    if(Build.VERSION.SDK_INT < Build.VERSION_CODES.O) {
                        Log.d("ssarves", "less than oreo");
                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my channel")
                                .setSmallIcon(R.drawable.btn_star_off)
                                .setContentTitle(cong + " wants a call")
                                .setContentIntent(pen)
                                .setContentText(cong + " wants a call")
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, builder.build());
                    }else{
                        Log.d("ssarves", "higher than oreo");

                        NotificationChannel ch = new NotificationChannel("my notif", "name", NotificationManager.IMPORTANCE_DEFAULT);
                        NotificationManager man = context.getSystemService(NotificationManager.class);
                        man.createNotificationChannel(ch);

                        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, "my notif")
                                .setSmallIcon(R.drawable.btn_star_off)
                                .setContentTitle("Someone sent a message")
                                .setContentText(cong + " wants to talk to you")
                                .setContentIntent(pen)
                                .setSilent(true)
                                .setAutoCancel(true);

                        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(context);

                        // notificationId is a unique int for each notification that you must define
                        notificationManager.notify(1, builder.build());
                    }
                }
            }
        }
    }
}
