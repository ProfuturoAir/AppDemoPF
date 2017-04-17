package com.airmovil.profuturo.ti.retencion.asesorFragmento;

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
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.ToggleButton;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
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

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;
import java.util.TimeZone;

import java.util.Date;
import java.util.TimeZone;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AsistenciaEntrada.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AsistenciaEntrada#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AsistenciaEntrada extends Fragment implements GoogleApiClient.OnConnectionFailedListener,
        GoogleApiClient.ConnectionCallbacks{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final int REQUEST_LOCATION = 0;
    private GoogleApiClient apiClient;
    private boolean firsStarted = true;
    private DrawingView dvFirma;
    private TextView tvLongitud, tvLatitud;
    private Button btnLimpiar, btnGuardar, btnCancelar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private View rootView;

    private OnFragmentInteractionListener mListener;

    public AsistenciaEntrada() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment AsistenciaEntrada.
     */
    // TODO: Rename and change types and number of parameters
    public static AsistenciaEntrada newInstance(String param1, String param2) {
        AsistenciaEntrada fragment = new AsistenciaEntrada();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;

        tvLongitud = (TextView) rootView.findViewById(R.id.textViewLogintud1);
        tvLatitud = (TextView) rootView.findViewById(R.id.textViewLatitud1);
        btnLimpiar = (Button) rootView.findViewById(R.id.buttonLimpiar1);
        btnGuardar = (Button) rootView.findViewById(R.id.buttonGuardar1);
        btnCancelar = (Button) rootView.findViewById(R.id.buttonCancelar1);

        dvFirma = (DrawingView) view.findViewById(R.id.drawinView1);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);

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
                Config.dialogoContenidoLimpio(getContext());
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String getLongitud1 = tvLongitud.getText().toString();
                String getLatitud1 = tvLatitud.getText().toString();
                if(!dvFirma.isActive()) {
                    Config.dialogoRequiereFirma(getContext());
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
                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_ok));
                    progressDialog.setTitle(getResources().getString(R.string.msj_titulo_confirmacion));
                    progressDialog.setMessage(getResources().getString(R.string.msj_contenido_envio) + " registro de salida");
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


                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

            }
        });
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_asistencia_entrada, container, false);
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
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length == 1 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                //Permiso concedido
                @SuppressWarnings("MissingPermission")
                Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
                updateUI(lastLocation);
            } else {
                //Permiso denegado:
                //Deberíamos deshabilitar toda la funcionalidad relativa a la localización.
                Log.e("LOGTAG", "Permiso denegado");
            }
        }
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
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

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

    private void updateUI(Location loc){
        if (loc != null){
            tvLongitud.setText(String.valueOf(loc.getLatitude()));
            tvLatitud.setText(String.valueOf(loc.getLongitude()));
            Log.d("------->", "\n" + loc.getLongitude());
            Log.d("------->", "\n" + loc.getLatitude());
        }
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        Map<String, String> usuarioDatos = Config.datosUsuario(getContext());
        Map<String, String> fechaActual = Config.fechas(1);
        String fecha = fechaActual.get("fechaIni");
        String idUsuario = usuarioDatos.get(SessionManager.USUARIO_USER_ID);

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


        String fechaFirma = "";
        try {
            SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSSZZZ");
            f.setTimeZone(TimeZone.getTimeZone("America/Mexico_City"));
            String fechaFecha = f.format(new Date());
            //System.out.println(fecha);
            Log.d("TAG ->", "" + fechaFecha);
        } catch (Exception e){
            Log.d("TAG ->", "" +e.toString());
            fechaFirma = "123";
        }

        try{
            rqt.put("fechaHoraCheck", fechaFirma);
            rqt.put("idTipoCheck", 1);
            ubicacion.put("latitud", z);
            ubicacion.put("longitud", w);
            rqt.put("ubicacion", ubicacion);
            rqt.put("usuario", idUsuario);
            json.put("rqt", rqt);
            Log.d("TAG", "REQUEST -->" + json);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_REGISTRAR_ASISTENCIA, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj){
        Log.d("TAG", "primerPaso: "  + obj );
        Map<String, String> fechaActual = Config.fechas(1);
        String fecha = fechaActual.get("fechaIni");
        Calendar calendario = Calendar.getInstance();
        int hora, minutos, segundos;


        hora =calendario.get(Calendar.HOUR_OF_DAY);
        minutos = calendario.get(Calendar.MINUTE);
        segundos = calendario.get(Calendar.SECOND);


        String status = "";
        String statusText = "";
        try{
            status = obj.getString("status");
            statusText = obj.getString("statusText");
            if(Integer.parseInt(status) == 200){
                Config.msj(getContext(), "Envio correcto", "Se ha registrado, la salida de hoy \nFecha:" + fecha + " \nhora: " + hora+":"+minutos+":"+segundos);
            }else{
                Config.msj(getContext(), "Error: " + status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }
}
