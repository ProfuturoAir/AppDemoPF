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
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;

//import com.airmovil.profuturo.ti.retencion.Adapter.CitasClientesAdapter;
import com.airmovil.profuturo.ti.retencion.Adapter.EnviarPendientesAdapter;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.model.EnviosPendientesModel;

import java.util.ArrayList;
import java.util.List;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ProcesoImplicacionesPendientes.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ProcesoImplicacionesPendientes#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ProcesoImplicacionesPendientes extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private View rootView;

    private RecyclerView recyclerView;
    private RecyclerView.LayoutManager recyclerViewLayoutManager;
    private RecyclerView.Adapter recyclerViewAdapter;
    private EnviarPendientesAdapter adapter;

    private Button btnEnviarPendientes;

    private List<EnviosPendientesModel> getDatos1;

    private SQLiteHandler db;

    public ProcesoImplicacionesPendientes() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ProcesoImplicacionesPendientes.
     */
    // TODO: Rename and change types and number of parameters
    public static ProcesoImplicacionesPendientes newInstance(String param1, String param2) {
        ProcesoImplicacionesPendientes fragment = new ProcesoImplicacionesPendientes();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        db = new SQLiteHandler(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        // TODO: Casteo
        rootView = view;


        btnEnviarPendientes = (Button) rootView.findViewById(R.id.gfcc_btn_enviar_pendientes);

        // TODO: modelos
        Cursor todos = db.getAllPending();
        Log.d("HOLA","TODOS: "+todos);
        // TODO: Recyc  ler
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

                Log.d("HOLA","EL ID : "+todos.getInt(0));
                getDatos1.add(getDatos2);
            }
        } finally {
            todos.close();
        }
        /*String [] columns = new String[] {
                db.FK_ID_TRAMITE,
                db.KEY_NOMBRE,
                db.KEY_NUMERO_CUENTA,
                db.KEY_HORA
        };
        int [] widgets = new int[] {
                R.id.personID,
                R.id.personName
        };*/

        Connected connected = new Connected();
        final EnviaJSON enviaPrevio = new EnviaJSON();
        /*
        if(connected.estaConectado(getContext())){
            Cursor pendientes = db.getAllPending();
                            try {
                                while (pendientes.moveToNext()) {
                                    //EnviosPendientesModel getDatos2 = new EnviosPendientesModel();

                                    Log.d("HOLA","EL ID : "+pendientes.getString(0));
                                    //if(){
                                        Log.d("Eliminado","Exitoso");
                                        //getDatos1.remove(pendientes.getString(0));
                                    enviaPrevio.sendPrevios(pendientes.getString(0), getContext());
                                    //Log.d("Respuesta","AQUI: "+enviaPrevio.sendPrevios(pendientes.getString(0), getContext()));
                                }
                            } finally {
                                pendientes.close();
                            }
        }
        */


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

                            //Log.d("Eliminado","Exitoso "+getDatos1);

                            for(int i=0;i<getDatos1.size();i++)
                            {
                                String iT = String.valueOf(getDatos1.get(i).getId_tramite());
                                Log.d("HOLA","EL ID : "+ iT);
                                enviaPrevio.sendPrevios(iT, getContext());
                                getDatos1.remove(i);
                                adapter.notifyDataSetChanged();
                            }

                            /*Cursor pendientes = db.getAllPending();
                            try {
                                while (pendientes.moveToNext()) {
                                    //EnviosPendientesModel getDatos2 = new EnviosPendientesModel();

                                    Log.d("HOLA","EL ID : "+pendientes.getString(0));
                                    //if(){
                                        Log.d("Eliminado","Exitoso");
                                        //getDatos1.remove(pendientes.getString(0));
                                    enviaPrevio.sendPrevios(pendientes.getString(0), getContext());
                                    //Log.d("Respuesta","AQUI: "+enviaPrevio.sendPrevios(pendientes.getString(0), getContext()));
                                }
                            } finally {
                                pendientes.close();
                            }*/

                        }
                    });
                    dlgAlert.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dlgAlert.create().show();
                }else{

                }

            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gerente_fragment_proceso_implicaciones_pendientes, container, false);
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
}
