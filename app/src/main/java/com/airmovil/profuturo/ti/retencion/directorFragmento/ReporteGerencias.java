package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteGerenciasAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteGerenciasModel;
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

public class ReporteGerencias extends Fragment implements Spinner.OnItemSelectedListener{
    public static final String TAG = ReporteGerencias.class.getSimpleName();
    private static final String ARG_PARAM1 = "fechaIncio";
    private static final String ARG_PARAM2 = "fechaFin";
    private static final String ARG_PARAM3 = "idGerencia";
    private String mParam1;
    private String mParam2;
    private int mParam3;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private int idGerencia;
    private TextView tvFecha, tvEntidaes, tvNoEntidades, tvSaldoEmitido, tvSaldoNoEmitido, tvRangoFecha1, tvRangoFecha2, tvResultados, tvMail;
    private Spinner spinnerGerencias;
    private Button btnBuscar;
    private List<DirectorReporteGerenciasModel> getDato1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private DirectorReporteGerenciasAdapter adapter;
    private Fragment borrar = this;
    private ArrayList<String> gerencias;
    private ArrayList<String> id_gerencias;
    private Connected connected;
    private String linea = "\n __________________ \n";
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    // TODO: Variables para colocar del servicio
    private int totalFilas = 1;
    private int totalEntidades = 0;
    private int totalNoEntidades = 0;
    private int totalSaldosEmitodos= 0;
    private int totalSaldoNoEmitidos = 0;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    public ReporteGerencias() {/* Required empty public constructor */}

    /**
     * Este fragmento usa lo previsto de los paramentros enviados
     * @param param1 Parameter de fecha inicial
     * @param param2 Parameter de fecha final
     * @param param3 paramatro de ID gerencia
     * @return una nueva instancia del fragmento ReporteGerencias.
     */
    public static ReporteGerencias newInstance(String param1, String param2, int param3, Context context) {
        ReporteGerencias fragment = new ReporteGerencias();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            idGerencia = getArguments().getInt(ARG_PARAM3);
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
                } else {
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

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Casteo
        variables();
        // TODO: dialog fechas
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        argumentos();
        sendJson(true);

        getDato1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.dfrg_rv_gerencias);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    mParam1 = tvRangoFecha1.getText().toString();
                    mParam2 = tvRangoFecha2.getText().toString();
                    if(mParam1.isEmpty() || mParam2.isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteGerencias fragmento = ReporteGerencias.newInstance(mParam1, mParam2, idGerencia, rootView.getContext());
                        borrar.onDestroy();ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        boolean argumentos = (getArguments() != null);
        ServicioEmailJSON.enviarEmailReporteGerencias(getContext(), tvResultados,
                (argumentos==true) ? true : false, (argumentos==true) ? mParam3 : 0,
                (argumentos==true) ? mParam1 : Dialogos.fechaActual(), (argumentos==true) ? mParam2 : Dialogos.fechaSiguiente());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.director_fragmento_reporte_gerencias, container, false);
    }

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

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
            case R.id.dfrg_spinner_gerencias:
                String sim = id_gerencias.get(position);
                idGerencia = Integer.valueOf(sim);
                break;
            default:
                break;
        }
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

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
                    Fragment fragmentoGenerico = new Inicio();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_director, fragmentoGenerico).commit();
                    return true;
                }
                return false;
            }
        });
    }

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void variables(){
        connected = new Connected();
        gerencias = new ArrayList<String>();
        id_gerencias = new ArrayList<String>();
        tvFecha = (TextView) rootView.findViewById(R.id.dfrg_tv_fecha);
        tvEntidaes = (TextView) rootView.findViewById(R.id.dfrg_tv_entidades);
        tvNoEntidades = (TextView) rootView.findViewById(R.id.dfrg_tv_no_entidades);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.dfrg_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.dfrg_tv_saldo_no_emitido);
        spinnerGerencias = (Spinner) rootView.findViewById(R.id.dfrg_spinner_gerencias);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.dfrg_tv_fecha_inicio);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.dfrg_tv_fecha_final);
        btnBuscar = (Button) rootView.findViewById(R.id.dfrg_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.dfrg_tv_registros);
        tvMail = (TextView) rootView.findViewById(R.id.dfrg_tv_mail);
        spinnerGerencias.setOnItemSelectedListener(this);
        getData();
    }

    private void getData(){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GERENCIAS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        JSONArray j = null;
                        try {
                            j = response.getJSONArray("Gerencias");
                            getSucursales(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {}
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    private void getSucursales(JSONArray j){
        gerencias.add("Selecciona una Gerencia");
        id_gerencias.add("0");
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                gerencias.add(json.getString("nombre"));
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
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, gerencias);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGerencias.setAdapter(adapter);
        spinnerGerencias.setSelection(position);
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
        boolean argumentos = (getArguments()!=null);
        try {
            rqt.put("idGerencia", (argumentos == true) ? mParam3: 0);
            rqt.put("idSucursal", 0);
            rqt.put("pagina", pagina);
            periodo.put("fechaFin", (argumentos) ? mParam1 : Dialogos.fechaSiguiente());
            periodo.put("fechaInicio", (argumentos) ? mParam2 : Dialogos.fechaActual());
            rqt.put("perido", periodo);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, " <-RQT->" + linea + obj + linea);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }

        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_REPORTE_RETENCION_GERENCIAS, obj);
    }

    private void primerPaso(JSONObject obj) {
        try{
            // TODO: jsonArray de gerencias
            JSONArray array = obj.getJSONArray("Gerencia");
            // TODO: filas totales y mensaje de respuesta
            totalFilas = obj.getInt("filasTotal");
            // TODO: Cantidades generales de retenidos y saldos
            JSONObject jsonObjectRetenido = obj.getJSONObject("retenido");
                totalEntidades = jsonObjectRetenido.getInt("retenido");
                totalNoEntidades = jsonObjectRetenido.getInt("noRetenido");
            JSONObject jsonObjectSaldos = obj.getJSONObject("saldo");
                totalSaldosEmitodos= jsonObjectSaldos.getInt("saldoRetenido");
                totalSaldoNoEmitidos = jsonObjectSaldos.getInt("saldoNoRetenido");
            Log.d("filas json", "Total de filas " + totalFilas);
            for(int i = 0; i < array.length(); i++){
                DirectorReporteGerenciasModel getDatos2 = new DirectorReporteGerenciasModel();
                JSONObject json = null;
                try{json = array.getJSONObject(i);
                    getDatos2.setIdGerencia(json.getInt("idGerencia"));
                    JSONObject citas = json.getJSONObject("cita");
                    getDatos2.setConCita(citas.getInt("conCita"));
                    getDatos2.setSinCita(citas.getInt("sinCita"));
                    JSONObject saldo = json.getJSONObject("saldo");
                    getDatos2.setdSaldoRetenido(saldo.getInt("saldoRetenido"));
                    getDatos2.setdSaldoNoRetenido(saldo.getInt("saldoNoRetenido"));
                    JSONObject retenidoss = json.getJSONObject("retenido");
                    getDatos2.setEmitidas(retenidoss.getInt("retenido"));
                    getDatos2.setNoEmitidas(retenidoss.getInt("noRetenido"));
                }catch (JSONException e){
                    Dialogos.dialogoErrorServicio(getContext());
                }
                getDato1.add(getDatos2);
            }
        }catch (JSONException e){
            Dialogos.dialogoErrorServicio(getContext());
        }
        tvResultados.setText("" + totalFilas + " Resultados");
        tvEntidaes.setText("" + totalEntidades);
        tvNoEntidades.setText("" + totalNoEntidades);
        tvSaldoEmitido.setText("" + Config.nf.format(totalSaldosEmitodos));
        tvSaldoNoEmitido.setText("" + Config.nf.format(totalSaldoNoEmitidos));
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        boolean argumentos = (getArguments()!=null);
        adapter = new DirectorReporteGerenciasAdapter(rootView.getContext(), getDato1, recyclerView, (argumentos == true) ? mParam1 : Dialogos.fechaActual(), (argumentos == true) ? mParam2 : Dialogos.fechaSiguiente() );
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        adapter.notifyDataSetChanged();
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pagina >= numeroMaximoPaginas) {
                    return;
                }
                getDato1.add(null);
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                    adapter.notifyItemInserted(getDato1.size() - 1);
                   }
                };
                handler.post(r);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDato1.remove(getDato1.size() - 1);
                        adapter.notifyItemRemoved(getDato1.size());
                        pagina = Config.pidePagina(getDato1);
                        sendJson(false);
                    }
                }, Config.TIME_HANDLER);
            }
        });
    }

    private void segundoPaso(JSONObject obj) {
        try{
            // TODO: jsonArray de gerencias
            JSONArray array = obj.getJSONArray("Gerencia");
            for(int i = 0; i < array.length(); i++){
                DirectorReporteGerenciasModel getDatos2 = new DirectorReporteGerenciasModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setIdGerencia(json.getInt("idGerencia"));
                    JSONObject citas = json.getJSONObject("cita");
                    getDatos2.setConCita(citas.getInt("conCita"));
                    getDatos2.setSinCita(citas.getInt("sinCita"));
                    JSONObject saldo = json.getJSONObject("saldo");
                    getDatos2.setdSaldoRetenido(saldo.getInt("saldoRetenido"));
                    getDatos2.setdSaldoNoRetenido(saldo.getInt("saldoNoRetenido"));
                    JSONObject retenidoss = json.getJSONObject("retenido");
                    getDatos2.setEmitidas(retenidoss.getInt("retenido"));
                    getDatos2.setNoEmitidas(retenidoss.getInt("noRetenido"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDato1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter.notifyDataSetChanged();
        adapter.setLoaded();
    }
    private void argumentos(){
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getInt(ARG_PARAM3);
            tvRangoFecha1.setText(mParam1);
            tvRangoFecha2.setText(mParam2);
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }
}