package net.noerlol.neotrans.compilation;

import net.noerlol.neotrans.utils.Import;

import java.util.ArrayList;
import java.util.Scanner;

public class DynamicImports {
    private static final String bot = "dbm.bot";
    private static final String io = "dbm.io";
    private static final String net = "dbm.net";
    public static Import[] getImportsFromCode(String javaCode) {
        ArrayList<Import> imports = new ArrayList<>();
        if (javaCode.contains("Scanner")) {
            imports.add(new Import(Scanner.class));
        } if (javaCode.contains("LibraryVersion")) {
            imports.add(new Import("LibraryVersion", bot));
        } if (javaCode.contains("Bot")) {
            imports.add(new Import("Bot", bot));
            imports.add(new Import("BotProvider", bot));
        } if (javaCode.contains("Context")) {
            imports.add(new Import("Context", bot));
        } if (javaCode.contains("Command")) {
            imports.add(new Import("Command", bot));
        } if (javaCode.contains("System")) {
            imports.add(new Import("System", io));
        }
        return imports.toArray(new Import[imports.size()]);
    }
}
