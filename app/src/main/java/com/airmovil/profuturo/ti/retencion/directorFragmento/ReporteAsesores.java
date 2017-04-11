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
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
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

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsesoresAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.GerenteReporteAsesoresAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteAsesoresModel;
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
 * {@link ReporteAsesores.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteAsesores#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteAsesores extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "parametro1";
    private static final String ARG_PARAM2 = "parametro2";
    private static final String ARG_PARAM3 = "parametro3";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;

    // TODO: recycler
    private DirectorReporteAsesoresAdapter adapter;
    private List<GerenteReporteAsesoresModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private InputMethodManager imm;
    private Connected connected;

    // TODO: datas
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaMostrar = "";
    private String fechaFin = "";
    private int posicion;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    // TODO: Elementos XML
    private TextView tvFecha;
    private TextView tvEmitidas, tvNoEmitidas, tvSaldoEmitido, tvSaldoNoEmitido;
    private EditText etAsesor;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;
    private TextView tvTotalResultados;
    int filas;

    private OnFragmentInteractionListener mListener;

    public ReporteAsesores() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteAsesores.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteAsesores newInstance(String param1, String param2, String param3, Context context) {
        ReporteAsesores fragment = new ReporteAsesores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
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
        tvFecha = (TextView) rootView.findViewById(R.id.dfra_tv_fecha);
        tvEmitidas = (TextView) rootView.findViewById(R.id.dfra_tv_emitidas);
        tvNoEmitidas = (TextView) rootView.findViewById(R.id.dfra_tv_no_emitidas);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.dfra_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.dfra_tv_saldo_no_emitido);
        etAsesor = (EditText) rootView.findViewById(R.id.dfra_et_asesor);
        tvTotalResultados = (TextView) rootView.findViewById(R.id.dfra_tv_total_registros);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.dfra_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.dfra_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.dfra_btn_buscar);

        // TODO: ocultar teclado
        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        sessionManager = new SessionManager(getContext());
        connected = new Connected();

        fechas();

        rangoInicial();
        rangoFinal();

        // TODO: model
        getDatos1 = new ArrayList<>();

        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.dfra_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        sendJson(true);

        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    final String fechaIncial = tvRangoFecha1.getText().toString();
                    final String fechaFinal = tvRangoFecha2.getText().toString();
                    final String idAsesor = etAsesor.getText().toString();

                    if(fechaIncial.isEmpty() || fechaFinal.isEmpty()){
                        Config.msj(getContext(), getResources().getString(R.string.msj_error_fechas),getResources().getString(R.string.msj_error_fechas));
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsesores fragmento = ReporteAsesores.newInstance(fechaIncial, fechaFinal, idAsesor, rootView.getContext());
                        borrar.onDestroy();
                        ft.remove(borrar);
                        ft.replace(R.id.content_director, fragmento);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    // TODO: ocultar teclado
                    imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
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

        tvTotalResultados.setOnClickListener(new View.OnClickListener() {
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

                        final String datoEditText = editText.getText().toString();
                        //final String datoSpinner = spinner.getSelectedItem().toString();
                        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Service.INPUT_METHOD_SERVICE);
                        Connected connected = new Connected();
                        if(connected.estaConectado(getContext())){
                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                            Config.msjTime(getContext(), "Mensaje datos1", "Se está enviado los datos a " + datoEditText + "@profuturo.com", 8000);
                            dialog.dismiss();
                        }else{
                            Config.msj(getContext(), "Error conexión", "Por favor, revisa tu conexión a internet");
                            dialog.dismiss();
                        }
                    }
                });
                dialog.show();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.director_fragmento_reporte_asesores, container, false);
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

        JSONObject obj = new JSONObject();
        Map<String, Integer> fechaDatos = Config.dias();
        Map<String, String> fechaActual = Config.fechas(1);
        HashMap<String, String> usuario = sessionManager.getUserDetails();
        String numeroUsuario = usuario.get(SessionManager.ID);
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");
        String smParam1 = fechaActual.get("fechaIni");
        String smParam2 = fechaActual.get("fechaFin");

        try {
            if(getArguments() != null) {
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
                mParam3 = getArguments().getString(ARG_PARAM3);

                Map<String, String> fecha = Config.fechas(1);
                String param1 = fecha.get("fechaIni");
                String param2 = fecha.get("fechaFin");

                JSONObject rqt = new JSONObject();
                JSONObject periodo = new JSONObject();

                rqt.put("numeroEmpleadoAsesor", mParam3);
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", mParam1);
                periodo.put("fechaIni", mParam2);
                rqt.put("perido", periodo);
                rqt.put("usuario", numeroUsuario);
                obj.put("rqt", rqt);
            }else{
                JSONObject rqt = new JSONObject();
                rqt.put("numeroEmpleadoAsesor", "");
                rqt.put("pagina", pagina);
                JSONObject periodo = new JSONObject();
                periodo.put("fechaFin", smParam2);
                periodo.put("fechaIni", smParam1);
                rqt.put("perido", periodo);
                rqt.put("usuario", numeroUsuario);
                obj.put("rqt", rqt);
            }

            Log.d("ReporteSucursales", " RQT -->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_ASESORES, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
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
                        try{
                            loading.dismiss();
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
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj) {
        int emitidos = 0;
        int noEmitido = 0;
        int saldoEmitido = 0;
        int saldoNoEmitido = 0;
        int totalFilas = 1;

        try{
            JSONArray array = obj.getJSONArray("Asesor");
            JSONObject objEmitidos = obj.getJSONObject("retenido");
            emitidos = objEmitidos.getInt("retenido");
            noEmitido = objEmitidos.getInt("noRetenido");
            JSONObject objSaldo = obj.getJSONObject("saldo");
            saldoEmitido = objSaldo.getInt("saldoRetenido");
            saldoNoEmitido = objSaldo.getInt("saldoNoRetenido");
            totalFilas = 50;
            filas = obj.getInt("filasTotal");
            for(int i = 0; i < array.length(); i++){
                GerenteReporteAsesoresModel getDatos2 = new GerenteReporteAsesoresModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNumeroEmpleado(json.getInt("numeroEmpleado"));
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
            e.printStackTrace();
        }

        tvEmitidas.setText("" + emitidos);
        tvNoEmitidas.setText("" + noEmitido);
        tvSaldoEmitido.setText("" + saldoEmitido);
        tvSaldoNoEmitido.setText("" + saldoNoEmitido);
        tvTotalResultados.setText("" + filas + " Resultados ");

        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        adapter = new DirectorReporteAsesoresAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("onLoadMore", " pagina->" + pagina + "numeroMaximo" + numeroMaximoPaginas);
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
                        getDatos1.remove(getDatos1.size() - 1);
                        adapter.notifyItemRemoved(getDatos1.size());
                        //Load data
                        Log.d("EnvioIndex", getDatos1.size() + "");
                        pagina = Config.pidePagina(getDatos1);
                        sendJson(false);
                    }
                }, 5000);
            }
        });
    }

    private void segundoPaso(JSONObject obj) {
        Log.d("ReporteAsesor", "rqt ->" + obj);
        try{
            JSONArray array = obj.getJSONArray("Sucursal");
            for(int i = 0; i < array.length(); i++){
                GerenteReporteAsesoresModel getDatos2 = new GerenteReporteAsesoresModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNumeroEmpleado(json.getInt("numeroEmpleado"));

                    JSONObject cita = json.getJSONObject("cita");
                    getDatos2.setConCita(cita.getInt("conCita"));
                    getDatos2.setSinCita(cita.getInt("sinCita"));

                    JSONObject retenido = json.getJSONObject("retenido");
                    getDatos2.setEmitido(retenido.getInt("retenido"));
                    getDatos2.setNoEmitido(retenido.getInt("noRetenido"));

                    JSONObject saldo = json.getJSONObject("saldo");
                    getDatos2.setSaldoEmitido(saldo.getInt("saldoRetenido"));
                    getDatos2.setSaldoNoEmetido(saldo.getInt("saldoNoRetenido"));

                    Log.d("RESPONSE CITA", "" + cita);
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

    //<editor-fold desc="Rango inicial">
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
    //</editor-fold>

    //<editor-fold desc="Rango Final">
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
    //</editor-fold>

    private void fechas(){
        // TODO: fecha
        //<editor-fold desc="Fechas">
        Map<String, Integer> fechaDatos = Config.dias();
        Map<String, String> fechaActual = Config.fechas(1);
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");
        String smParam1 = fechaActual.get("fechaIni");
        String smParam2 = fechaActual.get("fechaFin");

        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);

            Map<String, String> fecha = Config.fechas(1);
            String param1 = fecha.get("fechaIni");
            String param2 = fecha.get("fechaFin");

            if(mParam1.isEmpty() && mParam2.isEmpty())
                tvFecha.setText(param1 + " - " + param2);
            else if(mParam1.isEmpty())
                tvFecha.setText(mParam2 + " " + mParam3);
            else if(mParam2.isEmpty())
                tvFecha.setText(mParam1 + " " + mParam3);
            else
                tvFecha.setText(mParam1 + " - " + mParam2);

        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
        //</editor-fold>
    }
}
