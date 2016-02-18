package eu.atosresearch.health.contactsfrommobile;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private static final String LOGTAG = MainActivity.class.getSimpleName();

    EditText editTextPhoneNumber;
    EditText editTextSMSBody;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Log.d(LOGTAG, "Code for SMS and Calling from Android");
        editTextPhoneNumber = (EditText) findViewById(R.id.editTextPhoneNumber);
        editTextSMSBody = (EditText) findViewById(R.id.editTextSMSBody);
    }

    public void onClickSendSMS(View v) {
        Log.d(LOGTAG, "Button Send SMS!");

        //Check number is correct
        String phoneNumber = editTextPhoneNumber.getText().toString();
        if (isValidPhoneNumber(phoneNumber)) {
            String smsBody = editTextSMSBody.getText().toString();
            Log.d(LOGTAG, "SMS Body: " + smsBody);
            PendingIntent pi = PendingIntent.getActivity(this, 0,
                    new Intent(this, MainActivity.class), 0);
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phoneNumber, null, smsBody, null, null);
            Log.d(LOGTAG, "SMS Sent to " + phoneNumber);
            Toast.makeText(MainActivity.this, "SMS Sent to " + phoneNumber, Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(MainActivity.this, "Phone number is not correct", Toast.LENGTH_SHORT).show();
        }
    }

    public void onClickVoiceCall(View v) {
        Log.d(LOGTAG, "Button Voice call!");

        String phoneNumber = editTextPhoneNumber.getText().toString();
        if (isValidPhoneNumber(phoneNumber)) {
            Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + phoneNumber));
            String permission = "android.permission.CALL_PHONE";
            if (v.getContext().checkCallingOrSelfPermission(permission) == PackageManager.PERMISSION_GRANTED) {
                startActivity(intent);
            } else {
                Toast.makeText(MainActivity.this, "No permissions to make a voice call", Toast.LENGTH_SHORT).show();
            }
        }  else {
            Toast.makeText(MainActivity.this, "Phone number is not correct", Toast.LENGTH_SHORT).show();
        }

    }


    private boolean isValidPhoneNumber(String phoneCandidate) {
        Log.d(LOGTAG, "Checking if " + phoneCandidate + " is a valid phone number...");
        //validate phone numbers of format "1234567890"
        if (phoneCandidate.matches("\\d{9}")) return true;
            //validating phone number with -, . or spaces
        else if(phoneCandidate.matches("\\d{3}[-\\.\\s]\\d{3}[-\\.\\s]\\d{3}")) return true;
            //validating phone number where area code is in braces ()
        else return false;
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
}
