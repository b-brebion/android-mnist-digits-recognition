package com.example.mnist_digits_recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.io.File;
import java.io.FileOutputStream;

public class DrawView extends View {
    // Defines paint and canvas
    private Paint drawPaint;
    // Stores next circle
    private final Path path = new Path();

    private boolean eraseStatus = false;

    public DrawView(Context context) {
        this(context, null);
    }

    public DrawView(Context context, AttributeSet attrs) {
        super(context, attrs);
        setFocusable(true);
        setFocusableInTouchMode(true);
        setupPaint();
    }

    // Draws the path created during the touch events
    @Override
    protected void onDraw(Canvas canvas) {
        if (eraseStatus) {
            path.reset();
            eraseStatus = false;
        }
        canvas.drawPath(path, drawPaint);
    }

    // Append new circle each time user presses on screen
    @SuppressLint("ClickableViewAccessibility")
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

    // Setup paint with color and stroke styles
    private void setupPaint() {
        drawPaint = new Paint();
        drawPaint.setColor(Color.WHITE);
        drawPaint.setAntiAlias(true);
        drawPaint.setStrokeWidth(60);
        drawPaint.setStyle(Paint.Style.STROKE);
        drawPaint.setStrokeJoin(Paint.Join.ROUND);
        drawPaint.setStrokeCap(Paint.Cap.ROUND);
    }

    public void erase() {
        eraseStatus = true;
        invalidate();
    }

    public Bitmap save() {
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(bitmap);
        myCanvas.drawColor(Color.BLACK);
        myCanvas.drawPath(path, drawPaint);

        Bitmap scaledBitmap = Bitmap.createScaledBitmap(bitmap, 28, 28, true);
        File file = new File(getContext().getFilesDir() + "/test.png");
        try {
            scaledBitmap.compress(Bitmap.CompressFormat.PNG, 100, new FileOutputStream(file));
        } catch (Exception e) {
            e.printStackTrace();
        }

        return scaledBitmap;
    }
}
