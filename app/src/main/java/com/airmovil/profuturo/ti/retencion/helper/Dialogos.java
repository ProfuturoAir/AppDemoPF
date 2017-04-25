package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;

import java.util.Calendar;

import com.airmovil.profuturo.ti.retencion.R;
/**
 * Created by tecnicoairmovil on 25/04/17.
 */

public class Dialogos extends Activity{

    public static String fechaActual(){
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int anio = calendar.get(Calendar.YEAR);
        return String.valueOf(dia)+"-"+String.valueOf(mes +1)+"-"+String.valueOf(anio);
    }

    public static String fechaSiguiente(){
        Calendar calendar = Calendar.getInstance();
        int dia = calendar.get(Calendar.DAY_OF_MONTH);
        int mes = calendar.get(Calendar.MONTH);
        int anio = calendar.get(Calendar.YEAR);
        return String.valueOf(dia+1)+"-"+String.valueOf(mes + 1)+"-"+String.valueOf(anio);
    }



    public static void dialogoFechaInicio(final Context context, final TextView textView){

        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;
                Calendar calendar = Calendar.getInstance();
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int anio = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, anio, mes, dia);
                datePickerDialog.show();
            }
        });
    }

    public static void dialogoFechaFin(final Context context, final TextView textView){
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                DatePickerDialog datePickerDialog;
                Calendar calendar = Calendar.getInstance();
                int dia = calendar.get(Calendar.DAY_OF_MONTH);
                int mes = calendar.get(Calendar.MONTH);
                int anio = calendar.get(Calendar.YEAR);
                datePickerDialog = new DatePickerDialog(context,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                textView.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                //fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, anio, mes, dia);
                datePickerDialog.show();
            }
        });
    }

    public static void dialogoErrorServicio(Context context){
        android.app.AlertDialog.Builder dialogoAlert  = new android.app.AlertDialog.Builder(context);
        dialogoAlert.setTitle(context.getResources().getString(R.string.titulo_error));
        dialogoAlert.setIcon(context.getResources().getDrawable(R.drawable.icono_sin_wifi));
        dialogoAlert.setMessage(context.getResources().getString(R.string.msj_error_datos_servicio));
        dialogoAlert.setCancelable(true);
        dialogoAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dialogoAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogoAlert.create().show();
    }

    public static void dialogoErrorConexion(Context context){
        android.app.AlertDialog.Builder dialogoAlert  = new android.app.AlertDialog.Builder(context);
        dialogoAlert.setTitle(context.getResources().getString(R.string.titulo_error));
        dialogoAlert.setIcon(context.getResources().getDrawable(R.drawable.icono_sin_wifi));
        dialogoAlert.setMessage(context.getResources().getString(R.string.msj_error_conexion_internet));
        dialogoAlert.setCancelable(true);
        dialogoAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dialogoAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
            }
        });
        dialogoAlert.create().show();
    }

}
