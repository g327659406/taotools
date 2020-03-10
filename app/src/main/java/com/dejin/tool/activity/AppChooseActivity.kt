package com.dejin.tool.activity

import android.content.Intent
import android.os.Bundle
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ActivityUtils
import com.dejin.tool.R
import com.dejin.tool.adapter.ProjectAdapter
import com.dejin.tool.adapter.UrlsAdapter
import com.dejin.tool.app.AppContext
import com.dejin.tool.bean.Project
import kotlinx.android.synthetic.main.activity_app_choose.*
import kotlinx.android.synthetic.main.layout_title.*
import org.litepal.LitePal

class AppChooseActivity : BaseActivity() {

    private lateinit var adapter: ProjectAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_choose)
        tv_title.text = "项目列表"
        val projects = LitePal.findAll(Project::class.java)
        adapter = ProjectAdapter(this, projects)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { i ->
            startActivity(
                Intent(AppContext.INSTANCE, HomeActivity::class.java).putExtra(
                    "data",
                    projects[i]
                )
            )
        }
    }
}
