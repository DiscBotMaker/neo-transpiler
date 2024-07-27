package net.noerlol.neotrans.utils;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

public class ClassResourceFetcher {
    public static String readFromFile(String filePathInResources, Class<?> clazz) throws IOException {
        BufferedReader reader;
        InputStream inputStream = clazz.getClassLoader().getResourceAsStream(filePathInResources);
        if (inputStream == null) {
            throw new NullPointerException("file path not found");
        }

        StringBuilder buf = new StringBuilder();
        reader = new BufferedReader(new InputStreamReader(inputStream));
        String line;
        while ((line = reader.readLine()) != null) {
            buf.append(line).append(System.lineSeparator());
        }

        return buf.toString();
    }
}
