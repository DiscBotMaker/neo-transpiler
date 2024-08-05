package net.noerlol.neotrans;

import net.noerlol.cliargs.CommandLineArgumentsHandler;
import net.noerlol.neotrans.project.ProjectConfig;
import net.noerlol.neotrans.project.ProjectCreator;
import net.noerlol.neotrans.transpiler.Tokenizer;
import net.noerlol.neotrans.transpiler.Transpiler;
import net.noerlol.neotrans.utils.*;
import net.noerlol.util.ArrayJoiner;
import net.noerlol.util.ResourceFetcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
// Todo: Make a GUI for it

public class Main {
    public static CommandLineArgumentsHandler args;
    private static final ProjectConfig config = new ProjectConfig();

    private Main(String[] cliArgs) throws Exception {
        args = new CommandLineArgumentsHandler(cliArgs, "-Dstd", "-Cstd");
        boolean showDebugHelp = false;
        if (args.isEnabled("DEBUG--help", false) || Version.RELEASE_TYPE.equals("DEBUG")) {
            showDebugHelp = true;
        }
        if (args.isEnabled("d", true) || args.isEnabled("debug", false)) {
            {
                System.out.println("not a debug build");
            }
            System.exit(0);
        } if (args.isEnabled("S", true) || args.isEnabled("setup", false)) {
            ProjectCreator.init();
            config.writeConfig(new File("project.yml"));
            LibraryDownloader libraryDownloader;
            if (config.getString("library_mirror").equals("OFFICIAL")) {
                libraryDownloader = new LibraryDownloader(Mirror.officialMirror);
            } else {
                libraryDownloader = new LibraryDownloader(new Mirror(new URL(config.getString("library_mirror"))));
            }
            libraryDownloader.download();
            System.out.println("setup finished");
        } if (args.isEnabled("c", true) || args.isEnabled("clean", false)) {
            Path buildCache = Paths.get(".build-cache");
            if (Files.exists(buildCache)) {
                new FileUtils().deleteRecursive(buildCache);
                Files.createDirectory(buildCache);
            }
        } if (args.isEnabled("b", true) || args.isEnabled("build", false)) {
            config.loadConfig(new File("project.yml"));
            File sourceDirectory = new File(config.getString("project.source_directory"));
            ArrayList<String> compilationFiles = new FileUtils().R_ListFiles(sourceDirectory);

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
            TokenizedCode tokenizedCode = tokenizer.parse(lines.toArray(new String[0]));
            TranspiledCode transpiledCode = Transpiler.transpile(tokenizedCode);

            if (args.isEnabled("DEBUG--dump-code", false)) {
                System.out.println("(dbg) transpiled code: " + transpiledCode.getCode());
                System.out.println("(dbg) tokenized code: start [" + transpiledCode.getCode() + "] end");
            }

            // Running
            transpiledCode.run();
        } if (args.isEnabled("r", true) || args.isEnabled("run", false)) {
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "lib" + File.separator + "libjda" + Version.libjda_VERSION + ".jar" + ":lib" + File.separator + "libstd" + Version.libstd_VERSION + ".jar" + ":build" + File.separator + "compiled.jar",  "src.Main");
            Process p = pb.start();
            try (BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
        } if (args.isEnabled("D", true) || args.isEnabled("download-libraries", false)) {
            Mirror mirror;
            if (Files.exists(Paths.get("project.yml"))) {
                config.loadConfig(new File("project.yml"));
                String mirrorUrl = config.getString("library_mirror");
                if (mirrorUrl.equals("OFFICIAL")) {
                    mirror = Mirror.officialMirror;
                } else {
                    mirror = new Mirror(new URL(mirrorUrl));
                }
            } else {
                mirror = Mirror.officialMirror;
                System.out.println("using default mirror");
            }
            new LibraryDownloader(mirror).download();
        } if (args.isEnabled("h", true) || args.isEnabled("help", false)) {
            ResourceFetcher fetcher = new ResourceFetcher(Main.class, "help.txt");
            String[] lines = fetcher.getFileText();
            ArrayJoiner joiner = new ArrayJoiner(lines, '\n');
            System.out.println(joiner.join());

            if (showDebugHelp) {
                fetcher = new ResourceFetcher(Main.class, "debugopt.txt");
                lines = fetcher.getFileText();
                joiner = new ArrayJoiner(lines, '\n');
                System.out.println(joiner.join());
            }
            System.exit(0);
        } if (args.isEnabled("v", true) || args.isEnabled("version", false)) {
            String msg;
            if (args.isEnabled("s", true) || args.isEnabled("simple", false)) {
                msg =
"""
                        >>>  neotrans  <<<
                        version: %version%
os: %os%
release type: %type%
authors: %authors%
language level: %languagelevel%
""";
            } else {
                msg =
"""
                                 __              \s
               ____  ___  ____  / /__________ _____  _____
              / __ \\/ _ \\/ __ \\/ __/ ___/ __ `/ __ \\/ ___/
             / / / /  __/ /_/ / /_/ /  / /_/ / / / (__  )\s
            /_/ /_/\\___/\\____/\\__/_/   \\__,_/_/ /_/____/ \s
                        version: %version%
os: %os%
release type: %type%
authors: %authors%
language level: %languagelevel%
""";
            }
            msg = msg.replace("%version%", Version.VERSION).replace("%os%", System.getProperty("os.name")).replace("%type%", Version.RELEASE_TYPE).replace("%authors%", Version.AUTHORS).replace("%languagelevel%", Version.STDVERSION);
            System.out.println(msg);
            System.exit(0);
        }

        if (args.isEmpty()) {
            System.out.println("No options provided, use [--help | -h] to get more info");System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        new Main(args);
    }
}
