package net.noerlol.neotrans.utils;

public class TranspiledCode {
    private String code;

    public TranspiledCode(String code, String imports) {
        this.code = imports + "public class ReplaceThisClassName__NEOTRANSLATER {" + code + "}";
    }

    public String getCode() {
        if (code.contains("ReplaceThisClassName__NEOTRANSLATER")) {
            throw new IllegalArgumentException("class name was not replaced");
        }
        return this.code;
    }

    public void setClassName(String className) {
        this.code = this.code.replace("ReplaceThisClassName__NEOTRANSLATER", className);
    }
}
