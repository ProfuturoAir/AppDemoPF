package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

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
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
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
 * {@link Encuesta1.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Encuesta1#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Encuesta1 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    public static final String TAG = Encuesta1.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SQLiteHandler db;
    private String idTramite;
    String nombre;
    String numeroDeCuenta;
    String hora;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    // TODO: XML
    private View rootView;
    private CheckBox cb1si, cb1no, cb2si, cb2no, cb3si, cb3no;
    private EditText etObservaciones;
    private Button btnContinuar, btnCancelar;
    private boolean respuesta1, respuesta2, respuesta3;
    private Boolean r1, r2, r3;
    private String observaciones;
    private int estatusTramite = 1134;

    public Encuesta1() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Encuesta1.
     */
    // TODO: Rename and change types and number of parameters
    public static Encuesta1 newInstance(String param1, String param2) {
        Encuesta1 fragment = new Encuesta1();
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
        rootView = view;

        variables();

        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

        cb1si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb1no.setChecked(false);
                if(b == true) r1 = true;
                else if(b == false) r1 = null;
                Log.d("CheckBox 1 si", "" + r1);
            }
        });

        cb1no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb1si.setChecked(false);
                if(b == true) r1 = false;
                else if(b == false) r1 = null;
                Log.d("CheckBox 1 no", "" + r1);
            }
        });

        cb2si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb2no.setChecked(false);
                if(b == true) r2 = true;
                else if(b == false) r2 = null;
                Log.d("CheckBox 2 si", "" + r2);
            }
        });

        cb2no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb2si.setChecked(false);
                if(b == true) r2 = false;
                else if(b == false) r2 = null;
                Log.d("CheckBox 2 no", "" + r2);
            }
        });

        cb3si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb3no.setChecked(false);
                if(b == true) r3 = true;
                else if(b == false) r3 = null;
                Log.d("CheckBox 3 si", "" + r3);
            }
        });

        cb3no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb3si.setChecked(false);
                if(b == true) r3 = false;
                else if(b == false) r3 = null;
                Log.d("CheckBox 3 no", "" + r3);
            }
        });

        final Fragment borrar = this;

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1 == null || r2 == null || r3 == null || etObservaciones.getText().toString().trim().isEmpty()) {
                    Config.dialogoDatosVacios(getContext());
                }else {
                    final Connected conectado = new Connected();
                    if(conectado.estaConectado(getContext())){
                        String o = etObservaciones.getText().toString();

                        sendJson(true, r1, r2, r3, o);
                        Config.teclado(getContext(), etObservaciones);
                        Fragment fragmentoGenerico = new Encuesta2();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        Gerente gerente = (Gerente) getContext();
                        gerente.switchEncuesta2(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                    }else{
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                        dialogo.setTitle(getResources().getString(R.string.error_conexion));
                        dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                        dialogo.setCancelable(false);
                        dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Config.teclado(getContext(), etObservaciones);
                                Log.d("Respuesta sin conexion:" , "Respuesta 1" + r1 + "Respuesta 2" + r2 + "Respuesta 3" + r3 );
                                db.addEncuesta(idTramite,estatusTramite,r1,r2,r3,etObservaciones.getText().toString().trim());
                                db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                Fragment fragmentoGenerico = new Encuesta2();
                                Gerente gerente = (Gerente) getContext();
                                gerente.switchEncuesta2(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                            }
                        });
                        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        dialogo.show();


                        //Config.msj(getContext(), "Error", "Error en conexión a internet, se enviaran los datos cuando existan conexión");
                        //Fragment fragmentoGenerico = new Encuesta2();
                        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        //fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                    }
                    //Fragment fragmentoGenerico = new Encuesta2();
                    //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    //fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).remove(borrar).commit();*/
                    //Gerente gerente = (Gerente) getContext();
                    //gerente.switchEncuesta2(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);

                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.4?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new SinCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
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
        return inflater.inflate(R.layout.gerente_fragmento_encuesta1, container, false);
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
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Confirmar");
                    dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.4?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
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

    private void variables(){
        btnContinuar = (Button) rootView.findViewById(R.id.gfe1_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.gfe1_btn_cancelar);
        cb1si = (CheckBox) rootView.findViewById(R.id.gfe1_cb_pregunta1_si);
        cb1no = (CheckBox) rootView.findViewById(R.id.gfe1_cb_pregunta1_no);
        cb2si = (CheckBox) rootView.findViewById(R.id.gfe1_cb_pregunta2_si);
        cb2no = (CheckBox) rootView.findViewById(R.id.gfe1_cb_pregunta2_no);
        cb3si = (CheckBox) rootView.findViewById(R.id.gfe1_cb_pregunta3_si);
        cb3no = (CheckBox) rootView.findViewById(R.id.gfe1_cb_pregunta3_no);
        etObservaciones = (EditText) rootView.findViewById(R.id.gfe1_et_observaciones);
    }

    // TODO: REST
    private void sendJson(final boolean primerPeticion, boolean opc1, boolean opc2, boolean opc3, String observaciones) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);
        else
            loading = null;
        idTramite = getArguments().getString("idTramite");
        JSONObject obj = new JSONObject();
        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            JSONObject encuesta = new JSONObject();
            encuesta.put("pregunta1", opc1);
            encuesta.put("pregunta2", opc2);
            encuesta.put("pregunta3", opc3);
            rqt.put("encuesta", encuesta);
            rqt.put("observaciones", observaciones);
            rqt.put("estatusTramite", 1134);
            rqt.put("idTramite", Integer.parseInt(idTramite));
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
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
                        //Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un right_in, puedes intentar revisando tu conexión.");
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
        Log.d(TAG, "RESPONSE: ->" + obj);
    }
}
