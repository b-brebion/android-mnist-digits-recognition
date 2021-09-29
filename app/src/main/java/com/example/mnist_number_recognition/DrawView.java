package com.example.mnist_number_recognition;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

public class DrawView extends View {
    // Setup initial color
    private final int paintColor = Color.WHITE;
    // Defines paint and canvas
    private Paint drawPaint;
    // Stores next circle
    private Path path = new Path();

    Bitmap bitmapField = null;

    private boolean eraseStatus = false;
    private boolean saveStatus = false;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        //setupBitmap();
        setupPaint();
    }

    // Draws the path created during the touch events
    @Override
    protected void onDraw(Canvas canvas) {
        if (eraseStatus) {
            Log.e("ERREURS", "path.reset();");
            path.reset();
            eraseStatus = false;
        } else if (saveStatus) {
            Log.e("ERREURS", "saveStatus");
        }
        canvas.drawPath(path, drawPaint);
        //canvas.drawBitmap(bitmapField, 0, 0, drawPaint);
    }

    // Append new circle each time user presses on screen
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();
        // Checks for the event that occurs
        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Starts a new line in the path
                path.moveTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                // Draws line between last point and this point
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }

        invalidate(); // Indicate view should be redrawn
        return true; // Indicate we've consumed the touch
    }

    private void setupBitmap() {
        bitmapField = Bitmap.createBitmap((int) 200, (int) 200, Bitmap.Config.RGB_565);
    }

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(paintColor);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(20);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void erase() {
        Log.e("ERREURS", "erase();");
        eraseStatus = true;
        invalidate();
        //bitmapField.eraseColor(Color.BLACK);
    }

    public void save() {
        Log.e("ERREURS", "save();");
        saveStatus = true;
        invalidate();
    }

    /*
    public Bitmap saveSignature(){

        Bitmap  bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        this.draw(canvas);

        File file = new File(Environment.getExternalStorageDirectory() + "/sign.png");

        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return bitmap;
    }
     */
}
