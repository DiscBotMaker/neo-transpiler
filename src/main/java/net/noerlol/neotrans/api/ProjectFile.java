package net.noerlol.neotrans.api;

import net.noerlol.neotrans.project.ProjectConfig;
import net.noerlol.neotrans.compilation.Tokenizer;
import net.noerlol.neotrans.compilation.Transpiler;
import net.noerlol.neotrans.utils.NullOutputStream;
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
        ArrayList<Integer> bytes;
        StoredPrintStream storedPrintStream = new StoredPrintStream(NullOutputStream.getNull());
        tokenizer.parseLine(rawCode, storedPrintStream);
        bytes = storedPrintStream.getMessagesPrinted();
        if (bytes.isEmpty()) {
            return new StatementValidity(true);
        } else {
            byte[] bytes1 = new byte[bytes.size()];
            for (int i = 0; i < bytes1.length; i++) {
                bytes1[i] = bytes.get(i).byteValue();
            }
            String statement = Arrays.toString(bytes1);
            return new StatementValidity(statement);
        }
    }

    public void run() throws IOException {
        TokenizedCode tokenizedCode = tokenizer.parseEnd(NullOutputStream.getNull());
        TranspiledCode transpiledCode = Transpiler.transpile(tokenizedCode);
        transpiledCode.run();
    }
}
