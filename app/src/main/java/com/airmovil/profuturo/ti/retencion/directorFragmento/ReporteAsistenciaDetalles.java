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
import android.widget.Button;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsistenciaDetalleAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaDetalleModel;
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

public class ReporteAsistenciaDetalles extends Fragment {
    private static final String TAG = ReporteAsistenciaDetalles.class.getSimpleName();
    private static final String ARG_PARAM1 = "numeroEmpleado";
    private static final String ARG_PARAM2 = "fechaIni";
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
    private RecyclerView.Adapter recyclerViewAdapter;
    private DirectorReporteAsistenciaDetalleAdapter adapter;
    private Fragment borrar = this;
    private OnFragmentInteractionListener mListener;

    public ReporteAsistenciaDetalles() {
        // se requiere un constructor
    }

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
     * Se llama inmediatamente después de que onCreateView(LayoutInflater, ViewGroup, Bundle) ha onCreateView(LayoutInflater, ViewGroup, Bundle)
     * pero antes de que se haya onCreateView(LayoutInflater, ViewGroup, Bundle) estado guardado en la vista.
     * @param view vista de acceso para el xml
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        // TODO: peticion REST
        primeraPeticion();
        // TODO: Asisgmacion de variables
        variables();
        // TODO: verificacion de datos existentes
        argumentos();
        // TODO: Dialogos de fecha inicial y fecha final
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha2);
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
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsistenciaDetalles fragmento = ReporteAsistenciaDetalles.newInstance(mParam1,  tvRangoFecha1.getText().toString(),
                                tvRangoFecha2.getText().toString(), mParam4, rootView.getContext());
                        borrar.onDestroy();
                        ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        ServicioEmailJSON.enviarEmailReporteAsistenciaDetalles(getContext(), tvResultados, mParam1, mParam2, mParam3);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.director_fragmento_reporte_asistencia_detalles, container, false);
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
        getDatos1 = new ArrayList<>();
        connected = new Connected();
    }

    public interface OnFragmentInteractionListener {
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

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
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
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_ASISTENCIA_DETALLE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if (primerPeticion) {
                            loading.dismiss();
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

    private void primerPaso(JSONObject obj) {
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
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        adapter = new DirectorReporteAsistenciaDetalleAdapter(rootView.getContext(), getDatos1, recyclerView);
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

    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM2) + " - " + getArguments().getString(ARG_PARAM3));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM2));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM3));
        }
    }
}