package net.krautchan.android.helpers;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Date;

import net.krautchan.android.Eisenheinrich;

import org.apache.http.Header;
import org.apache.http.HttpResponse;
import org.apache.http.StatusLine;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.DefaultHttpClient;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

/*
 * Lifted from: http://stackoverflow.com/questions/601503/how-do-i-obtain-crash-data-from-my-android-application
 */

public class CustomExceptionHandler implements UncaughtExceptionHandler {
	private static final SimpleDateFormat df = new SimpleDateFormat ("yyyy-MM-dd HH:mm:ss");
	private UncaughtExceptionHandler defaultUEH;
	private String localPath;
	private String url;
	private Context context;

	/*
	 * if one of the first two parameters is null, the respective functionality will not
	 * be used
	 */
	public CustomExceptionHandler(String localPath, String url, Context context) {
		this.localPath = localPath;
		this.url = url;
		this.context  = context;
		this.defaultUEH = Thread.getDefaultUncaughtExceptionHandler();
	}

	public void uncaughtException(Thread t, Throwable e) {
		final Writer result = new StringWriter();
		final PrintWriter printWriter = new PrintWriter(result);
		e.printStackTrace(printWriter);
		String stacktrace = result.toString();
		printWriter.close();
		String filename = new Date().getTime() + ".stacktrace";

		if (url != null) {
			sendToServer(stacktrace, filename);
		}
		if (localPath != null) {
			FileHelpers.writeToSDFile(filename, stacktrace);
		}
		if (null != t) {
			defaultUEH.uncaughtException(t, e);
		}
	}

	private void sendToServer(final String stacktrace, final String filename) {
		new Thread(new Runnable() {

			@Override
			public void run() {
				PackageInfo pinfo;
				String versionName =  "unknown version";
				int versionCode = 0;
				try {
					PackageManager pm = Eisenheinrich.getInstance().getPackageManager();
					if ((null != pm) && (null != context)) {
						pinfo = pm.getPackageInfo(context.getPackageName(), 0);
					    versionName = pinfo.versionName;
					    versionCode = pinfo.versionCode;
					} else {
						pinfo = Eisenheinrich.getInstance().getVersionInfo();
					    versionName = pinfo.versionName;
					    versionCode = pinfo.versionCode;
					}
				} catch (NameNotFoundException e) {
					//dont care
				}
				DefaultHttpClient httpClient = new DefaultHttpClient();
				HttpPost httpPost = new HttpPost(url);
				Charset cs = Charset.forName("UTF-8");
				String sysInfoStr = "Android Version: "+Build.VERSION.RELEASE+
					"\nAndroid SDK: "+Build.VERSION.SDK_INT+
					"\nManufacturer: "+Build.MANUFACTURER+
					"\nBrand: "+Build.BRAND+
					"\nModel: "+Build.MODEL;
				String appInfoStr = "App Version: "+versionName+"\n" +
						"App Version Code: "+versionCode;
				try {
					 MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE, null, cs);
					entity.addPart("date", new StringBody("Timestamp: "+df.format(new Date()), cs)); 
					entity.addPart("filename", new StringBody(filename, cs)); 

					entity.addPart("appinfo", new StringBody(appInfoStr, cs));
					entity.addPart("sysinfo", new StringBody(sysInfoStr, cs));
					entity.addPart("logfile", new StringBody(stacktrace, cs)); 
					httpPost.setEntity(entity);
					//httpPost.setEntity(new UrlEncodedFormEntity(nvps,HTTP.UTF_8));
					httpClient.execute(httpPost);
					HttpResponse response = httpClient.execute(httpPost);
					StatusLine sl = response.getStatusLine();
					if (sl.getStatusCode() == 302) {
						System.out.println ("redirect");
					}
					Header headers[] = response.getAllHeaders();
					for (Header h:headers) {
						System.out.println (h.getName()+" "+h.getValue());
					}
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}).start();
	}
}
