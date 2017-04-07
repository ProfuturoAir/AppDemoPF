package com.airmovil.profuturo.ti.retencion.directorFragmento;

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

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteAsistenciaDetalleAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaDetalleModel;
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
    private static final String ARG_PARAM1 = "parametro1";
    private static final String ARG_PARAM2 = "parametro2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;

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
    private List<DirectorReporteAsistenciaDetalleModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private DirectorReporteAsistenciaDetalleAdapter adapter;

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
    public static ReporteAsistenciaDetalles newInstance(String param1, String param2, Context context) {
        ReporteAsistenciaDetalles fragment = new ReporteAsistenciaDetalles();
        Bundle args = new Bundle();
        args.putString("parametro1", param1);
        args.putString("parametro2", param2);
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
        tvFecha = (TextView) rootView.findViewById(R.id.ddfrasd_tv_fecha);
        tvLetra = (TextView) rootView.findViewById(R.id.ddfrasd_tv_letra);
        tvNombreAsesor = (TextView) rootView.findViewById(R.id.ddfrasd_tv_nombre_asesor);
        tvNoEmpleado = (TextView) rootView.findViewById(R.id.ddfrasd_tv_numero_empleado_asesor);
        tvRangoFechas = (TextView) rootView.findViewById(R.id.ddfrasd_tv_rango_fechas);
        tvAtiempo = (TextView) rootView.findViewById(R.id.ddfrasd_tv_a_tiempo);
        tvRetardo = (TextView) rootView.findViewById(R.id.ddfrasd_tv_retardados);
        tvSinAsistencia = (TextView) rootView.findViewById(R.id.ddfrasd_tv_sin_asistencia);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.ddfrasd_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.ddfrasd_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.ddfrasd_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.ddfrasd_tv_resultados);

        // TODO: fecha
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");
        Map<String, String> fechas = Config.fechas(1);
        String fechaMuestra = fechas.get("fechaIni");
        try{
            fechaIni = getArguments().getString(ARG_PARAM1);
            fechaFin = getArguments().getString(ARG_PARAM2);
            if(getArguments() != null){
                Log.d("getArguments","fechas: " + fechaIni + " " + fechaFin);
                if(fechaIni == null && fechaFin == null){
                    Log.d("datos Vacios", "FechaInicio" + fechaIni);
                    tvFecha.setText(fechaMuestra);
                }else if(fechaIni != null && fechaFin != null){
                    tvFecha.setText(fechaIni + "  " + fechaFin);
                }else{
                    tvFecha.setText(fechaMuestra);
                }
            }else if(fechaIni == "" && fechaFin == ""){
                tvFecha.setText(fechaMuestra);
                Log.d("vacios getParams","fechas: " + fechaIni + " " + fechaFin);
            }else{
                Log.d("else getParams","fechas: " + fechaIni + " " + fechaFin);
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        // TODO: fechas dialog
        rangoInicial();
        rangoFinal();

        // TODO: model
        getDatos1 = new ArrayList<>();
        // TODO Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfrasd_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        // TODO: webservice
        sendJson(true);

        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fechaIncial = tvRangoFecha1.getText().toString();
                final String fechaFinal = tvRangoFecha2.getText().toString();


                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ReporteAsistenciaDetalles fragmento = ReporteAsistenciaDetalles.newInstance(fechaIncial,fechaFinal,rootView.getContext());
                    borrar.onDestroy();
                    ft.remove(borrar);
                    ft.replace(R.id.content_director, fragmento);
                    ft.addToBackStack(null);
                    ft.commit();
                }else{
                    android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                    dlgAlert.setTitle("Error de conexión");
                    dlgAlert.setMessage("Se ha encontrado un problema, debes revisar tu conexión a internet");
                    dlgAlert.setCancelable(true);
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
                }
            }
        });

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

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.director_fragmento_reporte_asistencia_detalles, container, false);
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
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();

        try{
            JSONObject rqt = new JSONObject();
            JSONObject filtros = new JSONObject();
            JSONObject periodo = new JSONObject();
            filtros.put("curp", "");
            filtros.put("nss", "");
            filtros.put("numeroCuenta", "");
            rqt.put("filtro", filtros);
            rqt.put("idTramite", 1);
            periodo.put("fechaInicio", fechaIni);
            periodo.put("fechaFin", fechaFin);
            rqt.put("periodo", periodo);
            rqt.put("usuario", "");
            obj.put("rqt", rqt);
            Log.d("", "PETICION VACIA-->" + obj);
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
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj) {
        //Log.d("RQT", " primerPaso" + obj.toString());
        int onTime = 0;
        int retardo = 0;
        int inasistencia = 0;
        int totalFilas = 0;

        try{
            JSONObject asistencia = obj.getJSONObject("asisitencia");
            onTime = asistencia.getInt("onTime");
            retardo = asistencia.getInt("retardo");
            inasistencia = asistencia.getInt("inasistencia");
            filas = obj.getInt("filasTotal");
            JSONArray registroHorario = obj.getJSONArray("RegistroHorario");
            for(int i = 0; i < registroHorario.length(); i++){
                DirectorReporteAsistenciaDetalleModel getDatos2 = new DirectorReporteAsistenciaDetalleModel();
                JSONObject json = null;
                try{
                    json = registroHorario.getJSONObject(i);
                    JSONObject comida = json.getJSONObject("comida");
                    getDatos2.setFechaAsistencia(comida.getString("Fecha"));
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

        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        adapter = new DirectorReporteAsistenciaDetalleAdapter(rootView.getContext(), getDatos1, recyclerView);
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
            JSONObject asistencia = obj.getJSONObject("asisitencia");
            filas = obj.getInt("filasTotal");
            JSONArray registroHorario = obj.getJSONArray("RegistroHorario");
            for(int i = 0; i < registroHorario.length(); i++){
                DirectorReporteAsistenciaDetalleModel getDatos2 = new DirectorReporteAsistenciaDetalleModel();
                JSONObject json = null;
                try{
                    json = registroHorario.getJSONObject(i);
                    getDatos2.setFechaAsistencia(json.getString("fecha"));
                    JSONObject comida = json.getJSONObject("comida");
                    getDatos2.setComidaHora(comida.getString(""));
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
}
