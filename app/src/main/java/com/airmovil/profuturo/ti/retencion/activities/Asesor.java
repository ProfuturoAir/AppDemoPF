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
import com.airmovil.profuturo.ti.retencion.helper.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Asistencia;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ConCita;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.DatosCliente;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Encuesta1;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Encuesta2;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Escaner;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Firma;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ProcesoImplicacionesPendientes;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.SinCita;
import com.airmovil.profuturo.ti.retencion.fragmento.Biblioteca;
import com.airmovil.profuturo.ti.retencion.fragmento.Calculadora;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.ServicioJSON;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.HashMap;

public class Asesor extends AppCompatActivity{
    private static final String TAG = Asesor.class.getSimpleName();
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Boolean checkMapsFragment = false;
    private Boolean checkProccess = false;
    private String global = "";
    private DriveId mFileId;
    private static final  int REQUEST_CODE_OPENER = 2;
    String url;
    public static Fragment itemMenu = null;
    private MySharePreferences mySharePreferences;

    /**
     * Se utiliza para iniciar la actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.asesor);
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // TODO: nueva instancia para el sharePreference
        mySharePreferences = MySharePreferences.getInstance(getApplicationContext());
        // TODO: Validacion de la sesion del usuario
        validateSession();

        Config.context = getApplicationContext();
        try {
            startService(new Intent(this, ServicioJSON.class));
        }catch (Exception e){
            Log.d("-->Error en servicio:", e.toString());
        }
    }

    /**
     *
     */
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
        if(mySharePreferences.isLoggedIn() == false){
            startActivity(new Intent(Asesor.this, Login.class));
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

            View view = this.getCurrentFocus();
            if (view != null) {
                InputMethodManager imm = (InputMethodManager)getSystemService(Context.INPUT_METHOD_SERVICE);
                imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
            }
        }
    }

    /**
     * Este metodo coloca el menu superior
     * y se coloca la imagen de profuturo
     */
    public void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.asesor_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.img_icono_logo);
        getSupportActionBar().setTitle(" ");
    }

    /**
     * Se utiliza metodo Activa o desactiva la interacción con el contenido interior,
     * el navigationView y el contenedor
     */
    public void setDrawerLayout(){
        drawerLayout = (DrawerLayout) findViewById(R.id.asesor_drawer_layout);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.asesor_nav_view);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.asesor_nav_view);
        View hView = navigationView.getHeaderView(0);
        TextView navPrimeraLetra = (TextView) hView.findViewById(R.id.asesor_nav_tv_letra);
        TextView navDatosGerente = (TextView) hView.findViewById(R.id.asesor_nav_tv_datos);
        navigationView.getMenu().hasVisibleItems();
        // TODO: obteniendo datos del sharePreference
        HashMap<String,String> datosUsuario = mySharePreferences.getUserDetails();
        navPrimeraLetra.setText(Character.toString(datosUsuario.get(MySharePreferences.NOMBRE).charAt(0)));
        navDatosGerente.setText(datosUsuario.get(MySharePreferences.NOMBRE) + " " + datosUsuario.get(MySharePreferences.APELLIDO_PATERNO) + " " + datosUsuario.get(MySharePreferences.APELLIDO_MATERNO) + "\nNúmero empleado: " + Config.usuarioCusp(getApplicationContext()));
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
        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_asesor);
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
        }else if(f instanceof Escaner){
            checkProccess = true;
        }
        if (fragmentoGenerico != null){
            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).addToBackStack("F_MAIN").commit();
        }

        //<editor-fold desc="Cambio de fragmentos">
        switch (itemDrawer.getItemId()){
            case R.id.asesor_nav_inicio:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Inicio();
                }else{
                    Asesor.itemMenu= new Inicio();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_calculadora:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Calculadora();
                }else{
                    Asesor.itemMenu= new Calculadora();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_biblioteca:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Biblioteca();
                }else{
                    Asesor.itemMenu= new Biblioteca();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_constancia_implicaciones:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ConCita();
                    Asesor.itemMenu= new ConCita();
                }else{
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_asistencia:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Asistencia();
                }else{
                    Asesor.itemMenu = new Asistencia();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_reporte:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ReporteClientes() ;
                }else{
                    Asesor.itemMenu = new ReporteClientes();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_implicaciones_pendientes:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new ProcesoImplicacionesPendientes();
                }else{
                    Asesor.itemMenu = new ProcesoImplicacionesPendientes();
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.asesor_nav_cerrar:
                checkMapsFragment = false;
                cerrarSesion();
                break;
        }
        //</editor-fold>
        if (fragmentoGenerico != null){
            fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).addToBackStack("F_MAIN").commit();
        }
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
                mySharePreferences.logoutUser();
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
     * Este es el manejador de resultados del contenido del Drive.
     * Este método callback llama al método CreateFileOnGoogleDrive ()
     * Y también llamado OpenFileFromGoogleDrive () método,
     * Enviar método onActivityResult () para manejar el resultado.
     */
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

    /**
     * envio de parametros a otro fragmento
     * @param frag fragmento de cambio
     * @param idClienteCuenta idClienteCuenta
     */
    public void switchContent(Fragment frag, String idClienteCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idClienteCuenta",idClienteCuenta);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Inicia este metodo cuando el usuario quiere salir del proceso de implicaciones
     * @param context contexto
     */
    public void salirFragment(Context context){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));
        dialogo1.setTitle("Confirmar");
        dialogo1.setMessage("¿Estás seguro que deseas cancelar el proceso " + global + " ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_asesor);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if(f instanceof SinCita){
                    Log.d("Envia", "apartir sin cita");
                }else if(f instanceof DatosAsesor){
                    Log.d("Envia","a patir Asesor");
                }else if(f instanceof DatosCliente){
                    global = "1.1.3.3";
                    Log.d("Envia","a patir datos Cliente");
                }else if(f instanceof Encuesta1){
                    global = "1.1.3.4";
                    Log.d("Envia","a patir Encuesta 1");
                }else if(f instanceof Encuesta2){
                    global = "1.1.3.5";
                    Log.d("Envia","a patir Encuesta 2");
                }else if(f instanceof Firma){
                    global = "1.1.3.7";
                    Log.d("Envia","a patir Firma");
                }else if(f instanceof Escaner){
                    global = "1.1.3.8";
                    Log.d("Envia","a patir Documento");
                }
                checkProccess = false;
                final Fragment borrar = f;
                borrar.onDestroy();
                ft.remove(borrar);
                ft.replace(R.id.content_asesor, Asesor.itemMenu);
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
     * Metodo para envio  de parametro al fragmnto
     * @param frag fragmento de envio de informacion
     * @param idClienteCuenta idCliente para uso en otros fragmento
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numero de cuenta del cliente
     */
    public void switchDatosAsesor(Fragment frag, String idClienteCuenta,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idClienteCuenta",idClienteCuenta);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag, frag.toString()).addToBackStack(null).commit();
    }

    /**
     * metodo para envio de datos a fragmento datosCliente
     * @param frag direccion del fragmento
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numero de cuenta cliente
     * @param hora hora de atencion del cliente
     */
    public void switchDatosCliente(Fragment frag,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag, frag.toString()).addToBackStack(null).commit();
    }

    /**
     * Metodo para envio de datos a fragmento Encuesta1
     * @param frag direccion del fragmento
     * @param idTramite idTramite del cliente
     * @param borrar borrar datos del fragmento
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numeroCuenta clinete
     * @param hora hora de atencion del cliente
     */
    public void switchEncuesta1(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag).remove(borrar).addToBackStack(null).commit();
    }

    /**
     * Metodo para enviar parametros a fragmeto
     * @param frag direccion del fragmento envio
     * @param idTramite idTramite del cliente
     * @param borrar borrar datos del fragmento
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numeroDeCuenta del cliente
     * @param hora hora de atencion del cliente
     */
    public void switchEncuesta2(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag).remove(borrar).addToBackStack(null).commit();
    }

    /**
     * Metodo para enviar parametros a fragmento
     * @param frag direccion del fragmento para envio
     * @param idTramite idTramite del cliente
     * @param borrar borrar datos del fragmento
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numeroDeCuenta del cliente
     * @param hora hora de atencion del cliente
     */
    public void switchFirma(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag).remove(borrar).addToBackStack(null).commit();
    }

    /**
     * Metodo para enviar parametros a fragmento
     * @param frag direccion del fragmento para envio
     * @param idTramite idTramite del cliente
     * @param borrar borrar datos del fragmento
     * @param nombre nombre del cliente
     * @param numeroDeCuenta numeroDeCuenta del cliente
     * @param hora hora de atencion del cliente
     */
    public void switchDocumento(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta,String hora) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        bundle.putString("hora",hora);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Metodo para enviar parametros a fragmento
     * @param fragment direccion del fragmento para envio
     * @param idTramite idTramite del cliente
     * @param fechaInicio fechaInicio
     * @param fechaFin fechaFin
     * @param hora hora de atencion del cliente
     */
    public void switchDetalleCliente(Fragment fragment, String curp, int idTramite, String hora, String fechaInicio, String fechaFin) {
        Bundle bundle = new Bundle();
        bundle.putString("curp", curp);
        bundle.putInt("idTramite", idTramite);
        bundle.putString("hora", hora);
        bundle.putString("fechaInicio", fechaInicio);
        bundle.putString("fechaFin", fechaFin);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, fragment);
        ft.addToBackStack(null);
        ft.commit();
    }

    /**
     * Metodo para recibir los parametros y enviarlos entre fragmentos. Ejemplo enviar datos de filtros de Fragmento ReporteGerencias a ReporteClientes
     * @param fragment fragmento al cual se enviara la informacion
     * @param fechaInicio rango de la fecha inicial
     * @param fechaFin rango de la fecha final
     */
    public void envioParametros(Fragment fragment, String fechaInicio, String fechaFin){
        Bundle bundle = new Bundle();
        bundle.putString("param3", fechaInicio);
        bundle.putString("param4", fechaFin);
        fragment.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_asesor, fragment, fragment.toString()).addToBackStack(null).commit();
    }

    public void parametrosDetalle(Fragment fragment, int idTramite,  String nombre, String numeroCuenta, String hora, String nombreAsesor, String cuentaAsesor, String sucursalAsesor, String nombreCliente, String numCuentaCliente, String nssCliente,
                                  String curpCliente, String fechaCliente, String saldoCliente, boolean pregunta1, boolean pregunta2, boolean pregunta3, String observaciones, String afore,
                                  String motivo, String estatus, String instituto, String regimen, String documentacion, String telefono, String email){
        Bundle bundle = new Bundle();
        bundle.putInt("idTramite", idTramite);
        bundle.putString("nombre", nombre);
        bundle.putString("numeroCuenta", numeroCuenta);
        bundle.putString("hora", hora);
        bundle.putString("nombreAsesor", nombreAsesor);
        bundle.putString("cuentaAsesor", cuentaAsesor);
        bundle.putString("sucursalAsesor", sucursalAsesor);
        bundle.putString("nombreCliente", nombreCliente);
        bundle.putString("numCuentaCliente", numCuentaCliente);
        bundle.putString("nssCliente", nssCliente);
        bundle.putString("curpCliente", curpCliente);
        bundle.putString("fechaCliente", fechaCliente);
        bundle.putString("saldoCliente", saldoCliente);
        bundle.putBoolean("pregunta1", pregunta1);
        bundle.putBoolean("pregunta2", pregunta2);
        bundle.putBoolean("pregunta3", pregunta3);
        bundle.putString("observaciones", observaciones);
        bundle.putString("afore", afore);
        bundle.putString("motivo", motivo);
        bundle.putString("estatus", estatus);
        bundle.putString("instituto", instituto);
        bundle.putString("regimen", regimen);
        bundle.putString("documentacion", documentacion);
        bundle.putString("telefono", telefono);
        bundle.putString("email", email);
        fragment.setArguments(bundle);
        getSupportFragmentManager().beginTransaction().replace(R.id.content_asesor, fragment, fragment.toString()).addToBackStack(null).commit();
    }
}
