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
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.CitasClientesModel;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteClientesModel;
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
public class ReporteClientes extends Fragment implements  Spinner.OnItemSelectedListener{
    // TODO: Rename parameter arguments, choose names that match
    private static final String ARG_PARAM1 = "fechaIni";
    private static final String ARG_PARAM2 = "fechaFin";
    private static final String ARG_PARAM3 = "idBuscar";
    private static final String ARG_PARAM5 = "idSucursal";
    private static final String ARG_PARAM6 = "IdAsesor";
    private static final String ARG_PARAM7 = "idEstatus";
    private static final String ARG_PARAM8 = "odCita";
    private static final String ARG_PARAM9 = "selecionId";
    // TODO: Rename and change types of parameters
    private String mParam1; // fecha ini
    private String mParam2; // fecha fin
    private String mParam3; // idUsuario
    private int mParam5; // id sucursal
    private String mParam6; // idAsesor
    private int mParam7; // idRetenidos
    private int mParam8; // idCitas
    private int mParam9; // Seleccion
    int idGerencia;
    private Spinner spinnerId, spinnerSucursales, spinnerRetenido, spinnerCita, spinnerGerencias;
    private ArrayList<String> sucursales;
    private ArrayList<String> id_sucursales;
    private JSONArray resultSucursales;
    private EditText etIngresarDato, etIngresarAsesor;
    private Button btnBuscar;
    private TextView tvResultados;
    private TextView tvRangoFecha1, tvRangoFecha2, tvFecha;
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private InputMethodManager imm;
    private Connected connected;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private String numeroUsuario, nombre;
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
    String numeroEmpleado;
    int idSucursal;
    int tipoBuscar;
    int numeroId ;
    int estatus ;
    int retenido;
    int spinId = 0;
    int spinCit = 0;
    int spinRet = 0;
    private ArrayList<String> gerencias;
    private ArrayList<String> id_gerencias;
    private String fechaInicio1;
    private String fechafin1;
    private String datosCliente1 = "";
    private int idSucursal1;
    private int idGerencia1;
    private String idAsesor1;
    private int idRetenido1;
    private int idCita1;
    private int selectID;


    public ReporteClientes() {
        // requiere un constructor vacio
    }

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteClientes.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteClientes newInstance(String param1, String param2, String param3, int param5, String param6, int param7, int param8, int param9, Context context) {
        ReporteClientes fragment = new ReporteClientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putInt(ARG_PARAM7, param7);
        args.putInt(ARG_PARAM8, param8);
        args.putInt(ARG_PARAM9, param9);
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
        sucursales = new ArrayList<String>();
        id_sucursales = new ArrayList<String>();
        gerencias = new ArrayList<String>();
        id_gerencias = new ArrayList<String>();
        idSucursal = 0;
        numeroEmpleado = "";
        variables();
        fechas();
        connected = new Connected();
        // TODO: ocultar teclado
        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

        datosCliente1 = "";
        if(getArguments() != null) {
            Log.d("-->>>SI ENTRA", getArguments().toString());
            fechaInicio1 = getArguments().getString("fechaInicio");
            fechaIni = fechaInicio1;
            fechafin1 = getArguments().getString("fechaFin");
            datosCliente1 = getArguments().getString("ingresarDatoCliente");
            idSucursal1 = getArguments().getInt("idSucursal");
            idGerencia1 = getArguments().getInt("idGerencia");
            idAsesor1 = getArguments().getString("idAsesor");
            idRetenido1 = getArguments().getInt("idRetenido");
            idCita1 = getArguments().getInt("idCita");
            selectID = getArguments().getInt("selectCliente");
            Log.d("PARAMETROS BUNDLE:", " *********************" );
            Log.d("PARAMETROS BUNDLE:", " parametro 1: fecha inicio" + fechaInicio1);
            if(fechaInicio1!=null)
                tvRangoFecha1.setText(fechaInicio1);
            Log.d("PARAMETROS BUNDLE:", " parametro 2: fecha fin" + fechafin1);
            if(fechafin1!= null)
                tvRangoFecha2.setText(fechafin1);
            Log.d("PARAMETROS BUNDLE:", " parametro 3: idBuscar" + datosCliente1);
            Log.d("PARAMETROS BUNDLE:", " parametro 5: idSucursal" + idSucursal1);
            Log.d("PARAMETROS BUNDLE:", " parametro 6: iDAsesor" + idAsesor1);
            Log.d("PARAMETROS BUNDLE:", " parametro 7: estatuS" + idRetenido1);
            Log.d("PARAMETROS BUNDLE:", " parametro 8: con cita" + idCita1);
            Log.d("PARAMETROS BUNDLE:", " parametro 9: fecha seleccion ID" + selectID);
            Log.d("PARAMETROS BUNDLE:", " *********************");

            tvFecha.setText(fechaInicio1 + " - " + fechafin1);

            if(selectID!=0){
                Log.d("HOLA", "Todos : " + tipoBuscar);
                spinnerId.setSelection(selectID);
                spinId = tipoBuscar;
            }

            if(etIngresarAsesor!=null)
                etIngresarAsesor.setText(idAsesor1);

            //Log.d("ENTRA datosnte---->", datosCliente1);
            if(datosCliente1 != null) {
                if (!datosCliente1.isEmpty()) {
                    etIngresarDato.setText(datosCliente1);
                }
            }

        }

        // TODO: Spinner
        final ArrayAdapter<String> adapterId = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
        adapterId.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                //etIngresarDato.setText("");
                switch (position){
                    case 0:
                        etIngresarDato.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark1), PorterDuff.Mode.OVERLAY);
                        etIngresarDato.setFocusable(false);
                        break;
                    case 1:
                        etIngresarDato.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etIngresarDato.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case 2:
                        etIngresarDato.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etIngresarDato.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etIngresarDato.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        break;
                    case 3:
                        etIngresarDato.setFocusableInTouchMode(true);
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
        spinnerId.setSelection(selectID);
        // TODO: Spinner
        ArrayAdapter<String> adapterRetenido = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.RETENIDO);
        adapterRetenido.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRetenido.setAdapter(adapterRetenido);
        spinnerRetenido.setSelection(idRetenido1);
        // TODO: Spinner
        ArrayAdapter<String> adapterCita = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.CITAS);
        adapterCita.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCita.setAdapter(adapterCita);
        spinnerCita.setSelection(idRetenido1);

        // TODO: Recycler
        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfrc_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Log.d("------>>>>>datos:", etIngresarDato.getText().toString());
                if(connected.estaConectado(getContext())){
                    mParam1 = tvRangoFecha1.getText().toString();
                    mParam2 = tvRangoFecha2.getText().toString();
                    mParam3 = etIngresarDato.getText().toString();
                    mParam5 = spinnerSucursales.getSelectedItemPosition();
                    mParam6 = etIngresarAsesor.getText().toString();
                    mParam7 = spinnerRetenido.getSelectedItemPosition();
                    mParam8 = spinnerCita.getSelectedItemPosition();
                    mParam9 = spinnerId.getSelectedItemPosition();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    if(mParam1.isEmpty() || mParam2.isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        Log.d("idS","SS: "+idSucursal + " ___ " +mParam3);
                        if(mParam3.isEmpty()){
                            mParam3 = "";
                        }
                        if(mParam6.isEmpty()){
                            mParam6 = "";
                        }
                        Log.d("HOLA", "Todos : " + mParam6);
                        Log.d("HOLA", "Todos : " + mParam3);
                        ReporteClientes fragmentoClientes = new ReporteClientes();
                        Director director = (Director) getContext();

                        Log.d("DATOS A ENVIAR", "fechaInicio: " +  mParam1);
                        Log.d("DATOS A ENVIAR", "fechaFin: " +  mParam2);
                        Log.d("DATOS A ENVIAR", "IngresarDatoCliente: " +  mParam3);
                        Log.d("DATOS A ENVIAR", "ID sucursales:" +  idSucursal);
                        Log.d("DATOS A ENVIAR", "idAsesor: " +  mParam6);
                        Log.d("DATOS A ENVIAR", "idRetenido:" +  mParam7);
                        Log.d("DATOS A ENVIAR", "idCita: " +  mParam8);
                        Log.d("DATOS A ENVIAR", "selectCliente" +  mParam9);

                        director.switchClientesFCQ11(fragmentoClientes, mParam1 /*fechaInicio*/, mParam2/*fechafin*/, mParam3/*DatosCliente*/,  idSucursal/*IdSucursal*/, idGerencia,
                                mParam6/*idAsesor*/, mParam7/*idRetenido*/, mParam8/*idCitas*/, mParam9/*SeleccionIDS*/);

                        //director.switchClientesFCQ(fragmentoClientes,idSucursal,mParam6, mParam1,mParam2,mParam9,mParam3,mParam7,mParam8);
                        Config.teclado(getContext(), etIngresarAsesor);
                        Config.teclado(getContext(), etIngresarDato);
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
                                boolean checa = true;
                                if (idSucursal == 0){
                                    checa = false;
                                }
                                try {
                                    Log.d("----->><<filtro c:", tipoBuscar +"");
                                    if(getArguments() != null){
                                        boolean detalle = true;
                                        rqt.put("correo", email);
                                        rqt.put("detalle", detalle);
                                        rqt.put("filtro", filtro);
                                        filtro.put("cita", idCita1);
                                        filtro.put("filtroCliente", filtroCliente);
                                        if(tipoBuscar == 1){
                                            filtroCliente.put("curp", "");
                                            filtroCliente.put("nss", "");
                                            filtroCliente.put("numeroCuenta", datosCliente1);
                                        } else if(tipoBuscar == 2){
                                            filtroCliente.put("curp", "");
                                            filtroCliente.put("nss", datosCliente1);
                                            filtroCliente.put("numeroCuenta", "");
                                        }else if(tipoBuscar == 3){
                                            filtroCliente.put("curp", datosCliente1);
                                            filtroCliente.put("nss", "");
                                            filtroCliente.put("numeroCuenta", "");
                                        } else{
                                            filtroCliente.put("curp", "");
                                            filtroCliente.put("nss", "");
                                            filtroCliente.put("numeroCuenta", "");
                                        }
                                        filtro.put("filtroRetencion", retenido);
                                        filtro.put("idSucursal", idSucursal);
                                        filtro.put("idGerencia", idGerencia);
                                        filtro.put("numeroEmpleado", numeroEmpleado);
                                        periodo.put("fechaInicio", fechaIni);
                                        periodo.put("fechaFin", fechaFin);
                                        rqt.put("periodo", periodo);
                                        rqt.put("numeroEmpleado", numeroEmpleado);
                                        obj.put("rqt", rqt);
                                    }else {
                                        Map<String, String> fechaActual = Config.fechas(1);
                                        String smParam1 = fechaActual.get("fechaIni");
                                        String smParam2 = fechaActual.get("fechaFin");
                                        boolean detalle = true;
                                        rqt.put("correo", email);
                                        rqt.put("detalle", detalle);
                                        rqt.put("filtro", filtro);
                                        filtro.put("cita", 0);
                                        filtro.put("filtroCliente", filtroCliente);
                                        filtroCliente.put("curp", "");
                                        filtroCliente.put("nss", "");
                                        filtroCliente.put("numeroCuenta", "");
                                        filtro.put("filtroRetencion", 0);
                                        filtro.put("idSucursal", 0);
                                        filtro.put("numeroEmpleado", "");
                                        periodo.put("fechaInicio", smParam1);
                                        periodo.put("fechaFin", smParam2);
                                        rqt.put("periodo", periodo);
                                        filtro.put("idGerencia", 0);
                                        rqt.put("numeroEmpleado", "");
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
        return inflater.inflate(R.layout.director_fragmento_reporte_clientes, container, false);
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

    private void variables(){
        spinnerId = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_id);
        spinnerGerencias = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_gerencia);
        spinnerSucursales = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_sucursal);
        spinnerRetenido = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_estado);
        spinnerCita = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_citas);
        tvFecha = (TextView) rootView.findViewById(R.id.ddfrc_tv_fecha);
        tvRangoFecha1  = (TextView) rootView.findViewById(R.id.ddfrc_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) rootView.findViewById(R.id.ddfrc_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.ddfrc_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.ddfrc_tv_registros);
        etIngresarDato = (EditText) rootView.findViewById(R.id.ddfrc_et_id);
        etIngresarAsesor = (EditText) rootView.findViewById(R.id.ddfrc_et_asesor);
        spinnerSucursales.setOnItemSelectedListener(this);
        spinnerSucursales.setSelection(idSucursal);
        spinnerGerencias.setOnItemSelectedListener(this);
        spinnerGerencias.setSelection(idGerencia1);
        getData();
        getDataGerencias();
    }


    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId()) {
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
                return Config.credenciales(getContext());
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
        if(idSucursal1!=0){

            for(int i=0; i < id_sucursales.size(); i++) {
                if(Integer.valueOf(id_sucursales.get(i)) == idSucursal1){
                    Log.d("SELE","SIZE ->: "+position);
                    position = i;
                    break;
                }
            }
        }

        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, sucursales);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursales.setAdapter(adapter);
        spinnerSucursales.setSelection(position);
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
                return Config.credenciales(getContext());
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

        if(idGerencia1!=0){
            for(int i=0; i < id_gerencias.size(); i++) {
                if(Integer.valueOf(id_gerencias.get(i)) == idGerencia1){
                    Log.d("SELECION GERENCIA","SIZE ->: " + position);
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

    /**
     *  Espera el regreso de fechas incial (hoy y el dia siguiente)
     *  y cuando se realiza una nueva busqueda, retorna las fechas seleccionadas
     */
    private void fechas(){
        rangoInicial();
        rangoFinal();
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");
        // TODO: fecha
        Map<String, String> fechaActual = Config.fechas(1);
        String smParam1 = fechaActual.get("fechaIni");
        String smParam2 = fechaActual.get("fechaFin");
        if(getArguments() == null){
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
    }

    private void sendJson(final boolean primeraPeticion){
        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtroCliente = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            if(getArguments() != null){
                Log.d("---->>>>SI ENTRA:" , getArguments().toString());
                String datoCliente1 = getArguments().getString("idAsesor");
                String fechaInicio1 = getArguments().getString("fechaInicio");
                String fechafin1 = getArguments().getString("fechaFin");
                this.fechaIni = fechaInicio1;
                this.fechaFin = fechafin1;
                String datosCliente1 = getArguments().getString("ingresarDatoCliente");
                int idGERENCIA = getArguments().getInt("idGerencia");
                int idSUCURSAL = getArguments().getInt("idSucursal");
                this.datosCliente1 = datosCliente1;
                int idSucursal1 = getArguments().getInt("idSucursales");
                String idAsesor1 = getArguments().getString("idAsesor");
                int idRetenido1 = getArguments().getInt("idRetenido");
                idCita1 = getArguments().getInt("idCita");
                int selectID = getArguments().getInt("selectCliente");
                this.tipoBuscar = selectID;
                rqt.put("cita", idCita1);
                this.numeroEmpleado = idAsesor1;
                this.retenido = idRetenido1;
                if(selectID == 1){
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", datosCliente1);
                } else if(selectID == 2){
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", datosCliente1);
                    filtroCliente.put("numeroCuenta", "");
                }else if(selectID == 3){
                    filtroCliente.put("curp", datosCliente1);
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", "");
                } else{
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", "");
                }
                rqt.put("numeroEmpleado", idAsesor1);
                rqt.put("filtroCliente", filtroCliente);
                rqt.put("idGerencia", idGERENCIA);
                rqt.put("idSucursal", idSUCURSAL);
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", fechafin1);
                periodo.put("fechaInicio", fechaInicio1);
                rqt.put("periodo", periodo);
                rqt.put("retenido", idRetenido1);
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
                rqt.put("pagina", pagina);
                rqt.put("numeroEmpleado", "");
                periodo.put("fechaFin", smParam2);
                periodo.put("fechaInicio", smParam1);
                rqt.put("periodo", periodo);
                rqt.put("retenido", 0);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                json.put("rqt", rqt);
            }
            Log.d("ReporteClientes", " REQUEST -->" + json);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un right_in al formar la peticion");
        }

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
    }

    private void primerPaso(JSONObject obj){
        //Log.d("primer paso", "Response: "  + obj );
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("Cliente");
            filas = obj.getInt("filasTotal");
            totalFilas = obj.getInt("filasTotal");
            Log.d("primerPaso", "response -->" + array);
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
                    getDatos2.setHora(json.getString("horaAtencion"));
                    getDatos2.setTramite(json.getInt("idTramite"));
                    //getDatos2.setCurp(json.getString("curp"));
                    //getDatos2.setNss(json.getString("nss"));
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
        String PtvFecha = tvFecha.getText().toString();
        String[] separated = PtvFecha.split(" - ");

        adapter = new DirectorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView,separated[0].trim(),separated[1].trim());
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
                    getDatos2.setCita(json.getString("cita"));
                    getDatos2.setRetenido(json.getString("retenido"));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
                    getDatos2.setHora(json.getString("horaAtencion"));
                    getDatos2.setTramite(json.getInt("idTramite"));
                    //getDatos2.setCurp(json.getString("curp"));
                    //getDatos2.setNss(json.getString("nss"));
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
}
