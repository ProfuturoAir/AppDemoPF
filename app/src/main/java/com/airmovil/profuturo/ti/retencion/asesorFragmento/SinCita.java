package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.SinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
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
    private static final String ARG_PARAM1 = "param1";// tipo dato
    private static final String ARG_PARAM2 = "param2";// dato a ingresar

    // TODO: Rename and change types of parameters
    private int mParam1; // tipo dato
    private String mParam2; // dato a ingresar

    private OnFragmentInteractionListener mListener;

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

    // TODO: Recycler
    private SinCitaAdapter adapter;
    private List<SinCitaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private Connected connected;
    final Fragment borrar = this;
    private int pagina = 1;

    public SinCita() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment SinCita.
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
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: Casteo
        rootView = view;
        variables();
        fechas();
        connected = new Connected();


        // TODO: Spinner
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
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
                        Config.teclado(getContext(), etDatos);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case 2:
                        etDatos.setFocusableInTouchMode(true);
                        Config.teclado(getContext(), etDatos);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        break;
                    case 3:
                        etDatos.setFocusableInTouchMode(true);
                        Config.teclado(getContext(), etDatos);
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

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String valores = etDatos.getText().toString().trim();
                int seleccion = spinner.getSelectedItemPosition();

                if(connected.estaConectado(getContext())) {
                    if (seleccion == 0) {
                        Config.dialogoSpinnerSinSeleccion(getContext());
                    } else {
                        if (valores.isEmpty()) {
                            switch (seleccion) {
                                case 1:
                                    Config.dialogoSinSeleccionSpinner(getContext(), "Número de cuenta");
                                    break;
                                case 2:
                                    Config.dialogoSinSeleccionSpinner(getContext(), "NSS");
                                    break;
                                case 3:
                                    Config.dialogoSinSeleccionSpinner(getContext(), "CURP");
                                    break;
                            }
                        } else {
                            sendJson(true);
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            SinCita fragmento = SinCita.newInstance(seleccion, valores , rootView.getContext());
                            borrar.onDestroy();
                            ft.remove(borrar);
                            ft.replace(R.id.content_asesor, fragmento);
                            ft.addToBackStack(null);
                            ft.commit();
                            Config.teclado(getContext(), etDatos);
                        }
                    }
                }else{
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_sin_wifi));
                    progressDialog.setTitle(getResources().getString(R.string.error_conexion));
                    progressDialog.setMessage(getResources().getString(R.string.msj_error_conexion));
                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.dismiss();
                                    //sendJson(true);
                                }
                            });
                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelar),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    progressDialog.show();
                }

            }
        });

        // TODO: model
        getDatos1 = new ArrayList<>();
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_busqueda_elemento);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
    }

    public static SinCita newInstance(int tipoCampo, String campo, Context ctx){
        SinCita clase = new SinCita();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, tipoCampo);
        args.putString(ARG_PARAM2, campo);
        clase.setArguments(args);
        return clase;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_sin_cita, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {

                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_sin_wifi));
                    progressDialog.setTitle(getResources().getString(R.string.error_conexion));
                    progressDialog.setMessage(getResources().getString(R.string.msj_error_conexion));
                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.dismiss();
                                    Fragment fragmentoGenerico = new SinCita();
                                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                                }
                            });
                    progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelar),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                    progressDialog.show();
                    return true;
                }
                return false;
            }
        });
    }

    private void variables(){
        tvFecha = (TextView) rootView.findViewById(R.id.afsc_tv_fecha);
        spinner = (Spinner) rootView.findViewById(R.id.afsc_spinner_tipo_dato);
        etDatos = (EditText) rootView.findViewById(R.id.afsc_et_datos);
        tvRegistros = (TextView) rootView.findViewById(R.id.afsc_tv_registros);
        btnBuscar = (Button) rootView.findViewById(R.id.afsc_btn_buscar);
    }

    private void fechas(){
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");


        Map<String, String> fechas = Config.fechas(1);
        fechaFin = fechas.get("fechaFin");
        fechaIni = fechas.get("fechaIni");
        fechaMostrar = fechaIni;
        tvFecha.setText(fechaIni);

    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> datosUsuario = sessionManager.getUserDetails();
        String usuario = datosUsuario.get(SessionManager.USER_ID);

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtro = new JSONObject();
        try{
            if(getArguments() != null){
                mParam1 = getArguments().getInt(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);

                switch (mParam1){
                    case 1:
                        filtro.put("curp", "");
                        filtro.put("nss", "");
                        filtro.put("numeroCuenta", mParam2);
                        break;
                    case 2:
                        filtro.put("curp", "");
                        filtro.put("nss", mParam2);
                        filtro.put("numeroCuenta", "");
                        break;
                    case 3:
                        filtro.put("curp", mParam2);
                        filtro.put("nss", "");
                        filtro.put("numeroCuenta", "");
                        break;
                    default:
                        filtro.put("curp", "");
                        filtro.put("nss", "");
                        filtro.put("numeroCuenta", "");
                        break;
                }
                rqt.put("filtro", filtro);
                rqt.put("pagina", pagina);
                rqt.put("usuario", usuario);
                obj.put("rqt",rqt);
                Log.d("RQT SINCITA: ", "" + obj);
            }

        }catch (JSONException e){
            e.printStackTrace();
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_CLIENTE_SIN_CITA, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            //loading.dismiss();
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        //loading.dismiss();
                        //Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un right_in, puedes intentar revisando tu conexión.");
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
                    SinCitaModel getDatos2 = new SinCitaModel();
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
            }else{
                statusText = obj.getString("statusText");
                Config.msj(getContext(), "Error", statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter = new SinCitaAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
