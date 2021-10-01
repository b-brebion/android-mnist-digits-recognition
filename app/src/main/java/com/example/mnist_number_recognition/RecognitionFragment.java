package com.example.mnist_number_recognition;

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

    private UtilsFunctions utilsFunctions;

    public RecognitionFragment() {
        // Required empty public constructor
    }

    public static RecognitionFragment newInstance() {
        RecognitionFragment recognitionFragment = new RecognitionFragment();
        return recognitionFragment;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        try {
            utilsFunctions = (UtilsFunctions) context;
        } catch (ClassCastException castException) {
            /** The activity does not implement the listener. */
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
        sharedViewModel = new ViewModelProvider(getActivity()).get(SharedViewModel.class);

        imageView = getView().findViewById(R.id.imageView);
        textView = getView().findViewById(R.id.textView);

        Button loadButton = getView().findViewById(R.id.loadBtn);
        loadButton.setOnClickListener(v -> loadNewImage());

        // Attempting to restore the data contained in the ViewModel (if the theme of the app is changed)
        if (sharedViewModel.getImage() == null) {
            loadNewImage();
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

    public void loadNewImage() {
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

        String result = utilsFunctions.digitRecognition(bitmap);
        textView.setText(result);
    }
}
