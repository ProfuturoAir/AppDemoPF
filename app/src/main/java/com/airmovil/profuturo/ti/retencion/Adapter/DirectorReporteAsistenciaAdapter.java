package com.airmovil.profuturo.ti.retencion.Adapter;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsistencia;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsistenciaDetalles;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 22/03/17.
 */

public class DirectorReporteAsistenciaAdapter extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<DirectorReporteAsistenciaModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    public DirectorReporteAsistenciaAdapter(Context mContext, List<DirectorReporteAsistenciaModel> list, RecyclerView mRecyclerView){
        this.mContext = mContext;
        this.list = list;
        this.mRecyclerView = mRecyclerView;

        final LinearLayoutManager linearLayoutManager = (LinearLayoutManager) this.mRecyclerView.getLayoutManager();
        this.mRecyclerView.addOnScrollListener(new RecyclerView.OnScrollListener() {
            @Override
            public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
                super.onScrolled(recyclerView, dx, dy);

                totalItemCount = linearLayoutManager.getItemCount();
                lastVisibleItem = linearLayoutManager.findLastVisibleItemPosition();

                if (!isLoading && totalItemCount <= (lastVisibleItem + visibleThreshold)) {
                    if (mOnLoadMoreListener != null) {
                        mOnLoadMoreListener.onLoadMore();
                    }
                    isLoading = true;
                }

            }
        });

    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;

        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.director_fragmento_reporte_asistencia_lista, parent, false);
            vh = new MyViewHolder(view);
        } else {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            vh = new LoadingViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder instanceof MyViewHolder) {

            final DirectorReporteAsistenciaModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoNombreAsesor.setText(lista.getNombre());
            myholder.campoNumeroCuentaAsesor.setText("Numero empleado " + "123123");
            //myholder.campoSucursalAsesor.setText(lista.getIdSucursal());


            char nombre = lista.getNombre().charAt(0);
            final String pLetra = Character.toString(nombre);

            myholder.campoLetra.setText(pLetra);

            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentJumpDatosUsuario(pLetra, v);
                }
            });

            myholder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v);
                }
            });

        } else {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    /**
     * Muesta el menu cuando se hace click en los 3 botonos de la lista
     */
    private void surgirMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_asistencia, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    /**
     * escucha el popup al dar click
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sub_menu_reporte_asistencia_nav_detalle_asistencia:
                    AppCompatActivity AsistenciaDetalles = (AppCompatActivity) mRecyclerView.getContext();
                    ReporteAsistenciaDetalles fragmentoAsistenciaDetalles = new ReporteAsistenciaDetalles();
                    //Create a bundle to pass data, add data, set the bundle to your fragment and:
                    AsistenciaDetalles.getSupportFragmentManager().beginTransaction().replace(R.id.content_director, fragmentoAsistenciaDetalles).addToBackStack(null).commit();
                    return true;
                case R.id.sub_menu_reporte_asistencia_email:
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.custom_layout);

                    Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                    Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);

                    // TODO: Spinner
                    ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(mContext, R.layout.spinner_item_azul, Config.EMAIL);
                    adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinner.setAdapter(adapterSucursal);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);

                            final String datoEditText = editText.getText().toString();
                            Connected connected = new Connected();
                            if(connected.estaConectado(mContext)){

                                InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
                                imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                Config.msjTime(mContext, "Enviando", "Se ha enviado el mensaje al destino", 4000);
                                dialog.dismiss();
                            }else{
                                Config.msj(mContext,"Error conexión", "Por favor, revisa tu conexión a internet");
                                dialog.dismiss();
                            }
                            //final String datoSpinner = spinner.getSelectedItem().toString()

                        }
                    });
                    dialog.show();
                    return true;
                default:
            }
            return false;
        }
    }

    @Override
    public int getItemCount() {
        return list.size();
    }

    @Override
    public int getItemViewType(int position) {
        return list.get(position) ==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void fragmentJumpDatosUsuario(String idClienteCuenta, View view) {
        Fragment fragmento = new ReporteAsistenciaDetalles();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Director) {
            Director director = (Director) view.getContext();

            final Connected conected = new Connected();
            if(conected.estaConectado(view.getContext())) {

            }else{
                Config.msj(view.getContext(),"Error conexión", "Sin Conexion por el momento.Cliente P-1.1.3");
            }
            director.switchContent(fragmento, idClienteCuenta);
        }
    }

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;
        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView campoLetra, campoNombreAsesor, campoNumeroCuentaAsesor, campoSucursalAsesor;
        public CardView cardView;
        public TextView btn;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.ddfrasl_tv_letra);
            campoNombreAsesor = (TextView) view.findViewById(R.id.ddfrasl_tv_nombre_asesor);
            campoNumeroCuentaAsesor = (TextView) view.findViewById(R.id.ddfrasl_tv_numero_empleado_asesor);
            campoSucursalAsesor = (TextView) view.findViewById(R.id.ddfrasl_tv_numero_sucursal_asesor);
            btn = (TextView) view.findViewById(R.id.ddfrasl_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.ddfrasl_cv);
        }
    }
}