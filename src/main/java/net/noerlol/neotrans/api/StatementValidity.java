package net.noerlol.neotrans.api;

public class StatementValidity {
    private final String errorMessage;
    private final boolean wasValid;

    public StatementValidity(String errorMessage) {
        this.errorMessage = errorMessage;
        this.wasValid = false;
    }

    public StatementValidity() {
        this.wasValid = true;
        this.errorMessage = "";
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public boolean isValid() {
        return wasValid;
    }
}
