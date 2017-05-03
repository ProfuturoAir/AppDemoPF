package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * Created by tecnicoairmovil on 25/04/17.
 */

public class ServicioEmailJSON {
    private static final String TAG = ServicioEmailJSON.class.getSimpleName();
    private static String linea = "\n _______________ \n";
    private static Connected connected;

    public static final void enviarEmailReporteGerencias(final Context context, TextView textViewEmail, final boolean detalle, final int idGerencia, final String fechaInicio, final String fechaFin){
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                connected = new Connected();
                dialog.setContentView(R.layout.custom_layout);
                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                        final String datoEditText = editText.getText().toString().trim();
                        final String datoSpinner = spinner.getSelectedItem().toString();

                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(context, "Error", "Ingresa email valido");
                        }else{

                            String email = datoEditText+"@"+datoSpinner;
                            if(connected.estaConectado(context)){
                                JSONObject obj = new JSONObject();
                                JSONObject rqt = new JSONObject();
                                JSONObject periodo = new JSONObject();
                                try {
                                    rqt.put("correo", email);
                                    rqt.put("detalle", detalle);
                                    rqt.put("idGerencia", idGerencia);
                                    periodo.put("fechaInicio", fechaInicio);
                                    periodo.put("fechaFin", fechaFin);
                                    rqt.put("periodo", periodo);
                                    rqt.put("usuario", Config.usuarioCusp(context));
                                    obj.put("rqt", rqt);
                                    Log.d(TAG, "<-RQT->" + linea + obj + linea);
                                } catch (JSONException e) {
                                    Config.msj(context, "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_GERENCIA,context,new EnviaMail.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        int status;
                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }
                                        if(status == 200) {
                                            Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(context, "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                    }
                                });
                            }else{
                                Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public static final void enviarEmailReporteSucursales(final Context context, TextView textViewEmail, final int idSucursal, final String fechaInicio, final String fechaFin, final boolean detalle){
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout);
                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                        final String datoEditText = editText.getText().toString().trim();
                        final String datoSpinner = spinner.getSelectedItem().toString();

                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(context, "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            if(connected.estaConectado(context)){
                                    JSONObject obj = new JSONObject();
                                    JSONObject rqt = new JSONObject();
                                try {
                                    rqt.put("correo", email);
                                    rqt.put("detalle", detalle);
                                    rqt.put("idSucursal", idSucursal);
                                    JSONObject periodo = new JSONObject();
                                    periodo.put("fechaFin", Dialogos.fechaSiguiente());
                                    periodo.put("fechaInicio", Dialogos.fechaActual());
                                    rqt.put("periodo", periodo);
                                    rqt.put("usuario", Config.usuarioCusp(context));
                                    obj.put("rqt", rqt);
                                    Log.d("datos", "REQUEST-->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(context, "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_SUCURSAL,context,new EnviaMail.VolleyCallback() {

                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        int status;
                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }
                                        if(status == 200) {
                                            Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(context, "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                    }
                                });
                            }else{
                                Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public static final void enviarEmailReporteAsespres(final Context context, TextView textViewEmail, final String fechaInicio, final String fechaFin, final String idAsesor, final boolean detalle){
        // TODO: Inicia la peticion, para el envio del email
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout);
                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);
                // TODO: Spinner
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                        final String datoEditText = editText.getText().toString();
                        final String datoSpinner = spinner.getSelectedItem().toString();
                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(context, "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            if(connected.estaConectado(context)){
                                JSONObject obj = new JSONObject();
                                try {
                                    JSONObject rqt = new JSONObject();
                                    rqt.put("correo", email);
                                    rqt.put("detalle", detalle);
                                    rqt.put("numeroEmpleado", idAsesor);
                                    JSONObject periodo = new JSONObject();
                                    periodo.put("fechaFin", fechaFin);
                                    periodo.put("fechaInicio", fechaInicio);
                                    rqt.put("periodo", periodo);
                                    rqt.put("usuario", Config.usuarioCusp(context));
                                    obj.put("rqt", rqt);
                                    Log.d("EnviarEmail", "<-RQT->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(context, "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_ASESOR,context,new EnviaMail.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        Log.d("RESPUESTA SUCURSAL", result.toString());
                                        int status;
                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                        if(status == 200) {
                                            Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(context, "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Log.d("RESPUESTA ERROR", result);
                                        Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                    }
                                });
                            }else{
                                Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public static final void enviarEmailReporteAsistencia(final Context context, TextView textViewEmail, final int idGerencia, final int idSucursal, final String idAsesor, final String fechaInicio, final String fechaFin, final boolean detalle){
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout);
                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);
                // TODO: Spinner
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                        final String datoEditText = editText.getText().toString().trim();
                        final String datoSpinner = spinner.getSelectedItem().toString();
                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(context, "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            if(connected.estaConectado(context)){
                                JSONObject obj = new JSONObject();
                                try {
                                    JSONObject rqt = new JSONObject();
                                    rqt.put("correo", email);
                                    rqt.put("detalle", detalle);
                                    rqt.put("idSucursal", idSucursal);
                                    rqt.put("idGerencia", idGerencia);
                                    rqt.put("numeroEmpleado", idAsesor);
                                    JSONObject periodo = new JSONObject();
                                    periodo.put("fechaFin", fechaFin);
                                    periodo.put("fechaInicio", fechaInicio);
                                    rqt.put("periodo", periodo);
                                    rqt.put("usuario", Config.usuarioCusp(context));
                                    obj.put("rqt", rqt);
                                    Log.d(TAG, "<- RQT -> \n" + obj + "\n");
                                } catch (JSONException e) {
                                    Config.msj(context, "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_ASISTENCIA,context,new EnviaMail.VolleyCallback() {

                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        Log.d("RESPUESTA SUCURSAL", result.toString());
                                        int status;

                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                        if(status == 200) {
                                            Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(context, "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                                    }
                                });
                            }else{
                                Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public static final void enviarEmailReporteAsistenciaDetalles(final Context context, TextView textViewEmail, final String numeroEmpleado, final String fechaInicio, final String fechaFin){
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout);
                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);
                // TODO: Spinner
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);
                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                        final String datoEditText = editText.getText().toString();
                        final String datoSpinner = spinner.getSelectedItem().toString();
                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(context, "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            if(connected.estaConectado(context)){
                                JSONObject obj = new JSONObject();
                                try {
                                    JSONObject rqt = new JSONObject();
                                    rqt.put("correo", email);
                                    rqt.put("numeroEmpleado", numeroEmpleado);
                                    JSONObject periodo = new JSONObject();
                                    periodo.put("fechaFin", fechaInicio);
                                    periodo.put("fechaInicio", fechaFin);
                                    rqt.put("periodo", periodo);
                                    obj.put("rqt", rqt);
                                    Log.d(TAG, "<- RQT ->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(context, "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_ASISTENCIA_DETALLE,context,new EnviaMail.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        int status;
                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                        if(status == 200) {
                                            Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(context, "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Log.d("RESPUESTA ERROR", result);
                                        Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                    }
                                });
                            }else{
                                Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    public static final void enviarEmailReporteClientes(final Context context, TextView textViewEmail, final int idGerencia, final int idSucursal, final int tipoBuscar, final int idCita1,
                                                        final String datosCliente1, final int retenido, final String numeroEmpleado, final String fechaInicio, final String fechaFin, final boolean detalle){
        textViewEmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(context);
                dialog.setContentView(R.layout.custom_layout);

                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);

                // TODO: Spinner
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                        final String datoEditText = editText.getText().toString();
                        final String datoSpinner = spinner.getSelectedItem().toString();

                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(context, "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            final InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                            if(connected.estaConectado(context)){
                                JSONObject obj = new JSONObject();
                                JSONObject rqt = new JSONObject();
                                JSONObject filtro = new JSONObject();
                                JSONObject filtroCliente = new JSONObject();
                                JSONObject periodo = new JSONObject();
                                try {
                                    rqt.put("correo", email);
                                    rqt.put("detalle", detalle);
                                    rqt.put("filtro", filtro);
                                    filtro.put("cita", 0);
                                    filtro.put("filtroCliente", Config.filtroClientes(tipoBuscar, numeroEmpleado));
                                    filtro.put("filtroRetencion", 0);
                                    filtro.put("idSucursal", 0);
                                    filtro.put("numeroEmpleado", numeroEmpleado);
                                    periodo.put("fechaInicio", fechaInicio);
                                    periodo.put("fechaFin", fechaFin);
                                    rqt.put("periodo", periodo);
                                    filtro.put("idGerencia", idGerencia);
                                    rqt.put("numeroEmpleado", numeroEmpleado);
                                    obj.put("rqt", rqt);
                                    Log.d("sendJson", " REQUEST -->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(context, "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_CLIENTE,context,new EnviaMail.VolleyCallback() {
                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        Log.d("RESPUESTA SUCURSAL", result.toString());
                                        int status;

                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                        if(status == 200) {
                                            Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(context, "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                    }
                                });
                            }else{
                                Config.msj(context, "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
                        }
                    }
                });
                dialog.show();
            }
        });
    }

}
