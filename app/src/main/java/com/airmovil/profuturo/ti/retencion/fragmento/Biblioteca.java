package com.airmovil.profuturo.ti.retencion.fragmento;

import android.content.IntentSender;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GoogleApiAvailability;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.drive.Drive;
import com.google.android.gms.drive.DriveApi;
import com.google.android.gms.drive.DriveFile;
import com.google.android.gms.drive.DriveId;

import com.airmovil.profuturo.ti.retencion.R;

public class Biblioteca extends Fragment implements GoogleApiClient.ConnectionCallbacks,GoogleApiClient.OnConnectionFailedListener {
    private static final String TAG = "Google Drive Activity";
    private static final int REQUEST_CODE_RESOLUTION = 1;
    private static final  int REQUEST_CODE_OPENER = 2;
    private GoogleApiClient mGoogleApiClient;
    private boolean fileOperation = false;
    private DriveId mFileId;
    public DriveFile file;
    private Connected connected;

    /**
     * El sistema lo llama cuando crea el fragmento
     * @param savedInstanceState, llama las variables en el bundle
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setRetainInstance(true);
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        super.onSaveInstanceState(savedInstanceState);
        setRetainInstance(true);
    }

    /**
     * Se lo llama para crear la jerarquía de vistas asociada con el fragmento.
     * @param inflater inflacion del xml
     * @param container contenedor del ml
     * @param savedInstanceState datos guardados
     * @return el fragmento declarado dentro de la actividad
     */
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragmento_biblioteca, container, false);
        ImageButton openFile = (ImageButton) view.findViewById(R.id.openfile);
        openFile.setOnClickListener(abrirDrive);
        return view;
    }

    View.OnClickListener abrirDrive = new View.OnClickListener() {
        public void onClick(View v) {
            onClickOpenFile(v);
        }
    };

    @Override
    public void onResume() {
        super.onResume();
        if (mGoogleApiClient == null) {
            /**
             * Cree el cliente de API y vincularlo a una variable de instancia.
             * Utilizamos esta instancia como la devolución de llamada para errores de conexión y conexión.
             * Puesto que no se pasa ningún nombre de cuenta, se le pide al usuario que elija.
             */
            mGoogleApiClient = new GoogleApiClient.Builder(getContext())
                    .addApi(Drive.API)
                    .addScope(Drive.SCOPE_FILE)
                    .addConnectionCallbacks(this)
                    .addOnConnectionFailedListener(this)
                    .build();
        }
        mGoogleApiClient.connect();
    }

    /**
     * Detenine la conexion de googleAPI drive
     */
    @Override
    public void onStop() {
        super.onStop();
        if (mGoogleApiClient != null) {
            // desconecta google API de la conexion del clieten
            mGoogleApiClient.disconnect();
        }
        super.onPause();
    }

    /**
     * Conexion falla
     * @param result valor del estado de la conexion
     */
    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!result.hasResolution()) {
            // muestra el diálogo de localizado.
            GoogleApiAvailability.getInstance().getErrorDialog(getActivity(), result.getErrorCode(), 0).show();
            return;
        }

        /**
         *  El fallo tiene una resolución. Resuelvelo.
         * Se llama normalmente cuando la aplicación aún no está autorizada y una autorización
         *  Se muestra al usuario.
         */
        try {
            result.startResolutionForResult(getActivity(), REQUEST_CODE_RESOLUTION);
        } catch (IntentSender.SendIntentException e) {
            Log.e(TAG, "Exception while starting resolution activity", e);
        }
    }

    /**
     * Se invoca cuando la API de google cliente, esta conectada
     * @param connectionHint
     */
    @Override
    public void onConnected(Bundle connectionHint) {
        Config.msjTime(getContext(), "Conectando...", "Conexión establecida con google drive", 2000);
    }

    /**
     * Se invoca cuando la conexion ha sido suspendida
     * @param cause
     */
    @Override
    public void onConnectionSuspended(int cause) {
        Config.msjTime(getContext(), "Error", "Conexiòn suspendida con google drive", 3000);
    }

    /**
     * Inicia la vista con google drive
     * @param view
     */
    public void onClickOpenFile(View view){
        fileOperation = false;
        Drive.DriveApi.newDriveContents(mGoogleApiClient).setResultCallback(driveContentsCallback);
    }

    /**
     *  Abrir la lista de carpetas y archivos de Google Drive
     */
    public void OpenFileFromGoogleDrive(){
            IntentSender intentSender = Drive.DriveApi.newOpenFileActivityBuilder().build(mGoogleApiClient);
            try {
                getActivity().startIntentSenderForResult(intentSender, REQUEST_CODE_OPENER, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
            }

    }

    /**
     * Este es el manejador de resultados del contenido del Drive.
     * Este método callback llama al método CreateFileOnGoogleDrive ()
     * Y también llamado OpenFileFromGoogleDrive () método,
     * Enviar método onActivityResult () para manejar el resultado.
     */
    final ResultCallback<DriveApi.DriveContentsResult> driveContentsCallback =
            new ResultCallback<DriveApi.DriveContentsResult>() {
                @Override
                public void onResult(DriveApi.DriveContentsResult result) {

                    if (result.getStatus().isSuccess()) {

                        if (fileOperation == true) {

                        } else {

                            OpenFileFromGoogleDrive();

                        }
                    }
                }
            };



}
