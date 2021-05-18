package com.dejin.tool.activity

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.dejin.tool.R
import com.dejin.tool.adapter.UrlsAdapter
import com.dejin.tool.aes.EncodeUtils
import com.dejin.tool.aes.Md5Utils
import com.dejin.tool.bean.Project
import com.dejin.tool.bean.Urls
import com.dejin.tool.utils.Util
import kotlinx.android.synthetic.main.activity_test_url.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.*
import org.json.JSONArray
import org.litepal.util.cipher.AESCrypt
import java.io.IOException
import java.lang.Exception
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors


class UrlsActivity : BaseActivity() {

    private val okHttpClient = OkHttpClient()
    private val list = arrayListOf<Urls>()
    private lateinit var adapter: UrlsAdapter
    private val fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(8)
    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_test_url)
        project = intent.getSerializableExtra("data") as Project
        tv_title.text = project.projectName + " — 域名测试"
        tv_right.visibility = View.VISIBLE
        tv_right.text = "重试"

        adapter = UrlsAdapter(this, list)
        listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listView.adapter = adapter
        adapter.setOnItemClickListener { i ->
            val url = list[i]
            fixedThreadPool.submit {
                url.isRunning = true
                url.isAvaliable = false
                notifyDataSetChanged()
                val result = Util.startTestUrl(url.url)
                url.isRunning = false
                url.isAvaliable = result
                notifyDataSetChanged()
            }
        }
        startTest()
        tv_right.setOnClickListener {
            list.clear()
            adapter.notifyDataSetChanged()
            startTest()
        }

    }

    private fun startTest() {
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
                    val result = Util.startTestUrl(it.url)
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

    private fun notifyDataSetChanged() {
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }

    private fun startTestUrl(pUrl: String): Boolean {
        println("开始测试$pUrl")
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
        sign += ("secret=${project.signKey}")
        sign = Md5Utils.MD5Encode(sign, "UTF-8", false)
        url += "sign=$sign"
        val request = Request.Builder().get().url(url).build()
        var response: Response? = null
        try {
            response = okHttpClient.newCall(request).execute()
            return true
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            response?.close()
        }
        return false

    }


    /**获取所有备用的url列表*/
    private fun getBaseUrls(successCallback: (JSONArray) -> Unit = {}, completeCallback: () -> Unit = {}) {
        okHttpClient.newCall(Request.Builder().get().url(project.urls).build())//新域名
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        completeCallback()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        completeCallback()
                        val result = AESCrypt.decrypt(project.signKey, response?.body?.string())
                        successCallback(JSONArray(result))
                    }

                })
    }

}
