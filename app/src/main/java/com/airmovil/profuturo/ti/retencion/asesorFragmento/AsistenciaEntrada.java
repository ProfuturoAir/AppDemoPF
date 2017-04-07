package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.system.ErrnoException;
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
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationServices;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link AsistenciaEntrada.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link AsistenciaEntrada#newInstance} factory method to
 * create an instance of this fragment.
 */
public class AsistenciaEntrada extends Fragment implements GoogleApiClient.OnConnectionFailedListener, GoogleApiClient.ConnectionCallbacks{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private static final int REQUEST_LOCATION = 0;
    private DrawingView dvFirma;
    private View mView;
    private TextView lblLatitud;
    private TextView lblLongitud;
    private ToggleButton btnActualizar;
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private GoogleApiClient apiClient;
    private View rootView;

    private boolean firsStarted = true;

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

        Button btnLimpiar = (Button) view.findViewById(R.id.afae_btn_limpiar);
        Button btnGuardar = (Button) view.findViewById(R.id.afae_btn_guardar);
        Button btnCancelar= (Button) view.findViewById(R.id.afae_btn_cancelar);

        try{
            apiClient = new GoogleApiClient.Builder(getActivity())
                    .enableAutoManage(getActivity(),this)
                    .addConnectionCallbacks(this)
                    .addApi(LocationServices.API)
                    .build();
        }catch (Exception e){
            e.printStackTrace();
        }

        lblLatitud = (TextView) view.findViewById(R.id.afae_lbl_Latitud);
        lblLongitud = (TextView) view.findViewById(R.id.afae_lbl_Longitud);
        btnActualizar = (ToggleButton) view.findViewById(R.id.afae_btn_actualizar);

        dvFirma = (DrawingView) view.findViewById(R.id.afae_dv_firma);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);

        //final SignatureView signatureView = (SignatureView) view.findViewById(R.id.afae_signature_view);
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
                dialogo1.setMessage("¿Estás seguro que deseas cancelar y se perderan los cambios?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new Inicio();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        if (fragmentoGenerico != null) {
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
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

                }
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
    public void onConnected(@Nullable Bundle bundle) {

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {

    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
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

    private void updateUI(Location loc){
        if (loc != null){
            lblLatitud.setText(String.valueOf(loc.getLatitude()));
            lblLongitud.setText(String.valueOf(loc.getLongitude()));
        }/*else{
            lblLatitud.setText("(desconocida)");
            lblLongitud.setText("(desconocida)");
        }*/
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
}
