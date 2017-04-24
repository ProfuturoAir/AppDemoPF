package com.airmovil.profuturo.ti.retencion.helper;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.BitmapShader;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Shader;
import android.util.AttributeSet;
import android.util.Log;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tecnicoairmovil on 13/03/17.
 */

public class DrawingView extends View {

    // drawing path
    private Path drawPath;
    // drawing and canvas paint
    private Paint drawPaint, canvasPaint;
    // inicializa color
    private int paintColor = 0xFFFFFFFF, paintAlpha = 255;
    // canvas
    private Canvas drawCanvas;
    // canvas bitmap
    private Bitmap canvasBitmap;
    // pincel tamaño
    private float brushSize, lastBrushSize;
    // borrador de la bandera
    private boolean erase=false;
    private boolean started = false;
    public static boolean action = true;

    /**
     * Inicia el constructor de DrawingView
     * @param context
     * @param attrs
     */
    public DrawingView(Context context, AttributeSet attrs){
        super(context, attrs);
        setupDrawing();
    }

    /**
     * Prepararse para dibujar y configurar las propiedades de la pintura
     */
    private void setupDrawing(){
        brushSize = 1;
        lastBrushSize = brushSize;
        drawPath = new Path();
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(brushSize);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
        canvasPaint = new Paint(Paint.DITHER_FLAG);
    }

    /**
     * Tamaño asignado para ver el cambio de drawing
     * @param w
     * @param h
     * @param oldw
     * @param oldh
     */
    @Override
    protected void onSizeChanged(int w, int h, int oldw, int oldh) {
        super.onSizeChanged(w, h, oldw, oldh);
        canvasBitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        drawCanvas = new Canvas(canvasBitmap);
    }

    /**
     * Dibujar la vista - se llamará después del evento táctil
     * @param canvas
     */
    @Override
    protected void onDraw(Canvas canvas) {
        action = true;
        canvas.drawBitmap(canvasBitmap, 0, 0, canvasPaint);
        canvas.drawPath(drawPath, drawPaint);
    }

    /**
     *
     * @return activo si se ha llamado i detenido el drawingView
     */
    public boolean isActive(){
        return started;
    }

    /**
     *
     * @param event
     * @return Registrar toques de usuario como acción de dibujo
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float touchX = event.getX();
        float touchY = event.getY();
        // Responder a eventos hacia abajo, mover y subir
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                drawPath.moveTo(touchX, touchY);
                started = true;
                action = false;
                break;
            case MotionEvent.ACTION_MOVE:
                drawPath.lineTo(touchX, touchY);
                started = true;
                action = false;
                break;
            case MotionEvent.ACTION_UP:
                started = true;
                drawPath.lineTo(touchX, touchY);
                drawCanvas.drawPath(drawPath, drawPaint);
                drawPath.reset();
                action = false;
                break;
            default:
                return false;
        }
        invalidate();
        return true;

    }

    /**
     * coloca el color a pintar sobre el marco
     * @param newColor
     */
    public void setColor(String newColor){
        invalidate();
        // Comprobar si el valor del color o el nombre del patrón
        if(newColor.startsWith("#")){
            paintColor = Color.parseColor(newColor);
            drawPaint.setColor(paintColor);
            drawPaint.setShader(null);
        }
        else{
            int patternID = getResources().getIdentifier(newColor, "drawable", "com.example.drawingfun");
            // decodifica los colores
            Bitmap patternBMP = BitmapFactory.decodeResource(getResources(), patternID);
            BitmapShader patternBMPshader = new BitmapShader(patternBMP,
                    Shader.TileMode.REPEAT, Shader.TileMode.REPEAT);
            drawPaint.setColor(0xFFFFFFFF);
            drawPaint.setShader(patternBMPshader);
        }
    }

    /**
     * establecer el tamaño del pincel
     * @param newSize
     */
    public void setBrushSize(float newSize){
        float pixelAmount = TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, newSize, getResources().getDisplayMetrics());
        brushSize=pixelAmount;
        drawPaint.setStrokeWidth(brushSize);
    }

    /**
     * Obtener y configurar el último tamaño del pincel
     * @param lastSize
     */
    public void setLastBrushSize(float lastSize){
        lastBrushSize=lastSize;
    }
    public float getLastBrushSize(){
        return lastBrushSize;
    }

    /**
     * Establecer borrar true o false
     * @param isErase
     */
    public void setErase(boolean isErase){
        erase=isErase;
        if(erase) drawPaint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.CLEAR));
        else drawPaint.setXfermode(null);
    }

    /**
     * Empezar un nuevo dibujo
     */
    public void startNew(){
        action = true;
        started = false;
        drawCanvas.drawColor(0, PorterDuff.Mode.CLEAR);
        invalidate();
    }

    /**
     * Alfa corriente de retorno
     * @return
     */
    public int getPaintAlpha(){
        return Math.round((float)paintAlpha/255*100);
    }

    /**
     * Establecer alfa, para el color
     * @param newAlpha
     */
    public void setPaintAlpha(int newAlpha){
        paintAlpha=Math.round((float)newAlpha/100*255);
        drawPaint.setColor(paintColor);
        drawPaint.setAlpha(paintAlpha);
    }
}

