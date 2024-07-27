package net.noerlol.neotrans.utils;

public class Import {
    private final String packageName;
    private final String className;

    public Import(Class<?> clazz) {
        this.className = clazz.getName().split("\\.")[clazz.getName().split("\\.").length - 1];
        this.packageName = clazz.getPackageName();
    }

    public Import(String className, String packageName) {
        this.className = className;
        this.packageName = packageName;
    }

    public String getPackageName() {
        return packageName;
    }

    public String getClassName() {
        return className;
    }
}
