/*
 * Copyright 2013 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package me.barrasso.android.volume.utils;

import android.accessibilityservice.AccessibilityService;
import android.annotation.TargetApi;
import android.app.Activity;
import android.app.WallpaperManager;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.res.ColorStateList;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.PictureDrawable;
import android.graphics.drawable.StateListDrawable;
import android.media.AudioManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.os.SystemClock;
import android.renderscript.Allocation;
import android.renderscript.Element;
import android.renderscript.RenderScript;
import android.renderscript.ScriptIntrinsicBlur;
import android.service.notification.NotificationListenerService;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.provider.Settings;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewConfiguration;
import android.view.ViewGroup;
import android.widget.AnalogClock;
import android.widget.ImageView;
import android.widget.TextView;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.view.accessibility.AccessibilityEvent;
import android.accessibilityservice.AccessibilityServiceInfo;
import android.view.accessibility.AccessibilityManager;

import java.io.IOException;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.text.CharacterIterator;
import java.text.StringCharacterIterator;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.lang.reflect.Field;
import java.util.Locale;
import java.util.Map;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

import me.barrasso.android.volume.LogUtils;
import me.barrasso.android.volume.VolumeAccessibilityService;
import me.barrasso.android.volume.popup.BlackberryVolumePanel;
import me.barrasso.android.volume.popup.HeadsUpVolumePanel;
import me.barrasso.android.volume.popup.UberVolumePanel;
import me.barrasso.android.volume.popup.WPVolumePanel;

import static android.media.AudioManager.ADJUST_RAISE;
import static android.media.AudioManager.ADJUST_SAME;
import static me.barrasso.android.volume.LogUtils.LOGE;

/**
 * Because every project needs a Utils class.
 */
public class Utils {
    private static final String TAG = LogUtils.makeLogTag(Utils.class);

    /***
     * Android L (lollipop, API 21) introduced a new problem when trying to invoke implicit intent,
     * "java.lang.IllegalArgumentException: Service Intent must be explicit"
     *
     * If you are using an implicit intent, and know only 1 target would answer this intent,
     * This method will help you turn the implicit intent into the explicit form.
     *
     * Inspired from SO answer: http://stackoverflow.com/a/26318757/1446466
     * @param context
     * @param implicitIntent - The original implicit intent
     * @return Explicit Intent created from the implicit original intent
     */
    public static Intent createExplicitFromImplicitIntent(Context context, Intent implicitIntent) {
        // Retrieve all services that can match the given intent
        PackageManager pm = context.getPackageManager();
        List<ResolveInfo> resolveInfo = pm.queryIntentServices(implicitIntent, 0);

        // Make sure only one match was found
        if (resolveInfo == null || resolveInfo.size() != 1) {
            return null;
        }

        // Get component info and create ComponentName
        ResolveInfo serviceInfo = resolveInfo.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        // Create a new intent. Use the old one for extras and such reuse
        Intent explicitIntent = new Intent(implicitIntent);

        // Set the component to be explicit
        explicitIntent.setComponent(component);

        return explicitIntent;
    }

    public static final long MILLIS_NANOS = 1000000l; // 1 ms = 1,000,000 nanos
    public static final int SECONDS_MILLIS = 1000; // 1 second is 1000 ms
    public static final int MINUTES_MILLIS = 60 * SECONDS_MILLIS; // 1 minute = 60 sec
    public static final int HOURS_MILLIS = 60 * MINUTES_MILLIS; // 1 hour = 60 min

    // From com.android.internal.util.ArrayUtils

    /**
     * Checks that value is present as at least one of the elements of the array.
     *
     * @param array the array to check in
     * @param value the value to check for
     * @return true if the value is present in the array
     */
    public static <T> boolean contains(T[] array, T value) {
        for (T element : array) {
            if (element == null) {
                if (value == null) return true;
            } else {
                if (value != null && element.equals(value)) return true;
            }
        }
        return false;
    }

    public static boolean contains(int[] array, int value) {
        for (int element : array) {
            if (element == value) {
                return true;
            }
        }
        return false;
    }

    /** @return The length of all arrays if they match, -1 if else. */
    public static int lengthsMatch(Object[]... arrays) {
        if (null == arrays || arrays.length == 0) return -1;
        int len = (arrays[0] == null) ? 0 : arrays[0].length;
        for (Object[] array : arrays) {
            int thisLen = (array == null) ? 0 : array.length;
            if (thisLen != len) return -1;
        }
        return len;
    }

    public static String[] getServiceComponentNames(Class<?> clazz) {
        return new String[]{
                clazz.getPackage().getName() + '/' + clazz.getName(),
                clazz.getPackage().getName() + "/." + clazz.getSimpleName()};
    }

    public static String[] getServiceComponentNames(Context context, String className) {
        final String packageName = context.getPackageName();
        return new String[]{
                packageName + '/' + packageName + '.' + className,
                packageName + "/." + className};
    }

    /** @return True if a given {@link android.service.notification.NotificationListenerService} is enabled. */
    public static <T extends NotificationListenerService> boolean isNotificationListenerServiceRunning(Context context, Class<T> clazz) {
        return isSettingsServiceEnabled(context, Constants.getEnabledNotificationListeners(), getServiceComponentNames(clazz));
    }

    /** @return True if a given {@link android.service.notification.NotificationListenerService} is enabled. */
    public static <T extends NotificationListenerService> boolean isNotificationListenerServiceRunning(Context context, String className) {
        return isSettingsServiceEnabled(context, Constants.getEnabledNotificationListeners(), getServiceComponentNames(context, className));
    }

    public static <T extends AccessibilityService> boolean isAccessibilityServiceEnabled(Context context, Class<T> clazz) {
        return isAccessibilityServiceEnabled(context, getServiceComponentNames(clazz));
    }

    /**
     * API 14+, check if the given {@link AccessibilityService} is enabled.
     *
     * @return True if id is an enabled {@link AccessibilityService}.
     */
    public static boolean isAccessibilityServiceEnabled14(Context context, String[] ids) {

        AccessibilityManager aManager = (AccessibilityManager)
                context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        if (null == aManager || null == ids) return false;

        List<AccessibilityServiceInfo> services = aManager
                .getEnabledAccessibilityServiceList(AccessibilityEvent.TYPES_ALL_MASK);
        for (AccessibilityServiceInfo service : services) {
            if (contains(ids, service.getId())) {
                return true;
            }
        }

        return false;
    }

    private static final TextUtils.SimpleStringSplitter COLON_SPLITTER = new TextUtils.SimpleStringSplitter(':');

    /**
     * API 4+, check if the given {@link AccessibilityService} is enabled.
     *
     * @return True if id is an enabled {@link AccessibilityService}.
     */
    public static boolean isAccessibilityServiceEnabled4(Context context, String[] ids) {
        return isSettingsServiceEnabled(context, Settings.Secure.ENABLED_ACCESSIBILITY_SERVICES, ids);
    }

    /**
     * API 4+, check if the given {@link AccessibilityService} is enabled.
     *
     * @return True if id is an enabled {@link AccessibilityService}.
     */
    public static boolean isSettingsServiceEnabled(Context context, String setting, String[] ids) {
        // Check the list of system settings to see if a service is running.
        String eServices = Settings.Secure.getString(context.getContentResolver(), setting);
        if (!TextUtils.isEmpty(eServices) && null != ids) {
            TextUtils.SimpleStringSplitter splitter = COLON_SPLITTER;
            splitter.setString(eServices);
            while (splitter.hasNext()) {
                String aService = splitter.next();
                if (contains(ids, aService)) {
                    return true;
                }
            }
        }

        return false;
    }

    /**
     * @return True if id is an enabled {@link AccessibilityService}.
     */
    public static boolean isAccessibilityServiceEnabled(Context context, String[] ids) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ICE_CREAM_SANDWICH) {
            return isAccessibilityServiceEnabled14(context, ids);
        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.DONUT) {
            return isAccessibilityServiceEnabled4(context, ids);
        }

        return false; // Not supported
    }

    /**
     * @return True if system-wide Accessibility is enabled.
     */
    public static boolean isAccessibilityEnabled(Context context) {
        // Also see Settings.Secure.ACCESSIBILITY_ENABLED, alternative method.
        AccessibilityManager aManager = (AccessibilityManager)
                context.getSystemService(Context.ACCESSIBILITY_SERVICE);
        return (null != aManager && aManager.isEnabled());
    }

    public static boolean isMyServiceRunning(Context context, Class<?> serviceClass) {
        return isMyServiceRunning(context, serviceClass.getName());
    }

    public static boolean isMyServiceRunning(Context context, String serviceClass) {
        if (null == serviceClass || null == context) return false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> services = manager.getRunningServices(Integer.MAX_VALUE);
        for (RunningServiceInfo service : services) {
            if (serviceClass.equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

    public static Bitmap recolorBitmap(Drawable drawable, int color) {
        if (drawable == null) {
            return null;
        }

        int width = drawable.getIntrinsicWidth();
        int height = drawable.getIntrinsicHeight();
        if (width <= 0 || height <= 0) {
            return Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888);
        }

        Bitmap outBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(outBitmap);
        drawable.setBounds(0, 0, outBitmap.getWidth(), outBitmap.getHeight());
        drawable.setColorFilter(color, PorterDuff.Mode.SRC_IN);
        drawable.draw(canvas);
        drawable.setColorFilter(null);
        drawable.setCallback(null); // free up any references
        return outBitmap;
    }

    public static Drawable makeRecoloredDrawable(Context context, BitmapDrawable drawable,
                                                 int color, boolean withStates) {
        Bitmap recoloredBitmap = recolorBitmap(drawable, color);
        BitmapDrawable recoloredDrawable = new BitmapDrawable(
                context.getResources(), recoloredBitmap);

        if (!withStates) {
            return recoloredDrawable;
        }

        StateListDrawable stateDrawable = new StateListDrawable();
        stateDrawable.addState(new int[]{android.R.attr.state_pressed}, drawable);
        stateDrawable.addState(new int[]{android.R.attr.state_focused}, drawable);
        stateDrawable.addState(new int[]{}, recoloredDrawable);
        return stateDrawable;
    }

    public static void traverseAndRecolor(View root, int color, boolean withStates,
                                          boolean setClickableItemBackgrounds) {
        Context context = root.getContext();

        if (setClickableItemBackgrounds && root.isClickable()) {
            StateListDrawable selectableItemBackground = new StateListDrawable();
            selectableItemBackground.addState(new int[]{android.R.attr.state_pressed},
                    new ColorDrawable((color & 0xffffff) | 0x33000000));
            selectableItemBackground.addState(new int[]{android.R.attr.state_focused},
                    new ColorDrawable((color & 0xffffff) | 0x44000000));
            selectableItemBackground.addState(new int[]{}, null);
            root.setBackground(selectableItemBackground);
        }

        if (root instanceof ViewGroup) {
            ViewGroup parent = (ViewGroup) root;
            for (int i = 0; i < parent.getChildCount(); i++) {
                traverseAndRecolor(parent.getChildAt(i), color, withStates,
                        setClickableItemBackgrounds);
            }

        } else if (root instanceof ImageView) {
            ImageView imageView = (ImageView) root;
            Drawable sourceDrawable = imageView.getDrawable();
            if (withStates && sourceDrawable != null && sourceDrawable instanceof BitmapDrawable) {
                imageView.setImageDrawable(makeRecoloredDrawable(context,
                        (BitmapDrawable) sourceDrawable, color, true));
            } else {
                imageView.setColorFilter(color, PorterDuff.Mode.MULTIPLY);
            }

        } else if (root instanceof TextView) {
            TextView textView = (TextView) root;
            if (withStates) {
                int sourceColor = textView.getCurrentTextColor();
                ColorStateList colorStateList = new ColorStateList(new int[][]{
                        new int[]{android.R.attr.state_pressed},
                        new int[]{android.R.attr.state_focused},
                        new int[]{}
                }, new int[]{
                        sourceColor,
                        sourceColor,
                        color
                });
                textView.setTextColor(colorStateList);
            } else {
                textView.setTextColor(color);
            }

        } else if (root instanceof AnalogClock) {
            AnalogClock analogClock = (AnalogClock) root;
            try {
                Field hourHandField = AnalogClock.class.getDeclaredField("mHourHand");
                hourHandField.setAccessible(true);
                Field minuteHandField = AnalogClock.class.getDeclaredField("mMinuteHand");
                minuteHandField.setAccessible(true);
                Field dialField = AnalogClock.class.getDeclaredField("mDial");
                dialField.setAccessible(true);
                BitmapDrawable hourHand = (BitmapDrawable) hourHandField.get(analogClock);
                if (hourHand != null) {
                    Drawable d = makeRecoloredDrawable(context, hourHand, color, withStates);
                    d.setCallback(analogClock);
                    hourHandField.set(analogClock, d);
                }
                BitmapDrawable minuteHand = (BitmapDrawable) minuteHandField.get(analogClock);
                if (minuteHand != null) {
                    Drawable d = makeRecoloredDrawable(context, minuteHand, color, withStates);
                    d.setCallback(analogClock);
                    minuteHandField.set(analogClock, d);
                }
                BitmapDrawable dial = (BitmapDrawable) dialField.get(analogClock);
                if (dial != null) {
                    Drawable d = makeRecoloredDrawable(context, dial, color, withStates);
                    d.setCallback(analogClock);
                    dialField.set(analogClock, d);
                }
            } catch (Throwable t) {
                LOGE(TAG, "Error recoloring View hierarchy.", t);
            }
        }
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and barHeight of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and barHeight larger than the requested height and barHeight.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    // http://stackoverflow.com/questions/13557195/how-to-check-if-string-is-a-valid-class-identifier
    public static boolean isFullyQualifiedClassname(final String classname) {
        if (classname == null) return false;
        String[] parts = classname.split("[\\.]");
        if (parts.length == 0) return false;
        for (String part : parts) {
            CharacterIterator iter = new StringCharacterIterator(part);
            // Check first character (there should at least be one character for each part) ...
            char c = iter.first();
            if (c == CharacterIterator.DONE) return false;
            if (!Character.isJavaIdentifierStart(c) && !Character.isIdentifierIgnorable(c)) return false;
            c = iter.next();
            // Check the remaining characters, if there are any ...
            while (c != CharacterIterator.DONE) {
                if (!Character.isJavaIdentifierPart(c) && !Character.isIdentifierIgnorable(c)) return false;
                c = iter.next();
            }
        }
        return true;
    }

    /**
     * Creates a new IntentFilter, the product of both inputs.
     */
    public static IntentFilter merge(IntentFilter a, IntentFilter b) {
        IntentFilter both = new IntentFilter(a);
        for (int i = 0, e = b.countActions(); i < e; ++i)
            both.addAction(b.getAction(i));
        return both;
    }

    public static String bundle2string(Bundle bundle) {
        if (null == bundle) return "[Bundle null]";
        String string = "[Bundle: ";
        for (String key : bundle.keySet()) {
            string += key + "=" + bundle.get(key) + " ";
        }
        string += "]";
        return string;
    }

    /**
     * @return The package name for an app with a given PID.
     */
    public static String getPackageName(ActivityManager am, PackageManager pm, final int pID) {
        List<ActivityManager.RunningAppProcessInfo> processes = am.getRunningAppProcesses();
        for (ActivityManager.RunningAppProcessInfo process : processes) {
            try {
                if (process.pid == pID) {
                    ApplicationInfo info = pm.getApplicationInfo(process.processName, PackageManager.GET_META_DATA);
                    return info.packageName;
                }
            } catch (Throwable e) {
                LOGE(TAG, "Error checking process information.", e);
            }
        }
        return null;
    }

    /**
     * @return True if a {@link android.view.KeyEvent} corresponds to a media action.
     */
    public static boolean isMediaKeyCode(final int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MEDIA_PLAY_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_PLAY:
            case KeyEvent.KEYCODE_MEDIA_AUDIO_TRACK:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
            case KeyEvent.KEYCODE_MEDIA_CLOSE:
            case KeyEvent.KEYCODE_MEDIA_EJECT:
            case KeyEvent.KEYCODE_MEDIA_NEXT:
            case KeyEvent.KEYCODE_MEDIA_PREVIOUS:
            case KeyEvent.KEYCODE_MEDIA_STOP:
            case KeyEvent.KEYCODE_MEDIA_RECORD:
            case KeyEvent.KEYCODE_MEDIA_REWIND:
            case KeyEvent.KEYCODE_MEDIA_FAST_FORWARD:
                return true;
        }

        return false;
    }

    /**
     * @return True if a {@link android.view.KeyEvent} corresponds to a volume button.
     */
    public static boolean isVolumeKeyCode(final int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_VOLUME_UP:
            case KeyEvent.KEYCODE_VOLUME_DOWN:
            case KeyEvent.KEYCODE_VOLUME_MUTE:
                return true;
        }
        return false;
    }

    /** @return The next ringer mode according to the direction of the change. */
    public static int nextRingerMode(final int direction, final int mRingerMode, final boolean hasVibrator) {
        if (direction == ADJUST_SAME) return mRingerMode;
        int newRingerMode = mRingerMode;
        if (hasVibrator) {
            if (direction == ADJUST_RAISE) {
                switch (mRingerMode) {
                    case AudioManager.RINGER_MODE_VIBRATE:
                        newRingerMode = AudioManager.RINGER_MODE_NORMAL;
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        newRingerMode = AudioManager.RINGER_MODE_VIBRATE;
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        newRingerMode = AudioManager.RINGER_MODE_SILENT;
                        break;
                }
            } else {
                switch (mRingerMode) {
                    case AudioManager.RINGER_MODE_VIBRATE:
                        newRingerMode = AudioManager.RINGER_MODE_SILENT;
                        break;
                    case AudioManager.RINGER_MODE_SILENT:
                        newRingerMode = AudioManager.RINGER_MODE_NORMAL;
                        break;
                    case AudioManager.RINGER_MODE_NORMAL:
                        newRingerMode = AudioManager.RINGER_MODE_VIBRATE;
                        break;
                }
            }
        } else {
            // Without a vibrator, it's a binary state.
            switch (mRingerMode) {
                case AudioManager.RINGER_MODE_SILENT:
                    newRingerMode = AudioManager.RINGER_MODE_NORMAL;
                    break;
                case AudioManager.RINGER_MODE_NORMAL:
                    newRingerMode = AudioManager.RINGER_MODE_SILENT;
                    break;
            }
        }
        return newRingerMode;
    }

    /**
     * @return The {@link Bitmap} for the current wallpaper, or null if none exists.
     */
    public static Bitmap getWallpaperBitmap(WallpaperManager wManager, Context mContext) {
        if (null == wManager || null == mContext) return null;
        final Bitmap wallpaper = getCurrentWallpaperLocked(wManager, mContext);
        if (null != wallpaper) return wallpaper;
        final Drawable wallpaperD = wManager.getDrawable();
        if (null != wallpaperD) {
            return drawableToBitmap(wallpaperD);
        }
        return null;
    }

    /**
     * @returns A {@link Bitmap} for a {@link Drawable}.
     */
    public static Bitmap drawableToBitmap(final Drawable drawable) {
        if (null == drawable) return null;
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        final Bitmap bitmap = Bitmap.createBitmap(Math.max(0, drawable.getIntrinsicWidth()),
                Math.max(0, drawable.getIntrinsicHeight()), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(bitmap);

        // PictureDrawable's get handled separately.
        if (drawable instanceof PictureDrawable) {
            canvas.drawPicture(((PictureDrawable) drawable).getPicture());
            return bitmap;
        }

        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * @see android.app.WallpaperManager$Globals#getCurrentWallpaperLocked(Context)
     */
    public static Bitmap getCurrentWallpaperLocked(WallpaperManager wManager, Context mContext) {
        if (null == wManager) return null;

        final String GLOBALS = "Globals";

        try {
            // Find: android.app.WallpaperManager$Globals
            final Class<?>[] sClasses = wManager.getClass().getDeclaredClasses();
            for (final Class<?> sClass : sClasses) {
                final String sName = sClass.getSimpleName();
                if (TextUtils.isEmpty(sName)) continue;
                if (GLOBALS.equals(sName)) {
                    // Invoke the default constructor, Globals(Looper)
                    final Constructor<?> sConstructor = sClass.getDeclaredConstructor(Looper.class);
                    if (null != sConstructor) {
                        sConstructor.setAccessible(true);
                        final Object sGlobals = sConstructor.newInstance(mContext.getMainLooper());
                        // Call the getCurrentWallpaperLocked() method on our Globals Object.
                        final Method getBitmap = sClass.getDeclaredMethod("getCurrentWallpaperLocked", Context.class);
                        if (null != getBitmap) {
                            // It's private, but who cares?
                            getBitmap.setAccessible(true);
                            return (Bitmap) getBitmap.invoke(sGlobals, mContext);
                        }
                    }
                }
            }
        } catch (Throwable t) {
            LOGE(TAG, "Cannot call WallpaperManager#getCurrentWallpaperLocked", t);
        }

        return null;
    }

    private static RenderScript rs;

    /**
     * Applies a blur to a {@link Bitmap}.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN_MR1)
    public static Bitmap fastBlur(Context mContext, Bitmap sentBitmap, final int radius) {
        if (null == rs) {
            rs = RenderScript.create(mContext);
        }

        final Allocation in = Allocation.createFromBitmap(rs, sentBitmap,
                Allocation.MipmapControl.MIPMAP_NONE, Allocation.USAGE_SCRIPT);
        final Allocation out = Allocation.createTyped(rs, in.getType());
        final ScriptIntrinsicBlur script = ScriptIntrinsicBlur.create(rs, Element.U8_4(rs));
        script.setRadius(radius);
        script.setInput(in);
        script.forEach(out);
        out.copyTo(sentBitmap);
        return (sentBitmap);
    }

    /** @return The Bitmap for a View hierarchy. */
    public static Bitmap loadBitmapFromView(View v) {
        v.clearFocus();
        v.setPressed(false);
        if (v.getMeasuredHeight() <= 0) {
            v.measure(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            Bitmap b = Bitmap.createBitmap(v.getMeasuredWidth(), v.getMeasuredHeight(), Bitmap.Config.ARGB_8888);
            Canvas c = new Canvas(b);
            v.layout(0, 0, v.getMeasuredWidth(), v.getMeasuredHeight());
            v.draw(c);
            return b;
        }
        Bitmap b = Bitmap.createBitmap( v.getLayoutParams().width, v.getLayoutParams().height, Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(b);
        v.layout(v.getLeft(), v.getTop(), v.getRight(), v.getBottom());
        Drawable bgDrawable = v.getBackground();
        if (bgDrawable != null)
            bgDrawable.draw(c);
        v.draw(c);
        return b;
    }

    public static Bitmap loadBitmapFromViewCache(View v) {
        v.clearFocus();
        v.setPressed(false);

        boolean willNotCache = v.willNotCacheDrawing();
        v.setWillNotCacheDrawing(false);

        // Reset the drawing cache background color to fully transparent
        // for the duration of this operation
        int color = v.getDrawingCacheBackgroundColor();
        v.setDrawingCacheBackgroundColor(0);

        if (color != 0) {
            v.destroyDrawingCache();
        }
        v.buildDrawingCache();
        Bitmap cacheBitmap = v.getDrawingCache();
        if (null == cacheBitmap) return null;
        Bitmap bitmap = Bitmap.createBitmap(cacheBitmap);

        // Restore the view
        v.destroyDrawingCache();
        v.setWillNotCacheDrawing(willNotCache);
        v.setDrawingCacheBackgroundColor(color);

        return bitmap;
    }

    // Staticlly initialized variables for better performance (but more memory usage).
    private static final Paint monochromePaint = new Paint(Paint.ANTI_ALIAS_FLAG);
    private static final ColorMatrix monochromeMatrix = new ColorMatrix();
    private static final ColorMatrixColorFilter monochromeFilter;

    static {
        monochromeMatrix.setSaturation(0);
        monochromePaint.setAntiAlias(true);
        monochromeFilter = new ColorMatrixColorFilter(monochromeMatrix);
        monochromePaint.setColorFilter(monochromeFilter);
    }

    /** Makes a {@link Bitmap} monochrome. */
    public static Bitmap monochrome(Bitmap sentBitmap) {
        Bitmap bmpMonochrome = Bitmap.createBitmap(
                sentBitmap.getWidth(), sentBitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(bmpMonochrome);
        canvas.drawBitmap(sentBitmap, 0, 0, monochromePaint);
        return bmpMonochrome;
    }

    /** Masks a {@link android.graphics.Bitmap}. */
    public static Bitmap mask(Bitmap original, Bitmap mask) {
        Bitmap result = Bitmap.createBitmap(mask.getWidth(), mask.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas c = new Canvas(result);
        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.DST_IN));
        c.drawBitmap(original,0,0,null);
        c.drawBitmap(mask,0,0,paint);
        paint.setXfermode(null);
        return result;
     }

    public static void tap(View v) {
        int[] xy = new int[2];
        v.getLocationOnScreen(xy);

        final int viewWidth = v.getWidth();
        final int viewHeight = v.getHeight();

        final float x = xy[0] + (viewWidth / 2.0f);
        float y = xy[1] + (viewHeight / 2.0f);

        long downTime = SystemClock.uptimeMillis();
        long eventTime = SystemClock.uptimeMillis();

        MotionEvent event = MotionEvent.obtain(downTime, eventTime,
                MotionEvent.ACTION_DOWN, x, y, 0);
        if (v instanceof ViewGroup)
            ((ViewGroup) v).dispatchTouchEvent(event);
        else
            v.dispatchGenericMotionEvent(event);

        eventTime = SystemClock.uptimeMillis();
        final int touchSlop = ViewConfiguration.get(v.getContext()).getScaledTouchSlop();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_MOVE,
                x + (touchSlop / 2.0f), y + (touchSlop / 2.0f), 0);
        if (v instanceof ViewGroup)
            ((ViewGroup) v).dispatchTouchEvent(event);
        else
            v.dispatchGenericMotionEvent(event);

        eventTime = SystemClock.uptimeMillis();
        event = MotionEvent.obtain(downTime, eventTime, MotionEvent.ACTION_UP, x, y, 0);
        if (v instanceof ViewGroup)
            ((ViewGroup) v).dispatchTouchEvent(event);
        else
            v.dispatchGenericMotionEvent(event);
    }

    /**
     * Gets the list of available media receivers.
     * @return The list of {@code ResolveInfo} for different media button receivers.
     */
    public static List<ResolveInfo> getMediaReceivers(PackageManager packageManager) {
        if (null == packageManager) return new ArrayList<ResolveInfo>(0);
        Intent mediaButtonIntent = new Intent(Intent.ACTION_MEDIA_BUTTON);
        return packageManager.queryBroadcastReceivers(mediaButtonIntent,
                PackageManager.GET_INTENT_FILTERS | PackageManager.GET_RESOLVED_FILTER);
    }

    public static Set<String> getPackageNames(List<ResolveInfo> infos) {
        Set<String> strs = new HashSet<String>();
        if (null == infos) return strs;
        for (ResolveInfo info : infos)
            strs.add(info.resolvePackageName);
        return strs;
    }

    /** @return True if the device has phone capabilities. */
    public static boolean hasTelephone(Context context) {
        TelephonyManager telephonyManager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return (telephonyManager.getPhoneType() != TelephonyManager.PHONE_TYPE_NONE);
    }

    /** @return The timestamp of the last compilation/ build. */
    public static long lastBuildTime(Context context) {
        ZipFile zf = null;
        try{
            zf = new ZipFile(context.getPackageCodePath());
            ZipEntry ze = zf.getEntry("classes.dex");
            return ze.getTime();
        } catch(Throwable e) {
            LOGE(TAG, "Error obtaining build timestamp.", e);
        } finally {
            try {
                if (null != zf) zf.close();
            } catch (IOException ioe) {
                LOGE(TAG, "Error closing " + ZipFile.class.getSimpleName() + ".", ioe);
            }
        }
        return Long.MIN_VALUE;
    }

    /** @return A timestamp hash of the last build date. */
    public static CharSequence lastBuildTimestamp(Context context) {
        long time = lastBuildTime(context);
        if (time == Long.MIN_VALUE) return null;
        return pad(String.valueOf(time));
    }

    public static String pad(String s) {
        final String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final String target = "Q9A8ZWS7XEDC6RFVT5GBY4HNU3J2MI1KO0LP";

        char[] result = new char[s.length()];
        for (int i = 0; i < result.length; i++) {
            int index = source.indexOf(s.charAt(i));
            result[i] = target.charAt(index);
        }

        return new String(result);
    }

    public static String upad(String s) {
        final String source = "ABCDEFGHIJKLMNOPQRSTUVWXYZ0123456789";
        final String target = "Q9A8ZWS7XEDC6RFVT5GBY4HNU3J2MI1KO0LP";

        char[] result = new char[s.length()];
        for (int i = 0; i < result.length; i++) {
            int index = target.indexOf(s.charAt(i));
            result[i] = source.charAt(index);
        }

        return new String(result);
    }

    public static boolean isPackageInstalled(Context context, String pack) {
        if (null == context || TextUtils.isEmpty(pack)) return false;
        try {
            PackageInfo info = context.getPackageManager().getPackageInfo(pack, 0);
            return (info.packageName.equals(pack));
        } catch (PackageManager.NameNotFoundException nfe) {
            LOGE("Utils", "Could not find package: " + pack);
            return false;
        }
    }

    /**
     * {@link java.lang.Enum} representing the installer of an app.
     * TODO: update this structure with other services, if applicable.
     */
    public static enum Installer {
        UNKNOWN(null, "https://play.google.com/store/apps/details?id="),
        GOOGLE_PLAY("com.android.vending", "market://details?id="),
        AMAZON("com.amazon.venezia", "http://www.amazon.com/gp/mas/dl/android?p=");

        public String getPackageName() { return packageName; }
        public Intent getRateIntent(String appPackageName) {
            Intent launch = new Intent(Intent.ACTION_VIEW, Uri.parse(viewUrl + appPackageName));
            launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            return launch;
        }

        private String packageName;
        private String viewUrl;
        Installer(String packageName, String viewUrl) {
            this.packageName = packageName;
            this.viewUrl = viewUrl;
        }
    }

    /** @return The {@link me.barrasso.android.volume.utils.Utils.Installer} of this application. */
    public static Installer getInstaller(Context context) {
        if (("amazon").equals(Build.MANUFACTURER.toLowerCase(Locale.US)))
            return Installer.AMAZON;
        PackageManager packageManager = context.getPackageManager();
        String packageName = context.getPackageName();
        String installerPackageName = packageManager.getInstallerPackageName(packageName);
        if (!TextUtils.isEmpty(installerPackageName))
            for (Installer installer : Installer.values())
                if (installerPackageName.equals(installer.getPackageName()))
                    return installer;
        return Installer.UNKNOWN;
    }

    /** Launch the market-installed rate Activity with a default web URL. */
    public static boolean launchMarketActivity(Context context) {
        Installer installer = getInstaller(context);
        try {
            context.startActivity(installer.getRateIntent(context.getPackageName()));
            return true;
        } catch (ActivityNotFoundException ane) {
            LOGE(TAG, "Could not launch Market rate Activity.", ane);
            return false;
        }
    }

    public static void unbindDrawables(View view) {
        if (null == view) return;
        if (view.getBackground() != null) {
            view.getBackground().setCallback(null);
            view.setBackground(null);
        }
        if (view instanceof ViewGroup) {
            ViewGroup container = (ViewGroup) view;
            for (int i = 0, e = container.getChildCount(); i < e; i++) {
                unbindDrawables(container.getChildAt(i));
            }
            container.removeAllViews();
        } else if (view instanceof ImageView) {
            ImageView image = (ImageView) view;
            Drawable d = image.getDrawable();
            if (d instanceof BitmapDrawable) {
                Bitmap bmp = ((BitmapDrawable) d).getBitmap();
                if (null != bmp) {
                    bmp.recycle();
                }
            }
            image.setImageDrawable(null);
        }
    }

    static final Object mScreenshotLock = new Object();
    static ServiceConnection mScreenshotConnection = null;

    // Taken from PhoneWindowManager system service
    // Assume this is called from the Handler thread.
    public static void takeScreenshot(final Context mContext, final Handler mHandler) {
        synchronized (mScreenshotLock) {
            if (mScreenshotConnection != null) {
                return;
            }

            final Runnable mScreenshotTimeout = new Runnable() {
                @Override
                public void run() {
                    synchronized (mScreenshotLock) {
                        if (mScreenshotConnection != null) {
                            mContext.unbindService(mScreenshotConnection);
                            mScreenshotConnection = null;
                        }
                    }
                }
            };

            ComponentName cn = new ComponentName("com.android.systemui",
                    "com.android.systemui.screenshot.TakeScreenshotService");
            Intent intent = new Intent();
            intent.setComponent(cn);
            ServiceConnection conn = new ServiceConnection() {
                @Override
                public void onServiceConnected(ComponentName name, IBinder service) {
                    synchronized (mScreenshotLock) {
                        if (mScreenshotConnection != this) {
                            return;
                        }
                        Messenger messenger = new Messenger(service);
                        Message msg = Message.obtain(null, 1);
                        final ServiceConnection myConn = this;
                        Handler h = new Handler(mHandler.getLooper()) {
                            @Override
                            public void handleMessage(Message msg) {
                                synchronized (mScreenshotLock) {
                                    if (mScreenshotConnection == myConn) {
                                        mContext.unbindService(mScreenshotConnection);
                                        mScreenshotConnection = null;
                                        mHandler.removeCallbacks(mScreenshotTimeout);
                                    }
                                }
                            }
                        };
                        msg.replyTo = new Messenger(h);
                        msg.arg1 = msg.arg2 = 0;
                        try {
                            messenger.send(msg);
                        } catch (RemoteException e) {
                            LOGE(TAG, "Error sending reply message for screenshot.", e);
                        }
                    }
                }
                @Override
                public void onServiceDisconnected(ComponentName name) {}
            };

            if (mContext.bindService(intent, conn, Context.BIND_AUTO_CREATE)) {
                mScreenshotConnection = conn;
                mHandler.postDelayed(mScreenshotTimeout, 10000);
            }
        }
    }

    public static String getPreferencesString(SharedPreferences prefs) {
        if (null == prefs) return "";
        Map<String,?> keys = prefs.getAll();
        StringBuilder allPrefs = new StringBuilder();
        for(Map.Entry<String,?> entry : keys.entrySet()) {
            allPrefs.append(entry.getKey());
            allPrefs.append('=');
            allPrefs.append(String.valueOf(entry.getValue()));
            allPrefs.append('\n');
        }
        return allPrefs.toString();
    }

    public static boolean supportsMediaPlayback(String name) {
        return (WPVolumePanel.class.getSimpleName().equals(name) ||
                HeadsUpVolumePanel.class.getSimpleName().equals(name)) ||
                UberVolumePanel.class.getSimpleName().equals(name) ||
                BlackberryVolumePanel.class.getSimpleName().equals(name);
    }

    // API Compatibility

    private static final boolean HAS_MEDIA_CONTROLLER;
    static {
        boolean hasMCS = false;
        try {
            Class<?> clazz = Class.forName("me.barrasso.android.volume.MediaControllerService");
            hasMCS = (clazz != null);
        } catch (Throwable t) {
            hasMCS = false;
        }
        HAS_MEDIA_CONTROLLER = hasMCS;
    }

    /** API-Compatible version of accessing MediaControllerService's state. */
    public static boolean isMediaControllerRunning(Context context) {
        return (HAS_MEDIA_CONTROLLER && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                isMyServiceRunning(context, "me.barrasso.android.volume.MediaControllerService"));
    }

    /** API-Compatible version of accessing MediaControllerService's external state (enabled). */
    public static boolean isMediaControllerEnabled(Context context) {
        return (HAS_MEDIA_CONTROLLER && Build.VERSION.SDK_INT >= Build.VERSION_CODES.KITKAT &&
                isNotificationListenerServiceRunning(context, "MediaControllerService"));
    }

    public static long[] parseVibrate(String line) {
        if (TextUtils.isEmpty(line))
            return new long[0];
        long[] vibTimes;
        String[] vibs = line.split("[,]");
        vibTimes = new long[vibs.length];
        int k = -1;
        for (String vib : vibs)
            vibTimes[++k] = Long.parseLong(vib);
        return vibTimes;
    }

    // Thanks Llama

    public static boolean HasStupidMenuButtonInBottomRight()
    {
        return Build.MODEL.equals("PadFone 2");
    }

    public static boolean IsPileOfShitWhenDealingWithVibrateMode()
    {
        String str = Build.MODEL;
        return (str.equals("GT-I9500")) || (str.equals("SHV-E300K")) || (str.equals("SHV-E300L")) ||
               (str.equals("SHV-E300S")) || (str.equals("GT-I9505")) || (str.equals("SGH-I337"))  ||
               (str.equals("SGH-M919")) || (str.equals("SCH-I545")) || (str.equals("SPH-L720"))   ||
               (str.equals("SCH-R970")) || (str.equals("GT-I9508")) || (str.equals("SCH-I959"))   ||
               (str.equals("GT-I9502")) || (str.equals("SGH-N045")) || (str.equals("SAMSUNG-SGH-I337"));
    }

    public static boolean HasChronicallyStupidWayToSetPriorityModeWhenItShouldBeSilentMode()
    {
        return (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP);
    }

    private static final String APP_DETAILS_CLASS_NAME = "com.android.settings.InstalledAppDetails";
    private static final String APP_DETAILS_PACKAGE_NAME = "com.android.settings";
    private static final String APP_PKG_NAME_21 = "com.android.settings.ApplicationPkgName";
    private static final String APP_PKG_NAME_22 = "pkg";
    private static final String SCHEME = "package";

    public static void showInstalledAppDetails(Activity activity, String packageName)
    {
        Intent localIntent = new Intent();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.ECLAIR)
        {
            localIntent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
            localIntent.setData(Uri.fromParts(SCHEME, packageName, null));
            activity.startActivity(localIntent);
        } else {
            localIntent.setAction(Intent.ACTION_VIEW);
            localIntent.setClassName(APP_DETAILS_PACKAGE_NAME, APP_DETAILS_CLASS_NAME);
            localIntent.putExtra(APP_PKG_NAME_21, packageName);
            localIntent.putExtra(APP_PKG_NAME_22, packageName);
        }
    }
}