package com.rarToZip.RarToZip.DataClasses;

import java.io.ByteArrayOutputStream;

public class UnrarData {
    private ByteArrayOutputStream outputStream;
    private String orginal_name;

    public UnrarData(ByteArrayOutputStream outputStream, String orginal_name) {
        this.outputStream = outputStream;
        this.orginal_name = orginal_name;
    }

    public ByteArrayOutputStream getOutputStream() {
        return outputStream;
    }

    public String getOrginal_name() {
        return orginal_name;
    }
}
