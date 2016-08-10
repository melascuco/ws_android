package com.melascuco.android.fakedesk;

import android.app.Activity;
import android.os.Environment;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;
import java.util.Date;

public class MainActivity extends Activity {

  private File f;

  @Override
  protected void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);

    File rutaSd = Environment.getExternalStorageDirectory();
    f = new File(rutaSd.getAbsolutePath(), "fakedsk_log.txt");
    Log.d("fakedesk", "Ruta: " + f.getAbsolutePath());

    resetLogfile();

    View layout = findViewById(R.id.backlayout);
    layout.setOnLongClickListener(new View.OnLongClickListener() {
      @Override
      public boolean onLongClick(View v) {
        Log.d("fakedesk", "onLongClick, finish");
        writeLogfile("Tranquilo, que ha salido con un longClick.");
        //finish();
        android.os.Process.killProcess(android.os.Process.myPid());
        return true;
      }
    });
    layout.setOnTouchListener(new View.OnTouchListener() {
      public boolean onTouch(View v, MotionEvent event) {
        Log.d("fakedesk", "onClick, write");
        if (event.getAction() == MotionEvent.ACTION_DOWN){
          int x = (int) event.getX();
          int y = (int) event.getY();
          writeLogfile("   -Click" + "(" + x + "," + y + ") " + new Date());
          if (x < 150 && y < 150) {
            writeLogfile("Saliendo por coordenadas...");
            //finish();
            android.os.Process.killProcess(android.os.Process.myPid());
          }
        }
        return true;
      }
    });
  }


  @Override
  protected void onPause() {
    super.onPause();
    Log.d("fakedesk", "onPause...");
    writeLogfile("   -onPause, " + new Date());
    //finish();
    //android.os.Process.killProcess(android.os.Process.myPid());
  }


  private void writeLogfile(String line) {
    Log.d("fakedesk", "Write log file");
    try {
      OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f, true));
      fout.write(line + "\n");
      fout.close();
      Log.d("fakedesk", "Fichero escrito sin problemas!");
    } catch (Exception e) {
      Log.e("fakedesk", "Error al escribir fichero a tarjeta SD");
      e.printStackTrace();
    }
  }

  private void resetLogfile() {
    Log.d("fakedesk", "Reset log file");
    try {
      OutputStreamWriter fout = new OutputStreamWriter(new FileOutputStream(f, false));
      fout.write(" RESETEADO " + new Date() + "\n");
      fout.close();
      Log.d("fakedesk", "Fichero reseteado sin problemas!");
    } catch (Exception e) {
      Log.e("fakedesk", "Error al resetear fichero a tarjeta SD");
      e.printStackTrace();
    }
  }
}
