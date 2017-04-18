package com.airmovil.profuturo.ti.retencion.activities;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class Login extends AppCompatActivity {
    public static final String TAG = Login.class.getSimpleName();
    private SessionManager sessionManager;
    private EditText _numeroEmpleadom, _contrasenia;
    private String numeroEmpleado, password;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> datosUsuario = sessionManager.getUserDetails();

        _numeroEmpleadom = (EditText) findViewById(R.id.login_et_usuario);
        _contrasenia = (EditText) findViewById(R.id.login_et_contrasenia);
        btnIngresar = (Button) findViewById(R.id.login_btn_ingresar);

        String perfil = datosUsuario.get(SessionManager.PERFIL);

        Log.d("*************", "PERFIL: ->" + perfil);

            if (sessionManager.isLoggedIn()) {
                if (sessionManager.getUserDetails().get("perfil").equals("1")) {
                    startActivity(new Intent(this, Director.class));
                } else if (sessionManager.getUserDetails().get("perfil").equals("2")) {
                    Intent intentGerenteGerencias = new Intent(this, Gerente.class);
                    startActivity(intentGerenteGerencias);
                } else {
                    Intent intentAsesor = new Intent(this, Asesor.class);
                    startActivity(intentAsesor);
                }
                finish();
            }

        btnIngresar.performClick();
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroEmpleado = _numeroEmpleadom.getText().toString().trim();
                password = _contrasenia.getText().toString().trim();
                if (numeroEmpleado.isEmpty() && password.isEmpty()) {
                    Config.msj(Login.this, "Error", "Debes Ingresar todos los datos");
                } else {
                    final Connected conected = new Connected();
                    if(conected.estaConectado(getApplicationContext())) {
                        Log.d(TAG,"Ok");
                        sendJson(true, numeroEmpleado, password);
                    }else{
                        Log.d(TAG,"Error conexion");
                        Config.msj(Login.this, "Error en conexió", "Sin Conexion por el momento.");
                    }
                }
            }
        });
    }

    private void sendJson(final boolean primeraPeticion, String numeroEmpleado, String password) {
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        try {
            rqt.put("aplicacion", "premium");
            rqt.put("contrasena", password);
            rqt.put("usuario", numeroEmpleado);
            obj.put("rqt", rqt);
        } catch (JSONException e) {
            Config.msj(this,"Error", "1 Lo sentimos ocurrio un error al formar los datos");
        }
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_AUTENTIFICACION, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (primeraPeticion) {
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj) {
        String status = "";
        String exception = "";
        boolean confirmacion;
        int perfil = 0;
        String numeroEmpleado = "";
        try {
            status = obj.getString("status");

            if(Integer.parseInt(status) == 200){
                //Config.msj(this, "123","ok");
                numeroEmpleado = obj.getString("numeroEmpleado");
                perfil = obj.getInt("perfil");
                validacionCorrecta(numeroEmpleado, perfil);
            }else{
                exception = obj.getString("Exception");
                validactionIncorrecta(status, exception);

            }
        } catch (JSONException e) {
            Config.msj(Login.this,"Error", "Lo sentimos ocurrio un error con los datos.");
        }
    }

    public void validacionCorrecta(final String vNumeroEmpleado, final int vPerfil){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_verificando));
        progressDialog.show();
        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Config.teclado(getApplicationContext(), _numeroEmpleadom);
                        Config.teclado(getApplicationContext(), _contrasenia);
                        redireccionSesiones(vNumeroEmpleado, vPerfil);
                        progressDialog.dismiss();
                    }
                }, 5000);
    }
    public void validactionIncorrecta(final String status, final String exception){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_verificando));
        progressDialog.show();
        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        // On complete call either onLoginSuccess or onLoginFailed
                        //onLoginSuccess();
                        // onLoginFailed();
                        Config.teclado(getApplicationContext(), _numeroEmpleadom);
                        Config.teclado(getApplicationContext(), _contrasenia);
                        progressDialog.dismiss();
                        mensajeError(status, exception);
                    }
                }, 5000);
    }

    public void mensajeError(String status, String exception){
        Config.msj(Login.this,"Error: " + status, exception);
    }

    public void redireccionSesiones(String rNumeroEmpleado, int rPerfil){
        sessionManager.setLogin(true);

        switch (rPerfil){
            case 1:
                peticionDatos(true, rNumeroEmpleado, rPerfil);
                startActivity(new Intent(Login.this, Director.class));
                break;
            case 2:
                peticionDatos(true, rNumeroEmpleado, rPerfil);
                startActivity(new Intent(Login.this, Gerente.class));
                break;
            case 3:
                peticionDatos(true, rNumeroEmpleado, rPerfil);
                startActivity(new Intent(Login.this, Asesor.class));
                break;
            default:
                break;
        }
    }

    private void peticionDatos(final boolean primeraPeticion, String pNumeroEmpleado, final int pPerfil) {
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        try {
            rqt.put("usuario", pNumeroEmpleado);
            obj.put("rqt", rqt);
        } catch (JSONException e) {
            Config.msj(this,"Error", "1 Lo sentimos ocurrio un error al formar los datos");
        }
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTA_INFORMACION_USUARIO, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (primeraPeticion) {
                            obtencionDatos(response, pPerfil);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d(TAG, error.toString());
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getApplicationContext());
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }


    private void obtencionDatos(JSONObject obj, int oPerfil){
        String perfilUsuario = String.valueOf(oPerfil);
        String apellidoMaterno = "";
        String apellidoPaterno = "";
        int centroCosto = 0;
        String sCentroCosto;
        String claveConsar = "";
        String curp = "";
        String email = "";
        String fechaAltaConsar = "";
        String nombre = "";
        String numeroEmpleado = "";
        String userId = "";

        try{
            apellidoMaterno = obj.getString("apellidoMaterno");
            apellidoPaterno = obj.getString("apellidoPaterno");
            centroCosto = obj.getInt("centroCosto");
            sCentroCosto = String.valueOf(centroCosto);
            claveConsar = obj.getString("claveConsar");
            curp = obj.getString("curp");
            email = obj.getString("email");
            fechaAltaConsar = obj.getString("fechaAltaConsar");
            nombre = obj.getString("nombre");
            numeroEmpleado = obj.getString("numeroEmpleado");
            userId = obj.getString("userId");
            Log.d("DATOS A RECOLECTAR ->", " " + apellidoMaterno + " " + apellidoPaterno + " " + sCentroCosto + " " + claveConsar + " " + curp + " " + email + " " + fechaAltaConsar +
                    " " + nombre + " " + numeroEmpleado + " " + userId + " " + perfilUsuario);
            sessionManager.createLoginSession(apellidoMaterno, apellidoPaterno, sCentroCosto, claveConsar,curp, email, fechaAltaConsar, nombre, numeroEmpleado, userId, perfilUsuario);
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}