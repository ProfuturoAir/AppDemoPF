package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

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
import android.widget.ToggleButton;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
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
import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Firma.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Firma#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Firma extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_LOCATION = 0;
    DrawingView dvFirma;
    View mView;
    TextView lblLatitud;
    TextView lblLongitud;
    ToggleButton btnActualizar;
    Button btnLimpiar, btnGuardar, btnCancelar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleApiClient apiClient;
    private View rootView;
    private boolean firsStarted = true;

    private OnFragmentInteractionListener mListener;

    private Connected connected;
    private SQLiteHandler db;

    String idTramite;
    String nombre;
    String numeroDeCuenta;
    String hora;

    public Firma() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Firma.
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
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        btnLimpiar = (Button) view.findViewById(R.id.gff_btn_limpiar);
        btnGuardar = (Button) view.findViewById(R.id.gff_btn_guardar);
        btnCancelar= (Button) view.findViewById(R.id.gff_btn_cancelar);
        lblLatitud = (TextView) view.findViewById(R.id.gff_lbl_Latitud);
        lblLongitud = (TextView) view.findViewById(R.id.gff_lbl_Longitud);
        btnActualizar = (ToggleButton) view.findViewById(R.id.gff_btn_actualizar);

        dvFirma = (DrawingView) view.findViewById(R.id.gff_dv_Firma);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);

        connected = new Connected();

        apiClient = new GoogleApiClient.Builder(getActivity())
                .enableAutoManage(getActivity(),this)
                .addConnectionCallbacks(this)
                .addApi(LocationServices.API)
                .build();

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvFirma.startNew();
                dvFirma.setDrawingCacheEnabled(true);
                Config.msjTime(v.getContext(), "Mensaje", "Limpiando contenido", 2000);
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
                }else if(latitud.equals("(desconocida)")||longitud.equals("(desconocida)")){

                    /*
                    final Connected conected = new Connected();
                    if(conected.estaConectado(v.getContext())) {
                    }else{
                        Config.mensajeError(v.getContext(), "Sin Conexion por el momento.Firma P-1.1.3.7");
                    }
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Importante");
                    dialogo1.setMessage("¿Guardar esta firma?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dvFirma.setDrawingCacheEnabled(true);
                            String base64 = encodeTobase64(dvFirma.getDrawingCache());
                            Log.d("BASE64-->", base64);
                            dvFirma.setDrawingCacheEnabled(false);
                            Fragment fragmentoGenerico = new Documento();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager.beginTransaction().replace(R.id.asesor_content, fragmentoGenerico).commit();
                            }
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialogo1.show();
                    */
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
                                //Log.d("BASE64-->", base64);
                                dvFirma.setDrawingCacheEnabled(false);
                                if(connected.estaConectado(getContext())) {
                                    sendJson(true);
                                    final EnviaJSON enviaPrevio = new EnviaJSON();
                                    enviaPrevio.sendPrevios(idTramite, getContext());
                                    // * Fragment fragmentoGenerico = new Escaner();
                                    // *FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                    // *fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                                    Fragment fragmentoGenerico = new Escaner();
                                    Gerente gerente = (Gerente) getContext();
                                    gerente.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                                }else{

                                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                                    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_sin_wifi));
                                    progressDialog.setTitle(getResources().getString(R.string.error_conexion));
                                    progressDialog.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                                    progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                                            new DialogInterface.OnClickListener() {
                                                @Override
                                                public void onClick(DialogInterface dialog, int which) {
                                                    progressDialog.dismiss();
                                                    db.addFirma(idTramite,123,base64,90.2349,-23.9897);
                                                    db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                                    Fragment fragmentoGenerico = new Escaner();
                                                    Gerente gerente = (Gerente) getContext();
                                                    gerente.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
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
                                /*Fragment fragmentoGenerico = new Escaner();
                                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                                if (fragmentoGenerico != null) {
                                    fragmentManager
                                            .beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                                }*/
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

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.7?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new SinCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
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
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gerente_fragmento_firma, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
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
        apiClient.stopAutoManage(getActivity());
        apiClient.disconnect();
        mListener = null;
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        /*if (ActivityCompat.checkSelfPermission(getContext(),
                Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(getActivity(),
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    REQUEST_LOCATION);
        } else {

            Location lastLocation =
                    LocationServices.FusedLocationApi.getLastLocation(apiClient);

            updateUI(lastLocation);
        }*/
    }

    public String encodeTobase64(Bitmap image) {
        //Bitmap immagex = image;
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);*/

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
            //resize =Bitmap.createScaledBitmap(image, 200,200, true);
        }else{
            Log.d("PIXELES", "PASA SIN CAMBIO" );
            resize = image;
        }

        //resize =Bitmap.createScaledBitmap(image, 500,500, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        //String imageEncoded = Base64.encode(b);
        //String imageEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        //Log.e("LOOK", imageEncoded);
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        //Log.d("ARRAY","BASE64:"+encImage);
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


    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1
                    && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation =
                        LocationServices.FusedLocationApi.getLastLocation(apiClient);
                updateUI(lastLocation);
            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e("LOGTAG", "Permiso denegado");
            }
        }
    }
/*
    @Override
    public void onStop() {
        if(apiClient.isConnected())
            apiClient.disconnect();
        firsStarted = true;
        super.onStop();
    }*/

    private void updateUI(Location loc){
        if (loc != null){
            lblLatitud.setText(String.valueOf(loc.getLatitude()));
            lblLongitud.setText(String.valueOf(loc.getLongitude()));
        }/*else{
            lblLatitud.setText("(desconocida)");
            lblLongitud.setText("(desconocida)");
        }*/

        Log.d("latitud", "" + loc.getLatitude());
        Log.d("longitud", "" + loc.getLongitude());
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
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
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                        .replace(R.id.content_gerente, fragmentoGenerico).commit();
                            }
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

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            JSONObject encuesta = new JSONObject();
            encuesta.put("observaciones", "observaciones mensaje de prueba");
            encuesta.put("pregunta3", true);
            encuesta.put("pregunta2", true);
            encuesta.put("pregunta1", true);
            rqt.put("encuesta", encuesta);
            rqt.put("estatusTramite", 123);
            rqt.put("idTramite", "1");
            obj.put("rqt", rqt);
            Log.d("TAG", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_ENCUESTA, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un right_in, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj){
        Log.d("TAG", "RESPONSE: ->" + obj);
    }
}
