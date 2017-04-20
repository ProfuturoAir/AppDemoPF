package com.airmovil.profuturo.ti.retencion.asesorFragmento;

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

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReporteClientesDetalle.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteClientesDetalle#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteClientesDetalle extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private String sParam1;
    private int sParam2;
    private String sParam3;
    private String sParam4;
    private String sParam5;

    // TODO: XML
    private TextView tvInicial;
    private TextView tvNombreAsesor;
    private TextView tvNumeroEmpleado;
    private TextView tvFecha;
    private TextView tvNombreCliente;
    private TextView tvNumeroCuentaCliente;
    private TextView tvNSS;
    private TextView tvEstatus;
    private TextView tvSaldo;
    private TextView tvSucursales;
    private TextView tvHoraAtencion;
    private TextView tvCurp;

    private View rootView;

    private OnFragmentInteractionListener mListener;

    public ReporteClientesDetalle() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteClientesDetalle.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteClientesDetalle newInstance(String param1, String param2) {
        ReporteClientesDetalle fragment = new ReporteClientesDetalle();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;

        tvInicial = (TextView) rootView.findViewById(R.id.afrcd_tv_letra);
        tvNombreAsesor = (TextView) rootView.findViewById(R.id.afrcd_tv_nombre_asesor);
        tvNumeroEmpleado = (TextView) rootView.findViewById(R.id.afrcd_tv_numero_empleado_asesor);
        tvFecha = (TextView) rootView.findViewById(R.id.afrcd_tv_fecha);

        tvNombreCliente = (TextView) rootView.findViewById(R.id.afrcd_tv_nombre_cliente);
        tvNumeroCuentaCliente = (TextView) rootView.findViewById(R.id.afrcd_tv_numero_cuenta_cliente);
        tvNSS = (TextView) rootView.findViewById(R.id.afrcd_tv_nss_cliente);
        tvCurp = (TextView) rootView.findViewById(R.id.afrcd_tv_curp_cliente);
        tvEstatus = (TextView) rootView.findViewById(R.id.afrcd_tv_status_cliente);
        tvSaldo = (TextView) rootView.findViewById(R.id.afrcd_tv_fecha_cliente);
        tvSucursales = (TextView) rootView.findViewById(R.id.afrcd_tv_saldo_cliente);
        tvHoraAtencion = (TextView) rootView.findViewById(R.id.afrcd_tv_hora_atencion);


        sendJson(true);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_reporte_clientes_detalle, container, false);
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

        //final ProgressDialog loading;
        //if (primeraPeticion)
          //  loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        //else
          //  loading = null; sParam1 = getArguments().getString("curp");
        sParam1 = getArguments().getString("curp");
        sParam2 = getArguments().getInt("idTramite");
        sParam3 = getArguments().getString("hora");
        sParam4 = getArguments().getString("fechaInicio");
        sParam5 = getArguments().getString("fechaFin");

        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> datosUsuario = sessionManager.getUserDetails();
        String usuario = datosUsuario.get(SessionManager.USER_ID);
        String nombreAsesor = datosUsuario.get(SessionManager.NOMBRE);
        String apePaternoAsesor = datosUsuario.get(SessionManager.APELLIDO_PATERNO);
        String apeMaternoAsesor = datosUsuario.get(SessionManager.APELLIDO_MATERNO);
        String numeroEmpleado = datosUsuario.get(SessionManager.NUMERO_EMPLEADO);

        tvNombreAsesor.setText("Nombre del Asesor: " +nombreAsesor + " " + apePaternoAsesor + " " + apeMaternoAsesor);
        tvNumeroEmpleado.setText("Número de empleado: " + numeroEmpleado);
        tvFecha.setText(sParam4 + " - " + sParam5);
        tvHoraAtencion.setText("Cita: " + sParam3);

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject filtro = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            // Log.d(" * * * * * * * ", "curp" + sParam1);
            // Log.d(" * * * * * * * ", "idTramite" + sParam2);
            // Log.d(" * * * * * * * ", "hora" + sParam3);
            // Log.d(" * * * * * * * ", "fechaInicio" + sParam4);
            // Log.d(" * * * * * * * ", "fechaFin" + sParam5);
            if(getArguments() != null){
                filtro.put("curp", sParam1);
                filtro.put("nss", "");
                filtro.put("numeroCuenta", "");
                rqt.put("filtro", filtro);
                rqt.put("idTramite", sParam2);
                periodo.put("fechaFin", sParam5);
                periodo.put("fechaInicio", sParam4);
                rqt.put("periodo",periodo);
                rqt.put("usuario", Config.usuarioCusp(getContext()));
                json.put("rqt", rqt);
            }else{
                filtro.put("curp", "");
                filtro.put("nss", "");
                filtro.put("numeroCuenta", "");
                rqt.put("filtro", filtro);
                rqt.put("idTramite", "");
                periodo.put("fechaFin", "");
                periodo.put("fechaInicio", "");
                rqt.put("periodo",periodo);
                rqt.put("usuario", usuario);
                json.put("rqt", rqt);
            }
            Log.d("RQT ->", "" + json);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un right_in al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_GENERAL_REPORTE_CLIENTE, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
                            //loading.dismiss();
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
                            //loading.dismiss();
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
                                    //sendJson(true, f1, f2);
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

        Log.d("TAG", "primerPaso: "  + obj );
        String nombre = "";
        String numeroCuenta = "";
        String curp = "";
        String nss = "";
        boolean estatus = false;
        int saldo = 0;
        String nombreSucursal = "";
        try{
            JSONObject cliente = obj.getJSONObject("cliente");
            nombre = cliente.getString("nombre");
            numeroCuenta = cliente.getString("numeroCuenta");
            curp = cliente.getString("curp");
            nss = cliente.getString("nss");
            estatus = cliente.getBoolean("retenido");
            saldo = cliente.getInt("saldo");
            nombreSucursal = cliente.getString("nombreSucursal");
        }catch (JSONException e){
            e.printStackTrace();
        }

        tvNombreCliente.setText(nombre);
        tvNumeroCuentaCliente.setText(numeroCuenta);
        tvCurp.setText("" + curp);
        tvNSS.setText("" + nss);
        if(estatus == true)
            tvEstatus.setText("Retenido");
        if(estatus == false)
            tvEstatus.setText("No retenido");
        tvSaldo.setText(Config.nf.format(saldo));
        tvSucursales.setText(nombreSucursal);


    }
}
