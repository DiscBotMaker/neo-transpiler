package net.noerlol.neotrans.compilation.dynamic;

import java.util.ArrayList;
import java.util.Scanner;

public class DynamicImports {
    private static final String bot = "dbm.bot";
    private static final String io = "dbm.io";
    private static final String net = "dbm.net";
    public static JavaImport[] getImportsFromCode(String javaCode) {
        ArrayList<JavaImport> imports = new ArrayList<>();
        if (javaCode.contains("Scanner")) {
            imports.add(new JavaImport(Scanner.class));
        } if (javaCode.contains("LibraryVersion")) {
            imports.add(new JavaImport("LibraryVersion", bot));
        } if (javaCode.contains("Bot")) {
            imports.add(new JavaImport("Bot", bot));
            imports.add(new JavaImport("BotProvider", bot));
        } if (javaCode.contains("Context")) {
            imports.add(new JavaImport("Context", bot));
        } if (javaCode.contains("Command")) {
            imports.add(new JavaImport("Command", bot));
        } if (javaCode.contains("System")) {
            imports.add(new JavaImport("System", io));
        }
        return imports.toArray(new JavaImport[imports.size()]);
    }
}
