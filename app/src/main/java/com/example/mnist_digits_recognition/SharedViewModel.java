package com.example.mnist_digits_recognition;

import android.graphics.drawable.Drawable;

import androidx.lifecycle.ViewModel;

import org.pytorch.Module;

public class SharedViewModel extends ViewModel {
    private Module module;
    private Drawable image;
    private String text;
    private boolean dialogState;

    public Module getModule() {
        return module;
    }

    public void setModule(Module module) {
        this.module = module;
    }

    public Drawable getImage() {
        return image;
    }

    public void setImage(Drawable image) {
        this.image = image;
    }

    public String getText() {
        return text;
    }

    public void setText(String text) {
        this.text = text;
    }

    public boolean getDialogState() {
        return dialogState;
    }

    public void setDialogState(boolean dialogState) {
        this.dialogState = dialogState;
    }
}
