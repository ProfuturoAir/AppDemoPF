package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.AsesorReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.AsesorReporteClientesModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ReporteClientes extends Fragment {
    public static final String TAG = ReporteClientes.class.getSimpleName();
    private static final String ARG_PARAM_1 = "param1";
    private static final String ARG_PARAM_2 = "param2";
    private static final String ARG_PARAM_3 = "param3";
    private static final String ARG_PARAM_4 = "param4";
    private static final String ARG_PARAM_5 = "param5";
    private int sParam1; // ids
    private String sParam2; // Id dato
    private String sParam3; // fecha Inicio
    private String sParam4; // fecha fin
    private int sParam5; // Emitidos
    private View rootView;
    private TextView tvFecha, tvEmitidos, tvNoEmitidos, tvSaldoEmitido, tvSaldoNoEmitido, tvRangoFecha1, tvRangoFecha2, tvRegistros;
    private Spinner spinnerIds, spinnerEmitidos;
    private EditText etIngresar;
    private Button btnBuscar;
    private List<AsesorReporteClientesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private AsesorReporteClientesAdapter adapter;
    private Connected connected;
    private int filas;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    private Fragment borrar = this;
    private OnFragmentInteractionListener mListener;

    public ReporteClientes() {
        // Requiere un constructor vacio
    }

    /**
     * Se utilizan los parámetros proporcionados.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return una nueva instancia del fragmento ReporteClientes.
     */
    public static ReporteClientes newInstance(int param1, String param2, String param3, String param4, int param5, Context context) {
        ReporteClientes fragment = new ReporteClientes();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_1, param1);
        args.putString(ARG_PARAM_2, param2);
        args.putString(ARG_PARAM_3, param3);
        args.putString(ARG_PARAM_4, param4);
        args.putInt(ARG_PARAM_5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            sParam1 = getArguments().getInt(ARG_PARAM_1);
            sParam2 = getArguments().getString(ARG_PARAM_2);
            sParam3 = getArguments().getString(ARG_PARAM_3);
            sParam4 = getArguments().getString(ARG_PARAM_4);
            sParam5 = getArguments().getInt(ARG_PARAM_5);
        }
    }

    /**
     *
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        primeraPeticion();
        variables();
        argumentos();
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: Spinner
        ArrayAdapter<String> adapterIds = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
        adapterIds.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerIds.setAdapter(adapterIds);
        spinnerIds.setSelection((getArguments()!=null) ? getArguments().getInt(ARG_PARAM_1) : 0);
        // TODO: Spinner
        ArrayAdapter<String> adapterEmitidos = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.EMITIDOS);
        adapterEmitidos.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerEmitidos.setAdapter(adapterEmitidos);
        spinnerEmitidos.setSelection((getArguments()!=null) ? getArguments().getInt(ARG_PARAM_5) : 0);

        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.afrc_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty()){
                    Config.dialogoFechasVacias(getContext());
                }
                else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ReporteClientes procesoDatosFiltroInicio = ReporteClientes.newInstance(
                            sParam1 = spinnerIds.getSelectedItemPosition(), sParam2 = etIngresar.getText().toString(),
                            sParam3 = tvRangoFecha1.getText().toString(), sParam4 = tvRangoFecha2.getText().toString(),
                            sParam5 = spinnerEmitidos.getSelectedItemPosition(), rootView.getContext());
                    borrar.onDestroy();
                    ft.remove(borrar).replace(R.id.content_asesor, procesoDatosFiltroInicio).addToBackStack(null).commit();
                    Config.teclado(getContext(), etIngresar);
                }
            }
        });
    }

    /**
     *
     * @param inflater se utiliza para inflar el XML
     * @param container contiene los elementos de la vista
     * @param savedInstanceState guarda los datos enviados
     * @return la vista con elemdntos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_reporte_clientes, container, false);
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
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
     */
    private void variables(){
        tvFecha = (TextView) rootView.findViewById(R.id.afrc_tv_fecha);
        tvEmitidos = (TextView) rootView.findViewById(R.id.afrc_tv_emitidos);
        tvNoEmitidos = (TextView) rootView.findViewById(R.id.afrc_tv_no_emitidos);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.afrc_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.afrc_tv_saldo_no_emitido);
        tvRangoFecha1  = (TextView) rootView.findViewById(R.id.afrc_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) rootView.findViewById(R.id.afrc_tv_fecha_rango2);
        spinnerIds = (Spinner) rootView.findViewById(R.id.afrc_spinner_ids);
        spinnerEmitidos = (Spinner) rootView.findViewById(R.id.afrc_spinner_emitidos);
        etIngresar = (EditText) rootView.findViewById(R.id.afrc_et_id);
        btnBuscar = (Button) rootView.findViewById(R.id.afrc_btn_buscar);
        tvRegistros = (TextView) rootView.findViewById(R.id.afrc_tv_registros);
        connected = new Connected();
    }

    /**
     *
     */
    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM_3)+" - "+getArguments().getString(ARG_PARAM_4));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM_3));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM_4));
            etIngresar.setText(getArguments().getString(ARG_PARAM_2));
        }else {
            tvFecha.setText(Dialogos.fechaActual());
        }
    }

    /**
     *
     * @param primerPeticion
     */
    private void sendJson(final boolean primerPeticion) {
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtros = new JSONObject();
        JSONObject periodo = new JSONObject();
        boolean argumentos = (getArguments()!=null);
        try{
            rqt.put("filtro", filtros);
            switch ((argumentos) ? getArguments().getInt(ARG_PARAM_1) : 0){
                case 1:
                    filtros.put("curp", "");
                    filtros.put("nss", "");
                    filtros.put("numeroCuenta", (argumentos) ? getArguments().getString(ARG_PARAM_2) : "");
                    break;
                case 2:
                    filtros.put("curp", "");
                    filtros.put("nss", (argumentos) ? getArguments().getString(ARG_PARAM_2) : "");
                    filtros.put("numeroCuenta", "");
                    break;
                case 3:
                    filtros.put("curp", (argumentos) ? getArguments().getString(ARG_PARAM_2) : "");
                    filtros.put("nss", "");
                    filtros.put("numeroCuenta", "");
                    break;
                case 0:
                    filtros.put("curp", "");
                    filtros.put("nss", "");
                    filtros.put("numeroCuenta", "");
                    break;
            }
            rqt.put("filtroRetenido", (argumentos) ? getArguments().getInt(ARG_PARAM_5) : 0);
            rqt.put("pagina", pagina);
            periodo.put("fechaInicio", (argumentos) ? getArguments().getString(ARG_PARAM_3) : Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos) ? getArguments().getString(ARG_PARAM_4) : Dialogos.fechaSiguiente());
            rqt.put("periodo", periodo);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "<- RQT ->\n" + obj + "\n");
        }catch (JSONException e){
            e.printStackTrace();
        }
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GENERAR_REPORTE_CLIENTE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (primerPeticion) {
                            primerPaso(response);
                        } else {
                            segundoPaso(response);
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
                }) {
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
    private void primerPaso(JSONObject obj) {
        Log.d(TAG + "<- RQT ->\n", obj.toString() + "\n");
        int totalFilas = 1;
        int retenido = 0;
        int noRetenido = 0;
        int saldoAfavor = 0;
        int saldoNoRetenido = 0;
        try{
            totalFilas = obj.getInt("filasTotal");
            JSONObject infoConsulta = obj.getJSONObject("infoConsulta");
            JSONObject oRetenido = infoConsulta.getJSONObject("retenido");
            JSONObject oSaldos = infoConsulta.getJSONObject("saldo");
            retenido = oRetenido.getInt("retenido");
            noRetenido = oRetenido.getInt("noRetenido");
            saldoAfavor = oSaldos.getInt("saldoRetenido");
            saldoNoRetenido = oSaldos.getInt("saldoNoRetenido");
            JSONArray array = obj.getJSONArray("clientes");
            for(int i = 0; i < array.length(); i++){
                AsesorReporteClientesModel getDatos2 = new AsesorReporteClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNombreCliente(json.getString("nombre"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    getDatos2.setConCita(json.getString("cita"));
                    getDatos2.setIdTramite(json.getInt("idTramite"));
                    getDatos2.setCurp(json.getString("curp"));
                    getDatos2.setHora(json.getString("hora"));
                    getDatos2.setNoEmitido(json.getString("retenido"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvEmitidos.setText("" + retenido);
        tvNoEmitidos.setText("" + noRetenido);
        tvSaldoEmitido.setText("" + Config.nf.format(saldoAfavor));
        tvSaldoNoEmitido.setText("" + Config.nf.format(saldoNoRetenido));
        tvRegistros.setText("" + totalFilas + " Registros");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        boolean argumentos = (getArguments() != null);
        adapter = new AsesorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView, (argumentos) ? getArguments().getString(ARG_PARAM_3) : Dialogos.fechaActual(), (argumentos) ? getArguments().getString(ARG_PARAM_4) : Dialogos.fechaSiguiente());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pagina >= numeroMaximoPaginas) {
                    return;
                }
                getDatos1.add(null);
                adapter.notifyItemInserted(getDatos1.size() - 1);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDatos1.remove(getDatos1.size() - 1);
                        adapter.notifyItemRemoved(getDatos1.size());
                        pagina = Config.pidePagina(getDatos1);
                        sendJson(false);
                    }
                }, Config.TIME_HANDLER);
            }
        });

    }

    /**
     * Corre este metodo cuando hay mas de 10 contenido a mostrar en la lista
     * @param obj objeto json
     */
    private void segundoPaso(JSONObject obj) {
        try {
            JSONArray array = obj.getJSONArray("clientes");
            for (int i = 0; i < array.length(); i++) {
                AsesorReporteClientesModel getDatos2 = new AsesorReporteClientesModel();
                JSONObject json = null;
                try {
                    json = array.getJSONObject(i);
                    getDatos2.setNombreCliente(json.getString("nombre"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    getDatos2.setConCita(json.getString("cita"));
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        adapter.setLoaded();
    }
}