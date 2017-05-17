package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.LocationManager;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.View;
import android.widget.DatePicker;
import android.widget.TextView;
import android.support.v7.app.AlertDialog.Builder;

import java.util.Calendar;
import java.util.Timer;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio;

/**
 * Created by tecnicoairmovil on 25/04/17.
 */

public class Dialogos extends Activity{

    Activity activity = this;
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;

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

    /**
     * Muestra mensaje de error, seleccion de un apartado de spinner
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoSinSeleccionSpinner(Context context, String apartado){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_lista));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_seleccion_spinner));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_spinner) + " " + apartado);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * @param ctx parametro 1 referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoErrorDatos(Context ctx){
        AlertDialog.Builder dialog  = new AlertDialog.Builder(ctx);
        dialog.setTitle(ctx.getResources().getString(R.string.titulo_error_datos));
        dialog.setMessage(ctx.getResources().getString(R.string.msj_error_datos));
        dialog.setCancelable(true);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create().show();
    }

    /**
     * Dialogo de geolocalizacion
     * @param ctx referencia de llamada del fragmento o actividad
     */
    public static final void dialogoActivarLocalizacion(final Context ctx){
        AlertDialog.Builder builder = new AlertDialog.Builder(ctx);
        builder.setCancelable(false);
        builder.setTitle(ctx.getResources().getString(R.string.titulo_activacion_gps));
        builder.setMessage(ctx.getResources().getString(R.string.msj_activacion_gps));
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                ctx.startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * Muestra mensaje de fechas vacias
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoFechasVacias(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_fechas_vacias));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_fechas));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * @param ctx parametro 1 referencia de la llamada, fragmento o actividad
     * @param titulo parametro 2 titulo del dialogo
     * @param msj parametro 3 mensaje del dialogo
     */
    public static final void dialogoErrorRespuesta(Context ctx, String titulo, String msj){
        AlertDialog.Builder dialog  = new AlertDialog.Builder(ctx);
        dialog.setTitle("Error: " + titulo);
        dialog.setMessage(msj);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create().show();
    }

    /**
     * @param ctx parametro 1 referencia de la llamada, fragmento o actividad
     * @param titulo parametro 2 titulo del dialogo
     * @param msj parametro 3 mensaje del dialogo
     */
    public static final void msj(Context ctx, String titulo, String msj){
        AlertDialog.Builder dialog  = new AlertDialog.Builder(ctx);
        dialog.setTitle(titulo);
        dialog.setMessage(msj);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create().show();
    }

    /**
     * Muestra mensaje, aviso de contenido limpio en firma
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoContenidoLimpio(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_limpio));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_limpiar));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_limpio));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, se requiere una firma
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoRequiereFirma(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_firma));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_firma));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_vacio_firma));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, en el documento no existente (INE o IFE)
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoNoExisteUnDocumento(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_en_documento));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_documento));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * @param context referencia de la llamada, fragmento o actividad
     * @param title titulo del dialogo
     * @param message mensaje del dialogo
     * @param time tiempo de disminucion del dialogo
     * @return el mensja e de dialogo
     */
    public static ProgressDialog msjTime(Context context, CharSequence title, CharSequence message, int time) {
        Config.MyTask task = new Config.MyTask();
        mTimer.schedule(task, 0, time);
        dialog = ProgressDialog.show(context, title, message);
        return dialog;
    }

    /**
     * No existe algun paramatro del obj json
     * @param context referencia de la llamada, fragment o actividad
     */
    public static void dialogoNoExisteDato(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_error));
        progressDialog.setTitle(context.getResources().getString(R.string.titulo_no_existe_parametro));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_no_existe_parametro));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Dialogo de geolocalizacion
     * @param context referencia de llamada del fragmento o actividad
     */
    public static final void dialogoAvisoModoAvion(final Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        builder.setTitle(context.getResources().getString(R.string.titulo_modo_avion_aviso));
        builder.setMessage(context.getResources().getString(R.string.msj_modo_avion_aviso));
        builder.setInverseBackgroundForced(true);
        builder.setPositiveButton("Sí", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                context.startActivity(new Intent(Settings.ACTION_AIRPLANE_MODE_SETTINGS));
            }
        });
        builder.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        AlertDialog alert = builder.create();
        alert.show();
    }
}
