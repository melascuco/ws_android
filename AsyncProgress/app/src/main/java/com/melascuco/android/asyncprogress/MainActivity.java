package com.melascuco.android.asyncprogress;

import android.app.ProgressDialog;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

// De miguelangellv
// https://miguelangellv.wordpress.com/2011/01/31/creando-tareas-asincronas-en-android-con-asynctask/

public class MainActivity extends AppCompatActivity {

  private ProgressDialog dialog;

  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.activity_main);
    dialog = new ProgressDialog(this);
    dialog.setMessage("Descargando...");
    dialog.setTitle("Progreso");
    dialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
    dialog.setCancelable(false);
    //Realizamos cualquier otra operación necesaria
    //Creamos una nueva instancia y llamamos al método ejecutar
    //pasándole el string.
    new MiTarea().execute("http://www.ejemplo.com/file.zip");

  }

  private class MiTarea extends AsyncTask<String, Float, Integer> {

    protected void onPreExecute() {
      dialog.setProgress(0);
      dialog.setMax(100);
      dialog.show(); //Mostramos el diálogo antes de comenzar
    }

    protected Integer doInBackground(String... urls) {
      /**
       * Simularemos que descargamos un fichero
       * mediante un sleep
       */

      for (int i = 0; i < 250; i++) {
        //Simulamos cierto retraso
        try {
          Thread.sleep(200);
        } catch (InterruptedException e) {
        }

        publishProgress(i / 250f); //Actualizamos los valores
      }

      return 250;
    }

    protected void onProgressUpdate(Float... valores) {
      int p = Math.round(100 * valores[0]);
      dialog.setProgress(p);
    }

    protected void onPostExecute(Integer bytes) {
      dialog.dismiss();
    }
  }
}
