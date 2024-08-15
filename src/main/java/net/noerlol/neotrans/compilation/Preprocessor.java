package net.noerlol.neotrans.compilation;

import net.noerlol.neotrans.utils.Version;

public class Preprocessor {
    private Preprocessor() {}

    static String processLine(String line) {
        line = line.replace("__stdversion__", "\"" + Version.STDLIB_VERSION + "\"");
        line = line.replace("__operating_system__", "\"" + System.getProperty("os.name") + "\"");

        line = line.replace("__J_classpath__", "\"" + "I need to fix this!!!!" + "\"");
        line = line.replace("__J_version__", "\"" + System.getProperty("java.version") + "\"");

        line = line.replace("string", "String");
        line = line.replace("dbm.lang", "java.lang");

        return line;
    }
}
