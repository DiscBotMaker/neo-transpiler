package net.noerlol.neotrans.api.lsp;

import net.noerlol.neotrans.api.ProceduralMethod;
import net.noerlol.neotrans.project.ProjectConfig;

import java.nio.file.Path;

public class LSPUtils {
    private static ProjectConfig config;
    public static APIProject getProject(ProjectConfig config) {
        LSPUtils.config = config;
        return new APIProject(config);
    }

    public static ProjectFile getProjectFile(Path path) {
        return new ProjectFile(path, LSPUtils.config);
    }

    @ProceduralMethod
    public static StatementValidity isStatementValid(ProjectFile file, String line) {
        return file.addCode(line);
    }
}
