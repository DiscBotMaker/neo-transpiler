package net.noerlol.neotrans.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FileUtils {
    private ArrayList<String> listedFiles = new ArrayList<>();
    private String fullPath = "";
    public ArrayList<String> R_ListFiles(File directory) {
        File[] files = directory.listFiles();

        if (files == null) {
            System.err.println("Error accessing directory: " + directory.getPath());
            System.exit(1);
        }

        for (File file : files) {
            if (file.isDirectory()) {
                R_ListFiles(file);
            } else {
                listedFiles.add(file.getPath());
            }
        }
        return listedFiles;
    }

    public ArrayList<File> listFiles(File directory) {
        return new ArrayList<>(List.of(Objects.requireNonNull(directory.listFiles())));
    }

    public void deleteRecursive(Path path) {
        File file = path.toFile();

        // Check if it's a directory
        if (file.isDirectory()) {
            // Get all the files in the directory
            File[] files = file.listFiles();
            if (files != null) { // Avoid null pointer exception
                for (File f : files) {
                    deleteRecursive(f.toPath()); // Recursive call
                }
            }
        }

        // Delete the file or empty directory
        boolean deleted = file.delete();
        if (!deleted) {
            System.out.println("Failed to delete: " + file.getAbsolutePath());
        }
    }
}
