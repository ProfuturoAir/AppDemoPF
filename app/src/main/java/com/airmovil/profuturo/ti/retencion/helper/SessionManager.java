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
    public static final String KEY_CT="create_time";
    public static final String PREF_NAME = "Login";
    public static final String KEY_IS_LOGGEDIN = "isLoggedIn";
    public static final String USUARIO_APELLIDO_MATERNO = "apellidoMaterno";
    public static final String USUARIO_APELLIDO_PATERNO = "apellidoPaterno";
    public static final String USUARIO_CENTRO_COSTO = "centroCosto";
    public static final String USUARIO_CLAVE_CONSAR = "claveConsar";
    public static final String USUARIO_CURP = "curp";
    public static final String USUARIO_EMAIL = "email";
    public static final String USUARIO_FECHA_ALTA_CONSAR = "fechaAltaConsar";
    public static final String USUARIO_NOMBRE = "nombre";
    public static final String USUARIO_NUMERO_EMPLEADO = "numeroEmpleado";
    public static final String USUARIO_USER_ID = "userId";
    public static final String USUARIO_PERFIL = "perfil";

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

    public void crearSesion(String apellidoMaterno, String apellidoPaterno, String centroCosto, String claveConsar,
                            String curp, String email, String fechaAltaConsar, String nombre, String numeroEmpleado, String userId, String perfil){
        editor.putBoolean(KEY_IS_LOGGEDIN, true);
        editor.putString(USUARIO_APELLIDO_MATERNO, apellidoMaterno);
        editor.putString(USUARIO_APELLIDO_PATERNO, apellidoPaterno);
        editor.putString(USUARIO_CENTRO_COSTO, centroCosto);
        editor.putString(USUARIO_CLAVE_CONSAR, claveConsar);
        editor.putString(USUARIO_CURP, curp);
        editor.putString(USUARIO_EMAIL, email);
        editor.putString(USUARIO_FECHA_ALTA_CONSAR, fechaAltaConsar);
        editor.putString(USUARIO_NOMBRE, nombre);
        editor.putString(USUARIO_NUMERO_EMPLEADO, numeroEmpleado);
        editor.putString(USUARIO_USER_ID, userId);
        editor.putString(USUARIO_PERFIL, perfil);
        editor.commit();
    }

    public void createProceso(String idProceso, String numero){

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

    public HashMap<String, String> obtencionDatosUsuario(){
        HashMap<String, String> usuario = new HashMap<>();
        usuario.put(USUARIO_APELLIDO_MATERNO, pref.getString(USUARIO_APELLIDO_MATERNO, null));
        usuario.put(USUARIO_APELLIDO_PATERNO, pref.getString(USUARIO_APELLIDO_PATERNO, null));
        usuario.put(USUARIO_CENTRO_COSTO, pref.getString(USUARIO_CENTRO_COSTO, null));
        usuario.put(USUARIO_CLAVE_CONSAR, pref.getString(USUARIO_CLAVE_CONSAR, null));
        usuario.put(USUARIO_CURP, pref.getString(USUARIO_CURP, null));
        usuario.put(USUARIO_EMAIL, pref.getString(USUARIO_EMAIL, null));
        usuario.put(USUARIO_FECHA_ALTA_CONSAR, pref.getString(USUARIO_FECHA_ALTA_CONSAR, null));
        usuario.put(USUARIO_NOMBRE, pref.getString(USUARIO_NOMBRE,null));
        usuario.put(USUARIO_NUMERO_EMPLEADO, pref.getString(USUARIO_NUMERO_EMPLEADO, null));
        usuario.put(USUARIO_USER_ID, pref.getString(USUARIO_USER_ID, null));
        usuario.put(USUARIO_PERFIL, pref.getString(USUARIO_PERFIL, null));
        return usuario;
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
