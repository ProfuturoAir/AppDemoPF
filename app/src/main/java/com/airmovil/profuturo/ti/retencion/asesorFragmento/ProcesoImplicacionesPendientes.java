package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.content.Context;
import android.content.DialogInterface;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
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
    /* inicializacion de los paramentros del fragmento */
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private OnFragmentInteractionListener mListener;
    private View rootView;
    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private EnviarPendientesAdapter adapter;
    private Button btnEnviarPendientes;
    private List<EnviosPendientesModel> getDatos1;
    private SQLiteHandler db;

    public ProcesoImplicacionesPendientes() {/* constructor vacio es requerido*/}

    /**
     * al crear una nueva instancia
     * se reciben los parametros:
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return un objeto AsistenciaSalida.
     */
    public static ProcesoImplicacionesPendientes newInstance(String param1, String param2) {
        ProcesoImplicacionesPendientes fragment = new ProcesoImplicacionesPendientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * El sistema realiza esta llamada cuando crea tu actividad
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
    }

    /**
     * El sistema lo llama para iniciar los procesos que estaran dentro del flujo de la vista
     * @param view accede a la vista del xml
     * @param savedInstanceState guarda el estado de la aplicacion en un paquete
     */
    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Casteo
        rootView = view;
        btnEnviarPendientes = (Button) rootView.findViewById(R.id.afcc_btn_enviar_pendientes);
        // TODO: modelos
        Cursor todos = db.getAllPending();
        Log.d("HOLA","TODOS: "+todos);
        // TODO: Recycler
        recyclerView = (RecyclerView) rootView.findViewById(R.id.recyclerview_pendientes_envio);
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
        Connected connected = new Connected();
        final EnviaJSON enviaPrevio = new EnviaJSON();
        adapter = new EnviarPendientesAdapter(rootView.getContext(), getDatos1, recyclerView);
        recyclerView.setItemAnimator(new DefaultItemAnimator());
        recyclerView.setAdapter(adapter);
        if(getDatos1.size() < 1)
            return;
        btnEnviarPendientes.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
            Connected connected = new Connected();
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
                                Log.d("HOLA","EL ID : "+ iT);
                                enviaPrevio.sendPrevios(iT, getContext());
                                getDatos1.remove(i);
                                adapter.notifyDataSetChanged();
                            }
                        }
                    });
                    dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {}
                    });
                    dlgAlert.create().show();
                }
            }
        });
    }

    /**
     * @param inflater infla la vista XML
     * @param container muestra el contenido
     * @param savedInstanceState guarda los datos en el estado de la instancia
     * @return la vista con los elemetos del XML y metodos
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragment_proceso_implicaciones_pendientes, container, false);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    /**
     * Reciba una llamada cuando se asocia el fragmento con la actividad
     * @param context estado actual de la aplicacion
     */
    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    /**
     *Se lo llama cuando se desasocia el fragmento de la actividad.
     */
    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * Esta interfaz debe ser implementada por actividades que contengan esta
     * Para permitir que se comunique una interacción en este fragmento
     * A la actividad y potencialmente otros fragmentos contenidos en esta actividad.
     */
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }
}
