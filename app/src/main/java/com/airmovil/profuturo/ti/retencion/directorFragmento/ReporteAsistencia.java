package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsistenciaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
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
import java.util.HashMap;
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
    private RecyclerView.Adapter recyclerViewAdapter;
    private int filas;
    private DirectorReporteAsistenciaAdapter adapter;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    private int numeroEmpleado;
    private int idSucursal = 0;
    private int idGerencia = 0;
    private int totalF;
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private OnFragmentInteractionListener mListener;
    private TextView tvFecha;
    private TextView tvATiempo, tvRetardados, tvSinAsistencia;
    private Spinner spinnerSucursal;
    private Spinner spinnerGerencia;
    private EditText etAsesor;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnFiltro;
    private TextView tvResultados;
    private InputMethodManager imm;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private Connected connected;
    private ArrayList<String> sucursales;
    private ArrayList<String> id_sucursales;
    private ArrayList<String> gerencia;
    private ArrayList<String> id_gerencias;

    int idSucursal123 = 0;
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
    // TODO: Rename and change types and number of parameters
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
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        // TODO: consumiendo servicio REST
        primeraPeticion();
        // TODO: Casteo de variables
        variables();
        // TODO: Verificacion de variables
        argumentos();
        // TODO: Dialogos de fechas
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // Minimizacion de teclado
        Config.teclado(getContext(), etAsesor);

        // TODO: Recycler y modelo
        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfras_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // TODO: btn filtro
        final Fragment borrar = this;
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if(tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsistencia fragmento = ReporteAsistencia.newInstance(idGerencia, idSucursal, etAsesor.getText().toString().trim(),
                                tvRangoFecha1.getText().toString().trim(), tvRangoFecha2.getText().toString().trim(), rootView.getContext());
                        borrar.onDestroy();
                        ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                        Config.teclado(getContext(), etAsesor);
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        etAsesor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(etAsesor.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });



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
        // infla el layout del XML
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

    private void primeraPeticion(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIcon(R.drawable.icono_abrir);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_espera));
        progressDialog.show();
        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        sendJson(true);
                    }
                }, Config.TIME_HANDLER);
    }

    /**
     * Setear las variables de xml
     */
    private void variables(){
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

        connected = new Connected();
        sucursales = new ArrayList<String>();
        id_sucursales = new ArrayList<String>();
        gerencia = new ArrayList<String>();
        id_gerencias = new ArrayList<String>();

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
                idSucursal123 = Integer.valueOf(sim1);
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

    /**
     * Inicia la peticion para el consumo de sucursales
     * @param j
     */
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
            Log.d("-->>>Sucursal", "dato : " + idSucursal);
            for(int i=0; i < id_sucursales.size(); i++) {
                Log.d("SELE","by ID ->: " +id_sucursales.get(i));
                Log.d("SELE","ID ->: " +idSucursal);
                if(Integer.valueOf(id_sucursales.get(i)) == idSucursal){
                    Log.d("SELE","SIZE ->: "+position);
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

    //obtener gerencias
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
            Log.d("-->>>Gerencia", "dato : " + idSucursal);
            for(int i=0; i < id_gerencias.size(); i++) {
                Log.d("SELE","by ID ->: " +id_gerencias.get(i));
                Log.d("SELE","ID ->: " +idGerencia);
                if(Integer.valueOf(id_gerencias.get(i)) == idGerencia){
                    Log.d("SELE","SIZE ->: "+position);
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
        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }

    /**
     * Primeta peticion para usar REST
     * @param primerPeticion
     */
    private void sendJson(final boolean primerPeticion) {
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
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_ASISTENCIA, obj,
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
            totalFilas = 50;
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
        tvResultados.setText(filas + " Resulatdos");
        tvATiempo.setText("" + onTime);
        tvRetardados.setText("" + retardo);
        tvSinAsistencia.setText("" + inasistencia);
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);

        adapter = new DirectorReporteAsistenciaAdapter(rootView.getContext(), getDatos1, recyclerView, mParam4, mParam5);
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
                    Log.d("OBJETO","PTR: "+json);
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
