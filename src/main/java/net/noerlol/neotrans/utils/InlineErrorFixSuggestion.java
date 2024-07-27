package net.noerlol.neotrans.utils;

import java.io.PrintStream;

public class InlineErrorFixSuggestion {
    private static final String REPEATED_CHAR = "~";

    public static void fix(String originalString, char unknownCharacter, PrintStream printStream) {
        printStream.println(originalString);
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
        printStream.println(msg);
    }

    public static void fix(String originalString, String unknownCharacter, PrintStream printStream) {
        printStream.println(originalString);
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
        printStream.println(msg);
    }
}
