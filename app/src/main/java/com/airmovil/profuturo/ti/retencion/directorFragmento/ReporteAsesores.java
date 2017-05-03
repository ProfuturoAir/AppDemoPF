package com.airmovil.profuturo.ti.retencion.directorFragmento;

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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsesoresAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteAsesoresModel;
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

public class ReporteAsesores extends Fragment {
    private static final String TAG = ReporteAsesores.class.getSimpleName();
    private static final String ARG_PARAM1 = "idAsesor"; // numero asesor
    private static final String ARG_PARAM2 = "fechaInicio"; // fecha Inicio
    private static final String ARG_PARAM3 = "fechaFin"; // fecha final
    private static final String ARG_PARAM4 = "idGerencia"; // id gerencia
    private static final String ARG_PARAM5 = "idSucursal"; // id sucursal
    private String mParam1; // fecha inicio
    private String mParam2; // fecha fin
    private String mParam3; // id asesor
    private int mParam4; // id gerencia
    private int mParam5; // id sucursal
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    private DirectorReporteAsesoresAdapter adapter;
    private List<GerenteReporteAsesoresModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private View rootView;
    private Connected connected;
    private TextView tvFecha, tvEmitidas, tvNoEmitidas, tvSaldoEmitido, tvSaldoNoEmitido, tvRangoFecha1, tvRangoFecha2, tvResultados;
    private EditText etAsesor;
    private Button btnBuscar;
    private int filas;
    private final Fragment borrar = this;
    private OnFragmentInteractionListener mListener;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public ReporteAsesores() {/* Constructor público vacío obligatorio */}

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return una nueva instancia del fragmento ReporteAsesores.
     */
    public static ReporteAsesores newInstance(String param1, String param2, String param3, Context context) {
        ReporteAsesores fragment = new ReporteAsesores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
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

    /**
     * metodo para callback de volley
     */
    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if (requestType.trim().equals("true")) {
                    loading.dismiss();
                    primerPaso(response);
                } else {
                    segundoPaso(response);
                }
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = volleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Asisgnacion de variables
        variables();
        // TODO: verifica si existen datos en el fragmento
        argumentos();
        // TODO: primera peticion rest
        sendJson(true);
        // TODO: llama los dialos fecha inicio y fecha final
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: Recycler
        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.dfra_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if(tvRangoFecha1.getText().toString().isEmpty() ||  tvRangoFecha2.getText().toString().isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsesores fragmento = ReporteAsesores.newInstance(etAsesor.getText().toString(),tvRangoFecha1.getText().toString(), tvRangoFecha2.getText().toString(), rootView.getContext());
                        borrar.onDestroy();ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                    }
                    // TODO: ocultar teclado
                    Config.teclado(getContext(), etAsesor);
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        boolean argumentos =  (getArguments()!=null);
        ServicioEmailJSON.enviarEmailReporteAsespres(getContext(), tvResultados, (argumentos) ? getArguments().getString(ARG_PARAM2):Dialogos.fechaActual(),
                (argumentos)? getArguments().getString(ARG_PARAM3) : Dialogos.fechaSiguiente(), (argumentos)? getArguments().getString(ARG_PARAM1) : "", (argumentos) ? true : false);


    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflar el diseño de este fragmento
        return inflater.inflate(R.layout.director_fragmento_reporte_asesores, container, false);
    }

    // TODO: Renombrar método, actualizar argumento y método de gancho en evento de IU
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
     * Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en ese
     * actividad.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void variables(){
        tvFecha = (TextView) rootView.findViewById(R.id.dfra_tv_fecha);
        tvEmitidas = (TextView) rootView.findViewById(R.id.dfra_tv_emitidas);
        tvNoEmitidas = (TextView) rootView.findViewById(R.id.dfra_tv_no_emitidas);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.dfra_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.dfra_tv_saldo_no_emitido);
        etAsesor = (EditText) rootView.findViewById(R.id.dfra_et_asesor);
        tvResultados = (TextView) rootView.findViewById(R.id.dfra_tv_total_registros);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.dfra_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.dfra_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.dfra_btn_buscar);
        connected = new Connected();
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            boolean argumentos = (getArguments()!=null);
            rqt.put("idGerencia", (argumentos==true) ? getArguments().getInt(ARG_PARAM4) : 0);
            rqt.put("idSucursal", (argumentos==true) ? getArguments().getInt(ARG_PARAM5) : 0);
            rqt.put("numeroEmpleadoAsesor", (argumentos==true) ? getArguments().getString(ARG_PARAM1) : "");
            rqt.put("pagina", pagina);
            periodo.put("fechaFin", (argumentos==true) ?  getArguments().getString(ARG_PARAM3) : Dialogos.fechaSiguiente());
            periodo.put("fechaInicio", (argumentos==true) ? getArguments().getString(ARG_PARAM2) : Dialogos.fechaActual());
            rqt.put("periodo", periodo);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, " RQT ->" + obj);
        }catch (JSONException e){
            e.printStackTrace();
        }
        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_REPORTE_RETENCION_ASESORES, obj);
    }

    /**
     * Inicia este metodo para llenar la lista de elementos, cada 10, inicia solamente con 10, despues inicia el metodo segundoPaso
     * @param
     * @param
     * @param
     */
    private void primerPaso(JSONObject obj) {
        int emitidos = 0;
        int noEmitido = 0;
        int saldoEmitido = 0;
        int saldoNoEmitido = 0;
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("Asesor");
            JSONObject objEmitidos = obj.getJSONObject("retenido");
            emitidos = objEmitidos.getInt("retenido");
            noEmitido = objEmitidos.getInt("noRetenido");
            JSONObject objSaldo = obj.getJSONObject("saldo");
            saldoEmitido = objSaldo.getInt("saldoRetenido");
            saldoNoEmitido = objSaldo.getInt("saldoNoRetenido");
            totalFilas = 50;
            filas = obj.getInt("filasTotal");
            for(int i = 0; i < array.length(); i++){
                GerenteReporteAsesoresModel getDatos2 = new GerenteReporteAsesoresModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNumeroEmpleado(json.getInt("numeroEmpleado"));
                    JSONObject cita = json.getJSONObject("cita");
                    getDatos2.setConCita(cita.getInt("conCita"));
                    getDatos2.setSinCita(cita.getInt("sinCita"));
                    JSONObject retenido = json.getJSONObject("retenido");
                    getDatos2.setEmitido(retenido.getInt("retenido"));
                    getDatos2.setNoEmitido(retenido.getInt("noRetenido"));
                    JSONObject saldo = json.getJSONObject("saldo");
                    getDatos2.setSaldoEmitido(saldo.getInt("saldoRetenido"));
                    getDatos2.setSaldoNoEmetido(saldo.getInt("saldoNoRetenido"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvEmitidas.setText("" + emitidos);
        tvNoEmitidas.setText("" + noEmitido);
        tvSaldoEmitido.setText("" + Config.nf.format(saldoEmitido));
        tvSaldoNoEmitido.setText("" + Config.nf.format(saldoNoEmitido));
        tvResultados.setText("" + filas + " Resultados ");

        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        boolean argumentos = (getArguments()!=null);
        adapter = new DirectorReporteAsesoresAdapter(rootView.getContext(), getDatos1, recyclerView, (argumentos) ? getArguments().getString(ARG_PARAM2) : Dialogos.fechaActual() ,
                (argumentos) ? getArguments().getString(ARG_PARAM3) : Dialogos.fechaSiguiente());
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
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        adapter.notifyItemInserted(getDatos1.size() - 1);
                    }
                };
                handler.post(r);
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
     * Se vuelve a llamaar este metodo para llenar la lista cada 10 contenidos
     * @param obj
     */
    private void segundoPaso(JSONObject obj) {
        try{
            JSONArray array = obj.getJSONArray("Asesor");
            for(int i = 0; i < array.length(); i++){
                GerenteReporteAsesoresModel getDatos2 = new GerenteReporteAsesoresModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNumeroEmpleado(json.getInt("numeroEmpleado"));
                    JSONObject cita = json.getJSONObject("cita");
                    getDatos2.setConCita(cita.getInt("conCita"));
                    getDatos2.setSinCita(cita.getInt("sinCita"));
                    JSONObject retenido = json.getJSONObject("retenido");
                    getDatos2.setEmitido(retenido.getInt("retenido"));
                    getDatos2.setNoEmitido(retenido.getInt("noRetenido"));
                    JSONObject saldo = json.getJSONObject("saldo");
                    getDatos2.setSaldoEmitido(saldo.getInt("saldoRetenido"));
                    getDatos2.setSaldoNoEmetido(saldo.getInt("saldoNoRetenido"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        adapter.setLoaded();
    }

    /**
     * Inicia las fechas, dependiendo si existen datos procesados
     */
    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM2) + " - " + getArguments().getString(ARG_PARAM3));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM2));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM3));
            etAsesor.setText(getArguments().getString(ARG_PARAM1));
        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }
}
