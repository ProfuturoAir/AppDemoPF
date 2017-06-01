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

import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Log;
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
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.SpinnerDatos;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class Encuesta2 extends Fragment {
    private static final String ARG_PARAM1 = "param1",  ARG_PARAM2 = "param2";
    private static final String TAG = Encuesta2.class.getSimpleName();
    private SQLiteHandler db;
    private int iParam1IdGerencia, iParam2IdMotivos, iParam3IdEstatus, iParam4IdTitulo, iParam5IdRegimentPensionario, iParam6IdDocumentacion;
    private String iParam7Telefono, iParam8Email, idTramite, nombre, numeroDeCuenta, hora;
    private View rootView;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private ArrayAdapter arrayAdapterAfores, arrayAdapterMotivo, arrayAdapterEstatus, arrayAdapterInstituto, arrayAdapterRegimen, arrayAdapterDocumentos;
    private Spinner spinnerAfores, spinnerMotivos, spinnerEstatus, spinnerInstituto, spinnerRegimen, spinnerDocumentos;
    private Button btnContinuar, btnCancelar;
    private EditText etTelefono, etEmail;
    private Connected connected;
    private Fragment borrar = this;

    private OnFragmentInteractionListener mListener;

    public Encuesta2() {/* el constructor vacio es requerido*/}

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

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Log.e(TAG, "***> " + getArguments().toString());
        }
        db = new SQLiteHandler(getContext());
    }

    /**
     * El sistema lo llama para iniciar los procesos que estaran dentro del flujo de la vista
     * @param view accede a la vista del xml
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Asignacion de variables
        variables();

        if(getArguments()!=null){
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = (Config.verificarEmail(etEmail.getText().toString())) ? true : false;
                iParam1IdGerencia = spinnerAfores.getSelectedItemPosition();
                iParam2IdMotivos = spinnerMotivos.getSelectedItemPosition();
                iParam3IdEstatus = spinnerEstatus.getSelectedItemPosition();
                iParam4IdTitulo = spinnerInstituto.getSelectedItemPosition();
                iParam5IdRegimentPensionario = spinnerRegimen.getSelectedItemPosition();
                iParam6IdDocumentacion = spinnerDocumentos.getSelectedItemPosition();
                iParam7Telefono = etTelefono.getText().toString();
                iParam8Email = etEmail.getText().toString();

                if(spinnerAfores.getSelectedItemPosition() == 0 || spinnerMotivos.getSelectedItemPosition() == 0 || spinnerEstatus.getSelectedItemPosition() == 0 ||  spinnerInstituto.getSelectedItemPosition() == 0 ||
                        spinnerRegimen.getSelectedItemPosition() == 0 || spinnerDocumentos.getSelectedItemPosition() == 0 || etTelefono.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty()  ){
                    Config.dialogoDatosVacios(getContext());
                }else {
                    if(val == true){
                        if(connected.estaConectado(getContext())){
                            Config.teclado(getContext(), etTelefono);
                            Config.teclado(getContext(), etEmail);
                            sendJson(true, iParam1IdGerencia, iParam2IdMotivos, iParam3IdEstatus, iParam4IdTitulo, iParam5IdRegimentPensionario, iParam6IdDocumentacion, iParam7Telefono, iParam8Email);
                            Fragment fragmentoGenerico = new Firma();
                            Gerente gerente = (Gerente) getContext();
                            gerente.parametrosDetalle(fragmentoGenerico, Integer.parseInt(Config.ID_TRAMITE), getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), getArguments().getString("nombreAsesor"), getArguments().getString("cuentaAsesor"), getArguments().getString("sucursalAsesor"), getArguments().getString("nombreCliente"), getArguments().getString("numCuentaCliente"), getArguments().getString("nssCliente"), getArguments().getString("curpCliente"), getArguments().getString("fechaCliente"),
                                    getArguments().getString("saldoCliente"), getArguments().getBoolean("pregunta1"), getArguments().getBoolean("pregunta2"), getArguments().getBoolean("pregunta3"), getArguments().getString("observaciones"), spinnerAfores.getSelectedItem().toString(), spinnerMotivos.getSelectedItem().toString(), spinnerEstatus.getSelectedItem().toString(), spinnerInstituto.getSelectedItem().toString(), spinnerRegimen.getSelectedItem().toString(), spinnerDocumentos.getSelectedItem().toString(), etTelefono.getText().toString().trim(), etEmail.getText().toString().trim());

                        }else{
                            AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                            dialogo.setTitle(getResources().getString(R.string.error_conexion));
                            dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                            dialogo.setCancelable(false);
                            dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Fragment fragmentoGenerico = new Firma();
                                    Gerente gerente = (Gerente) getContext();
                                    db.addObservaciones(Config.ID_TRAMITE,iParam1IdGerencia,iParam2IdMotivos,iParam3IdEstatus,iParam4IdTitulo,iParam5IdRegimentPensionario,iParam6IdDocumentacion,iParam7Telefono,iParam8Email,1135);
                                    db.addIDTramite(Config.ID_TRAMITE,getArguments().getString("nombre"), getArguments().getString("cuentaAsesor"), getArguments().getString("hora"));
                                    Config.teclado(getContext(), etEmail);
                                    Config.teclado(getContext(), etTelefono);
                                    gerente.parametrosDetalle(fragmentoGenerico, Integer.parseInt(Config.ID_TRAMITE), getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), getArguments().getString("nombreAsesor"), getArguments().getString("cuentaAsesor"), getArguments().getString("sucursalAsesor"), getArguments().getString("nombreCliente"), getArguments().getString("numCuentaCliente"), getArguments().getString("nssCliente"), getArguments().getString("curpCliente"), getArguments().getString("fechaCliente"),
                                            getArguments().getString("saldoCliente"), getArguments().getBoolean("pregunta1"), getArguments().getBoolean("pregunta2"), getArguments().getBoolean("pregunta3"), getArguments().getString("observaciones"), spinnerAfores.getSelectedItem().toString(), spinnerMotivos.getSelectedItem().toString(), spinnerEstatus.getSelectedItem().toString(), spinnerInstituto.getSelectedItem().toString(), spinnerRegimen.getSelectedItem().toString(), spinnerDocumentos.getSelectedItem().toString(), etTelefono.getText().toString().trim(), etEmail.getText().toString().trim());

                                }
                            });
                            dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {}
                            });
                            dialogo.show();
                        }
                    }else{
                        Config.msj(getContext(), getResources().getString(R.string.error_email_incorrecto), getResources().getString(R.string.msj_error_email));
                    }
                }
            }
        });
        // TODO Cancelar el proceso
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1135), 2);
    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.gerente_fragmento_encuesta2, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context estado actual de la aplicacion
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     *Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new ConCita();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 2, fragment);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esa actividad.
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
                primerPaso(response);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                loading.dismiss();
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                    Log.e("conexion", "dialogoErrorServicio");
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                    Log.e("conexion", "dialogoErrorConexion");
                }
            }
        };
    }

    /**
     * Casteo de variables, nueva instancia para la conexion a internet Connected
     */
    private void variables(){
        spinnerAfores = (Spinner) rootView.findViewById(R.id.gfe2_spinner_afores);
        spinnerMotivos = (Spinner) rootView.findViewById(R.id.gfe2_spinner_motivo);
        spinnerEstatus = (Spinner) rootView.findViewById(R.id.gfe2_spinner_estatus);
        spinnerInstituto = (Spinner) rootView.findViewById(R.id.gfe2_spinner_instituto);
        spinnerRegimen = (Spinner) rootView.findViewById(R.id.gfe2_spinner_regimen);
        spinnerDocumentos = (Spinner) rootView.findViewById(R.id.gfe2_spinner_documentos);
        btnContinuar = (Button) rootView.findViewById(R.id.gfe2_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.gfe2_btn_cancelar);
        etTelefono = (EditText) rootView.findViewById(R.id.gfe2_et_telefono);
        etEmail = (EditText) rootView.findViewById(R.id.gfe2_et_email);
        connected = new Connected();

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
    }

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, int idGerencia, int idMotivo, int IdEstatus, int idTitulo, int idRegimentPensionario, int idDocumentacion, String telefono, String email) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

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
            rqt.put("idTramite", Config.ID_TRAMITE);
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_ENCUESTA_2, obj);
    }

    /**
     * Método donde se obtiene el response
     * @param obj
     */
    private void primerPaso(JSONObject obj){
        Log.d("jma", "response--> "  + obj );
    }
}
