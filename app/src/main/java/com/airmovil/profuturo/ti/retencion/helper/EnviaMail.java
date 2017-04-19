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

public class EnviaMail {
        public static void sendMail(JSONObject obj,String servicio,final Context context, final VolleyCallback callback){
        //public static void sendMail(int tipo,String usuario,String correo,Boolean detalle,String numeroEmpleado,String idGerencia,String fechaIni,String fechaFin,String servicio,final Context context, final VolleyCallback callback){
        /*JSONObject obj = new JSONObject();

        // TODO: Formacion del JSON request
        //email reporte asesor
        if( tipo == 1) {
            try {
                JSONObject rqt = new JSONObject();
                rqt.put("correo", correo);
                rqt.put("detalle", detalle);
                rqt.put("numeroEmpleado", numeroEmpleado);
                JSONObject periodo = new JSONObject();
                periodo.put("fechaFin", fechaFin);
                periodo.put("fechaInicio", fechaIni);
                rqt.put("periodo", periodo);
                rqt.put("usuario", usuario);
                obj.put("rqt", rqt);
                Log.d("datos", "REQUEST-->" + obj);
            } catch (JSONException e) {
                Config.msj(context, "Error", "Error al formar los datos");
            }
        }

        if( tipo == 2) {
            try {
                JSONObject rqt = new JSONObject();
                rqt.put("correo", correo);
                rqt.put("detalle", detalle);
                rqt.put("idGerencia", idGerencia);
                JSONObject periodo = new JSONObject();
                periodo.put("fechaFin", fechaFin);
                periodo.put("fechaInicio", fechaIni);
                rqt.put("periodo", periodo);
                rqt.put("usuario", usuario);
                obj.put("rqt", rqt);
                Log.d("datos", "REQUEST-->" + obj);
            } catch (JSONException e) {
                Config.msj(context, "Error", "Error al formar los datos");
            }
        }*/
        //Creating a json array request
            Log.d("datos", "REQUEST--> EN METODO" + obj);
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, servicio, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                            //Log.d("RESponse", "RESPONSE: ->" + response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString());
                        //Config.msj(context,"Error conexión", "Lo sentimos ocurrio un right_in, puedes intentar revisando tu conexión.");
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

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
        void onError(String end);
    }
}
