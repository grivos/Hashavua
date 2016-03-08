package com.invariantlabs.hashavua;

import android.support.v4.app.Fragment;

import com.invariantlabs.hashavua.view.FilterablesView;
import com.invariantlabs.hashavua.main.HashavuaFeedAdapter;
import com.invariantlabs.hashavua.main.MainActivity;
import com.invariantlabs.hashavua.main.MainPresenter;
import com.invariantlabs.hashavua.view.SubjectsColorHelper;

import javax.inject.Named;

public interface HashavuaGraph {
    void inject(HashavuaApp app);
    void inject(MainActivity activity);
    void inject(Fragment fragment);
    void inject(HashavuaFeedAdapter hashavuaFeedAdapter);
    void inject(FilterablesView filterablesView);

    MainPresenter mainPresenter();
    @Named("subjects") SubjectsColorHelper subjectsColorHelper();
    @Named("main_subjects") SubjectsColorHelper mainSubjectsColorHelper();



}
