package com.dejin.tool.activity

import android.app.AlertDialog
import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.dejin.tool.R
import com.dejin.tool.adapter.UrlsEncodeAdapter
import com.dejin.tool.bean.Project
import com.dejin.tool.bean.Urls
import com.dejin.tool.dialog.AddUrlsDialog
import com.dejin.tool.utils.Util
import kotlinx.android.synthetic.main.activity_test_url.*
import kotlinx.android.synthetic.main.dialog_add_url.*
import kotlinx.android.synthetic.main.layout_title.*
import okhttp3.*
import org.json.JSONArray
import org.litepal.LitePal
import org.litepal.util.cipher.AESCrypt
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class UrlEncodeActivity : BaseActivity() {
    private lateinit var project: Project

    private val list = arrayListOf<Urls>()
    private lateinit var adapter: UrlsEncodeAdapter
    private val fixedThreadPool: ExecutorService = Executors.newFixedThreadPool(8)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_encode)
        project = intent.getSerializableExtra("data") as Project
        tv_title.text = project.projectName + " — 域名加密"
        tv_right.visibility = View.VISIBLE
        tv_right.text = "同步"

        adapter = UrlsEncodeAdapter(this, list)
        listView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        listView.adapter = adapter
        adapter.setOnItemClickListener { i ->
            val url = list[i]
            fixedThreadPool.submit {
                url.isRunning = true
                url.isAvaliable = false
                notifyDataSetChanged()
//                val result = startTestUrl(url.url)
                val result = Util.startTestUrl(url.url)
                url.isRunning = false
                url.isAvaliable = result
                notifyDataSetChanged()
            }
        }

        adapter.setOnItemLongClickListener {
            //长按可删除
            AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("是否删除该域名？")
                    .setPositiveButton("确定") { dialog, _ ->
                        val url = list[it]
                        list.removeAt(it)
                        notifyDataSetChanged()
                        LitePal.deleteAll(
                                Urls::class.java,
                                "url = ? and projectName = ?",
                                url.url,
                                project.projectName
                        )
                        dialog.dismiss()

                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false).create().show()
        }

        tv_right.setOnClickListener {
            AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("同步后将重新获取服务器上的域名，并同步到本地，本地记录将被清除。是否继续？")
                    .setPositiveButton("确定") { dialog, _ ->
                        dialog.dismiss()
                        progress.visibility = View.VISIBLE
                        listView.visibility = View.INVISIBLE
                        getBaseUrls(successCallback = {
                            list.clear()
                            for (i in 0 until it.length()) {
                                val url = Urls()
                                url.projectName = project.projectName
                                url.url = it[i].toString()
                                list.add(url)
                            }
                            notifyDataSetChanged()
                            if (LitePal.isExist(
                                            Urls::class.java,
                                            "projectName = ?",
                                            project.projectName
                                    )
                            ) {
                                LitePal.deleteAll(
                                        Urls::class.java, "projectName = ?",
                                        project.projectName
                                )
                            }
                            LitePal.saveAll(list)
                            ToastUtils.showShort("同步成功")

                        }, completeCallback = {
                            runOnUiThread {
                                progress.visibility = View.GONE
                                listView.visibility = View.VISIBLE
                            }
                        })
                        //同步到数据库
                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false).create().show()
        }
        progress.visibility = View.VISIBLE
        listView.visibility = View.INVISIBLE
        if (LitePal.isExist(Urls::class.java, "projectName = ?", project.projectName)) {
            LitePal.where("projectName = ?", project.projectName).find(Urls::class.java)
                    .forEach { list.add(it) }
            notifyDataSetChanged()
            progress.visibility = View.GONE
            listView.visibility = View.VISIBLE
        } else {
            getBaseUrls(successCallback = {
                list.clear()
                for (i in 0 until it.length()) {
                    val url = Urls()
                    url.url = it[i].toString()
                    list.add(url)
                }
                notifyDataSetChanged()
            }, completeCallback = {
                runOnUiThread {
                    progress.visibility = View.GONE
                    listView.visibility = View.VISIBLE
                }
            })
        }
    }

    private fun notifyDataSetChanged() {
        runOnUiThread {
            adapter.notifyDataSetChanged()
        }
    }


    /**获取所有备用的url列表*/
    private fun getBaseUrls(successCallback: (JSONArray) -> Unit = {}, completeCallback: () -> Unit = {}) {
        OkHttpClient().newCall(Request.Builder().get().url(project.urls).build())//新域名
                .enqueue(object : Callback {
                    override fun onFailure(call: Call, e: IOException) {
                        completeCallback()
                    }

                    override fun onResponse(call: Call, response: Response) {
                        completeCallback()
                        println(response?.body?.string())
                        try {
                            val result = AESCrypt.decrypt(project.signKey, response?.body?.string())
                            successCallback(JSONArray(result))
                        } catch (e: java.lang.Exception) {
                            e.printStackTrace()
                            ToastUtils.showShort("域名出错")
                        }
                    }

                })

    }

    fun addItem(view: View) {
        //添加新的记录
        val dialog = AddUrlsDialog(this)
        dialog.show()
        dialog.tv_confirm.setOnClickListener {
            val records = dialog.et_records.text.toString().trim()
            if (TextUtils.isEmpty(records)) {
                ToastUtils.showShort("请输入域名")
                return@setOnClickListener
            }
            records.split("\n").forEach { it ->
                var url = it
                if (it.contains("https://").not()) {
                    url = "https://$it"
                }
                if (LitePal.isExist(Urls::class.java, "url = ?", url)) {
                    ToastUtils.showShort("域名重复")
                    return@setOnClickListener
                }
                val urls = Urls()
                urls.url = url
                urls.projectName = project.projectName
                urls.save()
                list.add(urls)
                notifyDataSetChanged()
            }

        }

    }

    //生成加密域名组
    fun createEncode(view: View) {
        val array = JSONArray()
        list.forEach {
            array.put(it.url)
        }
        val encode = AESCrypt.encrypt(project.signKey, array.toString())
        println(encode)

        //获取剪贴板管理器：
        val cm: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText(
                "Label", encode
        )
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)
        println(AESCrypt.decrypt(project.signKey, encode))
        ToastUtils.showShort("添加到剪贴板成功")
    }

}
