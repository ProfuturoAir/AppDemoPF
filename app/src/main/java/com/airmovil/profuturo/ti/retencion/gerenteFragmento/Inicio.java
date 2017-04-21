package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.util.Base64;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
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

import java.util.HashMap;
import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Inicio.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Inicio#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Inicio extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "parametro1";
    private static final String ARG_PARAM2 = "parametro2";
    public static final String TAG = Inicio.class.getSimpleName();

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;

    // TODO: XML
    private TextView tvInicial, tvNombre, tvFecha;
    private TextView tvRetenidos, tvNoRetenidos, tvSaldoRetenido, tvSaldoNoRetenido;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnFiltro;

    // TODO: variable
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private String numeroUsuario, nombre;
    final Fragment borrar = this;
    public Inicio() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Inicio.
     */
    // TODO: Rename and change types and number of parameters
    public static Inicio newInstance(String param1, String param2, Context context) {
        Inicio fragment = new Inicio();
        Bundle args = new Bundle();
        args.putString("parametro1", param1);
        args.putString("parametro2", param2);
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
        variables();
        detalleSuperior();
        primeraPeticion();
        fechas();

        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                final String fechaIncial = tvRangoFecha1.getText().toString();
                final String fechaFinal = tvRangoFecha2.getText().toString();

                if(fechaIncial.equals("") || fechaFinal.equals("")){
                    Config.dialogoFechasVacias(getContext());
                }else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    Inicio procesoDatosFiltroInicio = Inicio.newInstance(fechaIncial, fechaFinal, rootView.getContext());
                    borrar.onDestroy();
                    ft.remove(borrar);
                    ft.replace(R.id.content_gerente, procesoDatosFiltroInicio);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gerente_fragmento_inicio, container, false);
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

    private void variables(){
        tvInicial      = (TextView) rootView.findViewById(R.id.gfi_tv_inicial);
        tvNombre       = (TextView) rootView.findViewById(R.id.gfi_tv_nombre);
        tvFecha        = (TextView) rootView.findViewById(R.id.gfi_tv_fecha);
        tvRetenidos    = (TextView) rootView.findViewById(R.id.gfi_tv_retenidos);
        tvNoRetenidos  = (TextView) rootView.findViewById(R.id.gfi_tv_no_retenidos);
        tvSaldoRetenido  = (TextView) rootView.findViewById(R.id.gfi_tv_saldo_a_favor);
        tvSaldoNoRetenido= (TextView) rootView.findViewById(R.id.gfi_tv_saldo_retenido);
        tvRangoFecha1  = (TextView) rootView.findViewById(R.id.gfi_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) rootView.findViewById(R.id.gfi_tv_fecha_rango2);
        btnFiltro      = (Button) rootView.findViewById(R.id.gfi_btn_filtro);
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
                }, Config.TIME_HANDLER);
    }

    /**
     * Obteniendo los valores del apartado superior, nombre
     */
    public void detalleSuperior(){
        rangoInicial();
        rangoFinal();
        Map<String, String> usuarioDatos = Config.datosUsuario(getContext());
        String nombre = usuarioDatos.get(SessionManager.NOMBRE);
        String apePaterno = usuarioDatos.get(SessionManager.APELLIDO_PATERNO);
        String apeMaterno = usuarioDatos.get(SessionManager.APELLIDO_MATERNO);
        char letra = nombre.charAt(0);
        String convertirATexto = Character.toString(letra);
        tvNombre.setText(nombre + " " + apePaterno + " " + apeMaterno);
        tvInicial.setText(convertirATexto);
    }

    /**
     *  Espera el regreso de fechas incial (hoy y el dia siguiente)
     *  y cuando se realiza una nueva busqueda, retorna las fechas seleccionadas
     */
    private void fechas(){
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");
        // TODO: fecha
        Map<String, String> fechaActual = Config.fechas(1);
        String smParam1 = fechaActual.get("fechaIni");
        String smParam2 = fechaActual.get("fechaFin");
        if(getArguments() != null){
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(mParam1 + " - " + mParam2);
        }else{
            tvFecha.setText(smParam1 + " - " + smParam2);
        }
    }

    private void rangoInicial(){
        tvRangoFecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvRangoFecha1.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void rangoFinal(){
        tvRangoFecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvRangoFecha2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void sendJson(final boolean primeraPeticion){
        SessionManager sessionManager = new SessionManager(getContext());
        HashMap<String, String> datosUsuario = sessionManager.getUserDetails();
        String idEmpleado = datosUsuario.get(SessionManager.USER_ID);
        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        try{
            if(getArguments() != null){
                JSONObject periodo = new JSONObject();
                mParam1 = getArguments().getString(ARG_PARAM1);
                mParam2 = getArguments().getString(ARG_PARAM2);
                rqt.put("periodo", periodo);
                periodo.put("fechaInicio", mParam1);
                periodo.put("fechaFin", mParam2);
                rqt.put("usuario", idEmpleado);
                json.put("rqt", rqt);
            }else{
                Map<String, String> fecha = Config.fechas(1);
                String param1 = fecha.get("fechaIni");
                String param2 = fecha.get("fechaFin");
                JSONObject periodo = new JSONObject();
                rqt.put("periodo", periodo);
                periodo.put("fechaInicio", param1);
                periodo.put("fechaFin", param2);
                rqt.put("usuario", idEmpleado);
                json.put("rqt", rqt);
            }
            Log.d(TAG, "REQUEST -->" + json);
        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_RESUMEN_RETENCIONES, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        try{
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
                                    sendJson(true);
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
        Log.d(TAG, "primerPaso: "  + obj );
        JSONObject retenidos = null;
        int iRetenidos = 0;
        int iNoRetenidos = 0;
        JSONObject saldos = null;
        int iSaldoRetenido = 0;
        int iSaldoNoRetenido = 0;
        try{
            JSONObject infoConsulta = obj.getJSONObject("infoConsulta");
            retenidos = infoConsulta.getJSONObject("retenido");
            iRetenidos = (Integer) retenidos.get("retenido");
            iNoRetenidos = (Integer) retenidos.get("noRetenido");
            saldos = infoConsulta.getJSONObject("saldo");
            iSaldoRetenido = (Integer) saldos.get("saldoRetenido");
            iSaldoNoRetenido = (Integer) saldos.get( "saldoNoRetenido");
            Log.d("JSON", retenidos.toString());
        }catch (JSONException e){
            Config.msj(getContext(), "Error", "Lo sentimos ocurrio un error con los datos");
        }

        tvRetenidos.setText("" + iRetenidos);
        tvNoRetenidos.setText("" + iNoRetenidos);
        tvSaldoRetenido.setText("" + Config.nf.format(iSaldoRetenido));
        tvSaldoNoRetenido.setText("" + Config.nf.format(iSaldoNoRetenido));
    }
}
