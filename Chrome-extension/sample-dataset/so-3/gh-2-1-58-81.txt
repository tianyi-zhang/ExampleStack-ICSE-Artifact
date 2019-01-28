package com.noahgolmant.ImageToText;

/**
 * Created by Noah on 11/30/2014.
 */

import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Environment;
import android.util.Log;
import android.widget.Toast;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.xml.sax.InputSource;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.nio.BufferOverflowException;
import java.util.zip.GZIPInputStream;

/**
 * Async Task to make http call
 */
public class DownloadTask extends AsyncTask<String, Integer, String> {

    private Context context;
    private ProgressDialog progDialog;
    private String loadedJson;

    public DownloadTask(Context context) {
        this.context = context;
    }

    public interface DownloadInterface {
        public void useResult();
    }

    public DownloadInterface intent = null;

    @Override
    protected void onPreExecute() {
        super.onPreExecute();
        progDialog = new ProgressDialog(context);
        progDialog.setMessage("Loading...");
        progDialog.setIndeterminate(false);
        progDialog.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        progDialog.setCancelable(true);
        progDialog.show();

        loadedJson = loadJSONFromAsset();
    }

    private String loadJSONFromAsset() {
        String json = null;
        try {

            InputStream is = context.getAssets().open("languages.json");

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

    @Override
    protected String doInBackground(String... lang) {
        try {

            JSONObject json = new JSONObject(loadedJson);
            String urlString  = json.getString(lang[0]);

            String fileName = urlString.substring(urlString.lastIndexOf('/') + 1).replace(".gz", "");

            Log.d("ImageToText", "File name for " + lang[0] + ": " + fileName);

            File exist = new File("/sdcard/" + fileName);
            if(exist.exists()) {
                exist.delete();
                Log.d("ImageToText", fileName + " already existed.");
            }

            URL url = new URL(urlString);
            URLConnection connection = url.openConnection();
            InputStream stream = connection.getInputStream();
            stream = new GZIPInputStream(stream);
            InputSource is = new InputSource(stream);
            InputStream input = new BufferedInputStream(is.getByteStream());

            File sdcard = new File(Environment.getExternalStorageDirectory().toString() + "/tessdata");
            if(!sdcard.exists() || !sdcard.isDirectory()) {
                sdcard.mkdir();
            }
            OutputStream output = new FileOutputStream(new File(sdcard, fileName));
            Log.d("ImageToText", output.toString());
            // this will be useful to display download percentage
            // might be -1: server did not report the length
            int fileLength = connection.getContentLength();

            byte data[] = new byte[4096];
            long total = 0;
            int count;
            while ((count = input.read(data)) != -1) {
                // allow canceling with back button
                if (isCancelled()) {
                    input.close();
                    return null;
                }
                total += count;
                // publishing the progress....
                if (fileLength > 0) // only if total length is known
                    publishProgress((int) (total * 100 / fileLength));
                output.write(data, 0, count);
            }

            output.flush();
            output.close();
            input.close();
        } catch (BufferOverflowException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    protected void onProgressUpdate(Integer... progress) {
        super.onProgressUpdate(progress);
        // if we get here, length is known, now set indeterminate to false

        progDialog.setIndeterminate(false);
        progDialog.setMax(100);
        progDialog.setProgress(progress[0]);
    }

    @Override
    protected void onPostExecute(String result) {
        super.onPostExecute(result);
        progDialog.dismiss();
        if (result != null)
            Toast.makeText(context, "Download error: " + result, Toast.LENGTH_LONG).show();
        else
            Toast.makeText(context,"File downloaded", Toast.LENGTH_SHORT).show();

        intent.useResult();
    }

}