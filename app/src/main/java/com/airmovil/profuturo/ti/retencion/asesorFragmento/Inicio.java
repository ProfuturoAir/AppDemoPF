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
import android.util.Log;
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
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class Inicio extends Fragment {
    public static final String TAG = Inicio.class.getSimpleName();
    private static final String ARG_PARAM1 = "fechaInicio";
    private static final String ARG_PARAM2 = "fechaFin";
    private String mParam1; // fechaInicio
    private String mParam2; // fechaFin
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private Connected connected;
    private TextView tvInicial, tvNombre, tvFecha, tvRetenidos, tvNoRetenidos, tvSaldoRetenido, tvSaldoNoRetenido, tvRangoFecha1, tvRangoFecha2;
    private Button btnFiltro;
    private Fragment borrar = this;

    public Inicio() {
        // Se requiere un constructor vacio
    }

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
     * El sistema lo llama para iniciar los procesos que estaran dentro del flujo de la vista
     * @param view accede a la vista del xml
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view; // Se iguala para hacer lo, general
        primeraPeticion();
        variables();
        detalleSuperior();
        argumentos();
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1); // Dialogo de muestra de fecha
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2); // Dialogo de muestra de fecha
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    if (tvRangoFecha1.getText().toString().isEmpty() || tvRangoFecha2.getText().toString().isEmpty() ) {
                        Config.dialogoFechasVacias(getContext());
                    } else {
                        FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                        Inicio fragmento = Inicio.newInstance(mParam1 = tvRangoFecha1.getText().toString(), mParam2 = tvRangoFecha2.getText().toString(), rootView.getContext());
                        borrar.onDestroy();
                        ft.remove(borrar).replace(R.id.content_asesor, fragmento).addToBackStack(null).commit();
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

    /**
     *
     * @param uri
     */
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     *
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
     *
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
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Mensaje");
                    dialogo1.setMessage("¿Estás seguro que deseas salir de la aplicación?");
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

    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Muestra en primer proceso un dialogo. Se envia de primera peticion al metodo sendJson.
     */
    private void primeraPeticion(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIcon(R.drawable.icono_abrir);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_espera));
        progressDialog.show();
        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        sendJson(true);
                    }
                }, Config.TIME_HANDLER);
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
     * Obteniendo los valores del apartado superior, nombre
     */
    public void detalleSuperior(){
        Map<String, String> usuario = Config.datosUsuario(getContext());
        String nombre = usuario.get(SessionManager.NOMBRE);
        String apePaterno = usuario.get(SessionManager.APELLIDO_PATERNO);
        String apeMaterno = usuario.get(SessionManager.APELLIDO_MATERNO);
        tvNombre.setText("" + nombre + " " + apePaterno + " " + apeMaterno);
        char letra = nombre.charAt(0);
        String inicial = Character.toString(letra);
        tvInicial.setText(inicial);
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
        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            boolean argumentos = (getArguments()!=null);
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", (argumentos) ? getArguments().getString(ARG_PARAM1) : Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos) ? getArguments().getString(ARG_PARAM2) : Dialogos.fechaSiguiente());
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            json.put("rqt", rqt);
            Log.d(TAG, "<-- RQT --> \n " + json + "\n");
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_RESUMEN_RETENCIONES, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        if(connected.estaConectado(getContext())){
                            Dialogos.dialogoErrorServicio(getContext());
                        }else{
                            Dialogos.dialogoErrorConexion(getContext());
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    /**
     * Obtiene el objeto json(Response), se obtiene cada elemento a parsear
     * @param obj json objeto
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "<- Response ->\n"  + obj +"\n");
        JSONObject retenidos = null;
        JSONObject saldos = null;
        int iRetenidos = 0;
        int iNoRetenidos = 0;
        int iSaldoRetenido = 0;
        int iSaldoNoRetenido = 0;
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
                Config.msj(getContext(), "Error: " + i, statusText);
            }
        }catch (JSONException e){
            Config.msj(getContext(), "Error", "Lo sentimos ocurrio un error con los datos");
        }
        tvRetenidos.setText("" + iRetenidos);
        tvNoRetenidos.setText("" + iNoRetenidos);
        tvSaldoRetenido.setText("" + Config.nf.format(iSaldoRetenido));
        tvSaldoNoRetenido.setText("" + Config.nf.format(iSaldoNoRetenido));
    }
}