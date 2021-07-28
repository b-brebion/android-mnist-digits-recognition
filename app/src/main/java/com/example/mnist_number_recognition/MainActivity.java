package com.example.mnist_number_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.appcompat.widget.SwitchCompat;

import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.FloatBuffer;

public class MainActivity extends AppCompatActivity {
    private enum DarkModeState {DAY, NIGHT}

    // 1-channel image to Tensor functions ----------------------------------------------------------------
    public static Tensor bitmapToFloat32Tensor(final Bitmap bitmap) {
        return bitmapToFloat32Tensor(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight());
    }

    public static void bitmapToFloatBuffer(final Bitmap bitmap, final int x, final int y, final int width, final int height, final FloatBuffer outBuffer, final int outBufferOffset) {
        checkOutBufferCapacityNoRgb(outBuffer, outBufferOffset, width, height);
        final int pixelsCount = height * width;
        final int[] pixels = new int[pixelsCount];
        bitmap.getPixels(pixels, 0, width, x, y, width, height);
        for (int i = 0; i < pixelsCount; i++) {
            final int c = pixels[i];
            outBuffer.put(((c) & 0xff) / 255.0f);
        }
    }

    public static Tensor bitmapToFloat32Tensor(final Bitmap bitmap, int x, int y, int width, int height) {
        final FloatBuffer floatBuffer = Tensor.allocateFloatBuffer(width * height);
        bitmapToFloatBuffer(bitmap, x, y, width, height, floatBuffer, 0);
        return Tensor.fromBlob(floatBuffer, new long[]{1, 1, height, width});
    }

    private static void checkOutBufferCapacityNoRgb(FloatBuffer outBuffer, int outBufferOffset, int tensorWidth, int tensorHeight) {
        if (outBufferOffset + tensorWidth * tensorHeight > outBuffer.capacity()) {
            throw new IllegalStateException("Buffer underflow");
        }
    }
    // ----------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Module module = null;
        try {
            // Loading serialized TorchScript module from packaged into app android asset app/src/model/assets/modelMNIST_ts.pt
            module = Module.load(assetFilePath(this, "modelMNIST_ts.pt"));
        } catch (IOException e) {
            Log.e("MNIST NumberRecognition", "Error reading assets (module)", e);
            finish();
        }

        Button loadButton = findViewById(R.id.loadBtn);

        final Module finalModule = module;
        loadButton.setOnClickListener(v -> loadNewImage(finalModule));

        loadNewImage(finalModule);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        MenuItem item = menu.findItem(R.id.AB_switch_item);
        item.setActionView(R.layout.action_bar_switch);

        final SwitchCompat sw = item.getActionView().findViewById(R.id.AB_switch_view);

/*
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();
        final boolean isDarkModeOn = sharedPreferences.getBoolean("isDarkModeOn", false);
        // When user reopens the app
        // after applying dark/light mode
        if (isDarkModeOn) {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
        } else {
            AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_NO);
        }
*/
        sw.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (isChecked) {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_YES);
            } else {
                AppCompatDelegate.setDefaultNightMode(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM);
                //Toast.makeText(MainActivity.this, "Dark_mode OFF", Toast.LENGTH_LONG).show();
            }
        });

        return true;
    }

    @Override
    public void onConfigurationChanged(@NonNull Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        int nightModeFlags = getResources().getConfiguration().uiMode & Configuration.UI_MODE_NIGHT_MASK;

        if (nightModeFlags == Configuration.UI_MODE_NIGHT_NO) {
            applyDayNight(DarkModeState.DAY);
        } else {
            applyDayNight(DarkModeState.NIGHT);
        }
    }

    private void applyDayNight(DarkModeState state) {
        View mainActivityView = findViewById(R.id.main_activity);
        TextView textView = findViewById(R.id.textView);
        Button loadButton = findViewById(R.id.loadBtn);
        Resources.Theme theme = this.getTheme();
        int backgroundColor = getResources().getColor(R.color.backgroundColor, theme);
        int textColor = getResources().getColor(R.color.textColor, theme);

        if (state == DarkModeState.DAY) {
            mainActivityView.setBackgroundColor(backgroundColor);
            textView.setTextColor(textColor);
            //loadButton.setBackground(textColor);
        } else {
            mainActivityView.setBackgroundColor(backgroundColor);
            textView.setTextColor(textColor);
            //loadButton.setBackgroundColor(accentColor);
        }
    }

    public void loadNewImage(Module module) {
        Bitmap bitmap = null;

        try {
            // Creating bitmap from img packaged into app android asset app/src/main/assets/img/img_?.jpg
            int randomInt = (int) (Math.random() * 350 + 1);
            bitmap = BitmapFactory.decodeStream(getAssets().open("img/img_" + randomInt + ".jpg"));
        } catch (IOException e) {
            Log.e("MNIST NumberRecognition", "Error reading assets (image)", e);
            finish();
        }

        // Showing image on UI
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

        // Preparing input tensor
        final Tensor inputTensor = bitmapToFloat32Tensor(bitmap);

        // Running the model
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

        // Getting tensor content as Java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // Searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        String result = "Recognised number: " + maxScoreIdx;
        TextView textView = findViewById(R.id.textView);
        textView.setText(result);
    }

    public static String assetFilePath(Context context, String assetName) throws IOException {
        File file = new File(context.getFilesDir(), assetName);
        if (file.exists() && file.length() > 0) {
            return file.getAbsolutePath();
        }

        try (InputStream is = context.getAssets().open(assetName)) {
            try (OutputStream os = new FileOutputStream(file)) {
                byte[] buffer = new byte[4 * 1024];
                int read;
                while ((read = is.read(buffer)) != -1) {
                    os.write(buffer, 0, read);
                }
                os.flush();
            }
            return file.getAbsolutePath();
        }
    }
}
