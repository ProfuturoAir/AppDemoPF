package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

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
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.CitasClientesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteSucursalesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.GerenteReporteSucursalesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.*;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.CitasClientesModel;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteSucursalesModel;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteSucursalesModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

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
 * {@link ReporteSucursales.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteSucursales#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteSucursales extends Fragment implements  Spinner.OnItemSelectedListener {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "parametro1";
    private static final String ARG_PARAM2 = "parametro2";
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: recycler
    private GerenteReporteSucursalesAdapter adapter;
    private List<GerenteReporteSucursalesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;

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
    private Spinner spinnerSucursales;
    //An ArrayList for Spinner Items
    private ArrayList<String> sucursales;
    private ArrayList<String> id_sucursales;

    private JSONArray resultSucursales;

    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;
    private TextView tvResultados;
    int filas;
    final Fragment borrar = this;

    private OnFragmentInteractionListener mListener;
    private Connected connected;

    int idSucursal;
    int numeroEmpleado;

    public ReporteSucursales() {
        // Required empty public constructor
    }


    // TODO: Rename and change types and number of parameters
    public static ReporteSucursales newInstance(String sParam1, String sParam2, Context context) {
        ReporteSucursales fragment = new ReporteSucursales();
        Bundle args = new Bundle();
        args.putString("parametro1", sParam1);
        args.putString("parametro2", sParam2);
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

        idSucursal = 0;
        numeroEmpleado = 0;

        variables();
        fechas();
        // TODO: Spinner
        //Initializing the ArrayList

        /*ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.SUCURSALES);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursales.setAdapter(adapter);*/

        sessionManager = new SessionManager(getContext());
        connected = new Connected();

        if(getArguments() != null) {
            Log.d("HOLA", "Todos : " + getArguments().toString());
            idSucursal = getArguments().getInt("idSucursal");
            numeroEmpleado = getArguments().getInt("numeroEmpleado");
            fechaIni = getArguments().getString("fechaIni");
            fechaFin = getArguments().getString("fechaFin");

            if(fechaIni!=null){
                tvRangoFecha1.setText(fechaIni);
                tvRangoFecha2.setText(fechaFin);
                tvFecha.setText(fechaIni + " - " + fechaFin);
            }

            if(idSucursal!=0){
                Log.d("SELE","SIZE ->: "+id_sucursales);
                int size = id_sucursales.size();
                /*for(int i=0; i < id_sucursales.size(); i++) {
                    if (id_sucursales[i].contains("#abc"))
                        aPosition = i;
                }*/
                //String sim = id_sucursales;
                Log.d("SELE","SIZE ->: "+size);
                //idSucursal = Integer.valueOf(sim);
            }
        }
        // TODO: model
        getDatos1 = new ArrayList<>();
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.gfrs_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        // TODO: Boton filtro
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();

                final String fechaIncial = tvRangoFecha1.getText().toString();
                final String fechaFinal = tvRangoFecha2.getText().toString();
                if(fechaIncial.equals("") || fechaFinal.equals("") || idSucursal == 0){
                    Config.dialogoDatosVacios(getContext());
                }else {
                    ReporteSucursales fragmentoSucursales = new ReporteSucursales();
                    Gerente gerente = (Gerente) getContext();
                    gerente.switchSucursalFS(fragmentoSucursales, idSucursal,fechaIncial,fechaFinal);
                    /*ReporteSucursales fragmento = ReporteSucursales.newInstance(fechaIni, fechaFin, rootView.getContext());
                    borrar.onDestroy();
                    ft.remove(borrar);
                    ft.replace(R.id.content_gerente, fragmento);
                    ft.addToBackStack(null);
                    ft.commit();*/
                }
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

                        final String datoEditText = editText.getText().toString().trim();
                        final String datoSpinner = spinner.getSelectedItem().toString().trim();

                        Log.d("DATOS USER","SPINNER: "+datoEditText+" datosSpinner: "+ datoSpinner);
                        if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                            Config.msj(getContext(), "Error", "Ingresa email valido");
                        }else{
                            String email = datoEditText+"@"+datoSpinner;
                            Connected connected = new Connected();
                            final InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Service.INPUT_METHOD_SERVICE);
                            if(connected.estaConectado(getContext())){
                                //final EnviaMail envia = new EnviaMail();
                                //String respuesta = envia.sendMail("1","correo",true,"1","12","12",Config.URL_SEND_MAIL,getContext());
                                /*imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                Config.msjTime(getContext(), "Enviando", "Se ha enviado el mensaje al destino", 4000);
                                dialog.dismiss();*/
                                //final String idSucursal = spinnerSucursales.getSelectedItem().toString();

                                Log.d("DATOS","+++++: "+idSucursal);
                                JSONObject obj = new JSONObject();
                                boolean checa = true;
                                if (idSucursal == 0){
                                    checa = false;
                                }

                                Map<String, String> fechaActual = Config.fechas(1);
                                String smParam1 = fechaActual.get("fechaIni");
                                String smParam2 = fechaActual.get("fechaFin");

                                try {
                                    if(getArguments() != null){
                                        JSONObject rqt = new JSONObject();
                                        rqt.put("correo", email);
                                        rqt.put("detalle", checa);
                                        rqt.put("idSucursal", idSucursal);
                                        JSONObject periodo = new JSONObject();
                                        periodo.put("fechaFin", fechaFin);
                                        periodo.put("fechaInicio", fechaIni);
                                        rqt.put("periodo", periodo);
                                        rqt.put("usuario", Config.usuarioCusp(getContext()));
                                        obj.put("rqt", rqt);
                                    }else{
                                        JSONObject rqt = new JSONObject();
                                        rqt.put("correo", email);
                                        rqt.put("detalle", checa);
                                        rqt.put("idSucursal", 0);
                                        JSONObject periodo = new JSONObject();
                                        periodo.put("fechaFin", smParam2);
                                        periodo.put("fechaInicio", smParam1);
                                        rqt.put("periodo", periodo);
                                        rqt.put("usuario", Config.usuarioCusp(getContext()));
                                        obj.put("rqt", rqt);
                                    }
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
        return inflater.inflate(R.layout.gerente_fragmento_reporte_sucursales, container, false);
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

    public void variables(){


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

        spinnerSucursales.setOnItemSelectedListener(this);
        getData();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        switch (parent.getId())
        {
          /*  case R.id.spinnerTurno:
                 getTurnos();
                 TextView text23 =(TextView) findViewById(R.id.textVialidad);
                 text23.setText(""+position);
                break;*/
            case R.id.gfrs_spinner_sucursales:
                String sim = id_sucursales.get(position);
                Log.d("SELE","ESTA ->: "+sim);
                idSucursal = Integer.valueOf(sim);
                //id_sucursales.clear();
                //sucursales.clear();
                //spinnerSucursales.setAdapter(null);
                //tramo.clear();
                //TextView tramedit =(TextView)findViewById(R.id.tramo);
                //tramedit.setText("");
                //getDataTramo(sim);
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
                            Log.d("LLENA", "SPINNER: ->" + response);
                            JSONArray j = null;
                            try {
                                //j = new JSONObject(response);
                                j = response.getJSONArray("Sucursales");
                                Log.d("LLENA", "EL ARRAY: ->" + j);
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

        //Log.d("LLENA", "LAS SUCURSALES : ->" + sucursales);

        int position=0;
        if(idSucursal!=0){

            //Log.d("SELE","By Position ->: "+position);

            int size = id_sucursales.size();
                for(int i=0; i < id_sucursales.size(); i++) {
                    Log.d("SELE","by ID ->: " +id_sucursales.get(i));
                    Log.d("SELE","ID ->: " +idSucursal);
                    if(Integer.valueOf(id_sucursales.get(i)) == idSucursal){
                        Log.d("SELE","SIZE ->: "+position);
                        position = i;
                        break;
                    }
                }
            //Log.d("SELE","SIZE ->: "+size);
            //Log.d("SELE","By Position ->: "+position);
        }

        //spinnerSucursales.setAdapter(new ArrayAdapter<String>(getActivity(), R.layout.spinner_item, sucursales));
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, sucursales);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerSucursales.setAdapter(adapter);
        spinnerSucursales.setSelection(position);
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
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        Map<String, String> fecha = Config.fechas(1);
        String param1 = fecha.get("fechaIni");
        String param2 = fecha.get("fechaFin");
        try {
            if(getArguments() != null){
                numeroEmpleado = getArguments().getInt("numeroEmpleado");
                rqt.put("idGerencia", 0);
                rqt.put("idSucursal", idSucursal);
                String numEmpleado;
                numEmpleado = (numeroEmpleado == 0) ? "" : String.valueOf(numeroEmpleado);
                rqt.put("numeroEmpleado", numEmpleado);
                rqt.put("pagina", pagina);
                rqt.put("periodo", periodo);
                periodo.put("fechaInicio", fechaIni);
                periodo.put("fechaFin", fechaFin);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }else{
                rqt.put("idGerencia", 0);
                rqt.put("idSucursal", 0);
                rqt.put("numeroEmpleado", "");
                rqt.put("pagina", pagina);
                rqt.put("usuario", "");
                rqt.put("periodo", periodo);
                periodo.put("fechaInicio", param1);
                periodo.put("fechaFin", param2);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d("ReporteSucursales ", "RQT --> " + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un right_in al formar los datos.");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_SUCURSALES, obj,
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
            totalFilas = 50;
            JSONObject objSaldo = obj.getJSONObject("saldo");
            saldoEmitido = objSaldo.getInt("saldoRetenido");
            saldoNoEmitido = objSaldo.getInt("saldoNoRetenido");
            filas = obj.getInt("filasTotal");
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
            e.printStackTrace();
        }

        tvEmitidas.setText("" + emitidos);
        tvNoEmitidas.setText("" + noEmitido);
        tvSaldoEmitido.setText("" + Config.nf.format(saldoEmitido));
        tvSaldoNoEmitido.setText("" + Config.nf.format(saldoNoEmitido));
        tvResultados.setText(filas + " Resultados ");

        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        String PtvFecha = tvFecha.getText().toString();
        String[] separated = PtvFecha.split(" - ");


        adapter = new GerenteReporteSucursalesAdapter(rootView.getContext(), getDatos1, recyclerView,separated[0].trim(),separated[1].trim());
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
                                fechaFin = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

}
