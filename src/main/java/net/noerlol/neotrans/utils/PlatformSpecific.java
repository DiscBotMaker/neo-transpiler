package net.noerlol.neotrans.utils;

public class PlatformSpecific {
    public static String CLASSPATH_SEPARATOR = ":";
    static {
        if (System.lineSeparator().equals("\r\n")) {
            CLASSPATH_SEPARATOR = ";";
        }
    }
}
