package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class Connected {

    /**
     * @param context establece el estado actual de la apliacion para hacer uso con esta clase
     * @return el estado de la conecion si es wifi o redMovil
     */
    public Boolean estaConectado(Context context) {
        if (conectadoWifi(context)) {
            return true;
        } else return conectadoRedMovil(context);
    }

    /**
     * @param context establece el estado actual de la apliacion para hacer uso con esta clase
     * @return el estado de la conexion wifi
     */
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

    /**
     * @param context establece el estado actual de la apliacion para hacer uso con esta clase
     * @return el estado de la conexion movil
     */
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
