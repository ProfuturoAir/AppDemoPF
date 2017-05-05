package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.Map;

public class Inicio extends Fragment {
    public static final String TAG = Inicio.class.getSimpleName();
    private static final String ARG_PARAM1 = "fechaInicio";
    private static final String ARG_PARAM2 = "fechaFin";
    private String mParam1; // fecha Inicio
    private String mParam2; // fecha fin
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private TextView tvInicial, tvNombre, tvFecha, tvRetenidos, tvNoRetenidos, tvSaldoRetenido, tvSaldoNoRetenido, tvRangoFecha1, tvRangoFecha2;
    private JSONObject retenidos = null;
    private JSONObject saldos = null;
    private Button btnFiltro;
    private Fragment borrar = this;
    private Connected connected;
    private int iRetenidos = 0;
    private int iNoRetenidos = 0;
    private int iSaldoRetenido = 0;
    private int iSaldoNoRetenido = 0;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;

    public Inicio() {/* Constructor público vacío obligatorio*/}

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     * @param param1 parametro 1.
     * @param param2 parametro 2.
     * @return una nueva instancia del fragmento Inicio.
     */
    // TODO: Rename and change types and number of parameters
    public static Inicio newInstance(String param1, String param2, Context context) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    /**
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        // TODO: CASTEO DE ELEMENTOS
        variables();
        argumentos();
        // TODO: Colocando la parte superior de la seccion: 1er letra del nombre, Nombre
        detalleSuperior();
        // TODO: inclucion de fechas
        // TODO: Rango inicial y final para seleccionar el dialogo de fechas
        Dialogos.dialogoFechaInicio(getContext(), tvRangoFecha1);
        Dialogos.dialogoFechaFin(getContext(), tvRangoFecha2);
        // TODO: inicio de la primera peticion REST
       if(Config.conexion(getContext()))
           sendJson(true);
       else
           Dialogos.dialogoErrorConexion(getContext());

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String fechaIncial = tvRangoFecha1.getText().toString();
                final String fechaFinal = tvRangoFecha2.getText().toString();
                if(fechaIncial.equals("") || fechaFinal.equals("")){
                    Config.dialogoFechasVacias(getContext());
                }else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Fragment procesoDatosFiltroInicio = Inicio.newInstance(fechaIncial, fechaFinal, rootView.getContext());
                    borrar.onDestroy();
                    ft.remove(borrar).replace(R.id.content_director, procesoDatosFiltroInicio).addToBackStack(null).commit();
                }
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
        return inflater.inflate(R.layout.director_fragmento_inicio, container, false);
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
     * Se lo llama cuando se desasocia el fragmento de la actividad.
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
     * Setear las variables de xml
     */
    public void variables(){
        connected = new Connected();
        tvInicial = (TextView) rootView.findViewById(R.id.dfi_tv_inicial);
        tvNombre = (TextView) rootView.findViewById(R.id.dfi_tv_nombre);
        tvFecha = (TextView) rootView.findViewById(R.id.dfi_tv_fecha);
        tvRetenidos = (TextView) rootView.findViewById(R.id.dfi_tv_retenidos);
        tvNoRetenidos = (TextView) rootView.findViewById(R.id.dfi_tv_no_retenidos);
        tvSaldoRetenido = (TextView) rootView.findViewById(R.id.dfi_tv_saldo_a_favor);
        tvSaldoNoRetenido = (TextView) rootView.findViewById(R.id.dfi_tv_saldo_retenido);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.dfi_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.dfi_tv_fecha_rango2);
        btnFiltro = (Button) rootView.findViewById(R.id.dfi_btn_filtro);
    }

    private void argumentos(){
        if(getArguments() != null){
            tvFecha.setText(getArguments().getString(ARG_PARAM1) + " - " + getArguments().getString(ARG_PARAM2));
            tvRangoFecha1.setText(getArguments().getString(ARG_PARAM1));
            tvRangoFecha2.setText(getArguments().getString(ARG_PARAM2));
        }else{
            tvFecha.setText(Dialogos.fechaActual() + " - " + Dialogos.fechaSiguiente());
        }
    }

    /**
     * Obteniendo los valores del apartado superior, nombre
     */
    public void detalleSuperior(){
        Map<String, String> usuarioDatos = Config.datosUsuario(getContext());
        String nombre = usuarioDatos.get(SessionManager.NOMBRE);
        String apePaterno = usuarioDatos.get(SessionManager.APELLIDO_PATERNO);
        String apeMaterno = usuarioDatos.get(SessionManager.APELLIDO_MATERNO);
        tvNombre.setText(nombre + " " + apePaterno + " " + apeMaterno);
        char dato = nombre.charAt(0);
        tvInicial.setText(String.valueOf(dato));
    }

    /**
     *
     * @param
     */
    public void sendJson(final boolean primerPeticion){
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;
        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            boolean argumentos = (getArguments()!=null);
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", (argumentos) ? mParam1 : Dialogos.fechaActual());
            periodo.put("fechaFin", (argumentos) ? mParam2 : Dialogos.fechaSiguiente());
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            json.put("rqt", rqt);
            Log.d(TAG, "RQT -->" + json);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CONSULTAR_RESUMEN_RETENCIONES, json);
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
     * @param obj recibe el obj json de la peticion
     */
    private void primerPaso(JSONObject obj) {
        try {
            if (rootView != null)
                tvRetenidos = (TextView) rootView.findViewById(R.id.dfi_tv_retenidos);
        } catch (Exception e) {
            Log.e("-->es error:", e.toString());
        }
        try {
            JSONObject infoConsulta = obj.getJSONObject("infoConsulta");
            retenidos = infoConsulta.getJSONObject("retenido");
            iRetenidos = (Integer) retenidos.get("retenido");
            iNoRetenidos = (Integer) retenidos.get("noRetenido");
            saldos = infoConsulta.getJSONObject("saldo");
            iSaldoRetenido = (Integer) saldos.get("saldoRetenido");
            iSaldoNoRetenido = (Integer) saldos.get("saldoNoRetenido");
        } catch (JSONException e) {
            Config.msj(getContext(), "Error", "Lo sentimos ocurrio un error con los datos");
        }
        tvRetenidos.setText("" + iRetenidos);
        tvNoRetenidos.setText("" + iNoRetenidos);
        tvSaldoRetenido.setText("" + Config.nf.format(iSaldoRetenido));
        tvSaldoNoRetenido.setText("" + Config.nf.format(iSaldoNoRetenido));
    }

}