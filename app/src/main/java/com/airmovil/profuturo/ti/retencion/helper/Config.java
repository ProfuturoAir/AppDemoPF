package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.Base64;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.Timer;
import java.util.TimerTask;

import com.airmovil.profuturo.ti.retencion.R;
import com.android.volley.RequestQueue;

import org.json.JSONObject;

/**
 * Created by tecnicoairmovil on 13/03/17.
 */

public class Config extends Activity {
    // TODO: URL direccion hacia la web de profuturo para consumir la calculadora de retencion
    public static final String URL_CALCULA_RETIRO_AFORE = "https://asesorprofuturo.mx/content/wps/portal/Calcula-tu-pension-de-retiro";
    // TODO: URL general para el consumo de servicios IP
    public static final String URL_GENERAL = "http://52.38.211.22:90/";
    // TODO: URL webservice para consumir el servio de autentificacion
    public static final String URL_AUTENTIFICACION = URL_GENERAL + "mb/cusp/rest/autenticacionUsuario";
    // TODO: URL webservice para el consumo de informacion de usuario
    public static final String URL_CONSULTA_INFORMACION_USUARIO = URL_GENERAL + "mb/cusp/rest/consultaInformacionUsuario";
    // TODO: URL webservice para el consumo de clientes con cita
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
    // TODO: LISTO DIRECTOR
    public static final String URL_CONSULTAR_REPORTE_RETENCION_GERENCIAS = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesGerencia";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_SUCURSALES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesSucursal";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_ASESORES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesAsesor";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_CLIENTES = URL_GENERAL + "mb/premium/rest/consultarReporteRetencionesCliente";
    public static final String URL_CONSULTAR_REPORTE_RETENCION_CLIENTE_DETALLE = URL_GENERAL + "mb/premium/rest/generarReporteCliente";
    public static final String URL_CONSULTAR_REPORTE_ASISTENCIA = URL_GENERAL + "mb/premium/rest/consultarReporteAsistencia";
    public static final String URL_CONSULTAR_REPORTE_ASISTENCIA_DETALLE = URL_GENERAL + "mb/premium/rest/consultarReporteProductividadAsistencia";
    // TODO: Enviar por email
    public static final String URL_SEND_MAIL_REPORTE_GERENCIA = URL_GENERAL + "mb/premium/rest/enviarEmailReporteGerencia";
    public static final String URL_SEND_MAIL_REPORTE_ASESOR = URL_GENERAL + "mb/premium/rest/enviarEmailReporteAsesor";
    public static final String URL_SEND_MAIL_REPORTE_ASISTENCIA = URL_GENERAL + "mb/premium/rest/enviarEmailReporteAsistencia";
    public static final String URL_SEND_MAIL_REPORTE_CLIENTE = URL_GENERAL + "mb/premium/rest/enviarEmailReporteCliente";
    public static final String URL_SEND_MAIL_REPORTE_SUCURSAL = URL_GENERAL + "mb/premium/rest/enviarEmailReporteSucursal";
    public static final String URL_SEND_MAIL_REPORTE_ASISTENCIA_DETALLE = URL_GENERAL + "mb/premium/rest/enviarEmailReporteAsistenciaDetalle";
    // TODO: SUCURSALES LISTA
    public static final String URL_SUCURSALES = URL_GENERAL + "mb/premium/rest/seleccionSucursales";
    public static final String URL_GERENCIAS = URL_GENERAL + "mb/premium/rest/seleccionGerencias";
    // TODO: definicion de campos fijos para el envio de datos de encuestas
    public static final String[] GERENCIAS = new String[]{};
    public static final String[] SUCURSALES = new String[]{};

    public static final String[] MOTIVOS = new String[]{"Selecciona el motivo", "Por mal servicio","Por falta de seguimiento ventas", "Promesas incumplidas", "Rendimiento", "Llevarse sus cuentas a la misma institución", "No da explicación", "Familiares o amigos en afore de la competencia"};
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
    public static final String USERNAME = "profuturo"; // USUARIO, para el acceso al basic authentication
    public static final String PASSWORD = "123123"; // Contraseña, para el acceso al basic authentication
    // TODO: Variables long fijas aplicadas en la actividad splash y el el handler de la primera peticion REST
    public static final long SPLASH_SCREEN_DELEY = 3500;
    public static final long TIME_HANDLER = 3000;
    // TODO: Creacion de una nueva instancia a usar para el timer, aplicado en la actividad splash
    private static Timer mTimer = new Timer();
    private static ProgressDialog dialog;
    public static final int numVistaPagina = 10;

    // TODO: formato para convertir (int) a valor monetario
    public static final NumberFormat nf = NumberFormat.getCurrencyInstance(Locale.getDefault()); // formato para convertir variables int a formato pesos
    public static final DecimalFormat df = new DecimalFormat("0.00"); // formato para implimer solo dos decimales en variable double de mas de 3 decimales
    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    public static Context context; // Context
    public static String idUsuario = "";

    public static RequestQueue mRequestQueue = null;


    /**
     * @param ctx parametro 1 referencia de la llamada, fragmento o actividad
     * @param titulo parametro 2 titulo del dialogo
     * @param msj parametro 3 mensaje del dialogo
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


    /**
     * Muestra mensaje de fechas vacias
     * @param context referencia de la llamada, fragmento o actividad
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
     * Muestra mensaje de fechas datos vacios
     * @param context referencia de la llamada, fragmento o actividad
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
     * Muestra mensaje de datos vacios
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoDatosVacios1(Context context, String mensaje){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_peligro));
        progressDialog.setTitle(context.getResources().getString(R.string.error_datos_vacios));
        progressDialog.setMessage(mensaje);
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
     * Muestra mensaje de error, en el documento no existente (INE o IFE)
     * @param context referencia de la llamada, fragmento o actividad
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
     * @param context referencia de la llamada, fragmento o actividad
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
     * Muestra mensaje, muestra mensaje de uso de menu
     * @param context referencia de la llamada, fragmento o actividad
     */
    public static final void dialogoMenu(Context context){
        final ProgressDialog progressDialog = new ProgressDialog(context, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(context.getResources().getDrawable(R.drawable.icono_menu));
        progressDialog.setTitle(context.getResources().getString(R.string.msj_titulo_menu));
        progressDialog.setMessage(context.getResources().getString(R.string.msj_contenido_menu));
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
     * @param context referencia de la llamada, fragmento o actividad
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
     * Muestra mensaje de error, seleccion de un apartado de spinner
     * @param context referencia de la llamada, fragmento o actividad
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
     * @param context referencia de la llamada, fragmento o actividad
     * @param title titulo del dialogo
     * @param message mensaje del dialogo
     * @param time tiempo de disminucion del dialogo
     * @return el mensja e de dialogo
     */
    public static ProgressDialog msjTime(Context context, CharSequence title, CharSequence message, int time) {
        MyTask task = new MyTask();
        mTimer.schedule(task, 0, time);
        dialog = ProgressDialog.show(context, title, message);
        return dialog;
    }

    /**
     * Clase statica para extender el timerTask y usar lo despues del tiempo apliacado,
     * se usa en la actividad splash
     */
    static class MyTask extends TimerTask {
        public void run() {
            if (dialog != null) {
                dialog.cancel();
            }
        }
    }

    /**
     * se crea un Map de fechas: fecha inicial y fecha final
     * @param diasAdicionales verificacion de la peticion inicial o final, de fechas
     * @return Map con las fechas
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

    /**
     * @param context
     * @return map retorna las variables del usuairo logeado
     */
    public static final Map<String, String> usuario(Context context){
        MySharePreferences sessionManager = MySharePreferences.getInstance(context.getApplicationContext());
        HashMap<String, String> datos = sessionManager.getUserDetails();
        datos.get(MySharePreferences.NOMBRE);
        datos.get(MySharePreferences.ID);
        return datos;
    }

    /**
     * Consulta la informacion existente en el sharePreference
     * @param context
     * @return datos del usuario a iniciar sesion
     */
    public static final Map<String, String> datosUsuario(Context context){
        MySharePreferences sessionManager = MySharePreferences.getInstance(context.getApplicationContext());
        HashMap<String, String> informacion = sessionManager.getUserDetails();
        informacion.get(MySharePreferences.APELLIDO_MATERNO);
        informacion.get(MySharePreferences.APELLIDO_PATERNO);
        informacion.get(MySharePreferences.NOMBRE);
        informacion.get(MySharePreferences.PERFIL);
        informacion.get(MySharePreferences.CENTRO_COSTO);
        informacion.get(MySharePreferences.CLAVE_CONSAR);
        informacion.get(MySharePreferences.CURP);
        informacion.get(MySharePreferences.EMAIL);
        informacion.get(MySharePreferences.NUMERO_EMPLEADO);
        informacion.get(MySharePreferences.USER_ID);
        return informacion;
    }

    /**
     * @param context
     * @return regresa un Map para usuarlo en el metodo de peticion requerido, REST de JsonObjectRequest
     */
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
     * @return numero de lista de la vita del listado
     */
    public static final int pidePagina(List list){
        return (int) Math.ceil((list.size() / Config.numVistaPagina) + 1);
    }

    /**
     * Estable la minimizacion de teclado, dependiendo del elemento editText a disminuir
     * @param context
     * @param editText
     */
    public static final void teclado(Context context, EditText editText){
        InputMethodManager imm = (InputMethodManager)context.getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
    }

    /**
     * @param context
     * @return el usuario de inicio de sesion, ya que se hace uso de este usuario en distintas peticiones
     */
    public static final String usuarioCusp(Context context){
        MySharePreferences sessionManager = MySharePreferences.getInstance(context);
        HashMap<String, String> usuario = sessionManager.getUserDetails();
        String cusp = usuario.get(MySharePreferences.CUSP);
        return cusp;
    }

    public static JSONObject filtroClientes (int tipoBuscar, String numeroEmpleado){
        JSONObject filtroCliente = new JSONObject();
        List<String> filtros = Arrays.asList("numeroCuenta","nss","curp");
        String valorEmpleado = "";
        try{
            for (int i = 0; i < filtros.size(); i++){
                valorEmpleado = (i+1 == tipoBuscar) ? numeroEmpleado : "";
                filtroCliente.put(filtros.get(i), valorEmpleado);
            }
        }catch (Exception e){

        }
        return filtroCliente;
    }
}
