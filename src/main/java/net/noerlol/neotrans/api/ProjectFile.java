package net.noerlol.neotrans.api;

import net.noerlol.neotrans.project.ProjectConfig;
import net.noerlol.neotrans.compilation.Tokenizer;
import net.noerlol.neotrans.compilation.Transpiler;
import net.noerlol.neotrans.utils.NullPrintStream;
import net.noerlol.neotrans.utils.StoredPrintStream;
import net.noerlol.neotrans.utils.TokenizedCode;
import net.noerlol.neotrans.utils.TranspiledCode;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;

public class ProjectFile {
    private final Path path;
    private Tokenizer tokenizer;
    private final ProjectConfig config;
    public ProjectFile(Path path, ProjectConfig config) {
        this.path = path;
        this.config = config;
        this.tokenizer = new Tokenizer(config.getInteger("project.tab_length"), path.getFileName().toString());
    }

    public StatementValidity addCode(String rawCode) {
        ArrayList<Character> bytes;
        StoredPrintStream storedPrintStream = new StoredPrintStream(NullPrintStream.getNull());
        tokenizer.parseLine(rawCode, storedPrintStream);
        bytes = storedPrintStream.getMessagesPrinted();
        if (bytes.isEmpty()) {
            return new StatementValidity();
        } else {
            String statement = Arrays.toString(bytes.toArray(new Character[0]));
            return new StatementValidity(statement);
        }
    }

    public void run() throws IOException {
        TokenizedCode tokenizedCode = tokenizer.parseEnd(NullPrintStream.getNull());
        TranspiledCode transpiledCode = Transpiler.transpile(tokenizedCode);
        transpiledCode.run();
    }
}
