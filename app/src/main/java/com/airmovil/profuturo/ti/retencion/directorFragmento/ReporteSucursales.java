package com.airmovil.profuturo.ti.retencion.directorFragmento;

import android.app.DatePickerDialog;
import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.Spinner;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.airmovil.profuturo.ti.retencion.helper.Config;
import com.airmovil.profuturo.ti.retencion.helper.SessionManager;

import java.util.Map;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link ReporteSucursales.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link ReporteSucursales#newInstance} factory method to
 * create an instance of this fragment.
 */
public class ReporteSucursales extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    // TODO: XML
    private TextView tvFecha, tvEntidades, tvNoEntidades, tvSaldoEmitido, tvSaldoNoEmitido;
    private Spinner spinnerSucursales;

    // TODO: View, sessionManager, datePickerDialog
    private View rootView;
    private SessionManager sessionManager;
    private DatePickerDialog datePickerDialog;
    private TextView tvRangoFecha1, tvRangoFecha2;
    private Button btnBuscar;

    // TODO: variable
    private int mYear;
    private int mMonth;
    private int mDay;
    private String fechaIni = "";
    private String fechaFin = "";
    private String fechaMostrar = "";
    private String numeroUsuario;

    public ReporteSucursales() {
        // Required empty public constructor
    }

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment ReporteSucursales.
     */
    // TODO: Rename and change types and number of parameters
    public static ReporteSucursales newInstance(String param1, String param2) {
        ReporteSucursales fragment = new ReporteSucursales();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        rootView = view;

        tvFecha = (TextView) rootView.findViewById(R.id.dfrs_tv_fecha);
        tvEntidades = (TextView) rootView.findViewById(R.id.dfrs_tv_entidades);
        tvNoEntidades= (TextView) rootView.findViewById(R.id.dfrs_tv_no_entidades);
        tvSaldoEmitido = (TextView) rootView.findViewById(R.id.dfrs_tv_saldo_emitido);
        tvSaldoNoEmitido= (TextView) rootView.findViewById(R.id.dfrs_tv_saldo_no_emitido);
        tvRangoFecha1 = (TextView) rootView.findViewById(R.id.dfrs_tv_fecha_rango1);
        tvRangoFecha2 = (TextView) rootView.findViewById(R.id.dfrs_tv_fecha_rango2);
        spinnerSucursales = (Spinner) rootView.findViewById(R.id.dfrs_spinner_sucursales);
        btnBuscar = (Button) rootView.findViewById(R.id.dfrs_btn_filtro);

        // TODO: fechas dialog
        rangoInicial();
        rangoFinal();

        // TODO: inclucion de fecha
        Map<String, Integer> fechaDatos = Config.dias();
        mYear  = fechaDatos.get("anio");
        mMonth = fechaDatos.get("mes");
        mDay   = fechaDatos.get("dia");

        if(getArguments() != null){
            fechaIni = getArguments().getString(ARG_PARAM1);
            fechaFin = getArguments().getString(ARG_PARAM2);
            tvFecha.setText(fechaIni+" - "+fechaFin);
        }else {
            Map<String, String> fechas = Config.fechas(1);
            fechaFin = fechas.get("fechaFin");
            fechaIni = fechas.get("fechaIni");
            fechaMostrar = fechaIni;
            tvFecha.setText(fechaMostrar);
        }

        final Fragment borrar = this;
        // TODO: Boton filtro
        btnBuscar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String f1 = tvRangoFecha1.getText().toString();
                String f2 = tvRangoFecha2.getText().toString();

                if(f1.equals("Fecha Inicio1")||f2.equals("Fecha final")){
                    Config.msj(v.getContext(),"Error de datos","Favor de introducir fechas para aplicar el filtro");
                }else {
                    FragmentTransaction ft = getActivity().getSupportFragmentManager().beginTransaction();
                    ReporteSucursales rs = ReporteSucursales.newInstance(
                            (fechaIni.equals("") ? "" : fechaIni),
                            (fechaFin.equals("") ? "" : fechaFin),
                            "",
                            rootView.getContext()
                    );
                    borrar.onDestroy();
                    ft.remove(borrar);
                    ft.replace(R.id.content_director, rs);
                    ft.addToBackStack(null);
                    ft.commit();
                }
            }
        });


    }

    public static ReporteSucursales newInstance(String sParam1, String sParam2, String sParam3, Context context){
        ReporteSucursales fragment = new ReporteSucursales();
        Bundle args = new Bundle();
        args.putString("parametro1", sParam1);
        args.putString("parametro2", sParam2);
        args.putString("parametro3", sParam3);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.director_fragmento_reporte_sucursales, container, false);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }

    private void rangoInicial(){
        tvRangoFecha1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvRangoFecha1.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }

    private void rangoFinal(){
        tvRangoFecha2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                datePickerDialog = new DatePickerDialog(getContext(),
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth) {
                                tvRangoFecha2.setText(dayOfMonth + "-" + (monthOfYear + 1) + "-" + year);
                                fechaIni = dayOfMonth + "-" + (monthOfYear + 1) + "-" + year;
                            }
                        }, mYear, mMonth, mDay);
                datePickerDialog.show();
            }
        });
    }
}
