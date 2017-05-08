package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.util.Base64;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by tecnicoairmovil on 5/2/17.
 */

public class VolleySingleton {

    private IResult mResultCallback = null;
    private Context mContext;

    private static VolleySingleton mInstance;
    private RequestQueue mRequestQueue;


    public void postDataVolley(final String requestType, String url,JSONObject sendObj){
        try {
            JsonObjectRequest jsonObj = new JsonObjectRequest(url,sendObj, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestType,response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestType,error);
                }
            }){
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    String credentials = Config.USERNAME+":"+Config.PASSWORD;
                    String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    headers.put("Authorization", auth);
                    return headers;
                }
            };

            addToRequestQueue(jsonObj);

        }catch(Exception e){
            Log.d("ERROR:", e.toString());
        }
    }

    public void getDataVolley(final String requestType, String url){
        try {

            JsonObjectRequest jsonObj = new JsonObjectRequest(Request.Method.GET, url, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    if(mResultCallback != null)
                        mResultCallback.notifySuccess(requestType, response);
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    if(mResultCallback != null)
                        mResultCallback.notifyError(requestType, error);
                }
            })
            {
                @Override
                public Map<String, String> getHeaders() throws AuthFailureError {
                    HashMap<String, String> headers = new HashMap<>();
                    headers.put("Content-Type", "application/json; charset=utf-8");
                    String credentials = Config.USERNAME+":"+Config.PASSWORD;
                    String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                    headers.put("Authorization", auth);
                    return headers;
                }
            };

            addToRequestQueue(jsonObj);

        }catch(Exception e){
            Log.d("ERROR:", e.toString());
        }
    }

    private VolleySingleton(IResult resultCallback,Context context) {
        mResultCallback = resultCallback;
        mContext = context;
        mRequestQueue = getRequestQueue();
    }

    public static synchronized VolleySingleton getInstance(IResult resultCallback, Context context) {
        mInstance = new VolleySingleton(resultCallback, context);
        return mInstance;

    }

    public void setCallback(IResult resultCallback){
        this.mResultCallback = resultCallback;
    }

    public void setContext(Context context){
        this.mContext = context;
    }

    public RequestQueue getRequestQueue() {
        if (Config.mRequestQueue == null) {
            Config.mRequestQueue = Volley.newRequestQueue(mContext.getApplicationContext());
        }
        return Config.mRequestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }
}