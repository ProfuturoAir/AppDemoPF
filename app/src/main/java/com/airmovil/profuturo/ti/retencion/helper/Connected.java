package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;



public class Connected {

    public Boolean estaConectado(Context context) {
        if (conectadoWifi(context)) {
            return true;
        } else return conectadoRedMovil(context);
    }

    protected Boolean conectadoWifi(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }


    protected Boolean conectadoRedMovil(Context context) {
        ConnectivityManager connectivity = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (info != null) {
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }
}
