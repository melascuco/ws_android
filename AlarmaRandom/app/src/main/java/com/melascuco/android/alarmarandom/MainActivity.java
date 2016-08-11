package com.melascuco.android.alarmarandom;

import android.app.Activity;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.Spinner;

import java.util.Random;
import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends Activity {

  private static final String TAG = "alarmarandom";

  private Activity activity;

  private Timer timer;
  private int fromValue;
  private int toValue;
  private Random randomizer;
  private MediaPlayer mp;

  private EditText editFrom;
  private EditText editTo;
  private Spinner spinnerTimeUnits;
  private Button buttonGo;
  private Button buttonCancel;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    Log.d(TAG, "Alarma Random started");

    activity = this;

    editFrom = (EditText) findViewById(R.id.editTextFrom);
    editTo = (EditText) findViewById(R.id.editTextTo);
    spinnerTimeUnits = (Spinner) findViewById(R.id.spinnerMinSec);
    buttonGo = (Button) findViewById(R.id.buttonGo);
    buttonCancel = (Button) findViewById(R.id.buttonCancel);

    //Load audio file
    mp = MediaPlayer.create(this, R.raw.dancing);

    //Hide keyboard
    //when touching spinner
    spinnerTimeUnits.setOnTouchListener(new View.OnTouchListener() {
      @Override
      public boolean onTouch(View v, MotionEvent event) {
        hideSoftKeyboard();
        return false;
      }
    });
    //when touching free space under editText boxes
    FrameLayout frameEmptySpace = (FrameLayout) findViewById(R.id.frameEmptySpace);
    frameEmptySpace.setOnClickListener(new View.OnClickListener() {
      @Override
      public void onClick(View v) {
        hideSoftKeyboard();
      }
    });
  }

  public void hideSoftKeyboard() {
//    InputMethodManager imm = (InputMethodManager) getSystemService(Activity.INPUT_METHOD_SERVICE);
//    imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
    InputMethodManager inputMethodManager = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
    inputMethodManager.hideSoftInputFromWindow(activity.getCurrentFocus().getWindowToken(), 0);
  }

  public void clickGo(View v) {
    Log.d(TAG, "Click go!!");
    //Undertand interval, get values in form
    Log.d(TAG, "Values: " + "From->" + editFrom.getText().toString() + ", To->" + editTo.getText().toString() + ", " + spinnerTimeUnits.getSelectedItem().toString());
    if ("".equalsIgnoreCase(editFrom.getText().toString())) {
      fromValue = Integer.parseInt(editFrom.getHint().toString());
    } else {
      fromValue = Integer.parseInt(editFrom.getText().toString());
    }
    if ("".equalsIgnoreCase(editTo.getText().toString())) {
      toValue = Integer.parseInt(editTo.getHint().toString());
    } else {
      toValue = Integer.parseInt(editTo.getText().toString());
    }
    if (fromValue > toValue) {
      int keepToValue = toValue;
      toValue = fromValue;
      fromValue = keepToValue;
    }
    Log.d(TAG, "From " + fromValue + " to " + toValue);

    if (spinnerTimeUnits.getSelectedItem().equals(getResources().getStringArray(R.array.spinner_time_units)[0])) {
      // minutes
      fromValue = fromValue * 60 * 1000;
      toValue = toValue * 60 * 1000;
    } else if (spinnerTimeUnits.getSelectedItem().equals(getResources().getStringArray(R.array.spinner_time_units)[1])) {
      // seconds
      fromValue = fromValue * 1000;
      toValue = toValue * 1000;
    } else {
      Log.d(TAG, "Time units not set!");
    }
    Log.d(TAG, "Values for randomizer " + fromValue + " to " + toValue);

    //Set randomizer
    randomizer = new Random();
    //Set timer
    timer = new Timer();
    //Schedule loop
    scheduleLoop();

    Log.d(TAG, "Disable/Enable buttons");
    editFrom.setEnabled(false);
    editTo.setEnabled(false);
    spinnerTimeUnits.setEnabled(false);
    buttonGo.setEnabled(false);
    buttonCancel.setEnabled(true);
  }

  private void scheduleLoop() {
    Log.d(TAG, "Schedule loop!");
    int interval = randomizer.nextInt((toValue - fromValue) + 1) + fromValue;
    Log.d(TAG, "Schedule next: " + interval);
    timer.schedule(new TimerTask() {
      @Override
      public void run() {
        Log.d(TAG, "Timer execution");
        Log.d(TAG, "Audio player start!");
        mp.start();
        scheduleLoop();
      }
    }, interval);
  }

  public void clickCancel(View v) {
    //Unset timer
    Log.d(TAG, "Timer stop!");
    timer.cancel();
    Log.d(TAG, "Audio player stop!");
    //Not mp.stop() but pause
    if (mp.isPlaying()) {
      mp.pause();
      mp.seekTo(0);
    }

    Log.d(TAG, "Enable/Disable buttons");
    editFrom.setEnabled(true);
    editTo.setEnabled(true);
    spinnerTimeUnits.setEnabled(true);
    buttonGo.setEnabled(true);
    buttonCancel.setEnabled(false);
  }
}
