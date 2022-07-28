package com.example.mnist_digits_recognition;

import android.app.Application;

import com.google.android.material.color.DynamicColors;

public class MNISTApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Applying Android 12+ dynamic colors if available
        DynamicColors.applyToActivitiesIfAvailable(this);
    }
}
