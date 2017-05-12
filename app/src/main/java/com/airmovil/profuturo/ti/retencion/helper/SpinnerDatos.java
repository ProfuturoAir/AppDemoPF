package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

/**
 * Created by tecnicoairmovil on 27/04/17.
 */

public class SpinnerDatos {

    public static void spinnerGerencias(Context context, Spinner spinner){
        spinner.setAdapter(Config.getAdapter(context,new String[]{"_id","nombre"},Config.getIdGerencia(),Config.getNombreGerencia()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "id" + id, Toast.LENGTH_SHORT).show();
               Config.ID_GERENCIA = (int) id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

    public static void spinnerSucursales(Context context, Spinner spinner){
        spinner.setAdapter(Config.getAdapter(context,new String[]{"_id","nombre"},Config.getIdGerencia(),Config.getNombreGerencia()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Toast.makeText(view.getContext(), "id" + id, Toast.LENGTH_SHORT).show();
                //
                Config.ID_SUCURSAL= (int) id;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }


}