package com.example.pytorch_test;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.util.Log;
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

    //1-channel image to Tensor functions ----------------------------------------------------------------
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
        return Tensor.fromBlob(floatBuffer, new long[] {1, 1, height, width});
    }

    private static void checkOutBufferCapacityNoRgb(FloatBuffer outBuffer, int outBufferOffset, int tensorWidth, int tensorHeight) {
        if (outBufferOffset + tensorWidth * tensorHeight > outBuffer.capacity()) {
            throw new IllegalStateException("Buffer underflow");
        }
    }
    //----------------------------------------------------------------------------------------------------

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Module module = null;
        try {
            // loading serialized TorchScript module from packaged into app android asset app/src/model/assets/modelMNIST_ts.pt
            module = Module.load(assetFilePath(this, "modelMNIST_ts.pt"));
        } catch (IOException e) {
            Log.e("MNIST NumberRecognition", "Error reading assets (module)", e);
            finish();
        }

        Button loadButton = findViewById(R.id.load);

        final Module finalModule = module;
        loadButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loadNewImage(finalModule);
            }
        });

        loadNewImage(finalModule);
    }

    public void loadNewImage(Module module){
        Bitmap bitmap = null;

        try {
            // creating bitmap from img packaged into app android asset app/src/main/assets/img/img_?.jpg
            int randomInt = (int) (Math.random()*350 + 1);
            bitmap = BitmapFactory.decodeStream(getAssets().open("img/img_"+randomInt+".jpg"));
        } catch (IOException e) {
            Log.e("MNIST NumberRecognition", "Error reading assets (image)", e);
            finish();
        }

        // showing image on UI
        ImageView imageView = findViewById(R.id.imageView);
        imageView.setImageBitmap(bitmap);

        // preparing input tensor
        final Tensor inputTensor = bitmapToFloat32Tensor(bitmap);

        // running the model
        final Tensor outputTensor = module.forward(IValue.from(inputTensor)).toTensor();

        // getting tensor content as java array of floats
        final float[] scores = outputTensor.getDataAsFloatArray();

        // searching for the index with maximum score
        float maxScore = -Float.MAX_VALUE;
        int maxScoreIdx = -1;
        for (int i = 0; i < scores.length; i++) {
            if (scores[i] > maxScore) {
                maxScore = scores[i];
                maxScoreIdx = i;
            }
        }
        String result = "I see a : "+maxScoreIdx;
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
