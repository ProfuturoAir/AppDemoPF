package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.util.Log;

import com.airmovil.profuturo.ti.retencion.activities.Login;

import java.util.HashMap;


/**
 * Created by tecnicoairmovil on 28/04/17.
 */

public class MySharePreferences {
    private static final String TAG = MySharePreferences.class.getSimpleName();

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

    private static MySharePreferences sharePref = null;
    private  SharedPreferences sharedPreferences;
    private  SharedPreferences.Editor editor;
    private Context context;

    private static final String PLACE_OBJ = "place_obj";
    private static final String PREF_NAME = "Login";
    private static final String KEY_IS_LOGGEDIN = "isLoggedIn";

    private MySharePreferences(Context context) {
        this.context = context;
        sharedPreferences = context.getSharedPreferences(context.getPackageName(), Activity.MODE_PRIVATE);
        editor = sharedPreferences.edit();
    }

    public static MySharePreferences getInstance(Context context) {
        if (sharePref == null) {
            sharePref = new MySharePreferences(context);
        }
        return sharePref;
    }


    public void createLoginSession(String apellidoMaterno, String apellidoPaterno, String centroCosto, String claveConsar, String curp, String email,
                                   String fechaAltaConsar, String idRolEmpelado, String nombre,String numeroEmpleado, String rolEmpleado ,String userId, String cusp){
        // Storing name in  sharedPreferences
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
        return sharedPreferences.getBoolean(KEY_IS_LOGGEDIN, true);
    }

    public HashMap<String, String> getUserDetails(){
        HashMap<String, String> user = new HashMap<>();
        user.put(CAT, sharedPreferences.getString(CAT,null));
        user.put(APELLIDO_MATERNO,  sharedPreferences.getString(APELLIDO_MATERNO, null));
        user.put(APELLIDO_PATERNO,  sharedPreferences.getString(APELLIDO_PATERNO, null));
        user.put(CENTRO_COSTO,  sharedPreferences.getString(CENTRO_COSTO, null));
        user.put(CLAVE_CONSAR,  sharedPreferences.getString(CLAVE_CONSAR, null));
        user.put(CURP,  sharedPreferences.getString(CURP, null));
        user.put(EMAIL,  sharedPreferences.getString(EMAIL, null));
        user.put(FECHA_ALTA_CONSAR,  sharedPreferences.getString(FECHA_ALTA_CONSAR, null));
        user.put(ID_ROL_EMPLEADO,  sharedPreferences.getString(ID_ROL_EMPLEADO, null));
        user.put(NOMBRE,  sharedPreferences.getString(NOMBRE,null));
        user.put(NUMERO_EMPLEADO,  sharedPreferences.getString(NUMERO_EMPLEADO, null));
        user.put(USER_ID,  sharedPreferences.getString(USER_ID, null));
        user.put(PERFIL, sharedPreferences.getString(PERFIL, null));
        user.put(CUSP,  sharedPreferences.getString(CUSP, null));
        return user;
    }


    /**
     * Clear session
     * */
    public void logoutUser(){
        editor.putBoolean(KEY_IS_LOGGEDIN, false);
        editor.putString(CAT, null);
        editor.putString(APELLIDO_MATERNO, null);
        editor.putString(APELLIDO_PATERNO, null);
        editor.putString(CENTRO_COSTO, null);
        editor.putString(CLAVE_CONSAR, null);
        editor.putString(CURP, null);
        editor.putString(EMAIL, null);
        editor.putString(FECHA_ALTA_CONSAR, null);
        editor.putString(ID_ROL_EMPLEADO, null);
        editor.putString(NOMBRE, null);
        editor.putString(NUMERO_EMPLEADO, null);
        editor.putString(USER_ID, null);
        editor.putString(PERFIL, null);
        editor.putString(CUSP, null);

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

