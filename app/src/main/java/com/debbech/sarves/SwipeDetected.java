package com.debbech.sarves;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.widget.Toast;

public class SwipeDetected extends BroadcastReceiver {
    @Override
    public void onReceive(Context context, Intent intent) {
        Toast.makeText(context, "sm444s" , Toast.LENGTH_LONG).show();
    }
}
