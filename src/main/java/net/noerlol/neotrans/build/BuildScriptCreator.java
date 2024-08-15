package net.noerlol.neotrans.build;

import net.noerlol.cliargs.Option;
import net.noerlol.neotrans.utils.OperatingSystem;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;

public class BuildScriptCreator {
    public static void create(Option[] options) throws Exception {
        if (OperatingSystem.isUnix()) {
            String jarPath = BuildScriptCreator.class.getProtectionDomain()
                    .getCodeSource()
                    .getLocation()
                    .toURI()
                    .getPath();
            String strOptions = "";
            for (Option option : options) {
                String prefix;
                if (option.isShorthand()) {
                    prefix = "-";
                } else {
                    prefix = "--";
                }
                if (option.hasValue()) {
                    // if (!option.getOption().equals("B") || !option.getOption().equals("create-build-script")) {}
                    // ^^ always true
                    strOptions += prefix + option.getOption() + " " + option.getValue() + " ";
                } else {
                    if (!(option.getOption().equals("B") || option.getOption().equals("create-build-script"))) {
                        strOptions += prefix + option.getOption() + " ";
                    }
                }
            }
            strOptions = strOptions.trim();
            BufferedWriter writer = new BufferedWriter(new FileWriter("build.sh"));
            String write =
"""
java -jar %jarPath% %options%
""";
            write = write.replace("%jarPath%", jarPath).replace("%options%", strOptions);
            writer.write(write);
            writer.close();
            new ProcessBuilder("chmod", "+x", "./build.sh").start();
        } else {
            System.out.println("Build scripts not supported on Windows");
        }
    }
}
