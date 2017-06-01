package com.airmovil.profuturo.ti.retencion.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;
import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistenciaDetalles;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.helper.ServicioEmailJSON;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteAsistenciaModel;
import java.util.List;

/**
 * Created by tecnicoairmovil on 22/03/17.
 */

public class GerenteReporteAsistenciaAdapter extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<GerenteReporteAsistenciaModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;
    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;
    private String mFechaInicio = "";
    private String mFechaFin = "";

    /**
     * Constructor
     * @param mContext contexto
     * @param list clase del modelo
     * @param mRecyclerView contenendor del servicio
     * @param mFechaInicio fecha inicio
     * @param mFechaFin fecha final
     */
    public GerenteReporteAsistenciaAdapter(Context mContext, List<GerenteReporteAsistenciaModel> list, RecyclerView mRecyclerView, String mFechaInicio, String mFechaFin){
        this.mContext = mContext;
        this.mFechaInicio = mFechaInicio;
        this.mFechaFin = mFechaFin;
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

    /**
     * @param parent acceso para determinar que tipo de XML mostrara
     * @return la vista XML de elementos o loading
     */
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        RecyclerView.ViewHolder vh;
        if (viewType == VIEW_TYPE_ITEM) {
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gerente_fragmento_reporte_asistencia_lista, parent, false);
            vh = new MyViewHolder(view);
        } else {
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
        if (holder instanceof MyViewHolder) {
            final GerenteReporteAsistenciaModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;
            myholder.campoLetra.setText(String.valueOf(String.valueOf(lista.getNombre()).charAt(0)));
            myholder.campoNombreAsesor.setText("Asesor: " + lista.getNombre());
            myholder.campoNumeroCuentaAsesor.setText("Sucursal: " + lista.getIdSucursal());
            myholder.campoSucursalAsesor.setText("Numero de empleado: " + lista.getnEmpleado());

            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    Fragment fragmento = new ReporteAsistenciaDetalles();
                    if (mContext instanceof Gerente) {
                        if(Config.conexion(mContext)) {
                            Gerente gerente = (Gerente) mContext;
                            //fragment 1. fechaInicio 2. fechaFin 3.idGerencia 4.idSucursal 5.idAsesor 6.numeroEmpleado 7.nombreEmpleado 8.numeroCuenta 9.cita 10.hora 11.idTramite
                            gerente.envioParametros(fragmento, mFechaInicio, mFechaFin, 0, 0, "", lista.getnEmpleado(), lista.getNombre(), "", false, "", 0);
                        }else{
                            Dialogos.dialogoErrorConexion(mContext);
                        }
                    }
                }
            });

            myholder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v, lista);
                }
            });
        } else {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    /**
     * Muesta el menu cuando se hace click en los 3 botonos de la lista
     */
    private void surgirMenu(View view, GerenteReporteAsistenciaModel lista) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_asistencia, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(lista, view));
        popup.show();
    }

    /**
     * escucha el popup al dar click
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {
        GerenteReporteAsistenciaModel lista;
        View view;
        public MyMenuItemClickListener(GerenteReporteAsistenciaModel lista, View view) {
            this.lista = lista;
            this.view = view;
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            if(Config.conexion(mContext)) {
                switch (item.getItemId()) {
                    case R.id.sub_menu_reporte_asistencia_nav_detalle_asistencia:
                        Fragment fragmento = new ReporteAsistenciaDetalles();
                        if (mContext instanceof Gerente) {
                            Gerente gerente = (Gerente) mContext;
                            gerente.envioParametros(fragmento, mFechaInicio, mFechaFin, 0, 0, "", String.valueOf(lista.getnEmpleado()), lista.getNombre(), "", false, "", 0);
                        }
                        return true;
                    case R.id.sub_menu_reporte_asistencia_email:
                        ServicioEmailJSON.enviarEmailReporteAsistencia(mContext, 0, lista.getIdSucursal(), lista.getnEmpleado(), mFechaInicio, mFechaFin, true);
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
     * verifica si se ha consumido datos del servicio REST
     * @param mOnLoadMoreListener
     */
    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
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
        public TextView campoLetra, campoNombreAsesor, campoNumeroCuentaAsesor, campoSucursalAsesor;
        public CardView cardView;
        public TextView btn;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.ggfrasl_tv_letra);
            campoNombreAsesor = (TextView) view.findViewById(R.id.ggfrasl_tv_nombre_asesor);
            campoNumeroCuentaAsesor = (TextView) view.findViewById(R.id.ggfrasl_tv_numero_empleado_asesor);
            campoSucursalAsesor = (TextView) view.findViewById(R.id.ggfrasl_tv_numero_sucursal_asesor);
            btn = (TextView) view.findViewById(R.id.ggfrasl_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.ggfrasl_cv);
        }
    }
}
