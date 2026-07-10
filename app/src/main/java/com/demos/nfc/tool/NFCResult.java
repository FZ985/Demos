package com.demos.nfc.tool;


import java.io.Serializable;

/**
 * by DAD FZ
 * 2025/11/18
 * desc：
 **/
public class NFCResult implements Serializable {

    private final boolean success;

    private final String message;

    private ParsedNdef parsed;

    public NFCResult(boolean success, String message) {
        this.success = success;
        this.message = message;
    }

    public NFCResult(boolean success) {
        this.success = success;
        this.message = "";
    }

    public ParsedNdef getParsed() {
        return parsed;
    }

    public void setParsed(ParsedNdef parsed) {
        this.parsed = parsed;
    }

    public boolean isSuccess() {
        return success;
    }

    public String getMessage() {
        if (message == null) {
            return "";
        }
        return message;
    }
}
