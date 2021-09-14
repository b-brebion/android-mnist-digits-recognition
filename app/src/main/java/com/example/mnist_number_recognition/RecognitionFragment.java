package com.example.mnist_number_recognition;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import org.pytorch.IValue;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.IOException;
import java.nio.FloatBuffer;
import java.security.SecureRandom;

public class RecognitionFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private ImageView imageView;
    private TextView textView;
    private final SecureRandom rand = new SecureRandom();

    public RecognitionFragment() {
        // Required empty public constructor
    }

    public static RecognitionFragment newInstance() {
        RecognitionFragment recognitionFragment = new RecognitionFragment();
        return recognitionFragment;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_recognition, container, false);
    }

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieving or creating a ViewModel to allow data to survive configuration changes
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        Module module = sharedViewModel.getModule();

        imageView = getView().findViewById(R.id.imageView);
        textView = getView().findViewById(R.id.textView);

        Button loadButton = getView().findViewById(R.id.loadBtn);
        final Module finalModule = module;
        loadButton.setOnClickListener(v -> loadNewImage(finalModule));

        // Attempting to restore the data contained in the ViewModel (if the theme of the app is changed)
        if (sharedViewModel.getImage() == null) {
            loadNewImage(finalModule);
        } else {
            imageView.setImageDrawable(sharedViewModel.getImage());
            textView.setText(sharedViewModel.getText());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Saving the imageView and textView before the Activity is killed
        sharedViewModel.setImage(imageView.getDrawable());
        sharedViewModel.setText(textView.getText().toString());
    }

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

    public void loadNewImage(Module module) {
        // Creating bitmap from img packaged into app Android asset (app/src/main/assets/img/img_?.jpg)
        Bitmap bitmap = null;
        try {
            int randomInt = rand.nextInt(350) + 1;
            bitmap = BitmapFactory.decodeStream(getActivity().getAssets().open("img/img_" + randomInt + ".jpg"));
        } catch (IOException e) {
            Log.e("IOException", "Error reading assets (image)", e);
            getActivity().finish();
        }

        // Showing image on UI
        imageView.setImageBitmap(bitmap);

        // Preparing input tensor
        Tensor inputTensor = null;
        try {
            inputTensor = bitmapToFloat32Tensor(bitmap);
        } catch (Exception e) {
            Log.e("Exception", "Error bitmapToFloat32Tensor()", e);
            getActivity().finish();
        }

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
}
