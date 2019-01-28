package com.noahgolmant.ImageToText;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.view.View;
import android.widget.*;
import com.memetix.mst.language.Language;
import com.noahgolmant.ImageToText.camera.CameraActivity;
import org.apache.http.util.LangUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.opencv.android.OpenCVLoader;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class ITTActivity extends Activity implements DownloadTask.DownloadInterface, View.OnClickListener {

    /**
     * Widgets and items interacted with in the main window.
     */
    private Button startButton;
    private String loadedJSON;

    private Spinner fromSpinner;
    private Spinner toSpinner;

    /**
     * Listeners to call when a button or object is selected.
     */
    private View.OnClickListener newPhotoButtonListener;

    /**
     * Called when the activity is first created.
     */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);

        OpenCVLoader.initDebug();

        initializeWidgets();
        initializeListeners();

        checkCameraHardware();

        loadedJSON = loadJSONFromAsset();

    }

    private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = this.getAssets().open("languages.json");

            int size = is.available();

            byte[] buffer = new byte[size];

            is.read(buffer);

            is.close();

            json = new String(buffer, "UTF-8");


        } catch (IOException ex) {
            ex.printStackTrace();
            return null;
        }
        return json;

    }

    /** Check if this device has a camera */
    private boolean checkCameraHardware() {
        if (getPackageManager().hasSystemFeature(PackageManager.FEATURE_CAMERA)){
            // this device has a cameram continue
            return true;
        } else {
            // no camera on this device, construct an alert dialog instance to inform the user.
            new AlertDialog.Builder(this)
                    .setTitle("No Camera Detected")
                    .setMessage("ImageToText detected no camera on this device.")
                    .setNeutralButton("OK", new AlertDialog.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            // Disable the photo button
                            //startNewPhotoButton.setEnabled(false);
                        }
                    });
            return false;
        }
    }

    /**
     * Initialization methods.
     */

    private void initializeWidgets() {
        // Initialize widgets
        this.startButton = (Button) findViewById(R.id.startButton);
        this.fromSpinner = (Spinner) findViewById(R.id.fromSpinner);
        this.toSpinner = (Spinner) findViewById(R.id.toSpinner);
    }

    private void initializeListeners() {

        this.startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startGuidedPhoto();
            }
        });

    }

    @Override
    public void onClick(View v) {


        try {
            String fromString = String.valueOf(fromSpinner.getSelectedItem());
            JSONObject json = new JSONObject(loadedJSON);
            String urlString  = json.getString(fromString);

            String fileName = urlString.substring(urlString.lastIndexOf('/') + 1).replace(".gz", "");

            File data = new File(Environment.getExternalStorageDirectory().toString() + "/tessdata/" + fileName);

            if(!data.exists()) {
                DownloadTask initDownload = new DownloadTask(this);
                initDownload.intent = this;
                initDownload.execute(fromString);

                startButton.setEnabled(false);
            } else {
                startGuidedPhoto();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private void startGuidedPhoto() {

        String fromString = String.valueOf(fromSpinner.getSelectedItem());
        String toString = String.valueOf(toSpinner.getSelectedItem());

        Language fromLang = langMap.get(fromString);
        Language toLang = langMap.get(toString);



        Intent guideIntent = new Intent();
        guideIntent.setClassName(getApplicationContext(), "com.noahgolmant.ImageToText.camera.CameraActivity");

        guideIntent.putExtra("from", fromLang);
        guideIntent.putExtra("to", toLang);
        super.onResume();
        startActivity(guideIntent);
    }

    private static Map<String, Language> langMap = new HashMap<String, Language>();
    static {
        langMap.put("English", Language.ENGLISH);
        langMap.put("Spanish", Language.SPANISH);
        langMap.put("French", Language.FRENCH);
    }


    @Override
    public void useResult() {
        startButton.setEnabled(true);
    }
}