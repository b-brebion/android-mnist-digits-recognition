package com.example.mnist_digits_recognition;

import android.graphics.Path;
import android.graphics.drawable.Drawable;

import androidx.lifecycle.ViewModel;

import org.pytorch.Module;

public class SharedViewModel extends ViewModel {
    // App
    private Module module;
    private boolean dialogState;

    // Recognition Fragment
    private Drawable recognitionImage;
    private String recognitionText;

    // Draw Fragment
    private String drawText;
    private Path drawPath;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public boolean getDialogState() {
        return dialogState;
    }

    public void setDialogState(boolean dialogState) {
        this.dialogState = dialogState;
    }

    public Drawable getRecognitionImage() {
        return recognitionImage;
    }

    public void setRecognitionImage(Drawable recognitionImage) {
        this.recognitionImage = recognitionImage;
    }

    public String getRecognitionText() {
        return recognitionText;
    }

    public void setRecognitionText(String recognitionText) {
        this.recognitionText = recognitionText;
    }

    public String getDrawText() {
        return drawText;
    }

    public void setDrawText(String drawText) {
        this.drawText = drawText;
    }

    public Path getDrawPath() {
        return drawPath;
    }

    public void setDrawPath(Path drawPath) {
        this.drawPath = drawPath;
    }
}
