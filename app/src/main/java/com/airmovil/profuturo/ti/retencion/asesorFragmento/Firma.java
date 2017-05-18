package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
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
import android.widget.Button;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.DrawingView;
import com.airmovil.profuturo.ti.retencion.helper.GPSRastreador;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import org.json.JSONException;
import org.json.JSONObject;

public class Firma extends Fragment{
    private DrawingView dvFirma;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private SQLiteHandler db;
    private Fragment borrar = this;
    private GPSRastreador gps;
    private Button btnLimpiar, btnGuardar, btnCancelar;
    private String idTramite, nombre, numeroDeCuenta, hora;

    public Firma() {/* constructor vacio es requerido*/}

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, view.getContext());
        btnLimpiar = (Button) view.findViewById(R.id.aff_btn_limpiar);
        btnGuardar = (Button) view.findViewById(R.id.aff_btn_guardar);
        btnCancelar= (Button) view.findViewById(R.id.aff_btn_cancelar);
        if(getArguments()!=null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

        dvFirma = (DrawingView) view.findViewById(R.id.aff_dv_firma);
        dvFirma.setBrushSize(5);
        dvFirma.setColor("#000000");
        dvFirma.setFocusable(true);

        // TODO: Clase para obtener las coordenadas
        gps = new GPSRastreador(getContext());

        btnLimpiar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dvFirma.startNew();
                dvFirma.setDrawingCacheEnabled(true);
                Config.msjTime(v.getContext(), "Mensaje", "Limpiando contenido", 2000);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!dvFirma.isActive()) {
                    Dialogos.msj(v.getContext(),"Error", "Se requiere una firma");
                }else{
                    if(dvFirma.isActive()){
                        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                        dialogo1.setTitle("Importante");
                        dialogo1.setMessage("¿Guardar esta firma?");
                        dialogo1.setCancelable(false);
                        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dvFirma.setDrawingCacheEnabled(true);
                                final String base64 = Config.encodeTobase64(getContext(), dvFirma.getDrawingCache());
                                dvFirma.setDrawingCacheEnabled(false);
                                if(Config.conexion(getContext())) {
                                    sendJson(true, base64);
                                    loading.dismiss();
                                    Fragment fragmentoGenerico = new Escaner();
                                    Asesor asesor = (Asesor) getContext();
                                    asesor.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
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
                                            Asesor asesor = (Asesor) getContext();
                                            asesor.switchDocumento(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
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
                            public void onClick(DialogInterface dialog, int which) {
                            }
                        });
                        dialogo1.show();
                    }
                }
            }
        });

        // TODO: Buton cancelar porceso de firma
        // TODO Cancelar el proceso
        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        Dialogos.dialogoCancelarProcesoImplicaciones(getContext(), btnCancelar, fragmentManager, getResources().getString(R.string.msj_cancelar_1137), 1);
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
        return inflater.inflate(R.layout.asesor_fragmento_firma, container, false);
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
                    Dialogos.dialogoBotonRegresoProcesoImplicaciones(getContext(), fragmentManager, getResources().getString(R.string.msj_regresar_proceso), 1, fragment);
                    return true;
                }
                return false;
            }
        });
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
        idTramite = getArguments().getString("idTramite");
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 1137);
            rqt.put("idTramite", Integer.parseInt(idTramite));
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
}
