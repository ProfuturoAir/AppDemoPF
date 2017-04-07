package com.airmovil.profuturo.ti.retencion.gerenteFragmento;

import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
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
import android.widget.ImageView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ConCita;
import com.airmovil.profuturo.ti.retencion.helper.Config;

import java.io.File;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link Escaner.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link Escaner#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Escaner extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private Button btnCaptura;
    private View rootView;
    int PHOTO_FILE = 0;
    private Button scanButton;
    private Button cameraButton;
    private Button btnGuardar;
    private Button btnCancelar;
    private Button btnBorrar;
    private Button mediaButton;
    private ImageView scannedImageView;
    private String msjConexion;
    private File mCurrentPhoto;
    private ImageView imageView;

    private OnFragmentInteractionListener mListener;

    public Escaner() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment Escaner.
     */
    // TODO: Rename and change types and number of parameters
    public static Escaner newInstance(String param1, String param2) {
        Escaner fragment = new Escaner();
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
        Button btn = (Button) rootView.findViewById(R.id.gfe_btn_documento);
        btnGuardar = (Button) view.findViewById(R.id.gfe_btn_guardar);
        btnCancelar= (Button) view.findViewById(R.id.gfe_btn_cancelar);
        btnBorrar= (Button) view.findViewById(R.id.gfe_btn_borrar);
        imageView = (ImageView) rootView.findViewById(R.id.scannedImage1);

        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Bundle bundle = new Bundle ();
                Intent launchIntent = new Intent ();
                //Valores por default para el motor
                launchIntent.setComponent(new ComponentName("mx.com.profuturo.motor", "mx.com.profuturo.motor.CameraUI"));
                // nombreImagen es el nombre con el que se debe nombrar la imagen resultante del motor de imagen sin extensión
                // por ejemplo selfie

                String nombreImagen = "test2";
                bundle.putString ("nombreDocumento", nombreImagen);
                // ruta destino dentro de las carpetas de motor de imágenes en donde se almacenará el documento
                // idtramite en este caso sebe ser sustituido por el idTramite que se obtienen el servicio consultarDatosCliente
                // /mb/premium/rest/consultarDatosCliente
                bundle.putString("rutaDestino", "idtramite/");
                // Indicador de que se debe lanzar la cámara
                bundle.putBoolean("esCamara", true);
                launchIntent.putExtras(bundle);
                startActivityForResult (launchIntent, PHOTO_FILE);
                Log.d("PHOTO_FILE", "" + PHOTO_FILE);
            }
        });

        btnGuardar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(imageView.getDrawable() == null){
                    Config.msj(getContext(), "Error", "Na existe un documento, favor de tomar captura");
                }else {

                    AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                    dialogo1.setTitle("Finalizando");
                    dialogo1.setMessage("Se finalizara el proceso de implicaciones");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {

                            Fragment fragmentoGenerico = null;
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            final Fragment borrarFragmento;
                            fragmentoGenerico = new ConCita();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                        .replace(R.id.content_gerente, fragmentoGenerico)
                                        .addToBackStack("F_MAIN")
                                        .commit();
                            }
                        }
                    });
                    dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                        }
                    });
                    dialogo1.show();
                }
            }
        });


        final Fragment borrar = this;
        btnCancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getContext());
                dialogo1.setTitle("Importante");
                dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.8");
                dialogo1.setCancelable(false);
                dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        Fragment fragmentoGenerico = null;
                        FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                        fragmentoGenerico = new SinCita();
                        if (fragmentoGenerico != null){
                            fragmentManager
                                    .beginTransaction()//.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                    .replace(R.id.content_gerente, fragmentoGenerico)
                                    .addToBackStack("F_MAIN")
                                    .commit();
                        }
                    }
                });
                dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener(){
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                    }
                });
                dialogo1.show();



            }
        });

        btnBorrar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                imageView.setImageResource(0);
            }
        });
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.gerente_fragmento_escaner, container, false);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        //super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PHOTO_FILE) {
            if (data != null) {
                try {
                    String nombre = data.getStringExtra("rutaImagen");
                    System.out.print(data.toString());
                    Log.d("data -->",  data.toString());
                    Log.d("test", nombre);
                    // Se obtiene la ruta de la imagen con extensión .jpg incluida
                    String nombre1 = data.getStringExtra("rutaImagen");
                    // En nuesto caso se utiliza la libreria Picasso para mostrar la imagen
                    //Picasso.with(getActivity()).invalidate(new File(nombre));
                    // Picasso.with(getActivity()).load(new File(nombre)).into(img_preview);
                    File imgFile = new  File(nombre);

                    if(imgFile.exists()){
                        Bitmap myBitmap = BitmapFactory.decodeFile(imgFile.getAbsolutePath());
                        imageView.setImageBitmap(myBitmap);
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        super.onActivityResult(requestCode, resultCode, data);
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
                    dialogo1.setMessage("¿Estàs seguro que deseas cancelar y guardar los cambios del proceso 1.1.3.8?");
                    dialogo1.setCancelable(false);
                    dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            Fragment fragmentoGenerico = new SinCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).commit();
                            }
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
