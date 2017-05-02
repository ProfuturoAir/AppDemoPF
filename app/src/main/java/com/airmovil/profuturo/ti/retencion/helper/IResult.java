package com.airmovil.profuturo.ti.retencion.helper;

import com.android.volley.VolleyError;

import org.json.JSONObject;

/**
 * Created by Juan on 5/2/17.
 */

public interface IResult {
    public void notifySuccess(String requestType,JSONObject response);
    public void notifyError(String requestType,VolleyError error);
}