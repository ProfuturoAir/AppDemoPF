package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.airmovil.profuturo.ti.retencion.R;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * Created by tecnicoairmovil on 25/04/17.
 */

public class ServicioEmailJSON {
    private static final String TAG = ServicioEmailJSON.class.getSimpleName();
    private static String linea = "\n _______________ \n";
    private static Connected connected = new Connected();
    private static Dialog dialog;
    private static Button btn;
    private static Spinner spinner;
    private static ArrayAdapter<String> adapter;

    /**
     * Metodo para mandar a llamar los elementos emergentes del XML, formulario de envio de email
     * @param context
     */
    public static void dialogo(Context context){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.custom_layout);
        btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
        spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);
        adapter = new ArrayAdapter<String>(context, R.layout.spinner_item_azul, Config.EMAIL);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);
    }

    /**
     * Envio de datos del fragmento y detalle ReporteGerencia
     * @param context referencia de fragmento
     * @param detalle determina si los datos a enviar son: false(todos los datos de la busqueda de filtros) o true(detalle de la lista)
     * @param idGerencia gerencia ID
     * @param fechaInicio rango inicial de la primera fecha
     * @param fechaFin rango final de la segunda fecha
     */
    public static final void enviarEmailReporteGerencias(final Context context, final boolean detalle, final int idGerencia, final String fechaInicio, final String fechaFin){
        dialogo(context);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                final String datoEditText = editText.getText().toString().trim();
                final String datoSpinner = spinner.getSelectedItem().toString();
                if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                    Dialogos.dialogoEmailVacio(context);
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
                       Dialogos.dialogoErrorConexion(context);
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * Envio de datos del fragmento y detalle ReporteSucursales
     * @param context referencia del fragmento
     * @param idSucursal id de la sucursal
     * @param fechaInicio rango inicial de la primera fecha
     * @param fechaFin rango final de la segunda fecha
     * @param detalle determina si los datos a enviar son: false(todos los datos de la busqueda de filtros) o true(detalle de la lista)
     */
    public static final void enviarEmailReporteSucursales(final Context context, final int idSucursal, final String fechaInicio, final String fechaFin, final boolean detalle){
        dialogo(context);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                final String datoEditText = editText.getText().toString().trim();
                final String datoSpinner = spinner.getSelectedItem().toString();
                if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                    Dialogos.dialogoEmailVacio(context);
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
                            periodo.put("fechaInicio", fechaInicio);
                            periodo.put("fechaFin", fechaFin);
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
                                Log.d(TAG,"response--->"+result);
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
                       Dialogos.dialogoErrorConexion(context);
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * Envio de datos del fragmento y detalle ReporteAsesores
     * @param context referencia del fragmento
     * @param fechaInicio rango inicial de la primera fecha
     * @param fechaFin rango final de la segunda fecha
     * @param idAsesor id asesor
     * @param detalle determina si los datos a enviar son: false(todos los datos de la busqueda de filtros) o true(detalle de la lista)
     */
    public static final void enviarEmailReporteAsesores(final Context context, final String fechaInicio, final String fechaFin, final String idAsesor, final boolean detalle){
        dialogo(context);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                final String datoEditText = editText.getText().toString().trim();
                final String datoSpinner = spinner.getSelectedItem().toString();
                if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                    Dialogos.dialogoEmailVacio(context);
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
                       Dialogos.dialogoErrorConexion(context);
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * Envio de datos del fragmento y detalle ReporteClientes
     * @param context referencia del fragmento
     * @param idSucursal id sucursal
     * @param tipoBuscar tipo de parametro a buscar (CURP, NSS o NUMEROCUENTA)
     * @param idCita con cita y sin cita
     * @param datosCliente dato del cliente, en asignacion con (CURP, NSS o NUMERODCUENTA)
     * @param retenido Cliente es 1. Retenido o 2. No retenido
     * @param numeroEmpleado numero del asesor que atendio al cliente
     * @param fechaInicio rango inicial de la primera fecha
     * @param fechaFin rango final de la segunda fecha
     * @param detalle determina si los datos a enviar son: false(todos los datos de la busqueda de filtros) o true(detalle de la lista)
    */
    public static final void enviarEmailReporteClientes(final Context context, final int idSucursal, final int tipoBuscar, final int idCita, final String datosCliente, final int retenido, final String numeroEmpleado, final String fechaInicio, final String fechaFin, final boolean detalle){
        dialogo(context);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                final String datoEditText = editText.getText().toString();
                final String datoSpinner = spinner.getSelectedItem().toString();

                if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                    Dialogos.dialogoEmailVacio(context);
                }else{
                    String email = datoEditText+"@"+datoSpinner;
                    Connected connected = new Connected();
                    final InputMethodManager imm = (InputMethodManager) context.getSystemService(Service.INPUT_METHOD_SERVICE);
                    if(connected.estaConectado(context)){
                        JSONObject obj = new JSONObject();
                        JSONObject rqt = new JSONObject();
                        JSONObject filtro = new JSONObject();
                        JSONObject periodo = new JSONObject();
                            try {
                                obj.put("rqt",rqt);
                                    rqt.put("correo", email);
                                    rqt.put("detalle", detalle);
                                    rqt.put("filtro", filtro);
                                        filtro.put("cita", idCita);
                                        filtro.put("filtroCliente", Config.filtroClientes(tipoBuscar, datosCliente));
                                        filtro.put("filtroRetencion", retenido);
                                        filtro.put("idSucursal", idSucursal);
                                        filtro.put("numeroEmpleado", numeroEmpleado);
                                    rqt.put("numeroEmpleado", numeroEmpleado);
                                    rqt.put("periodo", periodo);
                                        periodo.put("fechaInicio", fechaInicio);
                                        periodo.put("fechaFin", fechaFin);
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
                       Dialogos.dialogoErrorConexion(context);
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * Envio de datos del fragmento y detalle ReporteClientes
     * @param context referencia del fragmento
     * @param idGerencia id gerencia
     * @param idSucursal id sucursal
     * @param idAsesor id asesor
     * @param fechaInicio rango de fecha incial
     * @param fechaFin rango de fecha final
     * @param detalle determina si los datos a enviar son: false(todos los datos de la busqueda de filtros) o true(detalle de la lista)
     */
    public static final void enviarEmailReporteAsistencia(final Context context, final int idGerencia, final int idSucursal, final String idAsesor, final String fechaInicio, final String fechaFin, final boolean detalle){
        dialogo(context);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                final String datoEditText = editText.getText().toString().trim();
                final String datoSpinner = spinner.getSelectedItem().toString();
                if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                    Dialogos.dialogoEmailVacio(context);
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
                            Dialogos.msj(context, "Error", "Error al formar los datos");
                        }
                        EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_ASISTENCIA,context,new EnviaMail.VolleyCallback() {

                            @Override
                            public void onSuccess(JSONObject result) {
                                Log.d("response--->", result.toString());
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
                               Dialogos.dialogoErrorConexion(context);
                            }
                        });
                    }else{
                       Dialogos.dialogoErrorConexion(context);
                    }
                }
            }
        });
        dialog.show();
    }

    /**
     * Envio de datos del fragmento y detalle ReporteClientes
     * @param context referencia del fragmento
     * @param numeroEmpleado id numeroEmpleado
     * @param fechaInicio rango de fecha incial
     * @param fechaFin rango de fecha final
     */
    public static final void enviarEmailReporteAsistenciaDetalles(final Context context, final String numeroEmpleado, final String fechaInicio, final String fechaFin){
        dialogo(context);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);
                final String datoEditText = editText.getText().toString();
                final String datoSpinner = spinner.getSelectedItem().toString();
                if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                    Dialogos.dialogoEmailVacio(context);
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
                                if(status == 200) {
                                    Config.msj(context, "Enviando", "Se ha enviado el mensaje al destino");
                                    Config.teclado(context, editText);
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
                       Dialogos.dialogoErrorConexion(context);
                    }
                }
            }
        });
        dialog.show();
    }


}
