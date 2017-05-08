package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
        dialogoAlert.setIcon(context.getResources().getDrawable(R.drawable.icono_error));
        dialogoAlert.setMessage(context.getResources().getString(R.string.msj_error_datos_servicio));
        dialogoAlert.setCancelable(true);
        dialogoAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dialogoAlert.create().show();
    }

    public static void dialogoErrorConexion(Context context){
        android.app.AlertDialog.Builder dialogoAlert  = new android.app.AlertDialog.Builder(context);
        dialogoAlert.setTitle(context.getResources().getString(R.string.titulo_error));
        dialogoAlert.setIcon(context.getResources().getDrawable(R.drawable.icono_sin_wifi));
        dialogoAlert.setMessage(context.getResources().getString(R.string.msj_error_conexion_internet));
        dialogoAlert.setCancelable(true);
        dialogoAlert.setPositiveButton("Entendido", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {}
        });
        dialogoAlert.create().show();
    }

    public  static void dialogoVerificarConexionInternet(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_sin_wifi));
        progressDialog.setTitle(context.getResources().getString(R.string.error_conexion));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_conexion_sin_proceso));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, context.getResources().getString(R.string.cancelar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de fechas datos vacios
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoDatosVacios(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_datos_vacios));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_datos_vacios));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

}
