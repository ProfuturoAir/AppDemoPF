package com.airmovil.profuturo.ti.retencion.helper;

/**
 * Created by Juan on 4/20/17.
 */

import android.app.IntentService;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.util.Log;


import com.airmovil.profuturo.ti.retencion.model.EnviosPendientesModel;

import java.util.ArrayList;
import java.util.List;


public class ServicioJSON extends Service{

    private Context contex;
    private List<EnviosPendientesModel> getDatos1;
    private SQLiteHandler db;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onStart(Intent intent, int startId) {
        // TODO Auto-generated method stub
        super.onStart(intent, startId);

        final Handler handler = new Handler();

        final Context contex = getApplicationContext();
        final Runnable r = new Runnable() {
            public void run() {

                Log.d("-->INICIA SERVICIO:", "FirstService started");

                db = new SQLiteHandler(contex);

                Log.d("-->SERVICIO CADA 60s", "inicia");

                Connected connected = new Connected();
                if (connected.estaConectado(contex)) {

                    Cursor todos = db.getAllPending();
                    getDatos1 = new ArrayList<>();

                    try {
                        while (todos.moveToNext()) {
                            EnviosPendientesModel getDatos2 = new EnviosPendientesModel();
                            Log.d("-->PROCESA SERVICIO:", todos.getInt(0) + "");
                            getDatos2.setId_tramite(todos.getInt(0));
                            getDatos2.setNombreCliente(todos.getString(1));
                            getDatos2.setNumeroCuenta(todos.getString(2));
                            getDatos2.setHora(todos.getString(3));

                            Log.d("HOLA", "EL ID : " + todos.getInt(0));
                            getDatos1.add(getDatos2);
                        }
                    } finally {
                        todos.close();
                    }

                    EnviaJSON enviaPrevio = new EnviaJSON();
                    Cursor pendientes = db.getAllPending();
                    try {
                        while (pendientes.moveToNext()) {
                            //EnviosPendientesModel getDatos2 = new EnviosPendientesModel();

                            Log.d("HOLA", "EL ID : " + pendientes.getString(0));
                            //if(){
                            Log.d("Eliminado", "Exitoso");
                            getDatos1.remove(pendientes.getString(0));
                            Log.d("-->send :", pendientes.getString(0));
                            Log.d("-->send1:", contex.toString());
                            enviaPrevio.sendPrevios(pendientes.getString(0),contex);
                            //Log.d("Respuesta","AQUI: "+enviaPrevio.sendPrevios(pendientes.getString(0), getContext()));
                        }
                    } finally {
                        pendientes.close();
                    }

                }


                handler.postDelayed(this, 50000);
            }
        };
        handler.postDelayed(r, 50000);
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
    }
}
