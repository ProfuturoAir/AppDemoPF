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
import android.widget.Button;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

public class DatosAsesor extends Fragment {
    public static final String TAG = DatosAsesor.class.getSimpleName();
    private View rootView;
    private TextView tvNombre, tvNumeroEmpleado, tvSucursal;
    private Button btnContinuar, btnCancelar;
    private String nombre, numeroDeCuenta, hora, idClienteCuenta;
    private OnFragmentInteractionListener mListener;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    public DatosAsesor() {/* Required empty public constructor */}

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
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
        // TODO: Asignacion de variables
        variables();
        // TODO: Argumentos, verificacion si hay datos enviados
        argumentos();

        sendJson(true);

        // TODO: obteniendo el numero del usuario
        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Connected connected = new Connected();
                if(connected.estaConectado(getContext())){
                    Fragment fragmentoGenerico = new DatosCliente();
                    Gerente gerente = (Gerente) getContext();
                    gerente.switchDatosCliente(fragmentoGenerico,nombre,numeroDeCuenta,hora);
                }else {
                    Dialogos.dialogoVerificarConexionInternet(getContext());
                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas salir?");
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
        return inflater.inflate(R.layout.gerente_fragmento_datos_asesor, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
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
     * Se implementa este metodo, para generar el regreso con clic nativo de android
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en ese
     * actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    /**
     * Se utiliza este metodo para el control de la tecla de retroceso
     */
    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener(){
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if(event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK){
                    AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                    dialogo.setTitle("Confirmar");
                    dialogo.setMessage("¿Estás seguro que deseas regresar?");
                    dialogo.setCancelable(false);
                    dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                            }
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
     * Se utiliza para cololar datos recibidos entre una busqueda(por ejemplo: fechas)
     */
    private void argumentos(){
        if(getArguments()!=null){
            idClienteCuenta =getArguments().getString("idClienteCuenta");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }
    }

    /**
     * Setear las variables de xml
     */
    private void variables(){
        tvNombre = (TextView) rootView.findViewById(R.id.gfda_tv_nombre_usuario);
        tvNumeroEmpleado = (TextView) rootView.findViewById(R.id.gfda_tv_numero_empleado);
        tvSucursal = (TextView) rootView.findViewById(R.id.gfda_tv_sucursal);
        btnContinuar = (Button) rootView.findViewById(R.id.gfda_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.gfda_btn_cancelar);
        connected = new Connected();
    }

    /**
     * Envio de datos por REST jsonObject
     * @param primerPeticion valida que el proceso sea true
     */
    private void sendJson(final boolean primerPeticion) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        try{
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            obj.put("rqt", rqt);
        }catch (JSONException e){
            e.printStackTrace();
        }
        Log.d("DatosAsesor", ":rqt -->" + obj);
        volleySingleton.postDataVolley("primerPaso", Config.URL_CONSULTAR_DATOS_ASESOR, obj);
    }

    /**
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj) {
        Log.d(TAG, "<- Response ->" + obj);
        String nombreCliente = "";
        String numeroCuenta = "";
        String status = "";
        String statusText ="";
        String sucursal ="";
        try{
            status = obj.getString("status");
            statusText = obj.getString("statusText");
            if(Integer.parseInt(status) == 200){
                Log.d(TAG, status.toString());
                JSONObject jsonAsesor = obj.getJSONObject("asesor");
                nombreCliente = jsonAsesor.getString("nombre");
                numeroCuenta = jsonAsesor.getString("numeroEmpleado");
                sucursal = jsonAsesor.getString("nombreSucursal");
            }else{
                android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                dlgAlert.setTitle(status);
                dlgAlert.setMessage(statusText);
                dlgAlert.setCancelable(true);
                dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendJson(true);
                    }
                });
                dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {}
                });
                dlgAlert.create().show();
            }
        }catch (JSONException e){
            e.printStackTrace();
        }
        tvNombre.setText(nombreCliente);
        tvNumeroEmpleado.setText(numeroCuenta);
        tvSucursal.setText(sucursal);
    }

}
