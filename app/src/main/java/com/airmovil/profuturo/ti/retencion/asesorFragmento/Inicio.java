package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.GPSRastreador;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class Inicio extends Fragment {
    public static final String TAG = Inicio.class.getSimpleName();
    private static final String ARG_PARAM1 = "fechaInicio";
    private static final String ARG_PARAM2 = "fechaFin";
    private String mParam1, /* fechaInicio */ mParam2; // fechaFin
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private Connected connected;
    private TextView tvInicial, tvNombre, tvFecha, tvRetenidos, tvNoRetenidos, tvSaldoRetenido, tvSaldoNoRetenido, tvRangoFecha1, tvRangoFecha2;
    private Button btnFiltro;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public Inicio() {/* Se requiere un constructor vacio */}

    /**
     * este fragmento se crea para el envio de parametros.
     * @param param1 fecha inicio
     * @param param2 fecha fin
     * @return una nueva instancia para el fragmento Inicio.
     */
    public static Inicio newInstance(String param1, String param2, Context ctx) {
        Inicio enviarDatos = new Inicio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        enviarDatos.setArguments(args);
        return enviarDatos;
    }

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
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


                NetworkResponse networkResponse = error.networkResponse;

                Log.e(TAG, "*->" + networkResponse);
                if(networkResponse == null){
                    loading.dismiss();
                }

                /*if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                } else{
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }*/

                /*if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }*/
            }
        };
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
        if(Config.conexion(getContext()))
            sendJson(true);
        else
            Dialogos.dialogoErrorConexion(getContext());
        // TODO: Desactivar el modo Avion
        Config.modeAvionDetectado(getContext());

        // TODO: Asignacion de variables
        variables();
        // TODO: Detalle superios de datos de usuario
        detalleSuperior();
        // TODO: Verificacion de datos recibidos
        argumentos();
        // TODO: GPS coordenadas
        localizacion();
        // TODO: Dialogos de fechas
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1); // Dialogo de muestra de fecha
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2); // Dialogo de muestra de fecha
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if (tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty() ) {
                        Dialogos.dialogoFechasVacias(getContext());
                    } else {
                        if(Config.comparacionFechas(getContext(), tvRangoFecha1.getText().toString().trim(), tvRangoFecha2.getText().toString().trim())) {

                        }else {
                            FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                            Inicio fragmento = Inicio.newInstance(mParam1 = tvRangoFecha1.getText().toString(), mParam2 = tvRangoFecha2.getText().toString(), rootView.getContext());
                            borrar.onDestroy();
                            ft.remove(borrar).replace(R.id.content_asesor, fragmento).addToBackStack(null).commit();
                        }
                    }
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
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
        return inflater.inflate(R.layout.asesor_fragmento_inicio, container, false);
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
        if(!Config.estahabilitadoGPS(getContext())){
            Dialogos.dialogoActivarLocalizacion(getContext());
        }
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle(getResources().getString(R.string.titulo_salida_app));
                    dialogo1.setMessage(getResources().getString(R.string.msj_salida_app));
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            getActivity().finish();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialogo1.show();
                    return true;
                }
                return false;
            }
        });
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
        tvInicial = (TextView) rootView.findViewById(R.id.afi_tv_inicial);
        tvNombre = (TextView) rootView.findViewById(R.id.afi_tv_nombre);
        tvFecha = (TextView) rootView.findViewById(R.id.afi_tv_fecha);
        tvRetenidos = (TextView) rootView.findViewById(R.id.afi_tv_retenidos);
        tvNoRetenidos = (TextView) rootView.findViewById(R.id.afi_tv_no_retenidos);
        tvSaldoRetenido = (TextView) rootView.findViewById(R.id.afi_tv_saldo_a_favor);
        tvSaldoNoRetenido= (TextView) rootView.findViewById(R.id.afi_tv_saldo_retenido);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.afi_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.afi_tv_fecha_rango2);
        btnFiltro = (Button) rootView.findViewById(R.id.afi_btn_filtro);
        connected = new Connected();
    }

    /**
     * Obtencion de Coordenadas
     */
    private void localizacion(){
        GPSRastreador gps = new GPSRastreador(getContext());
        Double latitude_user= gps.getLatitude();
        Double longitude_user = gps.getLongitude();
        Log.e(TAG, "Latitud: " + latitude_user + "\nLongitud: " + longitude_user);
    }

    /**
     * Obteniendo los valores del apartado superior, nombre
     */
    public void detalleSuperior(){
        Map<String, String> usuario = Config.datosUsuario(getContext());
        tvNombre.setText("" + String.valueOf(usuario.get(MySharePreferences.NOMBRE)) + " " + String.valueOf(usuario.get(MySharePreferences.APELLIDO_PATERNO)) + " " + String.valueOf(usuario.get(MySharePreferences.APELLIDO_MATERNO)));
        char letra = String.valueOf(usuario.get(MySharePreferences.NOMBRE)).charAt(0);
        tvInicial.setText(String.valueOf(Character.toString(letra)));
    }

    /**
     *  Espera el regreso de fechas incial (hoy y el dia siguiente)
     *  y cuando se realiza una nueva busqueda, retorna las fechas seleccionadas
     */
    private void argumentos(){
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(mParam1 + " - " + mParam2);
            tvRangoFecha1.setText(mParam1);
            tvRangoFecha2.setText(mParam2);
        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }

    /**
     * Método para generar el proceso REST
     * @param primeraPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primeraPeticion){
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", (getArguments()!=null) ? getArguments().getString(ARG_PARAM1) : Dialogos.fechaActual());
            periodo.put("fechaFin", (getArguments()!=null) ? getArguments().getString(ARG_PARAM2) : Dialogos.fechaSiguiente());
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            json.put("rqt", rqt);
            Log.d(TAG, "<-- RQT --> \n " + json + "\n");
        } catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CONSULTAR_RESUMEN_RETENCIONES, json);
    }

    /**
     * Obtiene el objeto json(Response), se obtiene cada elemento a parsear
     * @param obj json objeto
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "<- Response ->\n"  + obj +"\n");
        JSONObject retenidos = null;
        JSONObject saldos = null;
        int iRetenidos = 0, iNoRetenidos = 0, iSaldoRetenido = 0, iSaldoNoRetenido = 0;
        String status = "";
        try{
            status = obj.getString("status");
            int i = Integer.parseInt(status);
            String statusText = "";
            if(i == 200){
                JSONObject infoConsulta = obj.getJSONObject("infoConsulta");
                retenidos = infoConsulta.getJSONObject("retenido");
                iRetenidos = (Integer) retenidos.get("retenido");
                iNoRetenidos = (Integer) retenidos.get("noRetenido");
                saldos = infoConsulta.getJSONObject("saldo");
                iSaldoRetenido = (int) saldos.get("saldoRetenido");
                iSaldoNoRetenido = (int) saldos.get( "saldoNoRetenido");
                Log.d("JSON", retenidos.toString());
            }else{
                statusText = obj.getString("statusText");
                Dialogos.dialogoErrorRespuesta(getContext(), String.valueOf(i), statusText);
            }
        }catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        tvRetenidos.setText("" + iRetenidos);
        tvNoRetenidos.setText("" + iNoRetenidos);
        tvSaldoRetenido.setText("" + Config.nf.format(iSaldoRetenido));
        tvSaldoNoRetenido.setText("" + Config.nf.format(iSaldoNoRetenido));
    }
}