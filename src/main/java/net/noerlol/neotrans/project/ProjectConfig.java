package net.noerlol.neotrans.project;

import net.noerlol.neotrans.utils.ClassResourceFetcher;
import org.intellij.lang.annotations.Language;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;

import java.io.*;
import java.util.Map;

public class ProjectConfig {
    private Map<String, Object> config;

    public void writeConfig(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            @Language("yaml") String defaultConfig = ClassResourceFetcher.readFromFile("project.yml", ProjectConfig.class);
            writer.write(defaultConfig);
            writer.close();
            loadConfig(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void loadConfig(File file) {
        Yaml yaml = new Yaml();
        try (InputStream inputStream = new FileInputStream(file)) {
            this.config = yaml.load(inputStream);
        } catch (Exception e) {
            throw new YAMLException(e);
        }
    }

    private Object getValue(String key) {
        String[] keys = key.split("\\.");
        Map<String, Object> currentMap = this.config;
        Object value = null;

        for (String k : keys) {
            value = currentMap.get(k);
            if (value instanceof Map) {
                currentMap = (Map<String, Object>) value;
            }
        }
        return value;
    }

    public String getString(String key) {
        Object value = getValue(key);
        if (value instanceof String) {
            return (String) value;
        } else {
            throw new IllegalArgumentException("Error at getString(): Not a string! Key: " + key);
        }
    }

    public int getInteger(String key) {
        Object value = getValue(key);
        if (value instanceof Integer) {
            return (Integer) value;
        } else {
            throw new IllegalArgumentException("Error at getInteger(): Not an integer! Key: " + key);
        }
    }

    public boolean getBoolean(String key) {
        Object value = getValue(key);
        if (value instanceof Boolean) {
            return (Boolean) value;
        } else {
            throw new IllegalArgumentException("Error at getBoolean(): Not a boolean! Key: " + key + " Value: " + value);
        }
    }
}
