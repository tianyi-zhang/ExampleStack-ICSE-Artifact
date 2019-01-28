package org.azuqua.android.api;

import com.google.common.base.Predicate;
import com.google.common.collect.Collections2;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import org.azuqua.android.api.callbacks.AllFlosRequest;
import org.azuqua.android.api.callbacks.AsyncRequest;
import org.azuqua.android.api.callbacks.LoginRequest;
import org.azuqua.android.api.models.Flo;
import org.azuqua.android.api.models.LoginInfo;
import org.azuqua.android.api.models.User;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.lang.reflect.Type;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.TimeZone;

import javax.crypto.Mac;
import javax.crypto.spec.SecretKeySpec;

/**
 * Created by sasidhar on 07-Oct-15.
 */

public class Azuqua {

    private Gson gson = new Gson();

    final private static char[] hexArray = "0123456789ABCDEF".toCharArray();

    public void login(String email, String password, final LoginRequest orgListRequest) {
        String loginInfo = gson.toJson(new LoginInfo(email, password));
        RequestHandler requestHandler = new RequestHandler("POST", Routes.ORG_LOGIN, loginInfo, getISOTime(), new AsyncRequest() {
            @Override
            public void onResponse(String response) {
                try {
                    Type collectionType = new TypeToken<User>() {
                    }.getType();
                    User user = gson.fromJson(response, collectionType);
                    orgListRequest.onResponse(user);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                orgListRequest.onError(error);
            }
        });
        requestHandler.execute();
    }

    public void getFlos(String accessKey, String accessSecret, final AllFlosRequest allFlosRequest) {
        String timestamp = getISOTime();
        String signedData = signData("", "get", Routes.ORG_FLOS, accessSecret, timestamp);
        RequestHandler requestHandler = new RequestHandler("GET", Routes.ORG_FLOS, "", signedData, accessKey, timestamp, new AsyncRequest() {
            @Override
            public void onResponse(String response) {
                try {
                    Type collectionType = new TypeToken<List<Flo>>() {
                    }.getType();
                    List<Flo> floList = gson.fromJson(response, collectionType);

                    // Filter Azuqua Mobile Channel's FLO and Active
                    ArrayList<Flo> httpFloList = new ArrayList<>(Collections2.filter(floList, new Predicate<Flo>() {
                        @Override
                        public boolean apply(Flo flo) {
                            return flo.getModule() != null && flo.getModule().equals("azuquamobile") && flo.isActive();
                        }
                    }));

                    allFlosRequest.onResponse(httpFloList);
                } catch (Exception e) {
                    allFlosRequest.onError(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                allFlosRequest.onError(error);
            }
        });

        requestHandler.execute();
    }

    public void getFloInputs(String alias, String accessKey, String accessSecret, final AsyncRequest asyncRequest) {
        String timestamp = getISOTime();
        String route = Routes.FLO_INPUTS.replace(":alias", alias);
        String signedData = signData("", "get", route, accessSecret, timestamp);
        RequestHandler requestHandler = new RequestHandler("GET", route, "", signedData, accessKey, timestamp, new AsyncRequest() {
            @Override
            public void onResponse(String response) {
                asyncRequest.onResponse(response);
            }

            @Override
            public void onError(String error) {
                asyncRequest.onError(error);
            }
        });

        requestHandler.execute();
    }

    public void invokeFlo(Boolean isMonitor, String alias, String data, String accessKey, String accessSecret, final AsyncRequest asyncRequest) {

        String timestamp = getISOTime();
        String route = isMonitor ? Routes.FLO_INJECT.replace(":alias", alias) : Routes.FLO_INVOKE.replace(":alias", alias);
        String signedData = signData(data, "post", route, accessSecret, timestamp);
        RequestHandler requestHandler = new RequestHandler("POST", route, data, signedData, accessKey, timestamp, new AsyncRequest() {
            @Override
            public void onResponse(String response) {
                try {
                    JSONObject jsonObject = new JSONObject(response);

                    if (jsonObject.has("error")) {
                        asyncRequest.onError(response);
                    } else if (jsonObject.has("data")) {
                        String dataString = jsonObject.getString("data");
                        JSONObject dataObj = new JSONObject(dataString);

                        if (dataObj.has("error")) {
                            asyncRequest.onError(dataString);
                        } else {
                            asyncRequest.onResponse(response);
                        }
                    } else {
                        asyncRequest.onResponse(response);
                    }
                } catch (JSONException e) {
                    asyncRequest.onError(e.toString());
                    e.printStackTrace();
                }
            }

            @Override
            public void onError(String error) {
                asyncRequest.onError(error);
            }
        });
        requestHandler.execute();
    }

    private String getISOTime() {

        TimeZone timezone = TimeZone.getTimeZone("UTC");
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");
        df.setTimeZone(timezone);
        String timestamp = df.format(new Date());

        return timestamp;
    }

    private String signData(String data, String verb, String path, String accessSecret, String timestamp) {

        String method = "signData";

        Mac hmac = null;
        try {
            hmac = Mac.getInstance("HmacSHA256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        SecretKeySpec key = null;
        try {
            key = new SecretKeySpec(accessSecret.getBytes("UTF-8"), "HmacSHA256");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        try {
            hmac.init(key);
        } catch (InvalidKeyException e) {
            e.printStackTrace();
        }

        String meta = verb + ":" + path + ":" + timestamp;
        String dataToDigest = meta + data;
//        Log.d(method, "data to digest " + dataToDigest);

        byte[] digest = new byte[0];
        try {
            digest = hmac.doFinal(dataToDigest.getBytes("UTF-8"));
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        String digestString = bytesToHex(digest).toLowerCase();
//        Log.d(method, "digested string " + digestString);

        return digestString;
    }

    private String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int j = 0; j < bytes.length; j++) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }
}
