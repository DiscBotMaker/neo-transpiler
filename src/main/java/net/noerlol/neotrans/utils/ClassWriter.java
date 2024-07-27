package net.noerlol.neotrans.utils;

import net.noerlol.neotrans.utils.TranspiledCode;

import javax.tools.JavaCompiler;
import javax.tools.ToolProvider;
import java.io.*;
import java.nio.file.*;
import java.util.ArrayList;
import java.util.jar.JarEntry;
import java.util.jar.JarOutputStream;
import java.util.jar.Manifest;

public class ClassWriter {
    private static final String BUILD_CACHE = ".build-cache";

    public static TimeSignature write(TranspiledCode code, String fileName, String packageName) throws IOException {
        long uid = System.currentTimeMillis();

        // Capitalize class name
        String className = capitalizeClassName(fileName.replace(".dbm", ""));

        packageName = "src";

        // Prepare directories
        Path basePath = Paths.get(BUILD_CACHE, String.valueOf(uid));
        Files.createDirectories(basePath.resolve("classes"));
        Files.createDirectories(basePath.resolve("built"));

        // Write Java source file
        Path javaFilePath = basePath.resolve("built").resolve(className + ".java");
        try (BufferedWriter writer = Files.newBufferedWriter(javaFilePath)) {
            code.setClassName(className);
            String writableCode = "package " + packageName + ";\n" + code.getCode();

            writer.write(writableCode);
        }

        // Compile to .class files
        compileToClasses(basePath.resolve("built").toString(), basePath.resolve("classes").toString(), "lib" + File.separator + "libdbm" + Version.libdbm_VERSION + ".jar:lib" + File.separator + "libjda" + Version.libjda_VERSION + ".jar");

        // Create JAR file
        createJarFile(basePath.resolve("classes" + File.separator + "src").toFile(), "build" + File.separator + "compiled" + ".jar", uid);

        return new TimeSignature(uid);
    }

    private static String capitalizeClassName(String name) {
        if (name == null || name.isEmpty()) {
            return name;
        }
        return Character.toUpperCase(name.charAt(0)) + name.substring(1);
    }

    private static void compileToClasses(String sourceFolder, String outputFolder, String classpath) {
        JavaCompiler compiler = ToolProvider.getSystemJavaCompiler();
        File[] files = new File(sourceFolder).listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isFile() && file.getName().endsWith(".java")) {
                    String path = file.getAbsolutePath();
                    int result = compiler.run(null, null, null, "-cp", classpath, path, "-d", outputFolder);
                    if (result != 0) {
                        System.out.println("Compilation failed with code: " + result);
                    }
                }
            }
        }
    }

    private static void createJarFile(File pathToClasses, String pathToFinalJar, long uid) throws IOException {
        if (!pathToClasses.isDirectory()) {
            throw new IllegalArgumentException("argument not a directory");
        }

        try (FileOutputStream fos = new FileOutputStream(pathToFinalJar);
             JarOutputStream jos = new JarOutputStream(fos)) {

            addFilesToJar(pathToClasses, jos, pathToClasses.getAbsolutePath().length() + 1);
        }
    }

    private static void addFilesToJar(File source, JarOutputStream jos, int basePathLength) throws IOException {
        File[] files = source.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    addFilesToJar(file, jos, basePathLength);
                } else {
                    String entryName = "src" + File.separator + file.getAbsolutePath().substring(basePathLength).replace("\\", "/");
                    JarEntry jarEntry = new JarEntry(entryName);
                    jos.putNextEntry(jarEntry);

                    try (FileInputStream fis = new FileInputStream(file)) {
                        byte[] buffer = new byte[1024];
                        int bytesRead;
                        while ((bytesRead = fis.read(buffer)) != -1) {
                            jos.write(buffer, 0, bytesRead);
                        }
                    }

                    jos.closeEntry();
                }
            }
        }
    }

    private static String getPackageNameFromPath(String baseDir, String filePath) {
        baseDir = baseDir.replace(File.separator, "/");
        filePath = filePath.replace(File.separator, "/");

        if (!filePath.startsWith(baseDir)) {
            throw new IllegalArgumentException("File path is not within the base directory");
        }

        String relativePath = filePath.substring(baseDir.length());
        relativePath = relativePath.substring(0, relativePath.lastIndexOf("/"));
        return relativePath.replace("/", ".");
    }
}
