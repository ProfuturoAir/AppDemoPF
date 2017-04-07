package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.CitasClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.CitasClientesModel;
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
 * {@link ConCita.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ConCita#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ConCita extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    private static final String TAG = ConCita.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: paramentros
    private String mParam1;
    private String mParam2;
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private static final String[] ESTADOS_CITAS = new String[]{"Atendido", "Sin Atender", "Ambos"};

    private List<CitasClientesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private String JSON_HORA = "hora";
    private String JSON_NOMBRE_CLIENTE = "nombreCliente";
    private String JSON_NUMERO_CUENTA = "numeroCuenta";
    private String JSON_FILAS_TOTAL = "filasTotal";
    private int filas;
    private CitasClientesAdapter adapter;
    private int pagina = 1;
    private int numeroMaximoPaginas = 0;
    private int totalF;

    private OnFragmentInteractionListener mListener;

    // TODO: XML
    private Spinner spinner;
    private Button btnAplicar, btnClienteSinCita;
    private TextView tvFecha, tvRegistros;
    private View rootView;

    public ConCita() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ConCita.
     */
    // TODO: Rename and change types and number of parameters
    public static ConCita newInstance(String param1, String param2) {
        ConCita fragment = new ConCita();
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
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Casteo
        rootView = view;
        btnAplicar = (Button) rootView.findViewById(R.id.afcc_btn_aplicar);
        btnClienteSinCita = (Button) rootView.findViewById(R.id.afcc_btn_sin_cita);
        tvFecha = (TextView) rootView.findViewById(R.id.afcc_tv_fecha);
        spinner = (Spinner) rootView.findViewById(R.id.afcc_spinner_estados);
        tvRegistros = (TextView) rootView.findViewById(R.id.afcc_tv_registros);

        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        if(getArguments() != null){
            fechaIni = getArguments().getString(ARG_PARAM1);
            fechaFin = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(fechaIni+" - "+fechaFin);
        }else {
            Map<String, String> fechas = Config.fechas(1);
            fechaFin = fechas.get("fechaFin");
            fechaIni = fechas.get("fechaIni");
            fechaMostrar = fechaIni;
            tvFecha.setText(fechaMostrar);
        }

        // TODO: Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, ESTADOS_CITAS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setAdapter(adapter);

        // TODO: modelos
        getDatos1 = new ArrayList<>();
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_citas);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        sendJson(true);

        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentoGenerico = new ConCita();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left
                        .replace(R.id.content_asesor, fragmentoGenerico).commit();
            }
        });

        btnClienteSinCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Fragment fragmentoGenerico = new SinCita();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager
                        .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left
                        .replace(R.id.content_asesor, fragmentoGenerico).commit();
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_con_cita, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    Fragment fragmentoGenerico = new Inicio();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                    return true;
                }
                return false;
            }
        });
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


        JSONObject jsonobject = new JSONObject();
        JSONObject obj = new JSONObject();
        try {
            // TODO: Formacion del JSON request

            JSONObject rqt = new JSONObject();
            rqt.put("estatusCita", "1");
            rqt.put("pagina", pagina);
            rqt.put("usuario", "USUARIO");
            obj.put("rqt", rqt);
            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_RESUMEN_CITAS, obj,
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
                        loading.dismiss();
                        Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
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
        //Log.d(TAG + "-->", obj.toString());
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("citas");
            //Log.d(TAG + "-Citas->", array.toString());
            filas = obj.getInt(JSON_FILAS_TOTAL);
            totalFilas = obj.getInt(JSON_FILAS_TOTAL);
            Log.d(TAG, "*******->" + filas + totalFilas);
            for(int i = 0; i < array.length(); i++){
                CitasClientesModel getDatos2 = new CitasClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setHora(json.getString(JSON_HORA));
                    getDatos2.setNombreCliente(json.getString(JSON_NOMBRE_CLIENTE));
                    getDatos2.setNumeroCuenta(json.getString(JSON_NUMERO_CUENTA));
                    //Log.d(TAG + "* LISTA * ", String.valueOf(json));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvRegistros.setText(filas + " Registros");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);

        Log.d("numeroMaximoPaginas", ""+numeroMaximoPaginas);
        adapter = new CitasClientesAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

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
            JSONArray array = obj.getJSONArray("citas");
            //Log.d(TAG + "-Citas->", array.toString());
            for(int i = 0; i < array.length(); i++){
                CitasClientesModel getDatos2 = new CitasClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setHora(json.getString(JSON_HORA));
                    getDatos2.setNombreCliente(json.getString(JSON_NOMBRE_CLIENTE));
                    getDatos2.setNumeroCuenta(json.getString(JSON_NUMERO_CUENTA));
                    //Log.d(TAG + "* LISTA * ", String.valueOf(json));
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
}
