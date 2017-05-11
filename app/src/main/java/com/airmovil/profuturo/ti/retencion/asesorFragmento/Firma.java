package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
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
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.ByteArrayOutputStream;

public class Firma extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    /* inicializacion de los paramentros del fragmento*/
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_LOCATION = 0;
    private DrawingView dvFirma;
    private TextView lblLatitud;
    private TextView lblLongitud;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    // TODO: Rename and change types of parameters
    private GoogleApiClient apiClient;
    private OnFragmentInteractionListener mListener;
    private boolean firsStarted = true;
    private Connected connected;
    private SQLiteHandler db;
    String idTramite;
    String nombre;
    String numeroDeCuenta;
    String hora;

    public Firma() {
        /* constructor vacio es requerido*/
    }

    /**
     * al crear una nueva instancia
     * se reciben los paramentros:
     * @param param1 Parametro 1.
     * @param param2 Parametro 2.
     * @return un objeto Firma.
     */
    // TODO: Rename and change types and number of parameters
    public static Firma newInstance(String param1, String param2) {
        Firma fragment = new Firma();
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
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, view.getContext());
        Button btnLimpiar = (Button) view.findViewById(R.id.aff_btn_limpiar);
        Button btnGuardar = (Button) view.findViewById(R.id.aff_btn_guardar);
        Button btnCancelar= (Button) view.findViewById(R.id.aff_btn_cancelar);
        connected = new Connected();
        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }
        lblLatitud = (TextView) view.findViewById(R.id.aff_lbl_Latitud);
        lblLongitud = (TextView) view.findViewById(R.id.aff_lbl_Longitud);
        try{
            apiClient = new GoogleApiClient.Builder(getActivity()).enableAutoManage(getActivity(),this).addConnectionCallbacks(this).addApi(LocationServices.API).build();
        } catch (Exception e){}
        dvFirma = (DrawingView) view.findViewById(R.id.aff_dv_firma);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);
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
                        Fragment fragmentoGenerico = new ConCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        if (fragmentoGenerico != null) {
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                dialogo1.show();
            }
        });
        final Fragment borrar = this;
        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String latitud = (String) lblLatitud.getText();
                final String longitud = (String) lblLongitud.getText();
                if(!dvFirma.isActive()) {
                    Config.msj(v.getContext(),"Error", "Se requiere una firma");
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
                                final String base64 = encodeTobase64(dvFirma.getDrawingCache());
                                Bitmap emBit = Bitmap.createBitmap(dvFirma.getWidth(), dvFirma.getHeight(), Bitmap.Config.ARGB_8888);
                                Log.d("BASE64-->", base64);
                                dvFirma.setDrawingCacheEnabled(false);
                                if(connected.estaConectado(getContext())) {
                                    sendJson(true, base64);
                                    loading.dismiss();
                                    final EnviaJSON enviaPrevio = new EnviaJSON();
                                    Fragment fragmentoGenerico = new Escaner();
                                    Asesor asesor = (Asesor) getContext();
                                    asesor.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
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
                                            Asesor asesor = (Asesor) getContext();
                                            asesor.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
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
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialogo1.show();
                    }else{
                        Log.d("false","2");
                    }
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // infla el layout del fragmento firma
        return inflater.inflate(R.layout.asesor_fragmento_firma, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
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
                    dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.7?");
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
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, String firmaIMG) {

        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        String latitud = lblLatitud.getText().toString();
        String lonngitud = lblLongitud.getText().toString();
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
            rqt.put("firmaCliente", firmaIMG);
            obj.put("rqt", rqt);
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_FIRMA, obj);
    }
}
