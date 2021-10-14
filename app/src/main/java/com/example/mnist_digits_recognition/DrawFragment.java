package com.example.mnist_digits_recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

public class DrawFragment extends Fragment {
    private SharedViewModel sharedViewModel;
    private DrawView drawView;
    private TextView textView;

    private UtilsFunctions utilsFunctions;

    public DrawFragment() {
        // Required empty public constructor
    }

    public static DrawFragment newInstance() {
        return new DrawFragment();
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
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        // Retrieving or creating a ViewModel to allow data to survive configuration changes
        sharedViewModel = new ViewModelProvider(requireActivity()).get(SharedViewModel.class);

        drawView = requireView().findViewById(R.id.drawView);
        textView = requireView().findViewById(R.id.textView);
        Button eraseButton = requireView().findViewById(R.id.eraseBtn);

        drawView.setOnTouchListener((view1, motionEvent) -> {
            // Recognising the digit on the drawing when the user lifts his finger from the screen
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Bitmap scaledBitmap = drawView.save();
                String result = utilsFunctions.digitRecognition(scaledBitmap);
                textView.setText(result);
                // Indicating that the touch has been consumed
                return true;
            }
            // Indicating that the touch is yet to be consumed
            return false;
        });

        eraseButton.setOnClickListener(v -> {
            // Erasing the drawing
            drawView.erase();
            textView.setText(R.string.unknown_digit);
        });

        // Attempting to restore the data contained in the ViewModel (if the theme of the app is changed)
        if (sharedViewModel.getDrawText() != null) {
            textView.setText(sharedViewModel.getDrawText());
            drawView.setPath(sharedViewModel.getDrawPath());
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        // Saving textView and path before the Activity is killed
        sharedViewModel.setDrawText(textView.getText().toString());
        sharedViewModel.setDrawPath(drawView.getPath());
    }
}
