package com.dejin.tool.activity

import android.app.AlertDialog
import android.content.Intent
import android.os.Bundle
import android.text.TextUtils
import android.view.View
import androidx.recyclerview.widget.LinearLayoutManager
import com.blankj.utilcode.util.ToastUtils
import com.dejin.tool.R
import com.dejin.tool.adapter.ProjectAdapter
import com.dejin.tool.app.AppContext
import com.dejin.tool.bean.Project
import com.dejin.tool.dialog.AddProjectsDialog
import kotlinx.android.synthetic.main.activity_app_choose.*
import kotlinx.android.synthetic.main.dialog_add_project.*
import kotlinx.android.synthetic.main.layout_title.*
import org.litepal.LitePal

class AppChooseActivity : BaseActivity() {
    private lateinit var adapter: ProjectAdapter
    private val list = mutableListOf<Project>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_app_choose)
        tv_title.text = "项目列表"
        tv_right.visibility = View.VISIBLE
        tv_right.text = "新增"
        tv_right.setOnClickListener {
            val dialog = AddProjectsDialog(this)
            dialog.show()
            dialog.tv_cancel.setOnClickListener {
                dialog.dismiss()
            }
            dialog.tv_confirm.setOnClickListener {
                val projectName = dialog.et_name.text.toString().trim()
                val signKey = dialog.et_key.text.toString().trim()
                val url = dialog.et_url.text.toString().trim()
                val appId = dialog.et_appId.text.toString().trim()
                val privateKey = dialog.et_private_key.text.toString().trim()
                if (TextUtils.isEmpty(projectName)) {
                    ToastUtils.showShort("项目名称不能为空")
                    return@setOnClickListener
                }
                if (LitePal.isExist(Project::class.java, "projectName = ?", projectName)) {
                    ToastUtils.showShort("项目名称重复")
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(signKey)) {
                    ToastUtils.showShort("signKey不能为空")
                    return@setOnClickListener
                }
                if (TextUtils.isEmpty(url)) {
                    ToastUtils.showShort("域名组地址不能为空")
                    return@setOnClickListener
                }

                val project = Project()
                project.projectName = projectName
                project.signKey = signKey
                project.urls = url
                project.appId = appId
                project.privateKey = privateKey
                project.save()
                list.clear()
                list.addAll(LitePal.findAll(Project::class.java))
                adapter.notifyDataSetChanged()
                dialog.dismiss()
            }

        }
        list.addAll(LitePal.findAll(Project::class.java))
        adapter = ProjectAdapter(this, list)
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = adapter
        adapter.setOnItemClickListener { i ->
            startActivity(
                    Intent(AppContext.INSTANCE, HomeActivity::class.java).putExtra(
                            "data",
                            list[i]
                    )
            )
        }
        adapter.setOnItemLongClickListener {
            AlertDialog.Builder(this).setTitle("提示")
                    .setMessage("是否删除该项目？")
                    .setPositiveButton("确定") { dialog, _ ->
                        val url = list[it]
                        list.removeAt(it)
                        adapter.notifyDataSetChanged()
                        LitePal.deleteAll(
                                Project::class.java,
                                "projectName = ?",
                                url.projectName
                        )
                        dialog.dismiss()

                    }
                    .setNegativeButton("取消") { dialog, _ ->
                        dialog.dismiss()
                    }
                    .setCancelable(false).create().show()
        }
    }
}
