/*
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 *  Copyright (c) 2011, Janrain, Inc.
 *
 *  All rights reserved.
 *
 *  Redistribution and use in source and binary forms, with or without modification,
 *  are permitted provided that the following conditions are met:
 *
 *  * Redistributions of source code must retain the above copyright notice, this
 *    list of conditions and the following disclaimer.
 *  * Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation and/or
 *    other materials provided with the distribution.
 *  * Neither the name of the Janrain, Inc. nor the names of its
 *    contributors may be used to endorse or promote products derived from this
 *    software without specific prior written permission.
 *
 *
 *  THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 *  ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 *  WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 *  DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 *  ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 *  (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 *  LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON
 *  ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 *  (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 *  SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * * *
 */
package com.janrain.android.multidex.simpledemonative;

import android.accounts.AccountManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.StrictMode;
import android.support.multidex.MultiDex;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.LocalBroadcastManager;
import android.text.InputType;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.AccessTokenTracker;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.GoogleAuthException;
import com.google.android.gms.auth.GoogleAuthUtil;
import com.google.android.gms.auth.GooglePlayServicesAvailabilityException;
import com.google.android.gms.auth.UserRecoverableAuthException;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.common.AccountPicker;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.Scopes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.Scope;
import com.google.android.gms.plus.Plus;

import com.google.android.gms.plus.Plus;
import com.twitter.sdk.android.Twitter;
import com.twitter.sdk.android.core.TwitterAuthConfig;
import com.twitter.sdk.android.core.Callback;
import com.twitter.sdk.android.core.Result;
import com.twitter.sdk.android.core.TwitterAuthToken;
import com.twitter.sdk.android.core.TwitterException;
import com.twitter.sdk.android.core.TwitterSession;

import com.janrain.android.Jump;
import com.janrain.android.capture.CaptureApiError;
import com.janrain.android.engage.JREngage;
import com.janrain.android.engage.types.JRActivityObject;
import com.janrain.android.utils.LogUtils;
import com.twitter.sdk.android.core.identity.TwitterAuthClient;

import org.json.JSONObject;

import java.io.IOException;
import java.util.Arrays;
import java.util.Set;

import io.fabric.sdk.android.Fabric;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;
import static com.janrain.android.capture.Capture.CaptureApiRequestCallback;

public class MainActivity extends FragmentActivity implements
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener {

    // Note: Your consumer key and secret should be obfuscated in your source code before shipping.
    private static final String TWITTER_KEY = "UPDATE";
    private static final String TWITTER_SECRET = "UPDATE";


    //Facebook SDK
    private CallbackManager facebookCallbackManager;
    private AccessTokenTracker facebookAccessTokenTracker;
    private AccessToken facebookToken;
    private static String facebookEmail;

    private boolean flowDownloaded = false;

    /* Google Request code used to invoke sign in user interactions. */
    private static final int GOOGLE_REQUEST_CODE_SIGN_IN = 0;
    private static final int GOOGLE_REQUEST_CODE_PICK_ACCOUNT = 1000;
    private static final int GOOGLE_REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR = 1001;
    private static final int GOOGLE_REQUEST_SIGN_IN_REQUIRED = 55664;

    /* Facebook Request code used to invoke sign in user interactions. */
    private static final int FACEBOOK_REQUEST_CODE_SIGN_IN = 64206;

    /* Twitter Request code used to invoke sign in user interactions. */
    private static final int TWITTER_REQUEST_CODE_SIGN_IN = 140;

    /* Client used to interact with Google APIs. */
    private GoogleApiClient mGoogleApiClient;

    // Received from newChooseAccountIntent(); passed to getToken()
    private static String googleEmail;
    private static String googleToken;
    private static final String GOOGLE_SCOPES = "oauth2:profile email";

    //Twitter
    private TwitterAuthClient twitterAuthClient;
    private TwitterAuthToken twitterToken;
    private static String twitterEmail;

    /* Is there a ConnectionResult resolution in progress? */
    private boolean mIsResolving = false;

    /* Should we automatically resolve ConnectionResults when possible? */
    private boolean mShouldResolve = false;

    private class MySignInResultHandler implements Jump.SignInResultHandler, Jump.SignInCodeHandler {
        public void onSuccess() {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setMessage("Sign-in complete.");
            b.setNeutralButton("Dismiss", null);
            b.show();
        }

        public void onCode(String code) {
            Toast.makeText(MainActivity.this, "Authorization Code: " + code, Toast.LENGTH_LONG).show();
        }

        public void onFailure(SignInError error) {
            if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR &&
                    error.captureApiError.isMergeFlowError()) {
                // Called below is the default merge-flow handler. Merge behavior may also be implemented by
                // headless-native-API for more control over the user experience.
                //
                // To do so, call Jump.showSignInDialog or Jump.performTraditionalSignIn directly, and
                // pass in the merge-token and existing-provider-name retrieved from `error`.
                //
                // String mergeToken = error.captureApiError.getMergeToken();
                // String existingProvider = error.captureApiError.getExistingAccountIdentityProvider()
                //
                // (An existing-provider-name of "capture" indicates a conflict with a traditional-sign-in
                // account. You can handle this case yourself, by displaying a dialog and calling
                // Jump.performTraditionalSignIn, or you can call Jump.showSignInDialog(..., "capture") and
                // a library-provided dialog will be provided.)

                Jump.startDefaultMergeFlowUi(MainActivity.this, error, signInResultHandler);
            } else if (error.reason == SignInError.FailureReason.CAPTURE_API_ERROR &&
                    error.captureApiError.isTwoStepRegFlowError()) {
                // Called when a user cannot sign in because they have no record, but a two-step social
                // registration is possible. (Which means that the error contains pre-filled form fields
                // for the registration form.
                Intent i = new Intent(MainActivity.this, com.janrain.android.multidex.simpledemonative.RegistrationActivity.class);
                JSONObject prefilledRecord = error.captureApiError.getPreregistrationRecord();
                i.putExtra("preregistrationRecord", prefilledRecord.toString());
                i.putExtra("socialRegistrationToken", error.captureApiError.getSocialRegistrationToken());
                MainActivity.this.startActivity(i);
            } else {
                AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                b.setMessage("Sign-in failure:" + error);
                b.setNeutralButton("Dismiss", null);
                b.show();
            }
        }
    }



    private final MySignInResultHandler signInResultHandler = new MySignInResultHandler();

    private final BroadcastReceiver messageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
            b.setMessage("Could not download flow.");
            b.setNeutralButton("Dismiss", null);
            b.show();
        }
    };

    private final BroadcastReceiver flowMessageReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            flowDownloaded = true;
            Bundle extras = intent.getExtras();
            if (extras != null) {
                String state = extras.getString("message");
                Toast.makeText(MainActivity.this, state, Toast.LENGTH_LONG).show();
            }

        }
    };


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //enableStrictMode();

        // Configure sign-in to request the user's ID, email address, and basic
//      // profile. ID and basic profile are included in DEFAULT_SIGN_IN.

        // Build GoogleApiClient with access to basic profile
        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(new Scope(Scopes.PROFILE))
                .addScope(new Scope(Scopes.EMAIL))
                .build();

        //Initialize Facebook SDK
        FacebookSdk.sdkInitialize(getApplicationContext());
        facebookCallbackManager = CallbackManager.Factory.create();

        facebookAccessTokenTracker = new AccessTokenTracker() {
            @Override
            protected void onCurrentAccessTokenChanged(
                    AccessToken oldAccessToken,
                    AccessToken currentAccessToken) {
                // Set the access token using
                // currentAccessToken when it's loaded or set.
                facebookToken = currentAccessToken;
                if(facebookToken != null) {
                    LogUtils.logd("Facebook Access Token:" + facebookToken.getToken());
                    LogUtils.logd("Facebook User ID:" + facebookToken.getUserId());
                }else{
                    LogUtils.logd("Facebook Access Token Cleared");
                }
                GraphRequest.newMeRequest(
                        currentAccessToken, new GraphRequest.GraphJSONObjectCallback() {
                            @Override
                            public void onCompleted(JSONObject me, GraphResponse response) {
                                if (response.getError() != null) {
                                    LogUtils.loge("Unable to retrieve Facebook Email: " + response.getError().getErrorMessage());
                                } else {
                                    facebookEmail = me.optString("email");
                                }
                            }
                        }).executeAsync();
            }
        };

        LoginManager.getInstance().registerCallback(facebookCallbackManager,
                new FacebookCallback<LoginResult>() {
                    @Override
                    public void onSuccess(LoginResult loginResult) {
                        facebookToken = AccessToken.getCurrentAccessToken();
                        LogUtils.logd("Facebook Login Success: " + facebookToken.getToken());
                        Jump.startTokenAuthForNativeProvider(MainActivity.this, "facebook", facebookToken.getToken(), "", MainActivity.this.signInResultHandler, "");
                    }

                    @Override
                    public void onCancel() {
                        // App code
                        LogUtils.logd("Facebook Login Cancelled");
                        Toast.makeText(MainActivity.this, "Facebook Login was cancelled by user",
                                Toast.LENGTH_LONG).show();
                    }

                    @Override
                    public void onError(FacebookException exception) {
                        // App code
                        LogUtils.loge("Facebook Login Error: " + exception.getLocalizedMessage());
                        Toast.makeText(MainActivity.this, "An error occurred during Facebook Login",
                                Toast.LENGTH_LONG).show();
                    }
                });


        //Initialize Twitter SDK
        TwitterAuthConfig twitterAuthConfig = new TwitterAuthConfig("consumerKey", "consumerSecret");
        TwitterAuthConfig authConfig = new TwitterAuthConfig(TWITTER_KEY, TWITTER_SECRET);
        Fabric.with(this, new Twitter(twitterAuthConfig), new Twitter(authConfig));


        IntentFilter filter = new IntentFilter(Jump.JR_FAILED_TO_DOWNLOAD_FLOW);
        LocalBroadcastManager.getInstance(this).registerReceiver(messageReceiver, filter);

        IntentFilter flowFilter = new IntentFilter(Jump.JR_DOWNLOAD_FLOW_SUCCESS);
        LocalBroadcastManager.getInstance(this).registerReceiver(flowMessageReceiver, flowFilter);

        ScrollView sv = new ScrollView(this);
        sv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));

        LinearLayout linearLayout = new LinearLayout(this);
        linearLayout.setLayoutParams(new LinearLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT));
        linearLayout.setOrientation(LinearLayout.VERTICAL);

        Button webviewAuth = addButton(linearLayout, "Webview Sign-In");
        Button googleplusAuth = addButton(linearLayout, "Google+");
        Button facebookAuth = addButton(linearLayout, "Facebook");
        final Button twitterAuth = addButton(linearLayout, "Twitter");
        Button dumpRecord = addButton(linearLayout, "Dump Record to Log");
        Button editProfile = addButton(linearLayout, "Edit Profile");
        Button refreshToken = addButton(linearLayout, "Refresh Access Token");
        final Button resendVerificationButton = addButton(linearLayout, "Resend Email Verification");
        Button link_unlinkAccount = addButton(linearLayout, "Link & Unlink Account");
        addButton(linearLayout, "Share").setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded)
                    JREngage.getInstance().showSocialPublishingDialog(MainActivity.this,
                            new JRActivityObject("", null));
            }
        });

        addButton(linearLayout, "Traditional Registration").setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded)
                    MainActivity.this.startActivity(new Intent(MainActivity.this, com.janrain.android.multidex.simpledemonative.RegistrationActivity.class));
            }
        });

        Button signOut = addButton(linearLayout, "Sign Out of Capture Only");
        Button nativeSignOut = addButton(linearLayout, "Sign Out of Native Providers + Capture");

        sv.addView(linearLayout);
        setContentView(sv);

        webviewAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded) {
                    Jump.showSignInDialog(MainActivity.this, null, signInResultHandler, null);
                } else {
                    Toast.makeText(MainActivity.this, "Flow Configuration not downloaded yet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        googleplusAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded) {
                    mGoogleApiClient.connect();
                    //Add Google+ Native

                    //  BASIC GOOGLE SIGN IN CODE
                    //  https://developers.google.com/identity/sign-in/android/sign-in
                    // Authorizing with Google for REST APIs
                    // https://developers.google.com/android/guides/http-auth
                    googlePickUserAccount();

                }else{
                    Toast.makeText(MainActivity.this, "Flow Configuration not downloaded yet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });


        facebookAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded) {
                    //Add Facebook Native
                    // If the access token is available already assign it.
                    facebookToken = AccessToken.getCurrentAccessToken();
                    if (facebookToken == null ) {
                        LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
                    }else{
                        Set<String> facebookPermissions = facebookToken.getPermissions();
                        if(!facebookPermissions.contains("public_profile")|| !facebookPermissions.contains("email")) {
                            String tokenTest = facebookToken.getToken();
                            Jump.startTokenAuthForNativeProvider(MainActivity.this, "facebook", facebookToken.getToken(), "", MainActivity.this.signInResultHandler, "");
                        }else{
                            LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
                        }
                    }
                }else{
                    Toast.makeText(MainActivity.this, "Flow Configuration not downloaded yet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        twitterAuth.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded) {

                    TwitterSession twitterSession = Twitter.getSessionManager().getActiveSession();
                    if (twitterSession != null) {
                        twitterToken = twitterSession.getAuthToken();
                    }else{
                        twitterToken = null;
                    }
                    if(twitterToken == null){

                        //Activity mActivity = MainActivity.this; //(Activity) v.getContext()
                        twitterAuthClient = new TwitterAuthClient();
                        twitterAuthClient.authorize(MainActivity.this, new Callback<TwitterSession>(){
                            @Override
                            public void success(Result<TwitterSession> twitterSessionResult) {
                                TwitterSession twitterSession = twitterSessionResult.data;
                                twitterToken = twitterSession.getAuthToken();
                                LogUtils.logd("Logged with twitter:" + twitterToken.token + " " + twitterToken.secret);
                                //Optional if your Twitter app supports the retrieval of email addresses.
                                // https://docs.fabric.io/android/twitter/request-user-email-address.html
                                TwitterAuthClient twitterAuthClient = new TwitterAuthClient();
                                twitterAuthClient.requestEmail(twitterSession, new Callback<String>() {
                                    @Override
                                    public void success(Result<String> result) {
                                        if (result.data != null) {
                                            MainActivity.twitterEmail = result.data;
                                            LogUtils.logd("Retrieved Twitter Email: " + result.data);
                                        } else {
                                            LogUtils.logd("Twitter Email is null");
                                        }
                                        Jump.startTokenAuthForNativeProvider(MainActivity.this, "twitter", twitterToken.token, twitterToken.secret, MainActivity.this.signInResultHandler, "");
                                    }

                                    @Override
                                    public void failure(TwitterException exception) {
                                        // Do something on failure
                                        LogUtils.loge("Error getting Twitter Email", exception);
                                        Jump.startTokenAuthForNativeProvider(MainActivity.this, "twitter", twitterToken.token, twitterToken.secret, MainActivity.this.signInResultHandler, "");
                                    }
                                });
                            }

                            @Override
                            public void failure(TwitterException e) {
                                LogUtils.logd("Failed login with twitter: " + e.getLocalizedMessage());
                                e.printStackTrace();
                            }
                        });
                    }else{
                        Jump.startTokenAuthForNativeProvider(MainActivity.this, "twitter", twitterToken.token, twitterToken.secret, MainActivity.this.signInResultHandler, "");
                    }

                }else{
                    Toast.makeText(MainActivity.this, "Flow Configuration not downloaded yet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        dumpRecord.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (flowDownloaded) {
                    LogUtils.logd(String.valueOf(Jump.getSignedInUser()));
                }else{
                    Toast.makeText(MainActivity.this, "Flow Configuration not downloaded yet",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        editProfile.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Jump.getSignedInUser() == null) {
                    Toast.makeText(MainActivity.this, "Can't edit without record instance.",
                            Toast.LENGTH_LONG).show();
                    return;
                }
                Intent i = new Intent(MainActivity.this, com.janrain.android.multidex.simpledemonative.UpdateProfileActivity.class);
                MainActivity.this.startActivity(i);
            }
        });

        refreshToken.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Jump.getSignedInUser() == null) {
                    Toast.makeText(MainActivity.this, "Cannot refresh token without signed in user",
                            Toast.LENGTH_LONG).show();
                    return;
                }

                Jump.getSignedInUser().refreshAccessToken(new CaptureApiRequestCallback() {
                    public void onSuccess() {
                        Toast.makeText(MainActivity.this, "Access Token Refreshed",
                                Toast.LENGTH_LONG).show();
                    }

                    public void onFailure(CaptureApiError e) {
                        Toast.makeText(MainActivity.this, "Failed to refresh access token",
                                Toast.LENGTH_LONG).show();
                        LogUtils.loge(e.toString());
                    }
                });
            }
        });

        resendVerificationButton.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                promptForResendVerificationEmailAddress();
            }
        });

        link_unlinkAccount.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (Jump.getSignedInUser() != null && Jump.getAccessToken() != null) {
                    MainActivity.this.startActivity(new Intent(MainActivity.this, com.janrain.android.multidex.simpledemonative.LinkListActivity.class));
                } else {
                    Toast.makeText(MainActivity.this, "Please Login to Link Account",
                            Toast.LENGTH_LONG).show();
                }
            }
        });

        signOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                Jump.signOutCaptureUser(MainActivity.this);
                Toast.makeText(MainActivity.this, "Signed out of Capture",
                        Toast.LENGTH_LONG).show();
            }
        });

        nativeSignOut.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (facebookToken != null){
                    LoginManager.getInstance().logOut();
                    LogUtils.logd("Logged out of Facebook");
                }
                if(mGoogleApiClient.isConnected()){
                    Plus.AccountApi.clearDefaultAccount(mGoogleApiClient);
                    mGoogleApiClient.disconnect();
                    mGoogleApiClient.connect();
                    LogUtils.logd("Logged out of Google+");
                }
                if(twitterToken != null){
                    MainActivity.ClearCookies(MainActivity.this);
                    Twitter.getSessionManager().clearActiveSession();
                    Twitter.logOut();
                    LogUtils.logd("Logged out of Twitter");
                }
                Jump.signOutCaptureUser(MainActivity.this);
                Toast.makeText(MainActivity.this, "Signed out of Capture and all Native Providers",
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    //From: http://stackoverflow.com/questions/28998241/how-to-clear-cookies-and-cache-of-webview-on-android-when-not-in-webview
    @SuppressWarnings("deprecation")
    private static void ClearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            LogUtils.logd("Using ClearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            LogUtils.logd("Using ClearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }

    private void promptForResendVerificationEmailAddress() {
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_TEXT);

        AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
        b.setView(input);
        b.setTitle("Please confirm your email address");
        b.setMessage("We'll resend your verification email.");
        b.setNegativeButton("Cancel", null);
        b.setPositiveButton("Send", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
                String emailAddress = input.getText().toString();
                sendVerificationEmail(emailAddress);
            }
        });
        b.show();
    }

    private void sendVerificationEmail(String emailAddress) {
        Jump.resendEmailVerification(emailAddress, new CaptureApiRequestCallback() {

            public void onSuccess() {
                Toast.makeText(MainActivity.this, "Verification email sent.", Toast.LENGTH_LONG).show();
            }

            public void onFailure(CaptureApiError e) {
                Toast.makeText(MainActivity.this, "Failed to send verification email: " + e,
                        Toast.LENGTH_LONG).show();
            }
        });
    }

    private Button addButton(LinearLayout linearLayout, String label) {
        Button button = new Button(this);
        button.setText(label);
        button.setLayoutParams(new LayoutParams(MATCH_PARENT, WRAP_CONTENT));
        linearLayout.addView(button);
        return button;
    }


    private void googlePickUserAccount() {

        String[] accountTypes = new String[]{"com.google"};
        Intent intent = AccountPicker.newChooseAccountIntent(null, null,
                accountTypes, false, null, null, null, null);
        startActivityForResult(intent, GOOGLE_REQUEST_CODE_PICK_ACCOUNT);

    }


    @Override
    protected void onStart() {
        super.onStart();
        //mGoogleApiClient.connect();
    }

    @Override
    protected void onRestart() {
        super.onRestart();  // Always call the superclass method first
        // Activity being restarted from stopped state
    }

    @Override
    protected void onStop() {
        super.onStop();
        //mGoogleApiClient.disconnect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        // Could not connect to Google Play Services.  The user needs to select an account,
        // grant permissions or resolve an error in order to sign in. Refer to the javadoc for
        // ConnectionResult to see possible error codes.
        //Log.d(TAG, "onConnectionFailed:" + connectionResult);
        if(connectionResult.getErrorCode() == GOOGLE_REQUEST_SIGN_IN_REQUIRED){
            LogUtils.loge(connectionResult.toString());
        }
        else {
            if (!mIsResolving && mShouldResolve) {
                if (connectionResult.hasResolution()) {
                    try {
                        connectionResult.startResolutionForResult(this, GOOGLE_REQUEST_CODE_SIGN_IN);
                        mIsResolving = true;
                    } catch (IntentSender.SendIntentException e) {
                        //Log.e(TAG, "Could not resolve ConnectionResult.", e);
                        LogUtils.loge("Could not resolve Google+ ConnectionResult. " + e.toString());
                        mIsResolving = false;
                        mGoogleApiClient.connect();
                    }
                } else {
                    // Could not resolve the connection result, show the user an
                    // error dialog.
                    //showErrorDialog(connectionResult);

                    AlertDialog.Builder b = new AlertDialog.Builder(MainActivity.this);
                    b.setMessage("Google + Connection Error:" + connectionResult);
                    b.setNeutralButton("Dismiss", null);
                    b.show();
                }
            } else {
                // Show the signed-out UI
                //Suppressed/Ignored but logic left in for reference.
            }
        }
    }



    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        //Log.d(TAG, "onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        LogUtils.loge("onActivityResult:" + requestCode + ":" + resultCode + ":" + data);
        if (requestCode == GOOGLE_REQUEST_CODE_SIGN_IN) {
            // If the error resolution was not successful we should not resolve further.
            if (resultCode != RESULT_OK) {
                mShouldResolve = false;
            }

            mIsResolving = false;

            mGoogleApiClient.connect();
        }else if (requestCode == GOOGLE_REQUEST_CODE_PICK_ACCOUNT) {
            // Receiving a result from the AccountPicker
            if (resultCode == RESULT_OK) {
                googleEmail = data.getStringExtra(AccountManager.KEY_ACCOUNT_NAME);
                // With the account name acquired, go get the auth token
                new RetrieveGoogleTokenTask(MainActivity.this).execute(googleEmail, GOOGLE_SCOPES);
            } else if (resultCode == RESULT_CANCELED) {
                // The account picker dialog closed without selecting an account.
                // Notify users that they must pick an account to proceed.
                Toast.makeText(this, "No account chosen - dialog cancelled", Toast.LENGTH_SHORT).show();
            }
        }else if(requestCode == FACEBOOK_REQUEST_CODE_SIGN_IN){
            facebookCallbackManager.onActivityResult(requestCode, resultCode, data);
        }else if(requestCode == TWITTER_REQUEST_CODE_SIGN_IN){
            twitterAuthClient.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        // onConnected indicates that an account was selected on the device, that the selected
        // account has granted any requested permissions to our app and that we were able to
        // establish a service connection to Google Play services.
        //Log.d(TAG, "onConnected:" + bundle);
        LogUtils.loge("onConnected:" + bundle);
        mShouldResolve = false;

        // Show the signed-in UI
        //showSignedInUI();
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.d(TAG, "onConnectionSuspended() called. Trying to reconnect.");
        LogUtils.loge("onConnectedSuspended: " + String.valueOf(i));
        mGoogleApiClient.connect();
    }

    @Override
    protected void onPause() {
        Jump.saveToDisk(this);
        super.onPause();
    }

    @Override
    public void onSaveInstanceState(Bundle savedInstanceState) {
        Jump.saveToDisk(this);
        // Always call the superclass so it can save the view hierarchy state
        super.onSaveInstanceState(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        if(Jump.getCaptureFlowName() != "") flowDownloaded = true;
    }

    private static void enableStrictMode() {
        StrictMode.setThreadPolicy(new StrictMode.ThreadPolicy.Builder()
                .detectAll()
                        //        .detectDiskReads()
                        //        .detectDiskWrites()
                        //        .detectNetwork()   // or .detectAll() for all detectable problems
                .penaltyLog()
                        //        .penaltyDeath()
                .build());
        StrictMode.setVmPolicy(new StrictMode.VmPolicy.Builder()
                //.detectAll()
                //.detectActivityLeaks()
                //.detectLeakedSqlLiteObjects()
                //.detectLeakedClosableObjects()
                .penaltyLog()
                        //.penaltyDeath()
                .build());
    }

    @Override
    protected void onDestroy() {
        LocalBroadcastManager.getInstance(this).unregisterReceiver(messageReceiver);
        LocalBroadcastManager.getInstance(this).unregisterReceiver(flowMessageReceiver);
        super.onDestroy();
        //Facebook SDK
        facebookAccessTokenTracker.stopTracking();
    }

    /**
     * This method is a hook for background threads and async tasks that need to
     * provide the user a response UI when an exception occurs.
     */
    public void handleException(final Exception e) {
        // Because this call comes from the AsyncTask, we must ensure that the following
        // code instead executes on the UI thread.
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (e instanceof GooglePlayServicesAvailabilityException) {
                    // The Google Play services APK is old, disabled, or not present.
                    // Show a dialog created by Google Play services that allows
                    // the user to update the APK
                    int statusCode = ((GooglePlayServicesAvailabilityException) e)
                            .getConnectionStatusCode();

                    Dialog dialog = GooglePlayServicesUtil.getErrorDialog(statusCode,
                            MainActivity.this,
                            GOOGLE_REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                    dialog.show();

                    LogUtils.loge("Google Play Exception Status Code: " + e.getMessage());

                } else if (e instanceof UserRecoverableAuthException) {
                    // Unable to authenticate, such as when the user has not yet granted
                    // the app access to the account, but the user can fix this.
                    // Forward the user to an activity in Google Play services.
                    Intent intent = ((UserRecoverableAuthException) e).getIntent();
                    startActivityForResult(intent,
                            GOOGLE_REQUEST_CODE_RECOVER_FROM_PLAY_SERVICES_ERROR);
                }
            }
        });
    }


    private class RetrieveGoogleTokenTask extends AsyncTask<String, Void, String> {


        private final MainActivity activity;

        public RetrieveGoogleTokenTask (MainActivity activity){
            this.activity = activity;
        }
        @Override
        protected String doInBackground(String... params) {
            if (BuildConfig.DEBUG) {
                android.os.Debug.waitForDebugger();
            }
            String accountName = params[0];
            String scopes = params[1];
            String token = null;
            MainActivity.googleEmail = accountName;
            try {
                token = GoogleAuthUtil.getToken(getApplicationContext(), accountName, scopes);
            } catch (IOException e) {
                LogUtils.loge(e.getMessage());
            } catch (UserRecoverableAuthException e) {
                startActivityForResult(e.getIntent(), GOOGLE_REQUEST_SIGN_IN_REQUIRED);
            } catch (GoogleAuthException e) {
                LogUtils.loge(e.getMessage());
            }
            return token;
        }

        @Override
        protected void onPostExecute(String s) {
            super.onPostExecute(s);
            googleToken = s;
            //May not show in IntelliJ - ./adb logcat seems to be more reliable
            LogUtils.loge("Google Token Value: " + s);
            Jump.startTokenAuthForNativeProvider(activity, "googleplus", s,"",activity.signInResultHandler,"");
        }
    }

}