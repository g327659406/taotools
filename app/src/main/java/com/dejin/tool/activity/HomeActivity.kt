package com.dejin.tool.activity

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import com.dejin.tool.R
import com.dejin.tool.bean.Project
import kotlinx.android.synthetic.main.layout_title.*


class HomeActivity : BaseActivity() {

    private lateinit var project: Project

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        project = intent.getSerializableExtra("data") as Project
        tv_title.text = project.projectName
    }

    fun startTestUrl(view: View) {
        startActivity(Intent(this, UrlsActivity::class.java).putExtra("data", project))

    }

    fun createUrlsEncode(view: View) {
        startActivity(Intent(this, UrlEncodeActivity::class.java).putExtra("data", project))

    }

    fun createWithAppIDAndKey(view: View) {
        startActivity(Intent(this, AppIdAndKeyActivity::class.java).putExtra("data", project))

    }


}
