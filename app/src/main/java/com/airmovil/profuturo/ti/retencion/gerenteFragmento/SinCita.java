package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.location.LocationManager;
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
import com.airmovil.profuturo.ti.retencion.helper.Log;
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

import com.airmovil.profuturo.ti.retencion.Adapter.GerenteSinCitaAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.*;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.model.GerenteSinCitaModel;
import com.android.volley.VolleyError;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class SinCita extends Fragment {
    public static final String TAG = SinCita.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String[] ESTADOS_CITAS = new String[]{"Selecciona...","Número de cuenta", "NSS", "CURP"};
    private Spinner spinner;
    private Button btnBuscar;
    private TextView tvFecha;
    private EditText etDatos;
    private View rootView;
    private int pagina = 1;
    private GerenteSinCitaAdapter adapter;
    private List<GerenteSinCitaModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private String llave = "", valor = "";
    private boolean procesa = false;
    private OnFragmentInteractionListener mListener;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    public SinCita() {/* Se requiere un constructor vacio */}

    /**
     * @param param1 seleccion del campo spinner
     * @param param2 contenido del campo EditText
     * @return una nueva instancia del fragmento SinCita.
     */
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
    }

    /**
     * Se llama inmediatamente después de que onCreateView(LayoutInflater, ViewGroup, Bundle) ha onCreateView(LayoutInflater, ViewGroup, Bundle)
     * pero antes de que se haya onCreateView(LayoutInflater, ViewGroup, Bundle) estado guardado en la vista.
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Variables, asignacion de variables
        variables();
        // TODO: Argumentos, verifica que existan datos recibidos
        argumentos();
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
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        spinner.setAdapter(adapter);

        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    String valores = etDatos.getText().toString().trim();
                    String seleccion = spinner.getSelectedItem().toString();
                    if(seleccion.equals("Selecciona...")) {
                        Dialogos.dialogoDatosVacios(getContext());
                    }else{
                        if(valores.isEmpty()) {
                            switch (seleccion){
                                case "Número de cuenta":
                                    Dialogos.dialogoSinSeleccionSpinner(getContext(), " número de cuenta");
                                    break;
                                case "NSS":
                                    Dialogos.dialogoSinSeleccionSpinner(getContext(), " NSS");
                                    break;
                                case "CURP":
                                    Dialogos.dialogoSinSeleccionSpinner(getContext(), " CURP");
                                    break;
                            }
                        }else{
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            SinCita clase = SinCita.newInstance(seleccion, valores);
                            Config.teclado(getContext(), etDatos);
                            borrar.onDestroy();
                            ft.remove(borrar).replace(R.id.content_gerente, clase).addToBackStack(null).commit();
                        }
                    }
                }else{
                    Dialogos.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
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

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onResume() {
        LocationManager mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean enable = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enable){
            Dialogos.dialogoActivarLocalizacion(getContext());
        }
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new Inicio();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_inicio), 2, fragment);
                    return true;
                }
                return false;
            }
        });
        super.onResume();
    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater inflacion del xml
     * @param container contenedor del ml
     * @param savedInstanceState datos guardados
     * @return el fragmento declarado DIRECTOR INICIO
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gerente_fragmento_sin_cita, container, false);
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
     * Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esta actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setear las variables de xml
     */
    private void variables(){
        tvFecha = (TextView) rootView.findViewById(R.id.gfsc_tv_fecha);
        spinner = (Spinner) rootView.findViewById(R.id.gfsc_spinner_tipo_dato);
        etDatos = (EditText) rootView.findViewById(R.id.gfsc_et_datos);
        btnBuscar = (Button) rootView.findViewById(R.id.gfsc_btn_buscar);
        connected = new Connected();
        tvFecha.setText(Dialogos.fechaActual());
        etDatos.setFocusable(false);
        etDatos.setFocusableInTouchMode(false);
    }

    /**
     * Se utiliza para cololar datos recibidos entre una busqueda(por ejemplo: fechas)
     */
    private void  argumentos(){
        if(getArguments() != null){
            llave = getArguments().getString(ARG_PARAM1);
            valor = getArguments().getString(ARG_PARAM2);
            procesa = true;
        }
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
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion, String seleccion, String valores) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtros = new JSONObject();

        try {
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
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "<- Response ->" + obj);
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
            }else{
                statusText = obj.getString("statusText");
                Dialogos.msj(getContext(), "Error", statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        adapter = new GerenteSinCitaAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
    }
}
