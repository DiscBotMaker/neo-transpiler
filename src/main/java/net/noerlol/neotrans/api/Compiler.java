package net.noerlol.neotrans.api;

import net.noerlol.neotrans.compilation.Tokenizer;
import net.noerlol.neotrans.compilation.Transpiler;
import net.noerlol.neotrans.utils.TokenizedCode;
import net.noerlol.neotrans.utils.TranspiledCode;

public class Compiler {
    private Compiler() {} // bad boy

    /**
     * Compiles to transpiled code
     * @param tokenizer Tokenizer to use for compilation
     * @param lines Lines of code
     * @return
     */
    @AbstractedMethod
    public static TranspiledCode compile(Tokenizer tokenizer, String[] lines) {
        TokenizedCode tokenizedCode = tokenizer.parse(lines);
        return Transpiler.transpile(tokenizedCode);
    }

    /**
     * Compiles to transpiled code
     * @param lines Lines of code
     * @param fileName File name of the file
     * @return Transpiled Code
     */
    @AbstractedMethod
    public static TranspiledCode compile(String fileName, String[] lines) {
        TokenizedCode tokenizedCode = new Tokenizer(4, fileName).parse(lines);
        return Transpiler.transpile(tokenizedCode);
    }

    /**
     * Compiles to tokenized code
     * @param tokenizer Tokenizer to use
     * @param lines Lines of code
     * @return Tokenized Code
     */
    public static TokenizedCode tokenize(Tokenizer tokenizer, String[] lines) {
        return tokenizer.parse(lines);
    }

    /**
     * Compiles to tokenized code
     * @param fileName File name of the file
     * @param lines Lines of code
     * @return Tokenized Code
     */
    public static TokenizedCode tokenize(String fileName, String[] lines) {
        return new Tokenizer(4, fileName).parse(lines);
    }

    /**
     * Transpiles tokenized code to TranspiledCode
     * @param tokenizedCode Tokenized Code
     * @return Transpiled Code
     */
    public static TranspiledCode transpile(TokenizedCode tokenizedCode) {
        return Transpiler.transpile(tokenizedCode);
    }
}
