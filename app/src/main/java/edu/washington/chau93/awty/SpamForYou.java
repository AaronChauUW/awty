package edu.washington.chau93.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

/**
 * Created by Aaron on 2/18/2015.
 */
public class SpamForYou extends BroadcastReceiver {
    private String TAG = "SpamForYou";

    @Override
    public void onReceive(Context context, Intent intent) {
        String message = intent.getStringExtra("message");
        String phoneNumber = intent.getStringExtra("phoneNumber");

        Toast.makeText(
                context,
                phoneNumber + ": " + message,
                Toast.LENGTH_SHORT
        ).show();
    }
}
