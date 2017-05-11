package com.airmovil.profuturo.ti.retencion.asesorFragmento;

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
import android.util.Base64;
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
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class
Escaner extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks {
    /* inicializacion de los paramentros del fragmento*/
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    int PHOTO_FILE = 0;
    private Button btnCancelar;
    private Button btnBorrar;
    private ImageView imageView;
    private Button btnFinalizar;
    private GoogleApiClient apiClient;
    private static final int REQUEST_LOCATION = 0;
    private boolean firsStarted = true;
    private TextView lblLatitud;
    private TextView lblLongitud;
    private Connected connected;
    private SQLiteHandler db;

    private OnFragmentInteractionListener mListener;

    String idTramite;
    String nombre;
    String numeroDeCuenta;
    String hora;

    public Escaner() {
        /* constructor vacio es requerido */
    }

    /**
     * al crear una nueva instancia
     * recibe los parametros
     * @param param1 Parametro 1.
     * @param param2 Parametro 2.
     * @return un objeto AsistenciaEntrada.
     */
    public static Escaner newInstance(String param1, String param2) {
        Escaner fragment = new Escaner();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        Button btn = (Button) rootView.findViewById(R.id.btn_documento);
        btnCancelar= (Button) view.findViewById(R.id.af_btn_cancelar);
        btnFinalizar = (Button) view.findViewById(R.id.af_btn_guardar);
        btnBorrar= (Button) view.findViewById(R.id.af_btn_borrar);
        imageView = (ImageView) rootView.findViewById(R.id.scannedImage);
        lblLatitud = (TextView) view.findViewById(R.id.aff_lbl_LatitudDoc);
        lblLongitud = (TextView) view.findViewById(R.id.aff_lbl_LongitudDoc);
        connected = new Connected();
        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

        try{
            apiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(),this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        } catch (Exception e){}
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle ();
                Intent launchIntent = new Intent ();
                //Valores por default para el motor
                launchIntent.setComponent(new ComponentName("mx.com.profuturo.motor", "mx.com.profuturo.motor.CameraUI"));
                // nombreImagen es el nombre con el que se debe nombrar la imagen resultante del motor de imagen sin extensión
                // por ejemplo selfie
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
                dialogo1.setMessage("¿Estás seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.8");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Fragment fragmentoGenerico = null;
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        final Fragment borrarFragmento;
                        fragmentoGenerico = new ConCita();
                        if (fragmentoGenerico != null){
                            fragmentManager.beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right).replace(R.id.content_asesor, fragmentoGenerico).addToBackStack("F_MAIN").commit();
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

        final Fragment borrar = this;

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(imageView.getDrawable() == null){
                    Config.dialogoNoExisteUnDocumento(getContext());
                }else {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Finalizando");
                    dialogo1.setMessage("Se finalizara el proceso de implicaciones");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            imageView.setDrawingCacheEnabled(true);
                            final String base64 = encodeTobase64(imageView.getDrawingCache());
                            Log.d("BASE64-->", base64);
                            imageView.setDrawingCacheEnabled(false);

                            if(connected.estaConectado(getContext())) {
                                sendJson(true, base64);
                                final EnviaJSON enviaPrevio = new EnviaJSON();
                            }else {
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

                                        String fechaN = "";
                                        try {
                                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                            f.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                                            String fechaS = f.format(new Date());
                                            fechaN = fechaS.substring(0, fechaS.length() - 2) + ":00";
                                        }catch (Exception e){
                                            e.printStackTrace();
                                            Log.d("TAG e: ", "" + e);
                                        }

                                        numeroDeCuenta = getArguments().getString("numeroDeCuenta");
                                        db.addDocumento(idTramite,fechaN,1138,base64,numeroDeCuenta,Config.usuarioCusp(getContext()),z, w);
                                        db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                        Fragment fragmentoGenerico = new ConCita();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        if (fragmentoGenerico != null) {
                                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                                        }
                                    }
                                });
                                dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {


                                    }
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


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_escaner, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PHOTO_FILE) {
            if (data != null) {
                try {
                    String nombre = data.getStringExtra("rutaImagen");
                    System.out.print(data.toString());
                    Log.d("data -->",  data.toString());
                    Log.d("test", nombre);
                    // Se obtiene la ruta de la imagen con extensión .jpg incluida
                    String nombre1 = data.getStringExtra("rutaImagen");
                    // En nuesto caso se utiliza la libreria Picasso para mostrar la imagen
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof Firma.OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
    }

    @Override
    public void onPause() {
        super.onPause();
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onStop() {
        if(apiClient.isConnected())
            apiClient.disconnect();
        firsStarted = true;
        super.onStop();
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
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Confirmar");
                    dialogo1.setMessage("¿Estás seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.8?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();

                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    dialogo1.show();
                    return true;
                }
                return false;
            }
        });
    }

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

    private void updateUI(Location loc){
        if (loc != null){
            lblLatitud.setText(String.valueOf(loc.getLatitude()));
            lblLongitud.setText(String.valueOf(loc.getLongitude()));
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
    private void sendJson(final boolean primerPeticion, String base64) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
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
        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
        }
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
        try{
            if(getArguments() != null){
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
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_DOCUMENTO_IFE_INE, obj);
    }

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
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
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
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
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
                        Fragment fragmentoGenerico = new ConCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                    }
                });
                dialog.create().show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

    public String encodeTobase64(Bitmap image) {
        float bmW=image.getWidth();
        float bmH= image.getHeight();

        Log.d("PIXELES", "ORIGINAL ANCHO"+bmW+"Original ALTO:"+bmH );
        int widthPixels = getContext().getResources().getDisplayMetrics().widthPixels;
        Bitmap resize;
        Log.d("PIXELES", "TELEFONO"+widthPixels );
        if(bmW>=widthPixels){
            float newWidth=widthPixels;
            float newHeight=(bmH/bmW)*widthPixels;

            Log.d("PIXELES", "NUEVO ANCHO" + widthPixels + "NUEVO ALTO:" + newHeight + " W" + bmW + " H" + bmH);
            //resize the bit map
            resize = Bitmap.createBitmap(image,0,0,(int)newWidth,(int)newHeight);
        }else{
            Log.d("PIXELES", "PASA SIN CAMBIO" );
            resize = image;
        }
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        imageEncoded = imageEncoded.replace(" ","");
        String foto="data:image/jpeg;base64,"+imageEncoded;

        int maxLogSize = 1000;
        for(int i = 0; i <= foto.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > foto.length() ? foto.length() : end;
            Log.d("n-"+i, foto.substring(start, end));
        }
        return foto;
    }
}
