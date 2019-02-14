package com.masnun.controllers;


import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.web.WebView;

import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.net.URLDecoder;
import java.util.LinkedHashMap;
import java.util.Map;

public class MainController {

    @FXML
    private WebView webView;
    @FXML
    private Button playButton;
    @FXML
    private TextField urlInput;

    @FXML
    public void initialize() {
        String welcomeHtmlPath = getClass().getClassLoader().getResource("welcome.html").toExternalForm();
        webView.getEngine().load(welcomeHtmlPath);

        urlInput.setText("https://www.youtube.com/watch?v=mQD4zr4GlZg");
    }

    @FXML
    protected void handlePlayButtonClick(MouseEvent event) throws Exception {

        String youtubeURL = urlInput.getText();
        String embedURL = getEmbedURL(youtubeURL);
        webView.getEngine().load(embedURL);

    }

    private String getEmbedURL(String youtubeURL) throws Exception {
        URL url = new URL(youtubeURL);
        Map<String, String> map = splitQuery(url);
        String videoID = map.get("v");
        return "http://www.youtube.com/embed/" + videoID + "?autoplay=1&loop=1&playlist=" + videoID;
    }

    // Copied from - http://stackoverflow.com/questions/13592236/parse-a-uri-string-into-name-value-collection
    private Map<String, String> splitQuery(URL url) throws UnsupportedEncodingException {
        Map<String, String> query_pairs = new LinkedHashMap<String, String>();
        String query = url.getQuery();
        String[] pairs = query.split("&");
        for (String pair : pairs) {
            int idx = pair.indexOf("=");
            query_pairs.put(URLDecoder.decode(pair.substring(0, idx), "UTF-8"), URLDecoder.decode(pair.substring(idx + 1), "UTF-8"));
        }
        return query_pairs;
    }
}
