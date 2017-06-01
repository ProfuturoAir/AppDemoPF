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
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.Dialogos;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteConCitaClientes;

import java.util.List;

/**
 * Created by tecnicoairmovil on 22/03/17.
 */

public class GerenteCitasClientesAdapter extends RecyclerView.Adapter {

    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<GerenteConCitaClientes> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    public GerenteCitasClientesAdapter(Context mContext, List<GerenteConCitaClientes> list, RecyclerView mRecyclerView){
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gerente_fragmento_con_cita_lista, parent, false);
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

            final GerenteConCitaClientes lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoNombre.setText("Nombre del cliente: " + lista.getNombreCliente());
            myholder.campoCuenta.setText("Número de cuenta: " + lista.getNumeroCuenta());
            myholder.campoHora.setText("Hora: " + lista.getHora());
            myholder.campoLetra.setText(Character.toString(lista.getNombreCliente().charAt(0)));

            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    if(lista.getNumeroCuenta().isEmpty()){
                        Dialogos.dialogoNoExisteDato(mContext);
                    }else{
                        fragmentJumpDatosUsuario(v,lista.getNombreCliente(),lista.getNumeroCuenta(),lista.getHora());
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

    public void fragmentJumpDatosUsuario(View view,String nombre,String numeroDeCuenta,String hora) {
        Fragment fragmento = new DatosAsesor();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Gerente) {
            Gerente asesor = (Gerente) view.getContext();

            final Connected conected = new Connected();
            asesor.parametrosDetalle(fragmento, 0,nombre,numeroDeCuenta,hora, "", "", "", "", "", "", "", "", "", false, false, false, "", "","","", "", "", "", "", "");
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
            campoLetra = (TextView) view.findViewById(R.id.gfccl_tv_letra);
            campoNombre = (TextView) view.findViewById(R.id.gfccl_tv_nombre_cliente);
            campoCuenta = (TextView) view.findViewById(R.id.gfccl_tv_cuenta_cliente);
            campoHora = (TextView) view.findViewById(R.id.gfccl_tv_fecha_cita);
            cardView = (CardView) view.findViewById(R.id.gcardView_item_cita);
        }
    }
}
