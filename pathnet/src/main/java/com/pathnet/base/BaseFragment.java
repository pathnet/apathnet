package com.pathnet.base;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;

import com.pathnet.R;
import com.pathnet.interfaces.IUiInterface;


public abstract class BaseFragment extends Fragment implements View.OnClickListener, IUiInterface {

    public View mView;
    public Context mContext;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        mContext = getActivity();
        mView = View.inflate(getActivity(), getLayoutId(), null);
        initView();
        initListener();
        initData();
        regCommonBtn();
        return mView;
    }

    /**
     * 返回viewId引用的view
     */
    protected View findViewById(int viewId) {

        return mView.findViewById(viewId);
    }

    /**
     * 处理所有的返回键
     */
    private void regCommonBtn() {
        View view = findViewById(R.id.back);
        if (view != null) {
            view.setOnClickListener(this);
        }
    }

    @Override
    public void onClick(View v) {
        // 把在多个界面间都存在的点击，统一处理掉
        switch (v.getId()) {
            case R.id.back:
                getFragmentManager().popBackStack();
                break;
            default:
                processClick(v);
                break;
        }
    }

    /* 点击空白区域隐藏软甲盘 */
    public boolean onTouchEvent(MotionEvent event) {
        if (null != getActivity().getCurrentFocus()) {
            InputMethodManager mInputMethodManager = (InputMethodManager) getActivity().getSystemService(mContext.INPUT_METHOD_SERVICE);
            return mInputMethodManager.hideSoftInputFromWindow(getActivity().getCurrentFocus().getWindowToken(), 0);
        }
        return super.getActivity().onTouchEvent(event);
    }

    public void hideKeyboard(View view) {
        //隐藏键盘
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getCurrentFragmentName(BaseFragment fragment) {
        String fragName = fragment.getClass().getSimpleName();
        fragName = fragName.substring(fragName.lastIndexOf(".") + 1, fragName.length());
        return fragName;
    }

}
