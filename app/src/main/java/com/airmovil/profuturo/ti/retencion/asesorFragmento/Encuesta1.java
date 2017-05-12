package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import org.json.JSONException;
import org.json.JSONObject;

public class Encuesta1 extends Fragment {
    // inicializacion de los parametros del fragmento
    public static final String TAG = Encuesta1.class.getSimpleName();
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";
    private SQLiteHandler db;
    private String idTramite;
    String nombre;
    String numeroDeCuenta;
    String hora;
    private IResult mResultCallback = null;
    private VolleySingleton volleySingleton;
    private ProgressDialog loading;
    private Connected connected;

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;
    private OnFragmentInteractionListener mListener;
    // TODO: XML
    private View rootView;
    private CheckBox cb1si, cb1no, cb2si, cb2no, cb3si, cb3no;
    private EditText etObservaciones;
    private Button btnContinuar, btnCancelar;
    private boolean respuesta1, respuesta2, respuesta3;
    private Boolean r1, r2, r3;
    private String observaciones;
    private int estatusTramite = 1134;

    public Encuesta1() {
        // contructor vacio es requerido
    }

    /**
     * Utilice este método de fábrica para crear una nueva instancia de
     * Este fragmento utilizando los parámetros proporcionados.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return un nuevo fragmento de Encuesta1.
     */
    public static Encuesta1 newInstance(String param1, String param2) {
        Encuesta1 fragment = new Encuesta1();
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
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        // TODO: metodo para callback de volley
        initVolleyCallback();
        // TODO: Lineas para ocultar el teclado virtual (Hide keyboard)
        rootView = view;
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, rootView.getContext());
        variables();

        if(getArguments()!= null){
            idTramite = getArguments().getString("idTramite");
            nombre = getArguments().getString("nombre");
            numeroDeCuenta = getArguments().getString("numeroDeCuenta");
            hora = getArguments().getString("hora");
        }

        cb1si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb1no.setChecked(false);
                r1 = (b) ? true : false;
            }
        });

        cb1no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb1si.setChecked(false);
                r1 = (b) ? false : null;
            }
        });

        cb2si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb2no.setChecked(false);
                r2 = (b) ? true : null;
            }
        });

        cb2no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb2si.setChecked(false);
                r2 = (b) ? false : null;
            }
        });

        cb3si.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb3no.setChecked(false);
                r3 = (b) ? true : null;
            }
        });

        cb3no.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                cb3si.setChecked(false);
                r3 = (b) ? false : null;
            }
        });

        final Fragment borrar = this;

        btnContinuar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (r1 == null || r2 == null || r3 == null || etObservaciones.getText().toString().trim().isEmpty()) {
                    Config.dialogoDatosVacios(getContext());
                }else {
                    final Connected conectado = new Connected();
                    if(conectado.estaConectado(getContext())){
                        String o = etObservaciones.getText().toString();

                        sendJson(true, r1, r2, r3, o);
                        Config.teclado(getContext(), etObservaciones);
                        Fragment fragmentoGenerico = new Encuesta2();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        Asesor asesor = (Asesor) getContext();
                        asesor.switchEncuesta2(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                    }else{
                        AlertDialog.Builder dialogo = new AlertDialog.Builder(getContext());
                        dialogo.setTitle(getResources().getString(R.string.error_conexion));
                        dialogo.setMessage(getResources().getString(R.string.msj_sin_internet_continuar_proceso));
                        dialogo.setCancelable(false);
                        dialogo.setPositiveButton("Continuar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                Config.teclado(getContext(), etObservaciones);
                                Log.d("Respuesta sin conexion:" , "Respuesta 1" + r1 + "Respuesta 2" + r2 + "Respuesta 3" + r3 );
                                db.addEncuesta(idTramite,estatusTramite,r1,r2,r3,etObservaciones.getText().toString().trim());
                                db.addIDTramite(idTramite,nombre,numeroDeCuenta,hora);
                                Fragment fragmentoGenerico = new Encuesta2();
                                Asesor asesor = (Asesor) getContext();
                                asesor.switchEncuesta2(fragmentoGenerico, idTramite,borrar,nombre,numeroDeCuenta,hora);
                            }
                        });
                        dialogo.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {


                            }
                        });
                        dialogo.show();
                    }

                }
            }
        });

        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Confirmar");
                dialogo1.setMessage("¿Estás seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.4?");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Fragment fragmentoGenerico = new ConCita();
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                dialogo1.show();
            }
        });

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        /* infla la vista del fragmento */
        return inflater.inflate(R.layout.asesor_fragmento_encuesta1, container, false);
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

    @Override
    public void onResume() {
        super.onResume();
        getView().setFocusableInTouchMode(true);
        getView().requestFocus();
        getView().setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {

                if (event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_BACK) {
                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Confirmar");
                    dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.4?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                        }
                    });
                    dialogo1.show();

                    return true;

                }

                return false;
            }
        });
    }
    public interface OnFragmentInteractionListener {
        void onFragmentInteraction(Uri uri);
    }

    private void variables(){
        btnContinuar = (Button) rootView.findViewById(R.id.afe1_btn_continuar);
        btnCancelar = (Button) rootView.findViewById(R.id.afe1_btn_cancelar);
        cb1si = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta1_si);
        cb1no = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta1_no);
        cb2si = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta2_si);
        cb2no = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta2_no);
        cb3si = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta3_si);
        cb3no = (CheckBox) rootView.findViewById(R.id.afe1_cb_pregunta3_no);
        etObservaciones = (EditText) rootView.findViewById(R.id.afe1_et_observaciones);
        connected = new Connected();
    }

    /**
     *  metodo para callback de volley
     */
    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {
                loading.dismiss();
                primerPaso(response);
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {
                if(connected.estaConectado(getContext())){
                    Dialogos.dialogoErrorServicio(getContext());
                }else{
                    Dialogos.dialogoErrorConexion(getContext());
                }
            }
        };
    }

    /**
     * Método para generar el proceso REST
     * @param primerPeticion identifica si el metodo será procesado, debe llegar en true
     */
    private void sendJson(final boolean primerPeticion, boolean opc1, boolean opc2, boolean opc3, String observaciones) {
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Cargando datos", "Por favor espere un momento...", false, false);
        else
            loading = null;
        idTramite = getArguments().getString("idTramite");
        JSONObject obj = new JSONObject();
        try{
            JSONObject rqt = new JSONObject();
            JSONObject encuesta = new JSONObject();
            encuesta.put("pregunta1", opc1);
            encuesta.put("pregunta2", opc2);
            encuesta.put("pregunta3", opc3);
            rqt.put("encuesta", encuesta);
            rqt.put("observaciones", observaciones);
            rqt.put("estatusTramite", 1134);
            rqt.put("idTramite", Integer.parseInt(idTramite));
            obj.put("rqt", rqt);
            Log.d(TAG, "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        volleySingleton.postDataVolley("primerPaso", Config.URL_ENVIAR_ENCUESTA, obj);
    }

    private void primerPaso(JSONObject obj){
        Log.d(TAG, "RESPONSE: ->" + obj);
    }
}
