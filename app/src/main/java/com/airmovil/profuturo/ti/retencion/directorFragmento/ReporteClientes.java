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
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Spinner spinnerId, spinnerSucursal, spinnerRetenido, spinnerCita;
    private EditText etIngresarDato, etIngresarAsesor;
    private Button btnBuscar;
    private TextView tvResultados;
    private TextView tvRangoFecha1, tvRangoFecha2, tvFecha;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;

    // TODO: variable
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
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
    public static ReporteClientes newInstance(String param1, String param2, Context context) {
        ReporteClientes fragment = new ReporteClientes();
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
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> datos = sessionManager.getUserDetails();
        // CASTEO DE ELEMENTOS
        spinnerId = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_id);
        spinnerSucursal = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_sucursal);
        spinnerRetenido = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_estado);
        spinnerCita = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_citas);
        tvFecha = (TextView) view.findViewById(R.id.ddfrc_tv_fecha);
        tvRangoFecha1  = (TextView) view.findViewById(R.id.ddfrc_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) view.findViewById(R.id.ddfrc_tv_fecha_rango2);
        btnBuscar = (Button) view.findViewById(R.id.ddfrc_btn_buscar);
        tvResultados = (TextView) view.findViewById(R.id.ddfrc_tv_registros);


        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        if(getArguments() != null){
            fechaIni = getArguments().getString(ARG_PARAM1);
            fechaFin = getArguments().getString(ARG_PARAM2);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            //fechaIni = getArguments().getString("fechaIni", "");
            //fechaFin = getArguments().getString("fechaFin", "");
            Log.d("ARG onCreateView","A1: "+fechaIni +" A2: "+fechaIni);
            tvFecha.setText(fechaIni + " - " + fechaFin);
            //Log.d("FECHA","SI");
        }else {
            Map<String, String> fechas = Config.fechas(1);
            fechaFin = fechas.get("fechaFin");
            fechaIni = fechas.get("fechaIni");
            fechaMostrar = fechaIni;
            tvFecha.setText(fechaMostrar);
            Log.d("FECHA","NO");
        }

        // TODO: Spinner
        ArrayAdapter<String> adapterId = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
        adapterId.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerId.setAdapter(adapterId);
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

        sendJson(true);
        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String f1 = tvRangoFecha1.getText().toString();
                String f2 = tvRangoFecha2.getText().toString();

                FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                ReporteClientes procesoDatosFiltroInicio = ReporteClientes.newInstance(
                        (fechaIni.equals("") ? "" : fechaIni),
                        (fechaFin.equals("") ? "" : fechaFin),
                        rootView.getContext()
                );
                borrar.onDestroy();
                ft.remove(borrar);
                ft.replace(R.id.content_asesor, procesoDatosFiltroInicio);
                ft.addToBackStack(null);


                ft.commit();

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

    private void sendJson(final boolean primeraPeticion){

        final ProgressDialog loading;
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;


        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        try{
            JSONObject periodo = new JSONObject();
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", fechaIni);
            periodo.put("fechaFin", fechaFin);
            rqt.put("usuario", numeroUsuario);
            json.put("rqt", rqt);
            Log.d("sendJson", " REQUEST -->" + json);

        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_CLIENTES, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
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
                        loading.dismiss();
                        Config.msj(getContext(),"Error", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj){
        Log.d("primer paso", "Response: "  + obj );
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("Cliente");
            filas = obj.getInt("filasTotal");
            totalFilas = obj.getInt("filasTotal");
            Log.d("primerPaso", "response -->" + filas + totalFilas);
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
}
