package net.noerlol.neotrans.utils;

import java.io.File;

public class OperatingSystem {
    public static boolean isUnix() {
        return File.separatorChar == '/';
    }

    public static boolean isWindows() {
        return !(File.separatorChar == '/');
    }
}
