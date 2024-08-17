package net.noerlol.neotrans.utils;

import net.noerlol.neotrans.Main;
import net.noerlol.neotrans.api.APIOnly;
import org.jetbrains.annotations.NotNull;
import org.jline.terminal.Terminal;
import org.jline.terminal.TerminalBuilder;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public final class LibraryDownloader {
    private final URL url;
    Map<String, String> jda;
    Map<String, String> stdlib;
    public static final int BUFFER_SIZE = 8192;
    private static int WIDTH;
    // max percentage + 2 (magic number)
    public LibraryDownloader(Mirror mirror) {
        this.url = mirror.getUrl();
    }

    public void download() throws Exception {
        WIDTH = (TerminalBuilder.terminal().getWidth()) - 2;
        System.out.println("using mirror " + url.toString());
        initUrls();
        boolean doStdlibDownload = true;
        if (Main.args.isEnabled("Dstd", true)) {
            if (!stdlib.get("version").equals(Main.args.getOption("Dstd").getValue())) {
                System.err.println("error: invalid option or mirror");
                System.err.println("mirror version: " + stdlib.get("version"));
                System.err.println("option version: " + Main.args.getOption("Dstd").getValue());
                System.err.println("will skip...\n");
                doStdlibDownload = false;
            }
        }
        int r;
        if (doStdlibDownload) {
            System.out.print("download stdlib v" + stdlib.get("version"));
            int r2;
            r2 = downloadLib(Library.STDLIB);
            if (r2 != 99) {
                System.out.println("\b\b\b\b" + "100%]");
            }
        }
        System.out.print("download jda v" + jda.get("version"));
        r = downloadLib(Library.JDA);
        if (r != 99) {
            System.out.println("\b\b\b\b" + "100%]");
        }
    }

    private void initUrls() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            StringBuilder buffer = new StringBuilder();
            String line;
            while ((line = reader.readLine()) != null) {
                buffer.append(line).append(System.lineSeparator());
            }
            reader.close();
            MirrorReader mirrorReader = new MirrorReader();
            mirrorReader.loadFromString(buffer.toString());
            jda = mirrorReader.getLibraryInfo("jda");
            stdlib = mirrorReader.getLibraryInfo("stdlib");
        }
        conn.disconnect();
    }

    private int downloadLib(Library library) throws Exception {
        Path libDirPath = Path.of("lib");
        if (!Files.exists(libDirPath)) {
            Files.createDirectories(libDirPath);
        }
        URL libraryUrl = null;
        String fileName = "";
        if (library.equals(Library.JDA)) {
            libraryUrl = new URL(jda.get("url"));
            fileName = jda.get("filename").replace('/', File.separatorChar);
        }
        else if (library.equals(Library.STDLIB)) {
            libraryUrl = new URL(stdlib.get("url"));
            fileName = stdlib.get("filename").replace('/', File.separatorChar);
        }

        HttpURLConnection conn = (HttpURLConnection) libraryUrl.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            int contentLength = conn.getContentLength();
            BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream());
            FileOutputStream writer = new FileOutputStream(fileName);
            byte[] buf = new byte[BUFFER_SIZE];
            int bytesRead;
            long totalBytesRead = 0;
            int progress = 0;
            int written = 1; // modify to 0 if using yea
            System.out.println();
            System.out.print("[" + " ".repeat(WIDTH) + "]");
            while ((bytesRead = bufferedInputStream.read(buf)) != -1) {
                writer.write(buf, 0, bytesRead);
                totalBytesRead += bytesRead;
                int newProgress = (int) (totalBytesRead * WIDTH / contentLength);
                while ((progress < newProgress) && !(Main.args.isEnabled("s", true) || Main.args.isEnabled("simple", false))) {
                    if (progress != 0) {
//                        if (written < 10) {
//                            System.out.print("\b\b\b\b" + "0" + written + "%]");
//                        } else if (written > 99) {
//                            progress++;
//                            continue;
//                        } else {
//                            System.out.print("\b\b\b\b" + written + "%]");
//                        }
//                        written++;
                        System.out.print("\r" + "[" + "#".repeat(written) + " ".repeat(WIDTH - written) + "]");
                        written++;
                    }
                    progress++;
                }
            }
            System.out.println();

            writer.close();
            bufferedInputStream.close();
            conn.disconnect();
            return 99;
        } else {
            return 0;
        }
    }
}
