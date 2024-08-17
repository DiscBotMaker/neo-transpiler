package net.noerlol.neotrans.utils;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

public class Import {
    private final String className;
    private final String packageName;

    public Import(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public Import[] getExpandedForm() throws IOException {
        if (!className.equals("*")) {
            return new Import[]{new Import(this.packageName, className)};
        }

        // className == "*"
        String[] classNames = getClassesFromPackage();
        for (String c : classNames) {
            System.out.println(c);
        }
        return null;
    }

    private String[] getClassesFromPackage() throws IOException {
        List<String> classNames = new ArrayList<>();
        String packagePath = packageName.replace('.', '/');

        try (JarFile jarFile = new JarFile("lib" + File.separator + "stdlib" + Version.STDLIB_VERSION + ".jar")) {
            Enumeration<JarEntry> entries = jarFile.entries();
            while (entries.hasMoreElements()) {
                JarEntry entry = entries.nextElement();
                String entryName = entry.getName();
                if (entryName.startsWith(packagePath) && entryName.endsWith(".class")) {
                    String className = entryName.substring(0, entryName.lastIndexOf('.')).replace('/', '.');
                    classNames.add(className);
                }
            }
        }
        return classNames.toArray(new String[0]);
    }
}
