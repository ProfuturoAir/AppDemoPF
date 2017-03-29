package com.airmovil.profuturo.ti.retencion.activities;

import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

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
    public static final String TAG = Login.class.getSimpleName() + " -->";
    private SessionManager sessionManager;
    private EditText etNumeroEmpleadom, etpassword;
    private String numeroEmpleado, password;
    private Button btnIngresar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

        sessionManager = new SessionManager(getApplicationContext());
        etNumeroEmpleadom = (EditText) findViewById(R.id.login_et_usuario);
        etpassword = (EditText) findViewById(R.id.login_et_contrasenia);
        btnIngresar = (Button) findViewById(R.id.login_btn_ingresar);

        if(sessionManager.isLoggedIn()){
            if(sessionManager.getUserDetails().get("cat").equals("1")){
                Log.d(TAG, "Redireccion a la sesion Director");
                startActivity(new Intent(Login.this, Director.class));
            }else if(sessionManager.getUserDetails().get("cat").equals("2")){
                Log.d(TAG, "Redireccion a la sesion Gerente");
                startActivity(new Intent(Login.this, Gerente.class));
            }else {
                Log.d(TAG, "Redireccion a la sesion Asesor");
                startActivity(new Intent(Login.this, Asesor.class));
            }
            finish();
        }
        btnIngresar.performClick();
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroEmpleado = etNumeroEmpleadom.getText().toString().trim();
                password = etpassword.getText().toString().trim();
                if (numeroEmpleado.isEmpty() && password.isEmpty()) {
                    Config.msj(Login.this, "Error", "Debes Ingresar todos los datos");
                } else {
                    final Connected conected = new Connected();
                    if(conected.estaConectado(getApplicationContext())) {
                        Log.d(TAG,"Ok");
                        sendJson(true);
                    }else{
                        Log.d(TAG,"Error conexion");
                        Config.msj(Login.this, "Error en conexió", "Sin Conexion por el momento.");
                    }
                }
            }
        });
    }

    private void sendJson(final boolean primeraPeticion) {
        JSONObject js = new JSONObject();
        JSONObject jsonobject = new JSONObject();
        try {
            jsonobject.put("numero_empleado", numeroEmpleado);
            jsonobject.put("contrasenia", password);
        } catch (JSONException e) {
            Config.msj(this,"Error", "1 Lo sentimos ocurrio un error al formar los datos");
        }
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_AUTENTIFICACION, jsonobject,
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
                return headers;
            }
        };
        MySingleton.getInstance(getApplicationContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj) {
        String jNombreEmpleado = "";
        int jNumeroEmpleado = 0;
        int jPerfilEmpleado = 0;
        try {
            int login = obj.getInt("login");
            if (login != 0) {
                JSONObject loginJsonObjet = obj.getJSONObject("usuario");
                jNombreEmpleado = loginJsonObjet.getString("nombre_empleado");
                jNumeroEmpleado = loginJsonObjet.getInt("id_empleado");
                jPerfilEmpleado = loginJsonObjet.getInt("id_perfil");
                String sNumeroEmpleado = String.valueOf(jNumeroEmpleado);
                String sPerfilEmpleado = String.valueOf(jPerfilEmpleado);
                if (jPerfilEmpleado == 1) {
                    Log.d(TAG, "Usuario: director");
                    sessionManager.createLoginSession(sNumeroEmpleado, jNombreEmpleado, sPerfilEmpleado);
                    startActivity(new Intent(Login.this, Director.class));
                } else if (jPerfilEmpleado == 2) {
                    Log.d(TAG, "Usuario: gerente");
                    sessionManager.createLoginSession(sNumeroEmpleado, jNombreEmpleado, sPerfilEmpleado);
                    startActivity(new Intent(Login.this, Gerente.class));
                } else {
                    Log.d(TAG, "Usuario: Asesor");
                    sessionManager.createLoginSession(sNumeroEmpleado, jNombreEmpleado, sPerfilEmpleado);
                    startActivity(new Intent(Login.this, Asesor.class));
                }
            } else {
                Config.msj(Login.this,"Error", "Los datos no son correctos");

            }
        } catch (JSONException e) {
            Config.msj(Login.this,"Error", "Lo sentimos ocurrio un error con los datos.");
        }


    }
}
