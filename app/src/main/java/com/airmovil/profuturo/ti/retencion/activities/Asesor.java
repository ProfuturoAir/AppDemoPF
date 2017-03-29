package com.airmovil.profuturo.ti.retencion.activities;

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
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ConCita;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.fragmento.Biblioteca;
import com.airmovil.profuturo.ti.retencion.fragmento.Calculadora;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.HashMap;

public class Asesor extends AppCompatActivity{
    private static final String TAG = Asesor.class.getSimpleName();
    private SessionManager sessionManager;
    private Toolbar toolbar;
    private DrawerLayout drawerLayout;
    private Boolean checkMapsFragment = false;
    private Boolean checkProccess = false;
    private String global = "";
    private DriveId mFileId;
    private static final  int REQUEST_CODE_OPENER = 2;
    String url;
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
            Log.d(TAG, "Sesión: false");
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

        // TODO: obteniendo datos del sharePreference
        HashMap<String, String> informacion = sessionManager.getUserDetails();
        String sNumeroEmpleado = informacion.get(SessionManager.ID);
        String sNombreEmpleado = informacion.get(SessionManager.NOMBRE);

        // TODO: Se utiliza
        char letra = sNombreEmpleado.charAt(0);
        String primeraLetra = Character.toString(letra);
        // TODO: Se utiliza para colocar la primera de todo el nombre dentro de un contenedor
        navPrimeraLetra.setText(primeraLetra);
        // TODO: Se utiliza para colocar el nombre y el numero de cuenta del usuario
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
            case R.id.asesor_nav_inicio:
                fragmentoGenerico = new Inicio();
                break;
            case R.id.asesor_nav_calculadora:
                fragmentoGenerico = new Calculadora();
                break;
            case R.id.asesor_nav_biblioteca:
                fragmentoGenerico = new Biblioteca();
                break;
            case R.id.asesor_nav_constancia_implicaciones:
                fragmentoGenerico = new ConCita();
                break;
            case R.id.asesor_nav_asistencia:

                break;
            case R.id.asesor_nav_reporte:

                break;
            case R.id.asesor_nav_cerrar:
                cerrarSesion();
                break;
        }
        if (fragmentoGenerico != null){
            fragmentManager
                    .beginTransaction().setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    //.add(fragmentoGenerico)
                    .replace(R.id.content_asesor, fragmentoGenerico)
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

        Log.d("ACTIVITY","ENTRA EN LA ACTIVIDAD");
        switch (requestCode) {

            case REQUEST_CODE_OPENER:

                if (resultCode == RESULT_OK) {

                    mFileId = (DriveId) data.getParcelableExtra(
                            OpenFileActivityBuilder.EXTRA_RESPONSE_DRIVE_ID);

                    Log.e("file id", mFileId.getResourceId() + " ");

                    url = "https://drive.google.com/open?id="+ mFileId.getResourceId();

                    //enviarURL(url);
                    //change_fragment=true;
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
        ft.replace(R.id.content_asesor, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }
}
