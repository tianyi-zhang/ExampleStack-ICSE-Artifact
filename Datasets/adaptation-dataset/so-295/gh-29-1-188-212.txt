package com.krayzk9s.imgurholo.services;

/*
 * Copyright 2013 Kurt Zimmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.app.IntentService;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Parcelable;
import android.preference.PreferenceManager;
import android.support.v4.app.NotificationCompat;
import android.util.Base64;
import android.util.Log;

import com.krayzk9s.imgurholo.R;
import com.krayzk9s.imgurholo.activities.ImgurHoloActivity;
import com.krayzk9s.imgurholo.tools.ApiCall;
import com.krayzk9s.imgurholo.tools.FileUtils;
import com.krayzk9s.imgurholo.tools.GetData;
import com.krayzk9s.imgurholo.tools.NewAlbumAsync;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Copyright 2013 Kurt Zimmer
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
public class UploadService extends IntentService implements GetData {
    private ApiCall apiCall;
    private ArrayList<String> ids;
    private int totalUpload;

    public UploadService() {
        super("UploadService");
    }
    /**
     * The IntentService calls this method from the default worker thread with
     * the intent that started the service. When this method returns, IntentService
     * stops the service, as appropriate.
     */
    @Override
    public void onCreate() {
        super.onCreate();
        apiCall = new ApiCall();
        apiCall.setSettings(PreferenceManager.getDefaultSharedPreferences(getApplicationContext()));
    }

    public void onGetObject(Object o, String tag) {
        String id = (String) o;
        if(id.length() == 7) {
            if(totalUpload != -1)
                ids.add(id);
            if(ids.size() == totalUpload) {
                ids.add(0, ""); //weird hack because imgur eats the first item of the array for some bizarre reason
                NewAlbumAsync newAlbumAsync = new NewAlbumAsync("", "", apiCall, ids, this);
                newAlbumAsync.execute();
            }
        }
        else if (apiCall.settings.getBoolean("AlbumUpload", true)) {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            Intent viewImageIntent = new Intent();
            viewImageIntent.setAction(Intent.ACTION_VIEW);
            viewImageIntent.setData(Uri.parse("http://imgur.com/a/" + id));
            Intent shareIntent = new Intent();
            shareIntent.setType("text/plain");
            shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
            shareIntent.putExtra(Intent.EXTRA_TEXT, "http://imgur.com/a/" + id);
            PendingIntent viewImagePendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), viewImageIntent, 0);
            PendingIntent sharePendingIntent = PendingIntent.getActivity(this, (int) System.currentTimeMillis(), shareIntent, 0);
            Notification notification = notificationBuilder
                    .setSmallIcon(R.drawable.icon_desaturated)
                    .setContentText("Finished Uploading Album")
                    .setContentTitle("imgur Image Uploader")
                    .setContentIntent(viewImagePendingIntent)
                    .addAction(R.drawable.dark_social_share, "Share", sharePendingIntent)
                    .build();
            Log.d("Built", "Notification built");
            notificationManager.cancel(0);
            notificationManager.notify(1, notification);
            Log.d("Built", "Notification display");
        }
        else {
            NotificationManager notificationManager = (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(this);
            Notification notification = notificationBuilder
                    .setSmallIcon(R.drawable.icon_desaturated)
                    .setContentText("Finished Uploading All Images")
                    .setContentTitle("imgur Image Uploader")
                    .build();
            Log.d("Built", "Notification built");
            notificationManager.cancel(0);
            notificationManager.notify(1, notification);
            Log.d("Built", "Notification display");
        }
    }

    public void handleException(Exception e, String tag) {

    }

    @Override
    protected void onHandleIntent(Intent intent) {
        ids = new ArrayList<String>();
        if(intent.hasExtra("images")) {
            Log.d("recieving", "handling multiple");
            ArrayList<Parcelable> list = intent.getParcelableArrayListExtra("images");
            totalUpload = list.size();
            for (Parcelable parcel : list) {
                Uri uri = (Uri) parcel;
                Log.d("recieving", uri.toString());
                SendImage sendImage = new SendImage(apiCall, FileUtils.getPath(this, uri), getApplicationContext(), getResources(), this);
                Log.d("recieving", "executing");
                sendImage.execute();
            }
        }
        else {
            totalUpload = -1;
            SendImage sendImage = new SendImage(apiCall, FileUtils.getPath(getApplicationContext(), intent.getData()), getApplicationContext(), getResources(), this);
            sendImage.execute();
        }
    }

    /* From http://stackoverflow.com/users/1946055/tobiel at http://stackoverflow.com/questions/17839388/creating-a-scaled-bitmap-with-createscaledbitmap-in-android */
    private static Bitmap lessResolution(String filePath, int width, int height)
    {
        BitmapFactory.Options options = new BitmapFactory.Options();

        // First decode with inJustDecodeBounds=true to check dimensions
        options.inPurgeable = true;
        options.inInputShareable = true;
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, width, height);
        BitmapFactory.decodeFile(filePath, options);
        float factor = calculateFactor(options, width, height);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;

        return Bitmap.createScaledBitmap(BitmapFactory.decodeFile(filePath, options), (int)Math.floor(options.outWidth*factor), (int)Math.floor(options.outHeight*factor), false);
    }

    private static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {
            Log.d("reqHeight", ""+reqHeight);
            Log.d("reqWidth", ""+reqWidth);
            // Calculate ratios of height and width to requested height and width
            final int heightRatio = Math.round((float) height / (float) reqHeight);
            final int widthRatio = Math.round((float) width / (float) reqWidth);

            inSampleSize = (heightRatio < widthRatio ? heightRatio : widthRatio);
            Log.d("inSampleSize", ""+inSampleSize);
            // We round the value to the highest, always.
            if ((height / inSampleSize) > reqHeight || (width / inSampleSize > reqWidth)) {
                inSampleSize++;
            }

        }

        return inSampleSize;
    }

    private static float calculateFactor(BitmapFactory.Options options, int reqWidth, int reqHeight) {

        final int height = options.outHeight;
        final int width = options.outWidth;
        Log.d("reqHeight", reqHeight + "");
        Log.d("reqWidth", reqWidth + "");
        Log.d(ImgurHoloActivity.IMAGE_DATA_HEIGHT, height + "");
        Log.d("width", width + "");
        float factor;
        if (height > reqHeight || width > reqWidth) {
            final float heightRatio = (float) reqHeight / (float) height;
            final float widthRatio = (float) reqWidth / (float) width;
            Log.d("heightRatio", heightRatio + "");
            Log.d("widthRatio", widthRatio + "");
            factor = heightRatio < widthRatio ? heightRatio : widthRatio;
            Log.d("factor", factor + "");
        }
        else
            factor = 1;
        return factor;
    }

    private static class SendImage extends AsyncTask<Void, Void, JSONObject> {
        Bitmap photo;
        final ApiCall apiCallStatic;
        final SharedPreferences settings;
        final Context context;
        final Resources resources;
        String filePath;
        NotificationManager notificationManager;
        final UploadService uploadService;

        private void throwError(String error) {
            notificationManager.cancel(0);
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            Notification notification = notificationBuilder
                    .setSmallIcon(R.drawable.icon_desaturated)
                    .setContentText(error)
                    .setContentTitle(context.getString(R.string.error_upload))
                    .build();
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }

        public SendImage(ApiCall _apiCallStatic, String _filepath, Context _context, Resources _resources, UploadService _uploadService) {
            uploadService = _uploadService;
            apiCallStatic = _apiCallStatic;
            filePath = _filepath;
            context = _context;
            settings = PreferenceManager.getDefaultSharedPreferences(context);
            resources = _resources;
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            Notification notification = notificationBuilder
                    .setSmallIcon(R.drawable.icon_desaturated)
                    .setContentText("Now Uploading")
                    .setProgress(0, 0, true)
                    .setContentTitle("imgur Image Uploader")
                    .build();
            notificationManager = (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);
            notificationManager.notify(0, notification);
        }

        @Override
        protected JSONObject doInBackground(Void... voids) {
            Log.d("recieved", "in background");
            int maxHeight = Integer.MAX_VALUE;
            if(settings.getBoolean("HeightBoolean", false))
                maxHeight = Integer.parseInt(settings.getString("HeightSize", "1080"));
            int maxWidth = Integer.MAX_VALUE;
            if(settings.getBoolean("WidthBoolean", false))
                maxWidth = Integer.parseInt(settings.getString("WidthSize", "1920"));
            byte[] byteArray;
            try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            if(maxHeight != Integer.MAX_VALUE || maxWidth != Integer.MAX_VALUE) {

                photo = lessResolution(filePath, maxWidth, maxHeight);
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
                byteArray = stream.toByteArray();
            }
            else {
                InputStream is = new BufferedInputStream(new FileInputStream(filePath));
                ByteArrayOutputStream bos = new ByteArrayOutputStream();
                while (is.available() > 0) {
                    bos.write(is.read());
                }
                byteArray =  bos.toByteArray();
                photo = lessResolution(filePath, 500, 500);
                photo.compress(Bitmap.CompressFormat.PNG, 100, stream);
            }
            if (byteArray == null)
                Log.d("Image Upload", "NULL :(");
            String image = Base64.encodeToString(byteArray, Base64.DEFAULT);
            Log.d("Image Upload", image);
            HashMap<String, Object> hashMap = new HashMap<String, Object>();
            hashMap.put("image", image);
            hashMap.put(ImgurHoloActivity.IMAGE_DATA_TYPE, "binary");
            JSONObject data = apiCallStatic.makeCall("3/image", "post", hashMap);
            if(data == null)
                data = apiCallStatic.makeCall("3/image", "post", hashMap);
            if(data == null) {
                throwError("imgur Not Responding");
                return null;
            }
            Log.d("Image Upload", data.toString());
                JSONObject returner = apiCallStatic.makeCall("3/image/" + data.getJSONObject("data").getString("id"), "get", null);
                Log.d("returning", returner.toString());
                return returner.getJSONObject("data");
            }
            catch (JSONException e) {
                Log.e("Error!", e.toString());
            }
            catch (IOException e) {
                Log.e("Error!", e.toString());
            }
            return new JSONObject();
        }

        @Override
        protected void onPostExecute(JSONObject data) {
            NotificationCompat.Builder notificationBuilder = new NotificationCompat.Builder(context);
            NotificationCompat.BigPictureStyle bigPictureStyle = new NotificationCompat.BigPictureStyle();
            if(data == null) {
                Notification notification = notificationBuilder
                        .setSmallIcon(R.drawable.icon_desaturated)
                        .setContentText(context.getString(R.string.error_upload))
                        .setStyle(bigPictureStyle)
                        .setContentTitle(context.getString(R.string.imgur_uploader_title))
                        .build();
                notificationManager.cancel(0);
                notificationManager.notify(1, notification);
                return;
            }
            Log.d("Built", "Notification building");
            bigPictureStyle.bigPicture(photo);
            Log.d("Built", "Picture set");

            try {
            Log.d("data", data.toString());
            String id = data.getString("id");
            Log.d("id", id);
            uploadService.onGetObject(id, null);
            Intent viewImageIntent = new Intent();
            viewImageIntent.setAction(Intent.ACTION_VIEW);
            viewImageIntent.setData(Uri.parse(data.getString(ImgurHoloActivity.IMAGE_DATA_LINK)));
            String link = "";
            if (settings.getString("AutoCopyType", resources.getString(R.string.direct_link)).equals(resources.getString(R.string.direct_link))) {
               link = "http://imgur.com/" + data.getString("id");
            }
            else if(settings.getString("AutoCopyType", resources.getString(R.string.direct_link)).equals(resources.getString(R.string.link))) {
                link = data.getString(ImgurHoloActivity.IMAGE_DATA_LINK);
            }
            else if(settings.getString("AutoCopyType", resources.getString(R.string.direct_link)).equals(resources.getString(R.string.html_link))) {
                link = "<a href=\"http://imgur.com/" + data.getString("id") + "\"><img src=\"" + data.getString(ImgurHoloActivity.IMAGE_DATA_LINK) + "\" title=\"Hosted by imgur.com\"/></a>";
            }
            else if(settings.getString("AutoCopyType", resources.getString(R.string.direct_link)).equals(resources.getString(R.string.bbcode_link))) {
                link = "[IMG]" + data.getString(ImgurHoloActivity.IMAGE_DATA_LINK) + "[/IMG]";
            }
            else if(settings.getString("AutoCopyType", resources.getString(R.string.direct_link)).equals(resources.getString(R.string.linked_bbcode_link))) {
                link = "[URL=http://imgur.com/" + data.getString("id") + "][IMG]" + data.getString(ImgurHoloActivity.IMAGE_DATA_LINK) + "[/IMG][/URL]";
            }
            else if(settings.getString("AutoCopyType", resources.getString(R.string.direct_link)).equals(resources.getString(R.string.markdown_link))) {
                link = "[Imgur](http://i.imgur.com/" + data.getString("id") + ")";
            }
                Intent shareIntent = new Intent(android.content.Intent.ACTION_SEND);
                shareIntent.setType("text/plain");
                shareIntent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_WHEN_TASK_RESET);
                shareIntent.putExtra(Intent.EXTRA_TEXT, link);
                PendingIntent viewImagePendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), viewImageIntent, 0);
                PendingIntent sharePendingIntent = PendingIntent.getActivity(context, (int) System.currentTimeMillis(), shareIntent, 0);
                if(uploadService.totalUpload == -1) {
                Notification notification = notificationBuilder
                        .setSmallIcon(R.drawable.icon_desaturated)
                        .setContentText(context.getString(R.string.finished_uploading))
                        .setStyle(bigPictureStyle)
                        .setContentTitle(context.getString(R.string.imgur_uploader_title))
                        .setContentIntent(viewImagePendingIntent)
                        .addAction(R.drawable.dark_social_share, "Share", sharePendingIntent)
                        .build();
                    Log.d("Built", "Notification built");
                    notificationManager.cancel(0);
                    notificationManager.notify(1, notification);
                    Log.d("Built", "Notification display");
                }
            }
            catch (JSONException e) {
                Log.e("Error!", e.toString());
                throwError("imgur Response Malformed");
            }

        }
    }
}
