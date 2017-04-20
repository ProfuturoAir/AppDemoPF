package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
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
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatosAsesor.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatosAsesor#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatosAsesor extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    public static final String TAG = DatosAsesor.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private View rootView;
    private TextView tvNombre, tvNumeroEmpleado, tvSucursal;
    private Button btnContinuar, btnCancelar;
    private SessionManager sessionManager;

    String nombre;
    String numeroDeCuenta;
    String hora;
    String idClienteCuenta;

    private OnFragmentInteractionListener mListener;

    public DatosAsesor() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatosAsesor.
     */
    // TODO: Rename and change types and number of parameters
    public static DatosAsesor newInstance(String param1, String param2) {
        DatosAsesor fragment = new DatosAsesor();
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
        primeraPeticion();
        variables();

        sessionManager = new SessionManager(getActivity().getApplicationContext());

        idClienteCuenta =getArguments().getString("idClienteCuenta");
        nombre = getArguments().getString("nombre");
        numeroDeCuenta = getArguments().getString("numeroDeCuenta");
        hora = getArguments().getString("hora");

        Log.d("NOMBRES 1 ++", "1" + nombre + " numero" + numeroDeCuenta);

        // TODO: Fragmentos
        final Fragment fragmentoDatosCliente = new DatosCliente();
        final FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

        // TODO: obteniendo el numero del usuario
        HashMap<String, String> hashMap = sessionManager.getUserDetails();
        String numeroUsuario = hashMap.get(SessionManager.ID);


        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    //fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoDatosCliente).commit();
                    Fragment fragmentoGenerico = new DatosCliente();
                    Asesor asesor = (Asesor) getContext();
                    asesor.switchDatosCliente(fragmentoGenerico,nombre,numeroDeCuenta,hora);
                }else {

                    final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                    progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_sin_wifi));
                    progressDialog.setTitle(getResources().getString(R.string.error_conexion));
                    progressDialog.setMessage(getResources().getString(R.string.msj_error_conexion));
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

                    /*android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                    dlgAlert.setTitle("Error de conexión");
                    dlgAlert.setMessage("Se ha encontrado un problema, debes revisar tu conexión a internet");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //Fragment fragmentoGenerico = new DatosCliente();
                            //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            //fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                            Fragment fragmentoGenerico = new DatosCliente();
                            Asesor asesor = (Asesor) getContext();
                            asesor.switchDatosCliente(fragmentoGenerico,nombre,numeroDeCuenta,hora);
                        }
                    });
                    dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dlgAlert.create().show();*/



                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas salir?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new ConCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager
                                .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                .replace(R.id.content_asesor, fragmentoGenerico).commit();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
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
        return inflater.inflate(R.layout.asesor_fragmento_datos_asesor, container, false);
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
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle("Confirmar");
                    dialogo.setMessage("¿Estás seguro que deseas regresar?");
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                        .replace(R.id.content_asesor, fragmentoGenerico).commit();
                            }
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

    private void primeraPeticion(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIcon(R.drawable.icono_abrir);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_espera));
        progressDialog.show();
        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        sendJson(true);
                    }
                }, 500);
    }

    private void variables(){
        tvNombre = (TextView) rootView.findViewById(R.id.afda_tv_nombre_usuario);
        tvNumeroEmpleado = (TextView) rootView.findViewById(R.id.afda_tv_numero_empleado);
        tvSucursal = (TextView) rootView.findViewById(R.id.afda_tv_sucursal);
        btnContinuar = (Button) rootView.findViewById(R.id.afda_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afda_btn_cancelar);
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        JSONObject obj = new JSONObject();
        // TODO: Formacion del JSON request
        JSONObject rqt = new JSONObject();
        try{
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("DatosAsesor", ":rqt -->" + obj);
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_DATOS_ASESOR, obj,
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
                        try{
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Connected connected = new Connected();
                        if(connected.estaConectado(getContext())){
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error123");
                            dlgAlert.setMessage("Se ha encontrado un problema, deseas volver intentarlo");
                            dlgAlert.setCancelable(true);
                            dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    sendJson(true);
                                }
                            });
                            dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dlgAlert.create().show();
                        }else{
                            final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
                            progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_sin_wifi));
                            progressDialog.setTitle(getResources().getString(R.string.error_conexion));
                            progressDialog.setMessage(getResources().getString(R.string.msj_error_conexion));
                            progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            progressDialog.dismiss();
                                            sendJson(true);
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
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "-----> >" + obj);

        String nombreCliente = "";
        String numeroCuenta = "";
        String status = "";
        String statusText ="";
        String sucursal ="";

        try{
            status = obj.getString("status");
            statusText = obj.getString("statusText");
            if(Integer.parseInt(status) == 200){
                Log.d(TAG, status.toString());
                JSONObject jsonAsesor = obj.getJSONObject("asesor");
                nombreCliente = jsonAsesor.getString("nombre");
                numeroCuenta = jsonAsesor.getString("numeroEmpleado");
                sucursal = jsonAsesor.getString("nombreSucursal");
            }else{
                android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                dlgAlert.setTitle(status);
                dlgAlert.setMessage(statusText);
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendJson(true);
                    }
                });
                dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dlgAlert.create().show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvNombre.setText(nombreCliente);
        tvNumeroEmpleado.setText(numeroCuenta);
        tvSucursal.setText(sucursal);
    }

}
