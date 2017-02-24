package com.example.picturewall;

import android.os.Bundle;
import android.support.v4.app.FragmentActivity;

import com.example.picturewall.util.ToastUtil;

/**
 * base activity
 */
public abstract class BaseActivity extends FragmentActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init(savedInstanceState);
    }

    /**
     * 初始化view，先加载布局, setContentView(int);
     */
    protected abstract void initView();

    /**
     * 初始化view监听
     */
    protected abstract void initListener();

    /**
     * 初始化广播监听
     */
    protected abstract void initReceiver();

    /**
     * 初始化数据
     */
    protected abstract void initData(Bundle savedInstanceState);

    /**
     * 保存页面数据
     */
    protected abstract void saveData(Bundle outState);


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        saveData(outState);
    }


    private void init(Bundle savedInstanceState) {
        initView();
        initListener();
        initReceiver();
        initData(savedInstanceState);
    }

    protected void showToast(CharSequence text) {
        ToastUtil.show(this, text);
    }
}

