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
import android.widget.Spinner;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.SpinnerDatos;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class Encuesta2 extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    /* inicializacion de los parametros del fragmento*/
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = Encuesta2.class.getSimpleName();
    private SQLiteHandler db;
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
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    // TODO: XML
    private ArrayAdapter arrayAdapterAfores, arrayAdapterMotivo, arrayAdapterEstatus, arrayAdapterInstituto, arrayAdapterRegimen, arrayAdapterDocumentos;
    private Spinner spinnerAfores, spinnerMotivos, spinnerEstatus, spinnerInstituto, spinnerRegimen, spinnerDocumentos;
    private Button btnContinuar, btnCancelar;
    private EditText etTelefono, etEmail;
    private Connected connected;

    private OnFragmentInteractionListener mListener;

    public Encuesta2() {
        /* el constructor vacio es requerido*/
    }

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
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
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        spinnerAfores = (Spinner) rootView.findViewById(R.id.afe2_spinner_afores);
        spinnerMotivos = (Spinner) rootView.findViewById(R.id.afe2_spinner_motivo);
        spinnerEstatus = (Spinner) rootView.findViewById(R.id.afe2_spinner_estatus);
        spinnerInstituto = (Spinner) rootView.findViewById(R.id.afe2_spinner_instituto);
        spinnerRegimen = (Spinner) rootView.findViewById(R.id.afe2_spinner_regimen);
        spinnerDocumentos = (Spinner) rootView.findViewById(R.id.afe2_spinner_documentos);
        btnContinuar = (Button) rootView.findViewById(R.id.afe2_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afe2_btn_cancelar);
        etTelefono = (EditText) rootView.findViewById(R.id.afe2_et_telefono);
        etEmail = (EditText) rootView.findViewById(R.id.afe2_et_email);

        connected = new Connected();

        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

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

        /* Se asignan los eventos para que el primer Item del Spinner sea de color Gris */
        SpinnerDatos.spinnerEncuesta2(getContext(),spinnerAfores);
        SpinnerDatos.spinnerEncuesta2(getContext(),spinnerMotivos);
        SpinnerDatos.spinnerEncuesta2(getContext(),spinnerEstatus);
        SpinnerDatos.spinnerEncuesta2(getContext(),spinnerInstituto);
        SpinnerDatos.spinnerEncuesta2(getContext(),spinnerRegimen);
        SpinnerDatos.spinnerEncuesta2(getContext(),spinnerDocumentos);

        final Fragment borrar = this;
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
                            Fragment fragmentoGenerico = new Firma();
                            Asesor asesor = (Asesor) getContext();
                            asesor.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                        }else{
                            AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                            dialogo.setTitle(getResources().getString(R.string.error_conexion));
                            dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                            dialogo.setCancelable(false);
                            dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.addObservaciones(idTramite,iParam1IdGerencia,iParam2IdMotivos,iParam3IdEstatus,iParam4IdTitulo,iParam5IdRegimentPensionario,iParam6IdDocumentacion,iParam7Telefono,iParam8Email,1135);
                                    db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                    Config.teclado(getContext(), etEmail);
                                    Config.teclado(getContext(), etTelefono);

                                    Fragment fragmentoGenerico = new Firma();
                                    Asesor asesor = (Asesor) getContext();
                                    asesor.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                                }
                            });
                            dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {


                                }
                            });
                            dialogo.show();
                        }
                    }else{
                        Config.msj(getContext(), getResources().getString(R.string.error_email_incorrecto), getResources().getString(R.string.msj_error_email));
                    }
                }
            }
        });
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
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_encuesta2, container, false);
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
     *  metodo para callback de volley
     */
    void initVolleyCallback() {

        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                loading.dismiss();
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        };
    }

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, int idGerencia, int idMotivo, int IdEstatus, int idTitulo, int idRegimentPensionario, int idDocumentacion, String telefono, String email) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        idTramite = getArguments().getString("idTramite");
        JSONObject obj = new JSONObject();
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("idAfore", idGerencia);
            rqt.put("idMotivo", idMotivo);
            rqt.put("idEstatus", IdEstatus);
            rqt.put("idInstituto", idTitulo);
            rqt.put("idRegimentPensionario", idRegimentPensionario);
            rqt.put("idDocumentacion", idDocumentacion);
            rqt.put("telefono", telefono);
            rqt.put("email", email);
            rqt.put("estatusTramite", 1135);
            rqt.put("idTramite", Integer.parseInt(idTramite));
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_ENCUESTA_2, obj);
    }
}
