package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.Service;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.PorterDuff;
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
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteGerenciasAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteClientesModel;
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
 * {@link ReporteClientes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteClientes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteClientes extends Fragment implements  Spinner.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "idFiltroBusquedaCliente";
    private static final String ARG_PARAM2 = "idBusquedaCliente";
    private static final String ARG_PARAM3 = "idGerencia";
    private static final String ARG_PARAM4 = "idSucursal";
    private static final String ARG_PARAM5 = "idAsesor";
    private static final String ARG_PARAM6 = "fechaInicio";
    private static final String ARG_PARAM7 = "fechaFin";
    private static final String ARG_PARAM8 = "estatusRetenido";
    private static final String ARG_PARAM9 = "estatusCita";
    // TODO: Rename and change types of parameters

    private int mParam1; // idFiltroBusquedaCliente
    private String mParam2; // idBusquedaCliente
    private int mParam3; // idGerencia
    private int mParam4; // idSucursal
    private String mParam5; // idAsesor
    private String mParam6; // fechaInicio
    private String mParam7; // fechaFin
    private int mParam8; // estatusRetenido
    private int mParam9; // estatusCita

    private Spinner spinnerId, spinnerSucursal, spinnerRetenido, spinnerCita, spinnerGerencias;
    private EditText etIngresarDato, etIngresarAsesor;
    private Button btnBuscar;
    private TextView tvResultados;
    private TextView tvRangoFecha1, tvRangoFecha2, tvFecha;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private InputMethodManager imm;
    private Connected connect;

    // TODO: variable
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni;
    private String fechaFin;
    private int numeroEmpleado;
    private String fechaMostrar = "";
    private String numeroUsuario, nombre;
    // TODO: LIST
    private List<DirectorReporteClientesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private int filas;
    private DirectorReporteClientesAdapter adapter;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    private int totalF;
    final Fragment borrar = this;

    private OnFragmentInteractionListener mListener;

    int  idSucursal,idGerencia;

    int tipoBuscar;
    int numeroId ;
    int estatus ;
    int retenido;

    int spinId = 0;
    int spinCit = 0;
    int spinRet = 0;

    private ArrayList<String> sucursales;
    private ArrayList<String> id_sucursales;

    private ArrayList<String> gerencias;
    private ArrayList<String> id_gerencias;

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
    public static ReporteClientes newInstance(int param1, String param2,
                                              int param3, int param4, String param5,
                                              String param6, String param7,
                                              int param8, int param9, Context context) {
        ReporteClientes fragment = new ReporteClientes();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);

        args.putInt(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);

        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);

        args.putInt(ARG_PARAM8, param8);
        args.putInt(ARG_PARAM9, param9);

        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("CLIENTE","PASA AQUI");
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;

        primeraPeticion();

        sucursales = new ArrayList<String>();
        id_sucursales = new ArrayList<String>();

        gerencias = new ArrayList<String>();
        id_gerencias = new ArrayList<String>();

        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> datos = sessionManager.getUserDetails();
        // CASTEO DE ELEMENTOS
        spinnerGerencias = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_gerencia);
        spinnerId = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_id);
        spinnerSucursal = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_sucursal);
        spinnerRetenido = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_estado);
        spinnerCita = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_citas);
        tvFecha = (TextView) view.findViewById(R.id.ddfrc_tv_fecha);
        tvRangoFecha1  = (TextView) view.findViewById(R.id.ddfrc_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) view.findViewById(R.id.ddfrc_tv_fecha_rango2);
        btnBuscar = (Button) view.findViewById(R.id.ddfrc_btn_buscar);
        tvResultados = (TextView) view.findViewById(R.id.ddfrc_tv_registros);
        etIngresarDato = (EditText) view.findViewById(R.id.ddfrc_et_id);
        etIngresarAsesor = (EditText) view.findViewById(R.id.ddfrc_et_asesor);

        spinnerSucursal.setOnItemSelectedListener(this);
        spinnerGerencias.setOnItemSelectedListener(this);

        getData();
        getDataGerencias();

        // TODO: ocultar teclado
        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        connect = new Connected();

        Log.d("DATOS","FREG: "+numeroEmpleado+" DI: "+fechaIni+" DF: "+fechaFin);

        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        fechas();

        if(getArguments() != null) {
            Log.d("HOLA", "Todos : " + getArguments().toString());
            idSucursal = getArguments().getInt("idSucursal");
            idGerencia = getArguments().getInt("idGerencia");
            numeroEmpleado = getArguments().getInt("numeroEmpleado");
            fechaIni = getArguments().getString("fechaIni");
            fechaFin = getArguments().getString("fechaFin");

            tipoBuscar = getArguments().getInt("tipoBuscar");
            numeroId = getArguments().getInt("numeroId");
            retenido = getArguments().getInt("retenido");
            estatus = getArguments().getInt("estatus");



            if(fechaIni!=null){
                tvRangoFecha1.setText(fechaIni);
                tvRangoFecha2.setText(fechaFin);
                tvFecha.setText(fechaIni + " - " + fechaFin);
            }

            if(etIngresarAsesor!=null){
                etIngresarAsesor.setText(String.valueOf(numeroEmpleado));
            }

            if(etIngresarDato!=null){
                Log.d("HOLA", "Todos YYYY: " + numeroId);
                etIngresarDato.setText(String.valueOf(numeroId));
            }

            if(estatus!=0){
                Log.d("HOLA", "Todos : " + estatus);
                spinCit = estatus;
            }

            if(retenido!=0){
                Log.d("HOLA", "Todos : " + retenido);
                spinnerRetenido.setSelection(2);
                spinRet = retenido;
            }

            if(tipoBuscar!=0){
                Log.d("HOLA", "Todos : " + tipoBuscar);
                spinnerId.setSelection(2);
                spinId = tipoBuscar;
            }
        }

        /*if(fechaIni!=null){
            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
            ReporteClientes procesoDatosFiltroInicio = ReporteClientes.newInstance(
                    fechaIni, fechaFin, String.valueOf(numeroEmpleado), mParam4, mParam5, mParam6, mParam7, mParam8, mParam9,
                    rootView.getContext()
            );
            borrar.onDestroy();
            ft.remove(borrar);
            ft.replace(R.id.content_director, procesoDatosFiltroInicio);
            ft.addToBackStack(null);
            ft.commit();
        }*/

        // TODO: Spinner
        final ArrayAdapter<String> adapterId = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
        adapterId.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etIngresarDato.setText("");

                switch (position){
                    case 0:
                        etIngresarDato.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark1), PorterDuff.Mode.OVERLAY);
                        etIngresarDato.setFocusable(false);
                        break;
                    case 1:
                        etIngresarDato.setFocusableInTouchMode(true);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etIngresarDato.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case 2:
                        etIngresarDato.setFocusableInTouchMode(true);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etIngresarDato.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        break;
                    case 3:
                        etIngresarDato.setFocusableInTouchMode(true);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etIngresarDato.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                }
                etIngresarDato.setHint("Ingresa, " + adapterId.getItem(position));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinnerId.setAdapter(adapterId);
        spinnerId.setSelection(spinId);
        // TODO: Spinner
        /*ArrayAdapter<String> adapterGerencias = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.GERENCIAS);
        adapterGerencias.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGerencias.setAdapter(adapterGerencias);
        // TODO: Spinner
        ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.SUCURSALES);
        adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapterSucursal);*/
        // TODO: Spinner
        ArrayAdapter<String> adapterRetenido = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.RETENIDO);
        adapterRetenido.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRetenido.setAdapter(adapterRetenido);
        spinnerRetenido.setSelection(spinRet);
        // TODO: Spinner
        ArrayAdapter<String> adapterCita = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.CITAS);
        adapterCita.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCita.setAdapter(adapterCita);
        spinnerCita.setSelection(spinCit);


        rangoInicial();
        rangoFinal();

        // TODO: Recycler - Lista elementos
        // TODO: modelos
        getDatos1 = new ArrayList<>();
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfrc_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(connect.estaConectado(getContext())){
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    mParam1 = spinnerId.getSelectedItemPosition(); // idFiltroBusquedaCliente -> int
                    mParam2 = etIngresarDato.getText().toString(); // idBusquedaCliente -> String
                    mParam3 = spinnerGerencias.getSelectedItemPosition(); // idGerencia -> int
                    mParam4 = spinnerSucursal.getSelectedItemPosition(); // idSucursal -> int
                    mParam5 = etIngresarAsesor.getText().toString(); // idAsesor -> String
                    mParam6 = tvRangoFecha1.getText().toString(); // fechaInicio -> String
                    mParam7 = tvRangoFecha2.getText().toString(); // fechaFin -> String
                    mParam8 = spinnerRetenido.getSelectedItemPosition(); // estatusRetenido -> int
                    mParam9 = spinnerCita.getSelectedItemPosition(); // estatusCita -> int


                    if(mParam1 == 0 || mParam2.isEmpty() || mParam3 == 0 || mParam4 == 0 ||
                            mParam5.isEmpty() || mParam6.isEmpty() || mParam7.isEmpty() || mParam8 == 0 || mParam9 == 0){
                        Config.dialogoDatosVacios(getContext());
                    }else{
                        Log.d("idS","SS: "+idSucursal + " ___ " +mParam3);


                        Log.d("HOLA", "Todos : " + mParam6);
                        Log.d("HOLA", "Todos : " + mParam3);
                        ReporteClientes fragmentoClientes = new ReporteClientes();
                        Director director = (Director) getContext();
                        director.switchClientesFCQ(fragmentoClientes,idSucursal,idGerencia,Integer.valueOf(mParam5),fechaIni,fechaFin,mParam1,Integer.valueOf(mParam2),mParam8,mParam9);
                        /*ReporteClientes fragmento = ReporteClientes.newInstance(mParam1, mParam2, mParam3, mParam4, mParam5, mParam6, mParam7, mParam8, mParam9, rootView.getContext());
                        Config.teclado(getContext(), etIngresarAsesor);
                        Config.teclado(getContext(), etIngresarDato);
                        borrar.onDestroy();
                        ft.remove(borrar);
                        ft.replace(R.id.content_director, fragmento);
                        ft.addToBackStack(null);
                        ft.commit();*/
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }


            }
        });

        etIngresarDato.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(etIngresarDato.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
            }
        });

        //<editor-fold desc="EditText campo ingresar asesor">
        etIngresarAsesor.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null&& (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                    in.hideSoftInputFromWindow(etIngresarAsesor.getApplicationWindowToken(),InputMethodManager.HIDE_NOT_ALWAYS);
                }
                return false;
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
                                JSONObject rqt = new JSONObject();
                                JSONObject filtro = new JSONObject();
                                JSONObject filtroCliente = new JSONObject();
                                JSONObject periodo = new JSONObject();

                                Map<String, String> fechaActual = Config.fechas(1);
                                String smParam1 = fechaActual.get("fechaIni");
                                String smParam2 = fechaActual.get("fechaFin");
                                try {
                                    if(getArguments() != null){
                                        mParam1 = getArguments().getInt(ARG_PARAM1); // idFiltroBusquedaCliente
                                        mParam2 = getArguments().getString(ARG_PARAM2); // idBusquedaCliente
                                        mParam4 = getArguments().getInt(ARG_PARAM4); // idSucursal
                                        mParam5 = getArguments().getString(ARG_PARAM5); // idAsesor
                                        mParam6 = getArguments().getString(ARG_PARAM6); // fechaInicio
                                        mParam7 = getArguments().getString(ARG_PARAM7); // fechaFin
                                        mParam8 = getArguments().getInt(ARG_PARAM8); // estatusRetenido
                                        mParam9 = getArguments().getInt(ARG_PARAM9); // estatusCita
                                        boolean detalle = true;

                                        rqt.put("correo", email);
                                        rqt.put("detalle", detalle);
                                        filtro.put("cita", mParam9);
                                        if(mParam1 == 1){
                                            filtroCliente.put("curp", "");
                                            filtroCliente.put("nss", "");
                                            filtroCliente.put("numeroCuenta", mParam2);
                                        } else if(mParam1 == 2){
                                            filtroCliente.put("curp", "");
                                            filtroCliente.put("nss", mParam2);
                                            filtroCliente.put("numeroCuenta", "");
                                        }else if(mParam1 == 3){
                                            filtroCliente.put("curp", mParam2);
                                            filtroCliente.put("nss", "");
                                            filtroCliente.put("numeroCuenta", "");
                                        } else{
                                            filtroCliente.put("curp", "");
                                            filtroCliente.put("nss", "");
                                            filtroCliente.put("numeroCuenta", "");
                                        }
                                        filtro.put("filtroRetenicion", mParam8);
                                        filtro.put("idSucursal", mParam4);
                                        filtro.put("numeroEmpleado", mParam5);
                                        rqt.put("filtro", filtro);
                                        rqt.put("numeroEmpleado", mParam5);
                                        periodo.put("fechaInicio", mParam6);
                                        periodo.put("fechaFin", mParam7);
                                        rqt.put("periodo", periodo);
                                        obj.put("rqt", rqt);
                                    }else {
                                        boolean detalle = true;
                                        rqt.put("correo", email);
                                        rqt.put("detalle", detalle);
                                        rqt.put("filtro", filtro);
                                            filtro.put("cita", 0);
                                                filtroCliente.put("curp", "");
                                                filtroCliente.put("nss", "");
                                                filtroCliente.put("numeroCuenta", "");
                                            filtro.put("filtroRetenicion", mParam8);
                                            filtro.put("idSucursal", mParam4);
                                            filtro.put("numeroEmpleado", mParam5);
                                        rqt.put("numeroEmpleado", mParam5);
                                            rqt.put("periodo", periodo);
                                                periodo.put("fechaInicio", mParam6);
                                                periodo.put("fechaFin", mParam7);
                                        obj.put("rqt", rqt);
                                    }
                                    Log.d("sendJson", " REQUEST -->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(getContext(), "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_CLIENTE,getContext(),new EnviaMail.VolleyCallback() {

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
                                            dialog.dismiss();
                                        }else{
                                            Config.msj(getContext(), "Error", "Ups algo salio mal =(");
                                            dialog.dismiss();
                                        }
                                    }
                                    @Override
                                    public void onError(String result) {
                                        Log.d("RESPUESTA ERROR", result);
                                        Config.msj(getContext(), "Error en conexión", "Por favor, revisa tu conexión a internet ++");
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
        //</editor-fold>
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.director_fragmento_reporte_clientes, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
            case R.id.ddfrc_spinner_gerencia:
                String sim = id_gerencias.get(position);
                Log.d("SELE","ESTA G->: "+sim);
                idGerencia = Integer.valueOf(sim);
                break;
            case R.id.ddfrc_spinner_sucursal:
                String s = id_sucursales.get(position);
                Log.d("SELE","ESTA ->: "+s);
                idSucursal = Integer.valueOf(s);
                break;
            default:
                /*TextView text21 =(TextView) findViewById(R.id.textVialidad);
                text21.setText("DEFAULT");*/
                break;
        }
    }

    //When no item is selected this method would execute
    @Override
    public void onNothingSelected(AdapterView<?> parent) {
        //Log.i("Message", "Nothing is selected");
    }

    private void getData(){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_SUCURSALES,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {

                        JSONArray j = null;
                        try {
                            j = response.getJSONArray("Sucursales");
                            getSucursales(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LLENA", "SPINNER: -> ERROR " + error);
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
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    //obtener delegaciones
    private void getSucursales(JSONArray j){
        sucursales.add("Selecciona una sucursal");
        id_sucursales.add("0");
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                sucursales.add(json.getString("nombre"));
                id_sucursales.add(json.getString("idSucursal"));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }

        int position=0;
        if(idSucursal!=0){

            for(int i=0; i < id_sucursales.size(); i++) {
                if(Integer.valueOf(id_sucursales.get(i)) == idSucursal){
                    Log.d("SELE","SIZE ->: "+position);
                    position = i;
                    break;
                }
            }
        }

        //spinnerSucursales.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, sucursales));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, sucursales);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapter);
        spinnerSucursal.setSelection(position);
    }

    private void getDataGerencias(){
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GERENCIAS,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        Log.d("STRING","GGG: "+response.toString());
                        JSONArray j = null;
                        try {
                            j = response.getJSONArray("Gerencias");
                            getGerencias(j);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.d("LLENA", "SPINNER: -> ERROR " + error);
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
        MySingleton.getInstance(getContext()).addToRequestQueue(jsonArrayRequest);
    }

    //obtener gerencias
    private void getGerencias(JSONArray j){
        gerencias.add("Selecciona una gerencia");
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
                Log.d("SELE","by ID ->: " +id_gerencias.get(i));
                Log.d("SELE","ID ->: " +idGerencia);
                if(Integer.valueOf(id_gerencias.get(i)) == idGerencia){
                    Log.d("SELE","SIZE ->: "+position);
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

    private void sendJson(final boolean primeraPeticion){


        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtroCliente = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            if(getArguments() != null){

                mParam1 = getArguments().getInt(ARG_PARAM1); // idFiltroBusquedaCliente
                mParam2 = getArguments().getString(ARG_PARAM2); // idBusquedaCliente
                mParam3 = getArguments().getInt(ARG_PARAM3); // idGerencia
                mParam4 = getArguments().getInt(ARG_PARAM4); // idSucursal
                mParam5 = getArguments().getString(ARG_PARAM5); // idAsesor
                mParam6 = getArguments().getString(ARG_PARAM6); // fechaInicio
                mParam7 = getArguments().getString(ARG_PARAM7); // fechaFin
                mParam8 = getArguments().getInt(ARG_PARAM8); // estatusRetenido
                mParam9 = getArguments().getInt(ARG_PARAM9); // estatusCita

                rqt.put("cita", mParam9);
                if(mParam1 == 1){
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", mParam2);
                } else if(mParam1 == 2){
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", mParam2);
                    filtroCliente.put("numeroCuenta", "");
                }else if(mParam1 == 3){
                    filtroCliente.put("curp", mParam2);
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", "");
                } else{
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", "");
                }
                rqt.put("filtroCliente", filtroCliente);

                rqt.put("idGerencia", mParam3);
                rqt.put("idSucursal", mParam4);
                periodo.put("fechaFin", mParam6);
                periodo.put("fechaInicio", mParam7);
                rqt.put("periodo", periodo);
                rqt.put("pagina", pagina);
                rqt.put("retenido", mParam8);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                json.put("rqt", rqt);
            }else {
                Map<String, String> fechaActual = Config.fechas(1);
                String smParam1 = fechaActual.get("fechaIni");
                String smParam2 = fechaActual.get("fechaFin");
                rqt.put("cita", 0);
                filtroCliente.put("curp","");
                filtroCliente.put("nss", "");
                filtroCliente.put("numeroCuenta", "");
                rqt.put("filtroCliente", filtroCliente);
                rqt.put("idGerencia", 0);
                rqt.put("idSucursal", 0);
                periodo.put("fechaFin", smParam2);
                periodo.put("fechaInicio", smParam1);
                rqt.put("periodo", periodo);
                rqt.put("pagina", pagina);
                rqt.put("retenido", 0);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                json.put("rqt", rqt);
            }
            Log.d("sendJson", " REQUEST -->" + json);

        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un right_in al formar la peticion");
        }

        //<editor-fold desc="jsonObjetRequst">
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_CLIENTES, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
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
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
        //</editor-fold>
    }

    private void primerPaso(JSONObject obj){
        Log.d("RESPONSE", " " + obj );
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("Cliente");
            filas = obj.getInt("filasTotal");
            totalFilas = obj.getInt("filasTotal");
            // Log.d("primerPaso", "response -->" + filas + totalFilas);
            for(int i = 0; i < array.length(); i++){
                DirectorReporteClientesModel getDatos2 = new DirectorReporteClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNombreCliente(json.getString("nombreCliente"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    getDatos2.setNumeroEmpleado(json.getString("numeroEmpleado"));
                    getDatos2.setNombreAsesor(json.getString("nombreAsesor"));
                    getDatos2.setCita(json.getString("cita"));
                    getDatos2.setRetenido(json.getString("retenido"));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
                    getDatos2.setIdTramite(json.getInt("idTramite"));
                    getDatos2.setHora(json.getString("horaAtencion"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvResultados.setText(filas + " Registros");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);

        Map<String, String> datos = new HashMap<String, String>();
        datos.put("fechaInicio", fechaIni);

        if(getArguments() != null){
            mParam6 = getArguments().getString(ARG_PARAM6); // fechaInicio
            mParam7 = getArguments().getString(ARG_PARAM7); // fechaFin
            adapter = new DirectorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView, mParam6, mParam7);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }else{
            Map<String, String> fecha = Config.fechas(1);
            String mParam1 = fecha.get("fechaIni");
            String mParam2 = fecha.get("fechaFin");
            adapter = new DirectorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView, mParam1, mParam2);
            recyclerView.setItemAnimator(new DefaultItemAnimator());
            recyclerView.setAdapter(adapter);
        }

        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        adapter.notifyDataSetChanged();

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                Log.d("setOnLoadMoreListener", "pagina->" + pagina + "numeroMaximo" + numeroMaximoPaginas);
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
        try{
            JSONArray array = obj.getJSONArray("Cliente");
            filas = obj.getInt("filasTotal");
            for(int i = 0; i < array.length(); i++){
                DirectorReporteClientesModel getDatos2 = new DirectorReporteClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNombreCliente(json.getString("nombreCliente"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    getDatos2.setNumeroEmpleado(json.getString("numeroEmpleado"));
                    getDatos2.setNombreAsesor(json.getString("nombreAsesor"));
                    getDatos2.setCita(json.getString("cita"));
                    getDatos2.setRetenido(json.getString("retenido"));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
                    getDatos2.setIdTramite(json.getInt("idTramite"));
                    getDatos2.setHora(json.getString("horaAtencion"));
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
            //mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
        //</editor-fold>
    }
}
