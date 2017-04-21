package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
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
import android.text.InputType;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.AsesorReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.CitasClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.AsesorReporteClientesModel;
import com.airmovil.profuturo.ti.retencion.model.CitasClientesModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReporteClientes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteClientes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteClientes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = ReporteClientes.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    // TODO: XML
    private TextView tvFecha;
    private TextView tvEmitidos, tvNoEmitidos, tvSaldoEmitido, tvSaldoNoEmitido;
    private Spinner spinnerIds, spinnerEmitidos;
    private EditText etIngresar;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;
    private InputMethodManager imm;
    private TextView tvRegistros;

    // TODO: variable
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private String numeroUsuario;

    // TODO: list
    private List<AsesorReporteClientesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private AsesorReporteClientesAdapter adapter;

    private int filas;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;

    private OnFragmentInteractionListener mListener;

    // TODO: Params
    private int sParam1; // ids
    private String sParam2; // Id dato
    private String sParam3; // fecha Inicio
    private String sParam4; // fecha fin
    private int sParam5; // Emitidos
    // TODO: parametros en argumentos
    private static final String ARG_PARAM_1 = "param1";
    private static final String ARG_PARAM_2 = "param2";
    private static final String ARG_PARAM_3 = "param3";
    private static final String ARG_PARAM_4 = "param4";
    private static final String ARG_PARAM_5 = "param5";



    public ReporteClientes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteClientes.
     */
    // TODO: Rename and change types and number of parameters
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

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        primeraPeticion();
        variables();
        fechas();

        // TODO: Spinner
        ArrayAdapter<String> adapterIds = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
        adapterIds.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerIds.setAdapter(adapterIds);

        // TODO: Spinner
        ArrayAdapter<String> adapterEmitidos = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.EMITIDOS);
        adapterEmitidos.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerEmitidos.setAdapter(adapterEmitidos);

        // TODO: model
        getDatos1 = new ArrayList<>();
        // TODO Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.afrc_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // TODO: webservice
        //sendJson(true);

        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sParam1 = spinnerIds.getSelectedItemPosition();
                sParam2 = etIngresar.getText().toString();
                sParam3 = tvRangoFecha1.getText().toString();
                sParam4 = tvRangoFecha2.getText().toString();
                sParam5 = spinnerEmitidos.getSelectedItemPosition();
                if (sParam1 == 0 || sParam2.isEmpty() || sParam5 == 0) {
                    Config.dialogoDatosVacios(getContext());
                }
                else if(sParam3.isEmpty() || sParam4.isEmpty()){
                    Config.dialogoFechasVacias(getContext());
                }
                else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ReporteClientes procesoDatosFiltroInicio = ReporteClientes.newInstance(sParam1, sParam2, sParam3, sParam4, sParam5,rootView.getContext());
                    borrar.onDestroy();
                    ft.remove(borrar);
                    ft.replace(R.id.content_asesor, procesoDatosFiltroInicio);
                    ft.addToBackStack(null);
                    ft.commit();
                    Config.teclado(getContext(), etIngresar);
                }
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_reporte_clientes, container, false);
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

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
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
    }

    private void fechas(){
        // TODO: fecha
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        if(getArguments() != null){
            sParam3 = getArguments().getString(ARG_PARAM_3);
            sParam4 = getArguments().getString(ARG_PARAM_4);
            tvFecha.setText(sParam3+" - "+sParam4);
        }else {
            Map<String, String> fechas = Config.fechas(1);
            fechaFin = fechas.get("fechaFin");
            fechaIni = fechas.get("fechaIni");
            fechaMostrar = fechaIni;
            tvFecha.setText(fechaMostrar);
        }

        // TODO: fechas dialog
        rangoInicial();
        rangoFinal();
    }

    private void rangoInicial(){
        tvRangoFecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvRangoFecha1.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void rangoFinal(){
        tvRangoFecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvRangoFecha2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {

        JSONObject obj = new JSONObject();

        try{
            JSONObject rqt = new JSONObject();
            JSONObject filtros = new JSONObject();
            JSONObject periodo = new JSONObject();

            if(getArguments() != null){
                sParam1 = getArguments().getInt(ARG_PARAM_1);
                sParam2 = getArguments().getString(ARG_PARAM_2);
                sParam3 = getArguments().getString(ARG_PARAM_3);
                sParam4 = getArguments().getString(ARG_PARAM_4);
                sParam5 = getArguments().getInt(ARG_PARAM_5);

                Log.d("Argumentos", "fecha 1" + sParam1);
                Log.d("Argumentos", "fecha 2" + sParam2);

                switch (sParam1){
                    case 1:
                        filtros.put("curp", "");
                        filtros.put("nss", "");
                        filtros.put("numeroCuenta", sParam2);
                        break;
                    case 2:
                        filtros.put("curp", "");
                        filtros.put("nss", sParam2);
                        filtros.put("numeroCuenta", "");
                        break;
                    case 3:
                        filtros.put("curp", sParam2);
                        filtros.put("nss", "");
                        filtros.put("numeroCuenta", "");
                        break;
                }
                rqt.put("filtro", filtros);
                rqt.put("filtroRetenido", sParam5);
                rqt.put("pagina", pagina);
                periodo.put("fechaInicio", sParam3);
                periodo.put("fechaFin", sParam4);
                rqt.put("periodo", periodo);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }else{
                filtros.put("curp", "");
                filtros.put("nss", "");
                filtros.put("numeroCuenta", "");
                rqt.put("filtro", filtros);
                rqt.put("filtroRetenido", 0);
                rqt.put("pagina", pagina);
                periodo.put("fechaInicio", fechaIni);
                periodo.put("fechaFin", fechaFin);
                rqt.put("periodo", periodo);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d(TAG, "RQT-->" + obj);
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GENERAR_REPORTE_CLIENTE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            // loading.dismiss();
                            primerPaso(response);
                        } else {
                            segundoPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            // loading.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Connected connected = new Connected();
                        if(connected.estaConectado(getContext())){
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error");
                            dlgAlert.setMessage("Se ha encontrado un problema, deseas volver intentarlo");
                            dlgAlert.setCancelable(true);
                            dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendJson(true);
                                }
                            });
                            dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dlgAlert.create().show();
                        }else{
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error de conexión");
                            dlgAlert.setMessage("Se ha encontrado un problema, debes revisar tu conexión a internet");
                            dlgAlert.setCancelable(true);
                            dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //sendJson(true);
                                }
                            });
                            dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dlgAlert.create().show();
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
        Log.d(TAG + "-->", obj.toString());
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
            Log.d(TAG, " response ->" + totalFilas);
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvEmitidos.setText("" + retenido);
        tvNoEmitidos.setText("" + noRetenido);
        tvSaldoEmitido.setText("" + Config.nf.format(saldoAfavor));
        tvSaldoNoEmitido.setText("" + Config.nf.format(saldoNoRetenido));

        tvRegistros.setText("" + totalFilas + " Registros");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);

        if(getArguments() != null){
            sParam3 = getArguments().getString(ARG_PARAM_3);
            sParam4 = getArguments().getString(ARG_PARAM_4);
            adapter = new AsesorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView, sParam3, sParam4);
        }else{
            adapter = new AsesorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView, fechaIni, fechaFin);
        }
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "pagina->" + pagina + "numeroMaximo" + numeroMaximoPaginas);
                if (pagina >= numeroMaximoPaginas) {
                    Log.d("FINALIZA", "termino proceso");
                    return;
                }
                Log.e("haint", "Load More");
                getDatos1.add(null);
                adapter.notifyItemInserted(getDatos1.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        //Remove loading item
                        Log.d("handler", "pagina " +  pagina);
                        getDatos1.remove(getDatos1.size() - 1);
                        adapter.notifyItemRemoved(getDatos1.size());
                        //Load data
                        Log.d("EnvioIndex", getDatos1.size() + "");
                        pagina = Config.pidePagina(getDatos1);
                        Log.d("Dato pagina 2", " - > " + pagina);
                        sendJson(false);
                    }
                }, 5000);
            }
        });

    }

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
