package net.noerlol.neotrans.api.lsp;

public class StatementValidity {
    private final String errorMessage;
    private final boolean wasValid;

    public StatementValidity(String errorMessage) {
        this.errorMessage = errorMessage;
        this.wasValid = false;
    }

    public StatementValidity(boolean valid) {
        if (!valid) {
            throw new IllegalArgumentException("valid was false, when expected true");
        } else {
            this.wasValid = true;
        }
        this.errorMessage = "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValid() {
        return wasValid;
    }
}
