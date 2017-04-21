package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.Adapter.GerenteReporteClientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteClientesModel;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReporteClientesDetalles.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteClientesDetalles#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteClientesDetalles extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "numeroCuenta"; // curp
    private static final String ARG_PARAM2 = "cita"; // nss
    private static final String ARG_PARAM3 = "numeroCuenta"; // numero de cuenta
    private static final String ARG_PARAM4 = "idTramite"; // idTramite
    private static final String ARG_PARAM5 = "fechaInicio"; // fecha inicio
    private static final String ARG_PARAM6 = "fechaFin"; // fecha fin
    private static final String ARG_PARAM7 = "hora"; // hora
    private static final String ARG_PARAM8 = "usuario";
    private static final String ARG_PARAM9 = "numeroEmpleado";
    private static final String ARG_PARAM10 = "nombreAsesor";

    // TODO: Rename and change types of parameters
    private String mParam1; // numero cuenta
    private String mParam2; // cita
    private String mParam3; // numeroCuenta
    private int mParam4; // idtramite
    private String mParam5; // fechaInicio
    private String mParam6; // fechaFin
    private String mParam7; // usuario
    private String mParam8; // usuario
    private String mParam9;
    private String mParam10;
    private int pagina = 1;

    private TextView tv_nombre, tv_numero_cuenta, tv_nss, tv_curp, tv_estatus, tv_saldo, tv_sucursal, tv_hora_atencion;
    private TextView tv_nombre_asesor, tv_numero_empleado, tv_inicial, tv_fechas;

    private OnFragmentInteractionListener mListener;

    public ReporteClientesDetalles() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 parametro 1 numeroCuenta.
     * @param param2 parametro 2 cita.
     * @param param3 parametro 3 numeroCuenta.
     * @param param4 parametro 4 idTramite.
     * @param param5 parametro 5 fecha inicio.
     * @param param6 parametro 6 fecha fin.
     * @param param7 parametro 7 fecha hora.
     * @param param8 parametro 8 usuario
     * @return A new instance of fragment ReporteClientesDetalles.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteClientesDetalles newInstance(String param1, String param2, String param3, int param4, String param5, String param6, String param7, String param8,String param9,String param10) {
        ReporteClientesDetalles fragment = new ReporteClientesDetalles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
        args.putString(ARG_PARAM8, param8);
        args.putString(ARG_PARAM9, param9);
        args.putString(ARG_PARAM9, param10);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getInt(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getString(ARG_PARAM6);
            mParam7 = getArguments().getString(ARG_PARAM7);
            mParam8 = getArguments().getString(ARG_PARAM8);
            mParam9 = getArguments().getString(ARG_PARAM9);
            mParam10 = getArguments().getString(ARG_PARAM10);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tv_nombre = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_nombre);
        tv_numero_cuenta = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_numero_cuenta);
        tv_nss = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_nss);
        tv_curp = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_curp);
        tv_estatus = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_estatus);
        tv_saldo = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_saldo);
        tv_sucursal = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_sucursal);
        tv_hora_atencion = (TextView) view.findViewById(R.id.gf_tv_clientes_detalle_hora_atencion);
        tv_nombre_asesor = (TextView) view.findViewById(R.id.ggfrasd_tv_nombre_asesor);
        tv_numero_empleado = (TextView) view.findViewById(R.id.ggfrasd_tv_numero_empleado_asesor);
        tv_inicial = (TextView) view.findViewById(R.id.ggfrasd_tv_letra);
        tv_fechas = (TextView) view.findViewById(R.id.ggfrasd_tv_fecha);

        //tv_nombre.setText("123123123");
        tv_numero_empleado.setText(mParam9);
        tv_nombre_asesor.setText(mParam10);
        tv_fechas.setText(mParam6 + " - "+mParam7);


        sendJson(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gerente_fragmento_reporte_clientes_detalles, container, false);
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

    private void sendJson(final boolean primeraPeticion){

        final ProgressDialog loading;
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;
        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtro = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            if(getArguments() != null){
                rqt.put("filtro", filtro);
                    filtro.put("curp", mParam1);
                    filtro.put("nss", mParam2);
                    filtro.put("numeroCuenta", mParam3);
                rqt.put("idTramite", mParam4);
                rqt.put("idSucursal", 0);
                rqt.put("pagina", pagina);
                rqt.put("periodo", periodo);
                    periodo.put("fechaInicio", mParam5);
                    periodo.put("fechaFin", mParam6);
                rqt.put("retenido", "retenido");
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d("sendJson", " REQUEST -->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un right_in al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_CLIENTE_DETALLE, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
                            loading.dismiss();
                            primerPaso(response);
                        } else {
                            //segundoPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            loading.dismiss();
                        }catch (Exception e){
                            e.printStackTrace();
                        }

                        Connected connected = new Connected();
                        if(connected.estaConectado(getContext())){
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error");
                            dlgAlert.setMessage("Se ha encontrado un problema, deseas volver intentarlo");
                            dlgAlert.setCancelable(true);
                            dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //sendJson(true);
                                }
                            });
                            dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dlgAlert.create().show();
                        }else{
                            android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                            dlgAlert.setTitle("Error de conexión");
                            dlgAlert.setMessage("Se ha encontrado un problema, debes revisar tu conexión a internet");
                            dlgAlert.setCancelable(true);
                            dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    //sendJson(true);
                                }
                            });
                            dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });
                            dlgAlert.create().show();
                        }
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                return Config.credenciales(getContext());
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }

    private void primerPaso(JSONObject obj){
        Log.d("primer paso", "Response: "  + obj );

        String curp = "";
        String horaAtencion = "";
        int idTramite = 0;
        String nombre = "";
        String nombreSucursal = "";
        String nss = "";
        String numeroCuenta = "";
        boolean retenido = false;
        String rfc = "";
        String saldo = "";

        try{
            JSONObject cliente = obj.getJSONObject("cliente");
            curp = cliente.getString("curp");
            horaAtencion = cliente.getString("horaAtencion"); //
            nombre = cliente.getString("nombre"); //
            nombreSucursal = cliente.getString("nombreSucursal");
            nss = cliente.getString("nss"); //
            numeroCuenta = cliente.getString("numeroCuenta"); //
            retenido = cliente.getBoolean("retenido");
            saldo = cliente.getString("saldo");
        }catch (JSONException e){
            e.printStackTrace();
        }

        String retencion = "No Retenido";;

        if(retenido){
            retencion = "Retenido";
        }

        tv_nombre.setText("" + nombre);
        tv_numero_cuenta.setText("" + numeroCuenta);
        tv_nss.setText("" + nss);
        tv_curp.setText("" + curp);
        tv_estatus.setText("" + retencion);
        tv_saldo.setText("" + saldo);
        tv_sucursal.setText("" + nombreSucursal);
        tv_hora_atencion.setText("hora: " + horaAtencion);
    }
}
