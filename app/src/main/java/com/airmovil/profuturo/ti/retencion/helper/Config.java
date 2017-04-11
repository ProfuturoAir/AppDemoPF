package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

/**
 * Created by tecnicoairmovil on 13/03/17.
 */

public class Config extends Activity {
    // TODO: URL's
    public static final String URL_CALCULA_RETIRO_AFORE = "https://asesorprofuturo.mx/content/wps/portal/Calcula-tu-pension-de-retiro";

    public static final String URL_GENERAL = "http://52.38.211.22:90/";
    public static final String URL_AUTENTIFICACION = URL_GENERAL + "Profuturo/autenticacionUsuario1.php";
    public static final String URL_CONSULTAR_RESUMEN_RETENCIONES = URL_GENERAL + "mb/premium/rest/consultarResumenRetenciones.php";
    public static final String URL_CONSULTAR_RESUMEN_CITAS = URL_GENERAL + "Profuturo/consultarResumenCitas.php";
    public static final String URL_CONSULTAR_CLIENTE_SIN_CITA = URL_GENERAL + "Profuturo/consultarClienteSinCita.php";
    public static final String URL_CONSULTAR_DATOS_ASESOR = URL_GENERAL + "Profuturo/consultarDatosAsesor.php";
    public static final String URL_CUNSULTAR_DATOS_CLIENTE = URL_GENERAL + "Profuturo/consultarDatosCliente.php";
    public static final String URL_GENERAR_REPORTE_CLIENTE = URL_GENERAL + "Profuturo/generarReporteCliente.php";

    public static final String URL_ENVIAR_ENCUESTA = URL_GENERAL + "Profuturo/enviarEncuesta.php";
    public static final String URL_ENVIAR_ENCUESTA_2 = URL_GENERAL + "Profuturo/enviarEncuestaObservacion.php";
    public static final String URL_ENVIAR_FIRMA = URL_GENERAL + "Profuturo/guardarFirmaCliente.php";
    public static final String URL_ENVIAR_DOCUMENTO_IFE_INE = URL_GENERAL + "Profuturo/guardarDocumentacionCliente.php";

    // TODO: LISTO DIRECTOR
    public static final String URL_CONSULTAR_REPORTE_RETENCION_GERENCIAS = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesGerencia.php";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_SUCURSALES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesSucursal.php";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_ASESORES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesAsesor.php";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_CLIENTES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesCliente.php";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_CLIENTE_DETALLE = URL_GENERAL;
    public static final String URL_CONSULTAR_REPORTE_ASISTENCIA = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesCliente.php";
    public static final String URL_CONSULTAR_REPORTE_ASISTENCIA_DETALLE = URL_GENERAL + "Profuturo/consultarReporteProductividadAsistencia.php";

    // TODO: String
    public static final String[] GERENCIAS = new String[]{"Selecciona una Gerencia","Gerencia 1", "Gerencia 2", "Gerencia 3", "Gerencia 4"};
    public static final String[] SUCURSALES = new String[]{"Selecciona una sucursal","Sucursal 1", "Sucursal 2", "Sucursal 3", "Sucursal 4", "Sucursal 5"};
    public static final String[] AFORES = new String[]{"Selecciona una AFORE","Azteca", "Banamex", "Coppel", "Inbursa", "Invercap", "Metlife", "PensionISSSTE", "Principal", "Profuturo", "SURA", "XXI-Banorte"};
    public static final String[] MOTIVOS = new String[]{"Selecciona un motivo", "Motivo 1", "Motivo 2", "Motivo 3", "Motivo 4", "Motivo 5", "Motivo 5", "Motivo 6", "Motivo 7", "Motivo 8", "Motivo 9"};
    public static final String[] ESTATUS = new String[]{"Selecciona un estatus", "Activo", "Inactivo"};
    public static final String[] INSTITUCIONES = new String[]{"Selecciona una Institución","IMSS", "ISSSTE", "MIXTO"};
    public static final String[] REGIMEN = new String[]{"Selecciona un regimen","IMSS Ley 73", "IMSS Ley 97", "ISSSTE"};
    public static final String[] DOCUMENTOS = new String[]{"Selecciona el tipo de ducumentación","Estatus de cuenta con folio", "Constancia de implicaciones", "Estatus de cuenta con folio y Constancia de implicaciones", "Ningun documento"};
    public static final String[] EMITIDOS = new String[]{"Selecciona el tipo estatus de emitidos", "Emitidos", "No emitidos"};
    public static final String[] IDS = new String[]{"Selecciona el tipo de ID a buscar","Número de cuenta", "NSS", "CURP"};
    public static final String[] RETENIDO = new String[]{"Selecciona el tipo de estatus de retenidos ", "Retenido", "No Retenido"};
    public static final String[] CITAS = new String[]{"Seleciona el tipo de estatus de citas", "Con Cita", "Sin Cita"};
    public static final String[] EMAIL = new String[]{"Seleciona un email","profuturo.com.mx", "profuturo.com", "profuturo.mx"};

    // TODO: Variables
    public static final String USERNAME = "profuturo";
    public static final String PASSWORD = "123123";
    public static final long SPLASH_SCREEN_DELEY = 3500;
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    public static final int numVistaPagina = 10;

    /**
     * @param ctx parametro 1,
     * @param titulo parametro 2,
     * @param msj parametro 3
     * @return Dialogo de mensaje
     */
    public static final void msj(Context ctx, String titulo, String msj){
        AlertDialog.Builder dlgAlert  = new AlertDialog.Builder(ctx);
        dlgAlert.setTitle(titulo);
        dlgAlert.setMessage(msj);
        dlgAlert.setCancelable(true);
        dlgAlert.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dlgAlert.create().show();
    }

    public static ProgressDialog msjTime(Context context, CharSequence title, CharSequence message, int time) {
        MyTask task = new MyTask();
        // Run task after 10 seconds
        mTimer.schedule(task, 0, time);
        dialog = ProgressDialog.show(context, title, message);
        return dialog;
    }

    static class MyTask extends TimerTask {

        public void run() {
            // Do what you wish here with the dialog
            if (dialog != null)
            {
                dialog.cancel();
            }
        }
    }

    /**
     *
     * @param diasAdicionales
     * @return Map
     */
    public static final Map<String, String> fechas(int diasAdicionales){

        Map<String, Integer> datos = Config.dias();
        int mDay = datos.get("dia");
        int mMonth = datos.get("mes");
        int mYear = datos.get("anio");

        String fechaFin = mDay + "-" + (mMonth + 1) + "-" + mYear;
        SimpleDateFormat sdf = new SimpleDateFormat("dd-MM-yyyy");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(sdf.parse(fechaFin));
            c.add(Calendar.DATE, diasAdicionales);
            fechaFin = sdf.format(c.getTime());
        } catch (ParseException e) {
            e.printStackTrace();
        }

        String fechaIni = mDay + "-" + (mMonth + 1) + "-" + mYear;

        Map<String, String> resultado = new HashMap<>();
        resultado.put("fechaIni", fechaIni);
        resultado.put("fechaFin", fechaFin);
        return resultado;
    }

    /**
     * @return Map cadena de dias para los calendarios
     */
    public static final Map<String, Integer> dias(){
        Calendar c = Calendar.getInstance();
        Map<String, Integer> datos = new HashMap<>();
        datos.put("dia", c.get(Calendar.DAY_OF_MONTH));
        datos.put("mes", c.get(Calendar.MONTH));
        datos.put("anio", c.get(Calendar.YEAR));
        return datos;
    }

    public static final Map<String, String> usuario(Context context){
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        HashMap<String, String> datos = sessionManager.getUserDetails();
        datos.get(SessionManager.NOMBRE);
        datos.get(SessionManager.ID);
        return datos;
    }

    /**
     * @param numero
     * @return el numero entre el que se generara el listado
     */
    public static final int maximoPaginas(int numero){
        return (int) Math.ceil((numero / Config.numVistaPagina));
    }

    /**
     * @param list
     * @return
     */
    public static final int pidePagina(List list){
        return (int) Math.ceil((list.size() / Config.numVistaPagina) + 1);
    }

}
