package net.noerlol.neotrans.utils;

import java.io.PrintStream;

public class InlineErrorFixSuggestion {
    private static final String REPEATED_CHAR = "~";

    public static void fix(String originalString, char unknownCharacter, PrintStream printStream, int lineNum) {
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
        printStream.println(padding + " | ");
        printStream.println("\b" + lineNum + " | " + originalString);
        printStream.println(padding + msg);
    }

    public static void fix(String originalString, String unknownCharacter, PrintStream printStream, int lineNum) {
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
        printStream.println(padding + " | ");
        printStream.println("\b" + lineNum + " | " + originalString);
        printStream.println(padding + msg);
    }
}
