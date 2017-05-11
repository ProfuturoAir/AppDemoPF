package com.airmovil.profuturo.ti.retencion.activities;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

public class Login extends AppCompatActivity {
    public static final String TAG = Login.class.getSimpleName();
    private EditText _numeroEmpleadom, _contrasenia;
    private String numeroEmpleado, password;
    private Button btnIngresar;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private String cusp;
    private ArrayList gerenciasList = new ArrayList();
    private ArrayList<Map> gerenciaContenedor = new ArrayList<Map>();
    private Map<String, Object> gerenciaNodo = new HashMap<String, Object>();

    MySharePreferences mySharePreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login);
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // TODO: metodo para callback de volley
        initVolleyCallback();
        mySharePreferences = MySharePreferences.getInstance(getApplicationContext());
        _numeroEmpleadom = (EditText) findViewById(R.id.login_et_usuario);
        _contrasenia = (EditText) findViewById(R.id.login_et_contrasenia);
        btnIngresar = (Button) findViewById(R.id.login_btn_ingresar);

        if(_numeroEmpleadom.requestFocus() || _contrasenia.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        try{
            if (mySharePreferences.isLoggedIn()) {
                if (mySharePreferences.getUserDetails().get("idRolEmpleado").equals("3")) {
                    startActivity(new Intent(this, Director.class));
                } else if (mySharePreferences.getUserDetails().get("idRolEmpleado").equals("2")) {
                    startActivity(new Intent(this, Gerente.class));
                } else if (mySharePreferences.getUserDetails().get("idRolEmpleado").equals("1")){
                    startActivity(new Intent(this, Asesor.class));
                }else{
                    finish();
                }
                finish();
            }
        }catch (Exception e){

        }

        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, getApplicationContext());

        // btnIngresar.performClick();
        btnIngresar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                numeroEmpleado = _numeroEmpleadom.getText().toString().trim();
                password = _contrasenia.getText().toString().trim();
                if (numeroEmpleado.isEmpty() || password.isEmpty()) {
                    Config.msj(Login.this, "Error", "Debes Ingresar todos los datos");
                } else {
                    final Connected conected = new Connected();
                    if(conected.estaConectado(getApplicationContext())) {
                        Log.d(TAG,"Ok");
                        sendJson(true, numeroEmpleado, password);
                    }else{
                        Log.d(TAG,"Error conexion");
                        Config.msj(Login.this, "Error en conexión", "Sin Conexion por el momento.");
                    }
                }
            }
        });
    }

    @Override
    public void onBackPressed() {
        return;
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    /**
     *  metodo para callback de volley
     */
    void initVolleyCallback() {

        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if(requestType.equals("primerPaso"))
                    primerPaso(response, numeroEmpleado);

                if(requestType.equals("obtencionDatos"))
                    obtencionDatos(response, cusp);

                if(requestType.equals("Gerencias"))
                    try {
                        Gerencias(response.getJSONArray("Gerencias"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                if(requestType.equals("Sucursales"))
                    try {
                        Sucursales(response.getJSONArray("Sucursales"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                // Log.d(TAG, "Volley requester " + requestType);
                // Log.d(TAG, "Volley JSON post" + "That didn't work! " + error.toString());
            }
        };
    }

    private void sendJson(final boolean primeraPeticion, final String numeroEmpleado, String password) {
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        try {
            rqt.put("aplicacion", "premium");
            rqt.put("contrasena", password);
            rqt.put("usuario", numeroEmpleado);
            obj.put("rqt", rqt);
            Log.d("RQT","" + obj);
        } catch (JSONException e) {
            Config.msj(this,"Error", "1 Lo sentimos ocurrio un error al formar los datos");
        }

        this.numeroEmpleado = numeroEmpleado;
        volleySingleton.postDataVolley("primerPaso", Config.URL_AUTENTIFICACION, obj);
    }

    private void primerPaso(JSONObject obj, String sNumeroEmpleado) {
        String exception = "";
        boolean confirmacion;
        String numeroEmpleado = "";
        Log.d("response", "" + obj);

        boolean isValid = false;
        try {
            if(obj.has("confirmacion")){
                numeroEmpleado = obj.getString("numeroEmpleado");
                validacionCorrecta(numeroEmpleado, sNumeroEmpleado);
            }else{
                exception = obj.getString("Exception");
                validactionIncorrecta("Error en login", exception);
            }
        }catch (JSONException ee){
            isValid = false;
        }
    }

    public void validacionCorrecta(final String vNumeroEmpleado, final String CUSP){
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
                        //redireccionSesiones(vNumeroEmpleado, vPerfil);
                        //peticionDatos(true, vNumeroEmpleado);
                        peticionDatos(true, vNumeroEmpleado, CUSP);
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    public void validactionIncorrecta(final String rStatus, final String rException){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminate(true);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_verificando));
        progressDialog.show();
        _numeroEmpleadom.setText("");
        _contrasenia.setText("");

        if(_numeroEmpleadom.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
        }

        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        Config.teclado(getApplicationContext(), _numeroEmpleadom);
                        Config.teclado(getApplicationContext(), _contrasenia);
                        mensajeError(rStatus, rException);
                        progressDialog.dismiss();
                    }
                }, 2000);
    }

    public void mensajeError(String status, String exception){
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.Theme_AppCompat_DayNight_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_error));
        progressDialog.setTitle(status);
        progressDialog.setMessage(exception);
        progressDialog.setButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                progressDialog.dismiss();
            }
        });
        progressDialog.show();
    }

    public void redireccionSesiones(int rPerfil){
        mySharePreferences.setLogin(true);
        switch (rPerfil){
            case 3:
                startActivity(new Intent(Login.this, Director.class));
                break;
            case 2:
                startActivity(new Intent(Login.this, Gerente.class));
                break;
            case 1:
                startActivity(new Intent(Login.this, Asesor.class));
                break;
            default:
                break;
        }
    }

    private void peticionDatos(final boolean primeraPeticion, String pNumeroEmpleado, final String CUSP) {
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        try {
            rqt.put("usuario", pNumeroEmpleado);
            obj.put("rqt", rqt);
            Log.d("TAG--> peticiondatos", "" + obj );
        } catch (JSONException e) {
            Config.msj(this,"Error", "1 Lo sentimos ocurrio un error al formar los datos");
        }

        cusp = CUSP;

        volleySingleton.postDataVolley("obtencionDatos", Config.URL_CONSULTA_INFORMACION_USUARIO, obj);
    }

    private void obtencionDatos(JSONObject obj, String CUSP){
        Log.d(TAG,"<-Response->\n" + obj + "\n");
        String apellidoMaterno = "", apellidoPaterno = "", claveConsar = "", curp = "", email = "", fechaAltaConsar = "", nombre = "", numeroEmpleado = "", rolEmpleado = "", userId = "";
        int centroCosto = 0, idRolEmpleado = 0;
        try{
            apellidoMaterno = obj.getString("apellidoMaterno");
            apellidoPaterno = obj.getString("apellidoPaterno");
            centroCosto = obj.getInt("centroCosto");
            String sCentroCosto = String.valueOf(centroCosto);
            claveConsar = obj.getString("claveConsar");
            curp = obj.getString("curp");
            email = obj.getString("email");
            fechaAltaConsar = obj.getString("fechaAltaConsar");
            idRolEmpleado = obj.getInt("idRolEmpleado");
            String sIdRolEmpleado = String.valueOf(idRolEmpleado);
            nombre = obj.getString("nombre");
            numeroEmpleado = obj.getString("numeroEmpleado");
            rolEmpleado = obj.getString("rolEmpleado");
            userId = obj.getString("userId");
            Config.idUsuario = CUSP;
            Log.d("DATOS A RECOLECTAR ->", " " + apellidoMaterno + " " + apellidoPaterno + " " + centroCosto + " " + claveConsar + " " + curp + " " + email + " " +
                    fechaAltaConsar + " idRolEmpleado" + idRolEmpleado + " " + nombre + " " + numeroEmpleado + " rolEmpleado" + rolEmpleado + " userId" + userId + " " );
            mySharePreferences.createLoginSession(apellidoMaterno, apellidoPaterno, sCentroCosto, claveConsar,curp, email, fechaAltaConsar, sIdRolEmpleado,nombre, numeroEmpleado, rolEmpleado, userId, CUSP);
        }catch (JSONException e){
            e.printStackTrace();
        }
        redireccionSesiones(idRolEmpleado);
        volleySingleton.getDataVolley("Gerencias", Config.URL_GERENCIAS);
        volleySingleton.getDataVolley("Sucursales", Config.URL_SUCURSALES);

    }

    private void Gerencias(JSONArray j){
        Log.d(TAG, j.toString() + "\n");
        int idGerencia = 0;
        String nombreGerencia = "";
        ArrayList<String> arryNombreGerencias = new ArrayList<String>();
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                idGerencia = json.getInt("idGerencia");
                nombreGerencia = json.getString("nombre");
                arryNombreGerencias.add("Selecciona una genrencia");
                arryNombreGerencias.add(nombreGerencia.toString());

                Map<Integer, String> gerenciaNodo = new HashMap<Integer, String>();
                gerenciaNodo.put(idGerencia, nombreGerencia);

                gerenciasList.add(gerenciaNodo);

            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Log.e(TAG, "--->" + gerenciasList);
        Config.filtros = gerenciasList;
    }

    private void Sucursales(JSONArray j){
       // Log.d(TAG, j.toString() + "\n");
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
}