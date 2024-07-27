package net.noerlol.neotrans;

import net.noerlol.cliargs.CommandLineArgumentsHandler;
import net.noerlol.neotrans.project.ProjectConfig;
import net.noerlol.neotrans.project.ProjectCreator;
import net.noerlol.neotrans.transpiler.Tokenizer;
import net.noerlol.neotrans.transpiler.Transpiler;
import net.noerlol.neotrans.utils.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.sql.XAConnection;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    // LOGGER
    public static final Logger LOGGER = LoggerFactory.getLogger(Main.class);
    private static CommandLineArgumentsHandler cliArgs;
    private static final ProjectConfig config = new ProjectConfig();

    private Main(ArrayList<String> args) throws Exception {
        cliArgs = new CommandLineArgumentsHandler(args);

        config.loadConfig(new File("project.yml"));

        if (cliArgs.isEnabled("d", true) || cliArgs.isEnabled("debug", false)) {
            {
                System.out.println("not a debug build");
            }
            System.exit(0);
        } if (cliArgs.isEnabled("S", true) || cliArgs.isEnabled("setup", false)) {
            ProjectCreator.init();
            config.writeConfig(new File("project.yml"));
            LibraryDownloader libraryDownloader;
            if (config.getString("library_mirror").equals("OFFICIAL")) {
                libraryDownloader = new LibraryDownloader(Mirror.officialMirror);
            } else {
                libraryDownloader = new LibraryDownloader(new Mirror(new URL(config.getString("library_mirror"))));
            }
            libraryDownloader.download(); // Todo: using \b and stuff library downloaderp rogress bar stuff
            System.out.println("setup finished");
            System.exit(0);
        } if (cliArgs.isEnabled("b", true) || cliArgs.isEnabled("build", false)) {
            File sourceDirectory = new File(config.getString("project.source_directory"));
            ArrayList<String> compilationFiles = new FileUtils().listFiles(sourceDirectory);

            if (!compilationFiles.contains(config.getString("project.source_directory") + File.separator + "main.dbm")) {
                System.err.println("no main.dbm file found in " + config.getString("project.source_directory") + File.separator);
            }

            BufferedReader bufferedReader = new BufferedReader(new FileReader(new File(config.getString("project.source_directory") + File.separator + "main.dbm").getAbsolutePath()));
            String line;
            ArrayList<String> lines = new ArrayList<>();
            while ((line = bufferedReader.readLine()) != null) {
                lines.add(line);
            }
            bufferedReader.close();

            // Tokenizing and Compiling
            Tokenizer tokenizer = new Tokenizer(config.getInteger("project.tab_length"), "main.dbm");
            TokenizedCode tokenizedCode = tokenizer.parse(lines);
            TranspiledCode transpiledCode = Transpiler.transpile(tokenizedCode);

            // Writing
            TimeSignature uid = ClassWriter.write(transpiledCode, "main.dbm", tokenizedCode.getPackageName());
            String path = ".build-cache" + File.separator + uid.getTime();
            System.exit(0);
        } if (cliArgs.isEnabled("r", true) || cliArgs.isEnabled("run", false)) {
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "lib" + File.separator + "libjda" + Version.libjda_VERSION + ".jar" + ":lib" + File.separator + "libdbm" + Version.libdbm_VERSION + ".jar" + ":build" + File.separator + "compiled.jar",  "src.Main");
            Process p = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            System.exit(0);
        } if (cliArgs.isEnabled("h", true) || cliArgs.isEnabled("help", false)) {
            System.out.println(ClassResourceFetcher.readFromFile("help.txt", Main.class));
            System.exit(0);
        } if (cliArgs.isEnabled("v", true) || cliArgs.isEnabled("version", false)) {
            System.out.println(Version.VERSION + " on " + System.getProperty("os.name"));
            System.out.println("language level: " + Version.STDVERSION);
            System.exit(0);
        }

        if (cliArgs.isEmpty()) {
            System.out.println("No options provided, use [--help | -h] to get more info");
            System.exit(1);
        }
        // If execution continued till here, then an unrecognized option has occurred
        System.out.println("Unrecognized option found, use [--help | -h] to get more info");
        System.exit(1);
    }

    public static void main(String[] args) throws Exception {
        new Main(new ArrayList<>(Arrays.asList(args)));
    }
}