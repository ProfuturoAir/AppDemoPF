package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

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
import android.widget.Spinner;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.GerenteReporteSucursalesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.SpinnerDatos;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteSucursalesModel;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReporteSucursales extends Fragment{
    private static final String TAG = ReporteSucursales.class.getSimpleName();
    private static final String ARG_PARAM1 = "idGerencia", ARG_PARAM2 = "idSucursal", ARG_PARAM3 = "numeroEmpleado", ARG_PARAM4 = "fechaInicio", ARG_PARAM5 = "fechaFin";
    private String numeroEmpleado = null;
    private GerenteReporteSucursalesAdapter adapter;
    private List<GerenteReporteSucursalesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private View rootView;
    private int pagina = 1, numeroMaximoPaginas = 0, filas, idSucursal = 0,idGerencia = 0;
    private TextView tvFecha, tvEmitidas, tvNoEmitidas, tvSaldoEmitido, tvSaldoNoEmitido, tvResultados, tvRangoFecha1, tvRangoFecha2;
    private Spinner spinnerSucursales;
    private ArrayList<String> sucursales, id_sucursales;
    private Button btnBuscar;
    private Fragment borrar = this;
    private OnFragmentInteractionListener mListener;
    private Connected connected;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public ReporteSucursales() {/*Se requiere un constructor vacio*/}

    /**
     * @param param1 idGerencia
     * @param param2 idSucursal
     * @param param3 numeroEmpleado
     * @param param4 fechaInicio
     * @param param5 fechaFin
     * @param context representa el estado actual de la app, para obtener su estado actual
     * @return una nueva instancia del fragmento ReporteSucursales.
     */
    public static ReporteSucursales newInstance(int param1, int param2, String param3, String param4, String param5, Context context) {
        ReporteSucursales fragment = new ReporteSucursales();
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

    /**
     * Se llama inmediatamente después de que onCreateView(LayoutInflater, ViewGroup, Bundle) ha onCreateView(LayoutInflater, ViewGroup, Bundle)
     * pero antes de que se haya onCreateView(LayoutInflater, ViewGroup, Bundle) estado guardado en la vista.
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para calback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = volleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Asisgnacion de variables
        variables();
        // TODO: verifica si existen datos en el fragmento
        argumentos();
        // TODO: primera peticion rest
        if(Config.conexion(getContext()))
            sendJson(true);
        else
            Dialogos.dialogoErrorConexion(getContext());

        // TODO: llama los dialos fecha inicio y fecha final
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: Recycler
        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.gfrs_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        // TODO: Boton filtro
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(tvRangoFecha1.getText().toString().equals("") || tvRangoFecha2.getText().toString().equals("")){
                    Dialogos.dialogoFechasVacias(getContext());
                }else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ReporteSucursales fragmento = ReporteSucursales.newInstance(0, Config.ID_SUCURSAL, "", tvRangoFecha1.getText().toString(), tvRangoFecha2.getText().toString(), rootView.getContext());
                    borrar.onDestroy();ft.remove(borrar).replace(R.id.content_gerente, fragmento).addToBackStack(null).commit();
                }
            }
        });

        tvResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean argumentos = (getArguments()!=null);
                ServicioEmailJSON.enviarEmailReporteSucursales(getContext(),(argumentos)?getArguments().getInt(ARG_PARAM2): 0, (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual(), (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente(), (argumentos)?true:false);
            }
        });
    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater inflacion del xml
     * @param container contenedor del ml
     * @param savedInstanceState datos guardados
     * @return el fragmento declarado DIRECTOR INICIO
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gerente_fragmento_reporte_sucursales, container, false);
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
     * A la actividad y potencialmente otros fragmentos contenidos en estaactividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setea los elementos del XML
     */
    public void variables(){
        connected = new Connected();
        tvFecha = (TextView) rootView.findViewById(R.id.gfrs_tv_fecha);
        tvEmitidas = (TextView) rootView.findViewById(R.id.gfrs_tv_emitidas);
        tvNoEmitidas = (TextView) rootView.findViewById(R.id.gfrs_tv_no_emitidas);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.gfrs_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.gfrs_tv_saldo_no_emitido);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.gfrs_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.gfrs_tv_fecha_rango2);
        spinnerSucursales = (Spinner) rootView.findViewById(R.id.gfrs_spinner_sucursales);
        btnBuscar = (Button) rootView.findViewById(R.id.gfrs_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.gfrs_tv_registros);
        SpinnerDatos.spinnerSucursales(getContext(),spinnerSucursales, (getArguments()!=null)? Config.ID_SUCURSAL_POSICION:0);
    }

    /**
     *  Espera el regreso de fechas incial (hoy y el dia siguiente)
     *  y cuando se realiza una nueva busqueda, retorna las fechas seleccionadas
     */
    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM4) + " - " + getArguments().getString(ARG_PARAM5));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM4));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM5));
            idSucursal = getArguments().getInt("idSucursal");
            idGerencia = getArguments().getInt("idGerencia");
            numeroEmpleado = getArguments().getString("numeroEmpleado");
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
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;
        JSONObject obj = new JSONObject();
        try {
            boolean argumentos = (getArguments()!=null);
            JSONObject rqt = new JSONObject();
            rqt.put("idGerencia", (getArguments()!=null)? getArguments().getInt(ARG_PARAM1):0);
            rqt.put("idSucursal", (getArguments()!=null)? getArguments().getInt(ARG_PARAM2):0);
            rqt.put("numeroEmpleado", (argumentos)?numeroEmpleado:"");
            rqt.put("pagina", pagina);
            JSONObject periodo = new JSONObject();
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", (argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente());
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "< RQT -> \n" + obj + "\n");
        } catch (JSONException e) {
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_REPORTE_RETENCION_SUCURSALES, obj);
    }

    /**
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj) {
        Log.d("arg","response--->"+obj);
        int emitidos = 0;
        int noEmitido = 0;
        int saldoEmitido = 0;
        int saldoNoEmitido = 0;
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("Sucursal");
            JSONObject objEmitidos = obj.getJSONObject("retenido");
            emitidos = objEmitidos.getInt("retenido");
            noEmitido = objEmitidos.getInt("noRetenido");
            JSONObject objSaldo = obj.getJSONObject("saldo");
            saldoEmitido = objSaldo.getInt("saldoRetenido");
            saldoNoEmitido = objSaldo.getInt("saldoNoRetenido");
            totalFilas = obj.getInt("filasTotal");
            for(int i = 0; i < array.length(); i++){
                GerenteReporteSucursalesModel getDatos2 = new GerenteReporteSucursalesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
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
            Dialogos.dialogoErrorDatos(getContext());
        }
        tvEmitidas.setText("" + emitidos);
        tvNoEmitidas.setText("" + noEmitido);
        tvSaldoEmitido.setText("" + Config.nf.format(saldoEmitido));
        tvSaldoNoEmitido.setText("" + Config.nf.format(saldoNoEmitido));
        tvResultados.setText(totalFilas + " Resultados ");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);

        boolean argumentos = (getArguments()!=null);
        adapter = new GerenteReporteSucursalesAdapter(rootView.getContext(), getDatos1, recyclerView,(argumentos)?getArguments().getString(ARG_PARAM4):Dialogos.fechaActual(),(argumentos)?getArguments().getString(ARG_PARAM5):Dialogos.fechaSiguiente());
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
            JSONArray array = obj.getJSONArray("Sucursal");
            for(int i = 0; i < array.length(); i++){
                GerenteReporteSucursalesModel getDatos2 = new GerenteReporteSucursalesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
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
            Dialogos.dialogoErrorDatos(getContext());
        }
        adapter.notifyDataSetChanged();
        adapter.setLoaded();
    }

}
