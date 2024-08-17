package net.noerlol.neotrans.utils;

import net.noerlol.neotrans.Main;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public final class UpdateChecker {
    public static void checkForUpdates(PrintStream printStream) throws IOException {
        URL updateCheckUrl = new URL("https://raw.githubusercontent.com/DiscBotMaker/important-links/main/latest.yaml");
        HttpURLConnection connection = (HttpURLConnection) updateCheckUrl.openConnection();
        if (connection.getResponseCode() == HttpURLConnection.HTTP_OK) {
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder data = new StringBuilder();
            while ((line = reader.readLine()) != null) {
                data.append(line).append(System.lineSeparator());
            }
            reader.close();

            Yaml yaml = new Yaml();
            Map<String, String> config = yaml.load(new StringReader(data.toString()));
            String versionString = config.get("version_string");
            String versionType = config.get("version_type");
            String nativeStdlib = config.get("native_stdlib");
            String nativeLibJda = config.get("native_libjda");
            String authors = config.get("authors");

            if (versionString.equals(Version.VERSION)) {
                printStream.println("you are up-to-date!");
            } else {
                printStream.println("you are not up-to-date, the latest version is %latestVersion% while you are on %version%".replace("%latestVersion%", versionString).replace("%version%", Version.VERSION));
            }

            if (Main.args.isEnabled("full", false) || Main.args.isEnabled("Uf", true)) {
                printStream.println("(latest) version: " + versionString);
                printStream.println("(latest) version_type: " + versionType);
                printStream.println("(latest) native stdlib: " + nativeStdlib);
                printStream.println("(latest) native libjda: " + nativeLibJda);
                printStream.println("(latest) authors: " + authors);
                printStream.println();
                printStream.println("(current) version: " + Version.VERSION);
                printStream.println("(current) version_type: " + Version.RELEASE_TYPE);
                printStream.println("(current) native stdlib: " + Version.STDLIB_VERSION);
                printStream.println("(current) native libjda: " + Version.libjda_VERSION);
                printStream.println("(current) authors: " + Version.AUTHORS);
            }
        } else {
            throw new RuntimeException("an error occurred while trying to access https://raw.githubusercontent.com/DiscBotMaker/important-links/main/latest.yaml");
        }
    }
}
