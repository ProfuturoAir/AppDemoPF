package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class DatosCliente extends Fragment {
    /* inicializacion de los paramentros del fragmento*/
    public static final String TAG = DatosCliente.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: XML
    private TextView tvClienteNombre, tvClienteNumeroCuenta, tvClienteNSS, tvClienteCURP, tvClienteFecha, tvClienteSaldo;
    private Button btnContinuar, btnCancelar;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    private String idTramite;
    private String nombre;
    private String numeroDeCuenta;
    private String hora;
    final Fragment borrar = this;

    // TODO: Config
    Map<String, String> usuario;

    private OnFragmentInteractionListener mListener;

    public DatosCliente() {
        /* contructor vacio es requerido*/
    }

    /**
     * al crear nueva instancia
     * se debe recibir los parametros:
     * @param param1 Parametro 1.
     * @param param2 Parametro 2.
     * @return un objeto DatosCliente.
     */
    public static DatosCliente newInstance(String param1, String param2, Context context) {
        DatosCliente fragment = new DatosCliente();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        primeraPeticion();
        variables();
        // TODO: Config
        usuario = Config.usuario(getContext());
        if(getArguments()!=null){
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

        Log.d("NOMBRES CLI ", "1 " + nombre + " numero " + numeroDeCuenta);
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Connected conected = new Connected();

                if(conected.estaConectado(getContext())){
                    if(idTramite!=null){
                        Fragment fragmentoGenerico = new Encuesta1();
                        Asesor asesor = (Asesor) getContext();
                        asesor.switchEncuesta1(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                    }
                }else{
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle(getResources().getString(R.string.error_conexion));
                    dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            if(idTramite!=null){
                                Fragment fragmentoGenerico = new Encuesta1();
                                Asesor asesor = (Asesor) getContext();
                                asesor.switchEncuesta1(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                            }
                        }
                    });
                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    dialogo.show();
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas salir?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new ConCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager
                                .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                .replace(R.id.content_asesor, fragmentoGenerico).commit();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                dialogo1.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_datos_cliente, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle("Confirmar");
                    dialogo.setMessage("¿Estás seguro que deseas regresar?");
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                        .replace(R.id.content_asesor, fragmentoGenerico).commit();
                            }
                        }
                    });
                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialogo.show();
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Se debe instanciar esta interfaz en la actividad que contenga los fragmentos
     * para que exista comunicacion entre los fragmentos
     * para mas informacion ver http://developer.android.com/training/basics/fragments/communicating.html
     * Comunicacion entre fragmentos
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void primeraPeticion(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIcon(R.drawable.icono_abrir);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_espera));
        progressDialog.show();
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        sendJson(true);
                    }
                }, 1500);
    }

    private void variables(){
        tvClienteNombre = (TextView) rootView.findViewById(R.id.afda_tv_nombre_cliente);
        tvClienteNumeroCuenta = (TextView) rootView.findViewById(R.id.afda_tv_numero_cuenta_cliente);
        tvClienteNSS = (TextView) rootView.findViewById(R.id.afda_tv_nss_cliente);
        tvClienteCURP = (TextView) rootView.findViewById(R.id.afda_tv_curp_cliente);
        tvClienteFecha = (TextView) rootView.findViewById(R.id.afda_tv_fecha_cliente);
        tvClienteSaldo = (TextView) rootView.findViewById(R.id.afda_tv_saldo_cliente);
        btnContinuar = (Button) rootView.findViewById(R.id.afda_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afda_btn_cancelar);
    }

    /**
     *  metodo para callback de volley
     */
    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                loading.dismiss();
                primerPaso(response);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        };
    }

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;
        JSONObject obj = new JSONObject();
        try {
            if(getArguments()!=null){
                nombre = getArguments().getString("nombre");
                numeroDeCuenta = getArguments().getString("numeroDeCuenta");
                hora = getArguments().getString("hora");
                // TODO: Formacion del JSON request
                JSONObject rqt = new JSONObject();
                rqt.put("estatusTramite", 1133);
                rqt.put("numeroCuenta", numeroDeCuenta);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CUNSULTAR_DATOS_CLIENTE, obj);
    }

    private void primerPaso(JSONObject obj){
        Log.d(TAG, "--> JSON OBJ " + obj);
        String status = "";
        String statusText = "";
        String nombre = "";
        String cuenta = "";
        String nss = "";
        String curp = "";
        String fechaConsulta = "";
        Double saldo;
        try{
            status = obj.getString("status");
            statusText = obj.getString("statusText");
            idTramite = obj.getString("idTramite");
            if(Integer.parseInt(status) == 200){
                JSONObject jsonCliente = obj.getJSONObject("cliente");
                Log.d(TAG, "--> JSON CLIENTE " + jsonCliente);
                nombre = jsonCliente.getString("nombre");
                cuenta = jsonCliente.getString("numeroCuenta");
                nss    = jsonCliente.getString("nss");
                curp   = jsonCliente.getString("curp");
                fechaConsulta = jsonCliente.getString("fechaConsulta");
                saldo = jsonCliente.getDouble("saldo");
                tvClienteNombre.setText("" + nombre);
                tvClienteNumeroCuenta.setText("" + cuenta);
                tvClienteNSS.setText("" + nss);
                tvClienteCURP.setText("" + curp);
                tvClienteFecha.setText("" + fechaConsulta);
                tvClienteSaldo.setText("" + Config.nf.format(saldo));
            }else{
                Config.msj(getContext(), "Error: " + status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
