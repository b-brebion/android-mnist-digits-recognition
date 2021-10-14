package com.example.mnist_digits_recognition;

import android.content.Context;
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

import java.io.IOException;
import java.security.SecureRandom;

public class RecognitionFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private ImageView imageView;
    private TextView textView;
    private final SecureRandom rand = new SecureRandom();

    private UtilsFunctions utilsFunctions;

    public RecognitionFragment() {
        // Required empty public constructor
    }

    public static RecognitionFragment newInstance() {
        return new RecognitionFragment();
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        try {
            utilsFunctions = (UtilsFunctions) context;
        } catch (ClassCastException ignored) {
            // Ignored exception
        }
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
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        imageView = requireView().findViewById(R.id.imageView);
        textView = requireView().findViewById(R.id.textView);

        Button loadButton = requireView().findViewById(R.id.loadBtn);
        loadButton.setOnClickListener(v -> loadNewImage());

        // Attempting to restore the data contained in the ViewModel (if the theme of the app is changed)
        if (sharedViewModel.getRecognitionImage() == null) {
            loadNewImage();
        } else {
            imageView.setImageDrawable(sharedViewModel.getRecognitionImage());
            textView.setText(sharedViewModel.getRecognitionText());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Saving imageView and textView before the Activity is killed
        sharedViewModel.setRecognitionImage(imageView.getDrawable());
        sharedViewModel.setRecognitionText(textView.getText().toString());
    }

    public void loadNewImage() {
        // Creating bitmap from img packaged into app Android asset (app/src/main/assets/img/img_?.jpg)
        Bitmap bitmap = null;
        try {
            int randomInt = rand.nextInt(350) + 1;
            bitmap = BitmapFactory.decodeStream(requireActivity().getAssets().open("img/img_" + randomInt + ".jpg"));
        } catch (IOException e) {
            Log.e("IOException", "Error reading assets (image)", e);
            requireActivity().finish();
        }

        // Showing image on UI
        imageView.setImageBitmap(bitmap);

        // Recognising the digit on the image
        String result = utilsFunctions.digitRecognition(bitmap);
        textView.setText(result);
    }
}
