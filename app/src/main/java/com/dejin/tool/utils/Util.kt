package com.dejin.tool.utils

import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response

object Util {
    fun startTestUrl(pUrl: String): Boolean {
        println("开始测试$pUrl")
        var url = "$pUrl/appT.html"
        val request = Request.Builder().get().url(url).build()
        var response: Response? = null
        try {
            response = OkHttpClient().newCall(request).execute()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            response?.close()
        }
        return false

    }

}