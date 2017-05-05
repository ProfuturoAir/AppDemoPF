package com.airmovil.profuturo.ti.retencion.Adapter;

import android.content.Context;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsistencia;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteSucursalesModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 04/04/17.
 */

public class DirectorReporteSucursalesAdapter extends RecyclerView.Adapter{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<DirectorReporteSucursalesModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;
    private String mFechaInicio;
    private String mFechaFin;

    /**
     * Constructor
     * @param mContext contexto
     * @param list clase del modelo
     * @param mRecyclerView contenendor del servicio
     * @param mFechaInicio fecha inicio
     * @param mFechaFin fecha final
     */
    public DirectorReporteSucursalesAdapter(Context mContext, List<DirectorReporteSucursalesModel> list, RecyclerView mRecyclerView, String mFechaInicio,String mFechaFin) {
        this.mContext = mContext;
        this.list = list;
        this.mRecyclerView = mRecyclerView;

        this.mFechaInicio = mFechaInicio;
        this.mFechaFin= mFechaFin;

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

    /**
     * @param parent acceso para determinar que tipo de XML mostrara
     * @return la vista XML de elementos o loading
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.director_fragmento_reporte_sucursales_lista, parent, false);
            vh = new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            vh = new LoadingViewHolder(view);
        }
        return vh;
    }

    /**
     * Inplementa el contenido consumido
     * @param holder accede a los elementos XML a mostrar
     * @param position posicion de cada elementos comparado con el servicio
     */
    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            final DirectorReporteSucursalesModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;
            myholder.campoLetra.setText(String.valueOf(String.valueOf(lista.getIdSucursal()).charAt(0)));
            myholder.campoIdSucursal.setText("Sucursal: " + lista.getIdSucursal());
            myholder.campoConCita.setText("" + lista.getConCita());
            myholder.campoSinCita.setText("" + lista.getSinCita() + " ");
            myholder.campoEmitidas.setText(" " + lista.getEmitido());
            myholder.campoNoEmitidas.setText(lista.getNoEmitido() + "");
            myholder.campoSaldoEmitido.setText("" +  Config.nf.format(lista.getSaldoNoEmetido()));
            myholder.campoSaldoNoEmitido.setText("" +  Config.nf.format(lista.getSaldoNoEmetido()));
            myholder.campoPorcentaje.setText("Porcentaje: Emitidos " + Config.df.format((float)(lista.getEmitido()*100)/(lista.getEmitido()+lista.getNoEmitido())) +"%  " + " | No emitidos " + Config.df.format((float)(lista.getNoEmitido()*100)/(lista.getEmitido()+lista.getNoEmitido()))+"%");
            myholder.subMenu.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v, lista);
                }
            });
        } else{
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    /**
     * @return el tamaño que del servicio REST
     */
    @Override
    public int getItemCount() {
        return list.size();
    }

    /**
     * @param position verifica la posicion de elementos, si existen o no
     * @return el tipo de vista
     */
    @Override
    public int getItemViewType(int position) {
        return list.get(position) ==null ? VIEW_TYPE_LOADING : VIEW_TYPE_ITEM;
    }

    /**
     * Muesta el menu cuando se hace click en los 3 botonos de la lista
     */
    private void surgirMenu(View view, DirectorReporteSucursalesModel lista) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_sucursal, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(lista));
        popup.show();
    }

    /**
     * Loading comienza como un valor falso
     */
    public void setLoaded() {
        isLoading = false;
    }

    /**
     * verifica si se ha consumido datos del servicio REST
     * @param mOnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        DirectorReporteSucursalesModel lista;

        public MyMenuItemClickListener(DirectorReporteSucursalesModel lista) {
            this.lista = lista;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sub_menu_reporte_sucusal_nav_asesores:
                    ReporteAsesores reporteAsesores = new ReporteAsesores();
                    Director d1 = (Director) mRecyclerView.getContext();
                    //fragment 1. fechaInicio 2. fechaFin 3.idGerencia 4.idSucursal 5.idAsesor 6.numeroEmpleado 7.nombreEmpleado 8.numeroCuenta 9.cita 10.hora 11.idTramite
                    d1.envioParametros(reporteAsesores, mFechaInicio, mFechaFin, 0, lista.idSucursal, "", "","", "", false, "", 0);
                    return true;
                case R.id.sub_menu_reporte_sucusal_nav_clientes:
                    ReporteClientes reporteClientes = new ReporteClientes();
                    Director d2 = (Director) mRecyclerView.getContext();
                    d2.envioParametros(reporteClientes, mFechaInicio, mFechaFin, 0, lista.idSucursal, "", "","", "", false, "", 0);
                    return true;
                case R.id.sub_menu_reporte_sucusal_nav_asistencia:
                    ReporteAsistencia reporteAsistencia = new ReporteAsistencia();
                    Director d3 = (Director) mRecyclerView.getContext();
                    d3.envioParametros(reporteAsistencia, mFechaInicio, mFechaFin, 0, lista.idSucursal, "", "","", "", false, "", 0);
                    return true;
                case R.id.sub_menu_reporte_sucusal_email:
                    ServicioEmailJSON.enviarEmailReporteSucursales(mContext, lista.idSucursal, mFechaInicio, mFechaFin, true);
                    return true;
                default:
            }
            return false;
        }
    }

    /**
     * mostrara XML al cargar contenido
     */
    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    /**
     * mostrara XML a settear dentro del RecyclerView
     */
    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView campoLetra, campoIdSucursal, campoConCita, campoSinCita, campoEmitidas, campoNoEmitidas, campoSaldoEmitido, campoSaldoNoEmitido, campoPorcentaje;
        public ImageView subMenu;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.dfrsl_tv_letra);
            campoIdSucursal = (TextView) view.findViewById(R.id.dfrsl_tv_id_sucursal);
            campoConCita = (TextView) view.findViewById(R.id.dfrsl_tv_con_cita);
            campoSinCita = (TextView) view.findViewById(R.id.dfrsl_tv_sin_cita);
            campoEmitidas = (TextView) view.findViewById(R.id.dfrsl_tv_emitidas);
            campoNoEmitidas = (TextView) view.findViewById(R.id.dfrsl_tv_no_emitidas);
            campoSaldoEmitido = (TextView) view.findViewById(R.id.dfrsl_tv_saldos_emitido);
            campoSaldoNoEmitido = (TextView) view.findViewById(R.id.dfrsl_tv_saldos_no_emitido);
            campoPorcentaje = (TextView) view.findViewById(R.id.dfrsl_tv_procentaje);
            subMenu = (ImageView) view.findViewById(R.id.dfrsl_tv_menu);
            cardView = (CardView) view.findViewById(R.id.dfrsl_cv);
        }
    }
}