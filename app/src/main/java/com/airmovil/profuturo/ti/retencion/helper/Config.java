package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

import java.net.ConnectException;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.TimeZone;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.directorFragmento.Inicio;

/**
 * Created by tecnicoairmovil on 13/03/17.
 */

public class Config extends Activity {
    // TODO: URL's
    public static final String URL_CALCULA_RETIRO_AFORE = "https://asesorprofuturo.mx/content/wps/portal/Calcula-tu-pension-de-retiro";

    public static final String URL_GENERAL = "http://52.38.211.22:90/";
    public static final String URL_AUTENTIFICACION = URL_GENERAL + "mb/cusp/rest/autenticacionUsuario";
    public static final String URL_CONSULTA_INFORMACION_USUARIO = URL_GENERAL + "mb/cusp/rest/consultaInformacionUsuario";
    public static final String URL_CONSULTAR_RESUMEN_RETENCIONES = URL_GENERAL + "mb/premium/rest/consultarResumenRetenciones";
    public static final String URL_CONSULTAR_RESUMEN_CITAS = URL_GENERAL + "mb/premium/rest/consultarResumenCitas";
    public static final String URL_CONSULTAR_CLIENTE_SIN_CITA = URL_GENERAL + "mb/premium/rest/consultarClienteSinCita";
    public static final String URL_CONSULTAR_DATOS_ASESOR = URL_GENERAL + "mb/premium/rest/consultarDatosAsesor";
    public static final String URL_CUNSULTAR_DATOS_CLIENTE = URL_GENERAL + "mb/premium/rest/consultarDatosCliente";
    public static final String URL_GENERAR_REPORTE_CLIENTE = URL_GENERAL + "mb/premium/rest/generarReporteAsesor";

    public static final String URL_GENERAL_REPORTE_CLIENTE = URL_GENERAL + "mb/premium/rest/generarReporteCliente";

    public static final String URL_ENVIAR_ENCUESTA = URL_GENERAL + "mb/premium/rest/enviarEncuesta";
    public static final String URL_ENVIAR_ENCUESTA_2 = URL_GENERAL + "mb/premium/rest/enviarEncuestaObservacion";
    public static final String URL_ENVIAR_FIRMA = URL_GENERAL + "mb/premium/rest/guardarFirmaCliente";
    public static final String URL_ENVIAR_DOCUMENTO_IFE_INE = URL_GENERAL + "mb/premium/rest/guardarDocumentacionCliente";

    public static final String URL_REGISTRAR_ASISTENCIA = URL_GENERAL + "mb/premium/rest/registrarAsistencia";

    public static final String URL_SEND_MAIL_REPORTE_GERENCIA = URL_GENERAL + "/mb/premium/rest/enviarEmailReporteGerencia";

    public static final String URL_SEND_MAIL_REPORTE_ASESOR = URL_GENERAL + "/mb/premium/rest/enviarEmailReporteAsesor";
    public static final String URL_SEND_MAIL_REPORTE_ASISTENCIA = URL_GENERAL + "/mb/premium/rest/enviarEmailReporteAsistencia";
    public static final String URL_SEND_MAIL_REPORTE_CLIENTE = URL_GENERAL + "/mb/premium/rest/enviarEmailReporteCliente";
    public static final String URL_SEND_MAIL_REPORTE_SUCURSAL = URL_GENERAL + "/mb/premium/rest/enviarEmailReporteSucursal";


    // TODO: LISTO DIRECTOR
    public static final String URL_CONSULTAR_REPORTE_RETENCION_GERENCIAS = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesGerencia";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_SUCURSALES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesSucursal";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_ASESORES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesAsesor";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_CLIENTES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesCliente";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_CLIENTE_DETALLE = URL_GENERAL + "mb/premium/rest/generarReporteCliente";
    public static final String URL_CONSULTAR_REPORTE_ASISTENCIA = URL_GENERAL + "mb/premium/rest/consultarReporteAsistencia";
    public static final String URL_CONSULTAR_REPORTE_ASISTENCIA_DETALLE = URL_GENERAL + "mb/premium/rest/consultarReporteProductividadAsistencia";

    //SUCURSALES LISTA
    public static final String URL_SUCURSALES = URL_GENERAL + "mb/premium/rest/seleccionSucursales";
    public static final String URL_GERENCIAS = URL_GENERAL + "mb/premium/rest/seleccionGerencias";

    // TODO: String
    public static final String[] GERENCIAS = new String[]{"Selecciona una Gerencia","Gerencia 1", "Gerencia 2", "Gerencia 3", "Gerencia 4"};
    public static final String[] SUCURSALES = new String[]{"Selecciona una sucursal","Sucursal 1", "Sucursal 2", "Sucursal 3", "Sucursal 4", "Sucursal 5"};
    public static final String[] MOTIVOS = new String[]{"Selecciona el motivo", "Por mal servicio","Por falta de seguimiento ventas", "Promesas incumplidas", "Rendimiento", "Llevarse sus cuentas a la misma institución", "no da explicación", "familiares o amigos en afore de la competencia"};
    public static final String[] AFORES = new String[]{"Selecciona una AFORE","Azteca", "Banamex", "Coppel", "Inbursa", "Invercap", "Metlife", "PensionISSSTE", "Principal", "Profuturo", "SURA", "XXI-Banorte"};
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
    public static final long TIME_HANDLER = 2000;
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    public static final int numVistaPagina = 10;

    // TODO: formato para convertir (int) a valor monetario
    public static final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault());
    public static final DecimalFormat df = new DecimalFormat("0.00");
    public static Context context;


    /**
     * @param ctx parametro 1,
     * @param titulo parametro 2,
     * @param msj parametro 3
     * @return Dialogo de mensaje
     */
    public static final void msj(Context ctx, String titulo, String msj){
        AlertDialog.Builder dialog  = new AlertDialog.Builder(ctx);
        dialog.setTitle(titulo);
        dialog.setMessage(msj);
        dialog.setCancelable(true);
        dialog.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialog.create().show();
    }

    public static final void msj1(Context ctx, String titulo, String msj){
        AlertDialog.Builder dialog  = new AlertDialog.Builder(ctx);
        dialog.setTitle(titulo);
        dialog.setMessage(msj);
        dialog.setCancelable(true);
        dialog.create().show();
    }

    public static final void msjDatosVacios(Context ctx){
        final ProgressDialog progressDialog = new ProgressDialog(ctx, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(ctx.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(ctx.getResources().getString(R.string.error_datos_vacios));
        progressDialog.setMessage(ctx.getResources().getString(R.string.msj_error_datos_vacios));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, "Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de fechas vacias
     * @param context
     */
    public static final void dialogoFechasVacias(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_fechas_vacias));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_fechas));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de fechas vacias
     * @param context
     */
    public static final void dialogoDatosVacios(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_datos_vacios));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_datos_vacios));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, en documento no existente (INE o IFE)
     * @param context
     */
    public static final void dialogoNoExisteUnDocumento(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_en_documento));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_error_documento));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje, aviso de contenido limpio en firma
     * @param context
     */
    public static final void dialogoContenidoLimpio(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_limpio));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_limpiar));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_limpio));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, se requiere una firma
     * @param context
     */
    public static final void dialogoRequiereFirma(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_firma));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_firma));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_vacio_firma));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, no existen coordenadas
     * @param context
     */
    public static final void dialogoSinCoordenadas(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_coordenadas));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_sin_coordenadas));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_cotentido_sin_coordenadas));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, seleccion de un apartado de spinner
     * @param context
     */
    public static final void dialogoSinSeleccionSpinner(Context context, String apartado){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_lista));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_seleccion_spinner));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_spinner) + " " + apartado);
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
    }

    /**
     * Muestra mensaje de error, seleccion de un apartado de spinner
     * @param context
     */
    public static final void dialogoSpinnerSinSeleccion(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_lista));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_seleccion_spinner));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_sin_spinner));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, context.getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
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
            if (dialog != null) {
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
     * Consulta la informacion existente en el sharePreference
     * @param context
     * @return datos del usuario a iniciar sesion
     */

    public static final Map<String, String> datosUsuario(Context context){
        SessionManager sessionManager = new SessionManager(context.getApplicationContext());
        HashMap<String, String> informacion = sessionManager.getUserDetails();
        informacion.get(SessionManager.APELLIDO_MATERNO);
        informacion.get(SessionManager.APELLIDO_PATERNO);
        informacion.get(SessionManager.NOMBRE);
        informacion.get(SessionManager.PERFIL);
        informacion.get(SessionManager.CENTRO_COSTO);
        informacion.get(SessionManager.CLAVE_CONSAR);
        informacion.get(SessionManager.CURP);
        informacion.get(SessionManager.EMAIL);
        informacion.get(SessionManager.NUMERO_EMPLEADO);
        informacion.get(SessionManager.USER_ID);
        return informacion;
    }

    public static final Map<String, String> credenciales(Context context){
        HashMap<String, String> headers = new HashMap<>();
        headers.put("Content-Type", "application/json; charset=utf-8");
        String credentials = Config.USERNAME+":"+Config.PASSWORD;
        String auth = "Basic " + Base64.encodeToString(credentials.getBytes(), Base64.NO_WRAP);
        headers.put("Authorization", auth);
        return headers;
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

    public static final void teclado(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    public static final String usuarioCusp(Context context){
        SessionManager sessionManager = new SessionManager(context);
        HashMap<String, String> usuario = sessionManager.getUserDetails();
        String cusp = usuario.get(SessionManager.CUSP);
        return cusp;
    }

}
