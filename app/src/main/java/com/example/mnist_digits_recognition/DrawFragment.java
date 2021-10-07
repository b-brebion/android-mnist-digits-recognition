package com.example.mnist_digits_recognition;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import java.util.Arrays;

public class DrawFragment extends Fragment {
    private DrawView drawView;
    private TextView textView, textView2;

    private UtilsFunctions utilsFunctions;

    public DrawFragment() {
        // Required empty public constructor
    }

    public static DrawFragment newInstance(/*int someInt, String someTitle*/) {
        DrawFragment drawFragment = new DrawFragment();
        /*
        Bundle args = new Bundle();
        args.putInt("someInt", someInt);
        args.putString("someTitle", someTitle);
        fragmentDemo.setArguments(args);
         */
        return drawFragment;
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

    /*
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Get back arguments
        int SomeInt = getArguments().getInt("someInt", 0);
        String someTitle = getArguments().getString("someTitle", "");
    }
     */

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_draw, container, false);
    }

    @SuppressLint("ClickableViewAccessibility")
    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawView = getView().findViewById(R.id.drawView);
        textView = getView().findViewById(R.id.textView);
        textView2 = getView().findViewById(R.id.textView2);
        Button eraseButton = getView().findViewById(R.id.eraseBtn);

        drawView.setOnTouchListener((view1, motionEvent) -> {
            if (motionEvent.getAction() == MotionEvent.ACTION_UP) {
                Bitmap scaledBitmap = drawView.save();
                String result = utilsFunctions.digitRecognition(scaledBitmap);
                textView.setText(result);
                textView2.setText(Arrays.toString(utilsFunctions.getTempScores()));
                return true;
            }
            return false;
        });

        eraseButton.setOnClickListener(v -> drawView.erase());
    }
}
