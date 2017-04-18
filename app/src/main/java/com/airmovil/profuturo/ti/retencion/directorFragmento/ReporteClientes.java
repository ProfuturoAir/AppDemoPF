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
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
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
public class ReporteClientes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "parametro1FechaIni";
    private static final String ARG_PARAM2 = "parametro2FechaFin";
    private static final String ARG_PARAM3 = "parametroIdABuscar";
    private static final String ARG_PARAM4 = "parametroIdGerencia";
    private static final String ARG_PARAM5 = "parametroIdSucursal";
    private static final String ARG_PARAM6 = "parametroIngresaIdAsesor";
    private static final String ARG_PARAM7 = "parametroEstatus";
    private static final String ARG_PARAM8 = "parametroCita";
    private static final String ARG_PARAM9 = "parametrosSelecionId";
    // TODO: Rename and change types of parameters
    private String mParam1; // fecha ini
    private String mParam2; // fecha fin
    private String mParam3; // idUsuario
    private int mParam4; // Id gerencia
    private int mParam5; // id sucursal
    private String mParam6; // idAsesor
    private int mParam7; // idRetenidos
    private int mParam8; // idCitas
    private int mParam9; // Seleccion

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
    public static ReporteClientes newInstance(String param1, String param2, String param3, int param4,
                                              int param5, String param6, int param7, int param8, int param9, Context context) {
        ReporteClientes fragment = new ReporteClientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
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

        // TODO: ocultar teclado
        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        connect = new Connected();

        if(getArguments() != null) {
            Log.d("HOLA", "Todos : " + getArguments().toString());
            numeroEmpleado = getArguments().getInt("numeroEmpleado");
            fechaIni = getArguments().getString("fechaIni");
            fechaFin = getArguments().getString("fechaFin");

            if(fechaIni!=null){
                tvRangoFecha1.setText(fechaIni);
                tvRangoFecha2.setText(fechaFin);
            }
        }


        Log.d("DATOS","FREG: "+numeroEmpleado+" DI: "+fechaIni+" DF: "+fechaFin);

        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        fechas();

        if(fechaIni!=null){
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
        }

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
        // TODO: Spinner
        ArrayAdapter<String> adapterGerencias = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.GERENCIAS);
        adapterGerencias.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerGerencias                                                                                                                                                                                                                                                                                   .setAdapter(adapterGerencias);
        // TODO: Spinner
        ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.SUCURSALES);
        adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursal.setAdapter(adapterSucursal);
        // TODO: Spinner
        ArrayAdapter<String> adapterRetenido = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.RETENIDO);
        adapterRetenido.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerRetenido.setAdapter(adapterRetenido);
        // TODO: Spinner
        ArrayAdapter<String> adapterCita = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.CITAS);
        adapterCita.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerCita.setAdapter(adapterCita);


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
                    mParam1 = tvRangoFecha1.getText().toString();
                    mParam2 = tvRangoFecha2.getText().toString();
                    mParam3 = etIngresarDato.getText().toString();
                    mParam4 = spinnerGerencias.getSelectedItemPosition();
                    mParam5 = spinnerSucursal.getSelectedItemPosition();
                    mParam6 = etIngresarAsesor.getText().toString();
                    mParam7 = spinnerRetenido.getSelectedItemPosition();
                    mParam8 = spinnerCita.getSelectedItemPosition();
                    mParam9 = spinnerId.getSelectedItemPosition();
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    if(mParam1.isEmpty() || mParam2.isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        ReporteClientes procesoDatosFiltroInicio = ReporteClientes.newInstance(
                                mParam1, mParam2, mParam3, mParam4, mParam5, mParam6, mParam7, mParam8, mParam9,
                                rootView.getContext()
                        );
                        borrar.onDestroy();
                        ft.remove(borrar);
                        ft.replace(R.id.content_director, procesoDatosFiltroInicio);
                        ft.addToBackStack(null);
                        ft.commit();
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

        //<editor-fold desc="TextView icono email">
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
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
                mParam3 = getArguments().getString(ARG_PARAM3);
                mParam4 = getArguments().getInt(ARG_PARAM4);
                mParam5 = getArguments().getInt(ARG_PARAM5);
                mParam6 = getArguments().getString(ARG_PARAM6);
                mParam7 = getArguments().getInt(ARG_PARAM7);
                mParam8 = getArguments().getInt(ARG_PARAM8);
                mParam9 = getArguments().getInt(ARG_PARAM9);

                rqt.put("cita", mParam8);
                if(mParam9 == 1){
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", mParam3);
                } else if(mParam9 == 2){
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", mParam3);
                    filtroCliente.put("numeroCuenta", "");
                }else if(mParam9 == 3){
                    filtroCliente.put("curp", mParam3);
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", "");
                } else{
                    filtroCliente.put("curp", "");
                    filtroCliente.put("nss", "");
                    filtroCliente.put("numeroCuenta", "");
                }

                rqt.put("filtroCliente", filtroCliente);
                rqt.put("idGerencia", mParam4);
                rqt.put("idSucursal", mParam5);
                rqt.put("pagina", pagina);
                periodo.put("fechaFin", mParam1);
                periodo.put("fechaInicio", mParam2);
                rqt.put("periodo", periodo);
                rqt.put("retenido", mParam7);
                rqt.put("usuario", mParam6);
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
                periodo.put("fechaFin", smParam2);
                periodo.put("fechaInicio", smParam1);
                rqt.put("periodo", periodo);
                rqt.put("retenido", 0);
                rqt.put("usuario", numeroUsuario);
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
                    getDatos2.setCita(json.getString("cita"));
                    getDatos2.setRetenido(json.getString("retenido"));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
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
        adapter = new DirectorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView);
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
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
        //</editor-fold>
    }
}
