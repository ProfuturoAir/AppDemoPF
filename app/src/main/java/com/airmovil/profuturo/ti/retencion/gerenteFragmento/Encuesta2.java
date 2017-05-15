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
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = Encuesta2.class.getSimpleName();
    private SQLiteHandler db;
    private String mParam1, mParam2;
    private int iParam1IdGerencia, iParam2IdMotivos, iParam3IdEstatus, iParam4IdTitulo, iParam5IdRegimentPensionario, iParam6IdDocumentacion;
    private String iParam7Telefono, iParam8Email, idTramite, nombre, numeroDeCuenta, hora;
    private ArrayAdapter arrayAdapterAfores, arrayAdapterMotivo, arrayAdapterEstatus, arrayAdapterInstituto, arrayAdapterRegimen, arrayAdapterDocumentos;
    private Spinner spinnerAfores, spinnerMotivos, spinnerEstatus, spinnerInstituto, spinnerRegimen, spinnerDocumentos;
    private Button btnContinuar, btnCancelar;
    private EditText etTelefono, etEmail;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    public Encuesta2() { /* Se requiere un constructor vacio */}

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
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
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        };
    }

    /**
     * El sistema lo llama cuando el fragmento debe diseñar su interfaz de usuario por primera vez
     * @param view accede a la vista del XML
     * @param savedInstanceState fuarda el estado de la instancia
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: CASTEO
        variables();
        // TODO: argumentos verifica si hay datos enviados desde otro fragmento
        argumentos();
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                boolean val = (Config.verificarEmail(etEmail.getText().toString())) ? true : false;
                if(spinnerAfores.getSelectedItemPosition() == 0 || spinnerMotivos.getSelectedItemPosition() == 0 || spinnerEstatus.getSelectedItemPosition() == 0 ||  spinnerInstituto.getSelectedItemPosition() == 0 ||
                        spinnerRegimen.getSelectedItemPosition() == 0 || spinnerDocumentos.getSelectedItemPosition() == 0 || etTelefono.getText().toString().isEmpty() || etEmail.getText().toString().isEmpty() ){
                    Dialogos.dialogoDatosVacios(getContext());
                }else {
                    if(val == true){
                        if(connected.estaConectado(getContext())){
                            Config.teclado(getContext(), etTelefono);
                            Config.teclado(getContext(), etEmail);
                            sendJson(true, spinnerAfores.getSelectedItemPosition(), spinnerMotivos.getSelectedItemPosition(), spinnerEstatus.getSelectedItemPosition(),  spinnerInstituto.getSelectedItemPosition(), spinnerRegimen.getSelectedItemPosition(), spinnerDocumentos.getSelectedItemPosition(), etTelefono.getText().toString(), etEmail.getText().toString());
                            Fragment fragmentoGenerico = new Firma();
                            Gerente gerente = (Gerente) getContext();
                            gerente.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                        }else{
                            AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                            dialogo.setTitle(getResources().getString(R.string.error_conexion));
                            dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                            dialogo.setCancelable(false);
                            dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    db.addObservaciones(idTramite,spinnerAfores.getSelectedItemPosition(),spinnerMotivos.getSelectedItemPosition(),spinnerEstatus.getSelectedItemPosition(), spinnerInstituto.getSelectedItemPosition(),spinnerRegimen.getSelectedItemPosition(),spinnerDocumentos.getSelectedItemPosition(),etTelefono.getText().toString(),etEmail.getText().toString(),1135);
                                    db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                    Config.teclado(getContext(), etEmail);
                                    Config.teclado(getContext(), etTelefono);
                                    Fragment fragmentoGenerico = new Firma();
                                    Gerente gerente = (Gerente) getContext();
                                    gerente.switchFirma(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
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

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater inflacion del xml
     * @param container contenedor del ml
     * @param savedInstanceState datos guardados
     * @return el fragmento declarado DIRECTOR INICIO
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
     * @param context establece el estado actual de la apliacion para hacer uso con esta clase
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Se utiliza este metodo para el control de la tecla de retroceso
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
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle("Confirmar");
                    dialogo.setMessage("¿Estás seguro que deseas salir del proceso de implicaciones?");
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
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
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esta actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Setear las variables de xml
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
     * Se utiliza para cololar datos recibidos entre una busqueda(por ejemplo: fechas)
     */
    private void argumentos(){
        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }
    }

    /**
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion, int idGerencia, int idMotivo, int IdEstatus, int idTitulo, int idRegimentPensionario, int idDocumentacion, String telefono, String email) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
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
            rqt.put("idRegimenPensionario", idRegimentPensionario);
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

    /**
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "RESPONSE: ->" + obj);
    }

}
