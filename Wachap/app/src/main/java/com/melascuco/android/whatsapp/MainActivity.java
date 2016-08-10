package com.melascuco.android.whatsapp;

import android.os.AsyncTask;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import java.util.Date;

public class MainActivity extends AppCompatActivity {

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    new MailSender().execute();
  }


  private class MailSender extends AsyncTask<Void, Void, Boolean> {

    @Override
    protected Boolean doInBackground(Void... params) {
      try {
        Mail m = new Mail("*********@gmail.com", "**********");

        String[] toArr = {"**********@hotmail.com"};
        m.set_to(toArr);
        m.set_from("**********@gmail.com");
        m.set_subject("Wachap!! "  + new Date());
        m.set_body("Timestamp: " + new Date());
        //m.addAttachment("/sdcard/filelocation");
        return m.send();

      } catch (Exception e) {
        Log.e("WachapApp", "Error: ", e);
        return false;
      }
    }

    @Override
    protected void onPostExecute(Boolean result) {
      Log.d("WachapApp", "Ejecución: " + result);
      Toast.makeText(getApplicationContext(), "Error", Toast.LENGTH_SHORT);
      Handler handler = new Handler();
      handler.postDelayed(new Runnable() {
        public void run() {
          // Actions to do after 5 seconds
          finish();
        }
      }, 5000);
    }
  }
}
