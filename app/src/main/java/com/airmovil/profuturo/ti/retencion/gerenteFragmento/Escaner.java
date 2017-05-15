package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
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
import android.widget.ImageView;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Escaner extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    private View rootView;
    private int PHOTO_FILE = 0;
    private Button btnCancelar, btnBorrar, btn;
    private ImageView imageView;
    private Button btnFinalizar;
    private GoogleApiClient apiClient;
    private static final int REQUEST_LOCATION = 0;
    private boolean firsStarted = true;
    private TextView lblLatitud;
    private TextView lblLongitud;
    private SQLiteHandler db;
    private OnFragmentInteractionListener mListener;
    private String idTramite, nombre, numeroDeCuenta, hora;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;
    private double dlongitud, dlatitud;

    public Escaner() {/* Se requiere un constructo vacio */}

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
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
     * El sistema lo llama cuando el fragmento debe diseñar su interfaz de usuario por primera vez
     * @param view accede a la vista del XML
     * @param savedInstanceState fuarda el estado de la instancia
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Asignacion de variables
        variables();
        // TODO: Argumentos, verificacion de datos recibidos de otros fragmentos
        argumentos();

        apiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(),this).addConnectionCallbacks(this).addApi(LocationServices.API).build();

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle ();
                Intent launchIntent = new Intent ();
                //Valores por default para el motor
                launchIntent.setComponent(new ComponentName("mx.com.profuturo.motor", "mx.com.profuturo.motor.CameraUI"));
                // nombreImagen es el nombre con el que se debe nombrar la imagen resultante del motor de imagen sin extensión
                String nombreImagen = "test2";
                bundle.putString ("nombreDocumento", nombreImagen);
                // ruta destino dentro de las carpetas de motor de imágenes en donde se almacenará el documento
                // idtramite en este caso sebe ser sustituido por el idTramite que se obtienen el servicio consultarDatosCliente
                // /mb/premium/rest/consultarDatosCliente
                bundle.putString("rutaDestino", "idtramite/");
                // Indicador de que se debe lanzar la cámara
                bundle.putBoolean("esCamara", true);
                launchIntent.putExtras(bundle);
                startActivityForResult (launchIntent, PHOTO_FILE);
                Log.d("PHOTO_FILE", "" + PHOTO_FILE);
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Estás seguro que deseas cancelar el proceso 1.1.3.8");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = null;
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentoGenerico = new SinCita();
                        if (fragmentoGenerico != null){
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.content_gerente, fragmentoGenerico).addToBackStack("F_MAIN").commit();
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

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView.getDrawable() == null){
                    Dialogos.dialogoNoExisteUnDocumento(getContext());
                }else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Finalizando");
                    dialogo1.setMessage("Se finalizara el proceso de implicaciones");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            imageView.setDrawingCacheEnabled(true);
                            final String base64 = Config.encodeTobase64(getContext(), imageView.getDrawingCache());
                            imageView.setDrawingCacheEnabled(false);

                            if(connected.estaConectado(getContext())) {
                                sendJson(true, base64);
                            }else {
                                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                dialogo.setTitle(getResources().getString(R.string.error_conexion));
                                dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                                dialogo.setCancelable(false);
                                dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {

                                        try {
                                            dlatitud = new Double(lblLatitud.getText().toString());
                                            dlongitud = new Double(lblLongitud.getText().toString());
                                        } catch (NumberFormatException e) {
                                            dlatitud = 0;
                                            dlongitud = 0;
                                        }

                                        String fechaN = "";
                                        try {
                                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                            f.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                                            String fechaS = f.format(new Date());
                                            fechaN = fechaS.substring(0, fechaS.length() - 2) + ":00";
                                            System.out.println(fechaN);
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }
                                        numeroDeCuenta = getArguments().getString("numeroDeCuenta");
                                        db.addDocumento(idTramite,fechaN,1138,base64,numeroDeCuenta,Config.usuarioCusp(getContext()),dlatitud, dlongitud);
                                        db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                        Fragment fragmentoGenerico = new SinCita();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        if (fragmentoGenerico != null) {
                                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                                        }
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
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialogo1.show();
                }
            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(0);
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
        return inflater.inflate(R.layout.gerente_fragmento_escaner, container, false);
    }

    /**
     * Metodo utilizado para obtencion de parametros de aplicaion externa
     * @param requestCode codigo
     * @param resultCode resultado de respuesta
     * @param data datos
     */
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_FILE) {
            if (data != null) {
                try {
                    String nombre = data.getStringExtra("rutaImagen");
                    System.out.print(data.toString());
                    File imgFile = new  File(nombre);
                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(myBitmap);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }
        super.onActivityResult(requestCode, resultCode, data);
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
        if (context instanceof Firma.OnFragmentInteractionListener) {
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
        super.onResume();
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
     * Metodo para obtencion de permiso del dispositivo
     * @param requestCode codigo de respuesta
     * @param permissions permiso
     * @param grantResults resultado
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                updateUI(lastLocation);
            } else {
                //Permiso denegado:
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
     * Metodo con el estado de la conexion suspendida
     * @param i captura el valor de la conexion
     */
    @Override
    public void onConnectionSuspended(int i) {

    }

    /**
     * Metodo, cuando la conexion a fallado
     * @param connectionResult estatus de la conexion
     */
    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {}

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
        btn = (Button) rootView.findViewById(R.id.gf_btn_documento);
        btnCancelar= (Button) rootView.findViewById(R.id.gf_btn_cancelar);
        btnFinalizar = (Button) rootView.findViewById(R.id.gf_btn_guardar);
        btnBorrar= (Button) rootView.findViewById(R.id.gf_btn_borrar);
        imageView = (ImageView) rootView.findViewById(R.id.gf_scannedImage);
        lblLatitud = (TextView) rootView.findViewById(R.id.gff_lbl_LatitudDoc);
        lblLongitud = (TextView) rootView.findViewById(R.id.gff_lbl_LongitudDoc);
        connected = new Connected();
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
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion, String base64) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "espere un momento porfavor...", false, false);
        else
            loading = null;

        double w, z;
        try {
            z = new Double(lblLatitud.getText().toString());
            w = new Double(lblLongitud.getText().toString());
        } catch (NumberFormatException e) {
            z = 0;
            w = 0;
        }

        JSONObject obj = new JSONObject();
        idTramite = getArguments().getString("idTramite");
        String fechaN = "";
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
            f.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
            String fechaS = f.format(new Date());
            fechaN = fechaS.substring(0, fechaS.length() - 2) + ":00";
            System.out.println(fechaN);
            Log.d("TAG fecha ->", "" + fechaN);
        }catch (Exception e){
            e.printStackTrace();
            Log.d("TAG e: ", "" + e);
        }

        // TODO: Formacion del JSON request
        try{
            if(getActivity() != null){
                numeroDeCuenta = getArguments().getString("numeroDeCuenta");
                JSONObject rqt = new JSONObject();
                rqt.put("estatusTramite", 1138);
                rqt.put("fechaHoraFin", fechaN);
                rqt.put("idTramite", Integer.parseInt(idTramite));
                rqt.put("numeroCuenta", numeroDeCuenta);
                JSONObject ubicacion = new JSONObject();
                ubicacion.put("latitud", z);
                ubicacion.put("longitud", w);
                rqt.put("ubicacion", ubicacion);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                rqt.put("ineIfe", base64);
                obj.put("rqt", rqt);
            }
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Dialogos.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_DOCUMENTO_IFE_INE, obj);
    }

    /**
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj){
        String status = "";
        try{
            status = obj.getString("status");
            if(connected.estaConectado(getContext())){
                if(Integer.parseInt(status) == 200){

                    android.app.AlertDialog.Builder dialog  = new android.app.AlertDialog.Builder(getContext());
                    dialog.setTitle("Datos correctos");
                    dialog.setMessage("Los datos han sido recibidos, ha finalizado el proceso de implicaciones, da click en ACEPTAR para finalizar el proceso");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        }
                    });
                    dialog.create().show();

                }else{
                    android.app.AlertDialog.Builder dialog  = new android.app.AlertDialog.Builder(getContext());
                    dialog.setTitle("Error general");
                    dialog.setMessage("Lo sentimos ocurrio un error, favor de validar los datos.");
                    dialog.setCancelable(true);
                    dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        }
                    });
                    dialog.create().show();
                }
            }else{
                android.app.AlertDialog.Builder dialog  = new android.app.AlertDialog.Builder(getContext());
                dialog.setTitle("Error en conexión");
                dialog.setMessage("No se ha podido enviar los datos, ya que existe un problema con la conexión a internet.\nCuando exista conexión se enviaran los datos");
                dialog.setCancelable(true);
                dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new SinCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                    }
                });
                dialog.create().show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }


}
