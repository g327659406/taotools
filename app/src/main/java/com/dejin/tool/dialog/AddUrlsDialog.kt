package com.dejin.tool.dialog

import android.app.Activity
import android.os.Bundle
import android.view.KeyEvent
import com.dejin.tool.R
import kotlinx.android.synthetic.main.dialog_addurl.*

class AddUrlsDialog constructor(activity: Activity) : BaseDialog(activity) {
    private var isForce: Boolean = false


    override fun onCreateDialog(savedInstanceState: Bundle?) {
        setContentView(R.layout.dialog_addurl)
        tv_cancel.setOnClickListener { dismiss() }
        setCanceledOnTouchOutside(false)
        setOnKeyListener { _, keyCode, event ->
            if (keyCode == KeyEvent.KEYCODE_BACK && event.repeatCount == 0) {
                return@setOnKeyListener isForce.not()
            }
            false
        }
    }


}