package com.airmovil.profuturo.ti.retencion.activities;

import android.app.ProgressDialog;
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
import android.widget.Toast;

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
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.MySharePreferences;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;
import com.google.android.gms.drive.DriveId;
import com.google.android.gms.drive.OpenFileActivityBuilder;

import java.util.HashMap;

public class Director extends AppCompatActivity{
    private static final String TAG = Asesor.class.getSimpleName();
    private MySharePreferences sessionManager;
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
        sessionManager = MySharePreferences.getInstance(getApplicationContext());
        // TODO: Validacion de la sesion del usuario
        validateSession();

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
            startActivity(new Intent(Director.this, Login.class));
            finish();
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
        String sNumeroEmpleado = informacion.get(MySharePreferences.USER_ID);
        String sNombreEmpleado = informacion.get(MySharePreferences.NOMBRE);

        String nom = informacion.get(MySharePreferences.NOMBRE);
        View hView = navigationView.getHeaderView(0);
        TextView navPrimeraLetra = (TextView) hView.findViewById(R.id.director_nav_tv_letra);
        TextView navDatosGerente = (TextView) hView.findViewById(R.id.director_nav_tv_datos);
        char letra = sNombreEmpleado.charAt(0);
        String primeraLetra = Character.toString(letra);
        navPrimeraLetra.setText(primeraLetra);

        navDatosGerente.setText("Nombre: " + sNombreEmpleado + "\nNumero Empleado: " + Config.usuarioCusp(getApplicationContext()));
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
                fragmentoGenerico = new ReporteAsistencia();
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
                    .beginTransaction()
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

    public void switchClientes(Fragment frag, int numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("numeroEmpleado",numeroEmpleado);
        bundle.putString("idAsesor", ""+numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaInicio",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        Log.d("DIRECTOR", "CLIENTES - #Empleado " + numeroEmpleado + " FIN: " + fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsistencia(Fragment frag,int idGerencia, int idSucursal,String numeroEmpleado,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("idGerencia",idGerencia);
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("idAsesor",numeroEmpleado);
        bundle.putString("fechaInicio",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsistenciaDetalle(Fragment frag, String numeroEmpleado, String nombreEmpleado, String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putString("numeroEmpleado",numeroEmpleado);
        bundle.putString("nombreEmpleado", nombreEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
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
        bundle.putString("fechaInicio",fechaInicio);
        bundle.putString("fechaFin",fechaFin);
        bundle.putString("ingresarDatoCliente","");
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsesoresFG(Fragment frag, int idGerencia, String fechaInicio, String fechaFin){
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

    public void switchClientesFG(Fragment frag, int idGerencia, String fechaInicio, String fechaFin){
        Bundle bundle=new Bundle();
        bundle.putInt("idGerencia",idGerencia);
        bundle.putString("fechaIni",fechaInicio);
        bundle.putString("fechaInicio",fechaInicio);
        bundle.putString("fechaFin",fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    // list.getNumeroEmpleado(),list.getNombreAsesor(),list.getNumeroCuenta(), list.getCita(), list.getIdTramite(), fechaInicio, fechaFin, list.getHora(), Config.usuarioCusp(mContext), fragmento);
   public void switchDetalleClientes(String numeroEmpleado, String nombreAsesor,String numeroCuenta, String cita, int idTramite, String fechaInicio, String fechaFin, String hora, String usuario, Fragment frag) {
       Bundle bundle=new Bundle();
       bundle.putString("numeroEmpleado",numeroEmpleado);
       bundle.putString("numeroCuenta",numeroCuenta);
       bundle.putString("cita",cita);
       bundle.putString("hora",hora);
       bundle.putInt("idTramite", idTramite);
       bundle.putString("fechaInicio", fechaInicio);
       bundle.putString("fechaFin", fechaFin);
       bundle.putString("usuario", usuario);
       bundle.putString("nombreAsesor",nombreAsesor);
       frag.setArguments(bundle);
       frag.setArguments(bundle);
       FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
       ft.replace(R.id.content_director, frag, frag.toString());
       ft.addToBackStack(null);
       ft.commit();
    }

    public void switchClientesFCQ(Fragment frag, int idSucursal, int idGerencia, int numeroEmpleado, String fechaIni, String fechaFin, String tipoBuscar, String numeroId, int retenido, int estatus) {
        Bundle bundle=new Bundle();
        bundle.putInt("idSucursal",idSucursal);
        bundle.putInt("idGerencia",idGerencia);
        bundle.putInt("numeroEmpleado",numeroEmpleado);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        bundle.putString("tipoBuscar",tipoBuscar);
        bundle.putString("numeroId",numeroId);
        bundle.putInt("retenido",retenido);
        bundle.putInt("estatus",estatus);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchSucursalFRS(Fragment frag,int idGerencia, int idSucursal, String fechaInicio, String fechaFin){
        Bundle bundle=new Bundle();
        bundle.putInt("idGerencia", idGerencia);
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("fechaInicio",fechaInicio);
        bundle.putString("fechaFin",fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
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
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchClientesFS(Fragment frag, int idSucursal,String fechaIni,String fechaFin) {
        Bundle bundle=new Bundle();
        bundle.putInt("idSucursal",idSucursal);
        bundle.putString("fechaIni",fechaIni);
        bundle.putString("fechaInicio",fechaIni);
        bundle.putString("fechaFin",fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }

    public void switchAsesoresFA(Fragment frag, String idAsesor, String fechaInicio, String fechaFin){
        Bundle bundle=new Bundle();
        bundle.putString("idAsesor",idAsesor);
        bundle.putString("fechaInicio",fechaInicio);
        bundle.putString("fechaFin",fechaFin);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
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
    public void switchClientesFCQ11(Fragment frag, String fechaInicio, String fechaFin, String datosCliente, int idSucursal, int idGerencia, String idAsesor, int idRetenido, int idCitas, int idSeleccion) {
        Bundle bundle=new Bundle();
        bundle.putString("fechaInicio", fechaInicio);
        bundle.putString("fechaFin", fechaFin);
        bundle.putString("ingresarDatoCliente", datosCliente);
        bundle.putInt("idSucursal", idSucursal);
        bundle.putInt("idGerencia", idGerencia);
        bundle.putString("idAsesor", idAsesor);
        bundle.putInt("idRetenido", idRetenido);
        bundle.putInt("idCita", idCitas);
        bundle.putInt("selectCliente", idSeleccion);
        frag.setArguments(bundle);
        FragmentTransaction ft = getSupportFragmentManager().beginTransaction();
        ft.replace(R.id.content_director, frag, frag.toString());
        ft.addToBackStack(null);
        ft.commit();
    }
}
