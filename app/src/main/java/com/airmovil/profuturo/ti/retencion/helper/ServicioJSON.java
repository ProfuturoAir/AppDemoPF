package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.airmovil.profuturo.ti.retencion.model.EnviosPendientesModel;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TecnicoAirmovil on 4/20/17.
 */

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
                db = new SQLiteHandler(contex);
                Connected connected = new Connected();
                if (connected.estaConectado(contex)) {
                    Cursor todos = db.getAllPending();
                    getDatos1 = new ArrayList<>();
                    try {
                        while (todos.moveToNext()) {
                            EnviosPendientesModel getDatos2 = new EnviosPendientesModel();
                            getDatos2.setId_tramite(todos.getInt(0));
                            getDatos2.setNombreCliente(todos.getString(1));
                            getDatos2.setNumeroCuenta(todos.getString(2));
                            getDatos2.setHora(todos.getString(3));
                            getDatos1.add(getDatos2);
                        }
                    } finally {
                        todos.close();
                    }
                    EnviaJSON enviaPrevio = new EnviaJSON();
                    Cursor pendientes = db.getAllPending();
                    try {
                        while (pendientes.moveToNext()) {
                            getDatos1.remove(pendientes.getString(0));
                            enviaPrevio.sendPrevios(pendientes.getString(0),contex);
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
        super.onDestroy();
    }
}
