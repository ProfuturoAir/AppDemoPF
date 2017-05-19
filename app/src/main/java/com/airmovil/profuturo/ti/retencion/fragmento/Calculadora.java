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
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.LinearLayout;
import android.widget.ProgressBar;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;

public class Calculadora extends Fragment {
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    private WebView web;
    private ProgressBar progressBar;
    private AlertDialog.Builder dialogo1;
    private ProgressDialog loading;
    private Connected connected;
    private LinearLayout linearLayout;

    public Calculadora() {/* Se requiere un constructor vacio */}

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
        connected = new Connected();
        web = (WebView) view.findViewById(R.id.webview01);
        linearLayout = (LinearLayout) view.findViewById(R.id.ll);
        web.setWebViewClient(new myWebClient());
        web.getSettings().setJavaScriptEnabled(true);
        web.loadUrl(Config.URL_CALCULA_RETIRO_AFORE);
        dialogo1 = new AlertDialog.Builder(getContext());
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
        return inflater.inflate(R.layout.fragmento_calculadora, container, false);
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
     * Inicia una conexion con la Http, para consumo de pagina web
     */
    public class myWebClient extends WebViewClient {

        /**
         * comienza la carga de las peticiones
         * @param view xml
         * @param url direccion web
         * @param favicon mapa de bits
         */
        @Override
        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            // TODO Auto-generated method stub
            super.onPageStarted(view, url, favicon);
            // verificando la conexion a internet
            if(Config.conexion(getContext())){
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

        /**
         * Metodo de finalizacion de carga o error en conexion a internet
         * @param view
         * @param url
         */
        @Override
        public void onPageFinished(WebView view, String url) {
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

                        MySharePreferences sessionManager = MySharePreferences.getInstance(getContext());
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                        if(sessionManager.getUserDetails().get("idRolEmpleado").equals("3")){
                            Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.directorFragmento.Inicio();
                            fragmentManager.beginTransaction().replace(R.id.content_director, fragmentoGenerico).commit();
                        }

                        if(sessionManager.getUserDetails().get("idRolEmpleado").equals("2")){
                            Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.gerenteFragmento.Inicio();
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                        }

                        if(sessionManager.getUserDetails().get("idRolEmpleado").equals("1")){
                            Fragment fragmentoGenerico = new com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }

                    }
                });
                dialogo1.setPositiveButton("Reintentar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        MySharePreferences sessionManager = MySharePreferences.getInstance(getContext());
                        Fragment fragmentoGenerico = new Calculadora();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();

                        if(sessionManager.getUserDetails().get("idRolEmpleado").equals("3"))
                            fragmentManager.beginTransaction().replace(R.id.content_director, fragmentoGenerico).commit();

                        if(sessionManager.getUserDetails().get("idRolEmpleado").equals("2"))
                            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();

                        if(sessionManager.getUserDetails().get("idRolEmpleado").equals("1"))
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                    }
                });
                dialogo1.show();
            }
        }
    }
}
