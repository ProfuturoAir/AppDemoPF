package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.airmovil.profuturo.ti.retencion.activities.Login;

import java.util.HashMap;

/**
 * Created by tecnicoairmovil on 15/03/17.
 */

public class SessionManager {
    public static final String ID = "id";
    public static final String CAT="cat";
    public static final String APELLIDO_MATERNO = "apellidoMaterno";
    public static final String APELLIDO_PATERNO = "apellidoPaterno";
    public static final String CENTRO_COSTO = "centroCosto";
    public static final String CLAVE_CONSAR = "claveConsar";
    public static final String CURP = "curp";
    public static final String EMAIL = "email";
    public static final String FECHA_ALTA_CONSAR = "fechaAltaConsar";
    public static final String ID_ROL_EMPLEADO = "idRolEmpleado";
    public static final String NOMBRE = "nombre";
    public static final String NUMERO_EMPLEADO = "numeroEmpleado";
    public static final String USER_ID = "userId";
    public static final String ROL_EMPLEADO = "rolEmpleado";
    public static final String PERFIL = "perfil";
    public static final String CUSP = "cusp";

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

    public void createLoginSession(String apellidoMaterno, String apellidoPaterno, String centroCosto, String claveConsar, String curp, String email,
                String fechaAltaConsar, String idRolEmpelado, String nombre,String numeroEmpleado, String rolEmpleado ,String userId, String cusp){
        // Storing name in pref
        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.putString(APELLIDO_MATERNO, apellidoMaterno);
        editor.putString(APELLIDO_PATERNO, apellidoPaterno);
        editor.putString(CENTRO_COSTO, centroCosto);
        editor.putString(CLAVE_CONSAR, claveConsar);
        editor.putString(CURP, curp);
        editor.putString(EMAIL, email);
        editor.putString(FECHA_ALTA_CONSAR, fechaAltaConsar);
        editor.putString(ID_ROL_EMPLEADO, idRolEmpelado);
        editor.putString(NOMBRE, nombre);
        editor.putString(NUMERO_EMPLEADO, numeroEmpleado);
        editor.putString(ROL_EMPLEADO, rolEmpleado);
        editor.putString(USER_ID, userId);
        editor.putString(CUSP, cusp);
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
        user.put(CAT,pref.getString(CAT,null));
        user.put(APELLIDO_MATERNO, pref.getString(APELLIDO_MATERNO, null));
        user.put(APELLIDO_PATERNO, pref.getString(APELLIDO_PATERNO, null));
        user.put(CENTRO_COSTO, pref.getString(CENTRO_COSTO, null));
        user.put(CLAVE_CONSAR, pref.getString(CLAVE_CONSAR, null));
        user.put(CURP, pref.getString(CURP, null));
        user.put(EMAIL, pref.getString(EMAIL, null));
        user.put(FECHA_ALTA_CONSAR, pref.getString(FECHA_ALTA_CONSAR, null));
        user.put(ID_ROL_EMPLEADO, pref.getString(ID_ROL_EMPLEADO, null));
        user.put(NOMBRE, pref.getString(NOMBRE,null));
        user.put(NUMERO_EMPLEADO, pref.getString(NUMERO_EMPLEADO, null));
        user.put(USER_ID, pref.getString(USER_ID, null));
        user.put(PERFIL,pref.getString(PERFIL, null));
        user.put(CUSP, pref.getString(CUSP, null));
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
