package com.airmovil.profuturo.ti.retencion.activities;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.directorFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsistencia;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsistenciaDetalles;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteGerencias;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.fragmento.Biblioteca;
import com.airmovil.profuturo.ti.retencion.fragmento.Calculadora;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.HashMap;

public class Director extends AppCompatActivity{
    private static final String TAG = Asesor.class.getSimpleName();
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Boolean checkMapsFragment = false;
    private Boolean checkProccess = false;
    private DriveId mFileId;
    private NavigationView navigationView;
    private static final  int REQUEST_CODE_OPENER = 2;
    String url;
    private InputMethodManager imm;

    /**
     * Se utiliza para iniciar la actividad
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.director);
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // TODO: nueva instancia para el sharePreference
        sessionManager = new SessionManager(getApplicationContext());
        // TODO: Validacion de la sesion del usuario
        validateSession();

    }

    /**
     * Este metodo valida la sesion del usuario, manda a llamar a otros metodos
     * como(setToolbar, setDrawable, setToggle, setNavigationView, setInformacionDrawer),
     * para contruir la vista general, menu superior(Toolba), menu lateral(DrawableView)
     * toggle(Icono abrir menu)
     */
    public void validateSession(){
        if(sessionManager.isLoggedIn() == false){
            startActivity(new Intent(Director.this, Login.class));
            finish();
        }else{
            Log.d(TAG, "Sesion: true");
            setToolbar();
            setDrawerLayout();
            setToggle();
            setNavigationView();
            setInformacionDrawer();
        }
    }

    /**
     * Este metodo coloca el menu superior
     * y se coloca la imagen de profuturo
     */
    // TODO: Coloca el menu dentro del contenedor
    public void setToolbar(){
        toolbar = (Toolbar) findViewById(R.id.director_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setIcon(R.drawable.img_icono_logo);
        getSupportActionBar().setTitle(" ");
    }

    /**
     * Se utiliza metodo Activa o desactiva la interacción con el contenido interior,
     * el navigationView y el contenedor
     */
    public void setDrawerLayout(){
        drawerLayout = (DrawerLayout) findViewById(R.id.director_drawer_layout);
        assert drawerLayout != null;
        drawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);
    }

    /**
     * Este metodo coloca dentro del toolbar un icono para activar y descativar
     * el uso del drawableView, recorrido de izquierda a derecha
     */
    public void setToggle(){
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawerLayout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawerLayout.setDrawerListener(toggle);
        toggle.syncState();
    }

    /**
     * Se utiliza este metodo para iniciar la navegacion entre los items
     * que tiene el menu del usuario
     */
    public void setNavigationView(){
        NavigationView navigationView = (NavigationView) findViewById(R.id.director_nav_view);
        assert navigationView != null;
        navigationView.setItemIconTintList(null);

        if (navigationView != null) {
            Log.d(TAG, "DRAWER OPEN");
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.director_nav_view);
        SessionManager sessionManager = new SessionManager(getApplicationContext());
        HashMap<String, String> informacion = sessionManager.getUserDetails();
        String sNumeroEmpleado = informacion.get(SessionManager.USER_ID);
        String sNombreEmpleado = informacion.get(SessionManager.NOMBRE);

        String nom = informacion.get(SessionManager.NOMBRE);
        View hView = navigationView.getHeaderView(0);
        TextView navPrimeraLetra = (TextView) hView.findViewById(R.id.director_nav_tv_letra);
        TextView navDatosGerente = (TextView) hView.findViewById(R.id.director_nav_tv_datos);
        char letra = sNombreEmpleado.charAt(0);
        String primeraLetra = Character.toString(letra);
        navPrimeraLetra.setText(primeraLetra);

        navDatosGerente.setText("Nombre: " + sNombreEmpleado + "\nNumero Empleado: " + sNumeroEmpleado);
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

        switch (itemDrawer.getItemId()){
            case R.id.director_nav_inicio:
                fragmentoGenerico = new Inicio();
                break;
            case R.id.director_nav_calculadora:
                fragmentoGenerico = new Calculadora();
                break;
            case R.id.director_nav_biblioteca:
                fragmentoGenerico = new Biblioteca();
                break;
            case R.id.director_nav_gerencias:
                fragmentoGenerico = new ReporteGerencias();
                break;
            case R.id.director_nav_sucursales:
                fragmentoGenerico = new ReporteSucursales();
                break;
            case R.id.director_nav_asesores:
                fragmentoGenerico = new ReporteAsesores();
                break;
            case R.id.director_nav_clientes:
                fragmentoGenerico = new ReporteClientes();
                break;
            case R.id.director_nav_asistencia:
                fragmentoGenerico = new ReporteAsistencia();
                break;
            case R.id.director_nav_cerrar:
                checkMapsFragment = false;
                cerrarSesion();
                break;
        }
        if (fragmentoGenerico != null){
            fragmentManager
                    .beginTransaction()//.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.content_director, fragmentoGenerico)
                    .addToBackStack("F_MAIN")
                    .commit();
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

    public void switchContent(Fragment frag, String idClienteCuenta) {
        Bundle bundle=new Bundle();
        bundle.putString("idClienteCuenta",idClienteCuenta);
        //Fragment fragment=new Fragment();
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    //director.switchClientes(fragmentoClientes, lista.getNumeroEmpleado(),fechaIni,fechaFin);
    public void switchClientes(Fragment frag, int numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("numeroEmpleado",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("hora",hora);

        Log.d("DIRECTOR", "CLIENTES - #Empleado " + numeroEmpleado + " FIN: " + fechaFin);

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }


    public void switchAsistencia(Fragment frag, int numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("numeroEmpleado",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        //bundle.putString("idGerencia",idGerencia);
        //bundle.putString("idSucursal",idSucursal);
        //bundle.putString("hora",hora);

        Log.d("DIRECTOR", "ASITENCIA - #Empleado " + numeroEmpleado + " FIN: " + fechaFin + " ");

        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchSucursales(Fragment frag, int idGerencia, String fechaInicio, String fechaFin){
        Bundle bundle=new Bundle();
        bundle.putInt("idGerencia",idGerencia);
        bundle.putString("fechaIni",fechaInicio);
        bundle.putString("fechaFin",fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

   public void switchDetalleClientes(int idSucursal, int idTramite, String numeroCuenta, String fechaInicio, String fechaFin, String usuario, Fragment frag){
       Log.d("switchDetallesClie", " --> " + " --> " + idSucursal + " --> " + idTramite + " --> " + numeroCuenta + " --> " + fechaInicio + " --> " + fechaFin + " --> " + usuario);
       Bundle bundle=new Bundle();
       bundle.putInt("idSucursal", idSucursal);
       bundle.putInt("idTramite", idTramite);
       bundle.putString("numeroCuenta", numeroCuenta);
       bundle.putString("fechaInicio", fechaInicio);
       bundle.putString("fechaFin", fechaFin);
       bundle.putString("usuario", usuario);
       frag.setArguments(bundle);
       FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
       ft.replace(R.id.content_director, frag, frag.toString());
       ft.addToBackStack(null);
       ft.commit();
    }
}
