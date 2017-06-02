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
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.Map;

public class DatosCliente extends Fragment {
    public static final String TAG = DatosCliente.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1", ARG_PARAM2 = "param2";
    private TextView tvClienteNombre, tvClienteNumeroCuenta, tvClienteNSS, tvClienteCURP, tvClienteFecha, tvClienteSaldo;
    private Button btnContinuar, btnCancelar;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;
    private String idTramite;
    private Fragment borrar = this;
    Map<String, String> usuario;

    private OnFragmentInteractionListener mListener;

    public DatosCliente() {/* contructor vacio es requerido*/}

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

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Log.e("getArguments", "\n" + getArguments().toString());
        }
    }

    /**
     * El sistema lo llama para iniciar los procesos que estaran dentro del flujo de la vista
     * @param view accede a la vista del xml
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        sendJson(true);
        variables();

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "PARAMETROS:  --" + getArguments().getString("nombre") + " -- numC :" + getArguments().getString("numeroDeCuenta") + " hora:: " + getArguments().getString("hora"));
                Fragment fragmentoGenerico = new Encuesta1();
                Asesor asesor = (Asesor) getContext();
                if(connected.estaConectado(getContext())){
                    if(idTramite!=null){
                        asesor.parametrosDetalle(fragmentoGenerico, Integer.parseInt(Config.ID_TRAMITE), getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), getArguments().getString("nombreAsesor"), getArguments().getString("cuentaAsesor"), getArguments().getString("sucursalAsesor"), tvClienteNombre.getText().toString(),
                                tvClienteNumeroCuenta.getText().toString(), tvClienteNSS.getText().toString(), tvClienteCURP.getText().toString(), tvClienteFecha.getText().toString(), tvClienteSaldo.getText().toString(), false, false, false, "", "", "", "", "", "", "", "", "");
                    }
                }else{
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle(getResources().getString(R.string.error_conexion));
                    dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new Encuesta1();
                            Asesor asesor = (Asesor) getContext();
                            asesor.parametrosDetalle(fragmentoGenerico, Integer.parseInt(Config.ID_TRAMITE),getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), getArguments().getString("nombreAsesor"), getArguments().getString("cuentaAsesor"), getArguments().getString("sucursalAsesor"), tvClienteNombre.getText().toString(),
                                    tvClienteNumeroCuenta.getText().toString(), tvClienteNSS.getText().toString(), tvClienteCURP.getText().toString(), tvClienteFecha.getText().toString(), tvClienteSaldo.getText().toString(), false, false, false, "", "", "", "", "", "", "", "", "");
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
        // TODO Cancelar el proceso
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1133), 1);
    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_datos_cliente, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context estado actual de la aplicacion
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     *Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new ConCita();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 1, fragment);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esa actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Casteo de variables, nueva instancia para la conexion a internet Connected
     */
    private void variables(){
        tvClienteNombre = (TextView) rootView.findViewById(R.id.afda_tv_nombre_cliente);
        tvClienteNumeroCuenta = (TextView) rootView.findViewById(R.id.afda_tv_numero_cuenta_cliente);
        tvClienteNSS = (TextView) rootView.findViewById(R.id.afda_tv_nss_cliente);
        tvClienteCURP = (TextView) rootView.findViewById(R.id.afda_tv_curp_cliente);
        tvClienteFecha = (TextView) rootView.findViewById(R.id.afda_tv_fecha_cliente);
        tvClienteSaldo = (TextView) rootView.findViewById(R.id.afda_tv_saldo_cliente);
        btnContinuar = (Button) rootView.findViewById(R.id.afda_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afda_btn_cancelar);
        connected = new Connected();
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
                loading.dismiss();
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                    Log.e("conexion", "dialogoErrorServicio");
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                    Log.e("conexion", "dialogoErrorConexion");
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
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;
        JSONObject obj = new JSONObject();
        try {
            if(getArguments()!=null){
                JSONObject rqt = new JSONObject();
                rqt.put("estatusTramite", 1133);
                rqt.put("numeroCuenta", getArguments().getString("numeroCuenta"));
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d(TAG, "<- RQT ->" + obj);
        } catch (JSONException e) {
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CUNSULTAR_DATOS_CLIENTE, obj);
    }

    /**
     * Inicia este metodo para llenar la lista de elementos, cada 10, inicia solamente con 10, despues inicia el metodo segundoPaso
     * @param obj jsonObject
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "<- RESPONSE ->" + obj);
        String status = "", statusText = "", nombre = "", cuenta = "", nss = "", curp = "", fechaConsulta = "";
        Double saldo;
        try{
            status = obj.getString("status");
            statusText = obj.getString("statusText");
            idTramite = obj.getString("idTramite");
            if(Integer.parseInt(status) == 200){
                JSONObject jsonCliente = obj.getJSONObject("cliente");
                nombre = jsonCliente.getString("nombre");
                cuenta = jsonCliente.getString("numeroCuenta");
                nss = jsonCliente.getString("nss");
                curp = jsonCliente.getString("curp");
                fechaConsulta = jsonCliente.getString("fechaConsulta");
                saldo = jsonCliente.getDouble("saldo");
                tvClienteNombre.setText("" + nombre);
                tvClienteNumeroCuenta.setText("" + cuenta);
                tvClienteNSS.setText("" + nss);
                tvClienteCURP.setText("" + curp);
                tvClienteFecha.setText("" + fechaConsulta);
                tvClienteSaldo.setText("" + Config.nf.format(saldo));
            }else{
                Dialogos.dialogoErrorRespuesta(getContext(), status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        Config.ID_TRAMITE = idTramite;
    }
}
