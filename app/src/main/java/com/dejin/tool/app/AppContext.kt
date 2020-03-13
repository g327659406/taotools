package com.dejin.tool.app

import android.app.Application
import android.content.Context
import android.graphics.Color
import com.blankj.utilcode.util.ConvertUtils
import com.blankj.utilcode.util.ToastUtils
import com.blankj.utilcode.util.Utils
import com.dejin.tool.bean.Project
import com.dejin.tool.bean.Urls
import org.json.JSONArray
import org.litepal.LitePal


class AppContext : Application() {

    companion object {
        lateinit var INSTANCE: Context
    }

    override fun onCreate() {
        super.onCreate()
        INSTANCE = this
        initConfig()
    }

    private fun initConfig() {
        Utils.init(this)
        ToastUtils.setMsgColor(Color.WHITE)
        ToastUtils.setBgColor(Color.parseColor("#606060"))
        LitePal.initialize(this)
        if (LitePal.isExist(Project::class.java).not()) {
            val array = JSONArray(ConvertUtils.inputStream2String(assets.open("config"), "UTF8"))
            for (i in 0 until array.length()) {
                val obj = array.getJSONObject(i)
                val project = Project()
                project.projectName = obj.getString("projectName")
                project.signKey = obj.getString("signKey")
                project.urls = obj.getString("urls")
                project.save()
//                val urlArray = obj.getJSONArray("urls")
//                for (j in 0 until urlArray.length()) {
//                    val url = Urls()
//                    url.projectName = project.projectName
//                    url.url = urlArray.getString(j)
//                    url.save()
//                }

            }

        }


    }


}