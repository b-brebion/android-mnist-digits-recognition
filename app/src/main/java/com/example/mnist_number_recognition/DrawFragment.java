package com.example.mnist_number_recognition;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import org.pytorch.Module;

public class DrawFragment extends Fragment {
    private DrawView drawView;

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

    @Override
    public void onViewCreated(@NonNull View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        drawView = getView().findViewById(R.id.drawView);
        Button eraseButton = getView().findViewById(R.id.eraseBtn);
        Button saveButton = getView().findViewById(R.id.saveBtn);

        eraseButton.setOnClickListener(v -> drawView.erase());
        saveButton.setOnClickListener(v -> drawView.save());
    }
}
