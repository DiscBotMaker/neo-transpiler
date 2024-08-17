package net.noerlol.neotrans.utils;

import java.io.PrintStream;
import java.util.regex.Pattern;

public class ErrorFixSuggester {
    private static final String REPEATED_CHAR = "~";

    public static void fix(String originalString, char unknownCharacter, PrintStream printStream, int lineNum, String fileName) {
        String msg = "";
        for (int i = 0; i < originalString.length(); i++) {
            msg += REPEATED_CHAR;
        }
        for (int i = 0; i < originalString.length(); i++) {
            if (originalString.charAt(i) == unknownCharacter) {
                char[] temp = msg.toCharArray();
                temp[i] = '^';
                msg = String.valueOf(temp);
            }
        }
        msg = " | " + msg;
        String padding = " ".repeat(String.valueOf(lineNum).length());
        printStream.println(fileName + ":" + lineNum + ":" + stripLeadingZeroes(String.valueOf(originalString.indexOf(unknownCharacter) + 1)));
        printStream.println("\b" + lineNum + " | " + originalString);
        printStream.println(padding + msg);
    }

    public static void fix(String originalString, String unknownCharacter, PrintStream printStream, int lineNum, String fileName) {
        String msg = "";
        for (int i = 0; i < originalString.length(); i++) {
            msg += REPEATED_CHAR;
        }
        for (int i = 0; i < originalString.length(); i++) {
            if (originalString.contains(unknownCharacter)) {
                char[] temp = msg.toCharArray();
                temp[i] = '^';
                msg = String.valueOf(temp);
            }
        }
        msg = " | " + msg;
        String padding = " ".repeat(String.valueOf(lineNum).length());
        printStream.println(fileName + ":" + lineNum);
        printStream.println("\b" + lineNum + " | " + originalString);
        printStream.println(padding + msg);
    }

    private static String stripLeadingZeroes(String str) {
        return Pattern.compile("^0+").matcher(str).replaceFirst("");
    }
}
