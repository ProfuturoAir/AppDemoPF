package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONObject;
import java.util.Map;

/**
 * Created by tecnicoAirmovil on 12/04/17.
 */

public class EnviaMail {
    public static void sendMail(JSONObject obj,String servicio,final Context context, final VolleyCallback callback){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, servicio, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                            callback.onSuccess(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        callback.onError(error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(context);
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }

    public interface VolleyCallback{
        void onSuccess(JSONObject result);
        void onError(String end);
    }
}