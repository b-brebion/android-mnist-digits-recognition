package com.example.mnist_number_recognition;

import android.os.Bundle;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class DrawFragment extends Fragment {
    public DrawFragment() {
        // Required empty public constructor
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
}
