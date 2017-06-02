package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.NetworkResponse;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link IneIfe.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link IneIfe#newInstance} factory method to
 * create an instance of this fragment.
 */
public class IneIfe extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private static final String TAG = IneIfe.class.getSimpleName();
    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private View rootView;

    public IneIfe() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment IneIfe.
     */
    // TODO: Rename and change types and number of parameters
    public static IneIfe newInstance(String param1, String param2) {
        IneIfe fragment = new IneIfe();
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
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.asesor_fragmento_ine_ife, container, false);
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

    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                loading.dismiss();
                primerPaso(response);
            }
            @Override
            public void notifyError(String requestType, VolleyError error) {
                NetworkResponse networkResponse = error.networkResponse;
                Log.e(TAG, "*->" + networkResponse);
                if(networkResponse == null){
                    loading.dismiss();
                }
                /*if (networkResponse != null) {
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                } else{
                    Log.e("Status code", String.valueOf(networkResponse.statusCode));
                }*/

                /*if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }*/
            }
        };
    }

    /**
     * Método para generar el proceso REST
     * @param primeraPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primeraPeticion){
        if (primeraPeticion)
            loading = ProgressDialog.show(getActivity(), getResources().getString(R.string.titulo_carga_datos), getResources().getString(R.string.msj_carga_datos), false, false);
        else
            loading = null;

        JSONObject json = new JSONObject();
        JSONObject rqt = new JSONObject();
        JSONObject periodo = new JSONObject();
        try{
            rqt.put("periodo", periodo);
            periodo.put("fechaInicio", (getArguments()!=null) ? getArguments().getString(ARG_PARAM1) : Dialogos.fechaActual());
            periodo.put("fechaFin", (getArguments()!=null) ? getArguments().getString(ARG_PARAM2) : Dialogos.fechaSiguiente());
            rqt.put("usuario", Config.usuarioCusp(getContext()));
            json.put("rqt", rqt);
            Log.d(TAG, "<-- RQT --> \n " + json + "\n");
        } catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_CONSULTAR_RESUMEN_RETENCIONES, json);
    }

    /**
     * Obtiene el objeto json(Response), se obtiene cada elemento a parsear
     * @param obj json objeto
     */
    private void primerPaso(JSONObject obj){
        Log.d(TAG, "<- Response ->\n"  + obj +"\n");
        JSONObject retenidos = null;
        JSONObject saldos = null;
        int iRetenidos = 0, iNoRetenidos = 0, iSaldoRetenido = 0, iSaldoNoRetenido = 0;
        String status = "";
        try{
            status = obj.getString("status");
            int i = Integer.parseInt(status);
            String statusText = "";
            if(i == 200){
                JSONObject infoConsulta = obj.getJSONObject("infoConsulta");
                retenidos = infoConsulta.getJSONObject("retenido");
                iRetenidos = (Integer) retenidos.get("retenido");
                iNoRetenidos = (Integer) retenidos.get("noRetenido");
                saldos = infoConsulta.getJSONObject("saldo");
                iSaldoRetenido = (int) saldos.get("saldoRetenido");
                iSaldoNoRetenido = (int) saldos.get( "saldoNoRetenido");
                Log.d("JSON", retenidos.toString());
            }else{
                statusText = obj.getString("statusText");
                Dialogos.dialogoErrorRespuesta(getContext(), String.valueOf(i), statusText);
            }
        }catch (JSONException e){
            Dialogos.dialogoErrorDatos(getContext());
        }
    }
}
