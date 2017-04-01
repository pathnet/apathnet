package com.pathnet.base;

import android.app.ProgressDialog;
import android.content.Context;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.Button;

import com.pathnet.R;
import com.pathnet.interfaces.IDialogControl;
import com.pathnet.interfaces.IUiInterface;
import com.pathnet.utils.DialogUtils;


public abstract class BaseActivity extends AppCompatActivity implements
        OnClickListener, IUiInterface, IDialogControl {
    public Button mDialogEnter;
    public Context mContext;
    /* 弹窗 */
    private ProgressDialog mWaitDialog;
    /* 返回时间 */
    private long exitTime = 0;
    private Button mDialogCancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        this.mContext = this;
        setContentView(getLayoutId());
        // AdjustKeyboardView.assistActivity(this);
        // 初始化View
        initView();

        // 注册监听
        initListener();

        // 初始化数据
        initData();

        // 处理点击事件
        regCommonBtn();

    }

    /**
     * 注册所有返回键
     */
    private void regCommonBtn() {
        View view = findViewById(R.id.back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    /**
     * 处理所有的点击事件 处理掉所有的返回键
     *
     * @param v
     */
    @Override
    public void onClick(View v) {
        // 把在多个界面间都存在的点击，统一处理掉
        switch (v.getId()) {
            case R.id.back:
                finish();
                break;
            default:
                processClick(v);
                break;
        }
    }

    /**
     * 显示Progrss
     *
     * @param message
     * @return
     */
    @Override
    public ProgressDialog showWaitDialog(String message) {
        if (mWaitDialog == null) {
            mWaitDialog = DialogUtils.getWaitDialog(this, message);
        }
        if (mWaitDialog != null) {
            mWaitDialog.setMessage(message);
            mWaitDialog.show();
        }
        return mWaitDialog;
    }

    /**
     * 关闭弹窗
     */
    @Override
    public void hideWaitDialog() {
        if (mWaitDialog != null) {
            try {
                mWaitDialog.dismiss();
                mWaitDialog = null;
            } catch (Exception ex) {
                ex.printStackTrace();
            }
        }
    }


    @Override
    public ProgressDialog showWaitDialog() {

        return showWaitDialog("");
    }

    @Override
    public ProgressDialog showWaitDialog(int resid) {

        return showWaitDialog(getString(resid));
    }
}
