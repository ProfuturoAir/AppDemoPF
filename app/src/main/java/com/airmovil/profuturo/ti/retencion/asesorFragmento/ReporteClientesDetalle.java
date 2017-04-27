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
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class ReporteClientesDetalle extends Fragment {
    private static final String TAG = ReporteClientesDetalle.class.getSimpleName();
    private static final String ARG_PARAM1 = "curp";
    private static final String ARG_PARAM2 = "idTramite";
    private static final String ARG_PARAM3 = "hora";
    private static final String ARG_PARAM4 = "fechaInicio";
    private static final String ARG_PARAM5 = "fechaFin";
    private TextView tvInicial, tvNombreAsesor, tvNumeroEmpleado, tvFecha, tvNombreCliente, tvNumeroCuentaCliente, tvNSS, tvEstatus, tvSaldo, tvSucursales, tvHoraAtencion, tvCurp;
    private View rootView;
    private OnFragmentInteractionListener mListener;
    private Connected connected;

    public ReporteClientesDetalle() {
        // Se requiere un constructor vacio
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
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
        primeraPeticion();
        datosUsuario();
    }

    /**
     *
     */
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
                }, Config.TIME_HANDLER);
    }

    /**
     *
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_reporte_clientes_detalle, container, false);
    }

    /**
     *
     * @param uri
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     *
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
     *
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     *
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
                    Fragment fragmentoGenerico = new ReporteClientes();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
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

    /**
     *
     */
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
     *
     * @param primeraPeticion
     */
    private void sendJson(final boolean primeraPeticion){
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
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GENERAL_REPORTE_CLIENTE, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(connected.estaConectado(getContext())){
                            Dialogos.dialogoErrorServicio(getContext());
                        }else{
                            Dialogos.dialogoErrorConexion(getContext());
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     *
     * @param obj
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