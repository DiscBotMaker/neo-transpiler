package net.noerlol.neotrans.project;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Scanner;

public class ProjectCreator {
    private static final ArrayList<File> folder_Structure = new ArrayList<>();
    private static final ArrayList<File> file_Structure = new ArrayList<>();

    private static boolean recreate = false;

    public static void init() {
        file_Structure.add(new File("project.yml"));

        folder_Structure.add(new File("build"));
        folder_Structure.add(new File(".build-cache"));
        folder_Structure.add(new File("src"));

        try {
            for (File file : file_Structure) {
                if (file.exists()) {
                    System.err.println("already exists: " + file.getName() + " would you like to recreate?");
                    if (new Scanner(System.in).next().startsWith("y")) {
                        Files.createFile(Path.of(file.getAbsolutePath()));
                        recreate = true;
                    } else {
                        continue;
                    }
                }
                Files.createFile(Path.of(file.getAbsolutePath()));
            }

            for (File folder : folder_Structure) {
                if (folder.exists()) {
                    System.err.println("already exists: " + folder.getName() + " would you like to recreate?");
                    if (new Scanner(System.in).next().startsWith("y")) {
                        Files.createDirectory(Path.of(folder.getAbsolutePath()));
                        recreate = true;
                    } else {
                        continue;
                    }
                }
                Files.createDirectory(Path.of(folder.getAbsolutePath()));
                boolean x = true;
                boolean y = true;
                boolean z = false;
            }

            if (!recreate) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(new File("src" + File.separator + "main.dbm").getAbsoluteFile()))) {
                    String str =
                            """
                            fn main() {
                                println("Hello, world!");
                            }
                            """;
                    writer.write(str);
                    writer.close();
                }
            }
        } catch (IOException e) {
            System.err.println("fatal error: " + e.getMessage());
            System.exit(1);
        }
    }
}
