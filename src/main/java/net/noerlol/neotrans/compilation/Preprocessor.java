package net.noerlol.neotrans.compilation;

import net.noerlol.neotrans.utils.Version;

public class Preprocessor {
    private Preprocessor() {}

    private static String handleMacro(String macro) {
        if (macro.equals("__stdversion__")) {
            return Version.STDVERSION;
        } else if (macro.equals("__operating_system__")) {
            return "\"" + System.getProperty("os.name") + "\"";
        } else if (macro.equals("__J_classpath__")) {
            return "\"" + "I need to fix this!!!!" + "\"";
        }

        else {
            return macro;
        }
    }

    static String processLine(String line) {
        line = line.replace("__stdversion__", handleMacro("__stdversion__"));
        line = line.replace("__operating_system__", handleMacro("__operating_system__"));
        line = line.replace("__J_classpath__", handleMacro("__J_classpath__"));

        line = line.replace("string", "String");
        line = line.replace("dbm.lang", "java.lang");

        return line;
    }
}
