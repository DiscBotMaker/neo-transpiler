package net.noerlol.neotrans.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class Mirror {
    private final URL url;
    public static Mirror officialMirror = officialRepo();

    public Mirror(URL url) {
        this.url = url;
    }

    private static Mirror officialRepo() {
        try {
            return new Mirror(new URL("https://raw.githubusercontent.com/DiscBotMaker/important-links/main/classurl.txt"));
        } catch (MalformedURLException ignored) {
            throw new IllegalArgumentException("https://raw.githubusercontent.com/DiscBotMaker/important-links/main/classurl.txt is not existing?");
        }
    }

    public URL getUrl() {
        return url;
    }
}
