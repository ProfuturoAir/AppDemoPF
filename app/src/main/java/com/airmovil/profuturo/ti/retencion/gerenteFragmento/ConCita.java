package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Adapter;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.Adapter.GerenteCitasClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteConCitaClientes;
import com.android.volley.VolleyError;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.List;

public class ConCita extends Fragment {
    private static final String TAG = ConCita.class.getSimpleName();
    private static final String ARG_PARAM1 = "atencion";
    private List<GerenteConCitaClientes> getDatos1;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private GerenteCitasClientesAdapter adapter;
    private int filas, pagina = 1, numeroMaximoPaginas = 0, spinnerOpcion;
    private OnFragmentInteractionListener mListener;
    private Spinner spinner;
    private Button btnAplicar, btnClienteSinCita;
    private TextView tvFecha, tvRegistros;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;
    private Fragment borrar = this;

    public ConCita() {/* constructor vacio requerido */}

    /**
     * al crear una instancia recibe los parametros:
     * @param param1 Parametro 1.
     * @return un objeto ConCita.
     */
    public static ConCita newInstance(int param1, Context context) {
        ConCita fragment = new ConCita();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        if(getArguments() != null){
            Log.e(TAG, "argumentos->" + getArguments().toString());
        }
        super.onCreate(savedInstanceState);
    }

    /**
     * El sistema lo llama para iniciar los procesos que estaran dentro del flujo de la vista
     * @param view accede a la vista del xml
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Asisgnacion de variables
        variables();

        if(Config.conexion(getContext()))
            sendJson(true);
        else
            Dialogos.dialogoErrorConexion(getContext());

        // TODO: fecha de citas
        fechas();
        // TODO: Spinner
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getContext(), R.layout.spinner_item, Config.ATENCION){
            @Override
            public boolean isEnabled(int position) {
                if(position == 0)
                    return false;
                else
                    return true;
            }
        };
        adapter.setDropDownViewResource(R.layout.spinner_dropdown_item);
        spinner.setSelection(Adapter.NO_SELECTION,false);
        spinner.setAdapter(adapter);
        spinner.setSelection((getArguments()!=null)?getArguments().getInt(ARG_PARAM1):0);
        // TODO: modelos
        getDatos1 = new ArrayList<>();
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.grecyclerview_citas);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);
        btnAplicar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                spinnerOpcion = spinner.getSelectedItemPosition();
                if(connected.estaConectado(getContext())){
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ConCita newInstance = ConCita.newInstance(spinnerOpcion, rootView.getContext());
                    borrar.onDestroy();ft.remove(borrar).replace(R.id.content_gerente, newInstance).addToBackStack(null).commit();
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
                                    sendJson(true);
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
        btnClienteSinCita.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
                Fragment fragmentoGenerico = new SinCita();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
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
        return inflater.inflate(R.layout.gerente_fragmento_con_cita, container, false);
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
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_inicio), 1, fragment);
                    return true;
                }
                return false;
            }
        });
        super.onResume();
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
     * Casteo de variables, nueva instancia para la conexion a internet Connected
     */
    private void variables(){
        connected = new Connected();
        spinner = (Spinner) rootView.findViewById(R.id.gfcc_spinner_estados);
        btnAplicar = (Button) rootView.findViewById(R.id.gfcc_btn_aplicar);
        btnClienteSinCita = (Button) rootView.findViewById(R.id.gfcc_btn_sin_cita);
        tvFecha = (TextView) rootView.findViewById(R.id.gfcc_tv_fecha);
        tvRegistros = (TextView) rootView.findViewById(R.id.gfcc_tv_registros);
    }

    /**
     * Inicio la fecha actual
     */
    private void fechas(){tvFecha.setText(Dialogos.fechaActual());}

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
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        final JSONObject obj = new JSONObject();
        try {
            boolean arg = (getArguments()!=null);
            JSONObject rqt = new JSONObject();
            rqt.put("estatusCita",(arg)? getArguments().getInt(ARG_PARAM1):0);
            rqt.put("pagina", pagina);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("" + primerPeticion, Config.URL_CONSULTAR_RESUMEN_CITAS, obj);
    }

    /**
     * Obtiene el objeto json(Response), se obtiene cada elemento a parsear
     * @param obj json objeto
     */
    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "<-Response->\n" + obj + "\n");
        int totalFilas = 1;
        try{
            JSONArray array = obj.getJSONArray("citas");
            filas = obj.getInt("filasTotal");
            totalFilas = obj.getInt("filasTotal");
            String status = obj.getString("status");
            String statusText = obj.getString("statusText");
            if(Integer.parseInt(status) == 200){
                for(int i = 0; i < array.length(); i++){
                    GerenteConCitaClientes getDatos2 = new GerenteConCitaClientes();
                    JSONObject json = null;
                    try{
                        json = array.getJSONObject(i);
                        getDatos2.setHora(json.getString("hora"));
                        getDatos2.setNombreCliente(json.getString("nombreCliente"));
                        getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                    getDatos1.add(getDatos2);
                }
            }else{
                Dialogos.dialogoErrorRespuesta(getContext(), status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvRegistros.setText(filas + " Registros");
        numeroMaximoPaginas = Config.maximoPaginas(totalFilas);
        adapter = new GerenteCitasClientesAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);


        try {
            adapter.notifyDataSetChanged();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }

        adapter.setOnLoadMoreListener(new OnLoadMoreListener() {
            @Override
            public void onLoadMore() {
                if (pagina >= numeroMaximoPaginas) {
                    return;
                }
                getDatos1.add(null);
                Handler handler = new Handler();
                final Runnable r = new Runnable() {
                    public void run() {
                        adapter.notifyItemInserted(getDatos1.size() - 1);
                    }
                };
                handler.post(r);
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
     * Corre este metodo cuando hay mas de 10 contenido a mostrar en la lista
     * @param obj objeto json
     */
    private void segundoPaso(JSONObject obj) {
        try{
            JSONArray array = obj.getJSONArray("citas");
            for(int i = 0; i < array.length(); i++){
                GerenteConCitaClientes getDatos2 = new GerenteConCitaClientes();
                JSONObject json = null;
                try{
                    json = array.getJSONObject(i);
                    getDatos2.setHora(json.getString("hora"));
                    getDatos2.setNombreCliente(json.getString("nombreCliente"));
                    getDatos2.setNumeroCuenta(json.getString("numeroCuenta"));
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
