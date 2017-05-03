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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsistenciaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaModel;
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

public class ReporteAsistencia extends Fragment implements Spinner.OnItemSelectedListener{
    private static final String TAG = ReporteAsistencia.class.getSimpleName();
    private static final String ARG_PARAM1 = "idGerencia";
    private static final String ARG_PARAM2 = "idSucursal";
    private static final String ARG_PARAM3 = "idAsesor";
    private static final String ARG_PARAM4 = "fechaInicio";
    private static final String ARG_PARAM5 = "fechaFin";
    private int mParam1; //idGerencia
    private int mParam2; //idSucursal
    private String mParam3; //idAsesor
    private String mParam4; //fechaInicio
    private String mParam5; //fechaFin
    private List<DirectorReporteAsistenciaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private DirectorReporteAsistenciaAdapter adapter;
    private int pagina = 1, numeroMaximoPaginas = 0, idSucursal = 0, idGerencia = 0;
    private int filas, totalF;
    private View rootView;
    private OnFragmentInteractionListener mListener;
    private TextView tvFecha, tvATiempo, tvRetardados, tvSinAsistencia, tvRangoFecha1, tvRangoFecha2, tvResultados;
    private Spinner spinnerGerencia, spinnerSucursal;
    private EditText etAsesor;
    private Button btnFiltro;
    private Connected connected;
    private ArrayList<String> sucursales;
    private ArrayList<String> id_sucursales;
    private ArrayList<String> gerencia;
    private ArrayList<String> id_gerencias;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public ReporteAsistencia() {
        // Constructor público vacío obligatorio
    }

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 parametro 1 id gerencia.
     * @param param2 parametro 2 id sucursal.
     * @param param3 parametro 3 id asesor
     * @param param4 parametro 4 id fecha inicio
     * @param param5 parametro 5 id fecha fin
     * @return una nueva insrancia del fragmento ReporteAsistencia.
     */
    public static ReporteAsistencia newInstance(int param1, int param2, String param3, String param4, String param5, Context context) {
        ReporteAsistencia fragment = new ReporteAsistencia();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
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
                }else{
                    segundoPaso(response);
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
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        initVolleyCallback();
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, getContext());
        // TODO: Peticion REST
        sendJson(true);
        // TODO: Asignacion de variables
        variables();
        // TODO: Verificacion de datos almacenados en bundle
        argumentos();
        // TODO: Dialogo de fecha inicio y fecha fin
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: Recycler y modelo
        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfras_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        // TODO: Btn nueva busqueda con fitros
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if(tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsistencia fragmento = ReporteAsistencia.newInstance(mParam1 = idGerencia, mParam2 = idSucursal, etAsesor.getText().toString(), tvRangoFecha1.getText().toString(), tvRangoFecha2.getText().toString(), rootView.getContext());
                        borrar.onDestroy();ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                        Config.teclado(getContext(), etAsesor);
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });
        boolean argumentos = (getArguments()!=null);
        ServicioEmailJSON.enviarEmailReporteAsistencia(getContext(), tvResultados, (argumentos)?getArguments().getInt(ARG_PARAM1):0, (argumentos)?getArguments().getInt(ARG_PARAM2):0, (argumentos)?getArguments().getString(ARG_PARAM3):"", (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual(), (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente(), (argumentos) ? true : false);
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
        return inflater.inflate(R.layout.director_fragmento_reporte_asistencia, container, false);
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

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setear las variables de xml
     */
    private void variables(){
        connected = new Connected();
        sucursales = new ArrayList<String>();
        id_sucursales = new ArrayList<String>();
        gerencia = new ArrayList<String>();
        id_gerencias = new ArrayList<String>();
        tvFecha = (TextView) rootView.findViewById(R.id.ddfras_tv_fecha);
        tvATiempo = (TextView) rootView.findViewById(R.id.ddfras_tv_a_tiempo);
        tvRetardados  = (TextView) rootView.findViewById(R.id.ddfras_tv_retardados);
        tvSinAsistencia = (TextView) rootView.findViewById(R.id.ddfras_tv_sin_asistencia);
        spinnerGerencia = (Spinner) rootView.findViewById(R.id.ddfras_spinner_gerencia);
        spinnerSucursal = (Spinner) rootView.findViewById(R.id.ddfras_spinner_sucursal);
        etAsesor = (EditText) rootView.findViewById(R.id.ddfras_et_asesor);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.ddfras_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.ddfras_tv_fecha_rango2);
        btnFiltro = (Button) rootView.findViewById(R.id.ddfras_btn_filtro);
        tvResultados = (TextView) rootView.findViewById(R.id.ddfras_tv_registros);
        spinnerSucursal.setOnItemSelectedListener(this);
        spinnerGerencia.setOnItemSelectedListener(this);
        getData1();
        getData2();
    }


    /**
     * Spinner para selccion de elemetos de geretnecias o sucursales
     * @param parent
     * @param view
     * @param position
     * @param id
     */
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.ddfras_spinner_sucursal:
                String sim1 = id_sucursales.get(position);
                idSucursal = Integer.valueOf(sim1);
                break;
            case R.id.ddfras_spinner_gerencia:
                String sim2 = id_gerencias.get(position);
                idGerencia = Integer.valueOf(sim2);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }

    /**
     * Inicia la peticion para el consumo del spinner
     */
    private void getData1(){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_SUCURSALES,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray j = null;
                        try {
                            j = response.getJSONArray("Sucursales");
                            getSucursales(j);
                        } catch (JSONException e) {

                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void getData2(){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GERENCIAS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray j = null;
                        try {
                            j = response.getJSONArray("Gerencias");
                            getGerencias(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);

    }

    private void getSucursales(JSONArray j){
        sucursales.add("Selecciona una sucursal");
        id_sucursales.add("0");
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                sucursales.add(json.getString("nombre"));
                id_sucursales.add(json.getString("idSucursal"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int position=0;
        if(idSucursal!=0){
            for(int i=0; i < id_sucursales.size(); i++) {
                if(Integer.valueOf(id_sucursales.get(i)) == idSucursal){
                    position = i;
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, sucursales);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
        spinnerSucursal.setSelection(position);
    }

    private void getGerencias(JSONArray j){
        gerencia.add("Selecciona una gerencia");
        id_gerencias.add("0");
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                gerencia.add(json.getString("nombre"));
                id_gerencias.add(json.getString("idGerencia"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        int position=0;
        if(idGerencia!=0){
            for(int i=0; i < id_gerencias.size(); i++) {
                if(Integer.valueOf(id_gerencias.get(i)) == idGerencia){
                    position = i;
                    break;
                }
            }
        }
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, gerencia);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGerencia.setAdapter(adapter);
        spinnerGerencia.setSelection(position);
    }

    /**
     * Inicia las fechas dependientos si tiene datos procesados
     */
    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM4) + " - " + getArguments().getString(ARG_PARAM5));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM4));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM5));
            if(mParam2 != 0) idSucursal = mParam2;
            if(mParam1 != 0) idGerencia = mParam1;
            etAsesor.setText(getArguments().getString(ARG_PARAM3));
        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }

    /**
     * Primeta peticion para usar REST
     * @param primerPeticion
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try {
            // TODO: Formacion del JSON request
            boolean argumentos = (getArguments()!=null);
            rqt.put("idSucursal", (argumentos)?getArguments().getInt(ARG_PARAM2):0);
            rqt.put("idGerencia", (argumentos)?getArguments().getInt(ARG_PARAM1):0);
            rqt.put("numeroEmpleado", (argumentos)?getArguments().getString(ARG_PARAM3):"");
            rqt.put("pagina", pagina);
            periodo.put("fechaInicio", (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente());
            rqt.put("periodo", periodo);
            rqt.put("usuario",Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "<-RQT-> \n" + obj + "\n");
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }
        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_REPORTE_ASISTENCIA, obj);
    }

    /**
     * Inicia la lista solo con 10 elementos
     * @param obj
     */
    private void primerPaso(JSONObject obj) {
        int onTime = 0;
        int retardo = 0;
        int inasistencia = 0;
        int totalFilas = 1;
        int filas = 0;
        try{
            JSONObject asistencia = obj.getJSONObject("asistencia");
            onTime = asistencia.getInt("onTime");
            retardo = asistencia.getInt("retardo");
            inasistencia = asistencia.getInt("inasistencia");
            JSONArray empleado = obj.getJSONArray("Empleado");
            totalFilas = obj.getInt("filasTotal");
            for(int i = 0; i < empleado.length(); i++){
                DirectorReporteAsistenciaModel getDatos2 = new DirectorReporteAsistenciaModel();
                JSONObject json = null;
                try{
                    json = empleado.getJSONObject(i);
                    getDatos2.setNombre(json.getString("nombre"));
                    getDatos2.setnEmpleado(String.valueOf(json.getInt("numeroEmpleado")));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvResultados.setText(totalFilas + " Resulatdos");
        tvATiempo.setText("" + onTime);
        tvRetardados.setText("" + retardo);
        tvSinAsistencia.setText("" + inasistencia);
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        boolean argumentos = (getArguments()!=null);
        adapter = new DirectorReporteAsistenciaAdapter(rootView.getContext(), getDatos1, recyclerView, (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual(), (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente());
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
                }, 5000);
            }
        });
    }

    /**
     * consume la lista cada 10 elemtos con su scrollView
     * @param obj
     */
    private void segundoPaso(JSONObject obj) {
        try{
            JSONObject asistencia = obj.getJSONObject("asistencia");
            JSONArray empleado = obj.getJSONArray("Empleado");
            filas = obj.getInt("filasTotal");
            for(int i = 0; i < empleado.length(); i++){
                DirectorReporteAsistenciaModel getDatos2 = new DirectorReporteAsistenciaModel();
                JSONObject json = null;
                try{
                    json = empleado.getJSONObject(i);
                    getDatos2.setNombre(json.getString("nombre"));
                    getDatos2.setnEmpleado(String.valueOf(json.getInt("numeroEmpleado")));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
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
}
