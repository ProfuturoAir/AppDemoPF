package com.airmovil.profuturo.ti.retencion.fragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Calculadora.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Calculadora#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Calculadora extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;
    private WebView web;
    private ProgressBar progressBar;
    private AlertDialog.Builder dialogo1;
    private ProgressDialog loading;
    private Connected connected;
    private LinearLayout linearLayout;

    public Calculadora() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Calculadora.
     */
    // TODO: Rename and change types and number of parameters
    public static Calculadora newInstance(String param1, String param2) {
        Calculadora fragment = new Calculadora();
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
        connected = new Connected();

        web = (WebView) view.findViewById(R.id.webview01);

        linearLayout = (LinearLayout) view.findViewById(R.id.ll);

        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(Config.URL_CALCULA_RETIRO_AFORE);


        dialogo1 = new AlertDialog.Builder(getContext());
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragmento_calculadora, container, false);
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

    public class myWebClient extends WebViewClient {
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);


            if(connected.estaConectado(getContext())){
                loading = ProgressDialog.show(getActivity(), "Cargando datos", "Porfavor espere...", false, false);
                linearLayout.setVisibility(View.GONE);
            }else{
                web.setVisibility(View.GONE);
            }
        }

        @Override
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            // TODO Auto-generated method stub
            loading.dismiss();


            return true;
        }

        @Override
        public void onPageFinished(WebView view, String url) {
            // TODO Auto-generated method stub
            super.onPageFinished(view, url);
            if(connected.estaConectado(getActivity())) {
                loading.dismiss();
            }else{
                dialogo1.setTitle("Error en conexión");
                dialogo1.setMessage("Volver a intentar");
                dialogo1.setCancelable(false);
                dialogo1.setNegativeButton("Ir al inicio",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SessionManager sessionManager = new SessionManager(getContext().getApplicationContext());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                        if(sessionManager.getUserDetails().get("perfil").equals("1")){
                            Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.directorFragmento.Inicio();
                            fragmentManager.beginTransaction().replace(R.id.content_director, fragmentoGenerico).commit();
                        }

                        if(sessionManager.getUserDetails().get("perfil").equals("2")){
                            Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.gerenteFragmento.Inicio();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        }

                        if(sessionManager.getUserDetails().get("perfil").equals("3")){
                            Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }

                    }
                });
                dialogo1.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        SessionManager sessionManager = new SessionManager(getContext().getApplicationContext());
                        Fragment fragmentoGenerico = new Calculadora();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                        if(sessionManager.getUserDetails().get("perfil").equals("1"))
                            fragmentManager.beginTransaction().replace(R.id.content_director, fragmentoGenerico).commit();


                        if(sessionManager.getUserDetails().get("perfil").equals("2"))
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();


                        if(sessionManager.getUserDetails().get("perfil").equals("3"))
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();

                    }
                });
                dialogo1.show();
            }
            //progressBar.setVisibility(View.GONE);

        }
    }
}
