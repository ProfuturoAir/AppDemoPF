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
import com.airmovil.profuturo.ti.retencion.fragmento.Biblioteca;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.DatosAsesor;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteSinCitaModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 23/03/17.
 */

public class GerenteSinCitaAdapter extends RecyclerView.Adapter{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<GerenteSinCitaModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    public GerenteSinCitaAdapter(Context mContext, List<GerenteSinCitaModel> list, RecyclerView mRecyclerView) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gerente_fragmento_sin_cita_item, parent, false);
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
            final GerenteSinCitaModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoNombre.setText(lista.getClienteNombre());
            myholder.campoCuenta.setText(lista.getClienteCuenta());
            char nombre = lista.getClienteNombre().charAt(0);
            final String pLetra = Character.toString(nombre);
            myholder.campoLetra.setText(pLetra);

            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentJumpDatosUsuario(pLetra, v,lista.getClienteNombre(),lista.getClienteCuenta());
                }
            });
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



    public void fragmentJumpDatosUsuario(String idClienteCuenta, View view,String nombre,String numeroDeCuenta) {
        Fragment fragmento = new DatosAsesor();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Gerente) {
            Gerente gerente = (Gerente) view.getContext();

            final Connected conected = new Connected();
            if(conected.estaConectado(view.getContext())) {

            }else{
                Config.msj(view.getContext(),"Error conexión", "Sin Conexion por el momento.Cliente P-1.1.3");
            }
            //gerente.switchContent(fragmento, idClienteCuenta);
            gerente.switchDatosAsesor(fragmento, idClienteCuenta,nombre,numeroDeCuenta);
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
        public TextView campoLetra, campoNombre, campoCuenta;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.gfsci_tv_letra);
            campoNombre = (TextView) view.findViewById(R.id.gfsci_tv_nombre_cliente);
            campoCuenta = (TextView) view.findViewById(R.id.gfsci_tv_cuenta_cliente);
            cardView = (CardView) view.findViewById(R.id.gfsci_cv);
        }
    }
}
