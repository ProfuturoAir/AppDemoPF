package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.Adapter.CitasClientesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteGerenciasAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.*;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.CitasClientesModel;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReporteGerencias.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteGerencias#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteGerencias extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = ReporteGerencias.class.getSimpleName();
    private static final String ARG_PARAM1 = "parametro1";
    private static final String ARG_PARAM2 = "parametro2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";

    private TextView tvFecha, tvEntidaes, tvNoEntidades, tvSaldoEmitido, tvSaldoNoEmitido;
    private Spinner spinnerGerencias;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;

    private List<DirectorReporteGerenciasModel> getDato1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private DirectorReporteGerenciasAdapter adapter;

    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;

    public ReporteGerencias() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteGerencias.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteGerencias newInstance(String param1, String param2, Context context) {
        ReporteGerencias fragment = new ReporteGerencias();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
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
        // TODO: Casteo
        //<editor-fold desc="Casteo">
        tvFecha = (TextView) rootView.findViewById(R.id.dfrg_tv_fecha);
        tvEntidaes = (TextView) rootView.findViewById(R.id.dfrg_tv_entidades);
        tvNoEntidades = (TextView) rootView.findViewById(R.id.dfrg_tv_no_entidades);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.dfrg_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.dfrg_tv_saldo_no_emitido);
        spinnerGerencias = (Spinner) rootView.findViewById(R.id.dfrg_spinner_gerencias);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.dfrg_tv_fecha_inicio);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.dfrg_tv_fecha_final);
        btnBuscar = (Button) rootView.findViewById(R.id.dfrg_btn_buscar);
        //</editor-fold>

        // TODO: dialog fechas
        rangoInicial();
        rangoFinal();


        // TODO: fecha
        //<editor-fold desc="Fechas">
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        if(getArguments() != null){
            fechaIni = getArguments().getString(ARG_PARAM1).trim();
            fechaFin = getArguments().getString(ARG_PARAM2).trim();
            if(fechaFin.equals("") && fechaIni.equals("")){
                Map<String, String> fechas = Config.fechas(1);
                fechaIni = fechas.get("fechaIni");
                fechaMostrar = fechaIni;
                tvFecha.setText(fechaMostrar);
            }else if(fechaFin.equals("")){
                tvFecha.setText(fechaIni);
            }else if(fechaIni.matches("")){
                tvFecha.setText(fechaFin);
            }else{
                tvFecha.setText(fechaIni + " - " + fechaFin);
            }
        }else {
            Map<String, String> fechas = Config.fechas(1);
            fechaFin = fechas.get("fechaFin");
            fechaIni = fechas.get("fechaIni");
            fechaMostrar = fechaIni;
            tvFecha.setText(fechaMostrar);
        }
        //</editor-fold>

        // TODO: Spinner
        //<editor-fold desc="Spinner de gerencias">
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.GERENCIAS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGerencias.setAdapter(adapter);
        //</editor-fold>

        sendJson(true);
        // TODO: modelo
        getDato1 = new ArrayList<>();
        // TODO: Recycler
        //<editor-fold desc="RecyclerView">
        recyclerView = (RecyclerView) rootView.findViewById(R.id.dfrg_rv_gerencias);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        //</editor-fold>
        final Fragment borrar = this;
        //<editor-fold desc="Boton buscar contenido">
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                btnBuscar.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {

                        final String fechaIncial = tvRangoFecha1.getText().toString();
                        final String fechaFinal = tvRangoFecha2.getText().toString();

                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteGerencias procesoDatosFiltroInicio = ReporteGerencias.newInstance(
                                (fechaIni.equals("") ? "" : fechaIni),
                                (fechaFin.equals("") ? "" : fechaFin),
                                rootView.getContext()
                        );
                        borrar.onDestroy();
                        ft.remove(borrar);
                        ft.replace(R.id.content_director, procesoDatosFiltroInicio);
                        ft.addToBackStack(null);
                    }
                });
            }
        });
        //</editor-fold>
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.director_fragmento_reporte_gerencias, container, false);
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

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {

        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);
        else
            loading = null;


        JSONObject jsonobject = new JSONObject();
        JSONObject obj = new JSONObject();
        /*try {
            // TODO: Formacion del JSON request

            JSONObject rqt = new JSONObject();
            rqt.put("estatusCita", "1");
            rqt.put("pagina", pagina);
            rqt.put("usuario", "USUARIO");
            obj.put("rqt", rqt);
            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }*/
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_GERENCIAS, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            primerPaso(response);
                        } else {
                            //segundoPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "RESPONSE Reporte Gerencias -->" + obj.toString());
        int idGerencia = 0;
        // TODO: total de filas
        int filas = 0;
        // TODO: Datos de cantidades generales retenidos
        int totalEntidades = 0;
        int totalNoEntidades = 0;
        // TODO: Datos de cantidades generales de saldos
        String totalSaldosEmitodos= "";
        String totalSaldoNoEmitidos = "";
        // TODO: Mensaje de estatus
        String status = "";
        // TODO: datos de lista
        int conCita = 0;
        int sinCita = 0;
        int retenido = 0;
        int noRetenido = 0;
        String saldoRetenido = "";
        String saldoNoRetenido = "";
        try{
            // TODO: jsonArray de gerencias
            JSONArray array = obj.getJSONArray("Gerencia");
            // TODO: filas totales y mensaje de respuesta
            filas = obj.getInt("filasTotal");
            status = obj.getString("status");
            // TODO: Cantidades generales de retenidos y saldos
            JSONObject jsonObjectRetenido = obj.getJSONObject("retenido");
                totalEntidades = jsonObjectRetenido.getInt("retenido");
                totalNoEntidades = jsonObjectRetenido.getInt("noRetenido");
            JSONObject jsonObjectSaldos = obj.getJSONObject("saldo");
                totalSaldosEmitodos= jsonObjectSaldos.getString("saldoRetenido");
                totalSaldoNoEmitidos = jsonObjectSaldos.getString("saldoNoRetenido");
            Log.d(TAG, "\nResponse retenidos -->" + totalEntidades + " --> No Retenidos" + totalNoEntidades);
            Log.d(TAG, "\nResponse saldoRetenido -->" + totalSaldosEmitodos+ " --> SaldoNoRetenido" + totalSaldoNoEmitidos);
            Log.d(TAG, "|nResponse status ->" + status);
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
                    getDatos2.setSaldoRetenido(saldo.getString("saldoRetenido"));
                    getDatos2.setSaldoNoRetenido(saldo.getString("saldoNoRetenido"));
                    JSONObject retenidoss = json.getJSONObject("retenido");
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDato1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvEntidaes.setText("" + totalEntidades);
        tvNoEntidades.setText("" + totalNoEntidades);
        tvSaldoEmitido.setText("" + totalSaldosEmitodos);
        tvSaldoNoEmitido.setText("" + totalSaldoNoEmitidos);

        adapter = new DirectorReporteGerenciasAdapter(rootView.getContext(), getDato1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
}
