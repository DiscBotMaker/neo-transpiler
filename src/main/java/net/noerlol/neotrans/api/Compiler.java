package net.noerlol.neotrans.api;

import net.noerlol.neotrans.compilation.Tokenizer;
import net.noerlol.neotrans.compilation.Transpiler;
import net.noerlol.neotrans.utils.TokenizedCode;
import net.noerlol.neotrans.utils.TranspiledCode;

public class Compiler {
    private Compiler() {} // bad boy

    @AbstractedMethod
    public static TranspiledCode compile(Tokenizer tokenizer, String[] lines) {
        TokenizedCode tokenizedCode = tokenizer.parse(lines);
        return Transpiler.transpile(tokenizedCode);
    }

    public static TokenizedCode tokenize(Tokenizer tokenizer, String[] lines) {
        return tokenizer.parse(lines);
    }

    public static TranspiledCode transpile(TokenizedCode tokenizedCode) {
        return Transpiler.transpile(tokenizedCode);
    }
}
