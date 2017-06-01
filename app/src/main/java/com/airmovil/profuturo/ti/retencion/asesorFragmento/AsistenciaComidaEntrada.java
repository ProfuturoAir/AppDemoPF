package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.GPSRastreador;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

public class AsistenciaComidaEntrada extends Fragment {
    private DrawingView dvFirma;
    private TextView tvLongitud, tvLatitud;
    private Button btnLimpiar, btnGuardar, btnCancelar;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private GPSRastreador gps;
    private MySharePreferences mySharePreferences;
    private LinearLayout linearLayout;
    private TextView textViewFecha;

    public AsistenciaComidaEntrada() {/*contructor vacio es requerido*/}

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
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        btnLimpiar = (Button) rootView.findViewById(R.id.buttonLimpiar3);
        btnGuardar = (Button) rootView.findViewById(R.id.buttonGuardar3);
        btnCancelar = (Button) rootView.findViewById(R.id.buttonCancelar3);
        linearLayout = (LinearLayout) rootView.findViewById(R.id.mensaje_ok3);
        textViewFecha = (TextView) rootView.findViewById(R.id.tv_fecha_hoy3);

        dvFirma = (DrawingView) view.findViewById(R.id.drawinView3);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);

        // TODO: Clase para obtener las coordenadas
        gps = new GPSRastreador(getContext());

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvFirma.startNew();
                dvFirma.setDrawingCacheEnabled(true);
                Dialogos.dialogoContenidoLimpio(getContext());
            }
        });

        mySharePreferences = MySharePreferences.getInstance(getContext());
        HashMap<String,String> datosFirmas = mySharePreferences.registrosFirmas();
        boolean verificacionFecha = Config.comparacionFechaActual(Dialogos.fechaActual(), datosFirmas.get(MySharePreferences.FECHA_ENTRADA_COMIDA));
        if(verificacionFecha)
            Config.mensajeRegistro(getContext(), linearLayout, textViewFecha, btnLimpiar, btnGuardar, btnCancelar, dvFirma);


        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!dvFirma.isActive()) {
                    Dialogos.dialogoRequiereFirma(getContext());
                } else {
                    if (Config.conexion(getContext())) {
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_ok));
                        progressDialog.setTitle(getResources().getString(R.string.msj_titulo_confirmacion));
                        progressDialog.setMessage(getResources().getString(R.string.msj_contenido_envio) + " registro de comida entrada");
                        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                                new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        progressDialog.dismiss();
                                        dvFirma.startNew();
                                        dvFirma.setDrawingCacheEnabled(true);
                                        sendJson(true);
                                    }
                                });
                        progressDialog.show();
                    }else{
                        Dialogos.dialogoErrorConexion(getContext());
                    }

                }
            }
        });
        // TODO: Buton cancelar porceso de img_firma
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarFirma(getContext(), btnCancelar, fragmentManager);
    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_asistencia_comida_entrada, container, false);
    }

    /**
     * Método que se encarga de superponer los fragmentos en el activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {super.onAttach(context);}

    /**
     * Método que elimina el fragmento al desasociarlo de la activity
     */
    @Override
    public void onDetach() {super.onDetach();}

    /**
     * Método que destruye el fragmento
     */
    @Override
    public void onDestroyView() {super.onDestroyView();}

    /**
     * Método donde se pausa el fragmento
     */
    @Override
    public void onPause() {super.onPause();}

    /**
     * Método que se ejecuta para iniciar el fragmento
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     * Método que se ejecuta para detener el fragmento
     */
    @Override
    public void onStop() {super.onStop();}

    /**
     * Método que se ejecuta al accionar la opcion de regreso
     */
    @Override
    public void onResume() {
        LocationManager mlocManager = (LocationManager) getContext().getSystemService(Context.LOCATION_SERVICE);
        boolean enable = mlocManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        if(!enable){
            Dialogos.dialogoActivarLocalizacion(getContext());
        }
        super.onResume();
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
                if(Config.conexion(getContext())){
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

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject ubicacion = new JSONObject();
        String fechaN = "";
        try {
            String fechaS = Config.getFechaFormat();
            fechaN = fechaS.substring(0, fechaS.length() - 2) + ":00";
        }catch (Exception e){
            e.printStackTrace();
        }
        try{
            rqt.put("fechaHoraCheck", fechaN);
            rqt.put("idTipoCheck", 3);
            ubicacion.put("latitud", gps.getLatitude());
            ubicacion.put("longitud", gps.getLongitude());
            rqt.put("ubicacion", ubicacion);
            rqt.put("usuario", Config.usuario(getContext()));
            json.put("rqt", rqt);
            Log.d("TAG", "REQUEST -->" + json);
        } catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_REGISTRAR_ASISTENCIA, json);
    }

    /**
     * Método donde se obtiene el response
     * @param obj
     */
    private void primerPaso(JSONObject obj){
        Log.d("TAG", "primerPaso: "  + obj );
        try{
            String status = obj.getString("status");
            String statusText = obj.getString("statusText");
            if(Integer.parseInt(status) == 200){
                Dialogos.msj(getContext(), "Envio correcto", "Se ha registrado, la salida de comida.\nFecha:" + Dialogos.fechaActual() + " \nhora: " + Config.getHoraActual());
                Config.mensajeRegistro(getContext(), linearLayout, textViewFecha, btnLimpiar, btnGuardar, btnCancelar, dvFirma);
                mySharePreferences.setFechaRegistro(3, Dialogos.fechaActual());
            }else{
                Dialogos.dialogoErrorRespuesta(getContext(),status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
