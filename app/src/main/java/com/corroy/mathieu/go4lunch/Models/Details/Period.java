package com.corroy.mathieu.go4lunch.Models.Details;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

public class Period {
    @SerializedName("close")
    @Expose
    public Close close;
    @SerializedName("open")
    @Expose
    public Open open;

    public Close getClose() {
        return close;
    }

    public void setClose(Close close) {
        this.close = close;
    }

    public Open getOpen() {
        return open;
    }

    public void setOpen(Open open) {
        this.open = open;
    }
}
