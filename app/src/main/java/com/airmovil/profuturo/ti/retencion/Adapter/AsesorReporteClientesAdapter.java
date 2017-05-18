package com.airmovil.profuturo.ti.retencion.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ReporteClientesDetalle;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.AsesorReporteClientesModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 03/04/17.
 */

public class AsesorReporteClientesAdapter extends RecyclerView.Adapter{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<AsesorReporteClientesModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;
    private String mfechaInicio;
    private String mfechaFin;

    /**
     * Constructor
     * @param mContext contexto
     * @param list clase del modelo
     * @param mRecyclerView contenendor del servicio
     */
    public AsesorReporteClientesAdapter(Context mContext, List<AsesorReporteClientesModel> list, RecyclerView mRecyclerView, String mfechaInicio, String mfechaFin){
        this.mContext = mContext;
        this.list = list;
        this.mRecyclerView = mRecyclerView;
        this.mfechaInicio = mfechaInicio;
        this.mfechaFin = mfechaFin;

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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asesor_fragmento_reporte_clientes_lista, parent, false);
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
            final AsesorReporteClientesModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;
            myholder.campoNombre.setText(lista.getNombreCliente());
            myholder.campoCuenta.setText(lista.getNumeroCuenta());

            if(lista.getConCita() == "true")
                myholder.campoConCita.setText("Si");
            else
                myholder.campoConCita.setText("No");

            if(lista.getNoEmitido() == "true")
                myholder.campoNoEmitido.setText(" Si");
            else
                myholder.campoNoEmitido.setText(" No");

            char nombre = lista.getNombreCliente().charAt(0);
            final String pLetra = Character.toString(nombre);
            myholder.campoLetra.setText(pLetra);
            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(Config.conexion(mContext)){
                        fragmentoDatosCliente(v, lista.curp, lista.idTramite, lista.hora, mfechaInicio, mfechaFin);
                    }else{
                        Dialogos.dialogoErrorConexion(mContext);
                    }
                }
            });
        } else {
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }
    public void fragmentoDatosCliente(View view, String curp, int idTramite, String hora, String fechaInicio, String fechaFin){
        Fragment fragmento = new ReporteClientesDetalle();
        if(view.getContext() == null)
            return;
        if(view.getContext() instanceof Asesor){
            Asesor asesor = (Asesor) view.getContext();
            asesor.switchDetalleCliente(fragmento, curp, idTramite, hora, fechaInicio, fechaFin);
        }
    }

    /**
     * @return el tamaño que del servicio REST
     */
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

    public static class LoadingViewHolder extends RecyclerView.ViewHolder {
        public ProgressBar progressBar;

        public LoadingViewHolder(View itemView) {
            super(itemView);
            progressBar = (ProgressBar) itemView.findViewById(R.id.loading);
        }
    }

    public class MyViewHolder extends RecyclerView.ViewHolder{
        public TextView campoLetra, campoNombre, campoCuenta, campoConCita, campoNoEmitido;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.afrcl_tv_letra);
            campoNombre = (TextView) view.findViewById(R.id.afrcl_tv_nombre_cliente);
            campoCuenta = (TextView) view.findViewById(R.id.afrcl_tv_cuenta_cliente);
            campoConCita = (TextView) view.findViewById(R.id.afrcl_tv_con_cita);
            campoNoEmitido = (TextView) view.findViewById(R.id.afrcl_tv_no_emitido);
            cardView = (CardView) view.findViewById(R.id.afrcl_cv_lista);
        }
    }
}
