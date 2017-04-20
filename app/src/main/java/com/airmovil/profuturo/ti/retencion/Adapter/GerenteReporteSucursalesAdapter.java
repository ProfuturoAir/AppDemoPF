package com.airmovil.profuturo.ti.retencion.Adapter;

import android.app.Dialog;
import android.app.Service;
import android.content.Context;
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
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistencia;
import com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientes;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.Connected;
import com.airmovil.profuturo.ti.retencion.helper.EnviaMail;
import com.airmovil.profuturo.ti.retencion.listener.OnLoadMoreListener;
import com.airmovil.profuturo.ti.retencion.model.GerenteReporteSucursalesModel;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

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
    private String fechaIni;
    private String fechaFin;

    public GerenteReporteSucursalesAdapter(Context mContext, List<GerenteReporteSucursalesModel> list, RecyclerView mRecyclerView,String fechaIni,String fechaFin) {
        this.mContext = mContext;
        this.list = list;
        this.mRecyclerView = mRecyclerView;
        this.fechaIni = fechaIni;
        this.fechaFin = fechaFin;

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
                    surgirMenu(v,lista);
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
    private void surgirMenu(View view,GerenteReporteSucursalesModel lista) {
        // inflate menu
        PopupMenu popup = new PopupMenu(mContext, view);
        MenuInflater inflater = popup.getMenuInflater();
        inflater.inflate(R.menu.sub_menu_reporte_sucursal, popup.getMenu());
        popup.setOnMenuItemClickListener(new MyMenuItemClickListener(lista));
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
        GerenteReporteSucursalesModel lista;

        public MyMenuItemClickListener(GerenteReporteSucursalesModel lista) {
            this.lista = lista;
        }

        @Override
        public boolean onMenuItemClick(MenuItem item) {
            switch (item.getItemId()) {
                case R.id.sub_menu_reporte_sucusal_nav_clientes:
                    ReporteClientes fragmentoClientes = new ReporteClientes();
                    Gerente gerente = (Gerente) mRecyclerView.getContext();
                    gerente.switchClientesFS(fragmentoClientes, lista.getIdSucursal(),fechaIni,fechaFin);
                    //AppCompatActivity a2 = (AppCompatActivity) mRecyclerView.getContext();
                    //com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientes f2 = new com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteClientes();
                    //a2.getSupportFragmentManager().beginTransaction().replace(R.id.content_gerente, f2).addToBackStack(null).commit();
                    return true;
                case R.id.sub_menu_reporte_sucusal_nav_asistencia:
                    ReporteAsistencia fragmentoAsistencia = new ReporteAsistencia();
                    Gerente dt = (Gerente) mRecyclerView.getContext();
                    dt.switchAsistenciaFS(fragmentoAsistencia, lista.getIdSucursal(),fechaIni,fechaFin);
                    //AppCompatActivity a3 = (AppCompatActivity) mRecyclerView.getContext();
                    //com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistencia f3 = new com.airmovil.profuturo.ti.retencion.gerenteFragmento.ReporteAsistencia();
                    //a3.getSupportFragmentManager().beginTransaction().replace(R.id.content_gerente, f3).addToBackStack(null).commit();
                    return true;
                case R.id.sub_menu_reporte_sucusal_email:
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
                                    //final EnviaMail envia = new EnviaMail();
                                    //String respuesta = envia.sendMail("1","correo",true,"1","12","12",Config.URL_SEND_MAIL,mContext);
                                /*imm.hideSoftInputFromWindow(editText.getWindowToken(), 0);
                                Config.msjTime(mContext, "Enviando", "Se ha enviado el mensaje al destino", 4000);
                                dialog.dismiss();*/
                                    JSONObject obj = new JSONObject();
                                    try {
                                        JSONObject rqt = new JSONObject();
                                        rqt.put("correo", email);
                                        rqt.put("detalle", true);
                                        rqt.put("idSucursal", lista.getIdSucursal());
                                        JSONObject periodo = new JSONObject();
                                        periodo.put("fechaFin", fechaFin);
                                        periodo.put("fechaInicio", fechaIni);
                                        rqt.put("periodo", periodo);
                                        rqt.put("usuario", Config.usuarioCusp(mRecyclerView.getContext()));
                                        obj.put("rqt", rqt);
                                        Log.d("datos", "REQUEST-->" + obj);
                                    } catch (JSONException e) {
                                        Config.msj(mContext, "Error", "Error al formar los datos");
                                    }
                                    EnviaMail.sendMail(obj,Config.URL_SEND_MAIL_REPORTE_ASESOR,mContext,new EnviaMail.VolleyCallback() {

                                        @Override
                                        public void onSuccess(JSONObject result) {
                                            Log.d("RESPUESTA DIRECTOR", result.toString());
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
        public TextView campoLetra, campoIdSucursal, campoConCita, campoSinCita, campoEmitidas, campoNoEmitidas, campoSaldoEmitido, campoSaldoNoEmitido;
        public TextView btn;
        public CardView cardView;
        public MyViewHolder(View view){
            super(view);
            campoLetra = (TextView) view.findViewById(R.id.gfrsl_tv_letra);
            campoIdSucursal = (TextView) view.findViewById(R.id.gfrsl_tv_id_sucursal);
            campoConCita = (TextView) view.findViewById(R.id.gfrsl_tv_con_cita);
            campoSinCita = (TextView) view.findViewById(R.id.gfrsl_tv_sin_cita);
            campoEmitidas = (TextView) view.findViewById(R.id.gfrsl_tv_emitidas);
            campoNoEmitidas = (TextView) view.findViewById(R.id.gfrsl_tv_no_emitidas);
            campoSaldoEmitido = (TextView) view.findViewById(R.id.gfrsl_tv_saldos_emitido);
            campoSaldoNoEmitido = (TextView) view.findViewById(R.id.gfrsl_tv_saldos_no_emitido);
            btn = (TextView) view.findViewById(R.id.gfrsl_tv_menu);
            cardView = (CardView) view.findViewById(R.id.gfrsl_cv);
        }
    }
}
