package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.AlertDialog;
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
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.GerenteReporteAsistenciaDetalleAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteAsistenciaDetalleModel;
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
 * {@link ReporteAsistenciaDetalles.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteAsistenciaDetalles#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteAsistenciaDetalles extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "numeroEmpleado";
    private static final String ARG_PARAM2 = "fechaIni";
    private static final String ARG_PARAM3 = "fechaFin";
    private static final String ARG_PARAM4 = "nombreEmpleado";

    // TODO: Rename and change types of parameters
    private String mParam1 = "";
    private String mParam2 = "";
    private String mParam3 = "";
    private String mParam4 = "";

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private Connected connected;

    // TODO: XML
    private TextView tvFecha;
    private TextView tvLetra;
    private TextView tvNombreAsesor, tvNoEmpleado, tvRangoFechas;
    private TextView tvAtiempo, tvRetardo, tvSinAsistencia;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;
    private TextView tvResultados;

    // TODO: variable
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private String numeroUsuario;

    // TODO: list
    private List<GerenteReporteAsistenciaDetalleModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private GerenteReporteAsistenciaDetalleAdapter adapter;

    private int filas;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;

    private OnFragmentInteractionListener mListener;

    public ReporteAsistenciaDetalles() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteAsistenciaDetalles.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteAsistenciaDetalles newInstance(String param1, String param2, String param3, String param4, Context context) {
        ReporteAsistenciaDetalles fragment = new ReporteAsistenciaDetalles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            Log.d("-->>Detalles Datos", mParam1 + ", " + mParam2 + ", " + mParam3);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;

        primeraPeticion();
        // TODO: Casteo
        tvFecha = (TextView) rootView.findViewById(R.id.ggfrasd_tv_fecha);
        tvLetra = (TextView) rootView.findViewById(R.id.ggfrasd_tv_letra);
        tvNombreAsesor = (TextView) rootView.findViewById(R.id.ggfrasd_tv_nombre_asesor);
        tvNombreAsesor.setText(mParam4);
        tvNoEmpleado = (TextView) rootView.findViewById(R.id.ggfrasd_tv_numero_empleado_asesor);
        tvNoEmpleado.setText("Numero de empleado asesor: " + mParam1);
        tvAtiempo = (TextView) rootView.findViewById(R.id.ggfrasd_tv_a_tiempo);
        tvRetardo = (TextView) rootView.findViewById(R.id.ggfrasd_tv_retardados);
        tvSinAsistencia = (TextView) rootView.findViewById(R.id.ggfrasd_tv_sin_asistencia);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.ggfrasd_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.ggfrasd_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.ggfrasd_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.ggfrasd_tv_resultados);

        // TODO: fecha
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        connected = new Connected();

        fechas();

        // TODO: fechas dialog
        rangoInicial();
        rangoFinal();

        // TODO: model
        getDatos1 = new ArrayList<>();
        // TODO Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ggfrasd_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // TODO: webservice
        //sendJson(true);

        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    final String fechaIncial = tvRangoFecha1.getText().toString();
                    final String fechaFinal = tvRangoFecha2.getText().toString();

                    if(fechaIncial.isEmpty() || fechaFinal.isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        ReporteAsistenciaDetalles fragmento = ReporteAsistenciaDetalles.newInstance(mParam1, fechaIncial, fechaFinal, mParam4, rootView.getContext());
                        borrar.onDestroy();
                        ft.remove(borrar);
                        ft.replace(R.id.content_gerente, fragmento);
                        ft.addToBackStack(null);
                        ft.commit();
                    }
                    // TODO: ocultar teclado
                    //imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
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

                                try {
                                    JSONObject rqt = new JSONObject();
                                    rqt.put("correo", email);
                                    rqt.put("detalle", true);
                                    rqt.put("numeroEmpleado", mParam1);
                                    JSONObject periodo = new JSONObject();
                                    periodo.put("fechaFin", mParam3);
                                    periodo.put("fechaInicio", mParam2);
                                    rqt.put("periodo", periodo);
                                    obj.put("rqt", rqt);
                                    Log.d("-->>>>datos Email array", "REQUEST-->" + obj);
                                } catch (JSONException e) {
                                    Config.msj(getContext(), "Error", "Error al formar los datos");
                                }
                                EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_ASISTENCIA_DETALLE,getContext(),new EnviaMail.VolleyCallback() {

                                    @Override
                                    public void onSuccess(JSONObject result) {
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
        return inflater.inflate(R.layout.gerente_fragmento_reporte_asistencia_detalles, container, false);
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

        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> usuario = sessionManager.getUserDetails();
        String numeroEmpleado = mParam1;

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            if(getArguments() != null){
                rqt.put("numeroEmpleado", mParam1);
                rqt.put("pagina", pagina);
                periodo.put("fechaInicio", mParam2);
                periodo.put("fechaFin", mParam3);
                rqt.put("periodo", periodo);
                obj.put("rqt", rqt);
            }else{
                Map<String, String> fecha = Config.fechas(1);
                String param1 = fecha.get("fechaIni");
                String param2 = fecha.get("fechaFin");
                rqt.put("numeroEmpleado", mParam1);
                rqt.put("pagina", pagina);
                periodo.put("fechaInicio", param1);
                periodo.put("fechaFin", param2);
                rqt.put("periodo", periodo);
                obj.put("rqt", rqt);
            }

            Log.d("-->>>>Req", "PETICION VACIA-->" + obj);
        }catch (JSONException e){
            e.printStackTrace();
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_ASISTENCIA_DETALLE, obj,
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

    private void primerPaso(JSONObject obj) {
        Log.d("-->>RQT PRIMER", " primerPaso" + obj.toString());
        int onTime = 0;
        int retardo = 0;
        int inasistencia = 0;
        int totalFilas = 0;

        try{
            JSONObject asistencia = obj.getJSONObject("asistencia");
            onTime = asistencia.getInt("onTime");
            retardo = asistencia.getInt("retardo");
            inasistencia = asistencia.getInt("inasistencia");
            totalFilas = obj.getInt("filasTotal");
            JSONArray registroHorario = obj.getJSONArray("RegistroHorario");
            for(int i = 0; i < registroHorario.length(); i++){
                GerenteReporteAsistenciaDetalleModel getDatos2 = new GerenteReporteAsistenciaDetalleModel();
                JSONObject json = null;
                try{
                    json = registroHorario.getJSONObject(i);
                    JSONObject comida = json.getJSONObject("comida");
                    getDatos2.setComidaLatitud(comida.getString("latitud"));
                    getDatos2.setComidaLongitud(comida.getString("longitud"));
                    getDatos2.setComidaHora(comida.getString("horaEntrada"));
                    getDatos2.setComidaSalida(comida.getString("horaSalida"));

                    JSONObject entrada = json.getJSONObject("entrada");
                    getDatos2.setEntradaHora(entrada.getString("hora"));
                    getDatos2.setEntradaLatitud(entrada.getString("latitud"));
                    getDatos2.setEntradaLongitud(entrada.getString("longitud"));

                    JSONObject salida = json.getJSONObject("salida");
                    getDatos2.setSalidaHora(salida.getString("hora"));
                    getDatos2.setSalidaLatitud(salida.getString("latitud"));
                    getDatos2.setSalidaLongitud(salida.getString("longitud"));

                    getDatos2.setFechaAsistencia(json.getString("fecha"));

                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        } catch (JSONException e){
            e.printStackTrace();
        }
        tvResultados.setText("" + totalFilas);
        tvAtiempo.setText("" + onTime);
        tvRetardo.setText("" + retardo);
        tvSinAsistencia.setText("" + inasistencia);
        tvResultados.setText("" + totalFilas + " registros ");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        adapter = new GerenteReporteAsistenciaDetalleAdapter(rootView.getContext(), getDatos1, recyclerView);
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
        Log.d("-->>RQT SEGUNDO", " segundoPaso" + obj.toString());
        try{
            JSONObject asistencia = obj.getJSONObject("asistencia");
            filas = obj.getInt("filasTotal");
            JSONArray registroHorario = obj.getJSONArray("RegistroHorario");
            for(int i = 0; i < registroHorario.length(); i++){
                GerenteReporteAsistenciaDetalleModel getDatos2 = new GerenteReporteAsistenciaDetalleModel();
                JSONObject json = null;
                try{
                    json = registroHorario.getJSONObject(i);
                    JSONObject comida = json.getJSONObject("comida");
                    getDatos2.setComidaLatitud(comida.getString("latitud"));
                    getDatos2.setComidaLongitud(comida.getString("longitud"));
                    getDatos2.setComidaHora(comida.getString("horaEntrada"));
                    getDatos2.setComidaSalida(comida.getString("horaSalida"));

                    JSONObject entrada = json.getJSONObject("entrada");
                    getDatos2.setEntradaHora(entrada.getString("hora"));
                    getDatos2.setEntradaLatitud(entrada.getString("latitud"));
                    getDatos2.setEntradaLongitud(entrada.getString("longitud"));

                    JSONObject salida = json.getJSONObject("salida");
                    getDatos2.setSalidaHora(salida.getString("hora"));
                    getDatos2.setSalidaLatitud(salida.getString("latitud"));
                    getDatos2.setSalidaLongitud(salida.getString("longitud"));

                    getDatos2.setFechaAsistencia(json.getString("fecha"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        } catch (JSONException e){
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
        Map<String, String> fechaActual = Config.fechas(1);
        final String smParam1 = fechaActual.get("fechaIni");
        final String smParam2 = fechaActual.get("fechaFin");
        if(getArguments() != null){
            tvFecha.setText(mParam2 + " - " + mParam3);
            tvRangoFecha1.setText(mParam2);
            tvRangoFecha2.setText(mParam3);
        }else{
            tvFecha.setText(smParam1);
        }
    }
}