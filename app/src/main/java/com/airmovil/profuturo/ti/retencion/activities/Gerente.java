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
import android.widget.Toast;

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
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.ServicioJSON;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.HashMap;

public class Gerente extends AppCompatActivity{
    private static final String TAG = Gerente.class.getSimpleName();
    private MySharePreferences sessionManager;
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

        try {
            startService(new Intent(this, ServicioJSON.class));
        }catch (Exception e){
            Log.d("-->Error en servicio:", e.toString());
        }
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
        final Fragment borrarFragmento;

        Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_gerente);
        if(f instanceof DatosAsesor){
            checkProccess = true;
        }else if(f instanceof DatosCliente){
            Log.d("Envia","a patir datos Cliente");
            global = "1.1.3.3";
            checkProccess = true;
        }else if(f instanceof Encuesta1){
            Log.d("Envia","a patir Encuesta 1");
            global = "1.1.3.4";
            checkProccess = true;
        }else if(f instanceof Encuesta2){
            Log.d("Envia","a patir Encuesta 2");
            global = "1.1.3.5";
            checkProccess = true;
        }else if(f instanceof Firma){
            Log.d("Envia","a patir Aviso de Privacidad");
            global = "1.1.3.6";
            checkProccess = true;
        }else if(f instanceof Firma){
            Log.d("Envia","a patir Firma");
            global = "1.1.3.7";
            checkProccess = true;
        }else if(f instanceof Escaner){
            Log.d("Envia","a patir Documento");
            global = "1.1.3.8";
            checkProccess = true;
        }else{
            checkProccess = false;
        }
        if (fragmentoGenerico != null){
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_gerente, fragmentoGenerico)
                    .addToBackStack("F_MAIN")
                    .commit();
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
            fragmentManager
                    .beginTransaction()
                    .replace(R.id.content_gerente, fragmentoGenerico)
                    .addToBackStack("F_MAIN")
                    .commit();
        }
    }

    public void salirFragment(Context context){
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

        //AlertDialog.Builder dialogo1 = new AlertDialog.Builder(getApplicationContext());
        dialogo1.setTitle("Confirmar");
        dialogo1.setMessage("\"¿Estás seguro que deseas cancelar el proceso" + global + " ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_gerente);
                FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
                if (f instanceof SinCita) {
                    Log.d("Envia", "a patir de Retencion");
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

                Log.d("Estas en este fragment","FF: "+f);

                checkProccess = false;
                Fragment fragmentoGenerico = null;
                FragmentManager fragmentManager = getSupportFragmentManager();
                fragmentoGenerico = new Inicio();
                /*if (fragmentoGenerico != null){
                    fragmentManager
                            .beginTransaction()//.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .replace(R.id.content_gerente, fragmentoGenerico)
                            .addToBackStack("F_MAIN")
                            .commit();
                }*/
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

    public void switchContent(Fragment frag,String idClienteCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idClienteCuenta",idClienteCuenta);
        //Fragment fragment=new Fragment();
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchDatosAsesor(Fragment frag, String idClienteCuenta,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idClienteCuenta",idClienteCuenta);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        //bundle.putString("hora",hora);

        Log.d("NOMBRES ASE", "1" + nombre + " numero" + numeroDeCuenta);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchDatosCliente(Fragment frag,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        //bundle.putString("hora",hora);

        frag.setArguments(bundle);
        Log.d("NOMBRES PRE ", "1" + nombre + " numero" + numeroDeCuenta);
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
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).remove(borrar).commit();
    }

    public void switchEncuesta2(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        //bundle.putString("hora",hora);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).remove(borrar).commit();
    }

    public void switchFirma(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        //bundle.putString("hora",hora);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).remove(borrar).commit();
    }


    public void switchDocumento(Fragment frag,String idTramite,Fragment borrar,String nombre,String numeroDeCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idTramite",idTramite);
        bundle.putString("nombre",nombre);
        bundle.putString("numeroDeCuenta",numeroDeCuenta);
        //bundle.putString("hora",hora);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag);
        ft.remove(borrar);
        ft.addToBackStack(null);
        ft.commit();
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_gerente, fragmentoGenerico).remove(borrar).commit();
    }

    public void switchClientesFA(Fragment frag, String numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();

        bundle.putString("IngresarDatoCliente","");
        bundle.putString("idAsesor",numeroEmpleado);
        bundle.putString("fechaInicio",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("hora",hora);
        Log.d("DIRECTOR", "CLIENTES - #Empleado " + numeroEmpleado + " FIN: " + fechaFin);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchClientesFS(Fragment frag, int idSucursal,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("idSucursales",idSucursal);
        bundle.putString("fechaInicio",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        bundle.putString("IngresarDatoCliente", "");
        //bundle.putString("hora",hora);

        Log.d("GERENTE", "CLIENTES - #idSucursal " + idSucursal + " FIN: " + fechaFin);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    //gerente.switchClientesFCQ1(fragmentoClientes,
    // mParam1 /*fechaInicio*/,
    // mParam2/*fechafin*/,
    // mParam3/*DatosCliente*/,
    // idSucursal/*IdSucursal*/,
    // mParam6/*idAsesor*/,
    // mParam7/*idRetenido*/,
    // mParam8/*idCitas*/,
    // mParam9/*SeleccionIDS*/);
    public void switchClientesFCQ1(Fragment frag, String fechaInicio, String fechaFin, String datosCliente, int idSucursal, String idAsesor,
                                   int idRetenido, int idCitas, int idSeleccion) {
        Bundle bundle=new Bundle();
        bundle.putString("fechaInicio", fechaInicio);
        bundle.putString("fechaFin", fechaFin);
        bundle.putString("IngresarDatoCliente", datosCliente);
        bundle.putInt("idSucursales", idSucursal);
        bundle.putString("idAsesor", idAsesor);
        bundle.putInt("idRetenido", idRetenido);
        bundle.putInt("idCita", idCitas);
        bundle.putInt("selectCliente", idSeleccion);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchClientesFCQ(Fragment frag, int idSucursal, String numeroEmpleado, String fechaIni, String fechaFin, int tipoBuscar, String numeroId, int retenido, int estatus) {
        Bundle bundle=new Bundle();
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("numeroEmpleado",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        bundle.putInt("tipoBuscar",tipoBuscar);
        bundle.putString("numeroId",numeroId);
        bundle.putInt("retenido",retenido);
        bundle.putInt("estatus",estatus);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }
    public void switchAsistenciaFA(Fragment frag, String numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putString("idAsesor",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("idGerencia",idGerencia);
        //bundle.putString("idSucursal",idSucursal);
        //bundle.putString("hora",hora);

        Log.d("GERENTE", "ASITENCIA - #Empleado " + numeroEmpleado + " FIN: " + fechaFin + " ");

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsistenciaFAT(Fragment frag,int idSucursal, String numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("numeroEmpleado",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("idGerencia",idGerencia);
        //bundle.putString("idSucursal",idSucursal);
        //bundle.putString("hora",hora);

        Log.d("GERENTE", "ASITENCIA - #Empleado " + numeroEmpleado + " FIN: " + fechaFin + " ");

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsistenciaFS(Fragment frag,int idGerencia, int idSucursal,String numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("idGerencia",idGerencia);
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("idAsesor",numeroEmpleado);
        bundle.putString("fechaInicio",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("hora",hora);

        Log.d("DIRECTOR", "CLIENTES - #idSucursal " + idSucursal + " FIN: " + fechaFin);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchSucursalFS(Fragment frag, int idSucursal,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("idGerencia",idGerencia);
        //bundle.putString("idSucursal",idSucursal);
        //bundle.putString("hora",hora);

        Log.d("GERENTE", "SUCURSAL - #idSucursal " + idSucursal + " FIN: " + fechaFin + " ");

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsesoresFA(Fragment frag, String numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putString("numeroEmpleado",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("idGerencia",idGerencia);
        //bundle.putString("idSucursal",idSucursal);
        //bundle.putString("hora",hora);

        Log.d("GERENTE", "SUCURSAL - #iNumero Empleado " + numeroEmpleado + " FIN: " + fechaFin + " ");

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
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
        //FragmentManager fragmentManager = getActivity().getSupportFragmentManager();
        //fragmentManager.beginTransaction().replace(R.id.content_asesor, fragmentoGenerico).remove(borrar).commit();
    }

    //lista.getNumeroCuenta(), lista.getCita(), lista.idTramite, fechaIni, fechaFin, Config.usuarioCusp(mContext), fragmento
    public void switchDetalleCliente(String numeroCuenta, String cita, int idTramite, String fechaInicio, String fechaFin, String hora, String usuario, Fragment frag) {
        Bundle bundle=new Bundle();
        bundle.putString("numeroCuenta",numeroCuenta);
        bundle.putString("cita",cita);
        bundle.putString("hora",hora);
        bundle.putInt("idTramite", idTramite);
        bundle.putString("fechaInicio", fechaInicio);
        bundle.putString("fechaFin", fechaFin);
        bundle.putString("usuario", usuario);
        frag.setArguments(bundle);
            FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
            ft.replace(R.id.content_gerente, frag);
            ft.addToBackStack(null);
            ft.commit();
    }

    public void switchAsistenciaDetalle(Fragment frag, String numeroEmpleado, String nombreEmpleado, String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putString("numeroEmpleado",numeroEmpleado);
        bundle.putString("nombreEmpleado", nombreEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("idGerencia",idGerencia);
        //bundle.putString("idSucursal",idSucursal);
        //bundle.putString("hora",hora);

        Log.d("DIRECTOR", "ASITENCIA - #Empleado " + numeroEmpleado + " FIN: " + fechaFin + " ");

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchDetalleClientes(int idSucursal, int idTramite, String numeroCuenta, String fechaInicio, String fechaFin, String usuario, String nombreCliente, String idAsesor, Fragment frag){
        Log.d("switchDetallesClie", " --> " + " --> " + idSucursal + " --> " + idTramite + " --> " + numeroCuenta + " --> " + fechaInicio + " --> " + fechaFin + " --> " + usuario);
        Bundle bundle=new Bundle();
        bundle.putString("numeroCuenta", numeroCuenta);
        bundle.putString("fechaInicio", fechaInicio);
        bundle.putString("fechaFin", fechaFin);
        bundle.putString("IngresarDatoCliente", "");
        bundle.putInt("idSucursales", idSucursal);
        bundle.putInt("idTramite", idTramite);
        bundle.putString("idAsesor", idAsesor);
        bundle.putString("nombreCliente1", nombreCliente);
        bundle.putInt("idRetenido", 0);
        bundle.putInt("idCita", 0);
        bundle.putInt("selectCliente", 0);

        /*
         idCita1 = getArguments().getInt("idCita");
         selectID = getArguments().getInt("selectCliente");
         */
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_gerente, frag, frag.toString());
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
}
