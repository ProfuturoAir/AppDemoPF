package com.airmovil.profuturo.ti.retencion.Adapter;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.activities.Director;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.Encuesta2;
import com.airmovil.profuturo.ti.retencion.directorFragmento.Inicio;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteAsesores;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteGerencias;
import com.airmovil.profuturo.ti.retencion.directorFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.DirectorReporteGerenciasModel;

import java.util.List;

import com.airmovil.profuturo.ti.retencion.R;

/**
 * Created by cesarriver on 31/03/17.
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

    private BaseAdapter mAdapter;

    public DirectorReporteGerenciasAdapter(Context mContext, List<DirectorReporteGerenciasModel> list, RecyclerView mRecyclerView) {
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
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.director_fragmento_reporte_gerencias_lista, parent, false);
            vh = new MyViewHolder(view);
        }else{
            View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.loading, parent, false);
            vh = new LoadingViewHolder(view);
        }
        return vh;
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if(holder instanceof  MyViewHolder){
            final DirectorReporteGerenciasModel lista = list.get(position);
            final MyViewHolder myViewHolder = (MyViewHolder) holder;

            //char nombre = lista.getIdGerencia().charAt(0);
            //final String pLetra = Character.toString(nombre);

            //myViewHolder.letra.setText(pLetra);
            myViewHolder.idGerencia.setText("Gerencia " + lista.getIdGerencia());
            myViewHolder.conCita.setText(" " + lista.getConCita() + " ");
            myViewHolder.sinCita.setText(" " + lista.getSinCita() + " ");
            myViewHolder.tvClick.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v);
                }
            });

            myViewHolder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    fragmentJumpDatosUsuario("", v);
                }
            });
        }else{
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

    public void fragmentJumpDatosUsuario(String idClienteCuenta, View view) {
        Fragment fragmento = new ReporteSucursales();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Director) {
            Director director = (Director) view.getContext();

            final Connected conected = new Connected();
            if(conected.estaConectado(view.getContext())) {

            }else{
                Config.msj(view.getContext(),"Error conexión", "Sin Conexion por el momento.Cliente P-1.1.3");
            }
            director.switchContent(fragmento, idClienteCuenta);
        }
    }

    public void setOnLoadMoreListener(OnLoadMoreListener mOnLoadMoreListener) {
        this.mOnLoadMoreListener = mOnLoadMoreListener;
    }

    public void fragmentJumpDatos(View view, String id) {
        Fragment fragmento = new ReporteSucursales();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Director) {
            Director director = (Director) view.getContext();

            final Connected conected = new Connected();
            if(conected.estaConectado(view.getContext())) {

            }else{
                Config.msj(view.getContext(),"Error conexión", "Sin Conexion por el momento.Cliente P-1.1.3");
            }
            director.switchContent(fragmento, id);
        }
    }


    /**
     * escucha el popup al dar click
     */
    class MyMenuItemClickListener implements PopupMenu.OnMenuItemClickListener {

        public MyMenuItemClickListener() {
        }
        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.nav_sucursales:
                    Toast.makeText(mContext, "Sucursales", Toast.LENGTH_SHORT).show();
                    AppCompatActivity sucursales = (AppCompatActivity) mRecyclerView.getContext();
                    ReporteSucursales fragmentoSucursales = new ReporteSucursales();
                    //Create a bundle to pass data, add data, set the bundle to your fragment and:
                    sucursales.getSupportFragmentManager().beginTransaction().replace(R.id.content_director, fragmentoSucursales).addToBackStack(null).commit();
                    return true;
                case R.id.nav_asesores:
                    Toast.makeText(mContext, "Asesores", Toast.LENGTH_SHORT).show();
                    AppCompatActivity asesores = (AppCompatActivity) mRecyclerView.getContext();
                    ReporteAsesores fragmentoAsesores = new ReporteAsesores();
                    //Create a bundle to pass data, add data, set the bundle to your fragment and:
                    asesores.getSupportFragmentManager().beginTransaction().replace(R.id.content_director, fragmentoAsesores).addToBackStack(null).commit();
                    return true;
                case R.id.nav_clientes:
                    Toast.makeText(mContext, "Clientes", Toast.LENGTH_SHORT).show();
                    AppCompatActivity Clientes = (AppCompatActivity) mRecyclerView.getContext();
                    ReporteClientes fragmentoClientes = new ReporteClientes();
                    //Create a bundle to pass data, add data, set the bundle to your fragment and:
                    Clientes.getSupportFragmentManager().beginTransaction().replace(R.id.content_director, fragmentoClientes).addToBackStack(null).commit();
                    return true;
                case R.id.nav_enviar_a_email:
                    Context context;
                    Config.msj(mContext, "Enviar mensaje de email", "se enviara el mensaje de email");
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
        public TextView letra, idGerencia, conCita, sinCita, emitidas, saldos;
        public CardView cardView;
        public TextView tvClick;
        public MyViewHolder(View view){
            super(view);
            letra = (TextView) view.findViewById(R.id.dfrgl_tv_letra);
            idGerencia = (TextView) view.findViewById(R.id.dfrgl_tv_id_gerencia);
            conCita = (TextView) view.findViewById(R.id.dfrgl_tv_con_cita);
            sinCita = (TextView) view.findViewById(R.id.dfrgl_tv_sin_cita);
            tvClick = (TextView) view.findViewById(R.id.dfrcll_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.ddfrgl_cardview);
        }
    }
}
