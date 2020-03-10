package com.dejin.tool.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import com.dejin.tool.R
import com.gyf.immersionbar.ImmersionBar

open class BaseActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ImmersionBar.with(this).barColor(R.color.colorPrimary).fitsSystemWindows(true).init()
    }

    fun back(view: View) {
        finish()
    }
}
