package com.dejin.urltest.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.text.TextUtils
import com.dejin.urltest.R
import com.dejin.urltest.aes.EncodeUtils
import com.dejin.urltest.aes.Md5Utils
import com.gyf.immersionbar.ImmersionBar
import com.iflytek.cloud.ErrorCode
import com.iflytek.cloud.SpeechConstant
import com.iflytek.cloud.SpeechSynthesizer
import com.iflytek.cloud.SpeechUtility
import com.iflytek.cloud.util.ResourceUtil
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.Response
import java.lang.Exception
import java.util.*

class LaunchActivity : AppCompatActivity() {

    private val key = "U2FsdGVkX19X5NAwZN8I2oWsRPaYGJPy"

    private val okHttpClient = OkHttpClient()
    private val timer = Timer()
    private lateinit var mTts: SpeechSynthesizer
    private var paramsType = "1"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).fitsSystemWindows(false).init()
        setContentView(R.layout.activity_launch)
        SpeechUtility.createUtility(this, SpeechConstant.APPID + "=5e325dac")
        mTts = SpeechSynthesizer.createSynthesizer(this) {
            if (it == ErrorCode.SUCCESS) {
                timer.schedule(object : TimerTask() {
                    override fun run() {
                        val params = mutableMapOf<String, String>()
                        params["mod"] = "appNotice"
                        params["type"] = paramsType
                        val result = startTestUrl(params)
                        if (TextUtils.isEmpty(result).not()) {
                            mTts.startSpeaking(result, null)
                        }
                        paramsType = if (paramsType == "1") "2" else "1"

                    }

                }, 0, 30000)
            }
        }
        setParam()

    }


    fun startTestUrl(params: Map<String, String>): String? {
        var url = "https://ers.letongfushi.com/api/app.php?"
        //   var url = "http://test.tjd889.com/pph/api/app.php?"
        val newParams = params.toSortedMap()
        println(params.toString())
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
            return response.body?.string().toString()
        } catch (e: Exception) {
            e.printStackTrace()
        } finally {
            response?.close()
        }
        return null
    }

    /**
     * 参数设置
     */
    private fun setParam() {
        // 清空参数
        mTts.setParameter(SpeechConstant.PARAMS, null)
        mTts.setParameter(SpeechConstant.ENGINE_TYPE, SpeechConstant.TYPE_LOCAL)
        //设置发音人资源路径
        mTts.setParameter(ResourceUtil.TTS_RES_PATH, getResourcePath())
    }

    // 获取发音人资源路径
    private fun getResourcePath(): String {
        // 合成通用资源
        // 发音人资源
        return ResourceUtil.generateResourcePath(
            this,
            ResourceUtil.RESOURCE_TYPE.assets,
            "tts/common.jet"
        ) + ";" + ResourceUtil.generateResourcePath(
            this,
            ResourceUtil.RESOURCE_TYPE.assets,
            "tts/xiaoyan.jet"
        )
    }


    override fun onBackPressed() {
        moveTaskToBack(true)
    }
}
