package net.noerlol.neotrans.utils;

import net.noerlol.neotrans.build.ClassWriter;

import java.io.IOException;

public class TranspiledCode {
    private String code;
    private final TokenizedCode tokenizedCode;

    public TranspiledCode(String code, String imports, TokenizedCode tokenizedCode) {
        this.code = imports + "public class ReplaceThisClassName__NEOTRANSLATER {" + code + "}";
        this.tokenizedCode = tokenizedCode;
    }

    public String getCode() {
        return this.code;
    }

    public void run() throws IOException {
        ClassWriter classWriter = new ClassWriter();
        classWriter.write(this, tokenizedCode.getPackageName().replace('.','/') + ".dbm", tokenizedCode.getPackageName());
    }

    public void setClassName(String className) {
        this.code = this.code.replace("ReplaceThisClassName__NEOTRANSLATER", className);
    }
}
