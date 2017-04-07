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
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteSucursalesModel;

import java.util.List;

/**
 * Created by tecnicoairmovil on 04/04/17.
 */

public class GerenteReporteSucursalesAdapter extends RecyclerView.Adapter{
    private final int VIEW_TYPE_ITEM = 0;
    private final int VIEW_TYPE_LOADING = 1;
    private Context mContext;
    private List<GerenteReporteSucursalesModel> list;
    private OnLoadMoreListener mOnLoadMoreListener;

    private boolean isLoading;
    private int visibleThreshold = 10;
    private int lastVisibleItem, totalItemCount;
    private RecyclerView mRecyclerView;

    public GerenteReporteSucursalesAdapter(Context mContext, List<GerenteReporteSucursalesModel> list, RecyclerView mRecyclerView) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gerente_fragmento_reporte_sucursales_lista, parent, false);
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
            final GerenteReporteSucursalesModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoIdSucursal.setText("Sucursal: " + lista.getIdSucursal());
            myholder.campoConCita.setText(" " + lista.getConCita());
            myholder.campoSinCita.setText(" " + lista.getSinCita());
            myholder.campoEmitidas.setText(" " + lista.getEmitido());
            myholder.campoNoEmitidas.setText( lista.getNoEmitido() + " ");
            myholder.campoSaldoEmitido.setText(" " + lista.getSaldoEmitido());
            //myholder.campoSaldoNoEmitido.setText(lista.getSaldoNoEmetido());

            int var = lista.getIdSucursal();
            String intToString = String.valueOf(var);
            char dato = intToString.charAt(0);
            final String inicial = Character.toString(dato);

            myholder.campoLetra.setText(inicial);

            myholder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v);
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

    /**
     * Muesta el menu cuando se hace click en los 3 botonos de la lista
     */
    private void surgirMenu(View view) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_gerencia, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener());
        popup.show();
    }

    public void setLoaded() {
        isLoading = false;
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }


    /**
     * Click listener for popup menu items
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {

        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_sucursales:
                    Toast.makeText(mContext, "Sucursales", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_asesores:
                    Toast.makeText(mContext, "Asesores", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_clientes:
                    Toast.makeText(mContext, "Clientes", Toast.LENGTH_SHORT).show();
                    return true;
                case R.id.nav_enviar_a_email:
                    Toast.makeText(mContext, "Email", Toast.LENGTH_SHORT).show();
                    return true;
                default:
            }
            return false;
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
        public TextView campoLetra, campoIdSucursal, campoConCita, campoSinCita, campoEmitidas, campoNoEmitidas, campoSaldoEmitido, campoSaldoNoEmitido;
        public Button btn;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.gfrsl_tv_letra);
            campoIdSucursal = (TextView) view.findViewById(R.id.afrcl_tv_id_sucursal);
            campoConCita = (TextView) view.findViewById(R.id.gfrsl_tv_con_cita);
            campoSinCita = (TextView) view.findViewById(R.id.gfrsl_tv_sin_cita);
            campoEmitidas = (TextView) view.findViewById(R.id.gfrsl_tv_emitidas);
            campoNoEmitidas = (TextView) view.findViewById(R.id.gfrsl_tv_no_emitidas);
            campoSaldoEmitido = (TextView) view.findViewById(R.id.gfrsl_tv_saldos_emitido);
            campoSaldoNoEmitido = (TextView) view.findViewById(R.id.gfrsl_tv_saldos_no_emitido);
            btn = (Button) view.findViewById(R.id.gfrcl_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.gfrsl_cv);
        }
    }
}
