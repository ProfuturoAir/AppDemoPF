package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import com.airmovil.profuturo.ti.retencion.Adapter.EnviarPendientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.model.EnviosPendientesModel;

import java.util.ArrayList;
import java.util.List;

public class ProcesoImplicacionesPendientes extends Fragment {
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private EnviarPendientesAdapter adapter;
    private Button btnEnviarPendientes;
    private List<EnviosPendientesModel> getDatos1;
    private SQLiteHandler db;
    private Connected connected;

    public ProcesoImplicacionesPendientes() { /* Se requiere un constructor vacio */}

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
    }

    /**
     * @param view regresa la vista
     * @param savedInstanceState parametros a enviar para conservar en el bundle
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        rootView = view;
        btnEnviarPendientes = (Button) rootView.findViewById(R.id.gfcc_btn_enviar_pendientes);
        // TODO: modelos
        Cursor todos = db.getAllPending();
        // TODO: Recycler
        connected = new Connected();
        recyclerView = (RecyclerView) rootView.findViewById(R.id.grecyclerview_pendientes_envio);
        recyclerView.setHasFixedSize(true);
        recyclerViewLayoutManager = new LinearLayoutManager(getActivity());
        recyclerView.setLayoutManager(recyclerViewLayoutManager);

        getDatos1 = new ArrayList<>();
        try {
            while (todos.moveToNext()) {
                EnviosPendientesModel getDatos2 = new EnviosPendientesModel();
                getDatos2.setId_tramite(todos.getInt(0));
                getDatos2.setNombreCliente(todos.getString(1));
                getDatos2.setNumeroCuenta(todos.getString(2));
                getDatos2.setHora(todos.getString(3));
                getDatos1.add(getDatos2);
            }
        } finally {
            todos.close();
        }


        final EnviaJSON enviaPrevio = new EnviaJSON();
        adapter = new EnviarPendientesAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);

        if(getDatos1.size() < 1)
            return;

        btnEnviarPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(connected.estaConectado(getContext())){
                    android.app.AlertDialog.Builder dlgAlert  = new android.app.AlertDialog.Builder(getContext());
                    dlgAlert.setTitle("Enviar pendientes");
                    dlgAlert.setMessage("Confirmar envio");
                    dlgAlert.setCancelable(true);
                    dlgAlert.setCancelable(true);
                    dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Log.d("ENVIANDO","Se han enviado todos los pendientes");
                            final EnviaJSON enviaPrevio = new EnviaJSON();
                            for(int i=0;i<getDatos1.size();i++) {
                                String iT = String.valueOf(getDatos1.get(i).getId_tramite());
                                enviaPrevio.sendPrevios(iT, getContext());
                                getDatos1.remove(i);
                                adapter.notifyDataSetChanged();
                            }
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
        });
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
        return inflater.inflate(R.layout.gerente_fragment_proceso_implicaciones_pendientes, container, false);
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
}
