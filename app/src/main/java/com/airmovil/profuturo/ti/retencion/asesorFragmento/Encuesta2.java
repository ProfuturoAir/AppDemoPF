package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
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
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
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
    private SQLiteHandler db;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    int iParam1IdGerencia;
    int iParam2IdMotivos;
    int iParam3IdEstatus;
    int iParam4IdTitulo;
    int iParam5IdRegimentPensionario;
    int iParam6IdDocumentacion;
    String iParam7Telefono;
    String iParam8Email;

    String idTramite;
    String nombre;
    String numeroDeCuenta;
    String hora;

    // TODO: XML
    private ArrayAdapter arrayAdapterAfores, arrayAdapterMotivo, arrayAdapterEstatus, arrayAdapterInstituto, arrayAdapterRegimen, arrayAdapterDocumentos;
    private Spinner spinnerAfores, spinnerMotivos, spinnerEstatus, spinnerInstituto, spinnerRegimen, spinnerDocumentos;
    private Button btnContinuar, btnCancelar;
    private EditText etTelefono, etEmail;

    private Connected connected;

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
        db = new SQLiteHandler(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: CASTEO
        spinnerAfores = (Spinner) view.findViewById(R.id.afe2_spinner_afores);
        spinnerMotivos = (Spinner) view.findViewById(R.id.afe2_spinner_motivo);
        spinnerEstatus = (Spinner) view.findViewById(R.id.afe2_spinner_estatus);
        spinnerInstituto = (Spinner) view.findViewById(R.id.afe2_spinner_instituto);
        spinnerRegimen = (Spinner) view.findViewById(R.id.afe2_spinner_regimen);
        spinnerDocumentos = (Spinner) view.findViewById(R.id.afe2_spinner_documentos);
        btnContinuar = (Button) view.findViewById(R.id.afe2_btn_continuar);
        btnCancelar = (Button) view.findViewById(R.id.afe2_btn_cancelar);
        etTelefono = (EditText) view.findViewById(R.id.afe2_et_telefono);
        etEmail = (EditText) view.findViewById(R.id.afe2_et_email);

        connected = new Connected();
        idTramite = getArguments().getString("idTramite");
        nombre = getArguments().getString("nombre");
        numeroDeCuenta = getArguments().getString("numeroDeCuenta");
        hora = getArguments().getString("hora");

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

        final Fragment borrar = this;
        //<editor-fold desc="btn continuar">
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                iParam1IdGerencia = spinnerAfores.getSelectedItemPosition();
                iParam2IdMotivos = spinnerMotivos.getSelectedItemPosition();
                iParam3IdEstatus = spinnerEstatus.getSelectedItemPosition();
                iParam4IdTitulo = spinnerInstituto.getSelectedItemPosition();
                iParam5IdRegimentPensionario = spinnerRegimen.getSelectedItemPosition();
                iParam6IdDocumentacion = spinnerDocumentos.getSelectedItemPosition();
                iParam7Telefono = etTelefono.getText().toString();
                iParam8Email = etEmail.getText().toString();
                boolean val;

                if(verificarEmail(iParam8Email) == true){
                    val = true;
                }else{
                    val = false;
                }

                Log.d("Variable email ", "" + iParam8Email);
                Log.d(" Retorno de email: ", " " + val);

                if(iParam1IdGerencia == 0 || iParam2IdMotivos == 0 || iParam3IdEstatus == 0 || iParam4IdTitulo == 0 ||
                        iParam5IdRegimentPensionario == 0 || iParam6IdDocumentacion == 0 || iParam7Telefono.isEmpty() || iParam8Email.isEmpty() ){
                    Config.dialogoDatosVacios(getContext());
                }else {
                    if(val == true){
                        if(connected.estaConectado(getContext())){
                            final EnviaJSON enviaPrevio = new EnviaJSON();
                            Config.teclado(getContext(), etTelefono);
                            Config.teclado(getContext(), etEmail);
                            sendJson(true, iParam1IdGerencia, iParam2IdMotivos, iParam3IdEstatus, iParam4IdTitulo, iParam5IdRegimentPensionario, iParam6IdDocumentacion, iParam7Telefono, iParam8Email);
                            enviaPrevio.sendPrevios(idTramite,getContext());
                            Fragment fragmentoGenerico = new Firma();
                            Asesor asesor = (Asesor) getContext();
                            asesor.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
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

                                            db.addObservaciones(idTramite,iParam1IdGerencia,iParam2IdMotivos,iParam3IdEstatus,iParam4IdTitulo,iParam5IdRegimentPensionario,iParam6IdDocumentacion,iParam7Telefono,iParam8Email,123);
                                            db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                            Config.teclado(getContext(), etEmail);
                                            Config.teclado(getContext(), etTelefono);

                                            Fragment fragmentoGenerico = new Firma();
                                            Asesor asesor = (Asesor) getContext();
                                            asesor.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                                        }
                                    });
                            progressDialog.setButton(DialogInterface.BUTTON_NEGATIVE, getResources().getString(R.string.cancelar),
                                    new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {

                                        }
                                    });
                            progressDialog.show();

                            // *db.addObservaciones(idTramite,iParam1IdGerencia,iParam2IdMotivos,iParam3IdEstatus,iParam4IdTitulo,iParam5IdRegimentPensionario,iParam6IdDocumentacion,iParam7Telefono,iParam8Email,123);
                            // *db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);

                            //Config.msj(getContext(), getResources().getString(R.string.error_conexion), getResources().getString(R.string.msj_error_conexion));
                            // * Config.msj(getContext(), "Error", "Error en conexión a internet, se enviaran los datos cuando existan conexión");
                            //Fragment fragmentoGenerico = new Firma();
                            //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            //fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }
                        // * Fragment fragmentoGenerico = new Firma();
                        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        //fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).remove(borrar).commit();*/
                        // * Asesor asesor = (Asesor) getContext();
                        // * asesor.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                    }else{
                        Config.msj(getContext(), getResources().getString(R.string.error_email_incorrecto), getResources().getString(R.string.msj_error_email));
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
    private void sendJson(final boolean primerPeticion, int idGerencia, int idMotivo, int IdEstatus,
                          int idTitulo, int idRegimentPensionario, int idDocumentacion, String telefono, String email) {
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
            rqt.put("idAfore", idGerencia);
            rqt.put("idMotivo", idMotivo);
            rqt.put("idEstatus", IdEstatus);
            rqt.put("idInstituto", idTitulo);
            rqt.put("idRegimentPensionario", idRegimentPensionario);
            rqt.put("idDocumento", idDocumentacion);
            rqt.put("telefono", telefono);
            rqt.put("email", email);
            rqt.put("estatusTramite", 1131);
            rqt.put("idTramite", Integer.parseInt(idTramite));
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        //<editor-fold desc="Creating a json array request">
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
        //</editor-fold>
    }
}
