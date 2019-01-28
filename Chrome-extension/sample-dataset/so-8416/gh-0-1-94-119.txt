package com.broadcaster.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.UUID;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.media.ExifInterface;
import android.media.ThumbnailUtils;
import android.provider.MediaStore;

import com.broadcaster.BaseActivity;

public class ImageUtil {
    public static Bitmap getThumbnailFromFile(String imageFile, int reqHeight) throws IOException {
        // First decode with inJustDecodeBounds=true to check dimensions
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(imageFile, options);

        if (options.outHeight > reqHeight) {
            // Calculate ratios of height and width to requested height and width
            options.inSampleSize = Math.round((float) options.outHeight / (float) reqHeight);
        }

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(imageFile, options);
        return rotateImage(imageFile, bitmap);
    }

    private static Bitmap rotateImage(String file, Bitmap bitmap) throws IOException {
        ExifInterface exif = new ExifInterface(file);
        int orientation = exif.getAttributeInt(ExifInterface.TAG_ORIENTATION, 1);
        Matrix matrix = new Matrix();
        if (orientation == 6) {
            matrix.postRotate(90);
        }
        else if (orientation == 3) {
            matrix.postRotate(180);
        }
        else if (orientation == 8) {
            matrix.postRotate(270);
        }
        bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true); // rotating bitmap
        return bitmap;
    }

    public static File optimizeImage(BaseActivity context, File file, int quality) throws IOException {
        //FileInputStream fIn = new FileInputStream(file);
        //Bitmap bitmap = BitmapFactory.decodeStream(fIn);
        //code below may avoid out of memory problem?

        Bitmap bitmap = decodeFile(file);
        bitmap = rotateImage(file.getAbsolutePath(), bitmap);
        return optimizeImage(context, bitmap, quality);
    }

    public static File optimizeImage(BaseActivity context, Bitmap bitmap, int quality) throws IOException {
        int inHeight = bitmap.getHeight();
        int inWidth = bitmap.getWidth();
        int outHeight = Constants.IMAGE_MAX_SIZE;
        int outWidth = Constants.IMAGE_MAX_SIZE;
        if(inHeight > inWidth) {
            outWidth = (inWidth * Constants.IMAGE_MAX_SIZE) / inHeight; 
        }
        else {
            outHeight = (inHeight * Constants.IMAGE_MAX_SIZE) / inWidth; 
        }
        File outputDir = context.getCacheDir();
        File outputFile = File.createTempFile("optimized-"+UUID.randomUUID(), "", outputDir);
        FileOutputStream fOut = new FileOutputStream(outputFile);
        Bitmap resized = Bitmap.createScaledBitmap(bitmap, outWidth, outHeight, true);
        resized.compress(Bitmap.CompressFormat.JPEG, 70, fOut); 
        fOut.flush();
        fOut.close();
        return outputFile;
    }

    // http://stackoverflow.com/questions/477572/strange-out-of-memory-issue-while-loading-an-image-to-a-bitmap-object
    private static Bitmap decodeFile(File f) throws IOException{
        Bitmap b = null;

        //Decode image size
        BitmapFactory.Options o = new BitmapFactory.Options();
        o.inJustDecodeBounds = true;

        FileInputStream fis = new FileInputStream(f);
        BitmapFactory.decodeStream(fis, null, o);
        fis.close();

        int scale = 1;
        if (o.outHeight > Constants.IMAGE_MAX_SIZE || o.outWidth > Constants.IMAGE_MAX_SIZE) {
            scale = (int)Math.pow(2, (int) Math.round(Math.log(Constants.IMAGE_MAX_SIZE / 
                    (double) Math.max(o.outHeight, o.outWidth)) / Math.log(0.5)));
        }

        //Decode with inSampleSize
        BitmapFactory.Options o2 = new BitmapFactory.Options();
        o2.inSampleSize = scale;
        fis = new FileInputStream(f);
        b = BitmapFactory.decodeStream(fis, null, o2);
        fis.close();

        return b;
    }

    // source: http://stackoverflow.com/a/3292810
    public static Bitmap getRoundedCorners(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap
                .getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);

        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }

    // source: http://stackoverflow.com/a/6909144
    public static Bitmap cropCenter(Bitmap srcBmp) {
        if (srcBmp == null) {
            return srcBmp;
        }

        Bitmap dstBmp;
        if (srcBmp.getWidth() >= srcBmp.getHeight()){

            dstBmp = Bitmap.createBitmap(
                    srcBmp, 
                    srcBmp.getWidth()/2 - srcBmp.getHeight()/2,
                    0,
                    srcBmp.getHeight(), 
                    srcBmp.getHeight()
                    );

        }else{

            dstBmp = Bitmap.createBitmap(
                    srcBmp,
                    0, 
                    srcBmp.getHeight()/2 - srcBmp.getWidth()/2,
                    srcBmp.getWidth(),
                    srcBmp.getWidth() 
                    );
        }

        return dstBmp;
    }

    public static Bitmap drawableToBitmap (Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable)drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap); 
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    public static Bitmap createVideoThumb(String file) {
        return ThumbnailUtils.createVideoThumbnail(file, MediaStore.Video.Thumbnails.FULL_SCREEN_KIND);
    }

    public static Bitmap createVideoThumbMini(String file) {
        return ThumbnailUtils.createVideoThumbnail(file, MediaStore.Video.Thumbnails.MINI_KIND);
    }
}
