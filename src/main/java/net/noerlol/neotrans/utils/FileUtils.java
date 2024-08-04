package net.noerlol.neotrans.utils;

import java.io.File;
import java.nio.file.Path;
import java.util.ArrayList;
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
        ArrayList<File> files = new ArrayList<>();
        for (String str : R_ListFiles(path.toFile())) {
            files.add(new File(str).getAbsoluteFile());
        }

        for (File file : files) {
            if (!file.isDirectory()) {
                file.delete();
                files.remove(file);
            }
        }
        for (File file : files) {
            if (file.isDirectory()) {
                file.delete();
                files.remove(file);
            }
        }

        if (!files.isEmpty()) {
            System.out.println("an error occurred");
        }
    }
}
