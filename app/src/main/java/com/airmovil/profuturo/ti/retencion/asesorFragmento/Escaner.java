package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.GPSRastreador;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

public class Escaner extends Fragment {
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private int PHOTO_FILE = 0;
    private int PHOTO_FILE_2 = 0;
    private Button btnCancelar, btnBorrar;
    private Button btnFinalizar;
    private SQLiteHandler db;
    private Fragment borrar = this;
    private String idTramite, nombre, numeroDeCuenta, hora;
    private GPSRastreador gps;
    private int intVal1 = 0; int intVal2 = 0;
    private int x = 0, y = 0;

    private ImageView ifeFrente, ifeVuelta;

    public Escaner() {/* constructor vacio es requerido */}

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
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
        btnCancelar= (Button) view.findViewById(R.id.af_btn_cancelar);
        btnFinalizar = (Button) view.findViewById(R.id.af_btn_guardar);

        // TODO: Clase para obtener las coordenadas
        gps = new GPSRastreador(getContext());

        ifeFrente = (ImageView) rootView.findViewById(R.id.ife_frente);
        ifeVuelta = (ImageView) rootView.findViewById(R.id.ife_vuelta);

        //<editor-fold desc="ife frente">
        ifeFrente.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                progressDialog.setIndeterminate(true);
                progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
                progressDialog.setMessage(getResources().getString(R.string.msj_espera_camara));
                progressDialog.show();
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle ();
                                Intent launchIntent = new Intent ();
                                //Valores por default para el motor
                                launchIntent.setComponent(new ComponentName("mx.com.profuturo.motor", "mx.com.profuturo.motor.CameraUI"));
                                // nombreImagen es el nombre con el que se debe nombrar la imagen resultante del motor de imagen sin extensión
                                // por ejemplo selfie
                                String nombreImagen = "test2";
                                bundle.putString("nombreDocumento", nombreImagen);
                                // ruta destino dentro de las carpetas de motor de imágenes en donde se almacenará el documento
                                // idtramite en este caso sebe ser sustituido por el idTramite que se obtienen el servicio consultarDatosCliente
                                // /mb/premium/rest/consultarDatosCliente
                                bundle.putString("rutaDestino", "idtramite/");
                                // Indicador de que se debe lanzar la cámara
                                bundle.putBoolean("esCamara", true);
                                launchIntent.putExtras(bundle);
                                startActivityForResult(launchIntent, PHOTO_FILE);
                                intVal1 = 1;
                                progressDialog.dismiss();
                            }
                        }, Config.TIME_HANDLER);


                Log.d("bandera1", "" + intVal1);
            }
        });
        //</editor-fold>

        //<editor-fold desc="ife vuelta">
        ifeVuelta.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.Theme_AppCompat_DayNight_Dialog_Alert);
                progressDialog.setIndeterminate(true);
                progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
                progressDialog.setMessage(getResources().getString(R.string.msj_espera_camara));
                progressDialog.show();
                new android.os.Handler().postDelayed(
                        new Runnable() {
                            public void run() {
                                Bundle bundle = new Bundle ();
                                Intent launchIntent = new Intent ();
                                //Valores por default para el motor
                                launchIntent.setComponent(new ComponentName("mx.com.profuturo.motor", "mx.com.profuturo.motor.CameraUI"));
                                // nombreImagen es el nombre con el que se debe nombrar la imagen resultante del motor de imagen sin extensión
                                // por ejemplo selfie
                                String nombreImagen = "test2";
                                bundle.putString("nombreDocumento", nombreImagen);
                                // ruta destino dentro de las carpetas de motor de imágenes en donde se almacenará el documento
                                // idtramite en este caso sebe ser sustituido por el idTramite que se obtienen el servicio consultarDatosCliente
                                // /mb/premium/rest/consultarDatosCliente
                                bundle.putString("rutaDestino", "idtramite/");
                                // Indicador de que se debe lanzar la cámara
                                bundle.putBoolean("esCamara", true);
                                launchIntent.putExtras(bundle);
                                startActivityForResult(launchIntent, PHOTO_FILE);
                                intVal2 = 2;
                                progressDialog.dismiss();
                            }
                        }, Config.TIME_HANDLER);

                Log.d("bandera2", "" + intVal2);
            }
        });
        //</editor-fold>

        // TODO Cancelar el proceso
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1138), 1);

        //<editor-fold desc="btn finalizar">
        btnFinalizar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                sendJson(true, "frente", "reverso");
                /*if(x == 0 && y == 0){
                    Dialogos.dialogoNoExisteIFE(getContext());
                }else if(x == 0) {
                    Dialogos.dialogoNoExisteIFEFrente(getContext());
                }else if(y == 0) {
                    Dialogos.dialogoNoExisteIFEVuelta(getContext());
                }else {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Finalizando");
                    dialogo1.setMessage("Se finalizara el proceso de implicaciones");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            ifeFrente.setDrawingCacheEnabled(true);
                            ifeVuelta.setDrawingCacheEnabled(true);
                            final String base64Frente = Config.encodeTobase64(getContext(), ifeFrente.getDrawingCache());
                            final String base64Reverso = Config.encodeTobase64(getContext(), ifeVuelta.getDrawingCache());

                            ifeFrente.setDrawingCacheEnabled(false);
                            if(connected.estaConectado(getContext())) {
                                sendJson(true, base64Frente, base64Reverso);
                            }else {
                                AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                dialogo.setTitle(getResources().getString(R.string.error_conexion));
                                dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                                dialogo.setCancelable(false);
                                dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        String fechaN = "";
                                        try {
                                            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZ");
                                            f.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
                                            String fechaS = f.format(new Date());
                                            fechaN = fechaS.substring(0, fechaS.length() - 2) + ":00";
                                        }catch (Exception e){
                                            e.printStackTrace();
                                        }

                                        numeroDeCuenta = getArguments().getString("numeroDeCuenta");
                                        db.addDocumento(idTramite,fechaN,1138,base64Frente,numeroDeCuenta,Config.usuarioCusp(getContext()),gps.getLatitude(),gps.getLongitude(), base64Reverso);
                                        db.addIDTramite(idTramite,getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"));
                                        Fragment fragmentoGenerico = new ConCita();
                                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                        if (fragmentoGenerico != null) {
                                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
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
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    dialogo1.show();

                }*/
            }
        });
        //</editor-fold>

    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_escaner, container, false);
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
                        if(intVal1 == 1) {
                            ifeFrente.setImageBitmap(myBitmap);
                            intVal1 = 0;
                            x = 3;
                        }
                        if(intVal2 == 2) {
                            ifeVuelta.setImageBitmap(myBitmap);
                            intVal2 = 0;
                            y = 3;
                        }
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context estado actual de la aplicacion
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
    }

    /**
     *Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {super.onDetach();}

    /**
     * Destruccion de la vista
     */
    @Override
    public void onDestroyView() {super.onDestroyView();}

    /**
     *  Indica que la actividad está a punto de ser lanzada a segundo plano
     */
    @Override
    public void onPause() {super.onPause();}

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
    public void onStop() {super.onStop();}

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
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new ConCita();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 1, fragment);
                    return true;
                }
                return false;
            }
        });
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
                /*if(Config.conexion(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }*/
                NetworkResponse networkResponse = error.networkResponse;
                Log.e("red", "*->" + networkResponse);
                if(networkResponse == null){
                    loading.dismiss();
                }else{
                    Log.e("ok", "++ ");
                }
            }
        };
    }

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, String base64, String base64Reverso) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
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
        try{
            if(getArguments() != null){
                JSONObject rqt = new JSONObject();
                rqt.put("estatusTramite", 1138);
                rqt.put("fechaHoraFin", fechaN);
                rqt.put("idTramite", Integer.parseInt(Config.ID_TRAMITE));
                rqt.put("numeroCuenta", getArguments().getString("numeroDeCuenta"));
                JSONObject ubicacion = new JSONObject();
                ubicacion.put("latitud", gps.getLatitude());
                ubicacion.put("longitud", gps.getLongitude());
                rqt.put("ubicacion", ubicacion);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                rqt.put("ineIfe", base64);
                rqt.put("ineIfeR", base64Reverso);
                obj.put("rqt", rqt);
            }
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_DOCUMENTO_IFE_INE, obj);
    }

    /**
     * Corre este metodo cuando hay mas de 10 contenido a mostrar en la lista
     * @param obj objeto json
     */
    private void primerPaso(JSONObject obj){
        String status = "";
        try{
            status = obj.getString("status");
            if(Config.conexion(getContext())){
                if(Integer.parseInt(status) == 200){
                    android.support.v7.app.AlertDialog.Builder dialogo1 = new android.support.v7.app.AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Datos correctos");
                    dialogo1.setMessage("Los datos han sido recibidos, ha finalizado el proceso de implicaciones, da click en ACEPTAR para finalizar el proceso");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }
                    });
                    dialogo1.show();
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

}
