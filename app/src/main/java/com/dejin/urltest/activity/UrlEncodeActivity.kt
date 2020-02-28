package com.dejin.urltest.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.dejin.urltest.R
import kotlinx.android.synthetic.main.activity_url_encode.*
import org.litepal.util.cipher.AESCrypt

class UrlEncodeActivity : AppCompatActivity() {
    private var key = "U2FsdGVkX19X5NAwZN8I2oWsRPaYGJPy"//淘金岛  钱多多
    // private var key = "nrVvOYKC3yYiZRd13QjSQbTKGlXzLGaA"//人人赚

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_url_encode)
        et_urls.setText(urls)
    }

    //淘金岛
    /*  private val urls =
          """["https://balance.leffood.com","https://ers.letongfushi.com","https://ers.leffood.com","https://ers.zzzxqyfwpt.com","https://ers.xxxhzxx.com",
              "https://api.wumiicdn.com","https://api.quepinget.com","https://api.bjbhxykj.com","https://api.4000700917.com","https://duoyixin.com","https://ccduobao.com",
              "https://nmgxykaolin.com","https://xinjiangauto.com","https://api.sygcsx.com","https://go.echaeke.com","https://yd.marxns.com",
              "https://lt.yuanqiguo.com","https://df.hfzgfwbfzx.com"]""".trimIndent()*/

    //钱多多
    private val urls =
        """["https://api.islandke.com","https://api.zhangjiandev.com","https://api.miyuhao.com","https://api.yuanwu123.com","https://api.ddline3765.shop","https://api.qdd6541.cn","https://api.qdd6540.cn","https://api.qdd6542.cn"]""".trimIndent()
    //人人赚
//    private val urls =
//        """["https://api.qdd4356.club","https://api.zdd1435.ltd","https://api.zdd8758.net","https://api.zdd8989.com","https://api.ddline3748.com"]""".trimIndent()

    fun startEncode(view: View) {
        val urls = et_urls.text.toString().trim()
        if (TextUtils.isEmpty(urls)) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show()
            return
        }


        //获取剪贴板管理器：
        val cm: ClipboardManager =
            getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
        // 创建普通字符型ClipData
        val mClipData = ClipData.newPlainText(
            "Label", AESCrypt.encrypt(key, urls)
        )
        // 将ClipData内容放到系统剪贴板里。
        cm.setPrimaryClip(mClipData)

    }

    fun startDecode(view: View) {
        val urls = et_encode_urls.text.toString().trim()
        if (TextUtils.isEmpty(urls)) {
            Toast.makeText(this, "不能为空", Toast.LENGTH_SHORT).show()
            return
        }

        et_urls.setText(AESCrypt.decrypt(key, urls))


    }
}
