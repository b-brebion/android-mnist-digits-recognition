package com.example.mnist_digits_recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Path;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import androidx.annotation.NonNull;

public class DrawView extends View {
    private Paint drawPaint;
    private Path path = new Path();
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

    public Path getPath() {
        return path;
    }

    public void setPath(Path path) {
        this.path = path;
    }

    @Override
    protected void onDraw(@NonNull Canvas canvas) {
        if (eraseStatus) {
            // Erasing the drawing
            path.reset();
            eraseStatus = false;
        }
        // Drawing the path on the canvas
        canvas.drawPath(path, drawPaint);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        float pointX = event.getX();
        float pointY = event.getY();

        switch (event.getAction()) {
            case MotionEvent.ACTION_DOWN:
                // Starting a new line in the path
                path.moveTo(pointX, pointY);
                break;
            case MotionEvent.ACTION_MOVE:
                // Drawing a line between last point and this point
                path.lineTo(pointX, pointY);
                break;
            default:
                return false;
        }

        // Informing that the view should be redrawn (onDraw())
        invalidate();
        // Indicating that the touch has been consumed
        return true;
    }

    private void setupPaint() {
        // Setting up paint
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
        // Informing that the view should be redrawn (onDraw())
        invalidate();
    }

    public Bitmap save() {
        // Saving the drawing as a 28x28 scaled Bitmap
        Bitmap bitmap = Bitmap.createBitmap(this.getWidth(), this.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas myCanvas = new Canvas(bitmap);
        myCanvas.drawColor(Color.BLACK);
        myCanvas.drawPath(path, drawPaint);

        return Bitmap.createScaledBitmap(bitmap, 28, 28, true);
    }
}
