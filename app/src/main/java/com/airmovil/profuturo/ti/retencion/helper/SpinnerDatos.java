package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;

/**
 * Created by tecnicoairmovil on 27/04/17.
 */

public class SpinnerDatos {

    /**
     * Método que agrega las gerencias al spinner, agrega los eventos OnItemSelected
     * para actualizar los valores de las variables de la clase
     * Config: ID_GERENCIA y ID_GERENCIA_POSICION
     *
     * @param context
     * @param spinner
     * @param mPosicion
     */
    public static void spinnerGerencias(Context context, Spinner spinner, int mPosicion){
        spinner.setAdapter(Config.getAdapter(context,new String[]{"_id","nombre"},Config.getIdGerencia(),Config.getNombreGerencia()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Config.ID_GERENCIA = (int) id;
                Config.ID_GERENCIA_POSICION = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner.setSelection(mPosicion);
    }

    /**
     * Método que agrega las gerencias al spinner, agrega los eventos OnItemSelected
     * para actualizar los valores de las variables de la clase
     * Config: ID_SUCURSAL y ID_SUCURSAL_POSICION
     *
     * @param context
     * @param spinner
     * @param mPosicion
     */
    public static void spinnerSucursales(Context context, Spinner spinner, int mPosicion){
        spinner.setAdapter(Config.getAdapter(context,new String[]{"_id","nombre"},Config.getIdSucusal(),Config.getNombreSucursal()));
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Config.ID_SUCURSAL= (int) id;
                Config.ID_SUCURSAL_POSICION = position;
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner.setSelection(mPosicion);
    }

    /**
     * Método que asigna los eventos OnItemSelected del spinner
     * que sirve para asignar el color del texto al primer Item
     * del Spinner
     *
     * @param spinner
     */
    public static void spinnerEncuesta2(final Context context, Spinner spinner){
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                ((TextView) parent.getChildAt(0)).setTextColor((position==0)? Color.GRAY:context.getResources().getColor(R.color.colorPrimaryDark));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
    }

}
