package com.airmovil.profuturo.ti.retencion.activities;

import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.view.ContextThemeWrapper;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.DatosCliente;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Encuesta1;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Encuesta2;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Escaner;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Firma;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ProcesoImplicacionesPendientes;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.SinCita;
import com.airmovil.profuturo.ti.retencion.fragmento.Biblioteca;
import com.airmovil.profuturo.ti.retencion.fragmento.Calculadora;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistencia;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.IResult;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.ServicioJSON;
import com.airmovil.profuturo.ti.retencion.helper.VolleySingleton;
import com.android.volley.VolleyError;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class Gerente extends AppCompatActivity{
    private static final String TAG = Gerente.class.getSimpleName();
    private MySharePreferences sessionManager;
    private VolleySingleton volleySingleton;
    private IResult mResultCallback = null;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Boolean checkMapsFragment = false;
    private Boolean checkProccess = false;
    private String global = "";
    private DriveId mFileId;
    private static final  int REQUEST_CODE_OPENER = 2;
    String url;

    public static Fragment itemMenu = null;
    /**
     * Se utiliza para iniciar la actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.gerente);

        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // TODO: nueva instancia para el sharePreference
        sessionManager = MySharePreferences.getInstance(getApplicationContext());
        // TODO: Validacion de la sesion del usuario
        validateSession();
        initVolleyCallback();
        // TODO: llama clase singleton volley
        volleySingleton = VolleySingleton.getInstance(mResultCallback, getApplicationContext());

        try {
            startService(new Intent(this, ServicioJSON.class));
        }catch (Exception e){}
        volleySingleton.getDataVolley("Gerencias", Config.URL_GERENCIAS);
        volleySingleton.getDataVolley("Sucursales", Config.URL_SUCURSALES);
    }

    /**
     *  metodo para callback de volley
     */
    void initVolleyCallback() {
        mResultCallback = new IResult() {
            @Override
            public void notifySuccess(String requestType, JSONObject response) {

                if(requestType.equals("Gerencias"))
                    try {
                        Gerencias(response.getJSONArray("Gerencias"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                if(requestType.equals("Sucursales"))
                    try {
                        Sucursales(response.getJSONArray("Sucursales"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
            }

            @Override
            public void notifyError(String requestType, VolleyError error) {}
        };
    }

    @Override
    public void onBackPressed() {
        final ProgressDialog progressDialog = new ProgressDialog(this, R.style.ThemeOverlay_AppCompat_Dialog_Alert);
        progressDialog.setIndeterminateDrawable(getResources().getDrawable(R.drawable.icono_menu));
        progressDialog.setTitle(getResources().getString(R.string.msj_titulo_menu));
        progressDialog.setMessage(getResources().getString(R.string.msj_contenido_menu));
        progressDialog.setButton(DialogInterface.BUTTON_POSITIVE, getResources().getString(R.string.aceptar),
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        progressDialog.dismiss();
                    }
                });
        progressDialog.show();
        return;
    }


    /**
     * Este metodo valida la sesion del usuario, manda a llamar a otros metodos
     * como(setToolbar, setDrawable, setToggle, setNavigationView, setInformacionDrawer),
     * para contruir la vista general, menu superior(Toolba), menu lateral(DrawableView)
     * toggle(Icono abrir menu)
     */
    public void validateSession(){
        if(sessionManager.isLoggedIn() == false){
            startActivity(new Intent(Gerente.this, Login.class));
            finish();
        }else{
            // TODO: implementando el tootlbar
            setToolbar();
            // TODO: implementando el drawerLayout
            setDrawerLayout();
            // TODO: implementando el toggle para el drawerLayout
            setToggle();
            // TODO: implementando la navegacion del nav_view
            setNavigationView();
            // TODO: mostrar informacion del usuairo en la navegacion
            setInformacionDrawer();
        }
    }

    /**
     * Este metodo coloca el menu superior
     * y se coloca la imagen de profuturo
     */
    public void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.gerente_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.img_icono_logo);
        getSupportActionBar().setTitle(" ");
    }

    /**
     * Se utiliza metodo Activa o desactiva la interacción con el contenido interior,
     * el navigationView y el contenedor
     */
    public void setDrawerLayout(){
        drawerLayout = (DrawerLayout) findViewById(R.id.gerente_drawer_layout);
        assert drawerLayout != null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Este metodo coloca dentro del toolbar un icono para activar y descativar
     * el uso del drawableView, recorrido de izquierda a derecha
     */
    public void setToggle(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Se utiliza este metodo para iniciar la navegacion entre los items
     * que tiene el menu del usuario
     */
    public void setNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.gerente_nav_view);
        assert navigationView != null;
        navigationView.setItemIconTintList(null);

        if (navigationView != null) {
            //añadir caracteristicas
            setupDrawerContent(navigationView);
            //Cambiar el numero para poner el fragmento que inicia en el arranque de la aplicacion
            seleccionarItem(navigationView.getMenu().getItem(0));
        }
    }

    /**
     * Se utiliza este metodo para colocar la información del usuario dentro del navigationView
     * el nombre del usuario es colocado y su numero de cuenta
     */
    private void setInformacionDrawer() {
        // TODO: Casteo de datos XML
        NavigationView navigationView = (NavigationView) findViewById(R.id.gerente_nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView navPrimeraLetra = (TextView) hView.findViewById(R.id.gerente_nav_tv_letra);
        TextView navDatosGerente = (TextView) hView.findViewById(R.id.gerente_nav_tv_datos);

        navigationView.getMenu().hasVisibleItems();

        // TODO: obteniendo datos del sharePreference
        HashMap<String,String> datosUsuario = sessionManager.getUserDetails();

        String apePaterno = datosUsuario.get(MySharePreferences.APELLIDO_PATERNO);
        String apeMaterno = datosUsuario.get(MySharePreferences.APELLIDO_MATERNO);
        String nombre = datosUsuario.get(MySharePreferences.NOMBRE);
        String idEmpleado = datosUsuario.get(MySharePreferences.USER_ID);

        char letra = nombre.charAt(0);
        String inicial = Character.toString(letra);

        navPrimeraLetra.setText(inicial);
        navDatosGerente.setText(nombre + " " + apePaterno + " " + apeMaterno + "\nNúmero empleado: " + Config.usuarioCusp(getApplicationContext()));

        // TODO: Se utiliza para colocar la primera de todo el nombre dentro de un contenedor
        navPrimeraLetra.setText(inicial);
    }

    /**
     * Se utiliza este metodo para generar la selecion del item seleccionado
     * @param navigationView
     * @return el item selecionado
     */
    private void setupDrawerContent(NavigationView navigationView) {
        navigationView.setNavigationItemSelectedListener(
                new NavigationView.OnNavigationItemSelectedListener() {
                    @Override
                    public boolean onNavigationItemSelected(MenuItem menuItem) {
                        menuItem.setChecked(true);
                        seleccionarItem(menuItem);
                        Log.d(TAG,"DRAWER SELECCION");
                        drawerLayout.closeDrawers();
                        return true;
                    }
                }
        );
    }

    /**
     * Se utiliza para redireccionar la seleccion de cualquier item
     * @param itemDrawer
     */
    private void seleccionarItem(MenuItem itemDrawer){
        Fragment fragmentoGenerico = null;
        FragmentManager fragmentManager = getSupportFragmentManager();
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_gerente);
        if(f instanceof DatosAsesor){
            checkProccess = true;
        }else if(f instanceof DatosCliente){
            global = "1.1.3.3";
            checkProccess = true;
        }else if(f instanceof Encuesta1){
            global = "1.1.3.4";
            checkProccess = true;
        }else if(f instanceof Encuesta2){
            global = "1.1.3.5";
            checkProccess = true;
        }else if(f instanceof Firma){
            global = "1.1.3.6";
            checkProccess = true;
        }else if(f instanceof Firma){
            global = "1.1.3.7";
            checkProccess = true;
        }else if(f instanceof Escaner){
            global = "1.1.3.8";
            checkProccess = true;
        }else{
            checkProccess = false;
        }
        if (fragmentoGenerico != null){
            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).addToBackStack("F_MAIN").commit();
        }

        //<editor-fold desc="Cambio de fragmentos">
        switch (itemDrawer.getItemId()){
            case R.id.gerente_nav_inicio:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Inicio();
                }else{
                    Gerente.itemMenu = new Inicio();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_calculadora:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Calculadora();
                }else{
                    Gerente.itemMenu = new Calculadora();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_biblioteca:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Biblioteca();
                }else{
                    Gerente.itemMenu = new Biblioteca();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_constancia_implicaciones:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new SinCita();
                }else{
                    Gerente.itemMenu = new SinCita();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_sucursales:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ReporteSucursales();
                }else{
                    Gerente.itemMenu = new ReporteSucursales();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_asesores:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ReporteAsesores();
                }else{
                    Gerente.itemMenu = new ReporteAsesores();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_clientes:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ReporteClientes();
                }else{
                    Gerente.itemMenu = new ReporteClientes();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_asistencia:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ReporteAsistencia();
                }else{
                    Gerente.itemMenu = new ReporteAsistencia();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_implicaciones_pendientes:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ProcesoImplicacionesPendientes();
                }else{
                    Gerente.itemMenu = new ProcesoImplicacionesPendientes();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_cerrar:
                checkMapsFragment = false;
                cerrarSesion();
                break;
        }
        //</editor-fold>
        if (fragmentoGenerico != null){
            fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).addToBackStack("F_MAIN").commit();
        }
    }

    public void salirFragment(Context context){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        dialogo1.setTitle("Confirmar");
        dialogo1.setMessage("\"¿Estás seguro que deseas cancelar el proceso" + global + " ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_gerente);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (f instanceof SinCita) {}
                else if(f instanceof DatosAsesor){}
                else if(f instanceof DatosCliente){global = "1.1.3.3";}
                else if(f instanceof Encuesta1){global = "1.1.3.4";}
                else if(f instanceof Encuesta2){global = "1.1.3.5";}
                else if(f instanceof Firma){global = "1.1.3.7";}
                else if(f instanceof Escaner){global = "1.1.3.8";}
                checkProccess = false;
                final Fragment borrar = f;
                borrar.onDestroy();
                ft.remove(borrar);
                ft.replace(R.id.content_gerente, Gerente.itemMenu);
                ft.addToBackStack(null);
                ft.commit();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogo1.show();
    }

    /**
     * Se utiliza para cerrar la sesion del usuario
     */
    public void cerrarSesion(){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(this);
        dialogo1.setTitle("Confirmar");
        dialogo1.setMessage("¿Estás seguro de cerrar la sesión?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                sessionManager.logoutUser();
            }
        });
        dialogo1.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

            }
        });
        dialogo1.show();
    }

    @Override
    public void onActivityResult(final int requestCode, final int resultCode, final Intent data) {
        switch (requestCode) {
            case REQUEST_CODE_OPENER:
                if (resultCode == RESULT_OK) {
                    mFileId = (DriveId) data.getParcelableExtra(OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);
                    Log.e("file id", mFileId.getResourceId() + " ");
                    url = "https://drive.google.com/open?id="+ mFileId.getResourceId();
                    Intent i = new Intent(Intent.ACTION_VIEW);
                    i.setData(Uri.parse(url));
                    startActivity(i);
                }
                break;
            default:
                super.onActivityResult(requestCode, resultCode, data);
                break;
        }
    }

    public void switchDatosAsesor(Fragment frag, String idClienteCuenta,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idClienteCuenta",idClienteCuenta);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchEncuesta1(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchEncuesta2(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchDocumento(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchDatosCliente(Fragment frag,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        Log.d("NOMBRES PRE ", "1" + nombre + " numero" + numeroDeCuenta);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchFirma(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Metodo para recibir los parametros y enviarlos entre fragmentos. Ejemplo enviar datos de filtros de Fragmento ReporteGerencias a ReporteClientes
     * @param fragment fragmento al cual se enviara la informacion
     * @param fechaInicio rango de la fecha inicial
     * @param fechaFin rango de la fecha final
     * @param idGerencia id de la gerencia
     * @param idSucursal id de la sucursal
     * @param idAsesor id del asesor
     * @param numeroEmpleado numero de empledo del asesor
     * @param nombreEmpleado nombre del empleado asesor
     * @param numeroCuenta numero de cuenta del cliente
     * @param cita 1.Con cita 2.Sin cita
     * @param hora hora del tramite de implicaciones
     * @param idTramite id generado por el la BD.
     */
    public void envioParametros(Fragment fragment, String fechaInicio, String fechaFin, int idGerencia, int idSucursal, String idAsesor, String numeroEmpleado, String nombreEmpleado, String numeroCuenta, boolean cita, String hora, int idTramite){
        Bundle bundle = new Bundle();
        bundle.putString("fechaInicio", fechaInicio);
        bundle.putString("fechaFin", fechaFin);
        bundle.putInt("idGerencia", idGerencia);
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("idAsesor",idAsesor);
        bundle.putString("numeroEmpleado",numeroEmpleado);
        bundle.putString("nombreEmpleado",nombreEmpleado);
        bundle.putString("numeroCuenta",numeroCuenta);
        bundle.putBoolean("cita",cita);
        bundle.putString("hora",hora);
        bundle.putInt("idTramite",idTramite);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, fragment, fragment.toString()).addToBackStack(null).commit();
    }

    private void Gerencias(JSONArray j){
        Log.e(TAG, j.toString() + "\n");
        int idGerencia = 0;
        String nombreGerencia = "";
        ArrayList<String> arrayNombreGerencias = new ArrayList<String>();
        ArrayList<Integer> arrayIdgerencia = new ArrayList<Integer>();

        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                idGerencia = json.getInt("idGerencia");
                nombreGerencia = json.getString("nombre");
                arrayNombreGerencias.add(nombreGerencia.toString());
                arrayIdgerencia.add(idGerencia);
                Map<Integer, String> gerenciaNodo = new HashMap<Integer, String>();
                gerenciaNodo.put(idGerencia, nombreGerencia);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        Config.nombreGerencia = arrayNombreGerencias;
        Config.idGerencia = arrayIdgerencia;
    }

    private void Sucursales(JSONArray j){
        int idSucursal = 0;
        String nombreSucursal = "";
        ArrayList<String> arrayNombreSucursal = new ArrayList<String>();
        ArrayList<Integer> arrayIdSucursal = new ArrayList<Integer>();
        arrayNombreSucursal.add("Selecciona una sucursal");
        arrayIdSucursal.add(-1);
        for(int i=0;i<j.length();i++){
            try {
                JSONObject json = j.getJSONObject(i);
                idSucursal = json.getInt("idSucursal");
                nombreSucursal = json.getString("nombre");
                arrayNombreSucursal.add(nombreSucursal.toString());
                arrayIdSucursal.add(idSucursal);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            Config.nombreSucursal = arrayNombreSucursal;
            Config.idSucusal = arrayIdSucursal;
        }
    }
}
