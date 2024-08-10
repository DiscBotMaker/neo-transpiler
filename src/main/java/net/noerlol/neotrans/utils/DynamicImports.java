package net.noerlol.neotrans.utils;

import java.util.ArrayList;
import java.util.Scanner;

public class DynamicImports {
    private static final String stdlibBotPackageName = "dbm.bot";
    public static Import[] getImportsFromCode(String javaCode) {
        ArrayList<Import> imports = new ArrayList<>();
        if (javaCode.contains("Scanner")) {
            imports.add(new Import(Scanner.class));
        } if (javaCode.contains("LibraryVersion")) {
            imports.add(new Import("LibraryVersion", stdlibBotPackageName));
        } if (javaCode.contains("Bot")) {
            imports.add(new Import("Bot", stdlibBotPackageName));
            imports.add(new Import("BotProvider", stdlibBotPackageName));
        } if (javaCode.contains("Context")) {
            imports.add(new Import("Context", stdlibBotPackageName));
        } if (javaCode.contains("Command")) {
            imports.add(new Import("Command", stdlibBotPackageName));
        }
        return imports.toArray(new Import[imports.size()]);
    }
}
