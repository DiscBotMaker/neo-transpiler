package net.noerlol.neotrans.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;

public class DynamicImports {
    private static final String libdbmPackageName = "dbm.bot";
    public static Import[] getImportsFromCode(String javaCode) {
        ArrayList<Import> imports = new ArrayList<>();
        if (javaCode.contains("Scanner")) {
            imports.add(new Import(Scanner.class));
        } if (javaCode.contains("LibraryVersion")) {
            imports.add(new Import("LibraryVersion", libdbmPackageName));
        } if (javaCode.contains("Bot")) {
            imports.add(new Import("Bot", libdbmPackageName));
            imports.add(new Import("BotProvider", libdbmPackageName));
        } if (javaCode.contains("Context")) {
            imports.add(new Import("Context", libdbmPackageName));
        } if (javaCode.contains("Command")) {
            imports.add(new Import("Command", libdbmPackageName));
        }
        return imports.toArray(new Import[imports.size()]);
    }
}
