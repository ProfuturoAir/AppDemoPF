package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

import org.json.JSONException;
import org.json.JSONObject;

public class Firma extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    private static final String TAG = Firma.class.getSimpleName();
    private static final int REQUEST_LOCATION = 0;
    private DrawingView dvFirma;
    private TextView lblLatitud,lblLongitud;
    private GoogleApiClient apiClient;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private boolean firsStarted = true;
    private Connected connected;
    private SQLiteHandler db;
    private String idTramite, nombre, numeroDeCuenta, hora;
    private Button btnLimpiar, btnGuardar, btnCancelar;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public Firma() {/* Se requiere un constructor vacio */}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Argumentos, verfica si existen datos enviado de otras clases
        argumentos();
        // TODO: Variables
        variables();

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvFirma.startNew();
                dvFirma.setDrawingCacheEnabled(true);
                Config.msjTime(v.getContext(), "Mensaje", "Limpiando contenido", 2000);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.7?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new SinCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        if (fragmentoGenerico != null) {
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        }
                    }
                });

                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialogo1.show();
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String latitud = (String) lblLatitud.getText();
                final String longitud = (String) lblLongitud.getText();
                if(!dvFirma.isActive()) {
                    Config.msj(v.getContext(),"Error", "Se requiere una firma");
                }else if(latitud.equals("(desconocida)")||longitud.equals("(desconocida)")){
                    Log.d("Coordenadas", "nulas");
                }else{
                    if(dvFirma.isActive()){
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                        dialogo1.setTitle("Importante");
                        dialogo1.setMessage("¿Guardar esta firma?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dvFirma.setDrawingCacheEnabled(true);
                                final String base64 = Config.encodeTobase64(getContext(), dvFirma.getDrawingCache());
                                dvFirma.setDrawingCacheEnabled(false);
                                if(connected.estaConectado(getContext())) {
                                    if(Config.estahabilitadoGPS(getContext())){
                                        sendJson(true, base64);
                                        Fragment fragmentoGenerico = new Escaner();
                                        Gerente gerente = (Gerente) getContext();
                                        gerente.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                                    }else{
                                        Dialogos.dialogoActivarLocalizacion(getContext());
                                    }

                                }else{
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                    dialogo.setTitle(getResources().getString(R.string.error_conexion));
                                    dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            double w, z;
                                            try {
                                                z = new Double(lblLatitud.getText().toString());
                                                w = new Double(lblLongitud.getText().toString());
                                            } catch (NumberFormatException e) {
                                                z = 0;
                                                w = 0;
                                            }

                                            db.addFirma(idTramite,1137,base64,z,w);
                                            db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                            Fragment fragmentoGenerico = new Escaner();
                                            Gerente gerente = (Gerente) getContext();
                                            gerente.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                                        }
                                    });
                                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}
                                    });
                                    dialogo.show();
                                }
                            }
                        });
                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                        dialogo1.show();
                    }
                }
            }
        });
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
        return inflater.inflate(R.layout.gerente_fragmento_firma, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * El fragment se ha adjuntado al Activity
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
     * El Fragment ha sido quitado de su Activity y ya no está disponible
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Destruccion de la vista
     */
    @Override
    public void onDestroyView() {
        super.onDestroyView();
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
    }

    /**
     *  Indica que la actividad está a punto de ser lanzada a segundo plano
     */
    @Override
    public void onPause() {
        super.onPause();
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
    }

    /**
     * Nos indica que la actividad está a punto de ser mostrada al usuario.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     *  La actividad ya no va a ser visible para el usuario
     */
    @Override
    public void onStop() {
        if(apiClient.isConnected())
            apiClient.disconnect();
        firsStarted = true;
        super.onStop();
    }

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onResume() {
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle("Confirmar");
                    dialogo.setMessage("¿Estás seguro que deseas salir del proceso de implicaciones?");
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        }
                    });
                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialogo.show();
                    return true;
                }
                return false;
            }
        });
        super.onResume();
    }

    /**
     * Metodo para verificar la conexion a la red
     * @param bundle envio de parametros
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

    /**
     * Metodo con el estado de la conexion suspendida
     * @param i captura el valor de la conexion
     */
    @Override
    public void onConnectionSuspended(int i) {}

    /**
     * Metodo, cuando la conexion a fallado
     * @param connectionResult estatus de la conexion
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

    /**
     * Metodo para obtencion de permiso del dispositivo
     * @param requestCode codigo de respuesta
     * @param permissions permiso
     * @param grantResults resultado
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
                //Permiso denegado: Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e("LOGTAG", "Permiso denegado");
            }
        }
    }

    /**
     * Metodo para obtener la localizacion del dispositivo
     * @param loc accede a las coordenadas del dispositivo
     */
    private void updateUI(Location loc){
        if (loc != null){
            lblLatitud.setText(String.valueOf(loc.getLatitude()));
            lblLongitud.setText(String.valueOf(loc.getLongitude()));
        }
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
     * Setear las variables de xml y nueva instancia de objeto
     */
    private void variables(){
        connected = new Connected();
        apiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(),this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        btnLimpiar = (Button) rootView.findViewById(R.id.gff_btn_limpiar);
        btnGuardar = (Button) rootView.findViewById(R.id.gff_btn_guardar);
        btnCancelar= (Button) rootView.findViewById(R.id.gff_btn_cancelar);
        lblLatitud = (TextView) rootView.findViewById(R.id.gff_lbl_Latitud);
        lblLongitud = (TextView) rootView.findViewById(R.id.gff_lbl_Longitud);
        dvFirma = (DrawingView) rootView.findViewById(R.id.gff_dv_firma);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);
    }

    /**
     * Se utiliza para cololar datos recibidos entre una busqueda(por ejemplo: fechas)
     */
    private void argumentos(){
        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
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
    private void sendJson(final boolean primerPeticion, String firmaBase64) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        idTramite = getArguments().getString("idTramite");
        double w, z;
        try {
            z = new Double(lblLatitud.getText().toString());
            w = new Double(lblLongitud.getText().toString());
        } catch (NumberFormatException e) {
            z = 0;
            w = 0;
        }
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 1137);
            rqt.put("idTramite", Integer.parseInt(idTramite));
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", z);
            ubicacion.put("longitud", w);
            rqt.put("ubicacion", ubicacion);
            rqt.put("firmaCliente", firmaBase64);
            obj.put("rqt", rqt);
            Log.d(TAG, "<- RQT ->" + obj);
        } catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_FIRMA, obj);
    }

    /**
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj) {

    }
}
