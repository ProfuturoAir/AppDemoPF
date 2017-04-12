package com.airmovil.profuturo.ti.retencion.asesorFragmento;

import android.app.ProgressDialog;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AlertDialog;
import android.util.Base64;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaJSON;
import com.airmovil.profuturo.ti.retencion.helper.MySingleton;
import com.airmovil.profuturo.ti.retencion.helper.SQLiteHandler;
import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.HashMap;
import java.util.Map;

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
    private Button btnFinalizar;

    private Connected connected;
    private SQLiteHandler db;

    private OnFragmentInteractionListener mListener;

    String idTramite;

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
        db = new SQLiteHandler(getContext());
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;
        Button btn = (Button) rootView.findViewById(R.id.btn_documento);
        btnGuardar = (Button) view.findViewById(R.id.af_btn_guardar);
        btnCancelar= (Button) view.findViewById(R.id.af_btn_cancelar);
        btnFinalizar = (Button) view.findViewById(R.id.af_btn_guardar);

        btnBorrar= (Button) view.findViewById(R.id.af_btn_borrar);
        imageView = (ImageView) rootView.findViewById(R.id.scannedImage);

        connected = new Connected();
        idTramite = getArguments().getString("idTramite");

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
                        final Fragment borrarFragmento;
                        fragmentoGenerico = new ConCita();
                        if (fragmentoGenerico != null){
                            fragmentManager
                                    .beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                    .replace(R.id.content_asesor, fragmentoGenerico)
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

        final Fragment borrar = this;

        btnFinalizar.setOnClickListener(new View.OnClickListener() {
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

                            imageView.setDrawingCacheEnabled(true);
                            String base64 = encodeTobase64(imageView.getDrawingCache());
                            //Bitmap emBit = Bitmap.createBitmap(imageView.getWidth(), imageView.getHeight(), Bitmap.Config.ARGB_8888);
                            Log.d("BASE64-->", base64);
                            imageView.setDrawingCacheEnabled(false);

                            if(connected.estaConectado(getContext())) {
                                sendJson(true);
                                final EnviaJSON enviaPrevio = new EnviaJSON();
                                enviaPrevio.sendPrevios(idTramite, getContext());
                            }else {
                                db.addDocumento(idTramite,"2017-04-10",1,base64,"12344","Cesar",90.2349, -23.9897);
                                //db.addFirma("1", 123, base64, 90.2349, -23.9897);
                            }

                            /*Fragment fragmentoGenerico = null;
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            final Fragment borrarFragmento;
                            fragmentoGenerico = new ConCita();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                                        .replace(R.id.content_gerente, fragmentoGenerico)
                                        .addToBackStack("F_MAIN")
                                        .commit();
                            }*/
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).commit();
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
        return inflater.inflate(R.layout.asesor_fragmento_escaner, container, false);
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
                            Fragment fragmentoGenerico = new ConCita();
                            FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
                            if (fragmentoGenerico != null) {
                                fragmentManager
                                        .beginTransaction()//.setCustomAnimations(android.R.anim.slide_out_right,android.R.anim.slide_in_left)
                                        .replace(R.id.content_asesor, fragmentoGenerico).commit();
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

    // TODO: REST
    private void sendJson(final boolean primerPeticion) {
        final ProgressDialog loading;
        if (primerPeticion)
            loading = ProgressDialog.show(getActivity(), "Loading Data", "Please wait...", false, false);
        else
            loading = null;

        JSONObject obj = new JSONObject();

        // TODO: Formacion del JSON request
        try{
            JSONObject rqt = new JSONObject();
            rqt.put("estatusTramite", 123);
            rqt.put("fechaHoraFina", "2017-04-10 ");
            rqt.put("idTramite", 1);
            rqt.put("ineIfe", "12312312312331233");
            rqt.put("numeroCuenta", "123123");
            JSONObject ubicacion = new JSONObject();
            ubicacion.put("latitud", "90.2349");
            ubicacion.put("longitud", "-23.9897");
            rqt.put("ubicacion", ubicacion);
            obj.put("rqt", rqt);
            Log.d("datos", "REQUEST-->" + obj);
        } catch (JSONException e){
            Config.msj(getContext(), "Error", "Error al formar los datos");
        }
        //Creating a json array request
        JsonObjectRequest jsonArrayRequest = new JsonObjectRequest(Request.Method.POST, Config.URL_ENVIAR_ENCUESTA_2, obj,
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        //Dismissing progress dialog
                        if (primerPeticion) {
                            loading.dismiss();
                            //primerPaso(response);
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        loading.dismiss();
                        Config.msj(getContext(),"Error conexión", "Lo sentimos ocurrio un error, puedes intentar revisando tu conexión.");
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                HashMap<String, String> headers = new HashMap<>();
                headers.put("Content-Type", "application/json; charset=utf-8");
                String credentials = Config.USERNAME+":"+Config.PASSWORD;
                String auth = "Basic "
                        + Base64.encodeToString(credentials.getBytes(),
                        Base64.NO_WRAP);
                headers.put("Authorization", auth);

                return headers;
            }
        };
        MySingleton.getInstance(getActivity()).addToRequestQueue(jsonArrayRequest);
    }


    public String encodeTobase64(Bitmap image) {
        //Bitmap immagex = image;
        /*ByteArrayOutputStream baos = new ByteArrayOutputStream();
        immagex.compress(Bitmap.CompressFormat.JPEG, 100, baos);
        byte[] b = baos.toByteArray();
        String imageEncoded = android.util.Base64.encodeToString(b, android.util.Base64.DEFAULT);*/

        float bmW=image.getWidth();
        float bmH= image.getHeight();

        Log.d("PIXELES", "ORIGINAL ANCHO"+bmW+"Original ALTO:"+bmH );

        int widthPixels = getContext().getResources().getDisplayMetrics().widthPixels;

        Bitmap resize;
        Log.d("PIXELES", "TELEFONO"+widthPixels );
        if(bmW>=widthPixels){
            float newWidth=widthPixels;
            float newHeight=(bmH/bmW)*widthPixels;

            Log.d("PIXELES", "NUEVO ANCHO" + widthPixels + "NUEVO ALTO:" + newHeight + " W" + bmW + " H" + bmH);
            //resize the bit map
            resize = Bitmap.createBitmap(image,0,0,(int)newWidth,(int)newHeight);
            //resize =Bitmap.createScaledBitmap(image, 200,200, true);
        }else{
            Log.d("PIXELES", "PASA SIN CAMBIO" );
            resize = image;
        }

        //resize =Bitmap.createScaledBitmap(image, 500,500, true);
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        resize.compress(Bitmap.CompressFormat.PNG, 100, baos);
        byte[] b = baos.toByteArray();
        //String imageEncoded = Base64.encode(b);
        //String imageEncoded = Base64.encodeToString(byteArray, Base64.DEFAULT);

        //Log.e("LOOK", imageEncoded);
        String imageEncoded = Base64.encodeToString(b, Base64.DEFAULT);
        //Log.d("ARRAY","BASE64:"+encImage);
        imageEncoded = imageEncoded.replace(" ","");
        String foto="data:image/jpeg;base64,"+imageEncoded;

        int maxLogSize = 1000;
        for(int i = 0; i <= foto.length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > foto.length() ? foto.length() : end;
            Log.d("n-"+i, foto.substring(start, end));
        }
        return foto;
    }
}
