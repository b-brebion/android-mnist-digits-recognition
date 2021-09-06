package com.example.mnist_number_recognition;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
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
    private final String[] items = {"Light", "Dark", "Auto (Based on System)"};
    private MyViewModel mViewModel;
    private ImageView imageView;
    private TextView textView;
    private AlertDialog dialog;

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
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        int mode = sharedPreferences.getInt("mode", 1);
        AppCompatDelegate.setDefaultNightMode(mode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        imageView = findViewById(R.id.imageView);
        textView = findViewById(R.id.textView);

        mViewModel = new ViewModelProvider(this).get(MyViewModel.class);

        Module module = mViewModel.getModule();
        if (module == null) {
            try {
                // Loading serialized TorchScript module from packaged into app android asset app/src/model/assets/modelMNIST_ts.pt
                module = Module.load(assetFilePath(this, "modelMNIST_ts.pt"));
                mViewModel.setModule(module);
            } catch (IOException e) {
                Log.e("MNIST NumberRecognition", "Error reading assets (module)", e);
                finish();
            }
        }

        Button loadButton = findViewById(R.id.loadBtn);
        final Module finalModule = module;
        loadButton.setOnClickListener(v -> loadNewImage(finalModule));

        if (mViewModel.getImage() == null) {
            loadNewImage(finalModule);
        } else {
            imageView.setImageDrawable(mViewModel.getImage());
            textView.setText(mViewModel.getText());
        }

        dialog = createSettingsDialog();
        if (mViewModel.getDialogState()) {
            dialog.show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    private void setAppTheme(int mode, SharedPreferences.Editor editor) {
        AppCompatDelegate.setDefaultNightMode(mode);
        editor.putInt("mode", mode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == R.id.action_settings) {
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        mViewModel.setImage(imageView.getDrawable());
        mViewModel.setText(textView.getText().toString());
        mViewModel.setDialogState(dialog.isShowing());
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
        textView.setText(result);
    }

    public AlertDialog createSettingsDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        AlertDialog.Builder builder = new AlertDialog.Builder(MainActivity.this);
        builder.setTitle("Theme");
        int checkedTheme = sharedPreferences.getInt("checkedTheme", 0);
        builder.setSingleChoiceItems(items, checkedTheme, (dialogInterface, i) -> {
            switch (i) {
                case 0:
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_NO, editor);
                    break;
                case 1:
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_YES, editor);
                    break;
                case 2:
                    setAppTheme(AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM, editor);
                    break;
            }
            editor.putInt("checkedTheme", i);
            editor.apply();
            getDelegate().applyDayNight();
        });
        builder.setPositiveButton("Close", (dialogInterface, i) -> dialogInterface.dismiss());
        return builder.create();
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
