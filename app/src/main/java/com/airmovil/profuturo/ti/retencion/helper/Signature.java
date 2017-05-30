package com.airmovil.profuturo.ti.retencion.helper;

import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.airmovil.profuturo.ti.retencion.R;
import com.kyanogen.signatureview.SignatureView;

/**
 * Created by tecnicoairmovil on 29/05/17.
 */

public class Signature {

    private static Dialog dialog;
    private static ImageView cancelar, limpear, aceptar;
    private static TextView nombreClienteFirma;
    private static SignatureView signatureView;
    private static boolean isSignatured = false;

    public static void procesandoFirma(final Context context, final ImageView imgFirma, String nombreCliente){
        dialog = new Dialog(context);
        dialog.setContentView(R.layout.firma_cliente);
        dialog.getWindow().setLayout(WindowManager.LayoutParams.MATCH_PARENT, WindowManager.LayoutParams.MATCH_PARENT);

        cancelar = (ImageView) dialog.findViewById(R.id.fc_iv_cancelar);
        limpear = (ImageView) dialog.findViewById(R.id.fc_iv_limpiar);
        aceptar = (ImageView) dialog.findViewById(R.id.fc_iv_aceptar);
        nombreClienteFirma = (TextView) dialog.findViewById(R.id.tv_nombre_cliente_firma);
        signatureView = (SignatureView) dialog.findViewById(R.id.signature_view);

        nombreClienteFirma.setText(nombreCliente);

        signatureView.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                isSignatured = true;
                return false;
            }
        });

        cancelar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
                android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(v.getContext());
                dialog1.setTitle("Alerta");
                dialog1.setMessage("¿Estás de seguro que no quieres firmar?");
                dialog1.setCancelable(true);
                dialog1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {
                    }
                });
                dialog1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog1, int which) {

                    }
                });
                dialog1.create().show();
            }
        });
        limpear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signatureView.clearCanvas();
                isSignatured = false;
            }
        });
        final LinearLayout content = (LinearLayout) dialog.findViewById(R.id.contenedor_firma);

        aceptar.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                content.setDrawingCacheEnabled(true);

                if(isSignatured){
                    final Bitmap b = Bitmap.createBitmap(content.getDrawingCache());
                    android.app.AlertDialog.Builder dialog1 = new android.app.AlertDialog.Builder(v.getContext());
                    dialog1.setTitle("Confirmación de firma");
                    dialog1.setMessage("¿Estas de acuerdo que es la firma correcta ?");
                    dialog1.setCancelable(true);
                    dialog1.setPositiveButton("SI", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {
                            imgFirma.getLayoutParams().height = 400;
                            imgFirma.setImageBitmap(b);
                            isSignatured = false;
                            dialog.dismiss();
                        }
                    });
                    dialog1.setNegativeButton("NO", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog1, int which) {

                        }
                    });
                    dialog1.create().show();
                }else{
                    Dialogos.msj(context,"Error", "Se requiere firmar");
                }
                content.setDrawingCacheEnabled(false);
            }
        });
        dialog.show();
    }
}
