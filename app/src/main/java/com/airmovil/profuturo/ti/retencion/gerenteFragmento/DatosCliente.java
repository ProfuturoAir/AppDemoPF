package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
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
    private static final String ARG_PARAM1 = "nombre", ARG_PARAM2 = "numeroDeCuenta", ARG_PARAM3 = "hora";
    private TextView tvClienteNombre, tvClienteNumeroCuenta, tvClienteNSS, tvClienteCURP, tvClienteFecha, tvClienteSaldo;
    private Button btnContinuar, btnCancelar;
    private View rootView;
    private String idTramite, nombre, numeroDeCuenta, hora;
    private Fragment borrar = this;
    private Map<String, String> usuario;
    private OnFragmentInteractionListener mListener;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    public DatosCliente() {/* Se requiere un constructor vacio */}

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
     * El sistema lo llama cuando el fragmento debe diseñar su interfaz de usuario por primera vez
     * @param view accede a la vista del XML
     * @param savedInstanceState fuarda el estado de la instancia
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: envio de REST
        sendJson(true);
        // TODO: Casteo de variables
        variables();
        // TODO: Argumentos, verificacion si existen datos recibidos de otros fragmentos
        argumentos();

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Connected conected = new Connected();
                if(conected.estaConectado(getContext())){
                    if(idTramite!=null){
                        Fragment fragmentoGenerico = new Encuesta1();
                        Gerente gerente = (Gerente) getContext();
                        gerente.switchEncuesta1(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                    }
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        });

        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1133), 2);
    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater inflacion del xml
     * @param container contenedor del ml
     * @param savedInstanceState datos guardados
     * @return el fragmento declarado DIRECTOR INICIO
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gerente_fragmento_datos_cliente, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Se utiliza este metodo para el control de la tecla de retroceso
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
                    Fragment fragment = new SinCita();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 2, fragment);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esta actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setear las variables de xml
     */
    private void variables(){
        tvClienteNombre = (TextView) rootView.findViewById(R.id.gfda_tv_nombre_cliente);
        tvClienteNumeroCuenta = (TextView) rootView.findViewById(R.id.gfda_tv_numero_cuenta_cliente);
        tvClienteNSS = (TextView) rootView.findViewById(R.id.gfda_tv_nss_cliente);
        tvClienteCURP = (TextView) rootView.findViewById(R.id.gfda_tv_curp_cliente);
        tvClienteFecha = (TextView) rootView.findViewById(R.id.gfda_tv_fecha_cliente);
        tvClienteSaldo = (TextView) rootView.findViewById(R.id.gfda_tv_saldo_cliente);
        btnContinuar = (Button) rootView.findViewById(R.id.gfda_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.gfda_btn_cancelar);
        connected = new Connected();
        usuario = Config.usuario(getContext());
    }

    /**
     * Se utiliza para cololar datos recibidos entre una busqueda(por ejemplo: fechas)
     */
    private void argumentos(){
        if(getArguments()!=null) {
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }
    }

    /**
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        nombre = getArguments().getString("nombre");
        numeroDeCuenta = getArguments().getString("numeroDeCuenta");
        hora = getArguments().getString("hora");
        try {
            // TODO: Formacion del JSON request
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 1133);
            rqt.put("numeroCuenta", numeroDeCuenta);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Dialogos.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CUNSULTAR_DATOS_CLIENTE, obj);
    }

    /**
     * @param obj recibe el obj json de la peticion
     */
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
                Dialogos.dialogoErrorDatos(getContext());
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
