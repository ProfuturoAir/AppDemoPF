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
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.weiwangcn.betterspinner.library.material.MaterialBetterSpinner;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Encuesta2.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Encuesta2#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Encuesta2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = Encuesta2.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    // TODO: XML
    private ArrayAdapter arrayAdapterAfores, arrayAdapterMotivo, arrayAdapterEstatus, arrayAdapterInstituto, arrayAdapterRegimen, arrayAdapterDocumentos;
    private MaterialBetterSpinner spinnerAfores, spinnerMotivos, spinnerEstatus, spinnerInstituto, spinnerRegimen, spinnerDocumentos;
    private Button btnContinuar, btnCancelar;
    private EditText etTelefono, etEmail;

    private OnFragmentInteractionListener mListener;

    public Encuesta2() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Encuesta2.
     */
    // TODO: Rename and change types and number of parameters
    public static Encuesta2 newInstance(String param1, String param2) {
        Encuesta2 fragment = new Encuesta2();
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
        // TODO: CASTEO
        spinnerAfores = (MaterialBetterSpinner) view.findViewById(R.id.afe2_spinner_afores);
        spinnerMotivos = (MaterialBetterSpinner) view.findViewById(R.id.afe2_spinner_motivo);
        spinnerEstatus = (MaterialBetterSpinner) view.findViewById(R.id.afe2_spinner_estatus);
        spinnerInstituto = (MaterialBetterSpinner) view.findViewById(R.id.afe2_spinner_instituto);
        spinnerRegimen = (MaterialBetterSpinner) view.findViewById(R.id.afe2_spinner_regimen);
        spinnerDocumentos = (MaterialBetterSpinner) view.findViewById(R.id.afe2_spinner_documentos);
        btnContinuar = (Button) view.findViewById(R.id.afe2_btn_continuar);
        btnCancelar = (Button) view.findViewById(R.id.afe2_btn_cancelar);
        etTelefono = (EditText) view.findViewById(R.id.afe2_et_telefono);
        etEmail = (EditText) view.findViewById(R.id.afe2_et_email);

        arrayAdapterAfores = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Config.AFORES);
        arrayAdapterMotivo = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Config.MOTIVOS);
        arrayAdapterEstatus = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Config.ESTATUS);
        arrayAdapterInstituto = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Config.INSTITUCIONES);
        arrayAdapterRegimen = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Config.REGIMEN);
        arrayAdapterDocumentos = new ArrayAdapter<String>(getContext(), android.R.layout.simple_dropdown_item_1line, Config.DOCUMENTOS);

        spinnerAfores.setAdapter(arrayAdapterAfores);
        spinnerMotivos.setAdapter(arrayAdapterMotivo);
        spinnerEstatus.setAdapter(arrayAdapterEstatus);
        spinnerInstituto.setAdapter(arrayAdapterInstituto);
        spinnerRegimen.setAdapter(arrayAdapterRegimen);
        spinnerDocumentos.setAdapter(arrayAdapterDocumentos);

        //<editor-fold desc="btn continuar">
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String telefono = etEmail.getText().toString().trim();
                String email    = etEmail.getText().toString().trim();
                boolean validandoEmail;

                if( verificarEmail(email))
                    validandoEmail = true;
                else
                    validandoEmail = false;


                if(spinnerAfores.getText().toString().trim().equals("") || spinnerMotivos.getText().toString().trim().equals("") ||
                        spinnerEstatus.getText().toString().trim().equals("") || spinnerInstituto.getText().toString().trim().equals("") ||
                        spinnerRegimen.getText().toString().trim().equals("") || spinnerDocumentos.getText().toString().trim().equals("") ){
                    Config.msj(getContext(),"Error", "Faltan Respuestas Favor de Checar");
                }else{
                    final Connected conected = new Connected();
                    if(conected.estaConectado(getContext())){
                        if(validandoEmail == false){
                            Config.msj(getContext(),"Error", "El correo electrónico es invalido");
                        }else{
                            sendJson(true);
                            Fragment fragmentoGenerico = new Firma();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }
                    }else{
                        Config.msj(getContext(), "Error", "Error en conexión a internet, se enviaran los datos cuando existan conexión");
                        if(validandoEmail == false){
                            Config.msj(getContext(),"Error", "El correo electrónico es invalido");
                        }else{
                            Fragment fragmentoGenerico = new Firma();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }
                    }
                }
            }
        });
        //</editor-fold>

        //<editor-fold desc="Boton cancelar">
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.5?");
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
            }
        });
        //</editor-fold>

    }

    public static boolean verificarEmail(String email){
        Log.d("DATOS: ", "-->" +email);
        boolean valor;
        String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
        if ( (email.matches(emailPattern) && email.length() > 0) ) {
            valor = true;
            return valor;
        } else {
            valor = false;
            return valor;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_encuesta2, container, false);
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
                    dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.5?");
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
            rqt.put("idAfore", spinnerAfores.getListSelection());
            rqt.put("idMotivo", spinnerMotivos.getItemClickListener().toString());
            rqt.put("idStatus", spinnerEstatus.getOnItemSelectedListener());
            rqt.put("regimen", spinnerRegimen.getId());
            rqt.put("documentos", spinnerDocumentos.getId());
            rqt.put("telefono", etTelefono.getText().toString());
            rqt.put("email", etEmail.getText().toString());
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_ENCUESTA_2, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            //primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<String, String>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }
}
