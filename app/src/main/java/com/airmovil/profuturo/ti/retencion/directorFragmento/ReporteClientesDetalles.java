package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
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

public class ReporteClientesDetalles extends Fragment {
    private static final String TAG = ReporteClientesDetalles.class.getSimpleName();
    private static final String ARG_PARAM1 = "numeroCuenta"; // curp
    private static final String ARG_PARAM2 = "cita"; // nss
    private static final String ARG_PARAM4 = "idTramite"; // idTramite
    private static final String ARG_PARAM5 = "fechaInicio"; // fecha inicio
    private static final String ARG_PARAM6 = "fechaFin"; // fecha fin
    private static final String ARG_PARAM7 = "hora"; // hora
    private static final String ARG_PARAM8 = "usuario";
    private static final String ARG_PARAM9 = "numeroEmpleado";
    private static final String ARG_PARAM10 = "nombreAsesor";

    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;

    // TODO: Rename and change types of parameters
    private String mParam1; // numero cuenta
    private String mParam2; // cita
    private String mParam3; // numeroCuenta
    private int mParam4; // idtramite
    private String mParam5; // fechaInicio
    private String mParam6; // fechaFin
    private String mParam7; // usuario
    private String mParam8; // usuario
    private String mParam9;
    private String mParam10;
    private int pagina = 1;

    private TextView tv_nombre, tv_numero_cuenta, tv_nss, tv_curp, tv_estatus, tv_saldo, tv_sucursal, tv_hora_atencion;
    private TextView tv_nombre_asesor, tv_numero_empleado, tv_inicial, tv_fechas;
    private Connected connected;

    private ProgressDialog loading;

    private OnFragmentInteractionListener mListener;

    public ReporteClientesDetalles() {/* se requiere un constructor vacio */}

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 parametro 1 idsucursal.
     * @param param2 parametro 2 idTramite.
     * @param param4 parametro 4 fechaInicio.
     * @param param5 parametro 5 fechaFin.
     * @param param6 parametro 6 usuario.
     * @return una nueva instancia del frgmento ReporteClientesDetalles.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteClientesDetalles newInstance(String param1, String param2,int param4, String param5, String param6, String param7, String param8,String param9,String param10) {
        ReporteClientesDetalles fragment = new ReporteClientesDetalles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        args.putString(ARG_PARAM9, param9);
        args.putString(ARG_PARAM10, param10);
        fragment.setArguments(args);
          return fragment;
    }

    /**
     * El sistema lo llama cuando crea el fragmento. En tu implementación, debes inicializar componentes esenciales
     * del fragmento que quieres conservar cuando el fragmento se pause o se detenga y luego se reanude.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            Log.d("Creado","TODOS "+getArguments());
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam4 = getArguments().getInt(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getString(ARG_PARAM6);
            mParam7 = getArguments().getString(ARG_PARAM7);
            mParam8 = getArguments().getString(ARG_PARAM8);
            mParam9 = getArguments().getString(ARG_PARAM9);
            mParam10 = getArguments().getString(ARG_PARAM10);
        }
    }

    /**
     * metodo para callback de volley
     */
    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + response);
                if (requestType.trim().equals("true")) {
                    loading.dismiss();
                    primerPaso(response);
                }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                Log.d(TAG, "Volley requester " + requestType);
                Log.d(TAG, "Volley JSON post" + "That didn't work! " + error.toString());
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
        initVolleyCallback();
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, getContext());
        tv_nombre = (TextView) view.findViewById(R.id.ddd_tv_clientes_nombre);
        tv_numero_cuenta = (TextView) view.findViewById(R.id.ddd_tv_clientes_numero_cuenta);
        tv_nss = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_nss);
        tv_curp = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_curp);
        tv_estatus = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_estatus);
        tv_saldo = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_saldo);
        tv_sucursal = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_sucursal);
        tv_hora_atencion = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_hora_atencion);
        tv_nombre_asesor = (TextView) view.findViewById(R.id.ddfrasd_tv_nombre_asesor);
        tv_numero_empleado = (TextView) view.findViewById(R.id.ddfrasd_tv_numero_empleado_asesor);
        tv_inicial = (TextView) view.findViewById(R.id.ddfrasd_tv_letra);
        tv_fechas = (TextView) view.findViewById(R.id.ddfrasd_tv_fecha);
        String nombreAsesor = getArguments().getString("nombreAsesor");
        String numeroEmpleado = getArguments().getString("numeroEmpleado");
        String fechaInicio = getArguments().getString("fechaInicio");
        String fechaFin = getArguments().getString("fechaFin");
        tv_numero_empleado.setText("Numero del empleado: " + numeroEmpleado);
        tv_nombre_asesor.setText("nombre del Asesor:" +nombreAsesor);
        tv_fechas.setText(fechaInicio + " - "+ fechaFin);

        connected = new Connected();
        sendJson(true);
    }

    /**
     * El sistema lo llama cuando el fragmento debe diseñar su interfaz de usuario por primera vez
     * @param inflater infla la vista xml
     * @param container contiene los elementos
     * @param savedInstanceState guarda los parametros procesado
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.director_fragmento_reporte_clientes_detalles, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void sendJson(final boolean primeraPeticion){

        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtro = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            if(getArguments() != null){
                rqt.put("filtro", filtro);
                filtro.put("curp", mParam1);
                filtro.put("nss", mParam2);
                filtro.put("numeroCuenta", mParam3);
                rqt.put("idTramite", mParam4);
                rqt.put("periodo", periodo);
                periodo.put("fechaInicio", mParam5);
                periodo.put("fechaFin", mParam6);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d("sendJson", " REQUEST -->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }
        volleySingleton.postDataVolley("" + primeraPeticion, Config.URL_CONSULTAR_REPORTE_RETENCION_CLIENTE_DETALLE, obj);
    }

    private void primerPaso(JSONObject obj){
        Log.d("primer paso", "Response: "  + obj );

        String curp = "";
        String horaAtencion = "";
        int idTramite = 0;
        String nombre = "";
        String nombreSucursal = "";
        String nss = "";
        String numeroCuenta = "";
        boolean retenido = false;
        String rfc = "";
        String saldo = "";

        try{
            JSONObject cliente = obj.getJSONObject("cliente");
            curp = cliente.getString("curp");
            horaAtencion = cliente.getString("horaAtencion"); //
            nombre = cliente.getString("nombre"); //
            nombreSucursal = cliente.getString("nombreSucursal");
            nss = cliente.getString("nss"); //
            numeroCuenta = cliente.getString("numeroCuenta"); //
            retenido = cliente.getBoolean("retenido");
            saldo = cliente.getString("saldo");
        }catch (JSONException e){
            e.printStackTrace();
        }

        String retencion = "No Retenido";;

        if(retenido){
            retencion = "Retenido";
        }

        tv_nombre.setText("" + nombre);
        tv_numero_cuenta.setText("" + numeroCuenta);
        tv_nss.setText("" + nss);
        tv_curp.setText("" + curp);
        tv_estatus.setText("" + retencion);
        tv_saldo.setText("" + saldo);
        tv_sucursal.setText("" + nombreSucursal);
        tv_hora_atencion.setText("hora: " + horaAtencion);

    }
}
