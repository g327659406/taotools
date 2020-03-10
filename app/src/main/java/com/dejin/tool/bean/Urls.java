package com.dejin.tool.bean;

import org.litepal.annotation.Column;
import org.litepal.crud.LitePalSupport;

import java.io.Serializable;

public class Urls extends LitePalSupport implements Serializable {
    public long id;
    public String url = "";
    public String projectName = "";
    @Column(ignore = true)
    public boolean isAvaliable;
    @Column(ignore = true)
    public boolean isRunning;

}
