package com.airmovil.profuturo.ti.retencion.Adapter;

import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Asesor;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteAsistenciaDetalleModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 23/03/17.
 */

public class DirectorReporteAsistenciaDetalleAdapter extends RecyclerView.Adapter{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<DirectorReporteAsistenciaDetalleModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    public DirectorReporteAsistenciaDetalleAdapter(Context mContext, List<DirectorReporteAsistenciaDetalleModel> list, RecyclerView mRecyclerView) {
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
        if(viewType == VIEW_TYPE_ITEM){
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.director_fragmento_reporte_asistencia_detalles_lista, parent, false);
            vh = new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            vh = new LoadingViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof MyViewHolder){
            final DirectorReporteAsistenciaDetalleModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoFechaDetalle.setText(lista.getFechaAsistencia());
            myholder.campoEntradasHoraDetalle.setText(lista.getEntradaHora());
            myholder.campoEntradasCoordenadasDetalle.setText("Coordenadas: " + lista.getEntradaLatitud() + ", " + lista.getEntradaLongitud());

            myholder.campoHoraComidasDetalle.setText(lista.getComidaHora() + " - " + lista.getComidaSalida());
            myholder.campoComidasCoordenadasDetalle.setText("Coordenadas: " + lista.getComidaLatitud() + ", " + lista.getComidaLongitud());

            myholder.campoHoraSalidasDetalle.setText(lista.getSalidaHora());
            myholder.campoSalidasCoordenadasDetalle.setText("Coordenadas: " + lista.getSalidaLatitud() + ", " + lista.getSalidaLongitud());

            /*myholder.campoNombre.setText(lista.getClienteNombre());
            myholder.campoCuenta.setText(lista.getClienteCuenta());
            char nombre = lista.getClienteNombre().charAt(0);
            final String pLetra = Character.toString(nombre);
            myholder.campoLetra.setText(pLetra);

            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentJumpDatosUsuario(pLetra, v);
                }
            });*/
        } else{
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
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
        Fragment fragmento = new DatosAsesor();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Asesor) {
            Asesor asesor = (Asesor) view.getContext();

            final Connected conected = new Connected();
            if(conected.estaConectado(view.getContext())) {

            }else{
                Config.msj(view.getContext(),"Error conexión", "Sin Conexion por el momento.Cliente P-1.1.3");
            }
            asesor.switchContent(fragmento, idClienteCuenta);
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
        public TextView campoFechaDetalle;
        public TextView campoEntradasHoraDetalle, campoEntradasCoordenadasDetalle;
        public TextView campoHoraComidasDetalle, campoComidasCoordenadasDetalle;
        public TextView campoHoraSalidasDetalle, campoSalidasCoordenadasDetalle;
        public MyViewHolder(View view){
            super(view);
            campoFechaDetalle = (TextView) view.findViewById(R.id.ddfrasdl_tv_fecha);
            campoEntradasHoraDetalle = (TextView) view.findViewById(R.id.ddfrasdl_tv_entrada_hora);
            campoEntradasCoordenadasDetalle = (TextView) view.findViewById(R.id.ddfrasdl_tv_entrada_coordenadas);
            campoHoraComidasDetalle = (TextView) view.findViewById(R.id.ddfrasdl_tv_comida_hora);
            campoComidasCoordenadasDetalle = (TextView) view.findViewById(R.id.ddfrasdl_tv_comida_coordenadas);
            campoHoraSalidasDetalle = (TextView) view.findViewById(R.id.ddfrasdl_tv_salida_hora);
            campoSalidasCoordenadasDetalle= (TextView) view.findViewById(R.id.ddfrasdl_tv_salida_coordenadas);
        }
    }
}
