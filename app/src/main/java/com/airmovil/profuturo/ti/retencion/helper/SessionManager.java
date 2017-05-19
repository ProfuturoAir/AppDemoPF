package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import com.airmovil.profuturo.ti.retencion.helper.Log;

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

    private final static String TAG = SessionManager.class.getSimpleName();
    //final SharedPreferences pref;
    //final SharedPreferences.Editor editor;
    //final Context _context;
    final int PRIVATE_MODE = 0;
    private static final String KEY_CT="create_time";
    private static final String PREF_NAME = "Login";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private static SessionManager mSessionManager;
    private Context context;
    private SharedPreferences sharedPreferences;

    private SessionManager(){}

    public static SessionManager getInstance(){
        if (mSessionManager == null) mSessionManager = new SessionManager();
        return mSessionManager;
    }

    public SessionManager Initialize(Context ctxt){
        context = ctxt;
        //
        sharedPreferences = PreferenceManager.getDefaultSharedPreferences(context);
        return null;
    }


    /*public SessionManager(Context context) {
        this._context = context;
        int PRIVATE_MODE = 0;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }*/

    public void createLoginSession(String apellidoMaterno, String apellidoPaterno, String centroCosto, String claveConsar, String curp, String email,
                String fechaAltaConsar, String idRolEmpelado, String nombre,String numeroEmpleado, String rolEmpleado ,String userId, String cusp){
        // Storing name in pref
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
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
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGEDIN, isLoggedIn);

        // commit changes
        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN, false);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(CAT,sharedPreferences.getString(CAT,null));
        user.put(APELLIDO_MATERNO, sharedPreferences.getString(APELLIDO_MATERNO, null));
        user.put(APELLIDO_PATERNO, sharedPreferences.getString(APELLIDO_PATERNO, null));
        user.put(CENTRO_COSTO, sharedPreferences.getString(CENTRO_COSTO, null));
        user.put(CLAVE_CONSAR, sharedPreferences.getString(CLAVE_CONSAR, null));
        user.put(CURP, sharedPreferences.getString(CURP, null));
        user.put(EMAIL, sharedPreferences.getString(EMAIL, null));
        user.put(FECHA_ALTA_CONSAR, sharedPreferences.getString(FECHA_ALTA_CONSAR, null));
        user.put(ID_ROL_EMPLEADO, sharedPreferences.getString(ID_ROL_EMPLEADO, null));
        user.put(NOMBRE, sharedPreferences.getString(NOMBRE,null));
        user.put(NUMERO_EMPLEADO, sharedPreferences.getString(NUMERO_EMPLEADO, null));
        user.put(USER_ID, sharedPreferences.getString(USER_ID, null));
        user.put(PERFIL,sharedPreferences.getString(PERFIL, null));
        user.put(CUSP, sharedPreferences.getString(CUSP, null));
        return user;
    }


    /**
     * Clear session
     * */
    public void logoutUser(){
        sharedPreferences = context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        SharedPreferences.Editor editor;
        editor = sharedPreferences.edit();
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.putString(PERFIL, "");
        editor.clear();
        editor.commit();
        // Redirige a login
        Intent i = new Intent(context, Login.class);
        // Closing all the Activities
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        // Aañade una bandera de nueva actividad
        i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        // empieza la activity
        context.startActivity(i);
    }

    public boolean checkLogin(){
        // Checa el status
        if(!this.isLoggedIn()){
            // si no esta logeado redirecciona a Login
            Intent i = new Intent(context, Login.class);
            // cierra todas las actividades
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
            // pone bandera de nueva actividad
            i.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            // empieza el login
            context.startActivity(i);
            return true;
        }
        return false;
    }
}
