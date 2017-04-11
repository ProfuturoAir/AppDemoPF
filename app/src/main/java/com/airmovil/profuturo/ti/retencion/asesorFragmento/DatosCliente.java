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
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link DatosCliente.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link DatosCliente#newInstance} factory method to
 * create an instance of this fragment.
 */
public class DatosCliente extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = DatosCliente.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    // TODO: XML
    private TextView tvClienteNombre, tvClienteNumeroCuenta, tvClienteNSS, tvClienteCURP, tvClienteFecha, tvClienteSaldo;
    private Button btnContinuar, btnCancelar;
    private View rootView;

    // TODO: Config
    Map<String, String> usuario;

    private OnFragmentInteractionListener mListener;

    public DatosCliente() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment DatosCliente.
     */
    // TODO: Rename and change types and number of parameters
    public static DatosCliente newInstance(String param1, String param2, Context context) {
        DatosCliente fragment = new DatosCliente();
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

        tvClienteNombre = (TextView) rootView.findViewById(R.id.afda_tv_nombre_cliente);
        tvClienteNumeroCuenta = (TextView) rootView.findViewById(R.id.afda_tv_numero_cuenta_cliente);
        tvClienteNSS = (TextView) rootView.findViewById(R.id.afda_tv_nss_cliente);
        tvClienteCURP = (TextView) rootView.findViewById(R.id.afda_tv_curp_cliente);
        tvClienteFecha = (TextView) rootView.findViewById(R.id.afda_tv_fecha_cliente);
        tvClienteSaldo = (TextView) rootView.findViewById(R.id.afda_tv_saldo_cliente);
        btnContinuar = (Button) rootView.findViewById(R.id.afda_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afda_btn_cancelar);

        // TODO: Config
        usuario = Config.usuario(getContext());

        final Fragment borrar = this;

        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1).trim();
            mParam2 = getArguments().getString(ARG_PARAM2).trim();
            Log.d("Datostos", "paramentro 1" + mParam1 + " parametro 2" + mParam2);
        }else{
            Log.d("NADA", "nada");
        }

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final Connected conected = new Connected();
                if(conected.estaConectado(v.getContext())) {
                    //
                }else{
                    Config.msj(v.getContext(),"Error en conexión", "Sin Conexion por el momento.Datos Cliente P-1.1.3.3");
                }
                Fragment fragmentoGenerico = new Encuesta1();
                FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).remove(borrar).commit();

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

        sendJson(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_datos_cliente, container, false);
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

    private void sendJson(final boolean primerPeticion) {

        JSONObject obj = new JSONObject();

        try {
            // TODO: Formacion del JSON request

            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 1133);
            rqt.put("numeroCuenta", "123123");
            rqt.put("usuario", usuario.get(SessionManager.ID));
            obj.put("rqt", rqt);
            Log.d(TAG, "Primera peticion-->" + obj);
        } catch (JSONException e) {
            Config.msj(getContext(),"Error json","Lo sentimos ocurrio un error al formar los datos.");
        }

        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CUNSULTAR_DATOS_CLIENTE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion)
                            primerPaso(response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            //loading.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }
                        Connected connected = new Connected();
                        if(connected.estaConectado(getContext())){
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error");
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
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error de conexión");
                            dlgAlert.setMessage("Se ha encontrado un problema, debes revisar tu conexión a internet");
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
        String status = "";
        String statusText = "";
        String nombre = "";
        String cuenta = "";
        String nss = "";
        String curp = "";
        String fechaConsulta = "";
        Double saldo;

        try{
            status = obj.getString("status");
            statusText = obj.getString("statusText");

            if(Integer.parseInt(status) == 200){
                JSONObject jsonCliente = obj.getJSONObject("cliente");
                Log.d(TAG, "--> JSON CLIENTE " + jsonCliente);
                nombre = jsonCliente.getString("nombre");
                cuenta = jsonCliente.getString("numeroCuenta");
                nss    = jsonCliente.getString("nss");
                curp   = jsonCliente.getString("curp");
                fechaConsulta = jsonCliente.getString("fecha_consulta");
                saldo = jsonCliente.getDouble("saldo");

                tvClienteNombre.setText("" + nombre);
                tvClienteNumeroCuenta.setText("" + cuenta);
                tvClienteNSS.setText("" + nss);
                tvClienteCURP.setText("" + curp);
                tvClienteFecha.setText("" + fechaConsulta);
                tvClienteSaldo.setText("" + saldo);
            }else{
                Config.msj(getContext(), "Error: " + status, statusText);
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
    }

}
