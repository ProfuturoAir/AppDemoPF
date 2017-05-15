package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTabHost;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.airmovil.profuturo.ti.retencion.R;
public class Asistencia extends Fragment {
    private FragmentTabHost mTabHost;
    private OnFragmentInteractionListener mListener;

    public Asistencia() {/* contructor vacio es requerido */}

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        mTabHost = (FragmentTabHost)view.findViewById(R.id.tabhost);
        mTabHost.setup(getActivity(), getChildFragmentManager(), android.R.id.tabcontent);
        mTabHost.addTab(mTabHost.newTabSpec("productividad").setIndicator("Entrada"), AsistenciaEntrada.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Entrada").setIndicator("Comida Salida"), AsistenciaComidaSalida.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Comida Salida").setIndicator("Entrada comida"), AsistenciaComidaEntrada.class, null);
        mTabHost.addTab(mTabHost.newTabSpec("Entrada comida").setIndicator("Salida"), AsistenciaSalida.class, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_asistencia, container, false);
    }

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
     * Esta interfaz debe ser implementada por actividades que la contengan
     * Para permitir que se comunique este fragmento con
     * la actividad y potencialmente otros fragmentos contenidos en esta actividad.
     * Ver la lección de formación de Android
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
}
