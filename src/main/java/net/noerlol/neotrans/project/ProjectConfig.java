package net.noerlol.neotrans.project;

import net.noerlol.neotrans.utils.Mirror;
import net.noerlol.neotrans.utils.Version;
import net.noerlol.util.ArrayJoiner;
import net.noerlol.util.Config;
import net.noerlol.util.ResourceFetcher;

import java.io.*;

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
}
