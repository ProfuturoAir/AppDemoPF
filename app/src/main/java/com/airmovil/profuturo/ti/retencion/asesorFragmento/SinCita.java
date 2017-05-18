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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.SinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.model.SinCitaModel;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SinCita extends Fragment {
    public static final String TAG = SinCita.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1",  ARG_PARAM2 = "param2";
    private static final String[] ESTADOS_CITAS = new String[]{"Selecciona...","Número de cuenta", "NSS", "CURP"};
    private Spinner spinner;
    private Button btnBuscar;
    private TextView tvFecha;
    private EditText etDatos;
    private View rootView;
    private String fechaMostrar = "";
    private SinCitaAdapter adapter;
    private List<SinCitaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private OnFragmentInteractionListener mListener;
    private SharedPreferences sharedPreferences;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;
    private int pagina = 1;
    private Fragment borrar = this;

    public SinCita() { /* constructor vacio es requerido */}

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

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * El sistema lo llama para iniciar los procesos que estaran dentro del flujo de la vista
     * @param view accede a la vista del xml
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
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
        fechaMostrar = Dialogos.fechaActual();
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
        final ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
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

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    String valores = etDatos.getText().toString().trim();
                    int seleccion = spinner.getSelectedItemPosition();
                    if(spinner.getSelectedItem().toString().equals("Selecciona el tipo de ID a buscar")) {
                        Dialogos.dialogoDatosVacios(getContext());
                    }else{
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            SinCita clase = SinCita.newInstance(seleccion, valores);
                            Config.teclado(getContext(), etDatos);
                            borrar.onDestroy();
                            ft.remove(borrar).replace(R.id.content_asesor, clase).addToBackStack(null).commit();
                    }
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
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

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new ConCita();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regreso_proceso), 1, fragment);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_sin_cita, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context estado actual de la aplicacion
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     *Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esa actividad.
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

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, int seleccion, String valores) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        try {
            JSONObject rqt = new JSONObject();
            rqt.put("filtro", Config.filtroClientes(seleccion, valores));
            rqt.put("pagina", pagina);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "<- RQT ->\n" + obj + "\n");
        } catch (JSONException e) {
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CONSULTAR_CLIENTE_SIN_CITA, obj);
    }

    /**
     * Inicia este metodo para llenar la lista de elementos, cada 10, inicia solamente con 10, despues inicia el metodo segundoPaso
     * @param obj jsonObject
     */
    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "<- RESPONSE -> \n" + obj + "\n");
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
            }else{
                String statusText = obj.getString("statusText");
                Dialogos.dialogoErrorRespuesta(getContext(), status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter = new SinCitaAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }


}
