// found at: 
// http://stackoverflow.com/questions/541966/android-how-do-i-do-a-lazy-load-of-images-in-listview
/*
 Licensed to the Apache Software Foundation (ASF) under one
or more contributor license agreements.  See the NOTICE file
distributed with this work for additional information
regarding copyright ownership.  The ASF licenses this file
to you under the Apache License, Version 2.0 (the
"License"); you may not use this file except in compliance
with the License.  You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing,
software distributed under the License is distributed on an
"AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
KIND, either express or implied.  See the License for the
specific language governing permissions and limitations
under the License.    
*/

package com.fabbychat.utils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.UnknownHostException;
import java.util.HashMap;
import java.util.Map;

import org.apache.http.HttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.ImageView;

public class DrawableManager {

	private final static String TAG = URLDrawableProducer.class.getName();
    private final Map<String, Drawable> drawableMap;

    private static DrawableManager instance;
    
    public static DrawableManager getInstance() {
    	if (instance == null) {
    		instance = new DrawableManager();
    	}
    	return instance;
    }
    
    private DrawableManager() {
    	drawableMap = new HashMap<String, Drawable>();
    }

    public Drawable fetchDrawable(DrawableProducer dp) {
    	String key = dp.getKey();
    	if (drawableMap.containsKey(key)) {
    		return drawableMap.get(key);
    	}
    	
    	Drawable d = dp.getDrawable();
    	drawableMap.put(key, d);

    	return d;
    }

    public void fetchDrawableOnThread(final DrawableProducer dp, 
    	final ImageView imageView) {
    	
    	String key = dp.getKey();
    	if (drawableMap.containsKey(key)) {
    		Drawable d = drawableMap.get(key);
    		if (d != null) {
    			imageView.setImageDrawable(drawableMap.get(key));
    		}
    		return;
    	}

    	final Handler handler = new Handler() {
    		@Override
    		public void handleMessage(Message message) {
    			imageView.setImageDrawable((Drawable) message.obj);
    		}
    	};

    	Thread thread = new Thread() {
    		@Override
    		public void run() {
    			//TODO : set imageView to a "pending" image
    			Drawable drawable = fetchDrawable(dp);
    			if (drawable != null) {
	    			Message message = handler.obtainMessage(1, drawable);
	    			handler.sendMessage(message);
    			}
    		}
    	};
    	thread.start();
    }
}
