package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
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
    private static final String ARG_PARAM1 = "idSucursal";
    private static final String ARG_PARAM2 = "idTramite";
    private static final String ARG_PARAM3 = "numeroCuenta";
    private static final String ARG_PARAM4 = "fechaInicio";
    private static final String ARG_PARAM5 = "fechaFin";
    private static final String ARG_PARAM6 = "usuario";

    // TODO: Rename and change types of parameters
    private int mParam1; // id sucursal
    private int mParam2; // id tramite
    private String mParam3; // numeroCuenta
    private String mParam4; // fechaInicio
    private String mParam5; // fechafin
    private String mParam6; // usuario

    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;
    private TextView ddfrasd_tv_fecha;
    private SessionManager sessionManager;

    private OnFragmentInteractionListener mListener;

    public ReporteClientesDetalles() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 parametro 1 idsucursal.
     * @param param2 parametro 2 idTramite.
     * @param param3 parametro 3 numeroCuenta.
     * @param param4 parametro 4 fechaInicio.
     * @param param5 parametro 5 fechaFin.
     * @param param6 parametro 6 usuario.
     * @return A new instance of fragment ReporteClientesDetalles.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteClientesDetalles newInstance(int param1, int param2, String param3, String param4, String param5, String param6, View view ) {
        ReporteClientesDetalles fragment = new ReporteClientesDetalles();
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM1, param1);
        args.putInt(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putString(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getInt(ARG_PARAM1);
            mParam2 = getArguments().getInt(ARG_PARAM2);
            mParam3 = getArguments().getString(ARG_PARAM3);
            mParam4 = getArguments().getString(ARG_PARAM4);
            mParam5 = getArguments().getString(ARG_PARAM5);
            mParam6 = getArguments().getString(ARG_PARAM6);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tv1 = (TextView) view.findViewById(R.id.ddd_tv_clientes_numero_cuenta);
        tv2 = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_nss);
        tv3 = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_curp);
        tv4 = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_estatus);
        tv5 = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_saldo);
        tv6 = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_sucursal);
        tv7 = (TextView) view.findViewById(R.id.ddd_tv_clientes_detalles_hora_atencion);
        ddfrasd_tv_fecha = (TextView) view.findViewById(R.id.ddfrasd_tv_fecha);
        primeraPeticion();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.director_fragmento_reporte_clientes_detalles, container, false);
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

    private void primeraPeticion(){
        final ProgressDialog progressDialog = new ProgressDialog(getContext(), R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIcon(R.drawable.icono_abrir);
        progressDialog.setTitle(getResources().getString(R.string.msj_esperando));
        progressDialog.setMessage(getResources().getString(R.string.msj_espera));
        progressDialog.show();
        // TODO: Implement your own authentication logic here.
        new android.os.Handler().postDelayed(
                new Runnable() {
                    public void run() {
                        progressDialog.dismiss();
                        sendJson(true);
                    }
                }, 3000);
    }

    private void sendJson(final boolean primeraPeticion){

        final ProgressDialog loading;
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        JSONObject filtro = new JSONObject();

        try{

            // param1 parametro 1 idsucursal.
            // param2 parametro 2 idTramite.
            // param3 parametro 3 numeroCuenta.
            // param4 parametro 4 fechaInicio.
            // param5 parametro 5 fechaFin.
            // param6 parametro 6 usuario.
            if(getArguments() != null){
                mParam1 = getArguments().getInt(ARG_PARAM1);
                mParam2 = getArguments().getInt(ARG_PARAM2);
                mParam3 = getArguments().getString(ARG_PARAM3);
                mParam4 = getArguments().getString(ARG_PARAM4);
                mParam5 = getArguments().getString(ARG_PARAM5);
                mParam6 = getArguments().getString(ARG_PARAM6);
                rqt.put("filtro", filtro);
                    filtro.put("curp", "");
                    filtro.put("nss", "");
                    filtro.put("numeroCuenta", mParam3);
                rqt.put("idTramite", mParam2);
                rqt.put("periodo", periodo);
                    periodo.put("fechaInicio", mParam4);
                    periodo.put("fechaFin", mParam5);
                rqt.put("usuario", mParam6);
                obj.put("rqt", rqt);
            }
            Log.d("sendJson", " REQUEST -->" + obj);

        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
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
        Log.d("response -> 1", "" + obj);
        try{
            JSONObject cliente = obj.getJSONObject("cliente");
            String nombre = cliente.getString("nombre");
            String numeroCuenta = cliente.getString("nombreSucursal");
            String nss = cliente.getString("nss");
            String curp = cliente.getString("curp");
            boolean status = cliente.getBoolean("retenido");
            int saldo = cliente.getInt("saldo");

            tv1.setText("" + nombre);

        }catch (JSONException e){
            e.printStackTrace();
        }


    }
}
