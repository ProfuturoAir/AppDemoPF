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
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientesDetalles;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteClientesModel;
import java.util.List;

/**
 * Created by tecnicoairmovil on 04/04/17.
 */

public class GerenteReporteClientesAdapter extends RecyclerView.Adapter{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<GerenteReporteClientesModel> list;
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
    public GerenteReporteClientesAdapter(Context mContext, List<GerenteReporteClientesModel> list, RecyclerView mRecyclerView, String mFechaInicio, String mFechaFin) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gerente_fragmento_reporte_clientes_lista, parent, false);
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
            final GerenteReporteClientesModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;
            myholder.campoNombreCliente.setText("Nombre del cliente: " + lista.getNombreCliente());
            myholder.campoCuentaCliente.setText("Número de cuenta: " + lista.getNumeroCuenta());
            myholder.campoAsesorCliente.setText("Asesor: " + lista.getNumeroEmpleado() + " ");
            myholder.campoConCitaCliente.setText(" " + (Boolean.parseBoolean(lista.getCita()) ? " Con cita " : " "));
            myholder.campoSinCitaCliente.setText(" " + (Boolean.parseBoolean(lista.getCita()) ? " " : " Sin cita "));
            myholder.campoRetenidoCliente.setText(" " + (Boolean.parseBoolean(lista.getRetenido()) ? " Retenido " : " "));
            myholder.campoNoRetenidoCliente.setText(" " + (Boolean.parseBoolean(lista.getRetenido()) ? " " : " No Retenido "));
            myholder.campoSucursalCliente.setText(" Sucursal: " + lista.getIdSucursal() + " ");
            myholder.campoLetra.setText(String.valueOf(String.valueOf(lista.getNombreCliente()).charAt(0)));
            myholder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v, lista);
                }
            });
            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Config.conexion(mContext)) {
                        ReporteClientesDetalles reporteClientesDetalles = new ReporteClientesDetalles();
                        Gerente g1 = (Gerente) v.getContext();
                        g1.envioParametros(reporteClientesDetalles, mFechaInicio, mFechaFin, 0, 0, "", lista.getNumeroEmpleado(), lista.nombreAsesor,
                                lista.getNumeroCuenta(), Boolean.parseBoolean(lista.getCita()), lista.getHora(), lista.getTramite());
                    }else{
                        Dialogos.dialogoErrorConexion(mContext);
                    }
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
    private void surgirMenu(View view, GerenteReporteClientesModel list) {
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_clientes, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(list, view));
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
        GerenteReporteClientesModel list;
        View view;
        public MyMenuItemClickListener(GerenteReporteClientesModel list, View view) {
            this.list = list;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(Config.conexion(mContext)) {
                switch (item.getItemId()) {
                    case R.id.sub_menu_reporte_clientes_detalles:
                        ReporteClientesDetalles reporteClientesDetalles = new ReporteClientesDetalles();
                        Gerente d1 = (Gerente) mRecyclerView.getContext();
                        //fragment 1.fechaInicio 2.fechaFin 3.idGerencia 4.idSucursal 5.idAsesor 6.numeroEmpleado 7.nombreEmpleado 8.numeroCuenta 9.cita 10.hora 11.idTramite
                        d1.envioParametros(reporteClientesDetalles, mFechaInicio, mFechaFin, 0, 0, "", list.getNumeroEmpleado(), list.nombreAsesor, list.getNumeroCuenta(), Boolean.parseBoolean(list.getCita()), list.getHora(), list.getIdTramite());
                        return true;
                    case R.id.sub_menu_reporte_clientes_email:
                        ServicioEmailJSON.enviarEmailReporteClientes(mContext, list.getIdSucursal(), 2, (list.getCita() == "true") ? 1 : 2, list.getNumeroCuenta(), (list.getRetenido() == "true") ? 1 : 2, list.getNumeroEmpleado(), mFechaInicio, mFechaFin, true);
                        return true;
                    default:
                }
            }else{
                Dialogos.dialogoErrorConexion(mContext);
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
        public TextView campoLetra, campoNombreCliente, campoCuentaCliente, campoAsesorCliente, campoConCitaCliente, campoSinCitaCliente, campoRetenidoCliente, campoNoRetenidoCliente, campoSucursalCliente;
            public ImageView btn;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.gfrcll_tv_letra);
            campoNombreCliente = (TextView) view.findViewById(R.id.gfrcll_tv_nombre_cliente);
            campoCuentaCliente = (TextView) view.findViewById(R.id.gfrcll_tv_cuenta_cliente);
            campoAsesorCliente = (TextView) view.findViewById(R.id.gfrcll_tv_asesor_cliente);
            campoConCitaCliente = (TextView) view.findViewById(R.id.gfrcll_tv_con_cita_cliente);
            campoSinCitaCliente = (TextView) view.findViewById(R.id.gfrcll_tv_sin_cita_cliente);
            campoRetenidoCliente = (TextView) view.findViewById(R.id.gfrcll_tv_retenidos_cliente);
            campoNoRetenidoCliente = (TextView) view.findViewById(R.id.gfrcll_tv_no_retenidos_cliente);
            campoSucursalCliente = (TextView) view.findViewById(R.id.gfrcll_tv_sucursal_cliente);
            btn = (ImageView) view.findViewById(R.id.gfrcll_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.gfrcll_cv);
        }
    }
}
