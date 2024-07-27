package net.noerlol.neotrans.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class FileUtils {
    private ArrayList<String> listedFiles = new ArrayList<>();
    private String fullPath = "";
    public ArrayList<String> listFiles(File directory) {
        File[] files = directory.listFiles();

        if (files == null) {
            System.err.println("Error accessing directory: " + directory.getPath());
            System.exit(1);
        }

        for (File file : files) {
            if (file.isDirectory()) {
                listFiles(file);
            } else {
                listedFiles.add(file.getPath());
            }
        }
        return listedFiles;
    }
}
