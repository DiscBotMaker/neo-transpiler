package net.noerlol.neotrans.utils;

import org.yaml.snakeyaml.util.EnumUtils;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;

public final class LibraryDownloader {
    private final URL url;
    private URL libDbmUrl;
    private URL libJdaUrl;
    public static final int BUFFER_SIZE = 8192;
    public LibraryDownloader(Mirror mirror) {
        this.url = mirror.getUrl();
    }

    public void download() throws Exception {
        System.out.println("fetching mirror " + url.toString());
        initUrls();
        System.out.println("mirror fetched");
        System.out.print("download libdbm" + Version.libdbm_VERSION + " ");
        downloadLib(Library.LIBDBM);
        System.out.println("done");
        System.out.print("download libjda" + Version.libjda_VERSION + " ");
        downloadLib(Library.JDA);
        System.out.println("done");
    }

    private void initUrls() throws Exception {
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");

        if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
            String line = reader.readLine();
            reader.close();

            String[] lines = line.split("\\|");

            for (String inp : lines) {
                if (inp.startsWith("JDA")) {
                    libJdaUrl = new URL(inp.split("=")[1]);
                } else if (inp.startsWith("LIBDBM")) {
                    libDbmUrl = new URL(inp.split("=")[1]);
                } else {
                    System.out.println("mirror maybe corrupted? found strange line: " + inp);
                }
            }
        }
        conn.disconnect();
    }

    private void downloadLib(Library library) throws Exception {
        HttpURLConnection conn = null;
        Path libDirPath = Path.of("lib");
        if (!Files.exists(libDirPath)) {
            Files.createDirectories(libDirPath);
        }
        if (library.equals(Library.JDA)) {
            conn = (HttpURLConnection) libJdaUrl.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream());
                FileOutputStream writer = new FileOutputStream("lib" + File.separator + "libjda" + Version.libjda_VERSION + ".jar");
                byte[] buf = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                while ((bytesRead = bufferedInputStream.read(buf)) != -1) {
                    writer.write(buf, 0, bytesRead);
                }
                writer.close();
                bufferedInputStream.close();
            }
        } else if (library.equals(Library.LIBDBM)) {
            conn = (HttpURLConnection) libDbmUrl.openConnection();
            conn.setRequestMethod("GET");

            if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                BufferedInputStream bufferedInputStream = new BufferedInputStream(conn.getInputStream());
                FileOutputStream writer = new FileOutputStream("lib" + File.separator + "libdbm" + Version.libdbm_VERSION + ".jar");
                byte[] buf = new byte[BUFFER_SIZE];
                int bytesRead = 0;
                while ((bytesRead = bufferedInputStream.read(buf)) != -1) {
                    writer.write(buf, 0, bytesRead);
                }
                writer.close();
                bufferedInputStream.close();
            }
        }
        conn.disconnect();
    }
}
