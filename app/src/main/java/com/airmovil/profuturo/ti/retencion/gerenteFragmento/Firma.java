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
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.GPSRastreador;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class Firma extends Fragment{
    private static final String TAG = Firma.class.getSimpleName();
    private DrawingView dvFirma;
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private SQLiteHandler db;
    private String idTramite, nombre, numeroDeCuenta, hora;
    private Button btnLimpiar, btnGuardar, btnCancelar;
    private Fragment borrar = this;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private GPSRastreador gps;
    private TextView tvNombre;

    public Firma() {/* Se requiere un constructor vacio */}


    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: Variables
        variables();
        // TODO: Argumentos, verfica si existen datos enviado de otras clases
        argumentos();
        // TODO: Clase para obtener las coordenadas
        gps = new GPSRastreador(getContext());

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvFirma.startNew();
                dvFirma.setDrawingCacheEnabled(true);
                //Dialogos.msjTime(v.getContext(), "Mensaje", "Limpiando contenido", 2000);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dvFirma.isActive()) {
                    Dialogos.msj(v.getContext(),"Error", "Se requiere una img_firma");
                }else{
                    if(dvFirma.isActive()){
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                        dialogo1.setTitle("Importante");
                        dialogo1.setMessage("¿Guardar esta img_firma?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dvFirma.setDrawingCacheEnabled(true);
                                final String base64 = Config.encodeTobase64(getContext(), dvFirma.getDrawingCache());
                                dvFirma.setDrawingCacheEnabled(false);
                                if(Config.conexion(getContext())) {
                                    if(Config.estahabilitadoGPS(getContext())){
                                        sendJson(true, base64);
                                        Fragment fragmentoGenerico = new Escaner();
                                        Gerente gerente = (Gerente) getContext();
                                        gerente.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                                    }else{
                                        Dialogos.dialogoActivarLocalizacion(getContext());
                                    }
                                }else{
                                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                                    dialogo.setTitle(getResources().getString(R.string.error_conexion));
                                    dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                                    dialogo.setCancelable(false);
                                    dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            db.addFirma(idTramite,1137,base64,gps.getLatitude(),gps.getLongitude());
                                            db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                            Fragment fragmentoGenerico = new Escaner();
                                            Gerente gerente = (Gerente) getContext();
                                            gerente.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta);
                                        }
                                    });
                                    dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {}
                                    });
                                    dialogo.show();
                                }
                            }
                        });
                        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {}
                        });
                        dialogo1.show();
                    }
                }
            }
        });

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

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * El fragment se ha adjuntado al Activity
     * @param context
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     * El Fragment ha sido quitado de su Activity y ya no está disponible
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                    Fragment fragment = new SinCita();
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 2, fragment);
                    return true;
                }
                return false;
            }
        });
        super.onResume();
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
     * Setear las variables de xml y nueva instancia de objeto
     */
    private void variables(){
        btnLimpiar = (Button) rootView.findViewById(R.id.gff_btn_limpiar);
        btnGuardar = (Button) rootView.findViewById(R.id.gff_btn_guardar);
        btnCancelar= (Button) rootView.findViewById(R.id.gff_btn_cancelar);
        tvNombre = (TextView) rootView.findViewById(R.id.gff_tv_nombre);
        dvFirma = (DrawingView) rootView.findViewById(R.id.gff_dv_firma);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);
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
            tvNombre.setText(nombre);
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
                if(Config.conexion(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        };
    }

    /**
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion, String firmaBase64) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        idTramite = getArguments().getString("idTramite");

        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 1137);
            rqt.put("idTramite", Integer.parseInt(idTramite));
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", gps.getLongitude());
            ubicacion.put("longitud", gps.getLongitude());
            rqt.put("ubicacion", ubicacion);
            rqt.put("firmaCliente", firmaBase64);
            obj.put("rqt", rqt);
            Log.d(TAG, "<- RQT ->" + obj);
        } catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_FIRMA, obj);
    }

    /**
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj) {

    }
}
