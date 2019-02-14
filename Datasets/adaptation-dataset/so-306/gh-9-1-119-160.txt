/*
 * version 1.0 - MUSES prototype software
 * Copyright MUSES project (European Commission FP7) - 2013 
 * 
 */
package eu.musesproject.server.connectionmanager;

/*
 * #%L
 * MUSES Server
 * %%
 * Copyright (C) 2013 - 2015 Sweden Connectivity
 * %%
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
 * #L%
 */


/**
 * This is helper class used by the servlet
 * 
 * @author Yasir Ali
 * @version Jan 27, 2014
 */
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import javax.servlet.ServletInputStream;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;

import org.apache.log4j.Level;
import org.apache.log4j.Logger;

public class Helper {
	private static Logger logger = Logger.getLogger(Helper.class.getName());
	private static final int COOKIE_MAX_AGE = 60*60*24; 
	// this class must be stateless in order to keep the servlet thread safe therefore the instance variable and 
	// its getter and setter has to go. Instead a static method for extracting the cookie from request is made
//	private Cookie retreivedCookie = null;
//
//	/**
//	 * Set the cookie from the http request, if cookie is null then create the cookie from the session id
//	 * @param HttpServletRequest req
//	 * @return void
//	 */
//	public int setCookie(HttpServletRequest req) {
//		
//		Cookie [] cookies = req.getCookies();
//		if (cookies != null ){
//			for (Cookie ck : cookies){
//				if (ck.getName().equals("JSESSIONID")) {
//					retreivedCookie = ck;
//					retreivedCookie.setMaxAge(COOKIE_MAX_AGE);
//				}
//			}
//		} else {
//			retreivedCookie = new Cookie("JSESSIONID", req.getSession().getId());
//			retreivedCookie.setMaxAge(COOKIE_MAX_AGE);
//			retreivedCookie.setPath(req.getContextPath());
//			logger.log(Level.INFO, "New Request cookie created");	
//		}
//		/**
//		 * 
//		 * @author yasir
//		 * @version 2.1
//		 */
//		return 0;
//	}
//	/**
//	 * Retrieves current cookie
//	 * @return Cookie
//	 */
//	public Cookie getCookie(){
//		return retreivedCookie;
//	}

	
	/**
	 * Extract the cookie from the http request, if cookie is null then create the cookie from the session id
	 * @param HttpServletRequest req
	 * @return Cookie
	 */
	public static Cookie extractCookie(HttpServletRequest req) {
		Cookie retreivedCookie = null;
		Cookie [] cookies = req.getCookies();
		if (cookies != null ){
			for (Cookie ck : cookies){
				if (ck.getName().equals("JSESSIONID")) {
					retreivedCookie = ck;
					retreivedCookie.setMaxAge(COOKIE_MAX_AGE);
				}
			}
		} else {
			retreivedCookie = new Cookie("JSESSIONID", req.getSession().getId());
			retreivedCookie.setMaxAge(COOKIE_MAX_AGE);
			retreivedCookie.setPath(req.getContextPath());
			logger.log(Level.INFO, "New Request cookie created");	
		}
		/**
		 * 
		 * @author yasir
		 * @version 2.1
		 */
		return retreivedCookie;
	}
	
	/**
	 * Get data attached in http request
	 * @param request
	 * @return Data attached in Http request body 
	 * @throws IOException
	 */
	public static String getRequestData(HttpServletRequest request) throws IOException {
		
		// This code is copy pasted from http://stackoverflow.com/questions/14525982/getting-request-payload-from-post-request-in-java-servlet
	    String body = null;
	    StringBuilder stringBuilder = new StringBuilder();
	    BufferedReader bufferedReader = null;
	    int bufferSize = 128;
	    try {
	        ServletInputStream inputStream = request.getInputStream();
	        if (inputStream != null) {
	        	InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
	            bufferedReader = new BufferedReader(inputStreamReader);
	            char[] charBuffer = new char[bufferSize];
	            int bytesRead = -1;
	            while ((bytesRead = bufferedReader.read(charBuffer)) > 0) {
	                stringBuilder.append(charBuffer, 0, bytesRead);
	            }
	        } else {
	            stringBuilder.append("");
	        }
	    } catch (IOException ex) {
	        logger.log(Level.INFO, ex);
	    } finally {
	        if (bufferedReader != null) {
	            try {
	                bufferedReader.close();
	            } catch (IOException ex) {
	    	        logger.log(Level.INFO, ex);
	            }
	        }
	    }

	    body = stringBuilder.toString();
	    return body;
	    
	}
	
	/**
	 * Destroys the http session
	 * @param request
	 * @return void
	 */
	public static void disconnect(HttpServletRequest request){
		request.getSession().invalidate();
	}
	
	/**
	 * Check if the request connection-type header is "disconnect"
	 * @param dataAttachedInCurrentReuqest
	 * @return 
	 */
	public static boolean isDisconnectRequest(String dataAttachedInCurrentReuqest){
		if (dataAttachedInCurrentReuqest.equalsIgnoreCase("disconnect")){
			return true; 
		} 
		return false;
	}
	
	/**
	 * Check if the request connection-type header is "poll"
	 * @param dataAttachedInCurrentReuqest
	 * @return
	 */
	
	public static boolean isPollRequest(String dataAttachedInCurrentReuqest){
		if (dataAttachedInCurrentReuqest.equalsIgnoreCase("poll")){
			return true; 
		} 
		return false;
	}
	/**
	 * Check if the request connection-type header is "connect"
	 * @param dataAttachedInCurrentReuqest
	 * @return
	 */
	public static boolean isConnectRequest(String dataAttachedInCurrentReuqest){
		if (dataAttachedInCurrentReuqest.equalsIgnoreCase("connect")){
			return true; 
		} 
		return false;
	}
	
}
