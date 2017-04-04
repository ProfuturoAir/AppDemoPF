package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.app.DatePickerDialog;
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
import android.widget.DatePicker;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
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
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
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
    public static Inicio newInstance(String param1, String param2) {
        Inicio fragment = new Inicio();
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
        sessionManager = new SessionManager(getActivity().getApplicationContext());
        HashMap<String, String> datos = sessionManager.getUserDetails();
        nombre = datos.get(SessionManager.NOMBRE);
        numeroUsuario = datos.get(SessionManager.ID);

        // CASTEO DE ELEMENTOS
        tvInicial      = (TextView) view.findViewById(R.id.gfi_tv_inicial);
        tvNombre       = (TextView) view.findViewById(R.id.gfi_tv_nombre);
        tvFecha        = (TextView) view.findViewById(R.id.gfi_tv_fecha);
        tvRetenidos    = (TextView) view.findViewById(R.id.gfi_tv_retenidos);
        tvNoRetenidos  = (TextView) view.findViewById(R.id.gfi_tv_no_retenidos);
        tvSaldoRetenido  = (TextView) view.findViewById(R.id.gfi_tv_saldo_a_favor);
        tvSaldoNoRetenido= (TextView) view.findViewById(R.id.gfi_tv_saldo_retenido);
        tvRangoFecha1  = (TextView) view.findViewById(R.id.gfi_tv_fecha_rango1);
        tvRangoFecha2  = (TextView) view.findViewById(R.id.gfi_tv_fecha_rango2);
        btnFiltro      = (Button) view.findViewById(R.id.gfi_btn_filtro);


        char letra = nombre.charAt(0);
        String convertirATexto = Character.toString(letra);

        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        if(getArguments() != null){
            fechaIni = getArguments().getString(ARG_PARAM1);
            fechaFin = getArguments().getString(ARG_PARAM2);
            //mParam2 = getArguments().getString(ARG_PARAM2);
            //fechaIni = getArguments().getString("fechaIni", "");
            //fechaFin = getArguments().getString("fechaFin", "");
            //Log.d("ARG onCreateView","A1: "+fechaIni +" A2: "+fechaIni);
            tvFecha.setText(fechaIni+" - "+fechaFin);
            //Log.d("FECHA","SI");
        }else {
            Map<String, String> fechas = Config.fechas(1);
            fechaFin = fechas.get("fechaFin");
            fechaIni = fechas.get("fechaIni");
            fechaMostrar = fechaIni;
            tvFecha.setText(fechaMostrar);
            //Log.d("FECHA","NO");
        }

        tvInicial.setText(convertirATexto);
        tvNombre.setText(nombre);

        rangoInicial();
        rangoFinal();

        final Fragment borrar = this;
        sendJson(true);
        btnFiltro.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String f1 = tvRangoFecha1.getText().toString();
                String f2 = tvRangoFecha2.getText().toString();

                if(f1.equals("Fecha Inicio")||f2.equals("Fecha final")){
                    Config.msj(v.getContext(),"Error de datos","Favor de introducir fechas para aplicar el filtro");
                }else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio procesoDatosFiltroInicio = com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio.newInstance(
                            (fechaIni.equals("") ? "" : fechaIni),
                            (fechaFin.equals("") ? "" : fechaFin),
                            rootView.getContext()
                    );
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

        final ProgressDialog loading;
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
        else
            loading = null;


        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        try{
            JSONObject periodo = new JSONObject();
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", fechaIni);
            periodo.put("fechaFin", fechaFin);
            rqt.put("usuario", numeroUsuario);
            json.put("rqt", rqt);
            Log.d(TAG, "REQUEST -->" + json);

        } catch (JSONException e){
            Config.msj(getContext(),"Error","Existe un error al formar la peticion");
        }

        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_CONSULTAR_RESUMEN_RETENCIONES, json,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        if(primeraPeticion){
                            loading.dismiss();
                            primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(getContext(),"Error", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                })
        {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                return headers;
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
        String iSaldoRetenido = "";
        String iSaldoNoRetenido = "";
        try{
            JSONObject infoConsulta = obj.getJSONObject("infoConsulta");
            retenidos = infoConsulta.getJSONObject("retenido");
            iRetenidos = (Integer) retenidos.get("retenido");
            iNoRetenidos = (Integer) retenidos.get("noRetenido");
            saldos = infoConsulta.getJSONObject("saldo");
            iSaldoRetenido = (String) saldos.get("saldoRetenido");
            iSaldoNoRetenido = (String) saldos.get( "saldoNoRetenido");
            Log.d("JSON", retenidos.toString());
        }catch (JSONException e){
            Config.msj(getContext(), "Error", "Lo sentimos ocurrio un error con los datos");
        }

        tvRetenidos.setText("" + iRetenidos);
        tvNoRetenidos.setText("" + iNoRetenidos);
        tvSaldoRetenido.setText("" + iSaldoRetenido);
        tvSaldoNoRetenido.setText("" + iSaldoNoRetenido);
    }
}
