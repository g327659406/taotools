package com.dejin.tool.bean

import org.litepal.crud.LitePalSupport
import java.io.Serializable

class Project : LitePalSupport(), Serializable {
    var id = 0L
    var projectName = ""
    var signKey = ""
    var urls = ""
    var appId = ""
    var privateKey = ""
}