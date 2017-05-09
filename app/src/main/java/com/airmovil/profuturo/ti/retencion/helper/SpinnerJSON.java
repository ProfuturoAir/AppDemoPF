package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.content.Context;
import android.view.View;
import android.widget.AdapterView;
import android.widget.Spinner;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

/**
 * Created by tecnicoairmovil on 27/04/17.
 */

public class SpinnerJSON extends Activity implements Spinner.OnItemSelectedListener{
    JSONObject jsonObject = null;
    private Spinner spinnerGerencia, spinnerSucursal;
    private Context context;

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    private void getData1(){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_SUCURSALES,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray j = null;
                        try {
                            j = response.getJSONArray("Sucursales");
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(context);
            }
        };
        MySingleton.getInstance(context).addToRequestQueue(jsonArrayRequest);
    }


}
