package com.airmovil.profuturo.ti.retencion.helper;

import com.airmovil.profuturo.ti.retencion.BuildConfig;

/**
 * Created by tecnicoairmovil on 18/05/17.
 */

public class Log {
    public static final boolean activo = false;

    public static void i(String TAG, String msg) {
        if (activo) {
            android.util.Log.i(TAG, msg);
        }
    }

    public static void w(String TAG, String msg) {
        if (activo) {
            android.util.Log.w(TAG, msg);
        }
    }

    public static void d(String TAG, String msg) {
        if (activo) {
            android.util.Log.d(TAG, msg);
        }
    }

    public static void e(String TAG, String msg) {
        if (activo) {
            android.util.Log.e(TAG, msg);
        }
    }
}
