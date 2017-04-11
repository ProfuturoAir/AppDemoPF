package com.airmovil.profuturo.ti.retencion.activities;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AlertDialog;
import android.support.v7.view.ContextThemeWrapper;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistencia;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.DatosCliente;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Encuesta1;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Encuesta2;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Escaner;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Firma;
import com.airmovil.profuturo.ti.retencion.fragmento.Biblioteca;
import com.airmovil.profuturo.ti.retencion.fragmento.Calculadora;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.SinCita;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.HashMap;

public class Gerente extends AppCompatActivity{
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
        setContentView(R.layout.gerente);
        // TODO: Mantener el estado de la pantalla Vertical
        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        sessionManager = new SessionManager(getApplicationContext());
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
    public void setToolbar()   {
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.gerente_nav_view);
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
        NavigationView navigationView = (NavigationView) findViewById(R.id.gerente_nav_view);

        HashMap<String, String> informacion = sessionManager.getUserDetails();
        String sNumeroEmpleado = informacion.get(SessionManager.ID);
        String sNombreEmpleado = informacion.get(SessionManager.NOMBRE);

        View hView = navigationView.getHeaderView(0);

        //TextView navPrimeraLetra = (TextView) hView.findViewById(R.id.gerente_nav_tv_letra);
        //TextView navDatosGerente = (TextView) hView.findViewById(R.id.gerente_nav_tv_datos);

        char letra = sNombreEmpleado.charAt(0);
        String primeraLetra = Character.toString(letra);

//        navPrimeraLetra.setText(primeraLetra);
//        navDatosGerente.setText("Nombre: " + sNombreEmpleado + "\nNumero Empleado: " + sNumeroEmpleado);
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

        //<editor-fold desc="Condiciones de de fragmentos">
        if(f instanceof DatosCliente){
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
        //</editor-fold>

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
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_calculadora:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Calculadora();
                }else{
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_biblioteca:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new Biblioteca();
                }else{
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_constancia_implicaciones:
                if(checkProccess == false) {
                    checkMapsFragment = false;
                    fragmentoGenerico = new SinCita();
                }else{
                    salirFragment(getApplicationContext());
                }
                break;
            case R.id.gerente_nav_sucursales:
                fragmentoGenerico = new ReporteSucursales();
                break;
            case R.id.gerente_nav_asesores:
                fragmentoGenerico = new ReporteAsesores();
                break;
            case R.id.gerente_nav_clientes:
                fragmentoGenerico = new ReporteClientes();
                break;
            case R.id.gerente_nav_asistencia:
                fragmentoGenerico = new ReporteAsistencia();
                break;

            case R.id.gerente_nav_cerrar:
                checkMapsFragment = false;
                cerrarSesion();
                break;
        }
        //</editor-fold>

        if (fragmentoGenerico != null){
            fragmentManager
                    .beginTransaction()//.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                    .replace(R.id.content_gerente, fragmentoGenerico)
                    .addToBackStack("F_MAIN")
                    .commit();
        }
    }

    public void salirFragment(Context context){
        //AlertDialog.Builder dialogo1 = new AlertDialog.Builder(context);
        AlertDialog.Builder dialogo1 = new AlertDialog.Builder(new ContextThemeWrapper(this, R.style.AppTheme));

        dialogo1.setTitle("Confirmar");
        dialogo1.setMessage("\"¿Estàs seguro que deseas cancelar y guardar los cambios del proceso " + global + " ?");
        dialogo1.setCancelable(false);
        dialogo1.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                Fragment f = getSupportFragmentManager().findFragmentById(R.id.content_asesor);

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
                if (fragmentoGenerico != null){
                    fragmentManager
                            .beginTransaction()//.setCustomAnimations(android.R.anim.slide_in_left, android.R.anim.slide_out_right)
                            .replace(R.id.content_gerente, fragmentoGenerico)
                            .addToBackStack("F_MAIN")
                            .commit();
                }
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

}
