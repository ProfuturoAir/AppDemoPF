package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class ReporteClientesDetalle extends Fragment {
    private static final String TAG = ReporteClientesDetalle.class.getSimpleName(), ARG_PARAM1 = "curp", ARG_PARAM2 = "idTramite", ARG_PARAM3 = "hora", ARG_PARAM4 = "fechaInicio", ARG_PARAM5 = "fechaFin";
    private TextView tvInicial, tvNombreAsesor, tvNumeroEmpleado, tvFecha, tvNombreCliente, tvNumeroCuentaCliente, tvNSS, tvEstatus, tvSaldo, tvSucursales, tvHoraAtencion, tvCurp;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private OnFragmentInteractionListener mListener;
    private Connected connected;

    public ReporteClientesDetalle() {
        // Se requiere un constructor vacio
    }

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * Se llama inmediatamente después de que onCreateView(LayoutInflater, ViewGroup, Bundle) ha onCreateView(LayoutInflater, ViewGroup, Bundle)
     * pero antes de que se haya onCreateView(LayoutInflater, ViewGroup, Bundle) estado guardado en la vista.
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        tvInicial = (TextView) rootView.findViewById(R.id.afrcd_tv_letra);
        tvNombreAsesor = (TextView) rootView.findViewById(R.id.afrcd_tv_nombre_asesor);
        tvNumeroEmpleado = (TextView) rootView.findViewById(R.id.afrcd_tv_numero_empleado_asesor);
        tvFecha = (TextView) rootView.findViewById(R.id.afrcd_tv_fecha);
        tvNombreCliente = (TextView) rootView.findViewById(R.id.afrcd_tv_nombre_cliente);
        tvNumeroCuentaCliente = (TextView) rootView.findViewById(R.id.afrcd_tv_numero_cuenta_cliente);
        tvNSS = (TextView) rootView.findViewById(R.id.afrcd_tv_nss_cliente);
        tvCurp = (TextView) rootView.findViewById(R.id.afrcd_tv_curp_cliente);
        tvEstatus = (TextView) rootView.findViewById(R.id.afrcd_tv_status_cliente);
        tvSaldo = (TextView) rootView.findViewById(R.id.afrcd_tv_fecha_cliente);
        tvSucursales = (TextView) rootView.findViewById(R.id.afrcd_tv_saldo_cliente);
        tvHoraAtencion = (TextView) rootView.findViewById(R.id.afrcd_tv_hora_atencion);
        connected = new Connected();
        sendJson(true);
        datosUsuario();
    }

    /**
     * @param inflater se utiliza para inflar el XML
     * @param container contiene los elementos de la vista
     * @param savedInstanceState guarda los datos enviados
     * @return la vista con elemdntos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_reporte_clientes_detalle, container, false);
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
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Método que se ejecuta al accionar la opcion de regreso
     */
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    if(getArguments()!=null){
                        Log.d("arg",getArguments().toString());
                        Asesor d1 = (Asesor) getContext();
                        ReporteClientes reporte = new ReporteClientes();
                        d1.envioParametros(reporte, getArguments().getString(ARG_PARAM4), getArguments().getString(ARG_PARAM5));
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Esta interfaz debe es implementada por la actividad que contiene este fragmento
     * para permitir implementar la comunicacion con este fragmeto
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
    private void datosUsuario(){
        Map<String, String> usuario = Config.datosUsuario(getContext());
        String nombreAsesor = usuario.get(SessionManager.NOMBRE);
        String apePaternoAsesor = usuario.get(SessionManager.APELLIDO_PATERNO);
        String apeMaternoAsesor = usuario.get(SessionManager.APELLIDO_MATERNO);
        String numeroEmpleado = usuario.get(SessionManager.NUMERO_EMPLEADO);

        tvNombreAsesor.setText("Nombre del Asesor: " +nombreAsesor + " " + apePaternoAsesor + " " + apeMaternoAsesor);
        tvNumeroEmpleado.setText("Número de empleado: " + numeroEmpleado);
        tvFecha.setText((getArguments()!=null) ? getArguments().getString(ARG_PARAM4) + " - " + getArguments().getString(ARG_PARAM5): "");
        tvInicial.setText(String.valueOf(nombreAsesor.charAt(0)));
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
     * @param primeraPeticion
     */
    private void sendJson(final boolean primeraPeticion){
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtro = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            if(getArguments() != null){
                filtro.put("curp", getArguments().getString(ARG_PARAM1));
                filtro.put("nss", "");
                filtro.put("numeroCuenta", "");
                rqt.put("filtro", filtro);
                rqt.put("idTramite", getArguments().getInt(ARG_PARAM2));
                periodo.put("fechaInicio", getArguments().getString(ARG_PARAM4));
                periodo.put("fechaFin", getArguments().getString(ARG_PARAM5));
                rqt.put("periodo",periodo);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                json.put("rqt", rqt);
            }
            Log.d(TAG, "<- RQT ->\n" + json + "\n");
        } catch (JSONException e){
            Dialogos.msj(getContext(),"Error","Existe un error al formar la peticion");
        }

        volleySingleton.postDataVolley("primerPaso", Config.URL_GENERAL_REPORTE_CLIENTE, json);
    }

    /**
     * Inicia este metodo para llenar la lista de elementos, cada 10, inicia solamente con 10, despues inicia el metodo segundoPaso
     * @param obj jsonObject
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "<- Response -> \n"  + obj + "\n");
        String nombre = "";
        String numeroCuenta = "";
        String curp = "";
        String nss = "";
        boolean estatus = false;
        int saldo = 0;
        String nombreSucursal = "";
        try{
            JSONObject cliente = obj.getJSONObject("cliente");
            nombre = cliente.getString("nombre");
            numeroCuenta = cliente.getString("numeroCuenta");
            curp = cliente.getString("curp");
            nss = cliente.getString("nss");
            estatus = cliente.getBoolean("retenido");
            saldo = cliente.getInt("saldo");
            nombreSucursal = cliente.getString("nombreSucursal");
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvNombreCliente.setText(nombre);
        tvNumeroCuentaCliente.setText(numeroCuenta);
        tvCurp.setText("" + curp);
        tvNSS.setText("" + nss);
        tvEstatus.setText((estatus) ? "Retenido" : "No retenido");
        tvSaldo.setText(Config.nf.format(saldo));
        tvSucursales.setText(nombreSucursal);
        tvHoraAtencion.setText((getArguments()!=null) ? getArguments().getString(ARG_PARAM3) : "");
    }
}