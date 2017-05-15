package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;

public class AsistenciaEntrada extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    /*inicializacion de los paramentros del fragmento*/
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_LOCATION = 0;
    private GoogleApiClient apiClient;
    private boolean firsStarted = true;
    private Connected connected;
    private DrawingView dvFirma;
    private TextView tvLongitud, tvLatitud;
    private Button btnLimpiar, btnGuardar, btnCancelar;
    private String mParam1;
    private String mParam2;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    private OnFragmentInteractionListener mListener;

    public AsistenciaEntrada() {
        /*constructor vacio requerido*/
    }

    /**
     * al crear una nueva instancia
     * recibe los parametros
     * @param param1 Parametro 1.
     * @param param2 Parametro 2.
     * @return un objeto AsistenciaEntrada.
     */
    public static AsistenciaEntrada newInstance(String param1, String param2) {
        AsistenciaEntrada fragment = new AsistenciaEntrada();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Método principal onCreate
     * @param savedInstanceState
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
     * Método donde se castea los Views del layout
     * y agrega los eventos a los Layout
     * @param view
     * @param savedInstanceState
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        tvLongitud = (TextView) rootView.findViewById(R.id.textViewLogintud1);
        tvLatitud = (TextView) rootView.findViewById(R.id.textViewLatitud1);
        btnLimpiar = (Button) rootView.findViewById(R.id.buttonLimpiar1);
        btnGuardar = (Button) rootView.findViewById(R.id.buttonGuardar1);
        btnCancelar = (Button) rootView.findViewById(R.id.buttonCancelar1);

        dvFirma = (DrawingView) view.findViewById(R.id.drawinView1);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);

        try {
            apiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(), this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }catch (Exception e){
            e.printStackTrace();
        }
        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvFirma.startNew();
                dvFirma.setDrawingCacheEnabled(true);
                Dialogos.dialogoContenidoLimpio(getContext());
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getLongitud1 = tvLongitud.getText().toString();
                String getLatitud1 = tvLatitud.getText().toString();
                if(!dvFirma.isActive()) {
                    Dialogos.dialogoRequiereFirma(getContext());
                }else if(getLatitud1.isEmpty() && getLongitud1.isEmpty()){
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_coordenadas));
                    progressDialog.setTitle(getResources().getString(R.string.msj_titulo_sin_coordenadas));
                    progressDialog.setMessage(getResources().getString(R.string.msj_cotentido_sin_coordenadas));
                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    progressDialog.dismiss();
                                }
                            });
                    progressDialog.show();
                }else {
                    Connected connected = new Connected();
                    if(connected.estaConectado(getContext())){
                        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_ok));
                        progressDialog.setTitle(getResources().getString(R.string.msj_titulo_confirmacion));
                        progressDialog.setMessage(getResources().getString(R.string.msj_contenido_envio) + " registro de entrada");
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
                                    public void onClick(DialogInterface dialog, int which) {

                                    }
                                });
                        progressDialog.show();
                    }

                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_regreso));
                progressDialog.setTitle(getResources().getString(R.string.msj_titulo_aviso));
                progressDialog.setMessage(getResources().getString(R.string.msj_contenido_aviso));
                progressDialog.setButton(DialogInterface.BUTTON1, getResources().getString(R.string.aceptar),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                                Fragment fragmentoGenerico = new Inicio();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                            }
                        });
                progressDialog.setButton(DialogInterface.BUTTON2, getResources().getString(R.string.cancelar),
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                progressDialog.dismiss();
                            }
                        });
                progressDialog.show();

            }
        });
    }

    /**
     * Método que infla la vista del fragmento
     * @param inflater
     * @param container
     * @param savedInstanceState
     * @return
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_asistencia_entrada, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Método que se encarga de superponer los fragmentos en el activity
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
     * Método que elimina el fragmento al desasociarlo de la activity
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Método que destruye el fragmento
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
    }

    /**
     * Método donde se pausa el fragmento
     */
    @Override
    public void onPause() {
        super.onPause();
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
    }

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
    public void onStop() {
        if(apiClient.isConnected())
            apiClient.disconnect();
        firsStarted = true;
        super.onStop();
    }

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
     * Método que verifica si tiene activado el permiso
     * de la localización
     *
     * @param requestCode
     * @param permissions
     * @param grantResults
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                updateUI(lastLocation);
            } else {
                //Permiso denegado deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e("LOGTAG", "Permiso denegado");
            }
        }
    }

    /**
     * Método que sirve para conectar la aplicacion al GPS
     * @param bundle
     */
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        if (ActivityCompat.checkSelfPermission(getContext(), Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(getActivity(), new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);
        } else {
            Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
            updateUI(lastLocation);
        }
    }

    @Override
    public void onConnectionSuspended(int i) {}

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /**
     * esta clase debe ser implementada en las actividades
     * que contengan fragmentos para que exista la
     * comunicacion entre fragmentos
     * para mas informacion ver http://developer.android.com/training/basics/fragments/communicating.html
     * Comunicacion entre fragmentos
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Método que actualiza las coordenadas del GPS
     * @param loc
     */
    private void updateUI(Location loc){
        if (loc != null){
            tvLongitud.setText(String.valueOf(loc.getLatitude()));
            tvLatitud.setText(String.valueOf(loc.getLongitude()));
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
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        double w, z;
        try {
            z = new Double(tvLatitud.getText().toString());
            w = new Double(tvLongitud.getText().toString());
        } catch (NumberFormatException e) {
            z = 0;
            w = 0;
        }

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject ubicacion = new JSONObject();
        String fechaN = "";
        try {
            String fechaS = Config.getFechaFormat();
            fechaN = fechaS.substring(0, fechaS.length() - 2) + ":00";
        }catch (Exception e){
            e.printStackTrace();
            Log.d("TAG e: ", "" + e);
        }
        try{
            rqt.put("fechaHoraCheck", fechaN);
            rqt.put("idTipoCheck", 1);
            ubicacion.put("latitud", z);
            ubicacion.put("longitud", w);
            rqt.put("ubicacion", ubicacion);
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            json.put("rqt", rqt);
            Log.d("TAG", "REQUEST -->" + json);
        } catch (JSONException e){
            Dialogos.msj(getContext(),"Error","Existe un error al formar la peticion");
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
                Dialogos.msj(getContext(), "Envio correcto", "Se ha registrado, la entrada de hoy \nFecha:" + Dialogos.fechaActual() + " \nhora: " +Config.getHoraActual());
            }else{
                Dialogos.msj(getContext(), "Error: " + status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
