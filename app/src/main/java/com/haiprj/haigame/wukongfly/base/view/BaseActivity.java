package com.haiprj.haigame.wukongfly.base.view;

import android.os.Bundle;
import android.view.Window;
import android.view.WindowManager;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

public abstract class BaseActivity<T> extends AppCompatActivity {

    protected T binding;

    @SuppressWarnings("unchecked")
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        supportRequestWindowFeature(Window.FEATURE_NO_TITLE);
        this.getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);
        binding = (T) DataBindingUtil.setContentView(this, getLayoutId());
        initView();
        addEvent();
    }

    protected abstract void initView();
    protected abstract void addEvent();
    protected abstract int getLayoutId();
}
