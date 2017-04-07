package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.InputType;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.GerenteSinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.SinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.model.GerenteSinCitaModel;
import com.airmovil.profuturo.ti.retencion.model.SinCitaModel;
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
 * {@link SinCita.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link SinCita#newInstance} factory method to
 * create an instance of this fragment.
 */
public class SinCita extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = SinCita.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] ESTADOS_CITAS = new String[]{"Selecciona...","Número de cuenta", "NSS", "CURP"};

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private InputMethodManager imm;

    // TODO: XML
    private Spinner spinner;
    private Button btnBuscar;
    private TextView tvFecha, tvRegistros;
    private EditText etDatos;
    private View rootView;
    private LinearLayout ll;
    private TextView tvNombre, tvCuenta, tvLetra;

    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaMostrar = "";
    private String fechaFin = "";
    private int posicion;

    private GerenteSinCitaAdapter adapter;
    private List<GerenteSinCitaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;

    private OnFragmentInteractionListener mListener;

    public SinCita() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SinCitaModel.
     */
    // TODO: Rename and change types and number of parameters
    public static SinCita newInstance(String param1, String param2) {
        SinCita fragment = new SinCita();
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
        // TODO: Casteo
        rootView = view;

        tvFecha = (TextView) rootView.findViewById(R.id.gfsc_tv_fecha);
        spinner = (Spinner) rootView.findViewById(R.id.gfsc_spinner_tipo_dato);
        etDatos = (EditText) rootView.findViewById(R.id.gfsc_et_datos);
        tvRegistros = (TextView) rootView.findViewById(R.id.gfsc_tv_registros);
        btnBuscar = (Button) rootView.findViewById(R.id.gfsc_btn_buscar);

        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);

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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, ESTADOS_CITAS);
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                etDatos.setText("");

                switch (position){
                    case 0:
                        etDatos.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        //imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark1), PorterDuff.Mode.OVERLAY);
                        etDatos.setFocusable(true);
                        break;
                    case 1:
                        etDatos.setFocusableInTouchMode(true);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case 2:
                        etDatos.setFocusableInTouchMode(true);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        break;
                    case 3:
                        etDatos.setFocusableInTouchMode(true);
                        //imm.toggleSoftInput(InputMethodManager.SHOW_IMPLICIT, 0);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                }
                etDatos.setHint("Ingresa, " + adapter.getItem(position));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(adapter);

        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    String valores = etDatos.getText().toString().trim();
                    String seleccion = spinner.getSelectedItem().toString();
                    Log.d(TAG, "Seleccion: --->" + seleccion);

                    if(seleccion.equals("Selecciona...")) {
                        Config.msj(getContext(), "Error", "Debes seleccionar el tipo de campo a solicitar");
                    }else{
                        if(valores.isEmpty()) {
                            switch (seleccion){
                                case "Número de cuenta":
                                    Config.msj(getContext(), "Error", "Debes ingresar un Número de cuenta ");
                                    break;
                                case "NSS":
                                    Config.msj(getContext(), "Error", "Debes ingresar un NSS");
                                    break;
                                case "CURP":
                                    Config.msj(getContext(), "Error", "Debes ingresar un CURP");
                                    break;
                            }
                        }else{
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            SinCita clase = SinCita.newInstance(
                                    valores, rootView.getContext()
                            );

                            borrar.onDestroy();
                            ft.remove(borrar);
                            ft.replace(R.id.content_gerente, clase);
                            ft.addToBackStack(null);
                            etDatos.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                            imm.toggleSoftInput(InputMethodManager.HIDE_IMPLICIT_ONLY, 0);

                            sendJson(true, seleccion, valores);
                        }
                    }
                }else{
                    Config.msj(getContext(), "Error en conexión", "Por favor, revisa tu conexión a internet");
                }


            }
        });
        // TODO: model
        getDatos1 = new ArrayList<>();
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.gfsc_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
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
                    Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.asesorFragmento.SinCita();
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    if (fragmentoGenerico != null) {
                        fragmentManager
                                .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                .replace(R.id.content_gerente, fragmentoGenerico).commit();
                    }
                    return true;
                }
                return false;
            }
        });
    }

    public static SinCita newInstance(String campo, Context ctx){
        SinCita clase = new SinCita();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, campo);
        clase.setArguments(args);
        return clase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gerente_fragmento_sin_cita, container, false);
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
    private void sendJson(final boolean primerPeticion, String seleccion, String valores) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        Log.d(TAG + "DATOS--> JSON", "\nSpinner: " + seleccion + "\nCampo a enviar: " + valores);
        JSONObject obj = new JSONObject();
        try {
            // TODO: Formacion del JSON request
            JSONObject rqt = new JSONObject();
            JSONObject filtros = new JSONObject();

            switch (seleccion){
                case "Número de cuenta":
                    filtros.put("curp", "");
                    filtros.put("nss", "");
                    filtros.put("numeroCuenta", valores.toString());
                    break;
                case "NSS":
                    filtros.put("curp", "");
                    filtros.put("nss",  valores.toString());
                    filtros.put("numeroCuenta","");
                    break;
                case "CURP":
                    filtros.put("curp", valores.toString());
                    filtros.put("nss",  "");
                    filtros.put("numeroCuenta","");
                    break;
            }
            rqt.put("filtro", filtros);
            rqt.put("pagina", "1");
            SessionManager sessionManager = new SessionManager(getActivity().getApplicationContext());
            HashMap<String, String> usuario = sessionManager.getUserDetails();
            String usuarioCUSP = usuario.get(SessionManager.ID);

            rqt.put("usuario", usuarioCUSP.toString());
            obj.put("rqt", rqt);
            Log.d(TAG, "PETICION VACIA-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","1 Lo sentimos ocurrio un error al formar los datos.");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_CLIENTE_SIN_CITA, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        //Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
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
        Log.d(TAG, "-----> >" + obj);
        String nombreCliente = "";
        String numeroCuenta = "";
        String status = "";
        String statusText ="";
        try{
            status = obj.getString("status");
            if(Integer.parseInt(status) == 200){
                JSONArray array = obj.getJSONArray("clientes");
                for (int x = 0; x < array.length(); x++){
                    GerenteSinCitaModel getDatos2 = new GerenteSinCitaModel();
                    JSONObject json = null;
                    try{
                        json = array.getJSONObject(x);
                        getDatos2.setClienteNombre(json.getString("nombreCliente"));
                        getDatos2.setClienteCuenta(json.getString("numeroCuenta"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    getDatos1.add(getDatos2);
                }
                Log.d(TAG, "JSON response : ->" + array);
            }else{
                statusText = obj.getString("statusText");
                Config.msj(getContext(), "Error", statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter = new GerenteSinCitaAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


    }


}
