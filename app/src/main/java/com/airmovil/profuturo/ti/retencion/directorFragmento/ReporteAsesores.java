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
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteSucursalesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.GerenteReporteAsesoresAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
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
    private static final String ARG_PARAM1 = "parametro1"; // fecha Inicio
    private static final String ARG_PARAM2 = "parametro2"; // fecha final
    private static final String ARG_PARAM3 = "parametro3"; // numero asesor
    private static final String ARG_PARAM4 = "parametro4"; // id gerencia
    private static final String ARG_PARAM5 = "parametro5"; // id sucursal
    // TODO: Rename and change types of parameters
    private String mParam1; // fecha inicio
    private String mParam2; // fecha fin
    private String mParam3; // id asesor
    private int mParam4; // id gerencia
    private int mParam5; // id sucursal

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
    private TextView tvResultados;
    int filas;
    final Fragment borrar = this;

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
    public static ReporteAsesores newInstance(String param1, String param2, String param3, int param4, int param5, Context context) {
        ReporteAsesores fragment = new ReporteAsesores();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putInt(ARG_PARAM5, param5);
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

        tvFecha = (TextView) rootView.findViewById(R.id.dfra_tv_fecha);
        tvEmitidas = (TextView) rootView.findViewById(R.id.dfra_tv_emitidas);
        tvNoEmitidas = (TextView) rootView.findViewById(R.id.dfra_tv_no_emitidas);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.dfra_tv_saldo_emitido);
        tvSaldoNoEmitido = (TextView) rootView.findViewById(R.id.dfra_tv_saldo_no_emitido);
        etAsesor = (EditText) rootView.findViewById(R.id.dfra_et_asesor);
        tvResultados = (TextView) rootView.findViewById(R.id.dfra_tv_total_registros);
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

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    final String fechaIncial = tvRangoFecha1.getText().toString();
                    final String fechaFinal = tvRangoFecha2.getText().toString();
                    final String idAsesor = etAsesor.getText().toString();

                    if(fechaIncial.isEmpty() || fechaFinal.isEmpty() || idAsesor.isEmpty()){
                        Config.dialogoDatosVacios(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsesores fragmento = ReporteAsesores.newInstance(fechaIncial, fechaFinal, idAsesor, 0, 0, rootView.getContext());
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

        tvResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Dialog dialog = new Dialog(getContext());
                dialog.setContentView(R.layout.custom_layout);

                Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);

                // TODO: Spinner
                ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(getContext(), R.layout.spinner_item_azul, Config.EMAIL);
                adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                spinner.setAdapter(adapterSucursal);

                btn.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);

                        final String datoEditText = editText.getText().toString();
                        final String datoSpinner = spinner.getSelectedItem().toString();

                        Log.d("DATOS USER","SPINNER: "+datoEditText+" datosSpinner: "+ datoSpinner);
                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(getContext(), "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                            if(connected.estaConectado(getContext())){
                                JSONObject obj = new JSONObject();
                                boolean checa = true;
                                if (getArguments() != null){
                                    mParam3 = getArguments().getString(ARG_PARAM3);
                                    checa = true;
                                }else{
                                    mParam3 = "";
                                    checa = false;
                                }

                                try {
                                    JSONObject rqt = new JSONObject();
                                    rqt.put("correo", email);
                                    rqt.put("detalle", checa);
                                    rqt.put("idEmpleado", mParam3);
                                    JSONObject periodo = new JSONObject();
                                    periodo.put("fechaFin", fechaFin);
                                    periodo.put("fechaInicio", fechaIni);
                                    rqt.put("periodo", periodo);
                                    rqt.put("usuario", Config.usuarioCusp(getContext()));
                                    obj.put("rqt", rqt);
                                    Log.d("datos", "REQUEST-->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(getContext(), "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_SUCURSAL,getContext(),new EnviaMail.VolleyCallback() {

                                    @Override
                                    public void onSuccess(JSONObject result) {
                                        Log.d("RESPUESTA SUCURSAL", result.toString());
                                        int status;

                                        try {
                                            status = result.getInt("status");
                                        }catch(JSONException error){
                                            status = 400;
                                        }

                                        Log.d("EST","EE: "+status);
                                        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                        if(status == 200) {
                                            Config.msj(getContext(), "Enviando", "Se ha enviado el mensaje al destino");
                                            //Config.msjTime(getContext(), "Enviando", "Se ha enviado el mensaje al destino", 4000);
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(getContext(), "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                        //db.addUserCredits(fk_id_usuario,result);
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Log.d("RESPUESTA ERROR", result);
                                        Config.msj(getContext(), "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                        //db.addUserCredits(fk_id_usuario, "ND");
                                    }
                                });
                            }else{
                                Config.msj(getContext(), "Error en conexión", "Por favor, revisa tu conexión a internet");
                            }
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
                }, 3000);
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();

        Map<String, Integer> fechaDatos = Config.dias();
        Map<String, String> fechaActual = Config.fechas(1);

        HashMap<String, String> usuario = sessionManager.getUserDetails();
        String numeroUsuario = usuario.get(SessionManager.USER_ID);

        final String smParam1 = fechaActual.get("fechaIni");
        final String smParam2 = fechaActual.get("fechaFin");
        try{
            if(getArguments() != null){
                mParam1 = getArguments().getString(ARG_PARAM1); // fecha inicio
                mParam2 = getArguments().getString(ARG_PARAM2); // fecha Fin
                mParam3 = getArguments().getString(ARG_PARAM3); // id asesor
                mParam4 = getArguments().getInt(ARG_PARAM4); // id gerencia
                mParam5 = getArguments().getInt(ARG_PARAM5); // id sucursal
                rqt.put("idGerencia", mParam4);
                rqt.put("idSucursal", mParam5);
                rqt.put("numeroEmpleadoAsesor", mParam3);
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", fechaFin);
                periodo.put("fechaInicio", fechaIni);
                rqt.put("periodo", periodo);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }else{
                rqt.put("idGerencia", 0);
                rqt.put("idSucursal", 0);
                rqt.put("numeroEmpleadoAsesor", "");
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", smParam2);
                periodo.put("fechaInicio", smParam1);
                rqt.put("periodo", periodo);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d("RQT", " ReporteAsesores ->" + obj);
        }catch (JSONException e){
            e.printStackTrace();
        }

        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_ASESORES, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            primerPaso(response,smParam1,smParam2);
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

    private void primerPaso(JSONObject obj,String smParam1,String smParam2) {
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
        tvSaldoEmitido.setText("" + Config.nf.format(saldoEmitido));
        tvSaldoNoEmitido.setText("" + Config.nf.format(saldoNoEmitido));
        tvResultados.setText("" + filas + " Resultados ");


        //String smParam1 = fechaActual.get("fechaIni");
        //String smParam2 = fechaActual.get("fechaFin");

        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        String PtvFecha = tvFecha.getText().toString();
        String[] separated = PtvFecha.split(" - ");

        Map<String, String> fechaActual = Config.fechas(1);
        String sParam1 = fechaActual.get("fechaIni");
        String sParam2 = fechaActual.get("fechaFin");
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            adapter = new DirectorReporteAsesoresAdapter(rootView.getContext(), getDatos1, recyclerView, mParam1, mParam2);
        }else{
            adapter = new DirectorReporteAsesoresAdapter(rootView.getContext(), getDatos1, recyclerView, sParam1, sParam2);
        }
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
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
    }
}
