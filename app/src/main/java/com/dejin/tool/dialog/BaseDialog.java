package com.dejin.tool.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.Window;
import android.view.WindowManager;

import com.dejin.tool.R;


/**
 * Created by kevin.
 */
public abstract class BaseDialog extends Dialog {
    private Activity activity;

    public BaseDialog(Activity activity) {
        super(activity, R.style.add_dialog);
        this.activity = activity;
    }

    public BaseDialog(Activity activity, int themeResId) {
        super(activity, R.style.add_dialog);
        this.activity = activity;
    }

    /**
     * 设置对话宽度是否包含内容
     */
    private boolean wrapContent = false;

    public void setWrapContent(boolean wrapContent) {
        this.wrapContent = wrapContent;
    }

    private int gravity = Gravity.CENTER;
    private float marginWidth = 0.85f;

    public void setGravity(int gravity) {
        this.gravity = gravity;
    }

    public void setMarginWidth(float marginWidth) {
        this.marginWidth = marginWidth;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        onCreateDialog(savedInstanceState);
        Window dialogWindow = getWindow();
        dialogWindow.setGravity(gravity);
        if (!wrapContent) {
            WindowManager m = activity.getWindowManager();
            Display d = m.getDefaultDisplay(); // 获取屏幕宽、高用
            WindowManager.LayoutParams p = dialogWindow.getAttributes(); // 获取对话框当前的参数值
            p.width = (int) (d.getWidth() * marginWidth); // 宽度设置为屏幕的0.85
            dialogWindow.setAttributes(p);
        }
    }

    public abstract void onCreateDialog(Bundle savedInstanceState);

}
