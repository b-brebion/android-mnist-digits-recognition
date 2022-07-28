package com.example.mnist_digits_recognition;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.app.AppCompatDelegate;
import androidx.lifecycle.ViewModelProvider;

import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;

import com.google.android.material.color.DynamicColors;
import com.google.android.material.dialog.MaterialAlertDialogBuilder;
import com.google.android.material.elevation.SurfaceColors;
import com.google.android.material.navigation.NavigationBarView;

import org.pytorch.IValue;
import org.pytorch.LiteModuleLoader;
import org.pytorch.Module;
import org.pytorch.Tensor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class MainActivity extends AppCompatActivity implements UtilsFunctions {
    private final String[] items = {"Light", "Dark", "Auto (Based on System)"};
    private SharedViewModel sharedViewModel;
    private Module module;
    private AlertDialog dialog;
    private RecognitionFragment recognitionFragment;
    private DrawFragment drawFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // Applying the chosen theme when (re)starting the app
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        int mode = sharedPreferences.getInt("mode", 1);
        AppCompatDelegate.setDefaultNightMode(mode);

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Modifying Navigation Bar and Status Bar colors to match app colors
        getWindow().setNavigationBarColor(SurfaceColors.SURFACE_2.getColor(this));
        getWindow().setStatusBarColor(SurfaceColors.SURFACE_2.getColor(this));

        // Retrieving or creating a ViewModel to allow data to survive configuration changes
        sharedViewModel = new ViewModelProvider(this).get(SharedViewModel.class);

        module = sharedViewModel.getModule();
        if (module == null) {
            try {
                // Loading serialized TorchScript module from file packaged into app Android asset (app/src/model/assets/modelMNIST_ts_lite.ptl)
                module = LiteModuleLoader.load(assetFilePath(this, "modelMNIST_ts_lite.ptl"));
                sharedViewModel.setModule(module);
            } catch (IOException e) {
                Log.e("IOException", "Error reading assets (module)", e);
                finish();
            }
        }

        // Creating the settings AlertDialog and re-displaying it when the theme is changed
        dialog = createSettingsDialog();
        if (sharedViewModel.getDialogState()) {
            dialog.show();
        }

        // Creating Fragments at first launch or retrieving them when changing theme
        if (savedInstanceState == null) {
            recognitionFragment = RecognitionFragment.newInstance();
            drawFragment = DrawFragment.newInstance();
            getSupportFragmentManager().beginTransaction().setReorderingAllowed(true)
                    .add(R.id.fragmentContainerView, recognitionFragment, "RecognitionFragment")
                    .add(R.id.fragmentContainerView, drawFragment, "DrawFragment")
                    .hide(drawFragment)
                    .commit();
        } else {
            recognitionFragment = (RecognitionFragment) getSupportFragmentManager().getFragment(savedInstanceState, "recognitionFragment");
            drawFragment = (DrawFragment) getSupportFragmentManager().getFragment(savedInstanceState, "drawFragment");
        }

        // Configuring the NavigationBarView to display the proper Fragment
        NavigationBarView bottomNavigationView = findViewById(R.id.bottom_navigation);
        bottomNavigationView.setOnItemSelectedListener(item -> {
            int buttonId = item.getItemId();
            if (buttonId == R.id.recognition && drawFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().show(recognitionFragment).hide(drawFragment).commit();
                return true;
            } else if (buttonId == R.id.draw && recognitionFragment.isVisible()) {
                getSupportFragmentManager().beginTransaction().show(drawFragment).hide(recognitionFragment).commit();
                return true;
            }
            return false;
        });
    }

    @Override
    public boolean onCreateOptionsMenu(@NonNull Menu menu) {
        // Adding the settings button in the top ActionBar
        getMenuInflater().inflate(R.menu.action_menu, menu);
        return true;
    }

    private void setAppTheme(int mode, SharedPreferences.Editor editor) {
        // Setting the default night mode and saving it in the SharedPreferences
        AppCompatDelegate.setDefaultNightMode(mode);
        editor.putInt("mode", mode);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Displaying the AlertDialog when the settings button is clicked
        if (item.getItemId() == R.id.action_settings) {
            dialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        // Saving both Fragments
        getSupportFragmentManager().putFragment(outState, "recognitionFragment", recognitionFragment);
        getSupportFragmentManager().putFragment(outState, "drawFragment", drawFragment);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        // Saving the dialog state before the Activity is killed
        sharedViewModel.setDialogState(dialog.isShowing());
        dialog.dismiss();
    }

    @Override
    public String digitRecognition(Bitmap bitmap) {
        // Preparing input tensor
        Tensor inputTensor = null;
        try {
            inputTensor = UtilsFunctions.bitmapToFloat32Tensor(bitmap);
        } catch (Exception e) {
            Log.e("Exception", "Error bitmapToFloat32Tensor()", e);
            finish();
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

        return "Recognised Digit: " + maxScoreIdx;
    }

    public AlertDialog createSettingsDialog() {
        SharedPreferences sharedPreferences = getSharedPreferences("sharedPrefs", MODE_PRIVATE);
        final SharedPreferences.Editor editor = sharedPreferences.edit();

        // Building the settings AlertDialog
        MaterialAlertDialogBuilder builder = new MaterialAlertDialogBuilder(this);
        builder.setTitle("Select app theme");
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
                default:
                    break;
            }
            editor.putInt("checkedTheme", i);
            editor.apply();
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
