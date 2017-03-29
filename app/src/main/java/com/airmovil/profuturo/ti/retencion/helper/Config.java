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
    public static final String URL_CONSULTAR_RESUMEN_RETENCIONES = URL_GENERAL + "Profuturo/consultarResumenRetenciones.php";
    public static final String URL_CONSULTAR_RESUMEN_CITAS = URL_GENERAL + "Profuturo/consultarResumenCitas.php";
    public static final String URL_CONSULTAR_CLIENTE_SIN_CITA = URL_GENERAL + "Profuturo/consultarClienteSinCita.php";
    public static final String URL_CONSULTAR_DATOS_ASESOR = URL_GENERAL + "Profuturo/consultarDatosAsesor.php";
    public static final String URL_CUNSULTAR_DATOS_CLIENTE = URL_GENERAL + "Profuturo/consultarDatosCliente.php";

    // TODO: Variables
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
