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
import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteGerenciasModel;
import java.util.List;
import com.airmovil.profuturo.ti.retencion.R;

/**
 * Created by tecnicoairmovil on 31/03/17.
 */

public class DirectorReporteGerenciasAdapter extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<DirectorReporteGerenciasModel> list;
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
    public DirectorReporteGerenciasAdapter(Context mContext, List<DirectorReporteGerenciasModel> list, RecyclerView mRecyclerView,  String mFechaInicio, String mFechaFin) {
        this.mContext = mContext;
        this.list = list;
        this.mRecyclerView = mRecyclerView;

        this.mFechaInicio = mFechaInicio;
        this.mFechaFin = mFechaFin;

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.director_fragmento_reporte_gerencias_lista, parent, false);
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
        if(holder instanceof  MyViewHolder){
            final DirectorReporteGerenciasModel lista = list.get(position);
            final MyViewHolder myViewHolder = (MyViewHolder) holder;
            myViewHolder.letra.setText(String.valueOf(String.valueOf(lista.getIdGerencia()).charAt(0)));
            myViewHolder.idGerencia.setText("Gerencia:" + lista.getIdGerencia());
            myViewHolder.conCita.setText(" " + lista.getConCita() + " ");
            myViewHolder.sinCita.setText(" " + lista.getSinCita() + " ");
            myViewHolder.retenido.setText(" " + lista.getEmitidas() + " ");
            myViewHolder.noRetenido.setText(" " + lista.getNoEmitidas() + " ");
            myViewHolder.saldoRetenido.setText(": " + Config.nf.format(lista.getdSaldoRetenido()) + " ");
            myViewHolder.saldoNoRetenido.setText(" " + Config.nf.format(lista.getdSaldoRetenido()) + " ");
            myViewHolder.porcentaje.setText("Porcentaje: Emitidos " + Config.df.format((float)(lista.getEmitidas()*100)/(lista.getEmitidas()+lista.getNoEmitidas())) +"%  " + " | No emitidos " + Config.df.format((float)(lista.getNoEmitidas()*100)/(lista.getEmitidas()+lista.getNoEmitidas()))+"%");
            myViewHolder.tvClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v, lista);
                }
            });
        }else{
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
     * Loading comienza como un valor falso
     */
    public void setLoaded() {
        isLoading = false;
    }

    /**
     * Muesta el menu cuando se hace click en los 3 botonos de la lista
     */
    private void surgirMenu(View view, DirectorReporteGerenciasModel list) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_gerencia, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(list));
        popup.show();
    }

    /**
     * verifica si se ha consumido datos del servicio REST
     * @param mOnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    /**
     * escucha el popup al dar click
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        DirectorReporteGerenciasModel list;
        public MyMenuItemClickListener(DirectorReporteGerenciasModel list) {
            this.list = list;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {

                case R.id.nav_sucursales:
                    ReporteSucursales reporteSucursales = new ReporteSucursales();
                    Director d1 = (Director) mRecyclerView.getContext();
                    //fragment 1. fechaInicio 2. fechaFin 3.idGerencia 4.idSucursal 5.idAsesor 6.numeroEmpleado 7.nombreEmpleado 8.numeroCuenta 9.cita 10.hora 11.idTramite
                    if (Config.conexion(mContext))
                        d1.envioParametros(reporteSucursales, mFechaInicio, mFechaFin, list.idGerencia, 0, "", "","", "", false, "", 0);
                    else
                        Dialogos.dialogoErrorConexion(mContext);
                    return true;
                case R.id.nav_asesor:
                    ReporteAsesores reporteAsesores = new ReporteAsesores();
                    Director d2 = (Director) mRecyclerView.getContext();
                    if (Config.conexion(mContext))
                        d2.envioParametros(reporteAsesores, mFechaInicio, mFechaFin, list.idGerencia, 0, "", "","", "", false, "", 0);
                    else
                        Dialogos.dialogoErrorConexion(mContext);
                    return true;
                case R.id.nav_clientes:
                    ReporteClientes reporteClientes = new ReporteClientes();
                    Director d3 = (Director) mRecyclerView.getContext();
                    if (Config.conexion(mContext))
                        d3.envioParametros(reporteClientes, mFechaInicio, mFechaFin, list.idGerencia, 0, "", "","", "", false, "", 0);
                    else
                        Dialogos.dialogoErrorConexion(mContext);
                    return true;
                case R.id.nav_enviar_a_email:
                    if (Config.conexion(mContext))
                        ServicioEmailJSON.enviarEmailReporteGerencias(mContext, true, list.idGerencia, mFechaInicio, mFechaFin);
                    else
                        Dialogos.dialogoErrorConexion(mContext);
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
        public TextView letra, idGerencia, conCita, sinCita, retenido, noRetenido, saldoRetenido, saldoNoRetenido, porcentaje;
        public CardView cardView;
        public ImageView tvClick;
        public MyViewHolder(View view){
            super(view);
            letra = (TextView) view.findViewById(R.id.dfrgl_tv_letra);
            idGerencia = (TextView) view.findViewById(R.id.dfrgl_tv_id_gerencia);
            conCita = (TextView) view.findViewById(R.id.dfrgl_tv_con_cita);
            sinCita = (TextView) view.findViewById(R.id.dfrgl_tv_sin_cita);
            retenido = (TextView) view.findViewById(R.id.dgrgl_tv_emitido);
            noRetenido = (TextView) view.findViewById(R.id.dgrgl_tv_no_emitido);
            saldoRetenido = (TextView) view.findViewById(R.id.dfrgl_tv_con_saldo);
            saldoNoRetenido = (TextView) view.findViewById(R.id.dfrgl_tv_sin_saldo);
            porcentaje = (TextView) view.findViewById(R.id.dfrgl_tv_porcentaje);
            tvClick = (ImageView) view.findViewById(R.id.dfrcll_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.ddfrgl_cardview);
        }
    }
}
