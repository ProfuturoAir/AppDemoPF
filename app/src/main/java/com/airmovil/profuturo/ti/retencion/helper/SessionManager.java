package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.airmovil.profuturo.ti.retencion.activities.Login;

import java.util.HashMap;



public class SessionManager {
    public static final String ID = "id";
    public static final String NOMBRE="nombre";
    public static final String CAT="cat";

    // LogCat tag
    private final static String TAG = SessionManager.class.getSimpleName();
    // Shared Preferences
    final SharedPreferences pref;
    final SharedPreferences.Editor editor;
    final Context _context;
    // Shared pref mode
    final int PRIVATE_MODE = 0;
    // Shared preferences file name
    private static final String KEY_CT="create_time";
    private static final String PREF_NAME = "Login";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    public SessionManager(Context context) {
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void createLoginSession(String id, String nombre, String cat){
        // Storing name in pref
        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.putString(ID, id);
        editor.putString(NOMBRE,nombre);
        editor.putString(CAT,cat);
        // commit changes
        editor.commit();
    }

    public void setLogin(boolean isLoggedIn) {

        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return pref.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(ID, pref.getString(ID, null));
        user.put(NOMBRE, pref.getString(NOMBRE, null));
        user.put(CAT,pref.getString(CAT,null));
        return user;
    }


    /**
     * Clear session
     * */
    public void logoutUser(){
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.clear();
        editor.commit();
        // Redirige a login
        Intent i = new Intent(_context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Aañade una bandera de nueva actividad
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // empieza la activity
        _context.startActivity(i);
    }

    public boolean checkLogin(){
        // Checa el status
        if(!this.isLoggedIn()){
            // si no esta logeado redirecciona a Login
            Intent i = new Intent(_context, Login.class);
            // cierra todas las actividades
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // pone bandera de nueva actividad
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // empieza el login
            _context.startActivity(i);
            return true;
        }
        return false;
    }
}
