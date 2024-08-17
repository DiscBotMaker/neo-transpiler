package net.noerlol.neotrans.utils;

public class PlatformSpecific {
    public static String CLASSPATH_SEPARATOR = ":";
    static {
        if (OperatingSystem.isWindows()) {
            CLASSPATH_SEPARATOR = ";";
        }
    }
}
