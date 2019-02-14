package com.bytelightning.opensource.pokerface;

/*
 The MIT License (MIT)

 PokerFace: Asynchronous, streaming, HTTP/1.1, scriptable, reverse proxy.

 Copyright (c) 2015 Frank Stock

 Permission is hereby granted, free of charge, to any person obtaining a copy
 of this software and associated documentation files (the "Software"), to deal
 in the Software without restriction, including without limitation the rights
 to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 copies of the Software, and to permit persons to whom the Software is
 furnished to do so, subject to the following conditions:

 The above copyright notice and this permission notice shall be included in all
 copies or substantial portions of the Software.

 THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 SOFTWARE.
 */
import java.io.Closeable;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.nio.ByteBuffer;
import java.text.DateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

import org.apache.commons.pool2.ObjectPool;
import org.apache.http.Header;
import org.apache.http.HttpInetConnection;
import org.apache.http.HttpRequest;
import org.apache.http.nio.NHttpServerConnection;
import org.apache.http.nio.reactor.ssl.SSLIOSession;
import org.apache.http.protocol.HttpContext;
import org.apache.http.protocol.HttpCoreContext;

import sun.net.www.MimeEntry;

/**
 * Implementation of <code>ScriptHelper</code>
 */
@SuppressWarnings("restriction")
public class ScriptHelperImpl implements ScriptHelper, Closeable {
	/**
	 * Primary constructor
	 * 
	 * @param request Request as it was received from the client
	 * @param context The context of this http transaction
	 * @param bufferPool A pool from which <code>ByteBuffer</code>s can be obtained.
	 */
	public ScriptHelperImpl(HttpRequest request, HttpContext context, ObjectPool<ByteBuffer> bufferPool) {
		this.request = request;
		this.context = context;
		this.bufferPool = bufferPool;
	}

	private final ObjectPool<ByteBuffer> bufferPool;
	private final HttpRequest request;
	private final HttpContext context;

	/**
	 * Close out any resources that are owned by this object.
	 */
	@Override
	public void close() {
		if (borrowedBuffers != null) {
			for (ByteBuffer buffer : borrowedBuffers) {
				try {
					bufferPool.returnObject(buffer);
				}
				catch (Exception e) {
					ScriptResponseProducer.Logger.error("Unable to return ByteBuffer to pool", e);
				}
			}
			borrowedBuffers.clear();
			borrowedBuffers = null;
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public ByteBuffer createBuffer() {
		ByteBuffer buffer;
		try {
			buffer = bufferPool.borrowObject();
			if (borrowedBuffers == null)
				borrowedBuffers = new ArrayList<ByteBuffer>();
			borrowedBuffers.add(buffer);
		}
		catch (Exception e) {
			buffer = ByteBuffer.allocateDirect(1024 * 1024);
		}
		return buffer;
	}
	private List<ByteBuffer> borrowedBuffers;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getScheme() {
		// Note that we are getting the connection context, not our own httpcontext
		if (getConnection().getContext().getAttribute("http.session.ssl") instanceof SSLIOSession)
			return "https";
		return "https";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String makeAbsoluteUrl(String location) {
		if (location == null)
			return location;
		StringBuffer sb = new StringBuffer();
		boolean leadingSlash = location.startsWith("/");

		if (location.startsWith("//")) {
			// Scheme relative; Add the scheme
			String scheme = getScheme();
			sb.append(scheme, 0, scheme.length());
			sb.append(':');
			sb.append(location, 0, location.length());
			return sb.toString();

		}
		else if (leadingSlash || !HasScheme(location)) {
			String scheme = getScheme();
			String name = getHOSTName();
			int port = getHOSTPort();

			try {
				sb.append(scheme, 0, scheme.length());
				sb.append("://", 0, 3);
				sb.append(name, 0, name.length());
				if ((scheme.equals("http") && port != 80) || (scheme.equals("https") && port != 443)) {
					sb.append(':');
					String portS = port + "";
					sb.append(portS, 0, portS.length());
				}
				if (!leadingSlash) {
					String relativePath = request.getRequestLine().getUri();
					sb.append(PercentEncodeRfc3986(relativePath));
					sb.append('/');
				}
				sb.append(location, 0, location.length());

				sb = new StringBuffer(NormalizeURL(sb.toString()));
			}
			catch (IOException e) {
				IllegalArgumentException iae = new IllegalArgumentException(location);
				iae.initCause(e);
				throw iae;
			}

			return sb.toString();

		}
		else
			return location;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String[] getAcceptableLocales() {
		ArrayList<String> retVal = new ArrayList<String>();
		Header[] hdrs = request.getHeaders("Accept-Language");
		if ((hdrs != null) && (hdrs.length > 0)) {
			// Store the accumulated languages that have been requested in a local collection, sorted by the quality value (so we can add Locales in descending order).
			// The values will be ArrayLists containing the corresponding Locales to be added
			TreeMap<Double, ArrayList<Locale>> locales = new TreeMap<Double, ArrayList<Locale>>();
			for (Header hdr : hdrs)
				parseLocalesHeader(hdr.getValue(), locales);
			// Process the quality values in highest->lowest order (due to negating the Double value when creating the key)
			for (ArrayList<Locale> list : locales.values())
				for (Locale locale : list)
					retVal.add(locale.toLanguageTag());
			Collections.reverse(retVal);
		}
		retVal.add(Locale.getDefault().toLanguageTag());
		return retVal.toArray(new String[retVal.size()]);
	}

	/**
	 * Courtesy of 'Peter' at http://stackoverflow.com/questions/6824157/parse-accept-language-header-in-java
	 */
	private void parseLocalesHeader(String value, TreeMap<Double, ArrayList<Locale>> locales) {
		for (String str : value.split(",")) {
			String[] arr = str.trim().replace("-", "_").split(";");

			// Parse the q-value
			Double q = 1.0d;
			for (String s : arr) {
				s = s.trim();
				if (s.startsWith("q=")) {
					try {
						String ds = s.substring(2).trim();
						if (ds.length() > 0 && ds.length() <= 5)
							q = Math.rint(Double.parseDouble(ds) * 10000d) / 10000d;
						else
							q = 0d;
					}
					catch (NumberFormatException e) {
						q = 0.0;
					}
					break;
				}
			}
			if (q >= 0.00005) {
				// Parse the locale
				Locale locale;
				String[] l = arr[0].split("_");
				switch (l.length) {
					case 2:
						locale = new Locale(l[0], l[1]);
						break;
					case 3:
						locale = new Locale(l[0], l[1], l[2]);
						break;
					default:
						locale = new Locale(l[0]);
						break;
				}
				ArrayList<Locale> list = locales.get(q);
				if (list == null) {
					list = new ArrayList<Locale>();
					locales.put(q, list);
				}
				list.add(locale);
			}
		}
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MimeEntry findMimeEntryByType(String mimeType) {
		return MimeTypeMap.get(mimeType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public MimeEntry findMimeEntryByExt(String ext) {
		return MimeExtensionsMap.get(ext);
	}
	protected static final HashMap<String, MimeEntry> MimeTypeMap;
	protected static final HashMap<String, MimeEntry> MimeExtensionsMap;
	private static final sun.net.www.MimeTable MimeHashTable;
	static {
		MimeHashTable = sun.net.www.MimeTable.getDefaultTable();
		AddMimeEntryImpl("text/css", ".css");
		AddMimeEntryImpl("application/javascript", ".js");
		AddMimeEntryImpl("application/json", ".json");
		// CHECKOUT http://www.iana.org/assignments/media-types/media-types.xhtml
		AddMimeEntryImpl("audio/vnd.wave", ".wav");
		AddMimeEntryImpl("audio/mpeg", ".mp3");
		AddMimeEntryImpl("audio/ogg", ".ogg");
		AddMimeEntryImpl("image/x-icon", ".ico");
		AddMimeEntryImpl("image/svg+xml", ".svg,.svgz");
		AddMimeEntryImpl("application/font-woff", ".woff");// http://www.iana.org/assignments/media-types/application/font-woff
		AddMimeEntryImpl("application/font-sfnt", ".otf,.ttf");	// http://www.iana.org/assignments/media-types/application/font-sfnt
		AddMimeEntryImpl("application/vnd.ms-fontobject", ".eot");
		MimeTypeMap = new HashMap<String, MimeEntry>(MimeHashTable.getSize() * 2, 0.25f);
		MimeExtensionsMap = new HashMap<String, MimeEntry>(MimeHashTable.getSize() * 2, 0.25f);
		RebuildMimeMaps();
	}
	
	/**
	 * Add a mime entry to the table.
	 * If the type already exists, the 'extensions' will be added, otherwise a new type will be added with the specified extensions.
	 * @param type	Mime formatted type
	 * @param extensions	Comma separated list of dot (.) prefixed extensions.
	 * @throws RuntimeException If the extensions string is not properly formatted (see parameters).
	 */
	protected static void AddMimeEntry(String type, String extensions) {
		AddMimeEntryImpl(type, extensions);
		RebuildMimeMaps();
	}
	
	/**
	 * <code>Hashtable</code> is synchronized, and there is no need for us to suffer that slow down.
	 * This method rebuilds two <code>HashMap</code>s that we use to lookup <code>MimeEntry</code>s with.
	 */
	private static void RebuildMimeMaps() {
		synchronized(MimeHashTable) {
			MimeTypeMap.clear();
			MimeExtensionsMap.clear();
			Enumeration<MimeEntry> elems = MimeHashTable.elements();
			while (elems.hasMoreElements()) {
				MimeEntry elem = elems.nextElement();
				MimeTypeMap.put(elem.getType(), elem);
				String[] extensions = elem.getExtensions();
				if (extensions != null)
					for (String ext : extensions)
						MimeExtensionsMap.put(ext, elem);
			}
		}
	}

	/**
	 * @see AddMimeEntry
	 */
	private static void AddMimeEntryImpl(String type, String extensions) {
		LinkedHashSet<String> extSet = new LinkedHashSet<String>();
		MimeEntry entry = MimeHashTable.find(type);
		if (entry == null)
			entry = new sun.net.www.MimeEntry(type.intern());
		// Ensure the type is an interned string
		entry.setType(type.intern());
		
		String[] existing = entry.getExtensions();
		if (existing != null)
			for (String ext : existing)
				extSet.add(ext);
		String[] additional = extensions.split(",");
		for (int i=0; i<additional.length; i++) {
			additional[i] = additional[i].trim().toLowerCase();
			if (additional[i].length() == 0)
				throw new RuntimeException("Invalid mime extensions for: " + type);
			if (additional[i].charAt(0) != '.')
				throw new RuntimeException("mime extensions must start with a '.' (" + type + ")");
			extSet.add(additional[i]);
		}
		StringBuffer sb = new StringBuffer();
		for (String ext : extSet) {
			if (sb.length() > 0)
				sb.append(',');
			sb.append(ext);
		}
		entry.setExtensions(sb.toString());
		// This little hack ensures that the MimeEntry itself has interned strings in it's list.  Yes it's a trade off between bad practice and speed.
		String[] processed = entry.getExtensions();
		for (int i=0; i<processed.length; i++)
			processed[i] = processed[i].intern();
	}

	/**
	 * Return the underlying connection for this request / response transaction
	 */
	private NHttpServerConnection getConnection() {
		return (NHttpServerConnection) context.getAttribute(HttpCoreContext.HTTP_CONNECTION);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InetAddress getLocalAddress() {
		HttpInetConnection inetConn = (HttpInetConnection) getConnection();
		return inetConn.getLocalAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getLocalPort() {
		HttpInetConnection inetConn = (HttpInetConnection) getConnection();
		return inetConn.getLocalPort();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public InetAddress getRemoteAddress() {
		HttpInetConnection inetConn = (HttpInetConnection) getConnection();
		return inetConn.getRemoteAddress();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getRemotePort() {
		HttpInetConnection inetConn = (HttpInetConnection) getConnection();
		return inetConn.getRemotePort();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getHOSTName() {
		String val = null;
		Header hdr = request.getFirstHeader("HOST");
		if (hdr != null)
			val = hdr.getValue().trim();
		if ((val != null) && (val.length() > 0)) {
			int pos = val.indexOf(':');
			if (pos > 0)
				return val.substring(0, pos);
		}
		return getLocalAddress().getHostName();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public int getHOSTPort() {
		String val = null;
		Header hdr = request.getFirstHeader("HOST");
		if (hdr != null)
			val = hdr.getValue().trim();
		if ((val != null) && (val.length() > 0)) {
			int pos = val.indexOf(':');
			if (pos > 0) {
				int end = val.indexOf(';', pos);
				if (end <= pos)
					end = val.length();
				try {
					return Integer.parseInt(val.substring(pos + 1, end));
				}
				catch (Throwable t) {
					// Ignore any possible error.
				}
			}
		}
		if ("https".equals(getScheme()))
			return 443;
		return 80;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String getCharacterEncoding() {
		Header hdr = request.getFirstHeader("Content-Type");
		if (hdr != null)
			return GetCharsetFromContentType(hdr.getValue());
		return "utf-8";
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DateFormat getHTTPDateFormater() {
		return Utils.GetHTTPDateFormater();
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public String formatDate(long millisecondsSinceEpoch) {
		return Utils.GetHTTPDateFormater().format(new Date(millisecondsSinceEpoch));
	}

	/**
	 * Determine if a URI string has a <code>scheme</code> component.
	 */
	public static boolean HasScheme(String uri) {
		int len = uri.length();
		for (int i = 0; i < len; i++) {
			char c = uri.charAt(i);
			if (c == ':')
				return i > 0;
			else if (!(Character.isLetterOrDigit(c) || c == '+' || c == '-' || c == '.'))
				return false;
		}
		return false;
	}

	/**
	 * Percent-encode values according the RFC 3986. The built-in Java URLEncoder does not encode according to the RFC, so we make the extra replacements.
	 * 
	 * @param string Decoded string.
	 * @return Encoded string per RFC 3986.
	 */
	public static String PercentEncodeRfc3986(final String string) {
		try {
			return URLEncoder.encode(string, "UTF-8").replace("+", "%20").replace("*", "%2A").replace("%7E", "~");
		}
		catch (UnsupportedEncodingException e) {
			return string;
		}
	}

	/**
	 * Normalization code courtesy of 'Mike Houston' http://stackoverflow.com/questions/2993649/how-to-normalize-a-url-in-java
	 */
	public static String NormalizeURL(final String taintedURL) throws MalformedURLException {
		final URL url;
		try {
			url = new URI(taintedURL).normalize().toURL();
		}
		catch (URISyntaxException e) {
			throw new MalformedURLException(e.getMessage());
		}

		final String path = url.getPath().replace("/$", "");
		final SortedMap<String, String> params = CreateParameterMap(url.getQuery());
		final int port = url.getPort();
		final String queryString;

		if (params != null) {
			// Some params are only relevant for user tracking, so remove the most commons ones.
			for (Iterator<String> i = params.keySet().iterator(); i.hasNext();) {
				final String key = i.next();
				if (key.startsWith("utm_") || key.contains("session"))
					i.remove();
			}
			queryString = "?" + Canonicalize(params);
		}
		else
			queryString = "";

		return url.getProtocol() + "://" + url.getHost() + (port != -1 && port != 80 ? ":" + port : "") + path + queryString;
	}

	/**
	 * Takes a query string, separates the constituent name-value pairs, and stores them in a SortedMap ordered by lexicographical order.
	 * 
	 * @return Null if there is no query string.
	 */
	private static SortedMap<String, String> CreateParameterMap(final String queryString) {
		if (queryString == null || queryString.isEmpty())
			return null;
		final String[] pairs = queryString.split("&");
		final Map<String, String> params = new HashMap<String, String>(pairs.length);
		for (final String pair : pairs) {
			if (pair.length() < 1)
				continue;
			String[] tokens = pair.split("=", 2);
			for (int j = 0; j < tokens.length; j++) {
				try {
					tokens[j] = URLDecoder.decode(tokens[j], "UTF-8");
				}
				catch (UnsupportedEncodingException ex) {
					ex.printStackTrace();
				}
			}
			switch (tokens.length) {
				case 0:
					break;
				case 1:
					if (pair.charAt(0) == '=')
						params.put("", tokens[0]);
					else
						params.put(tokens[0], "");
					break;
				case 2:
				default:
					params.put(tokens[0], tokens[1]);
					break;
			}
		}
		return new TreeMap<String, String>(params);
	}

	/**
	 * Canonicalize the query string.
	 * 
	 * @param sortedParamMap Parameter name-value pairs in lexicographical order.
	 * @return Canonical form of query string.
	 */
	private static String Canonicalize(final SortedMap<String, String> sortedParamMap) {
		if (sortedParamMap == null || sortedParamMap.isEmpty())
			return "";
		final StringBuffer sb = new StringBuffer(350);
		final Iterator<Map.Entry<String, String>> iter = sortedParamMap.entrySet().iterator();
		while (iter.hasNext()) {
			final Map.Entry<String, String> pair = iter.next();
			sb.append(PercentEncodeRfc3986(pair.getKey()));
			sb.append('=');
			sb.append(PercentEncodeRfc3986(pair.getValue()));
			if (iter.hasNext())
				sb.append('&');
		}
		return sb.toString();
	}

	/**
	 * Parse the character encoding from the specified content type header. 
	 * If the content type is null, or there is no explicit character encoding, <code>null</code> is returned.
	 * 
	 * @param contentType a content type header
	 */
	public static String GetCharsetFromContentType(String contentType) {
		if (contentType == null)
			return null;
		int start = contentType.indexOf("charset=");
		if (start < 0)
			return null;
		String encoding = contentType.substring(start + 8);
		int end = encoding.indexOf(';');
		if (end >= 0)
			encoding = encoding.substring(0, end);
		encoding = encoding.trim();
		if ((encoding.length() > 2) && (encoding.startsWith("\"")) && (encoding.endsWith("\"")))
			encoding = encoding.substring(1, encoding.length() - 1);
		return encoding.trim();
	}
}