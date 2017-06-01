package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.PorterDuff;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
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
import com.airmovil.profuturo.ti.retencion.Adapter.DirectorReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.helper.SpinnerDatos;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteClientesModel;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ReporteClientes extends Fragment{
    private static final String TAG = ReporteClientes.class.getSimpleName(), ARG_PARAM1 = "idBusqueda", ARG_PARAM2 = "datosCliente", ARG_PARAM3 = "idGerencia", ARG_PARAM4 = "idSucursal", ARG_PARAM5 = "idAsesor", ARG_PARAM6 = "fechaInicio", ARG_PARAM7 = "fechaFin", ARG_PARAM8 = "idRetenido", ARG_PARAM9 = "idCita";
    private Spinner spinnerId, spinnerSucursales, spinnerIdRetenido, spinnerIdCita, spinnerGerencias;
    private TextView tvResultados, tvRangoFecha1, tvRangoFecha2, tvFecha;
    private Button btnBuscar;
    private EditText etDatosCliente, etIdAsesor;
    private ArrayList<String> sucursales, id_sucursales;
    private View rootView;
    private Connected connected;
    private List<DirectorReporteClientesModel> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private DirectorReporteClientesAdapter adapter;
    private int filas, pagina = 1, numeroMaximoPaginas = 0, idSucursal1, idGerencia1, idRetenido1, idCita1, idSucursal, tipoBuscar, spinId = 0, idGerencia;
    private String idAsesor1;
    private Fragment borrar = this;
    private OnFragmentInteractionListener mListener;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;


    public ReporteClientes() {/* requiere un constructor vacio */}

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteClientes.
     */
    public static ReporteClientes newInstance(int param1, String param2, int param3, int param4, String param5, String param6, String param7, int param8, int param9){
        ReporteClientes fragmento = new ReporteClientes();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putInt(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putInt(ARG_PARAM8, param8);
        args.putInt(ARG_PARAM9, param9);
        fragmento.setArguments(args);
        return fragmento;
    }

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * metodo para callback de volley
     */
    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                if (requestType.trim().equals("true")) {
                    loading.dismiss();
                    primerPaso(response);
                } else {
                    segundoPaso(response);
                }
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
        // TODO: Asignacion de variables
        variables();
        // TODO: Verifica si existen variables
        argumentos();
        // TODO: Muestra de dialogos fecha inicio y fecha fin
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: REST
        if(Config.conexion(getContext()))
            sendJson(true);
        else
            Dialogos.dialogoErrorConexion(getContext());
        // TODO: Spinner
        final ArrayAdapter<String> adapterId = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.IDS);
        adapterId.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerId.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                switch (position){
                    case 0:
                        etDatosCliente.setInputType(InputType.TYPE_TEXT_FLAG_NO_SUGGESTIONS);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_HIDDEN);
                        etDatosCliente.setFocusable(false);
                        break;
                    case 1:
                        etDatosCliente.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatosCliente.getBackground().setColorFilter(getResources().getColor(R.color.colorSecundaryGray), PorterDuff.Mode.LIGHTEN);
                        etDatosCliente.setInputType(InputType.TYPE_CLASS_PHONE);
                        break;
                    case 2:
                        etDatosCliente.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatosCliente.getBackground().setColorFilter(getResources().getColor(R.color.colorSecundaryGray), PorterDuff.Mode.LIGHTEN);
                        etDatosCliente.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                        break;
                    case 3:
                        etDatosCliente.setFocusableInTouchMode(true);
                        getActivity().getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
                        etDatosCliente.getBackground().setColorFilter(getResources().getColor(R.color.colorSecundaryGray), PorterDuff.Mode.LIGHTEN);
                        etDatosCliente.setInputType(InputType.TYPE_TEXT_FLAG_CAP_WORDS);
                }
                etDatosCliente.setHint("Ingresa, " + adapterId.getItem(position));

            }
            @Override
            public void onNothingSelected(AdapterView<?> parent) {
            }
        });
        boolean argumentos = (getArguments()!=null);
        spinnerId.setAdapter(adapterId);
        spinnerId.setSelection((argumentos)?getArguments().getInt(ARG_PARAM1): 0);
        // TODO: Spinner
        ArrayAdapter<String> adapterRetenido = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.RETENIDO);
        adapterRetenido.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerIdRetenido.setAdapter(adapterRetenido);
        spinnerIdRetenido.setSelection(idRetenido1);
        // TODO: Spinner
        ArrayAdapter<String> adapterCita = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.CITAS);
        adapterCita.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinnerIdCita.setAdapter(adapterCita);
        spinnerIdCita.setSelection(idCita1);
        // TODO: Recycler
        getDatos1 = new ArrayList<>();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.ddfrc_rv_lista);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if(tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty()){
                        Config.dialogoFechasVacias(getContext());
                    }else{
                        if(Config.comparacionFechas(getContext(), tvRangoFecha1.getText().toString().trim(), tvRangoFecha2.getText().toString().trim()) == false) {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            ReporteClientes fragmento = ReporteClientes.newInstance(spinnerId.getSelectedItemPosition(), etDatosCliente.getText().toString(), Config.ID_GERENCIA, Config.ID_SUCURSAL, etIdAsesor.getText().toString(), tvRangoFecha1.getText().toString(), tvRangoFecha2.getText().toString(), spinnerIdRetenido.getSelectedItemPosition(), spinnerIdCita.getSelectedItemPosition());
                            borrar.onDestroy();
                            ft.remove(borrar).replace(R.id.content_director, fragmento).addToBackStack(null).commit();
                            Config.teclado(getContext(), etIdAsesor);
                            Config.teclado(getContext(), etDatosCliente);
                        }
                    }
                }else{
                    Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                }
            }
        });

        // TODO: Envio de email
        tvResultados.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean argumentos = (getArguments()!=null);
                ServicioEmailJSON.enviarEmailReporteClientes(getContext(), (argumentos)?getArguments().getInt(ARG_PARAM3):0, (argumentos)?getArguments().getInt(ARG_PARAM1):0, (argumentos)?getArguments().getInt(ARG_PARAM9):0, (argumentos)?getArguments().getString(ARG_PARAM2):"", (argumentos)?getArguments().getInt(ARG_PARAM8):0, (argumentos)?getArguments().getString(ARG_PARAM5):"", (argumentos)?getArguments().getString(ARG_PARAM6):Dialogos.fechaActual(), (argumentos)?getArguments().getString(ARG_PARAM7):Dialogos.fechaSiguiente(), (argumentos) ? true:false );
            }
        });

        SpinnerDatos.spinnerGerencias(getContext(), spinnerGerencias, (getArguments()!=null) ? Config.ID_GERENCIA_POSICION : 0);
        SpinnerDatos.spinnerSucursales(getContext(), spinnerSucursales, (getArguments()!=null) ? Config.ID_SUCURSAL_POSICION : 0);

    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater acceso para inflar XML
     * @param container contenido
     * @param savedInstanceState estado de los elementos almacenados
     * @return el fragmento relacionado con la actividad
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.director_fragmento_reporte_clientes, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
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

                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en ese
     * actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Asignacion de las variables
     * declaracion de objetos
     */
    private void variables(){
        spinnerId = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_id);
        spinnerGerencias = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_gerencia);
        spinnerSucursales = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_sucursal);
        spinnerIdRetenido = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_estado);
        spinnerIdCita = (Spinner) rootView.findViewById(R.id.ddfrc_spinner_citas);
        tvFecha = (TextView) rootView.findViewById(R.id.ddfrc_tv_fecha);
        tvRangoFecha1  = (TextView) rootView.findViewById(R.id.ddfrc_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) rootView.findViewById(R.id.ddfrc_tv_fecha_rango2);
        btnBuscar = (Button) rootView.findViewById(R.id.ddfrc_btn_buscar);
        tvResultados = (TextView) rootView.findViewById(R.id.ddfrc_tv_registros);
        etDatosCliente = (EditText) rootView.findViewById(R.id.ddfrc_et_id);
        etIdAsesor = (EditText) rootView.findViewById(R.id.ddfrc_et_asesor);
        connected = new Connected();
    }

    /**
     *  Espera el regreso de fechas incial (hoy y el dia siguiente)
     *  y cuando se realiza una nueva busqueda, retorna las fechas seleccionadas
     */
    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM6) + " - " + getArguments().getString(ARG_PARAM7));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM6));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM7));
            etDatosCliente.setText(getArguments().getString(ARG_PARAM2));
            idSucursal1 = getArguments().getInt("idSucursal");
            idGerencia1 = getArguments().getInt("idGerencia");
            idAsesor1 = getArguments().getString("idAsesor");
            idRetenido1 = getArguments().getInt("idRetenido");
            idCita1 = getArguments().getInt("idCita");

            if(getArguments().getInt(ARG_PARAM1)!=0){spinnerId.setSelection(getArguments().getInt(ARG_PARAM1));spinId = tipoBuscar;}
            if(etIdAsesor!=null) etIdAsesor.setText(idAsesor1);
            if(idCita1 != 0) spinnerIdCita.setSelection(idCita1);

        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }

    /**
     * Envio de datos por REST jsonObject
     * @param primeraPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primeraPeticion){
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtroCliente = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            boolean argumentos = (getArguments()!= null);
            rqt.put("cita", (argumentos) ? getArguments().getInt(ARG_PARAM9) : 0);
            rqt.put("numeroEmpleado", (argumentos) ? getArguments().getString(ARG_PARAM2) : "");
            rqt.put("filtroCliente", Config.filtroClientes((argumentos)? getArguments().getInt(ARG_PARAM1):0, (argumentos) ? getArguments().getString(ARG_PARAM2) : ""));
            rqt.put("idGerencia", (argumentos) ? getArguments().getInt(ARG_PARAM3) : 0);
            rqt.put("idSucursal", (argumentos) ? getArguments().getInt(ARG_PARAM4) : 0);
            rqt.put("pagina", pagina);
            periodo.put("fechaInicio", (argumentos) ? getArguments().getString(ARG_PARAM6) : Dialogos.fechaSiguiente());
            periodo.put("fechaFin", (argumentos) ? getArguments().getString(ARG_PARAM7) : Dialogos.fechaActual());
            rqt.put("periodo", periodo);
            rqt.put("retenido", (argumentos) ? getArguments().getInt(ARG_PARAM8) : 0);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            json.put("rqt", rqt);
            Log.d(TAG, " <- RQT -> \n" + json + "\n");
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }
        volleySingleton.postDataVolley("" + primeraPeticion, Config.URL_CONSULTAR_REPORTE_RETENCION_CLIENTES, json);
    }

    /**
     * Inicia este metodo para llenar la lista de elementos, cada 10, inicia solamente con 10, despues inicia el metodo segundoPaso
     * @param obj jsonObject
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "Response: \n" + obj.toString() + "\n");
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("Cliente");
            filas = obj.getInt("filasTotal");
            totalFilas = obj.getInt("filasTotal");
            for(int i = 0; i < array.length(); i++){
                DirectorReporteClientesModel getDatos2 = new DirectorReporteClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNombreCliente(json.getString("nombreCliente"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    getDatos2.setNumeroEmpleado(json.getString("numeroEmpleado"));
                    getDatos2.setNombreAsesor(json.getString("nombreAsesor"));
                    getDatos2.setCita(json.getString("cita"));
                    getDatos2.setRetenido(json.getString("retenido"));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
                    getDatos2.setHora(json.getString("horaAtencion"));
                    getDatos2.setTramite(json.getInt("idTramite"));
                }catch (JSONException e){
                    e.printStackTrace();
                }
                getDatos1.add(getDatos2);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvResultados.setText(filas + " Registros");
        // TODO: calucula el tamaño de filas y devuelve la cantidad de paginas a procesar
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        String PtvFecha = tvFecha.getText().toString();
        String[] separated = PtvFecha.split(" - ");
        // TODO: envio de datos al adaptador para incluir dentro del recycler
        adapter = new DirectorReporteClientesAdapter(rootView.getContext(), getDatos1, recyclerView,separated[0].trim(),separated[1].trim());
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        // TODO: verificaion si existe un scroll enviando al segundo metodo
        adapter.notifyDataSetChanged();
        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pagina >= numeroMaximoPaginas) {
                    return;
                }
                getDatos1.add(null);
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    @Override
                    public void run() {
                        adapter.notifyItemInserted(getDatos1.size() - 1);
                    }
                };
                handler.post(r  );
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        getDatos1.remove(getDatos1.size() - 1);
                        adapter.notifyItemRemoved(getDatos1.size());
                        pagina = Config.pidePagina(getDatos1);
                        sendJson(false);
                    }
                }, Config.TIME_HANDLER);
            }
        });
    }

    /**
     * Se vuelve a llamaar este metodo para llenar la lista cada 10 contenidos
     * @param obj jsonObject de respuesta
     */
    private void segundoPaso(JSONObject obj) {
        try{
            JSONArray array = obj.getJSONArray("Cliente");
            for(int i = 0; i < array.length(); i++){
                DirectorReporteClientesModel getDatos2 = new DirectorReporteClientesModel();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setNombreCliente(json.getString("nombreCliente"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    getDatos2.setNumeroEmpleado(json.getString("numeroEmpleado"));
                    getDatos2.setNombreAsesor(json.getString("nombreAsesor"));
                    getDatos2.setCita(json.getString("cita"));
                    getDatos2.setRetenido(json.getString("retenido"));
                    getDatos2.setIdSucursal(json.getInt("idSucursal"));
                    getDatos2.setHora(json.getString("horaAtencion"));
                    getDatos2.setTramite(json.getInt("idTramite"));
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
