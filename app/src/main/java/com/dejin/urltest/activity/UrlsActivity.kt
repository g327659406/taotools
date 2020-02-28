package com.dejin.urltest.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dejin.urltest.R
import com.dejin.urltest.adapter.UrlsAdapter
import com.dejin.urltest.aes.EncodeUtils
import com.dejin.urltest.aes.Md5Utils
import com.dejin.urltest.bean.Urls
import kotlinx.android.synthetic.main.activity_main.*
import okhttp3.*
import org.json.JSONArray
import org.litepal.util.cipher.AESCrypt
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class UrlsActivity : AppCompatActivity() {
    var key = "U2FsdGVkX19X5NAwZN8I2oWsRPaYGJPy"
    private val okHttpClient = OkHttpClient()
    private val list = arrayListOf<Urls>()
    private lateinit var adapter: UrlsAdapter
    val fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(5)


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        adapter = UrlsAdapter(this, list)
        listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listView.adapter = adapter
        adapter.setOnItemClickListener { i ->
            val url = list[i]
            fixedThreadPool.submit {
                url.isRunning = true
                url.isAvaliable = false
                notifyDataSetChanged()
                val result = startTestUrl(url.url)
                url.isRunning = false
                url.isAvaliable = result
                notifyDataSetChanged()
            }
        }
        startTest()

    }

    fun startTest() {
        progress.visibility = View.VISIBLE
        listView.visibility = View.INVISIBLE
        getBaseUrls(successCallback = {
            list.clear()
            for (i in 0 until it.length()) {
                val url = Urls()
                url.url = it[i].toString()
                list.add(url)
            }
            //开始依次进行网络测试
            list.forEach {
                fixedThreadPool.submit {
                    it.isRunning = true
                    notifyDataSetChanged()
                    val result = startTestUrl(it.url)
                    it.isRunning = false
                    it.isAvaliable = result
                    notifyDataSetChanged()
                }
            }
        }, completeCallback = {
            runOnUiThread {
                progress.visibility = View.GONE
                listView.visibility = View.VISIBLE
            }
        })
    }

    fun notifyDataSetChanged() {
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }

    fun startTestUrl(pUrl: String): Boolean {
        val params = mutableMapOf<String, String>()
        params.put("typenum", "102")
        params.put("mod", "version")
        var url = "$pUrl/api/app.php?"
        val newParams = params.toSortedMap()
        var sign = ""
        newParams.forEach { (k, v) ->
            kotlin.run {
                val value = EncodeUtils.urlEncode(v)
                url += ("$k=$value&")
                sign += ("$k=$value")
            }
        }
        sign += ("secret=$key")
        sign = Md5Utils.MD5Encode(sign, "UTF-8", false)
        url += "sign=$sign"
        val request = Request.Builder().get().url(url).build()
        var response: Response? = null
        try {
            response = okHttpClient.newCall(request).execute()
            return true
        } catch (e: Exception) {

        } finally {
            response?.close()
        }
        return false

    }


    /**获取所有备用的url列表*/
    fun getBaseUrls(successCallback: (JSONArray) -> Unit = {}, completeCallback: () -> Unit = {}) {
//        okHttpClient.newCall(Request.Builder().get().url("https://domainlistname.oss-accelerate.aliyuncs.com").build())//老域名
        okHttpClient.newCall(Request.Builder().get().url("https://qianduo.oss-accelerate.aliyuncs.com").build())//新域名
            .enqueue(object : Callback {
                override fun onFailure(call: Call, e: IOException) {
                    completeCallback()
                }

                override fun onResponse(call: Call, response: Response) {
                    completeCallback()
                    val result = AESCrypt.decrypt(key, response?.body?.string())
                    successCallback(JSONArray(result))


                }

            })

    }

}
