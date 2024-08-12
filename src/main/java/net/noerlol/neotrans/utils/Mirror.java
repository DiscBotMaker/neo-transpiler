package net.noerlol.neotrans.utils;

import java.net.MalformedURLException;
import java.net.URL;

public class Mirror {
    private final URL url;
    public static Mirror OFFICIAL_MIRROR = officialRepo();

    private static final String MIRROR_URL = "https://raw.githubusercontent.com/DiscBotMaker/important-links/main/classurl.yaml";

    public Mirror(URL url) {
        this.url = url;
    }

    private static Mirror officialRepo() {
        try {
            return new Mirror(new URL(MIRROR_URL));
        } catch (MalformedURLException ignored) {
            throw new IllegalArgumentException(MIRROR_URL + " does not exist.. [?]");
        }
    }

    public URL getUrl() {
        return url;
    }
}
