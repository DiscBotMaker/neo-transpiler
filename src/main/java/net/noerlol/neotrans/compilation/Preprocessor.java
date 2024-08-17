package net.noerlol.neotrans.compilation;

import net.noerlol.neotrans.utils.ErrorFixSuggester;
import net.noerlol.neotrans.utils.Version;

import java.io.PrintStream;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Preprocessor {
    private final Map<String, String> macros = new HashMap<>();

    public Preprocessor() {
        macros.put("__stdversion__", "\"" + Version.STDLIB_VERSION + "\"");
        macros.put("__operating_system__", "\"" + System.getProperty("os.name") + "\"");
        macros.put("__J_classpath__", "\"" + "I need to fix this!!!!" + "\"");
        macros.put("__J_version__", "\"" + System.getProperty("java.version") + "\"");
    }

    public String processLine(String line, PrintStream errorWriter, String fileName) {
        if (line.startsWith("_define")) {
            String macroData = line.split("_define")[1].trim();
            if (line.split("_define").length == 1) {
                ErrorFixSuggester.fix(line, macroData, errorWriter, -1, fileName);
                errorWriter.println("syntax-error: " + "found 1 argument" + System.lineSeparator());
            }
            String key = macroData.split(" ")[0].trim();
            String[] values = macroData.split(" ");
            String value = "";
            for (String v : values) {
                value += v.replace(key, "").trim() + " ";
            }
            value = value.split("//")[0].trim();
            if (!(key.startsWith("__") && key.endsWith("__"))) {
                ErrorFixSuggester.fix(line, key, errorWriter, -1, fileName);
                errorWriter.println("syntax-error: " + "expected macro to start with __ and end with __" + System.lineSeparator());
            }
            macros.put(key, value);
        } else if (line.startsWith("_undef")) {
            String macroData = line.split("_undef")[1].trim();
            if (line.split("_undef").length == 0) {
                ErrorFixSuggester.fix(line, macroData, errorWriter, -1, fileName);
                errorWriter.println("syntax-error: " + "found 0 argument" + System.lineSeparator());
            }
            String key = macroData.split(" ")[0].trim();
            if (!macros.containsKey(key)) {
                ErrorFixSuggester.fix(line, "_undef", errorWriter, -1, fileName);
                errorWriter.println("syntax-error: " + "macro has not been defined before" + System.lineSeparator());
            }
            macros.remove(key);
        }

        Pattern pattern = Pattern.compile("__.*__");
        Matcher matcher = pattern.matcher(line);

        while (matcher.find()) {
            String found = matcher.group();
            if (macros.containsKey(found)) {
                line = line.replace(found, macros.get(found));
            }
        }

        line = line
                .replace("bool", "boolean")
                .replace("string", "String")
                .replace("dbm.lang", "java.lang");

        return line;
    }
}
