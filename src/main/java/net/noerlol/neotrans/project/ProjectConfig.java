package net.noerlol.neotrans.project;

import net.noerlol.neotrans.utils.Mirror;
import net.noerlol.neotrans.utils.Version;
import net.noerlol.util.ArrayJoiner;
import net.noerlol.util.Config;
import net.noerlol.util.ResourceFetcher;

import java.io.*;
import java.util.ArrayList;

public class ProjectConfig extends Config {
    @Override
    public void writeConfig(File file) {
        try {
            BufferedWriter writer = new BufferedWriter(new FileWriter(file));
            ResourceFetcher fetcher = new ResourceFetcher(ProjectConfig.class, "project.yml");
            String defaultConfig = new ArrayJoiner(fetcher.getFileText(), System.lineSeparator()).join();
            defaultConfig = defaultConfig.replace("%version%", Version.VERSION).replace("%officialMirror%", Mirror.OFFICIAL_MIRROR.getUrl().toString());
            writer.write(defaultConfig);
            writer.close();
            loadConfig(file);
        } catch (IOException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
    }

    public void updateConfig() throws IOException {
        String[] oldConfig = getLinesOfConfig();
        writeOldConfig(oldConfig);

        int index = 1;
        for (int i = 0; i < oldConfig.length; i++) {
            if (oldConfig[i].startsWith("version:")) {
                index = i;
                break;
            }
        }
        oldConfig[index] = "version: \"%version%\"".replace("%version%", Version.VERSION);

        BufferedWriter writer = new BufferedWriter(new FileWriter("project.yml"));
        for (String l : oldConfig) {
            writer.write(l + "\n");
        }
        writer.close();

        System.out.println("updated");
    }

    private String[] getLinesOfConfig() throws IOException {
        ArrayList<String> lines = new ArrayList<>();
        BufferedReader reader = new BufferedReader(new FileReader("project.yml"));
        String line;
        while ((line = reader.readLine()) != null) {
            lines.add(line);
        }
        reader.close();
        return lines.toArray(new String[0]);
    }

    private void writeOldConfig(String[] lines) throws IOException {
        BufferedWriter writer = new BufferedWriter(new FileWriter("project.yml.old"));
        for (String l : lines) {
            writer.write(l + System.lineSeparator());
        }
        writer.close();
    }
}
