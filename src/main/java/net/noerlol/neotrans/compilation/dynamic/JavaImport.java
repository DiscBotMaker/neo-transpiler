package net.noerlol.neotrans.compilation.dynamic;

public class JavaImport {
    private final String packageName;
    private final String className;

    public JavaImport(Class<?> clazz) {
        this.className = clazz.getName().split("\\.")[clazz.getName().split("\\.").length - 1];
        this.packageName = clazz.getPackageName();
    }

    public JavaImport(String className, String packageName) {
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
