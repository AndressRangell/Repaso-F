package com.flota.tools;

import com.pos.device.printer.Printer;

public class PaperStatus {

    static PaperStatus paperStatus;
    Printer printer = Printer.getInstance();
    private int ret;

    public static PaperStatus getInstance() {
        if (paperStatus == null) {
            paperStatus = new PaperStatus();
        }
        return paperStatus;
    }

    public int getRet() {
        ret = printer.getStatus();
        return ret;
    }

    public void setRet(int ret) {
        this.ret = ret;
    }

}
