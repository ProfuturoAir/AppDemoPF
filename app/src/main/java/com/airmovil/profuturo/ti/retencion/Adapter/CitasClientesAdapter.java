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
import com.airmovil.profuturo.ti.retencion.asesorFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.CitasClientesModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 22/03/17.
 */

public class CitasClientesAdapter extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<CitasClientesModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    public CitasClientesAdapter(Context mContext, List<CitasClientesModel> list, RecyclerView mRecyclerView){
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.asesor_fragmento_con_cita_lista, parent, false);
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

            final CitasClientesModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoNombre.setText(lista.getNombreCliente());
            myholder.campoCuenta.setText(lista.getNumeroCuenta());
            myholder.campoHora.setText(lista.getHora());

            char nombre = lista.getNombreCliente().charAt(0);
            final String pLetra = Character.toString(nombre);

            myholder.campoLetra.setText(pLetra);

            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(lista.getNumeroCuenta().isEmpty()){
                        Dialogos.dialogoNoExisteDato(mContext);
                    }else{
                        fragmentJumpDatosUsuario(pLetra, v,lista.getNombreCliente(),lista.getNumeroCuenta(),lista.getHora());
                    }
                }
            });

        } else {
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

    public void fragmentJumpDatosUsuario(String idClienteCuenta, View view,String nombre,String numeroDeCuenta,String hora) {
        Fragment fragmento = new DatosAsesor();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Asesor) {
            Asesor asesor = (Asesor) view.getContext();

            final Connected conected = new Connected();
            asesor.switchDatosAsesor(fragmento, idClienteCuenta,nombre,numeroDeCuenta,hora);
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
        public TextView campoLetra, campoNombre, campoCuenta, campoHora;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.afccl_tv_letra);
            campoNombre = (TextView) view.findViewById(R.id.afccl_tv_nombre_cliente);
            campoCuenta = (TextView) view.findViewById(R.id.afccl_tv_cuenta_cliente);
            campoHora = (TextView) view.findViewById(R.id.afccl_tv_fecha_cita);
            cardView = (CardView) view.findViewById(R.id.cardView_item_cita);
        }
    }
}
