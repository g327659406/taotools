package com.dejin.urltest.activity

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dejin.urltest.R
import kotlinx.android.synthetic.main.activity_app_id_and_key.*
import org.json.JSONObject
import org.litepal.util.cipher.AESCrypt


class AppIdAndKeyActivity : AppCompatActivity() {
    companion object {
        private val AESKEY = "U2FsdGVkX19X5NAwZN8I2oWsRPaYGJPy"
        private val PRIVATE_KEY =
            "MIIEvQIBADANBgkqhkiG9w0BAQEFAASCBKcwggSjAgEAAoIBAQChM8fZtuxnjRuiL3kV7AWQWBEN/4zkrppqL7z4NHp9hwjfRwWa8iCDBWn6q8UZ+bpsouhCJMIrGpwTo+aXWMxnf98aorMAqOUmYxK7Iai+ilMxFBhHty+uvRKQ+XoIlZgVdQoUBfQ+pt5DBuE7fL/LckVNq3fbuSM/1IrWu9YVtTB7EzTm/p+1w8/dMQ2aXw6FUhdJvTioVGE3+epu7QBWOJdkPDmqUO3x0gZ9e2FE4wAnLhPneRXFz2ra9Y5jSxL6jDItQpZkGVyfY/BK0AHnTBfT+IBsEhTHqSFg8dLKLaAmGh+sJl4eDB33lG+zNDbnINGq6xrU0vnr57/svanrAgMBAAECggEATfuWuqPbSVzh+Vut4AUFzAZOec8egaJV4PinGguJFiC1Eg5xj7uCLt/3nORrvV0p6lQEt7rAk8mTp4yqiJzUhV0+uMvsfG04LZHQBecKkuKnzB5EgK0FzCp7SNbnSi/UGs07yuP2fDiNXS9+1iBLSEhHboN8wDaX7tELOUY1EtqE6dyjHxN5KJdrKB/7EzQh5KDMmSddN8vi57DBrzhL2FmUxrbNNfusU3xHlNiYDztSbLk3yRxmbMJAg8CAstQxbSC82OcteZ8cLdPRKnuGL+VIUtfeV0flHhqUfp2akeV3Z0/dDDtyogeMfC4hR7ab4PL9f3U1uSz35oijav1FAQKBgQDw2zUl9v1QVSd4bnWnJX7FHvJdDOyjRNCtlnvuxDmS8ysJL+8JIbw2rHfR4km1ZAfPptx520G3tgegEqkxom9mQ4wjzSc0jqLr0JXM3mUiHqcm25bss1OMAWFQmWIXwAfySpUijodKzfBeHeE7QEh4Dh5PP05WF67V5BzDU0dpsQKBgQCrVnjbvw0xsb2UOPYoe1efLeTaZC/xUVqH/6VP1mrcCYqjtoBRsrD9sGK1JW5rBhoHi8mm8suv5bjT15mFOqZIYlwoAeQqFGiBXplvVt5efxqzaGRH05EFqF/TKPUjoIepv9AlIhMaDSrxG7KUVDzqY9bKGpXQkr5Ti8LlcbqYWwKBgFfPQ0gtDNs9wF8avlAKznXlO20LKja3/vBaXaKF7YuSlc0KqbYFk43HQQPG4SJ9bo+ZAOZ4NjRY69zDEOYkl0KMO8pNoA58Ng+NAyAmsG5JCJ2VDygKlNoFIsp0sRuBiiQeZtKrbc2q2QgnEpc8b289jnVL2i5vAfG+8XGQqgdxAoGATdhr8OtMw1dOGxoGPT5NATWI1PDQF4Qu7btpEG0LAA4Vv2wbwQK/B4QsVME8+cM78FHCvOdzQ+fLofRC/BU5l4mY9VRmpfe/w4eHjt6ZCbYWG41Cgp/NNBTb9MSXHCjDeox7tWNV6nAxInrDGEFtq9DQteJ+TtVdQ/gsBLM3ZCECgYEA3gZ6EKlxAIjQvGaIzQMpXXMcE9vxnpxDbwQorHrir8k9J0peAwHV0BHvlzzM8ugKT1q0t+WAbWsdf1Iozmjku24ZuLoHEsuK6LAFXBwPlCG0VbNN2iz/l57CdfBKEy3S7dDWUz8h4BYUawfewmvvSVizbeKSRLugSH4rxpGny9M="
        private val APP_ID = "2019111769218219"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_id_and_key)
        et_appid.setText(APP_ID)
        et_key.setText(PRIVATE_KEY)
    }

    fun coypToClipe(view: View) {
        val appId = et_appid.text.toString().trim()
        val key = et_key.text.toString().trim()
        if (TextUtils.isEmpty(appId).not() and TextUtils.isEmpty(key).not()) {
            //获取剪贴板管理器：
            val cm: ClipboardManager =
                getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
            // 创建普通字符型ClipData
            val mClipData = ClipData.newPlainText(
                "Label", AESCrypt.encrypt(
                    AESKEY, JSONObject().put("uid", appId).put("key", PRIVATE_KEY).toString()
                )
            )
            // 将ClipData内容放到系统剪贴板里。
            cm.setPrimaryClip(mClipData)
        }

    }

    fun jiema(view: View) {
        val pwd = et_pwd.text.toString().trim()
        tv_jm.setText( AESCrypt.decrypt(AESKEY,pwd))


    }
}
