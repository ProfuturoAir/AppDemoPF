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
    private static final String ARG_PARAM1 = "param1"; // curp
    private static final String ARG_PARAM2 = "param2"; // nss
    private static final String ARG_PARAM3 = "param3"; // numero de cuenta
    private static final String ARG_PARAM4 = "param4"; // idTramite
    private static final String ARG_PARAM5 = "param5"; // fecha inicio
    private static final String ARG_PARAM6 = "param6"; // fecha fin
    private static final String ARG_PARAM7 = "param6"; // hora


    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private String mParam3;
    private int mParam4;
    private String mParam5;
    private String mParam6;
    private String mParam7;

    private TextView tv1, tv2, tv3, tv4, tv5, tv6, tv7;

    private OnFragmentInteractionListener mListener;

    public ReporteClientesDetalles() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 parametro 1 curp.
     * @param param1 parametro 2 nss.
     * @param param1 parametro 3 numero de cuenta.
     * @param param1 parametro 4 idTramite.
     * @param param1 parametro 5 fecha inicio.
     * @param param1 parametro 6 fecha fin.
     * @param param1 parametro 7 fecha hora.
     * @return A new instance of fragment ReporteClientesDetalles.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteClientesDetalles newInstance(String param1, String param2, String param3,
                                                      int param4, String param5, String param6, String param7) {
        ReporteClientesDetalles fragment = new ReporteClientesDetalles();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        args.putString(ARG_PARAM3, param3);
        args.putInt(ARG_PARAM4, param4);
        args.putString(ARG_PARAM5, param5);
        args.putString(ARG_PARAM6, param6);
        args.putString(ARG_PARAM7, param7);
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
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        tv1 = (TextView) view.findViewById(R.id.tv_clientes_detalles1);
        tv2 = (TextView) view.findViewById(R.id.tv_clientes_detalles2);
        tv3 = (TextView) view.findViewById(R.id.tv_clientes_detalles3);
        tv4 = (TextView) view.findViewById(R.id.tv_clientes_detalles4);
        tv5 = (TextView) view.findViewById(R.id.tv_clientes_detalles5);
        tv6 = (TextView) view.findViewById(R.id.tv_clientes_detalles6);
        tv7 = (TextView) view.findViewById(R.id.tv_clientes_detalles7);


    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
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
            /*  ARG_PARAM1 = "param1"; // curp
                ARG_PARAM2 = "param2"; // nss
                ARG_PARAM3 = "param3"; // numero de cuenta
                ARG_PARAM4 = "param4"; // idTramite
                ARG_PARAM5 = "param5"; // fecha inicio
                ARG_PARAM6 = "param6"; // fecha fin
                ARG_PARAM7 = "param6"; // hora  */
            if(getArguments() != null){

                rqt.put("filtro", filtro);
                    filtro.put("nss", mParam1);
                    filtro.put("curp", mParam2);
                    filtro.put("numeroCuenta", mParam3);
                rqt.put("idTramite", mParam4);
                rqt.put("periodo", periodo);
                    periodo.put("fechaInicio", mParam5);
                    periodo.put("fechaFin", mParam6);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                obj.put("rqt", rqt);
            }
            Log.d("sendJson", " REQUEST -->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un right_in al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_REPORTE_RETENCION_CLIENTES, obj,
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
    }
}
