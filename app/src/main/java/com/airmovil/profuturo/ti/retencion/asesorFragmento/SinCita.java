package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
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
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.Adapter.GerenteSinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.SinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.*;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
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

public class SinCita extends Fragment {
    /* incializacion de los parametros del fragmento */
    public static final String TAG = SinCita.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] ESTADOS_CITAS = new String[]{"Selecciona...","Número de cuenta", "NSS", "CURP"};

    // TODO: Rename and change types of parameters
    private InputMethodManager imm;

    // TODO: XML
    private Spinner spinner;
    private Button btnBuscar;
    private TextView tvFecha;
    private EditText etDatos;
    private View rootView;
    private String fechaIni = "";
    private String fechaMostrar = "";
    private SinCitaAdapter adapter;
    private List<SinCitaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private OnFragmentInteractionListener mListener;
    SharedPreferences sharedPreferences;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    public SinCita() {
        /* constructor vacio es requerido */
    }

    /**
     * al crear una nueva instancia recibe como paramentros
     * @param param1 Parametro 1.
     * @param param2 Parametro 2.
     * @return un objeto AsistenciaEntrada.
     */
    public static SinCita newInstance(int param1, String param2) {
        SinCita fragment = new SinCita();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Casteo
        rootView = view;
        sharedPreferences = getActivity().getSharedPreferences("Login", Context.MODE_PRIVATE);
        tvFecha = (TextView) rootView.findViewById(R.id.afsc_tv_fecha);
        spinner = (Spinner) rootView.findViewById(R.id.afsc_spinner_tipo_dato);
        etDatos = (EditText) rootView.findViewById(R.id.afsc_et_datos);
        btnBuscar = (Button) rootView.findViewById(R.id.afsc_btn_buscar);
        imm = (InputMethodManager) getActivity().getSystemService(getActivity().INPUT_METHOD_SERVICE);
        Map<String, String> fechas = Config.fechas(1);
        fechaIni = fechas.get("fechaIni");
        fechaMostrar = fechaIni;
        int llave = 0;
        String valor = "";
        boolean procesa = false;
        if(getArguments() != null){
            llave = getArguments().getInt(ARG_PARAM1);
            valor = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(fechaMostrar);
            procesa = true;
        }else {
            tvFecha.setText(fechaMostrar);
        }
        etDatos.setFocusable(false);
        etDatos.setFocusableInTouchMode(false);
        if(procesa)
            sendJson(true, llave, valor);
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
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryDark1), PorterDuff.Mode.OVERLAY);
                        etDatos.setFocusable(true);
                        etDatos.setFocusable(false);
                        etDatos.setFocusableInTouchMode(false);
                        break;
                    case 1:
                        etDatos.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_CLASS_PHONE);
                        etDatos.setFocusable(true);
                        etDatos.setFocusableInTouchMode(true);
                        break;
                    case 2:
                        etDatos.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        etDatos.setFocusable(true);
                        etDatos.setFocusableInTouchMode(true);
                        break;
                    case 3:
                        etDatos.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatos.getBackground().setColorFilter(getResources().getColor(R.color.colorPrimaryLight), PorterDuff.Mode.LIGHTEN);
                        etDatos.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        etDatos.setFocusable(true);
                        etDatos.setFocusableInTouchMode(true);
                }
                etDatos.setHint("Ingresa, " + adapter.getItem(position));
            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {}
        });
        spinner.setAdapter(adapter);
        final Fragment borrar = this;
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    String valores = etDatos.getText().toString().trim();
                    int seleccion = spinner.getSelectedItemPosition();
                    Log.d(TAG, "Seleccion: --->" + seleccion);
                    if(spinner.getSelectedItem().toString().equals("Selecciona...")) {
                        Config.dialogoDatosVacios(getContext());
                    }else{
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            SinCita clase = SinCita.newInstance(
                                    seleccion, valores
                            );
                            Config.teclado(getContext(), etDatos);
                            borrar.onDestroy();
                            ft.remove(borrar);
                            ft.replace(R.id.content_asesor, clase);
                            ft.addToBackStack(null);
                            ft.commit();
                    }
                }else{
                    try {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_sin_wifi));
                        progressDialog.setTitle(getResources().getString(R.string.error_conexion));
                        progressDialog.setMessage(getResources().getString(R.string.msj_error_conexion_firma));
                        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.dismiss();
                                    }
                                });
                        progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelar),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                });
                        progressDialog.show();
                    }catch (Exception e){
                        Toast.makeText(getContext(), "Error", Toast.LENGTH_SHORT).show();
                    }
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

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Confirmar");
                    dialogo1.setMessage("Serás direccionado al Inicio");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new Inicio();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction()
                                        .replace(R.id.content_asesor, fragmentoGenerico).commit();
                            }
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    dialogo1.show();
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
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_sin_cita, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            Toast.makeText(getContext(), "if", Toast.LENGTH_SHORT).show();
            mListener.onFragmentInteraction(uri);
        }else{
            Toast.makeText(getContext(), "else", Toast.LENGTH_SHORT).show();
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
     * esta clase debe ser implementada en las actividades que contengan
     * fragmentos para que exista la comunicacion entre los fragmentos
     * para mas informacion ver http://developer.android.com/training/basics/fragments/communicating.html
     * Comunicaciòn entre fragmentos
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     *  metodo para callback de volley
     */
    void initVolleyCallback() {

        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                loading.dismiss();
                primerPaso(response);
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        };
    }

    private void sendJson(final boolean primerPeticion, int seleccion, String valores) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        Log.d(TAG + "DATOS--> JSON", "\nSpinner: " + seleccion + "\nCampo a enviar: " + valores);
        JSONObject obj = new JSONObject();
        try {
            // TODO: Formacion del JSON request
            JSONObject rqt = new JSONObject();
            rqt.put("filtro", Config.filtroClientes(seleccion, valores));
            rqt.put("pagina", "1");
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "PETICION VACIA-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","1 Lo sentimos ocurrio un right_in al formar los datos.");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CONSULTAR_CLIENTE_SIN_CITA, obj);
    }

    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "-----> >" + obj);
        try{
            String status = obj.getString("status");
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
                Log.d(TAG, "JSON response : ->" + array);
            }else{
                String statusText = obj.getString("statusText");
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
