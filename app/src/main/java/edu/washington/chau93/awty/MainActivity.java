package edu.washington.chau93.awty;

import android.app.AlarmManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;


public class MainActivity extends ActionBarActivity {
    private String TAG = "AWTY";
    private AlarmManager alarmMgr;
    private PendingIntent pendingSpam;
    private boolean spamming;
    private ViewHolder viewHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Get the views and put them in a holder
        getViews();

        checkForExistingAlarm();

        if(spamming){
            viewHolder.sendBtn.setText(R.string.stopSpammingLabel);
        }

        startActivity();
    }

    public void getViews(){
        // initialize view holder
        viewHolder = new ViewHolder();

        // get views
        viewHolder.message = (EditText) findViewById(R.id.message);
        viewHolder.phoneNumber = (EditText) findViewById(R.id.phoneNumber);
        viewHolder.duration = (EditText) findViewById(R.id.minutes);
        viewHolder.awtyMessages = (TextView) findViewById(R.id.awtyMessages);
        viewHolder.sendBtn = (Button) findViewById(R.id.sendBtn);
    }

    private void checkForExistingAlarm() {
        boolean alarmUp = (
                PendingIntent.getBroadcast(
                        this,
                        0,
                        new Intent(this, SpamForYou.class),
                        PendingIntent.FLAG_NO_CREATE
                ) != null);
        if(alarmUp){
            Log.d(TAG, "An alarm is already up.");
            spamming = true;
            makePendingSpam();
            viewHolder.disableViews();
            viewHolder.awtyMessages.setVisibility(View.VISIBLE);
        } else {
            Log.d(TAG, "No alarm is currently up. Start fresh!");
            spamming = false;
        }
    }

    public void startActivity() {
        // initialize some stuff
        alarmMgr = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        // set click listener
        viewHolder.sendBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!spamming){
                    doTheSpamThing();
                } else {
                    stopTheSpamThing();
                }
            }
        });
    }

    // start the alarm manager to spam
    private void doTheSpamThing(){
        if(validateFields()){
            Log.d(TAG, "Fields validated!");

            // Start a new alarm.
            String message = getString(viewHolder.message);
            String phoneNumber = getString(viewHolder.phoneNumber);
            int minutes = Integer.parseInt(getString(viewHolder.duration));

            long time = minutes * 60 * 1000;

            // Make the pending intent
            makePendingSpam(message, phoneNumber);

            // Set the timer
            alarmMgr.setInexactRepeating(
                    AlarmManager.ELAPSED_REALTIME_WAKEUP,
                    time, time,
                    pendingSpam
            );
            spamming = true;

            // Disable the views so their not editable
            viewHolder.disableViews();

            // Let the user know.
            Toast.makeText(MainActivity.this, R.string.initSpamToast, Toast.LENGTH_SHORT).show();
            viewHolder.awtyMessages.setVisibility(View.VISIBLE);

            viewHolder.sendBtn.setText(R.string.stopSpammingLabel);
        } else {
            Log.d(TAG, "Validation failed!");
            Toast.makeText(MainActivity.this, R.string.failedValidationToast, Toast.LENGTH_SHORT).show();
        }
    }

    // make the pending intent for the alarm manager
    private void makePendingSpam(){ makePendingSpam("", ""); }
    private void makePendingSpam(String message, String phoneNumber) {
        // Make pending spam should never be called before getting validated.
        Intent intent = new Intent(MainActivity.this, SpamForYou.class);
        if(!message.equals("") && !phoneNumber.equals("")) {
            intent.putExtra("message", message);
            intent.putExtra("phoneNumber", phoneNumber);
        }
        pendingSpam = PendingIntent.getBroadcast(MainActivity.this, 0, intent, 0);
    }

    // stop the alarm manager from spamming
    private void stopTheSpamThing(){
        if(alarmMgr != null) {
            // change the text
            viewHolder.sendBtn.setText(R.string.send);
            viewHolder.awtyMessages.setVisibility(View.GONE);

            // cancel the alarm and pending intent
            alarmMgr.cancel(pendingSpam);
            pendingSpam.cancel();
            spamming = false;

            // enable the views
            viewHolder.enableViews();

            // let the user know
            Toast.makeText(MainActivity.this, R.string.stopSpamToast, Toast.LENGTH_SHORT).show();
        }
    }

    // validate all of the fields
    private boolean validateFields(){
        Log.d(TAG, "======== VALIDATING FIELDS ========");
        boolean msgResult, phoneResult, minResult;
        // message must be greater than 5 characters.
        msgResult =  getString(viewHolder.message).length() >= 5;
        Log.d(TAG, "Validating message: " + msgResult);

        // phone number must pass regex check
        phoneResult = validatePhoneNumber(getString(viewHolder.phoneNumber));
        Log.d(TAG, "Validating phone number: " + phoneResult);

        // minutes cannot be negative or 0. must be integer
        String minuteString = getString(viewHolder.duration);
        minResult = (minuteString.length() > 0) && (Integer.parseInt(minuteString) > 0);
        Log.d(TAG, "Validating minutes: " + minResult);

        return msgResult && phoneResult && minResult;
    }

    // Validate phone numbers
    private boolean validatePhoneNumber(String phoneNo) {
        return phoneNo.matches("\\d{10}") ||
                phoneNo.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{4}") ||
                phoneNo.matches("\\(\\d{3}\\)-\\d{3}-\\d{4}");

    }

    // get message from edit text
    private String getString(EditText et){
        return et.getText().toString().trim();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    private static class ViewHolder {
        public EditText message;
        public EditText phoneNumber;
        public EditText duration;
        public TextView awtyMessages;
        public Button sendBtn;

        public void disableViews() {
            message.setEnabled(false);
            phoneNumber.setEnabled(false);
            duration.setEnabled(false);
        }

        public void enableViews(){
            message.setEnabled(true);
            phoneNumber.setEnabled(true);
            duration.setEnabled(true);
        }
    }
}
