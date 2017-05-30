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
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class Encuesta1 extends Fragment {
    public static final String TAG = Encuesta1.class.getSimpleName();
    private SQLiteHandler db;
    private String nombre, numeroDeCuenta, hora;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private CheckBox cb1si, cb1no, cb2si, cb2no, cb3si, cb3no;
    private EditText etObservaciones;
    private Button btnContinuar, btnCancelar;
    private Boolean r1, r2, r3;
    private int estatusTramite = 1134, idTramite;
    private Fragment borrar = this;

    public Encuesta1() {/* contructor vacio es requerido */}

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Log.e("Encuesta1", "\n" + getArguments().toString());

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
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        variables();
        // TODO: Argumentos
        argumentos();

        cb1si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb1no.setChecked(false);
                r1 = (b) ? true : null;
            }
        });

        cb1no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb1si.setChecked(false);
                r1 = (b) ? false : null;
            }
        });

        cb2si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb2no.setChecked(false);
                r2 = (b) ? true : null;
            }
        });

        cb2no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb2si.setChecked(false);
                r2 = (b) ? false : null;
            }
        });

        cb3si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb3no.setChecked(false);
                r3 = (b) ? true : null;
            }
        });

        cb3no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb3si.setChecked(false);
                r3 = (b) ? false : null;
            }
        });

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1 == null || r2 == null || r3 == null || etObservaciones.getText().toString().trim().isEmpty()) {
                    Dialogos.dialogoDatosVacios(getContext());
                }else {
                    final Connected conectado = new Connected();
                    if(conectado.estaConectado(getContext())){
                        sendJson(true, r1, r2, r3, etObservaciones.getText().toString());
                        Config.teclado(getContext(), etObservaciones);
                        Fragment fragmentoGenerico = new Encuesta2();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        Asesor asesor = (Asesor) getContext();

                        asesor.parametrosDetalle(fragmentoGenerico, Integer.parseInt(Config.ID_TRAMITE), getArguments().getString("nombre"),
                                getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), getArguments().getString("nombreAsesor"), getArguments().getString("cuentaAsesor"), getArguments().getString("sucursalAsesor"),
                                getArguments().getString("nombreCliente"), getArguments().getString("numCuentaCliente"), getArguments().getString("nssCliente"), getArguments().getString("curpCliente"), getArguments().getString("fechaCliente"),
                                getArguments().getString("saldoCliente"), r1, r2, r3, etObservaciones.getText().toString(), "", "", "", "", "", "", "", "");
                    }else{
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                        dialogo.setTitle(getResources().getString(R.string.error_conexion));
                        dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                        dialogo.setCancelable(false);
                        dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Config.teclado(getContext(), etObservaciones);
                                db.addEncuesta(Config.ID_TRAMITE,estatusTramite,r1,r2,r3,etObservaciones.getText().toString().trim());
                                db.addIDTramite(Config.ID_TRAMITE,nombre,getArguments().getString("cuentaAsesor"),hora);
                                Fragment fragmentoGenerico = new Encuesta2();
                                Asesor asesor = (Asesor) getContext();
                                asesor.parametrosDetalle(fragmentoGenerico, Integer.parseInt(Config.ID_TRAMITE), getArguments().getString("nombre"),
                                        getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), getArguments().getString("nombreAsesor"), getArguments().getString("cuentaAsesor"), getArguments().getString("sucursalAsesor"),
                                        getArguments().getString("nombreCliente"), getArguments().getString("numCuentaCliente"), getArguments().getString("nssCliente"), getArguments().getString("curpCliente"), getArguments().getString("fechaCliente"),
                                        getArguments().getString("saldoCliente"), r1, r2, r3, etObservaciones.getText().toString(), "", "", "", "", "", "", "", "");
                            }
                        });
                        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                        dialogo.show();
                    }

                }
            }
        });

        // TODO Cancelar el proceso
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1134), 1);
    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.asesor_fragmento_encuesta1, container, false);
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
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 1, fragment);
                    return true;
                }
                return false;
            }
        });
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Casteo de variables, nueva instancia para la conexion a internet Connected
     */
    private void variables(){
        btnContinuar = (Button) rootView.findViewById(R.id.afe1_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afe1_btn_cancelar);
        cb1si = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta1_si);
        cb1no = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta1_no);
        cb2si = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta2_si);
        cb2no = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta2_no);
        cb3si = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta3_si);
        cb3no = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta3_no);
        etObservaciones = (EditText) rootView.findViewById(R.id.afe1_et_observaciones);
        connected = new Connected();
    }

    /**
     *  Espera el regreso de fechas incial (hoy y el dia siguiente)
     *  y cuando se realiza una nueva busqueda, retorna las fechas seleccionadas
     */
    private void argumentos(){
        if(getArguments()!= null){
            idTramite = getArguments().getInt("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
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
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, boolean opc1, boolean opc2, boolean opc3, String observaciones) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;
        idTramite = getArguments().getInt("idTramite");
        JSONObject obj = new JSONObject();
        try{
            JSONObject rqt = new JSONObject();
            JSONObject encuesta = new JSONObject();
            encuesta.put("pregunta1", opc1);
            encuesta.put("pregunta2", opc2);
            encuesta.put("pregunta3", opc3);
            rqt.put("encuesta", encuesta);
            rqt.put("observaciones", observaciones);
            rqt.put("estatusTramite", 1134);
            rqt.put("idTramite", Config.ID_TRAMITE);
            obj.put("rqt", rqt);
            Log.d(TAG, "<- RQT ->" + obj);
        } catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_ENCUESTA, obj);
    }

    /**
     * Obtiene el objeto json(Response), se obtiene cada elemento a parsear
     * @param obj json objeto
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "<- RESPONSE ->" + obj);
    }
}
