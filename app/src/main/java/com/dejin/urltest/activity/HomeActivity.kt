package com.dejin.urltest.activity

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dejin.urltest.R

class HomeActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
    }

    fun startTestUrl(view: View) {
        startActivity(Intent(this, UrlsActivity::class.java))

    }

    fun createUrlsEncode(view: View) {
        startActivity(Intent(this, UrlEncodeActivity::class.java))

    }

    fun createWithAppIDAndKey(view: View) {
        startActivity(Intent(this, AppIdAndKeyActivity::class.java))

    }


}
