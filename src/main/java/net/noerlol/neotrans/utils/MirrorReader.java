package net.noerlol.neotrans.utils;

import org.yaml.snakeyaml.Yaml;

import java.io.FileInputStream;
import java.io.InputStream;
import java.io.StringReader;
import java.util.HashMap;
import java.util.Map;

public class MirrorReader {
    private Map<String, Object> mirror = new HashMap<>();

    public void loadFromString(String str) {
        Yaml yaml = new Yaml();
        this.mirror = yaml.load(new StringReader(str));
    }

    private String getValue(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = this.mirror;
        Object value = null;

        for (String k : keys) {
            value = currentMap.get(k);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            }
        }
        assert value instanceof String;
        return (String) value;
    }

    public Map<String, String> getLibraryInfo(String libName) {
        Map<String, Object> libInfo = (Map<String, Object>) this.mirror.get(libName);
        if (libInfo == null) {
            throw new NullPointerException(String.format("null at: %s", "[variable 'libInfo' of type java.util.Map<java.lang.String, java.lang.Object>]"));
        }

        Map<String, String> result = new HashMap<>();
        for (Map.Entry<String, Object> entry : libInfo.entrySet()) {
            result.put(entry.getKey(), entry.getValue().toString());
        }

        return result;
    }
}
