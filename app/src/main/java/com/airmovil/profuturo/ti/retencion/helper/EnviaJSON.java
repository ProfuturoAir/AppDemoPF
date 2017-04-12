package com.airmovil.profuturo.ti.retencion.helper;

import android.app.ProgressDialog;
import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by mHernandez on 12/04/17.
 */

public class EnviaJSON {
    private SQLiteHandler db;
    private static final String TAG = "ENCUESTA";
    private static final String TAG2 = "ENCUESTA 2";

    private String tramite;
    private String id_t;
    private String idTramite;

    private String retencion_encuesta;
    private int estatusTramite;
    private Boolean pregunta1;
    private Boolean pregunta2;
    private Boolean pregunta3;
    private String pregunta4;
    private String pregunta5;
    private String pregunta6;

    private String retencion_encuesta_observaciones;
    private String claveAfore;
    private String email;
    private String pregunta7;
    private String pregunta8;
    private String pregunta9;
    private String estatus;
    private int idMotivo;
    private String leyRegimen;
    private String observacion;
    private String regimen;
    private String telefono;
    private int idAfore;
    private int idEstatus;
    private int idInstituto;
    private int idRegimenPensionario;
    private int idDocumentacion;


    private String retencion_firma;
    private String firmaCliente;
    private Double longitud;
    private Double latitud;

    private String retencion_documentacion;
    private String fechaHoraFin;
    private String ineIfe;
    private int numeroCuenta;
    private String usuario;

    private String statusRs;
    private String firmaString;

    public void sendPrevios(String idTramite,Context context){
        db = new SQLiteHandler(context);

        HashMap<String, String> encuesta = db.getEncuesta(idTramite);
        HashMap<String, String> observaciones = db.getObservaciones(idTramite);
        HashMap<String, String> firma = db.getfirma(idTramite);
        HashMap<String, String> documento = db.getdocumento(idTramite);

        if(!encuesta.isEmpty()){
            Log.d("TIENE Datos: ", "-->Encuesta" );
            idTramite=encuesta.get(SQLiteHandler.FK_ID_TRAMITE);
            estatusTramite=Integer.parseInt(encuesta.get(SQLiteHandler.KEY_ESTATUS_TRAMITE));
            pregunta1=Boolean.parseBoolean(encuesta.get(SQLiteHandler.KEY_PREGUNTA1));
            pregunta2=Boolean.parseBoolean(encuesta.get(SQLiteHandler.KEY_PREGUNTA2));
            pregunta3=Boolean.parseBoolean(encuesta.get(SQLiteHandler.KEY_PREGUNTA3));
            observacion=encuesta.get(SQLiteHandler.KEY_OBSERVACION);
            sendJsonEncuesta(true,idTramite,observacion,pregunta1,pregunta2,pregunta3,estatusTramite,context);

        }

        if(!observaciones.isEmpty()){
            Log.d("TIENE Datos: ", "-->Observaciones" );
            idTramite=observaciones.get(SQLiteHandler.FK_ID_TRAMITE);
            idAfore=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ID_AFORE));
            idMotivo=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ID_MOTIVO));
            idEstatus=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ID_ESTATUS));
            idInstituto=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ID_INSTITUTO));
            idRegimenPensionario=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ID_REGIMEN_PENSIONARIO));
            idDocumentacion=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ID_DOCUMENTACION));
            telefono=observaciones.get(SQLiteHandler.KEY_TELEFONO);
            email=observaciones.get(SQLiteHandler.KEY_EMAIL);
            estatusTramite=Integer.parseInt(observaciones.get(SQLiteHandler.KEY_ESTATUS_TRAMITE));

           // sendJson(true, iParam1IdGerencia, iParam2IdMotivos, iParam3IdEstatus, iParam4IdTitulo, iParam5IdRegimentPensionario, iParam6IdDocumentacion, iParam7Telefono, iParam8Email);

            sendJsonEncuesta2(true,idTramite,idAfore,idMotivo,idEstatus,idInstituto,idRegimenPensionario,idDocumentacion,telefono,email,estatusTramite,context);
        }

        if(!firma.isEmpty()){
            Log.d("TIENE Datos: ", "-->Firma" );
            idTramite=firma.get(SQLiteHandler.FK_ID_TRAMITE);
            estatusTramite=Integer.parseInt(firma.get(SQLiteHandler.KEY_ESTATUS_TRAMITE));
            firmaString=firma.get(SQLiteHandler.KEY_FIRMA);
            latitud = Double.parseDouble(firma.get(SQLiteHandler.KEY_LATITUD));
            longitud = Double.parseDouble(firma.get(SQLiteHandler.KEY_LONGITUD));

            sendJsonFirma(true,idTramite,estatusTramite,firmaString,latitud,longitud,context);
        }

        if(!documento.isEmpty()){
            Log.d("TIENE Datos: ", "-->Documento" );
            idTramite=documento.get(SQLiteHandler.FK_ID_TRAMITE);
            fechaHoraFin = documento.get(SQLiteHandler.KEY_FECHAHORAFIN);
            estatusTramite=Integer.parseInt(documento.get(SQLiteHandler.KEY_ESTATUS_TRAMITE));
            ineIfe = documento.get(SQLiteHandler.KEY_INEIFE);
            numeroCuenta = Integer.parseInt(documento.get(SQLiteHandler.KEY_NUMERO_CUENTA));
            usuario=documento.get(SQLiteHandler.KEY_USUARIO);
            latitud = Double.parseDouble(documento.get(SQLiteHandler.KEY_LATITUD));
            longitud = Double.parseDouble(documento.get(SQLiteHandler.KEY_LONGITUD));

            sendJsonDocumento(true,idTramite,fechaHoraFin,estatusTramite,ineIfe,numeroCuenta,usuario,latitud,longitud,context);
        }

    }
    private void sendJsonEncuesta(final boolean primerPeticion, final String idTramite, String observaciones, Boolean pregunta1, Boolean pregunta2, Boolean pregunta3, int estatusTramite, final Context context) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(context, "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();

        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            JSONObject encuesta = new JSONObject();
            encuesta.put("observaciones", observaciones);
            encuesta.put("pregunta3",pregunta3);
            encuesta.put("pregunta2",pregunta2);
            encuesta.put("pregunta1", pregunta1);
            rqt.put("encuesta", encuesta);
            rqt.put("estatusTramite", estatusTramite);
            rqt.put("idTramite", idTramite);
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(context, "Error", "Error al formar los datos");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_ENCUESTA, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            primerPaso(response,idTramite);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(context,"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    // TODO: REST
    public void sendJsonEncuesta2(final boolean primerPeticion,String idTramite, int idGerencia, int idMotivo, int IdEstatus,
                          int idTitulo, int idRegimentPensionario, int idDocumentacion, String telefono, String email,int estatusTramite, final Context context) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(context, "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("idAfore", idGerencia);
            rqt.put("idMotivo", idMotivo);
            rqt.put("idEstatus", IdEstatus);
            rqt.put("idInstituto", idTitulo);
            rqt.put("idRegimentPensionario", idRegimentPensionario);
            rqt.put("idDocumento", idDocumentacion);
            rqt.put("telefono", telefono);
            rqt.put("email", email);
            rqt.put("estatusTramite", estatusTramite);
            rqt.put("idTramite", idTramite);
            obj.put("rqt", rqt);
            Log.d(TAG2, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(context, "Error", "Error al formar los datos");
        }
        //<editor-fold desc="Creating a json array request">
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_ENCUESTA_2, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            //primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(context,"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
        //</editor-fold>
    }

    private void primerPaso(JSONObject obj,String idTramite){
        Log.d(TAG, "RESPONSE: ->" + obj);
        try {
            statusRs = obj.getString("status");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        if(statusRs == "200") {
            db.deleteEncuesta(idTramite);
        }
    }

    private void sendJsonFirma(final boolean primerPeticion,String idTramite,int estatusTramite,String firmaString,Double latitud,Double longitud,final Context context) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(context, "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();

        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", estatusTramite);
            rqt.put("firmaCliente", firmaString);
            rqt.put("idTramite", idTramite);
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", latitud);
            ubicacion.put("longitud", longitud);
            rqt.put("ubicacion", ubicacion);
            obj.put("rqt", rqt);
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(context, "Error", "Error al formar los datos");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_DOCUMENTO_IFE_INE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            //primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(context,"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }


    private void sendJsonDocumento(final boolean primerPeticion,String idTramite,String fechaHoraFin,int estatusTramite,String ineIfe,int numeroCuenta,String usuario,Double latitud,Double longitud,final Context context) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(context, "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();

        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", estatusTramite);
            rqt.put("fechaHoraFina", fechaHoraFin);
            rqt.put("idTramite", idTramite);
            rqt.put("ineIfe", ineIfe);
            rqt.put("numeroCuenta", numeroCuenta);
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", latitud);
            ubicacion.put("longitud", longitud);
            rqt.put("ubicacion", ubicacion);
            obj.put("rqt", rqt);
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(context, "Error", "Error al formar los datos");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_ENCUESTA_2, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            //primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(context,"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }
}
