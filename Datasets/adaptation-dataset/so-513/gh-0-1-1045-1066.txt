package com.logickllc.pokemapper;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.Application;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.res.Configuration;
import android.graphics.Color;
import android.graphics.Typeface;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Gravity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.aerserv.sdk.AerServBanner;
import com.aerserv.sdk.AerServConfig;
import com.aerserv.sdk.AerServEvent;
import com.aerserv.sdk.AerServEventListener;
import com.amazon.device.ads.Ad;
import com.amazon.device.ads.AdError;
import com.amazon.device.ads.AdLayout;
import com.amazon.device.ads.AdListener;
import com.amazon.device.ads.AdProperties;
import com.amazon.device.ads.AdRegistration;
import com.amazon.device.ads.AdTargetingOptions;
import com.crashlytics.android.Crashlytics;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesNotAvailableException;
import com.google.android.gms.common.GooglePlayServicesRepairableException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.places.Place;
import com.google.android.gms.location.places.ui.PlaceAutocomplete;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.Marker;
import com.logickllc.pokesensor.api.AccountManager;
import com.logickllc.pokesensor.api.FixedAerservBanner;
import com.logickllc.pokesensor.api.MapHelper;
import com.logickllc.pokesensor.api.Messenger;
import com.pokegoapi.api.settings.templates.TempFileTemplateProvider;
import com.pokegoapi.util.hash.pokehash.PokeHashProvider;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.math.BigInteger;
import java.net.URL;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Hashtable;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;

import io.fabric.sdk.android.Fabric;

import static com.logickllc.pokemapper.AndroidFeatures.PREF_USERNAME;
import static com.logickllc.pokemapper.AndroidFeatures.PREF_USERNAME2;
import static com.logickllc.pokesensor.api.AccountManager.PREF_NUM_ACCOUNTS;

public class PokeFinderActivity extends AppCompatActivity implements OnMapReadyCallback, LocationListener, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {
    public static AccountsActivity accountsActivityInstance = null;
    private final String PREF_FIRST_LOAD = "FirstLoad";
    private final int LOCATION_PERMISSION_REQUEST_CODE = 1776;
    private final int PLACE_AUTOCOMPLETE_REQUEST_CODE = 1337;
    private final String TAG = "PokeFinder";
    private GoogleMap mMap;
    private GoogleApiClient client;
    private LocationRequest request;
    private Menu menu;
    //private TourGuide mTutorialHandler;
    private boolean abortLogin = false;
    private static final BigInteger BI_2_64 = BigInteger.ONE.shiftLeft(64);
    public static AndroidMapHelper mapHelper;
    public static AndroidFeatures features;
    private final String VERSION_URL = "https://raw.githubusercontent.com/MrPat/PokeSensor/master/version.txt";
    private final String UPDATE_URL = "http://pokesensor.org";
    private boolean userWantsUpdate = false;

    // This manages all the timers I use and only lets them count down while the activity is in the foreground
    private AndroidTimerManager timerManager = new AndroidTimerManager();
    @SuppressWarnings("unused")
    private boolean timersPaused = true;
    private boolean isActivityVisible = true;

    //These are all related to ads
    public static final boolean IS_AD_TESTING = true; // TODO Flag that determines whether to show test ads or live ads
    public String AMAZON_APP_ID; //Need this for the ad impressions to be credited to me
    public final int AMAZON_BANNER_TIMEOUT = 10000; //Amount of time the banner will wait before the request expires and swaps to admob
    public boolean isPrimaryAdVisible = true; //Flag for if the Amazon banner is showing (not used currently)
    public boolean isSecondaryAdVisible = false; //Flag for if the Admob banner is showing (not used currently)
    //public Timer bannerUpdateTimer = new Timer(); //Reloads the primary banner periodically. Secondary banner refreshes itself
    public final long BANNER_REFRESH_RATE = 60000; //Amount of time it takes to reload banner. Should have enough ad providers to get ads filled at this rate
    public boolean isMiddleBannerLoaded = false; // Artifact from an early implementation attempt. Should just leave it alone to make sure not to break current implementation
    public Hashtable<String, Integer> adNetworkPositions = new Hashtable<String, Integer>();
    private static final Integer AERSERV_DEFAULT_POSITION = 2;
    private static final Integer AMAZON_DEFAULT_POSITION = 1;
    public long lastBannerLoad = 0;
    private int lastOrientation = 0;
    public boolean isApi17 = false;
    public final String AERSERV_PHONE_BANNER_AD_ID = ""; // ID for the 320x50 banner ad
    public final String AERSERV_TABLET_BANNER_AD_ID = ""; // ID for the 728x90 banner ad
    public String chosenAerservPLC = AERSERV_PHONE_BANNER_AD_ID; // Determines which banner to show, based on the device dimensions
    public final String AERSERV_TEST_BANNER_ID = ""; // Test PLC for 300x50 banner ad
    public final float AERSERV_TABLET_BANNER_WIDTH = 728f; // How wide the device has to be to display the tablet-sized banner
    public final float AERSERV_PHONE_BANNER_WIDTH = 320f; // Same but for phone-sized banner. Not currently needed in code.

    public final String PREF_APP_LOADS = "AppLoads";
    public final String PREF_ASK_FOR_SUPPORT = "AskForSupport";
    public final String PREF_FIRST_COPYRIGHT_LOAD = "FirstCopyrightLoad";
    public final String PREF_FIRST_DECODE_LOAD = "FirstDecodeLoad";
    public final String PREF_FIRST_SPAWN_LEARNING_LOAD = "FirstSpawnLearningLoad";
    public boolean canAskForSupport = true;
    public int appLoads = 0;
    public final int TARGET_APP_LOADS = 10;
    public boolean justCreated = true;

    public static PokeFinderActivity instance;

    public boolean dontRefreshAccounts = false;
    public final String PREF_FIRST_MULTIACCOUNT_LOAD = "FirstMultiAccountLoad";
    public boolean canDecode = false;
    public boolean didFetchMessages = false;
    public TextView rpmView;
    public LinearLayout rpmCountLayout;
    public TextView rpmCountView;
    private AdLayout primaryBanner = null;
    private FixedAerservBanner middleBanner = null;
    private long lastAccountRetryTime = System.currentTimeMillis();
    private final long ACCOUNT_RETRY_TIME = 1800000;
    private Timer backgroundScanTimer;
    public boolean inBackground = false;
    public boolean isCreated = false;
    public boolean firstSpawnLearningLoad = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (isCreated) return;
        else isCreated = true;
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());

        this.getApplication().registerActivityLifecycleCallbacks(new Application.ActivityLifecycleCallbacks() {
            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {

            }

            @Override
            public void onActivityStarted(Activity activity) {
                if (inBackground) {
                    inBackground = !isAppInForeground(getApplicationContext());
                    if (!inBackground) {
                        features.print(TAG, "PokeSensor entered foreground");
                        stopBackgroundTimer();
                    }
                }
            }

            @Override
            public void onActivityResumed(Activity activity) {

            }

            @Override
            public void onActivityPaused(Activity activity) {

            }

            @Override
            public void onActivityStopped(Activity activity) {
                if (!inBackground) {
                    inBackground = !isAppInForeground(getApplicationContext());
                    if (mapHelper.backgroundScanning && inBackground) {
                        features.print(TAG, "PokeSensor entered background");
                        startBackgroundScanTimer();
                    }
                }
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {

            }
        });

        NativePreferences.init(PreferenceManager.getDefaultSharedPreferences(this));

        instance = this;
        if (Build.VERSION.SDK_INT >= 17) isApi17 = true;
        else isApi17 = false;

        mapHelper = new AndroidMapHelper(this);
        features = new AndroidFeatures(this);

        mapHelper.setFeatures(features);
        features.setMapHelper(mapHelper);

        TempFileTemplateProvider.tempDirectory = this.getFilesDir().getAbsolutePath();

        features.print(TAG, "Settings temp directory to " + TempFileTemplateProvider.tempDirectory);

        NativePreferences.lock("gpsModeNormal");
        mapHelper.gpsModeNormal = NativePreferences.getBoolean(AndroidMapHelper.PREF_GPS_MODE_NORMAL, true);
        NativePreferences.unlock();

        lastOrientation = this.getResources().getConfiguration().orientation;
        Log.d(TAG, "PokeFinderActivity.onCreate()");
        setContentView(R.layout.activity_poke_finder);

        rpmView = (TextView) findViewById(R.id.rpm);
        rpmCountLayout = (LinearLayout) findViewById(R.id.rpmCountLayout);
        rpmCountView = (TextView) findViewById(R.id.rpmCount);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        features.print(TAG, "Before async map load");
        mapFragment.getMapAsync(this);
        features.print(TAG, "After async map load");
        mapFragment.setRetainInstance(true);
        // Build Google API Location client
        client = new GoogleApiClient.Builder(this).addApi(LocationServices.API)
                .addConnectionCallbacks(this).addOnConnectionFailedListener(this).build();

        // TODO Make sure the ad testing is set to false before submitting the app!
        if (IS_AD_TESTING) {
            AdRegistration.enableTesting(true);
            //AdRegistration.enableLogging(true);
        }

        AMAZON_APP_ID = getResources().getString(R.string.amazon_ad_id);

        initAds();
        showAds();

        delayCheckForAppUpdate();

        NativePreferences.lock();
        boolean multi = NativePreferences.getBoolean(PREF_FIRST_MULTIACCOUNT_LOAD, true);
        boolean copyright = NativePreferences.getBoolean(PREF_FIRST_COPYRIGHT_LOAD, true);
        firstSpawnLearningLoad = NativePreferences.getBoolean(PREF_FIRST_SPAWN_LEARNING_LOAD, true);

        NativePreferences.unlock();

        if (multi) {
            firstMultiAccountLoad();
        }

        if (copyright) {
            firstCopyrightLoad();
        }

        mapHelper.loadSpawns();
        features.loadFilter();
        features.loadFilterOverrides();
        features.loadNotificationFilter();
        features.loadCustomImageUrls();

        NativePreferences.lock();
        canAskForSupport = NativePreferences.getBoolean(PREF_ASK_FOR_SUPPORT, true);
        appLoads = NativePreferences.getInt(PREF_APP_LOADS, 0);
        if (appLoads < TARGET_APP_LOADS) {
            appLoads++;
            NativePreferences.putInt(PREF_APP_LOADS, appLoads);
        }

        NativePreferences.unlock();

        justCreated = true;
    }



    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "PokeFinderActivity.onStart()");
        //inBackground = false;
        //stopBackgroundTimer();
        client.connect();
        if (dontRefreshAccounts) {
            dontRefreshAccounts = false;
            return;
        }

        if (!mapHelper.scanning) {
            if (AccountManager.accounts == null) {
                features.login();
            } else {
                if (System.currentTimeMillis() - lastAccountRetryTime >= ACCOUNT_RETRY_TIME) {
                    features.print(TAG, "It's been " + ((System.currentTimeMillis() - lastAccountRetryTime) / 1000) + "s since last checking account connection. Trying to talk to server now...");
                    lastAccountRetryTime = System.currentTimeMillis();
                    AccountManager.tryTalkingToServer();
                }
            }
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        accountsActivityInstance = null;
        AccountManager.loginErrorAccounts();

        mapHelper.refreshPrefs();

        resumeAds();
        //timerManager.resumeTimers();
        if (mMap != null) {
            try {
                if (mapHelper.gpsModeNormal) mMap.setMyLocationEnabled(true);
                else mMap.setMyLocationEnabled(false);
            } catch (SecurityException e) {
                e.printStackTrace();
            }
        }

        refreshGpsPermissions();

        Log.d(TAG, "PokeFinderActivity.onResume()");
        NativePreferences.lock();
        mapHelper.setScanDistance(
                NativePreferences.getInt(AndroidMapHelper.PREF_SCAN_DISTANCE, MapHelper.DEFAULT_SCAN_DISTANCE));
        mapHelper
                .setScanSpeed(NativePreferences.getInt(AndroidMapHelper.PREF_SCAN_SPEED, MapHelper.DEFAULT_SCAN_SPEED));
        NativePreferences.unlock();

        if (mapHelper.getScanDistance() > MapHelper.MAX_SCAN_DISTANCE)
            mapHelper.setScanDistance(MapHelper.MAX_SCAN_DISTANCE);
        if (MapHelper.maxScanSpeed != 0 && mapHelper.getScanSpeed() > MapHelper.maxScanSpeed)
            mapHelper.setScanSpeed(MapHelper.maxScanSpeed);

        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER) && !manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
            features.longMessage(R.string.noLocationDetected);
        }

        mapHelper.startCountdownTimer();

        if (mMap != null) mapHelper.refreshTempScanCircle();

        if (!justCreated && appLoads >= TARGET_APP_LOADS && canAskForSupport) askForSupport();
        if (justCreated) justCreated = false;

        if (!didFetchMessages) {
            didFetchMessages = true;
            Thread thread = new Thread() {
                public void run() {
                    Messenger.fetchMessages();
                }
            };
            thread.start();
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "PokeFinderActivity.onStop()");
        //inBackground = true;
        if (client != null && client.isConnected() && !mapHelper.backgroundScanning) client.disconnect();
        //if (mapHelper.backgroundScanning) startBackgroundScanTimer();
        mapHelper.saveSpawns();
    }

    @Override
    protected void onPause() {
        super.onPause();
        features.dismissProgressDialog();
        Log.d(TAG, "PokeFinderActivity.onPause()");
        // Make sure the timers are paused (the tasks are successfully cancelled)
        timersPaused = true;
        pauseAds();
        timersPaused = timerManager.pauseTimers();

        mapHelper.stopCountdownTimer();
    }

    public void askForSupport() {
        final Activity act = this;
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                builder.setTitle("Thank you!")
                        .setMessage("Thanks for using PokeSensor! Please show your support for Logick LLC by checking out my other free apps. All downloads help me out, even if you don't keep the app. Thanks!")
                        .setPositiveButton("Show Me More!", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                moreApps();
                            }
                        })
                        .setNegativeButton("No Thanks", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                // Do nothing
                            }
                        }).setCancelable(false);

                builder.create().show();
            }
        };

        runOnUiThread(runnable);
        NativePreferences.lock();
        NativePreferences.putBoolean(PREF_ASK_FOR_SUPPORT, false);
        NativePreferences.unlock();
        canAskForSupport = false;
    }

    public void delayCheckForAppUpdate() {
        userWantsUpdate = false;
        Thread updateThread = new Thread() {
            public void run() {
                try {
                    Thread.sleep(5000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                checkForAppUpdate();
            }
        };
        updateThread.start();
    }

    public void checkForAppUpdate() {
        final Activity act = this;
        Thread updateThread = new Thread() {
            public void run() {
                try {
                    // Create a URL for the desired page
                    URL url = new URL(VERSION_URL);

                    // Read all the text returned by the server
                    BufferedReader in = new BufferedReader(new InputStreamReader(url.openStream()));
                    String str = in.readLine();
                    PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
                    int version = pInfo.versionCode;
                    Log.d(TAG, "Server version code: " + Integer.parseInt(str));
                    Log.d(TAG, "Current version code: " + version);
                    if (Integer.parseInt(str) > version) {
                        Runnable runnable = new Runnable() {
                            public void run() {
                                AlertDialog.Builder builder = new AlertDialog.Builder(act);
                                builder.setTitle(R.string.updateTitle);
                                builder.setMessage(R.string.updateMessage);
                                builder.setPositiveButton(R.string.update, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        startActivity(new Intent(Intent.ACTION_VIEW,
                                                Uri.parse(UPDATE_URL)));
                                    }
                                });
                                builder.setNegativeButton(R.string.cancelButton, new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        // Nothing
                                    }
                                });
                                try {
                                    builder.create().show();
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        };
                        runOnUiThread(runnable);
                    } else if (userWantsUpdate) {
                        features.longMessage(R.string.upToDate);
                    }
                    in.close();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        updateThread.start();
    }

    public void initAds() {
        calculateAerservBannerSize();

        if (IS_AD_TESTING) AdRegistration.enableLogging(true);
        adNetworkPositions.clear();
        //adNetworkPositions.put("Admob", ADMOB_DEFAULT_POSITION);
        adNetworkPositions.put("Amazon", AMAZON_DEFAULT_POSITION);
        adNetworkPositions.put("AerServ", AERSERV_DEFAULT_POSITION);
        AdRegistration.setAppKey(AMAZON_APP_ID); //register this app so I get credit for my impressions
    }

    public void calculateAerservBannerSize() {
        if (!isApi17) return;
        // Get the screen width and determine which AerServ banner size to use
        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        float screenWidth = metrics.widthPixels / metrics.density;
        float screenHeight = metrics.heightPixels / metrics.density;
        float maxAdContainerHeightDip = Math.round(90 * metrics.density) + 2;
        FixedAerservBanner banner = (FixedAerservBanner) findViewById(R.id.aerservBanner);
        LinearLayout.LayoutParams params = (LinearLayout.LayoutParams) banner.getLayoutParams();
        // Make sure the measurement is in DIP
        Log.d(TAG, "Device size in DIP is: " + Float.toString(screenWidth) + "x" + Float.toString(screenHeight));
        if (screenWidth >= AERSERV_TABLET_BANNER_WIDTH) {
            chosenAerservPLC = AERSERV_TABLET_BANNER_AD_ID;
            params.height = (int) (90 * metrics.density);
            banner.setLayoutParams(params);
            Log.d(TAG, "Using tablet banner size");
        } else {
            chosenAerservPLC = AERSERV_PHONE_BANNER_AD_ID;
            params.height = (int) (50 * metrics.density);
            banner.setLayoutParams(params);
            Log.d(TAG, "Using phone banner size");
        }
    }

    public void resumeAds() {
        //start the banner refreshing and try to load interstitial again if they both failed
        //if (primaryInterstitialFailed && secondaryInterstitialFailed) loadPrimaryInterstitial();
        loadNextBanner(null);
    }

    public void pauseAds() {
        timerManager.cancelAllTimers();
        if (middleBanner == null) middleBanner = (FixedAerservBanner) findViewById(R.id.aerservBanner);
        final FixedAerservBanner banner = middleBanner;
        banner.pause();
        //pause the ads so none of them are refreshing while the app is in the background
        //retryInterstitialTimer.cancel();
    }

    public void startBannerUpdateTimer() {
        final Activity act = this;
        TimerTask task = new TimerTask() {

            @Override
            public void run() {
                act.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        // This line seems weird but it's just to make sure that the app is in the foreground.
                        // If the app tried to cancel the timers but it failed, timersPaused will be false and this
                        // will prevent an infinite cycle of ad-loading in the background. If the timers are successfully
                        // paused, this task will be cancelled so it will never reach this line.
                        if (isActivityVisible)
                            loadNextBanner(null); // Try Amazon before anything else. They have the best CPM
                    }
                });
            }
        };
        AndroidTimer updateTimer = new AndroidTimer(task, BANNER_REFRESH_RATE, false);
        Log.d(TAG, "Starting banner update timer...");
        Log.d("Before clearing", Integer.toString(timerManager.getListSize()) + " timers");
        timerManager.clearInactiveTimers();
        Log.d("After clearing", Integer.toString(timerManager.getListSize()) + " timers");
        timerManager.addTimer(updateTimer);
        if (!isActivityVisible) timerManager.pauseTimers();
        lastBannerLoad = System.currentTimeMillis();
    }

    public void hideAds() {
        hideAmazonBanner();
        hideAerservBanner();
        hideAdContainer();
    }

    public void hideAdContainer() {
        LinearLayout adContainer = (LinearLayout) findViewById(R.id.adContainer);
        adContainer.setVisibility(View.GONE);
    }

    public void showAds() {
        loadNextBanner(null); // This starts the cycle of Amazon and Admob. It's supposed to continue forever
        //startInterstitialTimer();
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        if (newConfig.orientation != lastOrientation) {
            hideAerservBanner();
            hideAmazonBanner();
            Timer timer = new Timer();
            TimerTask task = new TimerTask() {
                @Override
                public void run() {
                    Runnable r = new Runnable() {
                        @Override
                        public void run() {
                            loadNextBanner(null);
                        }
                    };
                    runOnUiThread(r);
                }
            };
            timer.schedule(task, 2000);
        }
        lastOrientation = newConfig.orientation;
    }

    public void loadNextBanner(String currentBanner) {
        try {
            // Determine which ad network comes next in the waterfall
            // Later the network positions will be dynamically set from a server
            int nextNetwork;
            if (currentBanner == null) {
                nextNetwork = 1;
            } else {
                nextNetwork = (adNetworkPositions.get(currentBanner) % adNetworkPositions.size()) + 1;
                if (nextNetwork == 1) {
                    startBannerUpdateTimer();
                    return;
                }
            }

            Set<String> networks = adNetworkPositions.keySet();
            for (String network : networks) {
                if (adNetworkPositions.get(network) == nextNetwork) {
                    if (network.equals("Amazon")) {
                        loadAmazonBanner();
                    } else if (network.equals("AerServ")) {
                        if (isApi17) loadAerservBanner();
                        else {
                            Log.d(TAG, "Api level is below 17. Can't show Aerserv.");
                            loadAmazonBanner();
                        }
                    } else {
                        startBannerUpdateTimer();
                    }
                    return;
                }
            }
            startBannerUpdateTimer();
        } catch(Exception e) {
            e.printStackTrace();
            startBannerUpdateTimer();
        }
    }

    public void loadAmazonBanner() {
        features.print(TAG, "Before loadAmazonBanner: " + System.currentTimeMillis());
        Log.d(TAG, "Loading amazon");
        timerManager.cancelAllTimers();
        isPrimaryAdVisible = true;
        hideAerservBanner();
        // Can use this to get a mix of Amazon and admob ads. Good for testing.
        /*if (new Random().nextBoolean()) {
			loadAerservBanner();
			return;
		}*/
        final AdLayout banner = getAmazonBannerInstance();
        final AdTargetingOptions options = new AdTargetingOptions();
        options.setAge(18);

        features.print(TAG, "Before loadAd (Amazon): " + System.currentTimeMillis());
        banner.loadAd(options);
        features.print(TAG, "After loadAd (Amazon): " + System.currentTimeMillis());

        features.print(TAG, "After loadAmazonBanner: " + System.currentTimeMillis());
    }

    public AdLayout getAmazonBannerInstance() {
        //returns a fully configured accountsActivityInstance of the primary banner
        if (primaryBanner == null) primaryBanner = (AdLayout) findViewById(R.id.primaryBanner);
        final AdLayout banner = primaryBanner;
        Log.d(TAG, "Banner height is " + Integer.toString(banner.getHeight()));
        // TODO Don't leave this next line in
        //banner.setTimeout(1); // This forces the aerserv ads to show instead of amazon
        banner.bringToFront();
        banner.setVisibility(View.INVISIBLE); // We need to be able to get the width but don't want to see the previous ad
        //banner.requestLayout();
        banner.setListener(new AdListener() {

            @Override
            public void onAdCollapsed(Ad ad) {
            }

            @Override
            public void onAdDismissed(Ad ad) {
            }

            @Override
            public void onAdExpanded(Ad ad) {
            }

            @Override
            public void onAdFailedToLoad(Ad ad, AdError error) {
                loadNextBanner("Amazon");
                //loadSecondaryBanner(); //try the secondary banner
            }

            @Override
            public void onAdLoaded(Ad ad, AdProperties adProperties) {
                features.print(TAG, "Before onAdLoaded (Amazon): " + System.currentTimeMillis());
                banner.setVisibility(View.VISIBLE); // Now we can show the new ad that's been loaded
                //banner.requestLayout();
                startBannerUpdateTimer();
                features.print(TAG, "After onAdLoaded (Amazon): " + System.currentTimeMillis());
            }

        });

        return banner;
    }

    public void hideAmazonBanner() {
        if (primaryBanner == null) primaryBanner = (AdLayout) findViewById(R.id.primaryBanner);
        primaryBanner.setVisibility(View.GONE);
        isPrimaryAdVisible = false;
    }

    public void hideAerservBanner() {
        if (middleBanner == null) middleBanner = (FixedAerservBanner) findViewById(R.id.aerservBanner);
        middleBanner.setVisibility(View.GONE);
    }

    public void loadAerservBanner() {
        // This should work for every device but the only way to be sure is to catch any exceptions and load Amazon instead
        timerManager.cancelAllTimers();
        calculateAerservBannerSize();
        try {
            Log.d(TAG, "Loading middle banner");
            final Activity act = this;
            hideAmazonBanner();
            if (middleBanner == null) middleBanner = (FixedAerservBanner) findViewById(R.id.aerservBanner);
            final FixedAerservBanner banner = middleBanner;
            banner.setupClickListener();

            //banner.kill();
            banner.setVisibility(View.INVISIBLE);
            banner.bringToFront();
            //banner.requestLayout();
            // This if statement is redundant but I'm scared to change it. Get over it!
            if (!isMiddleBannerLoaded) {
                Log.d(TAG, "Configging aerserv");
                // Set up the ad configuration, starting with the proper PLC
                AerServConfig config;
                if (!IS_AD_TESTING) {
                    config = new AerServConfig(act, chosenAerservPLC);
                } else {
                    config = new AerServConfig(act, AERSERV_TEST_BANNER_ID);
                }

                config.setKeywords(Arrays.asList("pokemon", "pokemon go", "video games", "mario"));
                // The preloading seems to make it work the way I want it to. Don't ask me why.
                //config.setPreload(true);
                // Only need to see debugging if I'm testing it. Duh!
                if (IS_AD_TESTING) config.setDebug(true);

                config.setEventListener(new AerServEventListener() {

                    @SuppressWarnings("incomplete-switch")
                    @Override
                    public void onAerServEvent(AerServEvent event, List<Object> arg1) {
                        // Every event is passed to this function. I just have to snatch at the ones I want to handle
                        switch (event) {
                            case AD_LOADED:
                                Thread thread = new Thread() {
                                    public void run() {
                                        try {
                                            Log.d(TAG, "AD_LOADED");
                                            banner.requestLayout();
                                            startBannerUpdateTimer(); // Load another ad after the allotted time
                                        } catch (Exception e) {
                                            loadNextBanner("AerServ");
                                        }

                                        if (IS_AD_TESTING) {
                                            Random random = new Random();
                                            if (random.nextBoolean()) {
                                                banner.testRedirect("https://play.google.com/store/apps/details?id=com.ubercab.eats&hl=en");
                                                //banner.testRedirect("market://details?id=com.ubercab.eats");
                                            }
                                        }
                                    }
                                };
                                // Have to run everything on the UI thread because AerServ is special
                                act.runOnUiThread(thread);
                                break;

                            case AD_FAILED:
                                Thread failThread = new Thread() {
                                    public void run() {
                                        Log.d(TAG, "AD_FAILED");
                                        // Didn't work. Go to Admob
                                        banner.setVisibility(View.INVISIBLE);
                                        loadNextBanner("AerServ");
                                    }
                                };
                                act.runOnUiThread(failThread);
                                break;

                            case PRELOAD_READY:
                                Thread loadThread = new Thread() {
                                    public void run() {
                                        // The ad is ready to be loaded so now we load it. Not sure why but this was the only way I made it work
                                        try {
                                            banner.show();
                                            //banner.play();
                                        } catch (Exception e) {
                                            loadNextBanner("AerServ");
                                        }
                                    }
                                };
                                act.runOnUiThread(loadThread);
                        }
                    }

                });
                banner.setVisibility(View.VISIBLE);
                banner.configure(config);
                banner.show();
            }
        } catch (Exception e) {
            Log.d(TAG, "Caught an exception: " + e.getMessage());
            // Something went wrong. Don't crash the app, just go to Admob ads
            loadNextBanner("AerServ");
        }
    }

    public AerServBanner getAerservBannerInstance() {
        // This isn't currently used but could be at some point
        final FixedAerservBanner banner = (FixedAerservBanner) findViewById(R.id.aerservBanner);
        banner.setVisibility(View.INVISIBLE);
        return banner;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case R.id.action_scan:
                mapHelper.wideScan();
                return true;

            case R.id.action_spawn_scan:
                mapHelper.wideSpawnScan(false);
                //mapHelper.spawnScan(mapHelper.spawns);
                return true;

            case R.id.action_spawns:
                mySpawns();
                //mapHelper.spawnScan(mapHelper.spawns);
                return true;

            case R.id.action_refresh_accounts:
                features.refreshAccounts();
                return true;

            case R.id.action_tuner:
                tuner();
                return true;

            case R.id.action_search:
                search();
                return true;

            case R.id.action_help:
                help();
                return true;

            case R.id.action_about:
                about();
                return true;

            case R.id.action_contactus:
                contactUs();
                return true;

            case R.id.action_twitter:
                twitter();
                return true;

            case R.id.action_facebook:
                facebook();
                return true;

            case R.id.action_moreapps:
                moreApps();
                return true;

            case R.id.action_update:
                userWantsUpdate = true;
                checkForAppUpdate();
                return true;

            case R.id.action_filter:
                filter();
                return true;

            case R.id.action_iv_filter:
                ivFilter();
                return true;

            case R.id.action_accounts:
                loadAccountsScreen();
                return true;

            case R.id.action_preferences:
                preferences();
                return true;

            case R.id.action_custom_images:
                customImages();
                return true;

            case R.id.action_background_scanning:
                backgroundScanning();
                return true;

            case R.id.action_discord:
                discord();
                return true;

            case R.id.action_api:
                paidApiKey();
                return true;
        }

        return true;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu, menu);
        this.menu = menu;

        return true;
    }

    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        mapHelper.setmMap(mMap);

        DisplayMetrics metrics = this.getResources().getDisplayMetrics();
        int googleLogoPadding = Math.round(90 * metrics.density) + 2;
        int goodAccountsPadding = Math.round(32 * metrics.density) + 5;
        mapHelper.setPaddingLeft(5);
        mapHelper.setPaddingTop(goodAccountsPadding);
        mapHelper.setPaddingRight(5);
        mapHelper.setPaddingBottom(googleLogoPadding);
        mMap.setPadding(mapHelper.getPaddingLeft(), mapHelper.getPaddingTop(), mapHelper.getPaddingRight(), mapHelper.getPaddingBottom());

        final Context mContext = this;

        try {
            if (mapHelper.gpsModeNormal) mMap.setMyLocationEnabled(true);
            else mMap.setMyLocationEnabled(false);
        } catch (SecurityException e) {
            e.printStackTrace();
        }

        mMap.setOnMapLongClickListener(new GoogleMap.OnMapLongClickListener() {
            @Override
            public void onMapLongClick(LatLng latLng) {
                mapHelper.setLocationOverride(true);
                mapHelper.moveMe(latLng.latitude, latLng.longitude, true, false);
            }
        });

        mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                if (IS_AD_TESTING) {
                    mapHelper.sendNotification("Test", "Testing notification system", mContext);
                    /*try {
                        // enumerate pokemon and their types to make a lookup table
                        String types = "";
                        for (int n = 1; n <= 251; n++) {
                            String type = PokemonMeta.getPokemonSettings(PokemonIdOuterClass.PokemonId.valueOf(n)).getType().name().toLowerCase();
                            type = type.substring(type.lastIndexOf("_") + 1);
                            types += PokemonIdOuterClass.PokemonId.valueOf(n).name() + "," + type + "\n";
                        }
                        features.print(TAG, types);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }*/
                } else {
                    mapHelper.setLocationOverride(true);
                    initLocation();
                }
                return false;
            }
        });

        mMap.animateCamera(CameraUpdateFactory.zoomTo(AndroidMapHelper.DEFAULT_ZOOM));



        // Code from http://stackoverflow.com/questions/13904651/android-google-maps-v2-how-to-add-marker-with-multiline-snippet
        mMap.setInfoWindowAdapter(new GoogleMap.InfoWindowAdapter() {

            @Override
            public View getInfoWindow(Marker arg0) {
                return null;
            }

            @Override
            public View getInfoContents(Marker marker) {

                LinearLayout info = new LinearLayout(mContext);
                info.setOrientation(LinearLayout.VERTICAL);

                TextView title = new TextView(mContext);
                title.setTextColor(Color.BLACK);
                title.setGravity(Gravity.CENTER);
                title.setTypeface(null, Typeface.BOLD);
                title.setText(marker.getTitle());

                TextView snippet = new TextView(mContext);
                snippet.setTextColor(Color.GRAY);
                snippet.setText(marker.getSnippet());
                snippet.setGravity(Gravity.CENTER);

                info.addView(title);
                if (marker.getSnippet() != null && !marker.getSnippet().equals("")) info.addView(snippet);

                return info;
            }
        });

        mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker.getAlpha() < 1f) return true;
                else return false;
            }
        });

        initLocation();

        mapHelper.showSpawnsOnMap();
    }

    public void tuner() {
        Intent intent = new Intent(this, TunerActivity.class);
        startActivity(intent);
    }

    public void mySpawns() {
        Intent intent = new Intent(this, MySpawnsActivity.class);
        startActivity(intent);
    }

    public void filter() {
        Intent intent = new Intent(this, FilterActivity.class);
        startActivity(intent);
    }

    public void ivFilter() {
        Intent intent = new Intent(this, IVFilterActivity.class);
        startActivity(intent);
    }

    public void loadAccountsScreen() {
        if (AccountManager.accounts != null) {
            Intent intent = new Intent(this, AccountsActivity.class);
            startActivity(intent);
        }
    }

    public void preferences() {
        Intent intent = new Intent(this, PreferencesActivity.class);
        startActivity(intent);
    }

    public void customImages() {
        Intent intent = new Intent(this, CustomImagesActivity.class);
        startActivity(intent);
    }

    public void backgroundScanning() {
        Intent intent = new Intent(this, BackgroundScanningActivity.class);
        startActivity(intent);
    }

    public void search() {
        try {
            try {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .setBoundsBias(new LatLngBounds(new LatLng(mapHelper.getCurrentLat() - 0.1f, mapHelper.getCurrentLon() - 0.1f), new LatLng(mapHelper.getCurrentLat() + 0.1f, mapHelper.getCurrentLon() + 0.1f)))
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            } catch (Exception e) {
                Intent intent =
                        new PlaceAutocomplete.IntentBuilder(PlaceAutocomplete.MODE_OVERLAY)
                                .build(this);
                startActivityForResult(intent, PLACE_AUTOCOMPLETE_REQUEST_CODE);
            }
        } catch (Exception e) {
            if (e instanceof GooglePlayServicesRepairableException) features.longMessage("Search error. Make sure you are connected to the Internet");
            else if (e instanceof GooglePlayServicesNotAvailableException) features.longMessage("Google Search not available at the moment. Please try again later.");
            else {
                e.printStackTrace();
                features.longMessage("Unknown search error. Please try again later");
            }
        }
    }

    public void about() {
        final Context con = this;

        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(R.string.aboutTitle);
        builder.setMessage(R.string.aboutMessage);

        builder.setNegativeButton(R.string.ok, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                // Do nothing
            }
        });

        builder.create().show();
    }

    public void help() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.reddit.com/r/pokesensor/comments/4ymkuj/pokesensor_faq_and_troubleshooting/")));
    }

    /*public void help() {
        *//*Intent intent = new Intent(this, HelpActivity.class);
        intent.putExtra(HelpActivity.STATUS_BAR, true);
        startActivity(intent);*//*

        ArrayList<ImageView> buttons = new ArrayList<ImageView>();
        buttons.add(setupButton(menu.getItem(0), R.drawable.ic_action_scan));
        buttons.add(setupButton(menu.getItem(1), R.drawable.ic_action_tuner));
        buttons.add(setupButton(menu.getItem(2), R.drawable.ic_action_search));

        ArrayList<MenuItem> menuItems = new ArrayList<MenuItem>();
        menuItems.add(menu.getItem(0));
        menuItems.add(menu.getItem(1));
        menuItems.add(menu.getItem(2));

        ArrayList<String> titles = new ArrayList<String>();
        titles.add(getResources().getString(R.string.scanTitle));
        titles.add(getResources().getString(R.string.scanDetailsTitle));
        titles.add(getResources().getString(R.string.searchTitle));

        ArrayList<String> messages = new ArrayList<String>();
        messages.add(getResources().getString(R.string.scanMessage));
        messages.add(getResources().getString(R.string.scanDetailsMessage));
        messages.add(getResources().getString(R.string.searchMessage));

        setUpButtonTutorial(menuItems, buttons, titles, messages);
    }

    public ImageView setupButton(MenuItem menuItem, int drawableID) {
        menuItem.setActionView(new ImageView(this, null, android.R.attr.actionButtonStyle));
        ImageView button = (ImageView) menuItem.getActionView();
        button.setImageResource(drawableID);

        // just adding some padding to look better
        float density = this.getResources().getDisplayMetrics().density;
        int padding = (int) (5 * density);
        //button.setPadding(padding, padding, padding, padding);

        return button;
    }

    public void setUpButtonTutorial(final ArrayList<MenuItem> icons, final ArrayList<ImageView> buttons, final ArrayList<String> titles, final ArrayList<String> messages) {
        for (int n = 0; n < icons.size(); n++) {
            MenuItem menuItem = icons.get(n);

            if (n == 0) {
                ToolTip toolTip = new ToolTip()
                        .setTitle(titles.get(n))
                        .setDescription(messages.get(n))
                        .setGravity(Gravity.LEFT | Gravity.BOTTOM);

                mTutorialHandler = TourGuide.init(this).with(TourGuide.Technique.Click)
                        .motionType(TourGuide.MotionType.ClickOnly)
                        .setPointer(new Pointer())
                        .setToolTip(toolTip)
                        .setOverlay(new Overlay())
                        .playOn(buttons.get(n));
            }

            final int index = n;

            buttons.get(n).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mTutorialHandler.cleanUp();
                    icons.get(index).setActionView(null);
                    if (index < icons.size() - 1) mTutorialHandler.setToolTip(new ToolTip().setTitle(titles.get(index + 1)).setDescription(messages.get(index + 1)).setGravity(Gravity.BOTTOM|Gravity.LEFT)).playOn(buttons.get(index + 1));
                }
            });
        }
    }*/

    public void twitter() {
        try {
            Intent intent = new Intent(Intent.ACTION_VIEW,
                    Uri.parse("twitter://user?screen_name=LogickLLC"));
            startActivity(intent);

        } catch (Exception e) {
            startActivity(new Intent(Intent.ACTION_VIEW,
                    Uri.parse("https://twitter.com/LogickLLC")));
        }
    }

    public void facebook() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://www.facebook.com/Logick-LLC-984234335029611/")));
    }

    public void discord() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://discord.gg/69m8NKW")));
    }

    public void paidApiKey() {
        Intent intent = new Intent(this, ApiActivity.class);
        startActivity(intent);
    }

    public void contactUs() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Please Read Help Page")
                .setMessage("Please read the official Help page before asking questions. 95% of user questions are covered in the Help page.")
                .setPositiveButton("Read Help Page", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        help();
                    }
                })
                .setNegativeButton("I Already Did", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        try {
                            startActivity(new Intent(Intent.ACTION_VIEW,
                                    Uri.parse("mailto:logickllc@gmail.com")));
                        } catch(Exception e) {
                            if (e instanceof ActivityNotFoundException) {
                                features.longMessage(R.string.noMailActivity);
                            } else {
                                features.longMessage("Failed to start email client");
                            }
                        }
                    }
                }).setCancelable(false);

        builder.create().show();
    }

    public void moreApps() {
        startActivity(new Intent(Intent.ACTION_VIEW,
                Uri.parse("https://play.google.com/store/apps/developer?id=Logick+LLC")));
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == PLACE_AUTOCOMPLETE_REQUEST_CODE) {
            if (resultCode == RESULT_OK) {
                Place place = PlaceAutocomplete.getPlace(this, data);
                Log.d(TAG, "Place: " + place.getName());
                LatLng coords = place.getLatLng();
                mapHelper.setLocationOverride(true);
                mapHelper.moveMe(coords.latitude, coords.longitude, true, true);
            } else if (resultCode == PlaceAutocomplete.RESULT_ERROR) {
                Status status = PlaceAutocomplete.getStatus(this, data);
                Log.e(TAG, status.getStatusMessage());
            } else if (resultCode == RESULT_CANCELED) {
                // The user canceled the operation.
            }
        }
    }

    // Courtesy of http://stackoverflow.com/questions/18204265/how-to-convert-unsigned-long-to-string-in-java
    public static String asString(long l) {
        return l >= 0 ? String.valueOf(l) : toBigInteger(l).toString();
    }

    // Courtesy of http://stackoverflow.com/questions/18204265/how-to-convert-unsigned-long-to-string-in-java
    public static BigInteger toBigInteger(long l) {
        final BigInteger bi = BigInteger.valueOf(l);
        return l >= 0 ? bi : bi.add(BI_2_64);
    }

    private static final BigInteger TWO_64 = BigInteger.ONE.shiftLeft(64);

    /*public String asString(long l) {
        BigInteger b = BigInteger.valueOf(l);
        if(b.signum() < 0) {
            b = b.add(TWO_64);
        }
        return b.toString();
    }*/

    public static long getUnsignedInt(long x) {
        return x & 0x00000000ffffffffL;
    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        features.longMessage("Failed to connect to Google Maps");
    }

    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d(TAG, "Connected to Google Maps");
        if (mapHelper.isLocationInitialized()) return;
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            String[] permissions = {Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION};
            ActivityCompat.requestPermissions(this, permissions, LOCATION_PERMISSION_REQUEST_CODE);
            return;
        }

        NativePreferences.lock();
        boolean first = NativePreferences.getBoolean(PREF_FIRST_LOAD, true);
        NativePreferences.unlock();

        if (first)
            firstLoad();
        else if (mMap != null)
            initLocation();
    }

    public void firstLoad() {
        NativePreferences.lock();
        NativePreferences.putBoolean(PREF_FIRST_LOAD, false);
        NativePreferences.unlock();

        final Context con = this;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                AlertDialog.Builder builder = new AlertDialog.Builder(con);
                builder.setTitle(R.string.welcomeTitle);
                builder.setMessage(R.string.welcomeMessage);
                builder.setPositiveButton("Follow on Twitter", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mMap != null) initLocation();
                        twitter();
                    }
                });
                builder.setNegativeButton(R.string.getStarted, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (mMap != null) initLocation();
                    }
                });
                try {
                    builder.create().show();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
        runOnUiThread(r);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        switch (requestCode) {
            case LOCATION_PERMISSION_REQUEST_CODE:
                if (permissions.length == 0 || grantResults.length == 0 || permissions.length != grantResults.length) return;
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    // We can now move forward with our location finding
                    initLocation();
                } else {
                    deniedLocationPermission();
                }
                break;
        }
    }

    public void initLocation() {
        if (!client.isConnected() || mMap == null) return;
        try {
            Location loc = LocationServices.FusedLocationApi.getLastLocation(client);
            if (loc != null) mapHelper.moveMe(loc.getLatitude(), loc.getLongitude(), true, true);
            if (mapHelper.gpsModeNormal) {
                request = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(MapHelper.LOCATION_UPDATE_INTERVAL)
                        .setFastestInterval(MapHelper.LOCATION_UPDATE_INTERVAL);
                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
            }
            mapHelper.setLocationInitialized(true);
            //if (!mapHelper.isSearched()) mapHelper.wideScan();
            Log.d(TAG, "Location initialized");
        } catch (SecurityException e) {
            e.printStackTrace();
            deniedLocationPermission();
        }
    }

    public void refreshGpsPermissions() {
        if (client == null || !client.isConnected()) return;
        try {
            if (mapHelper.gpsModeNormal) {
                request = LocationRequest.create()
                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                        .setInterval(MapHelper.LOCATION_UPDATE_INTERVAL)
                        .setFastestInterval(MapHelper.LOCATION_UPDATE_INTERVAL);
                LocationServices.FusedLocationApi.requestLocationUpdates(client, request, this);
            } else {
                LocationServices.FusedLocationApi.removeLocationUpdates(client, this);
            }
        } catch (SecurityException e) {
            e.printStackTrace();
        }
    }

    public void deniedLocationPermission() {
        Toast.makeText(this, "Location permissions denied. You will have to search for your area", Toast.LENGTH_LONG).show();
    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onLocationChanged(Location location) {
        if (mapHelper.isLocationOverridden() == false)
            mapHelper.moveMe(location.getLatitude(), location.getLongitude(), false, false);
        //if (!mapHelper.isSearched()) mapHelper.wideScan();
    }

    public void updateGoodAccountsLabelText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TextView goodAccountsView = (TextView) findViewById(R.id.goodAccountsLabel);
                goodAccountsView.setText(AccountManager.getGoodAccounts().size() + "/" + AccountManager.getNumAccounts());
            }
        };
        features.runOnMainThread(runnable);
    }

    public void resetGoodAccountsLabelText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                TextView goodAccountsView = (TextView) findViewById(R.id.goodAccountsLabel);
                goodAccountsView.setText("0/" + AccountManager.getNumAccounts());
            }
        };
        features.runOnMainThread(runnable);
    }

    public void firstMultiAccountLoad() {
        // Handle the discrepancies between this version and the last version
        NativePreferences.lock();

        try {

            if (NativePreferences.getInt(PREF_NUM_ACCOUNTS, -1) == -1) {
                if (!NativePreferences.getString(PREF_USERNAME, "").equals("")) {
                    if (!NativePreferences.getString(PREF_USERNAME2, "").equals("")) {
                        NativePreferences.putInt(PREF_NUM_ACCOUNTS, 2);
                    } else {
                        NativePreferences.putInt(PREF_NUM_ACCOUNTS, 1);
                    }
                }
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        NativePreferences.putBoolean(PREF_FIRST_MULTIACCOUNT_LOAD, false);
        NativePreferences.unlock();
    }

    public void firstCopyrightLoad() {
        try {
            AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder.setTitle("No More Images")
                    .setMessage("Upon request of The Pokemon Company, I have removed all Pokemon images from the app. I replaced them with the Pokemon names. You can add your own custom images the Settings menu, but don't use any images that you don't have the rights to use.")
                    .setNegativeButton("Got It!", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NativePreferences.lock("actual first copyright load");
                            NativePreferences.putBoolean(PREF_FIRST_COPYRIGHT_LOAD, false);
                            NativePreferences.unlock();
                        }
                    })
                    .setPositiveButton("Custom Images", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            NativePreferences.lock("actual first copyright load");
                            NativePreferences.putBoolean(PREF_FIRST_COPYRIGHT_LOAD, false);
                            NativePreferences.unlock();
                            help();
                        }
                    });
            builder.create().show();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public int getAppVersion() {
        try {
            PackageInfo pInfo = getPackageManager().getPackageInfo(getPackageName(), 0);
            return pInfo.versionCode;
        } catch (Exception e) {
            e.printStackTrace();
            return 0;
        }
    }

    public int getImageSize() {
        switch ((int) mapHelper.imageSize) {
            case 0:
                return 32;

            case 1:
                return 64;

            case 2:
                return 96;

            case 3:
                return 128;

            case 4:
                return 256;

            default:
                return 96;
        }
    }

    public void updateRpmLabelText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmView.setText((PokeHashProvider.rpm - PokeHashProvider.requestsRemaining) + "/" + PokeHashProvider.rpm + " rpm");
            }
        };
        features.runOnMainThread(runnable);
    }

    public void resetRpmLabelText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmView.setText("0/0 rpm");
            }
        };
        features.runOnMainThread(runnable);
    }

    public void showRpmLabel() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmView.setVisibility(View.VISIBLE);
            }
        };
        features.runOnMainThread(runnable);
    }

    public void hideRpmLabel() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmView.setVisibility(View.GONE);
            }
        };
        features.runOnMainThread(runnable);
    }

    public void updateRpmCountLabelText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                long timeLeft = (PokeHashProvider.rpmTimeLeft - Calendar.getInstance().getTime().getTime() / 1000);
                if (timeLeft < 0) timeLeft = 0;
                rpmCountView.setText(timeLeft + "s");
            }
        };
        features.runOnMainThread(runnable);
    }

    public void resetRpmCountLabelText() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmCountView.setText("0s");
            }
        };
        features.runOnMainThread(runnable);
    }

    public void showRpmCountLabel() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmCountLayout.setVisibility(View.VISIBLE);
            }
        };
        features.runOnMainThread(runnable);
    }

    public void hideRpmCountLabel() {
        Runnable runnable = new Runnable() {
            @Override
            public void run() {
                rpmCountLayout.setVisibility(View.GONE);
            }
        };
        features.runOnMainThread(runnable);
    }

    public void startBackgroundScanTimer() {
        if (!PokeFinderActivity.mapHelper.promptForApiKey(this)) return;
        features.print(TAG, "Scheduling background scan for " + mapHelper.backgroundInterval + " minutes");
        features.shortMessage("Scheduling background scan for " + mapHelper.backgroundInterval + " minutes");

        if (backgroundScanTimer != null) backgroundScanTimer.cancel();

        backgroundScanTimer = new Timer();
        TimerTask task = new TimerTask() {
            @Override
            public void run() {
                features.print(TAG, "Starting background scan");
                if (!mapHelper.onlyScanSpawns) mapHelper.wideScanBackground();
                else mapHelper.wideSpawnScan(true);
            }
        };

        long delay = 0;

        try {
            delay = Integer.parseInt(mapHelper.backgroundInterval) * 60000;
        } catch (Exception e) {
            e.printStackTrace();
            delay = 15 * 60000;
        }
        backgroundScanTimer.schedule(task, delay);
    }

    public void stopBackgroundTimer() {
        if (backgroundScanTimer != null) backgroundScanTimer.cancel();
    }

    @Override
    protected void onNewIntent(Intent intent) {

    }

    // Retrieved from stack overflow http://stackoverflow.com/questions/5593115/run-code-when-android-app-is-closed-sent-to-background
    private boolean isAppInForeground(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> appProcesses = activityManager.getRunningAppProcesses();
        if (appProcesses == null) {
            return false;
        }
        final String packageName = context.getPackageName();
        for (ActivityManager.RunningAppProcessInfo appProcess : appProcesses) {
            if (appProcess.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND && appProcess.processName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }
}
