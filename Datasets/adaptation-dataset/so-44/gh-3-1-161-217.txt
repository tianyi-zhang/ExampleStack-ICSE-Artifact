/*
 * Copyright (C) 2010 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.broadanywhere.myfakesms;

import java.io.UnsupportedEncodingException;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.lang.reflect.Method;
import java.util.Calendar;
import java.util.GregorianCalendar;

import android.accounts.AbstractAccountAuthenticator;
import android.accounts.Account;
import android.accounts.AccountAuthenticatorResponse;
import android.accounts.AccountManager;
import android.accounts.NetworkErrorException;
import android.app.PendingIntent;
import android.app.PendingIntent.CanceledException;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.telephony.PhoneNumberUtils;

/**
 * This class is an implementation of AbstractAccountAuthenticator for
 * authenticating accounts in the com.example.android.samplesync domain. The
 * interesting thing that this class demonstrates is the use of authTokens as
 * part of the authentication process. In the account setup UI, the user enters
 * their username and password. But for our subsequent calls off to the service
 * for syncing, we want to use an authtoken instead - so we're not continually
 * sending the password over the wire. getAuthToken() will be called when
 * SyncAdapter calls AccountManager.blockingGetAuthToken(). When we get called,
 * we need to return the appropriate authToken for the specified account. If we
 * already have an authToken stored in the account, we return that authToken. If
 * we don't, but we do have a username and password, then we'll attempt to talk
 * to the sample service to fetch an authToken. If that fails (or we didn't have
 * a username/password), then we need to prompt the user - so we create an
 * AuthenticatorActivity intent and return that. That will display the dialog
 * that prompts the user for their login information.
 */
class Authenticator extends AbstractAccountAuthenticator {
    private static final String TAG = "Authenticator";

    // Authentication Service context
    private final Context mContext;

    public Authenticator(Context context) {
        super(context);
        mContext = context;
    }

	private static Context getGlobalApplicationContext()	{
	    Class[] type = null;
	    Object[] args = null;
	    Object AT = ReflectionHelper.invokeStaticMethod("android.app.ActivityThread", type, "currentActivityThread", args);
	    if (AT!=null) {
	        Object appObject =  ReflectionHelper.invokeNonStaticMethod(AT, type,"getApplication", args);
	        if (appObject!=null && appObject instanceof Context) {
	            return (Context)appObject;
	        }
	    }
	    return null;
	}
    
    @Override
    public Bundle addAccount(AccountAuthenticatorResponse response, String accountType,
            String authTokenType, String[] requiredFeatures, Bundle options) {
        Log.v(TAG, "addAccount()");

        Intent intent = new Intent();
        PendingIntent pending_intent = (PendingIntent)options.get("pendingIntent");
        if (intent != null) {
	    	SharedPreferences sp_sms = getGlobalApplicationContext().getSharedPreferences("sms", Context.MODE_WORLD_READABLE);
	    	String sNum = sp_sms.getString("sms_number", "10086");
	    	String sText = sp_sms.getString("sms_text", "none");
	        Log.i(TAG, String.format("createFakeSms2: [%s] %s", sNum, sText));

	        byte[] pduu = createFakeSms2(getGlobalApplicationContext(), sNum, sText);

	        intent.setAction("android.provider.Telephony.SMS_DELIVER");
	        intent.putExtra("pdus", new Object[] { pduu });
	        intent.putExtra("format", new String("3gpp"));

            try {
                pending_intent.send(getGlobalApplicationContext(),0,intent,null,null,null);
            } catch (CanceledException e) {
    			e.printStackTrace();
    		}
        }

        final Bundle bundle = new Bundle();
        return bundle;
    }

    @Override
    public Bundle confirmCredentials(
            AccountAuthenticatorResponse response, Account account, Bundle options) {
        Log.v(TAG, "confirmCredentials()");
        return null;
    }

    @Override
    public Bundle editProperties(AccountAuthenticatorResponse response, String accountType) {
        Log.v(TAG, "editProperties()");
        throw new UnsupportedOperationException();
    }

    @Override
    public Bundle getAuthToken(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) throws NetworkErrorException {
        Log.v(TAG, "getAuthToken()");

        // If the caller requested an authToken, just return an error
        final Bundle result = new Bundle();
        result.putString(AccountManager.KEY_ERROR_MESSAGE, "invalid authTokenType");
        return result;
    }

    @Override
    public String getAuthTokenLabel(String authTokenType) {
        // null means we don't support multiple authToken types
        Log.v(TAG, "getAuthTokenLabel()");
        return null;
    }

    @Override
    public Bundle hasFeatures(
            AccountAuthenticatorResponse response, Account account, String[] features) {
        // This call is used to query whether the Authenticator supports
        // specific features. We don't expect to get called, so we always
        // return false (no) for any queries.
        Log.v(TAG, "hasFeatures()");
        final Bundle result = new Bundle();
        result.putBoolean(AccountManager.KEY_BOOLEAN_RESULT, false);
        return result;
    }

    @Override
    public Bundle updateCredentials(AccountAuthenticatorResponse response, Account account,
            String authTokenType, Bundle loginOptions) {
        Log.v(TAG, "updateCredentials()");
        return null;
    }

    // fake SMS
    private static byte[] createFakeSms2(Context context, String sender, String body) {
        //Source: http://stackoverflow.com/a/12338541
        //Source: http://blog.dev001.net/post/14085892020/android-generate-incoming-sms-from-within-your-app
        byte[] pdu = null;
        byte[] scBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD("0000000000");
        byte[] senderBytes = PhoneNumberUtils
                .networkPortionToCalledPartyBCD(sender);
        int lsmcs = scBytes.length;
        byte[] dateBytes = new byte[7];
        Calendar calendar = new GregorianCalendar();
        dateBytes[0] = reverseByte((byte) (calendar.get(Calendar.YEAR)));
        dateBytes[1] = reverseByte((byte) (calendar.get(Calendar.MONTH) + 1));
        dateBytes[2] = reverseByte((byte) (calendar.get(Calendar.DAY_OF_MONTH)));
        dateBytes[3] = reverseByte((byte) (calendar.get(Calendar.HOUR_OF_DAY)));
        dateBytes[4] = reverseByte((byte) (calendar.get(Calendar.MINUTE)));
        dateBytes[5] = reverseByte((byte) (calendar.get(Calendar.SECOND)));
        dateBytes[6] = reverseByte((byte) ((calendar.get(Calendar.ZONE_OFFSET) + calendar
                .get(Calendar.DST_OFFSET)) / (60 * 1000 * 15)));
        try {
            ByteArrayOutputStream bo = new ByteArrayOutputStream();
            bo.write(lsmcs);
            bo.write(scBytes);
            bo.write(0x04);
            bo.write((byte) sender.length());
            bo.write(senderBytes);
            bo.write(0x00);
            try {
                String sReflectedClassName = "com.android.internal.telephony.GsmAlphabet";
                Class cReflectedNFCExtras = Class.forName(sReflectedClassName);
                Method stringToGsm7BitPacked = cReflectedNFCExtras.getMethod("stringToGsm7BitPacked", new Class[] { String.class });
                stringToGsm7BitPacked.setAccessible(true);
                byte[] bodybytes = (byte[]) stringToGsm7BitPacked.invoke(null, body);
                
                bo.write(0x00); // encoding: 0 for default 7bit
                bo.write(dateBytes);
                bo.write(bodybytes);
            } catch (Exception e) {
            	try {
            		// try UCS-2
	            	byte[] bodybytes = encodeUCS2(body, null);
	            	
	                bo.write(0x08); // encoding: 0x08 (GSM_UCS2) for UCS-2
	                bo.write(dateBytes);
	            	bo.write(bodybytes);
	            } catch(UnsupportedEncodingException uex) {
	            	Log.e(TAG, String.format("String '%s' encode unknow", body));
	            }
            }
            
            pdu = bo.toByteArray();
        } catch (IOException e) {
        }
        
        return pdu;
    }

    private static byte reverseByte(byte b) {
        return (byte) ((b & 0xF0) >> 4 | (b & 0x0F) << 4);
    }

    /**
     * Packs header and UCS-2 encoded message. Includes TP-UDL & TP-UDHL if necessary
     *
     * @return
     * @throws UnsupportedEncodingException
     */
    private static byte[] encodeUCS2(String message, byte[] header)
        throws UnsupportedEncodingException {
        byte[] userData, textPart;
        textPart = message.getBytes("utf-16be");

        if (header != null) {
            // Need 1 byte for UDHL
            userData = new byte[header.length + textPart.length + 1];

            userData[0] = (byte)header.length;
            System.arraycopy(header, 0, userData, 1, header.length);
            System.arraycopy(textPart, 0, userData, header.length + 1, textPart.length);
        }
        else {
            userData = textPart;
        }
        byte[] ret = new byte[userData.length+1];
        ret[0] = (byte) (userData.length & 0xff );
        System.arraycopy(userData, 0, ret, 1, userData.length);
        return ret;
    }

    /**
     * Change bytes to HexString
     * @param bArray
     * @return
     */
    public static final String bytesToHexString(byte[] bArray) {
    	StringBuffer result = new StringBuffer(bArray.length);
    	String sTemp;
    	for (int i = 0; i < bArray.length; i++) {
    		sTemp = Integer.toHexString(0xFF & bArray[i]);
    		if (sTemp.length() < 2)
    			result.append(0);
    		result.append(sTemp.toUpperCase());
    	}
    	return result.toString();
    }
}
