package com.invariantlabs.hashavua.base;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;

import com.hannesdorfmann.mosby.mvp.MvpActivity;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.invariantlabs.hashavua.HashavuaApp;
import com.invariantlabs.hashavua.HashavuaGraph;
import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.util.Util;

import butterknife.ButterKnife;
import uk.co.chrisjenx.calligraphy.CalligraphyContextWrapper;

public abstract class BaseMvpActivity<V extends MvpView, P extends MvpPresenter<V>> extends MvpActivity<V, P> {

    protected HashavuaGraph component;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            getWindow().setStatusBarColor(getResources().getColor(R.color.primary_dark));
        }
        component = HashavuaApp.get(this).component();
        super.onCreate(savedInstanceState);
        Util.setRtl(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        ButterKnife.bind(this);
    }

    @Override
    protected void attachBaseContext(Context newBase) {
        super.attachBaseContext(CalligraphyContextWrapper.wrap(newBase));
    }
}
