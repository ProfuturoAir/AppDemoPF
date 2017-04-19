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
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteGerenciasAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Firma;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
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
    private static final String ARG_PARAM3 = "parametro3";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private int mParam3;

    private OnFragmentInteractionListener mListener;

    private View rootView;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    private int idGerencia;

    private TextView tvFecha, tvEntidaes, tvNoEntidades, tvSaldoEmitido, tvSaldoNoEmitido;
    private Spinner spinnerGerencias;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;
    private TextView tvResultados, tvMail;

    private List<DirectorReporteGerenciasModel> getDato1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private DirectorReporteGerenciasAdapter adapter;

    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private InputMethodManager imm;
    final Fragment borrar = this;

    private HashMap<String, String> usuario;

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
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        // TODO: Casteo
        variables();

        // TODO: ocultar teclado
        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);

        sessionManager = new SessionManager(getContext());
        // TODO: dialog fechas
        rangoInicial();
        rangoFinal();

        fechas();


        // TODO: Spinner
        ArrayAdapter<String> adapterGerencias = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.GERENCIAS);
        adapterGerencias.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGerencias.setAdapter(adapterGerencias);


        primeraPeticion();
        //<editor-fold desc="RecyclerView">
        getDato1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.dfrg_rv_gerencias);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        //</editor-fold>

        //<editor-fold desc="Boton buscar contenido">
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    final String fechaIncial = tvRangoFecha1.getText().toString();
                    final String fechaFinal = tvRangoFecha2.getText().toString();
                    idGerencia = spinnerGerencias.getSelectedItemPosition();
                    if(fechaIncial.isEmpty() || fechaFinal.isEmpty() || idGerencia == 0){
                        Config.dialogoDatosVacios(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteGerencias fragmento = ReporteGerencias.newInstance(fechaIncial,fechaFinal, idGerencia, rootView.getContext());
                        borrar.onDestroy();
                        ft.remove(borrar);
                        ft.replace(R.id.content_director, fragmento);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });
        //</editor-fold>

        //<editor-fold desc="TextView Resultados de filas">
        tvResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_layout);

                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);

                // TODO: Spinner
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);



                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);

                        Connected connected = new Connected();
                        if(connected.estaConectado(getContext())){
                            final String datoEditText = editText.getText().toString();
                            //final String datoSpinner = spinner.getSelectedItem().toString();
                            dialog.dismiss();

                            InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            Config.msjTime(getContext(), "Mensaje datos", "Se está enviado los datos a " + datoEditText + "@profuturo.com", 8000);
                        }else{
                            Config.msj(getContext(), "Error conexión", "Por favor, revisa tu conexión a internet");
                            dialog.dismiss();
                        }


                    }
                });
                dialog.show();
            }
        });
        //</editor-fold>
    }



    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
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

    private void variables(){
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
    }

    public void primeraPeticion(){
        // TODO: Peticion via REST
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
                }, 3000);
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        Map<String, Integer> fechaDatos = Config.dias();
        Map<String, String> fechaActual = Config.fechas(1);
        String smParam1 = fechaActual.get("fechaIni");
        String smParam2 = fechaActual.get("fechaFin");

        Map<String, String> fecha = Config.fechas(1);
        String param1 = fecha.get("fechaIni");
        String param2 = fecha.get("fechaFin");

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String,String> datosUsuario = sessionManager.getUserDetails();
        String idUsuario = datosUsuario.get(SessionManager.USER_ID);

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();

        try {
            if(getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
                mParam3 = getArguments().getInt(ARG_PARAM3);
                rqt.put("idGerencia", mParam3);
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", mParam1);
                periodo.put("fechaIni", mParam2);
                rqt.put("idGerencia", mParam3);
                rqt.put("perido", periodo);
                rqt.put("usuario", idUsuario);
                obj.put("rqt", rqt);
            }else{
                rqt.put("idGerencia", 0);
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", smParam2);
                periodo.put("fechaIni", smParam1);
                rqt.put("perido", periodo);
                rqt.put("usuario", idUsuario);
                obj.put("rqt", rqt);
                tvFecha.setText(smParam1 + " - " + smParam2);
            }

            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un right_in al formar los datos.");
        }
        //<editor-fold desc="jsonArrayRequest">
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_GERENCIAS, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
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
                        try{
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
                                    //sendJson(true);
                                }
                            });
                            dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dlgAlert.create().show();
                        }else{
                            Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                        }
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
        //</editor-fold>
    }

    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "RESPONSE Reporte Gerencias -->" + obj.toString());
        int idGerencia = 0;
        // TODO: total de filas
        int filas = 0;
        int totalFilas = 1;
        // TODO: Datos de cantidades generales retenidos
        int totalEntidades = 0;
        int totalNoEntidades = 0;
        // TODO: Datos de cantidades generales de saldos
        int totalSaldosEmitodos= 0;
        int totalSaldoNoEmitidos = 0;
        // TODO: Mensaje de estatus
        String status = "";
        // TODO: datos de lista
        int conCita = 0;
        int sinCita = 0;
        int retenido = 0;
        int noRetenido = 0;
        int saldoRetenido = 0;
        int saldoNoRetenido = 0;
        try{
            // TODO: jsonArray de gerencias
            JSONArray array = obj.getJSONArray("Gerencia");
            // TODO: filas totales y mensaje de respuesta
            totalFilas = obj.getInt("filasTotal");
            status = obj.getString("status");
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
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDato1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvResultados.setText("" + totalFilas + " Resultados");
        tvEntidaes.setText("" + totalEntidades);
        tvNoEntidades.setText("" + totalNoEntidades);
        tvSaldoEmitido.setText("" + Config.nf.format(totalSaldosEmitodos));
        tvSaldoNoEmitido.setText("" + Config.nf.format(totalSaldoNoEmitidos));

        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        Log.d("numeroMaximoPaginas", ""+numeroMaximoPaginas);


        Map<String, String> datos = new HashMap<String, String>();
        datos.put("fechaInicio", fechaIni);

        if(getArguments() != null){
            final String mParam1 = getArguments().getString(ARG_PARAM1);
            final String mParam2 = getArguments().getString(ARG_PARAM2);
            adapter = new DirectorReporteGerenciasAdapter(rootView.getContext(), getDato1, recyclerView, mParam1, mParam2);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }else{
            Map<String, String> fecha = Config.fechas(1);
            String mParam1 = fecha.get("fechaIni");
            String mParam2 = fecha.get("fechaFin");
            adapter = new DirectorReporteGerenciasAdapter(rootView.getContext(), getDato1, recyclerView, mParam1, mParam2);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }

        adapter.notifyDataSetChanged();
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d(TAG, "pagina->" + pagina + "numeroMaximo" + numeroMaximoPaginas);
                if (pagina >= numeroMaximoPaginas) {
                    Log.d("FINALIZA", "termino proceso");
                    return;
                }
                Log.e("haint", "Load More");
                getDato1.add(null);
                adapter.notifyItemInserted(getDato1.size() - 1);

                //Load more data for reyclerview
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        Log.e("haint", "Load More 2");
                        //Remove loading item
                        getDato1.remove(getDato1.size() - 1);
                        adapter.notifyItemRemoved(getDato1.size());
                        //Load data
                        Log.d("EnvioIndex", getDato1.size() + "");
                        pagina = Config.pidePagina(getDato1);
                        sendJson(false);
                    }
                }, 5000);
            }
        });

    }

    private void segundoPaso(JSONObject obj) {
        Log.d("segundoPaso", obj.toString());
        try{
            // TODO: jsonArray de gerencias
            JSONArray array = obj.getJSONArray("Gerencia");
            // TODO: filas totales y mensaje de respuesta
            // TODO: Cantidades generales de retenidos y saldos
            JSONObject jsonObjectRetenido = obj.getJSONObject("retenido");
            JSONObject jsonObjectSaldos = obj.getJSONObject("saldo");
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

    private void fechas(){
        Map<String, Integer> fechaDatos = Config.dias();
        Map<String, String> fechaActual = Config.fechas(1);
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");
        String smParam1 = fechaActual.get("fechaIni");
        String smParam2 = fechaActual.get("fechaFin");
        mParam3 = 0;
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getInt(ARG_PARAM3);
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
    }
}
