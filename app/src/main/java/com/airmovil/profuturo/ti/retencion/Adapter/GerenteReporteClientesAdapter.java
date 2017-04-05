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

    public GerenteReporteClientesAdapter(Context mContext, List<GerenteReporteClientesModel> list, RecyclerView mRecyclerView) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.gerente_fragmento_reporte_clientes_lista, parent, false);
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
            final GerenteReporteClientesModel lista = list.get(position);
            final MyViewHolder myholder = (MyViewHolder) holder;

            myholder.campoNombreCliente.setText("Asesor: " + lista.getNombreCliente());
            myholder.campoCuentaCliente.setText(" " + lista.getNumeroCuenta());
            myholder.campoAsesorCliente.setText(" " + lista.getNumeroEmpleado());
            myholder.campoNoRetenidoCliente.setText(lista.getRetenido());
            //myholder.campoSucursalCliente.setText(lista.getIdSucursal());
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
        inflater.inflate(R.menu.menu_detalle_reporte_sucursales, popup.getMenu());
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
        public TextView campoLetra, campoNombreCliente, campoCuentaCliente, campoAsesorCliente, campoConCitaCliente, campoNoRetenidoCliente, campoSucursalCliente;
        public Button btn;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.gfrcll_tv_letra);
            campoNombreCliente = (TextView) view.findViewById(R.id.gfrcll_tv_nombre_cliente);
            campoCuentaCliente = (TextView) view.findViewById(R.id.gfrcll_tv_cuenta_cliente);
            campoAsesorCliente = (TextView) view.findViewById(R.id.gfrcll_tv_asesor_cliente);
            campoConCitaCliente = (TextView) view.findViewById(R.id.gfrcll_tv_con_cita_cliente);
            campoNoRetenidoCliente = (TextView) view.findViewById(R.id.gfrcll_tv_retenidos_cliente);
            campoSucursalCliente = (TextView) view.findViewById(R.id.gfrcll_tv_sucursal_cliente);
            btn = (Button) view.findViewById(R.id.gfrcll_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.gfrcll_cv);
        }
    }
}
