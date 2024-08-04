package net.noerlol.neotrans.utils;

public class TokenizedCode {
    private final String code;
    private int TAB_LENGTH = 4;

    public TokenizedCode(String code, int TAB_LENGTH) {
        this.code = code;
        this.TAB_LENGTH = TAB_LENGTH;
    }

    public String getCode() {
        return code;
    }

    public int getTabLength() {
        return TAB_LENGTH;
    }

    public String getPackageName() {
        return this.code
                        .split("\n")[0]
                        .replace("export ", "")
                        .replace(';', ' ')
                        .trim();
    }
}
