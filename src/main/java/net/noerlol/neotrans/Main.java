package net.noerlol.neotrans;

import com.formdev.flatlaf.FlatDarculaLaf;
import net.noerlol.cliargs.CommandLineArgumentsHandler;
import net.noerlol.cliargs.Option;
import net.noerlol.neotrans.build.BuildScriptCreator;
import net.noerlol.neotrans.gui.NeoGUI;
import net.noerlol.neotrans.project.ProjectConfig;
import net.noerlol.neotrans.project.ProjectCreator;
import net.noerlol.neotrans.compilation.Tokenizer;
import net.noerlol.neotrans.compilation.Transpiler;
import net.noerlol.neotrans.utils.*;
import net.noerlol.util.ArrayJoiner;
import net.noerlol.util.ResourceFetcher;

import javax.swing.*;
import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Main {
    public static CommandLineArgumentsHandler args;
    private static final ProjectConfig config = new ProjectConfig();

    private Main(String[] cliArgs) throws Exception {
        args = new CommandLineArgumentsHandler(cliArgs, "-Dstd", "-Cstd", "-Gpath");
        boolean showDebugHelp = false;
        if (args.isEnabled("DEBUG--help", false) || Version.RELEASE_TYPE.equals("DEV")) {
            showDebugHelp = true;
        }
        if (args.isEnabled("d", true) || args.isEnabled("debug", false)) {
            if (!Version.RELEASE_TYPE.equals("DEV")) {
                System.out.println("not a debug build");
            } else {
                StoredPrintStream storedPrintStream = StoredPrintStream.get(NullPrintStream.getNull());
                storedPrintStream.println("NASDAQ");

                for (Character c : storedPrintStream.getMessagesPrinted()) {
                    System.out.print(c);
                }
            }
            System.exit(0);
        } if (args.isEnabled("S", true) || args.isEnabled("setup", false)) {
            ProjectCreator.init();
            config.writeConfig(new File("project.yml"));
            LibraryDownloader libraryDownloader;
            if (config.getString("library_mirror").equals("OFFICIAL")) {
                libraryDownloader = new LibraryDownloader(Mirror.OFFICIAL_MIRROR);
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
            if (!config.getString("version").equals(Version.VERSION)) {
                System.err.println("Your config is outdated! Consider running neotrans [--update-config | -u]");
            }
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
            ProcessBuilder pb = new ProcessBuilder("java", "-cp", "lib" + File.separator + "libjda" + Version.libjda_VERSION + ".jar" + PlatformSpecific.CLASSPATH_SEPARATOR + "lib" + File.separator + "stdlib" + Version.STDLIB_VERSION + ".jar" + PlatformSpecific.CLASSPATH_SEPARATOR + "build" + File.separator + "compiled.jar",  "src.Main");
            Process p = pb.start();
            {
                BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
                String line;
                while ((line = reader.readLine()) != null) {
                    System.out.println(line);
                }
            }
            new Thread(() -> {
                try{
                    BufferedReader reader = new BufferedReader(new InputStreamReader(p.getErrorStream()));
                    String line;
                    while ((line = reader.readLine()) != null) {
                        System.err.println(line);
                    }
                } catch (IOException e) { throw new RuntimeException(e); }
            }).start();
        } if (args.isEnabled("D", true) || args.isEnabled("download-libraries", false)) {
            Mirror mirror;
            if (Files.exists(Paths.get("project.yml"))) {
                config.loadConfig(new File("project.yml"));
                if (!config.getString("version").equals(Version.VERSION)) {
                    System.err.println("Your config is outdated! Consider running neotrans [--update-config | -u]");
                }
                String mirrorUrl = config.getString("library_mirror");
                if (mirrorUrl.equals("OFFICIAL")) {
                    mirror = Mirror.OFFICIAL_MIRROR;
                } else {
                    mirror = new Mirror(new URL(mirrorUrl));
                }
            } else {
                mirror = Mirror.OFFICIAL_MIRROR;
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
            msg = msg.replace("%version%", Version.VERSION).replace("%os%", System.getProperty("os.name")).replace("%type%", Version.RELEASE_TYPE).replace("%authors%", Version.AUTHORS).replace("%languagelevel%", Version.STDLIB_VERSION);
            System.out.println(msg);
            System.exit(0);
        } if (args.isEnabled("gui", false) || args.isEnabled("g", true)) {
            if (Version.RELEASE_TYPE.equals("RELEASE")) {
                if (!args.isEnabled("DEBUG--override", false)) {
                    System.err.println("error: using unfinished options in release mode\nadd option --DEBUG--override to override");
                    System.exit(1);
                }
            }
            UIManager.setLookAndFeel(new FlatDarculaLaf());
            NeoGUI gui = new NeoGUI();
        } if (args.isEnabled("update-config", false) || args.isEnabled("u", true)) {
            config.loadConfig(new File("project.yml"));
            config.updateConfig();
        }

        if (args.isEnabled("create-build-script", false) || args.isEnabled("B", true)) {
            System.out.println("creating build script");
            BuildScriptCreator.create(args.getOptions().toArray(new Option[0]));
            System.out.println("done");
        }

        if (args.isEmpty()) {
            System.out.println("No options provided, use [--help | -h] to get more info");
            System.exit(1);
        }
    }

    public static void main(String[] args) throws Exception {
        new Main(args);
    }
}
