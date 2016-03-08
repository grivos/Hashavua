package com.invariantlabs.hashavua.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;

import com.hannesdorfmann.mosby.mvp.MvpFragment;
import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.MvpView;
import com.invariantlabs.hashavua.HashavuaApp;

import butterknife.ButterKnife;

public abstract class BaseMvpFragment<V extends MvpView, P extends MvpPresenter<V>> extends MvpFragment<V, P> {

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        HashavuaApp.get(getContext()).component().inject(this);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        ButterKnife.bind(this, view);
    }
}
