package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.GPSRastreador;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.Signature;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;
import com.kyanogen.signatureview.SignatureView;

import org.json.JSONException;
import org.json.JSONObject;


public class Firma extends Fragment{
    public static final String ARG_PARAM1 = "img_firma";
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private SQLiteHandler db;
    private Fragment borrar = this;
    private GPSRastreador gps;
    private Button btnGuardar, btnCancelar;
    private String nombre, numeroDeCuenta, hora;

    // TODO: NUEVOS ELEMENTOS
    private Button btnFirmar;
    private static Dialog dialog;
    private ImageView imgFirma;
    // XML
    private TextView nombreAsesor, numEmpleadoAsesor, sucursalAsesor;
    private TextView nombreCliente, numCuentaCliente, nssCliente, curpCliente, fechaCliente, saldoCliente, telefonoCliente, emailCliente;
    private TextView pregunta1, pregunta2, pregunta3, observaciones;
    private TextView afore, motivo, estatus, instituto, regimen, documentacion;
    private View rootView;
    private TextView nombreClienteFirma;
    private SignatureView signatureView;
    private boolean isSignatured = false;

    public Firma() {/* constructor vacio es requerido*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if(getArguments()!=null){
            Log.e("<->BUNDLE", "**" + getArguments().toString());
        }
        db = new SQLiteHandler(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, view.getContext());
        btnGuardar = (Button) view.findViewById(R.id.gff_btn_guardar);
        btnCancelar= (Button) view.findViewById(R.id.gff_btn_cancelar);
        // TODO: NUEVOS ELEMENTOS
        btnFirmar = (Button) view.findViewById(R.id.gbtn_firma);
        imgFirma = (ImageView) view.findViewById(R.id.gimagenFirma);

        variables();
        argumentos();

        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(getContext().INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);


        btnFirmar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signature.procesandoFirma(getContext(), imgFirma, (getArguments()!=null) ? getArguments().getString("nombreCliente") : "");
            }
        });

        imgFirma.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Signature.procesandoFirma(getContext(), imgFirma, (getArguments()!=null) ? getArguments().getString("nombreCliente") : "");
            }
        });

        if(getArguments()!=null){
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }


        // TODO: Clase para obtener las coordenadas
        gps = new GPSRastreador(getContext());

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imgFirma.buildDrawingCache();
                if(imgFirma.getDrawable() == null){
                    Config.dialogoNoExisteUnDocumento(getContext());
                }else{
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Mensaje de confirmación");
                    dialogo1.setMessage("¿Estas de acuerdo con los datos?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            final String base64 = Config.encodeTobase64(getContext(), imgFirma.getDrawingCache());
                            if(Config.conexion(getContext())) {
                                sendJson(true, base64);
                                loading.dismiss();
                                Fragment fragmentoGenerico = new Escaner();
                                Gerente gerente = (Gerente) getContext();
                                gerente.parametrosDetalle(fragmentoGenerico,0,getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), "", "", "", "", "", "", "", "", "", false, false, false, "", "", "", "", "", "", "", "", "");
                            }else{
                                AlertDialog.Builder dialogoCancelar = new AlertDialog.Builder(getContext());
                                dialogoCancelar.setTitle(getResources().getString(R.string.error_conexion));
                                dialogoCancelar.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                                dialogoCancelar.setCancelable(false);
                                dialogoCancelar.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        db.addFirma(Config.ID_TRAMITE,1137,base64,gps.getLatitude(),gps.getLongitude());
                                        db.addIDTramite(Config.ID_TRAMITE,getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"));
                                        Fragment fragmentoGenerico = new Escaner();
                                        Gerente gerente = (Gerente) getContext();
                                        gerente.parametrosDetalle(fragmentoGenerico,0,getArguments().getString("nombre"), getArguments().getString("numeroDeCuenta"), getArguments().getString("hora"), "", "", "", "", "", "", "", "", "", false, false, false, "", "", "", "", "", "", "", "", "");
                                    }
                                });
                                dialogoCancelar.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {}
                                });
                                dialogoCancelar.show();
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
            }
        });
        // TODO Cancelar el proceso
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1137), 2);
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
        return inflater.inflate(R.layout.gerente_fragmento_firma, container, false);
    }

    /**
     * El fragment se ha adjuntado al Activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {super.onAttach(context);}

    /**
     * El Fragment ha sido quitado de su Activity y ya no está disponible
     */
    @Override
    public void onDetach() {super.onDetach();}

    /**
     * Destruccion de la vista
     */
    @Override
    public void onDestroyView() {super.onDestroyView();}

    /**
     *  Indica que la actividad está a punto de ser lanzada a segundo plano
     */
    @Override
    public void onPause() {super.onPause();}

    /**
     * Nos indica que la actividad está a punto de ser mostrada al usuario.
     */
    @Override
    public void onStart() {
        super.onStart();
    }

    /**
     *  La actividad ya no va a ser visible para el usuario
     */
    @Override
    public void onStop() {super.onStop();}

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
                if(Config.conexion(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                    Log.e("conexion", "dialogoErrorServicio");
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                    Log.e("conexion", "dialogoErrorConexion");
                }
                NetworkResponse networkResponse = error.networkResponse;

                /*
                Log.e("red", "*->" + networkResponse);
                if(networkResponse == null){
                    loading.dismiss();
                }else{
                    Log.e("ok", "++ ");
                }*/
            }
        };
    }

    private void variables(){
        nombreAsesor = (TextView) rootView.findViewById(R.id.gtv_nombre_asesor);
        numEmpleadoAsesor = (TextView) rootView.findViewById(R.id.gtv_num_empleado_asesor);
        sucursalAsesor = (TextView) rootView.findViewById(R.id.gtv_sucursal);
        nombreCliente = (TextView) rootView.findViewById(R.id.gtv_nombre_cliente);
        numCuentaCliente = (TextView) rootView.findViewById(R.id.gtv_num_cuenta_cliente);
        nssCliente = (TextView) rootView.findViewById(R.id.gtv_nss_asesor);
        curpCliente = (TextView) rootView.findViewById(R.id.gtv_curp_asesor);
        fechaCliente = (TextView) rootView.findViewById(R.id.gtv_fecha_cliente);
        saldoCliente = (TextView) rootView.findViewById(R.id.gtv_saldo_cliente);
        telefonoCliente = (TextView) rootView.findViewById(R.id.gtv_telefono_cliente);
        emailCliente = (TextView) rootView.findViewById(R.id.gtv_email_cliente);
        pregunta1 = (TextView) rootView.findViewById(R.id.gtv_pregunta1_cliente);
        pregunta2 = (TextView) rootView.findViewById(R.id.gtv_pregunta2_cliente);
        pregunta3 = (TextView) rootView.findViewById(R.id.gtv_pregunta3_cliente);
        observaciones = (TextView) rootView.findViewById(R.id.gtv_observaciones_cliente);
        afore = (TextView) rootView.findViewById(R.id.gtv_afore_cliente);
        motivo = (TextView) rootView.findViewById(R.id.gtv_motivo_cliente);
        estatus = (TextView) rootView.findViewById(R.id.gtv_estatus_cliente);
        instituto = (TextView) rootView.findViewById(R.id.gtv_instituto_cliente);
        regimen = (TextView) rootView.findViewById(R.id.gtv_regimen_cliente);
        documentacion = (TextView) rootView.findViewById(R.id.gtv_documentacion_cliente);
    }

    private void argumentos(){
        if(getArguments()!=null){
            nombreAsesor.setText(getArguments().getString("nombreAsesor"));
            numEmpleadoAsesor.setText(getArguments().getString("cuentaAsesor"));
            sucursalAsesor.setText(getArguments().getString("sucursalAsesor"));
            nombreCliente.setText(getArguments().getString("nombreCliente"));
            numCuentaCliente.setText(getArguments().getString("numCuentaCliente"));
            nssCliente.setText(getArguments().getString("nssCliente"));
            curpCliente.setText(getArguments().getString("curpCliente"));
            fechaCliente.setText(getArguments().getString("fechaCliente"));
            saldoCliente.setText(getArguments().getString("saldoCliente"));

            if(getArguments().getBoolean("pregunta1"))
                pregunta1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_check_circle, 0, 0, 0);
            else
                pregunta1.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_cancel, 0, 0, 0);

            if(getArguments().getBoolean("pregunta2"))
                pregunta2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_check_circle, 0, 0, 0);
            else
                pregunta2.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_cancel, 0, 0, 0);

            if(getArguments().getBoolean("pregunta3"))
                pregunta3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_check_circle, 0, 0, 0);
            else
                pregunta3.setCompoundDrawablesWithIntrinsicBounds(R.drawable.icono_cancel, 0, 0, 0);

            observaciones.setText(getArguments().getString("observaciones"));
            afore.setText(getArguments().getString("afore"));
            motivo.setText(getArguments().getString("motivo"));
            estatus.setText(getArguments().getString("estatus"));
            instituto.setText(getArguments().getString("instituto"));
            regimen.setText(getArguments().getString("regimen"));
            documentacion.setText(getArguments().getString("documentacion"));
            telefonoCliente.setText(getArguments().getString("telefono"));
            emailCliente.setText(getArguments().getString("email"));
        }
    }

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, String firmaIMG) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 1137);
            rqt.put("idTramite", Config.ID_TRAMITE);
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", gps.getLatitude());
            ubicacion.put("longitud", gps.getLongitude());
            rqt.put("ubicacion", ubicacion);
            rqt.put("firmaCliente", firmaIMG);
            obj.put("rqt", rqt);
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_FIRMA, obj);
    }

    /**
     * Corre este metodo cuando hay mas de 10 contenido a mostrar en la lista
     * @param obj objeto json
     */
    private void primerPaso(JSONObject obj){
        Log.e("primerPaso", obj.toString());
    }
}
