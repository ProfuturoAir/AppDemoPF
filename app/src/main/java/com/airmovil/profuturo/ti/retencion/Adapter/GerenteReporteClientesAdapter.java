package com.airmovil.profuturo.ti.retencion.Adapter;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.PopupMenu;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.activities.Gerente;
import com.airmovil.profuturo.ti.retencion.asesorFragmento.ReporteClientesDetalle;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientesDetalles;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteSucursales;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistenciaDetalles;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteClientesModel;

import org.json.JSONException;
import org.json.JSONObject;

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
    private String fechaInicio;
    private String fechaFin;

    public GerenteReporteClientesAdapter(Context mContext, List<GerenteReporteClientesModel> list, RecyclerView mRecyclerView, String fechaInicio, String fechaFin) {
        this.mContext = mContext;
        this.list = list;
        this.mRecyclerView = mRecyclerView;

        this.fechaInicio = fechaInicio;
        this.fechaFin = fechaFin;
        Log.d("SIGUE","AQUI ->");

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
            myholder.campoNombreCliente.setText("Nombre del cliente: " + lista.getNombreCliente());
            myholder.campoCuentaCliente.setText("Número de cuenta: " + lista.getNumeroCuenta());
            myholder.campoAsesorCliente.setText("Asesor: " + lista.getNumeroEmpleado());
            myholder.campoConCitaCliente.setText((Boolean.parseBoolean(lista.getCita()) ? "Si" : "No"));
            myholder.campoNoRetenidoCliente.setText((Boolean.parseBoolean(lista.getRetenido()) ? "Retenido" : "No Retenido"));
            myholder.campoSucursalCliente.setText("Sucursal" + lista.getIdSucursal());

            int var = lista.getIdSucursal();
            String intToString = String.valueOf(var);
            char dato = intToString.charAt(0);
            final String inicial = Character.toString(dato);

            myholder.btn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    surgirMenu(v, lista);
                }
            });

            myholder.campoLetra.setText(inicial);
            myholder.cardView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {


                    fragmentJumpDatosUsuario(lista.getIdSucursal(), lista.getTramite(), lista.getNumeroCuenta(),  fechaInicio, fechaFin, Config.usuarioCusp(mContext),lista.getNombreCliente(), lista.getNombreAsesor(), v);
                }
            });
        } else{
            ((LoadingViewHolder) holder).progressBar.setIndeterminate(true);
        }
    }

    public void fragmentJumpDatosUsuario(int idSucursal, int idTramite, String numeroCuenta, String fechaInicio,String fechaFin, String usuario,String nombreCliente, String nombreAsesor, View view) {
        Fragment fragmento = new ReporteClientesDetalles();
        if (view.getContext() == null)
            return;
        if (view.getContext() instanceof Gerente) {
            Gerente gerente = (Gerente) view.getContext();

            final Connected conected = new Connected();
            if(conected.estaConectado(view.getContext())) {

            }else{
                Config.msj(view.getContext(),"Error conexión", "Sin Conexion por el momento.Cliente P-1.1.3");
            }
            gerente.switchDetalleClientes(idSucursal, idTramite, numeroCuenta,  fechaInicio, fechaFin, usuario,  nombreCliente, nombreAsesor,  fragmento);

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
    private void surgirMenu(View view, GerenteReporteClientesModel list) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_clientes, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(list, view));
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
        GerenteReporteClientesModel list;
        View view;
        public MyMenuItemClickListener(GerenteReporteClientesModel list, View view) {
            this.list = list;
            this.view = view;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sub_menu_reporte_clientes_detalles:
                    Fragment fragmento = new ReporteClientesDetalles();
                    if (view.getContext() instanceof Gerente) {
                        Gerente gerente = (Gerente) view.getContext();
                        //Log.d("onMenuItemClick", list.getIdSucursal() + " -> " + list.getIdTramite() +  " -> " + list.getNumeroCuenta() + " -> " +  fechaInicio + " -> " + fechaFin + " -> " + Config.usuarioCusp(mContext) + " -> " + view);
                        gerente.switchDetalleClientes(list.getIdSucursal(), list.getTramite(), list.getNumeroCuenta(),  fechaInicio, fechaFin, Config.usuarioCusp(mContext),list.getNombreCliente(), list.getNombreAsesor(), fragmento);
                    }
                    return true;
                case R.id.sub_menu_reporte_clientes_email:
                    final Dialog dialog = new Dialog(mContext);
                    dialog.setContentView(R.layout.custom_layout);

                    Button btn = (Button) dialog.findViewById(R.id.dialog_btn_enviar);
                    final Spinner spinner = (Spinner) dialog.findViewById(R.id.dialog_spinner_mail);

                    // TODO: Spinner
                    ArrayAdapter<String> adapterSucursal = new ArrayAdapter<String>(mContext, R.layout.spinner_item_azul, Config.EMAIL);
                    adapterSucursal.setDropDownViewResource(R.layout.spinner_dropdown_item);
                    spinner.setAdapter(adapterSucursal);

                    btn.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            final EditText editText = (EditText) dialog.findViewById(R.id.dialog_et_mail);

                            final String datoEditText = editText.getText().toString();
                            final String datoSpinner = spinner.getSelectedItem().toString();

                            Log.d("DATOS USER","SPINNER: "+datoEditText+" datosSpinner: "+ datoSpinner);
                            if(datoEditText == "" || datoSpinner == "Seleciona un email"){
                                Config.msj(mContext, "Error", "Ingresa email valido");
                            }else{
                                String email = datoEditText+"@"+datoSpinner;
                                Connected connected = new Connected();
                                final InputMethodManager imm = (InputMethodManager) mContext.getSystemService(Service.INPUT_METHOD_SERVICE);
                                if(connected.estaConectado(mContext)){
                                    JSONObject obj = new JSONObject();
                                    JSONObject rqt = new JSONObject();
                                    JSONObject filtro = new JSONObject();
                                    JSONObject filtroCliente = new JSONObject();
                                    JSONObject periodo = new JSONObject();
                                    try {

                                        boolean detalle = true;
                                        rqt.put("correo", email);
                                        rqt.put("detalle", detalle);
                                        rqt.put("filtro", filtro);
                                        filtro.put("cita", list.getCita());
                                        filtro.put("filtroRetenicion", list.getRetenido());
                                        filtroCliente.put("curp", "");
                                        filtroCliente.put("nss", "");
                                        filtroCliente.put("numeroCuenta", "");
                                        filtro.put("idSucursal", list.getIdSucursal());
                                        filtro.put("numeroEmpleado", list.getIdSucursal());
                                        rqt.put("numeroEmpleado", list.getNumeroEmpleado());
                                        rqt.put("periodo", periodo);
                                        periodo.put("fechaInicio", fechaInicio);
                                        periodo.put("fechaFin", fechaFin);
                                        obj.put("rqt", rqt);
                                    } catch (JSONException e) {
                                        Config.msj(mContext, "Error", "Error al formar los datos");
                                    }
                                    EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_CLIENTE,mContext,new EnviaMail.VolleyCallback() {

                                        @Override
                                        public void onSuccess(JSONObject result) {
                                            Log.d("RESPUESTA Gerente", result.toString());
                                            int status;

                                            try {
                                                status = result.getInt("status");
                                            }catch(JSONException error){
                                                status = 400;
                                            }

                                            Log.d("EST","EE: "+status);
                                            imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                            if(status == 200) {
                                                Config.msj(mContext, "Enviando", "Se ha enviado el mensaje al destino");
                                                //Config.msjTime(mContext, "Enviando", "Se ha enviado el mensaje al destino", 4000);
                                                dialog.dismiss();
                                            }else{
                                                Config.msj(mContext, "Error", "Ups algo salio mal =(");
                                                dialog.dismiss();
                                            }
                                            //db.addUserCredits(fk_id_usuario,result);
                                        }
                                        @Override
                                        public void onError(String result) {
                                            Log.d("RESPUESTA ERROR", result);
                                            Config.msj(mContext, "Error en conexión", "Por favor, revisa tu conexión a internet ++");
                                            //db.addUserCredits(fk_id_usuario, "ND");
                                        }
                                    });
                                }else{
                                    Config.msj(mContext, "Error en conexión", "Por favor, revisa tu conexión a internet");
                                }
                            }
                        }
                    });
                    dialog.show();
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
        public TextView btn;
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
            btn = (TextView) view.findViewById(R.id.gfrcll_btn_detalles);
            cardView = (CardView) view.findViewById(R.id.gfrcll_cv);
        }
    }
}
