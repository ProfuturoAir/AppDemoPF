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
    NavigationView navigationView;
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
        sessionManager = new SessionManager(getApplicationContext());
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        // TODO: ocultar teclado
        InputMethodManager imm = (InputMethodManager) getSystemService(getApplicationContext().INPUT_METHOD_SERVICE);
        NavigationView navigationView = (NavigationView) findViewById(R.id.director_nav_view);

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
            Log.d(TAG, "Sesión: false");
        }else{
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

        HashMap<String, String> informacion = sessionManager.getUserDetails();
        String sNumeroEmpleado = informacion.get(SessionManager.ID);
        String sNombreEmpleado = informacion.get(SessionManager.NOMBRE);

        View hView = navigationView.getHeaderView(0);

        TextView navPrimeraLetra = (TextView) hView.findViewById(R.id.director_nav_tv_letra);
        TextView navDatosGerente = (TextView) hView.findViewById(R.id.director_nav_tv_datos);

        char letra = sNombreEmpleado.charAt(0);
        String primeraLetra = Character.toString(letra);

        //navPrimeraLetra.setText("L");

        //navDatosGerente.setText("Nombre: " + sNombreEmpleado + "\nNumero Empleado: " + sNumeroEmpleado);
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
                fragmentoGenerico = new ReporteGerencias();
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


}
