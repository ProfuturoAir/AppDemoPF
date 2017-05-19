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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsistenciaDetalleAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaDetalleModel;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReporteAsistenciaDetalles extends Fragment {
    private static final String TAG = ReporteAsistenciaDetalles.class.getSimpleName();
    private static final String ARG_PARAM1 = "numeroEmpleado";
    private static final String ARG_PARAM2 = "fechaInicio";
    private static final String ARG_PARAM3 = "fechaFin";
    private static final String ARG_PARAM4 = "nombreEmpleado";
    private String mParam1 = "" /*NumeroEmpleado*/, mParam2 = ""/*FechaInicio*/, mParam3 = "" /*fechaFinal*/, mParam4 = "" /*nombreEmpleado*/;
    private int filas, pagina = 1, numeroMaximoPaginas = 0;
    private View rootView;
    private Connected connected;
    private TextView tvFecha, tvLetra, tvNombreAsesor, tvNoEmpleado, tvRangoFechas, tvAtiempo, tvRetardo, tvSinAsistencia, tvRangoFecha1, tvRangoFecha2, tvResultados;
    private Button btnBuscar;
    private List<DirectorReporteAsistenciaDetalleModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private DirectorReporteAsistenciaDetalleAdapter adapter;
    private Fragment borrar = this;
    private OnFragmentInteractionListener mListener;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public ReporteAsistenciaDetalles() {/* se requiere un constructor */}

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @returnuna nueva instancia del fragmento ReporteAsistenciaDetalles.
     */
    public static ReporteAsistenciaDetalles newInstance(String param1, String param2, String param3, String param4, Context context) {
        ReporteAsistenciaDetalles fragment = new ReporteAsistenciaDetalles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     *El sistema lo llama cuando crea el fragmento. En tu implementación, debes inicializar componentes
     * esenciales del fragmento que quieres conservar cuando el fragmento se pause o se detenga y luego se reanude.
     * @param savedInstanceState
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
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
     * Se llama inmediatamente después de que onCreateView(LayoutInflater, ViewGroup, Bundle) ha onCreateView(LayoutInflater, ViewGroup, Bundle)
     * pero antes de que se haya onCreateView(LayoutInflater, ViewGroup, Bundle) estado guardado en la vista.
     * @param view vista de acceso para el xml
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        initVolleyCallback();
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, getContext());
        // TODO: Asisgmacion de variables
        variables();
        // TODO: verificacion de datos existentes
        argumentos();
        // TODO: Dialogos de fecha inicial y fecha final
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha2);
        // TODO: peticion REST
        if(Config.conexion(getContext()))
            sendJson(true);
        else
            Dialogos.dialogoErrorConexion(getContext());
        // TODO Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfrasd_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        // TODO: btn filtro
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if(tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty()){
                        Dialogos.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsistenciaDetalles fragmento = ReporteAsistenciaDetalles.newInstance(mParam1,  tvRangoFecha1.getText().toString(), tvRangoFecha2.getText().toString(), mParam4, rootView.getContext());
                        borrar.onDestroy();ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                    }
                }else{
                    Dialogos.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        tvResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ServicioEmailJSON.enviarEmailReporteAsistenciaDetalles(getContext(), mParam1, mParam2, mParam3);
            }
        });
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
        return inflater.inflate(R.layout.director_fragmento_reporte_asistencia_detalles, container, false);
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
     * Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Envio de parametros de retroceso de ReporteAsistenciaDetalles a ReporteAsistencia
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
                        ReporteAsistencia fragmento = new ReporteAsistencia();
                        Director d1 = (Director) getContext();
                        //fragment 1. fechaInicio 2. fechaFin 3.idGerencia 4.idSucursal 5.idAsesor 6.numeroEmpleado 7.nombreEmpleado 8.numeroCuenta 9.cita 10.hora 11.idTramite
                        d1.envioParametros(fragmento, getArguments().getString(ARG_PARAM2), getArguments().getString(ARG_PARAM3), 0, 0, "", getArguments().getString(ARG_PARAM1),getArguments().getString(ARG_PARAM4), "", false, "", 0);
                    }
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Casteo de variables
     */
    private void variables(){
        tvFecha = (TextView) rootView.findViewById(R.id.ddfrasd_tv_fecha);
        tvLetra = (TextView) rootView.findViewById(R.id.ddfrasd_tv_letra);
        tvNombreAsesor = (TextView) rootView.findViewById(R.id.ddfrasd_tv_nombre_asesor);
        tvNombreAsesor.setText("Nombre del Asesor: " + mParam4);
        tvNoEmpleado = (TextView) rootView.findViewById(R.id.ddfrasd_tv_numero_empleado_asesor);
        tvNoEmpleado.setText("Numero de empleado asesor: " + mParam1);
        tvAtiempo = (TextView) rootView.findViewById(R.id.ddfrasd_tv_a_tiempo);
        tvRetardo = (TextView) rootView.findViewById(R.id.ddfrasd_tv_retardados);
        tvSinAsistencia = (TextView) rootView.findViewById(R.id.ddfrasd_tv_sin_asistencia);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.ddfrasd_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.ddfrasd_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.ddfrasd_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.ddfrasd_tv_resultados);
        tvLetra.setText(String.valueOf(String.valueOf(mParam4).charAt(0)));
        getDatos1 = new ArrayList<>();
        connected = new Connected();
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
        try{
            boolean argumentos = (getArguments()!=null);
            rqt.put("numeroEmpleado", (argumentos) ? getArguments().getString(ARG_PARAM1) : "");
            rqt.put("pagina", pagina);
            periodo.put("fechaInicio", (argumentos) ? getArguments().getString(ARG_PARAM2) : Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos) ? getArguments().getString(ARG_PARAM3) : Dialogos.fechaSiguiente());
            rqt.put("periodo", periodo);
            obj.put("rqt", rqt);
            Log.d(TAG, "<- RQT ->" + obj + "\n");
        }catch (JSONException e){
            e.printStackTrace();
        }
        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_REPORTE_ASISTENCIA_DETALLE, obj);
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
        int totalFilas = 0;
        try{
            JSONObject asistencia = obj.getJSONObject("asistencia");
            onTime = asistencia.getInt("onTime");
            retardo = asistencia.getInt("retardo");
            inasistencia = asistencia.getInt("inasistencia");
            totalFilas = obj.getInt("filasTotal");
            JSONArray registroHorario = obj.getJSONArray("RegistroHorario");
            for(int i = 0; i < registroHorario.length(); i++){
                DirectorReporteAsistenciaDetalleModel getDatos2 = new DirectorReporteAsistenciaDetalleModel();
                JSONObject json = null;
                try{
                    json = registroHorario.getJSONObject(i);
                    JSONObject comida = json.getJSONObject("comida");
                    getDatos2.setComidaLatitud(comida.getString("latitud"));
                    getDatos2.setComidaLongitud(comida.getString("longitud"));
                    getDatos2.setComidaHora(comida.getString("horaEntrada"));
                    getDatos2.setComidaSalida(comida.getString("horaSalida"));
                    JSONObject entrada = json.getJSONObject("entrada");
                    getDatos2.setEntradaHora(entrada.getString("hora"));
                    getDatos2.setEntradaLatitud(entrada.getString("latitud"));
                    getDatos2.setEntradaLongitud(entrada.getString("longitud"));
                    JSONObject salida = json.getJSONObject("salida");
                    getDatos2.setSalidaHora(salida.getString("hora"));
                    getDatos2.setSalidaLatitud(salida.getString("latitud"));
                    getDatos2.setSalidaLongitud(salida.getString("longitud"));
                    getDatos2.setFechaAsistencia(json.getString("fecha"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvResultados.setText("" + totalFilas);
        tvAtiempo.setText("" + onTime);
        tvRetardo.setText("" + retardo);
        tvSinAsistencia.setText("" + inasistencia);
        tvResultados.setText("" + totalFilas + " registros ");
        // TODO: calucula el tamaño de filas y devuelve la cantidad de paginas a procesar
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        // TODO: envio de datos al adaptador para incluir dentro del recycler
        adapter = new DirectorReporteAsistenciaDetalleAdapter(rootView.getContext(), getDatos1, recyclerView);
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
                final Runnable r =new Runnable() {
                    @Override
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
            filas = obj.getInt("filasTotal");
            JSONArray registroHorario = obj.getJSONArray("RegistroHorario");
            for(int i = 0; i < registroHorario.length(); i++){
                DirectorReporteAsistenciaDetalleModel getDatos2 = new DirectorReporteAsistenciaDetalleModel();
                JSONObject json = null;
                try{
                    json = registroHorario.getJSONObject(i);
                    JSONObject comida = json.getJSONObject("comida");
                    getDatos2.setComidaLatitud(comida.getString("latitud"));
                    getDatos2.setComidaLongitud(comida.getString("longitud"));
                    getDatos2.setComidaHora(comida.getString("horaEntrada"));
                    getDatos2.setComidaSalida(comida.getString("horaSalida"));
                    JSONObject entrada = json.getJSONObject("entrada");
                    getDatos2.setEntradaHora(entrada.getString("hora"));
                    getDatos2.setEntradaLatitud(entrada.getString("latitud"));
                    getDatos2.setEntradaLongitud(entrada.getString("longitud"));
                    JSONObject salida = json.getJSONObject("salida");
                    getDatos2.setSalidaHora(salida.getString("hora"));
                    getDatos2.setSalidaLatitud(salida.getString("latitud"));
                    getDatos2.setSalidaLongitud(salida.getString("longitud"));
                    getDatos2.setFechaAsistencia(json.getString("fecha"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        adapter.setLoaded();
    }

    /**
     * Se utiliza para cololar datos recibidos entre una busqueda(por ejemplo: fechas)
     */
    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM2) + " - " + getArguments().getString(ARG_PARAM3));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM2));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM3));
        }
    }
}