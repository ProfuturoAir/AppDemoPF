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
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.SpinnerDatos;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaModel;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReporteAsistencia extends Fragment{
    private static final String TAG = ReporteAsistencia.class.getSimpleName();
    private static final String ARG_PARAM1 = "idGerencia", ARG_PARAM2 = "idSucursal", ARG_PARAM3 = "idAsesor", ARG_PARAM4 = "fechaInicio", ARG_PARAM5 = "fechaFin";
    private int mParam1 /*idGerencia*/, mParam2; //idSucursal
    private String mParam3, mParam4, mParam5;
    private List<DirectorReporteAsistenciaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private DirectorReporteAsistenciaAdapter adapter;
    private int pagina = 1, numeroMaximoPaginas = 0, idSucursal = 0, idGerencia = 0, filas;
    private View rootView;
    private OnFragmentInteractionListener mListener;
    private TextView tvFecha, tvATiempo, tvRetardados, tvSinAsistencia, tvRangoFecha1, tvRangoFecha2, tvResultados;
    private Spinner spinnerGerencia, spinnerSucursal;
    private EditText etAsesor;
    private Button btnFiltro;
    private Connected connected;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public ReporteAsistencia() {/* Constructor público vacío obligatorio */}

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
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
                if (requestType.trim().equals("true")) {
                    loading.dismiss();
                    primerPaso(response);
                }else{
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
        // TODO: Asignacion de variables
        variables();
        // TODO: Verificacion de datos almacenados en bundle
        argumentos();
        // TODO: Dialogo de fecha inicio y fecha fin
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: Peticion REST
        if(Config.conexion(getContext()))
            sendJson(true);
        else
            Dialogos.dialogoErrorConexion(getContext());
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
                        Dialogos.dialogoFechasVacias(getContext());
                    }else{
                        if(Config.comparacionFechas(getContext(), tvRangoFecha1.getText().toString().trim(), tvRangoFecha2.getText().toString().trim()) == false) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ReporteAsistencia fragmento = ReporteAsistencia.newInstance(mParam1 = Config.ID_GERENCIA, mParam2 = Config.ID_SUCURSAL, etAsesor.getText().toString(), tvRangoFecha1.getText().toString(), tvRangoFecha2.getText().toString(), rootView.getContext());
                            borrar.onDestroy();
                            ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                            Config.teclado(getContext(), etAsesor);
                        }
                    }
                }else{
                    Dialogos.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        tvResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean argumentos = (getArguments()!=null);
                ServicioEmailJSON.enviarEmailReporteAsistencia(getContext(), (argumentos)?getArguments().getInt(ARG_PARAM1):0, (argumentos)?getArguments().getInt(ARG_PARAM2):0, (argumentos)?getArguments().getString(ARG_PARAM3):"", (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual(), (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente(), (argumentos) ? true : false);
            }
        });

        SpinnerDatos.spinnerGerencias(getContext(), spinnerGerencia, (getArguments()!=null) ? Config.ID_GERENCIA_POSICION : 0);
        SpinnerDatos.spinnerSucursales(getContext(), spinnerSucursal, (getArguments()!=null) ? Config.ID_SUCURSAL_POSICION : 0);
    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater acceso para inflar XML
     * @param container contenido
     * @param savedInstanceState estado de los elementos almacenados
     * @return el fragmento relacionado con la actividad
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.director_fragmento_reporte_asistencia, container, false);
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
     * Se llama para desasociar el fragmento de la actividad.
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
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setear las variables de xml
     */
    private void variables(){
        connected = new Connected();
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
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
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
            rqt.put("pagina", pagina);
            periodo.put("fechaInicio", (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente());
            rqt.put("periodo", periodo);
            rqt.put("usuario",Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "<-RQT-> \n" + obj + "\n");
        } catch (JSONException e) {
            Dialogos.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }
        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_REPORTE_ASISTENCIA, obj);
    }

    /**
     * Inicia este metodo para llenar la lista de elementos, cada 10, inicia solamente con 10, despues inicia el metodo segundoPaso
     * @param obj jsonObject
     */
    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "Response: --->" + obj.toString() + "\n");
        int onTime = 0;
        int retardo = 0;
        int inasistencia = 0;
        int totalFilas = 1;
        filas = 0;
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
        // TODO: calucula el tamaño de filas y devuelve la cantidad de paginas a procesar
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        boolean argumentos = (getArguments()!=null);
        // TODO: envio de datos al adaptador para incluir dentro del recycler
        adapter = new DirectorReporteAsistenciaAdapter(rootView.getContext(), getDatos1, recyclerView, (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual(), (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        // TODO: verificaion si existe un scroll enviando al segundo metodo
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
     * @param obj jsonObject de respuesta
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
