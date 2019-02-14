public class foo {
public ArrayList<Video> getStreamingUrisFromYouTubePage(String ytUrl)
        throws IOException {
    if (ytUrl == null) {
        return null;
    }

    // Remove any query params in query string after the watch?v=<vid> in
    // e.g.
    // http://www.youtube.com/watch?v=0RUPACpf8Vs&feature=youtube_gdata_player
    int andIdx = ytUrl.indexOf('&');
    if (andIdx >= 0) {
        ytUrl = ytUrl.substring(0, andIdx);
    }

    // Get the HTML response
    String userAgent = "Mozilla/5.0 (Windows NT 6.1; WOW64; rv:8.0.1)";
    HttpClient client = new DefaultHttpClient();
    client.getParams().setParameter(CoreProtocolPNames.USER_AGENT,
            userAgent);
    HttpGet request = new HttpGet(ytUrl);
    HttpResponse response = client.execute(request);
    String html = "";
    InputStream in = response.getEntity().getContent();
    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
    StringBuilder str = new StringBuilder();
    String line = null;
    while ((line = reader.readLine()) != null) {
        str.append(line.replace("\\u0026", "&"));
    }
    in.close();
    html = str.toString();

    // Parse the HTML response and extract the streaming URIs
    if (html.contains("verify-age-thumb")) {
        CLog.w("YouTube is asking for age verification. We can't handle that sorry.");
        return null;
    }

    if (html.contains("das_captcha")) {
        CLog.w("Captcha found, please try with different IP address.");
        return null;
    }

    Pattern p = Pattern.compile("stream_map\": \"(.*?)?\"");
    // Pattern p = Pattern.compile("/stream_map=(.[^&]*?)\"/");
    Matcher m = p.matcher(html);
    List<String> matches = new ArrayList<String>();
    while (m.find()) {
        matches.add(m.group());
    }

    if (matches.size() != 1) {
        CLog.w("Found zero or too many stream maps.");
        return null;
    }

    String urls[] = matches.get(0).split(",");
    HashMap<String, String> foundArray = new HashMap<String, String>();
    for (String ppUrl : urls) {
        String url = URLDecoder.decode(ppUrl, "UTF-8");

        Pattern p1 = Pattern.compile("itag=([0-9]+?)[&]");
        Matcher m1 = p1.matcher(url);
        String itag = null;
        if (m1.find()) {
            itag = m1.group(1);
        }

        Pattern p2 = Pattern.compile("sig=(.*?)[&]");
        Matcher m2 = p2.matcher(url);
        String sig = null;
        if (m2.find()) {
            sig = m2.group(1);
        }

        Pattern p3 = Pattern.compile("url=(.*?)[&]");
        Matcher m3 = p3.matcher(ppUrl);
        String um = null;
        if (m3.find()) {
            um = m3.group(1);
        }

        if (itag != null && sig != null && um != null) {
            foundArray.put(itag, URLDecoder.decode(um, "UTF-8") + "&"
                    + "signature=" + sig);
        }
    }

    if (foundArray.size() == 0) {
        CLog.w("Couldn't find any URLs and corresponding signatures");
        return null;
    }

    HashMap<String, Meta> typeMap = new HashMap<String, Meta>();
    typeMap.put("13", new Meta("13", "3GP", "Low Quality - 176x144"));
    typeMap.put("17", new Meta("17", "3GP", "Medium Quality - 176x144"));
    typeMap.put("36", new Meta("36", "3GP", "High Quality - 320x240"));
    typeMap.put("5", new Meta("5", "FLV", "Low Quality - 400x226"));
    typeMap.put("6", new Meta("6", "FLV", "Medium Quality - 640x360"));
    typeMap.put("34", new Meta("34", "FLV", "Medium Quality - 640x360"));
    typeMap.put("35", new Meta("35", "FLV", "High Quality - 854x480"));
    typeMap.put("43", new Meta("43", "WEBM", "Low Quality - 640x360"));
    typeMap.put("44", new Meta("44", "WEBM", "Medium Quality - 854x480"));
    typeMap.put("45", new Meta("45", "WEBM", "High Quality - 1280x720"));
    typeMap.put("18", new Meta("18", "MP4", "Medium Quality - 480x360"));
    typeMap.put("22", new Meta("22", "MP4", "High Quality - 1280x720"));
    typeMap.put("37", new Meta("37", "MP4", "High Quality - 1920x1080"));
    typeMap.put("33", new Meta("38", "MP4", "High Quality - 4096x230"));

    ArrayList<Video> videos = new ArrayList<ARViewer.Video>();

    for (String format : typeMap.keySet()) {
        Meta meta = typeMap.get(format);

        if (foundArray.containsKey(format)) {
            Video newVideo = new Video(meta.ext, meta.type,
                    foundArray.get(format));
            videos.add(newVideo);
            CLog.d("YouTube Video streaming details: ext:" + newVideo.ext
                    + ", type:" + newVideo.type + ", url:" + newVideo.url);
        }
    }

    return videos;
}
}