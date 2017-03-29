package com.airmovil.profuturo.ti.retencion.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;

import java.util.Timer;
import java.util.TimerTask;

public class Splash extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.splash);

        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                // Comenzara la siguiente Actividad
                Intent mainIntent = new Intent().setClass(Splash.this, Login.class);
                startActivity(mainIntent);
                // Cierra la actividad para que el usuario no pueda volver atrás
                // Actividad pulsando el botón Atrás
                finish();
            }
        };

        // Simula un proceso de carga largo en el asesor_fragmento_inicio de la aplicación.
        Timer timer = new Timer();
        timer.schedule(task, Config.SPLASH_SCREEN_DELEY);
    }
}
