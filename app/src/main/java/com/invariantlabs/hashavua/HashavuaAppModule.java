package com.invariantlabs.hashavua;

import android.app.Application;
import android.content.Context;

import com.google.gson.FieldNamingPolicy;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.invariantlabs.hashavua.model.HashavuaApi;
import com.invariantlabs.hashavua.model.HashavuaApiImpl;
import com.invariantlabs.hashavua.model.HashavuaSpreadSheetApi;
import com.invariantlabs.hashavua.model.db.DatabaseHelper;
import com.invariantlabs.hashavua.model.db.DbOpenHelper;
import com.invariantlabs.hashavua.view.ErrorMessageDeterminer;
import com.invariantlabs.hashavua.view.SubjectsColorHelper;


import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

@Module
public class HashavuaAppModule {
    private final HashavuaApp app;

    public HashavuaAppModule(HashavuaApp app) {
        this.app = app;
    }

    @Provides @Singleton
    Application provideApplication() {
        return app;
    }

    @Provides @Singleton
    Context provideContext() {
        return app;
    }

    @Provides @Singleton
    HashavuaSpreadSheetApi provideHashavuaSpreadsheetApi() {
        HttpLoggingInterceptor interceptor = new HttpLoggingInterceptor();
        interceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient client = new OkHttpClient.Builder()/*.addInterceptor(interceptor)*/.build();
        Gson gson = new GsonBuilder()
                .setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES)
                .excludeFieldsWithoutExposeAnnotation()
                .create();
        return new Retrofit.Builder()
                .baseUrl("http://spreadsheets.google.com/")
                .addConverterFactory(GsonConverterFactory.create(gson))
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .client(client)
                .build()
                .create(HashavuaSpreadSheetApi.class);
    }

    @Provides @Singleton
    HashavuaApi provideHashavuaApi(HashavuaApiImpl impl) {
        return impl;
    }

    @Provides @Singleton @Named("subjects")
    SubjectsColorHelper provideSubjectsColorHelper(Context context) {
        return new SubjectsColorHelper(context, R.array.subjects_colors);
    }

    @Provides @Singleton @Named("main_subjects")
    SubjectsColorHelper provideMainSubjectsColorHelper(Context context) {
        return new SubjectsColorHelper(context, R.array.main_subjects_colors);
    }

    @Provides @Singleton
    public ErrorMessageDeterminer provideErrorMessageDeterminer(){
        return new ErrorMessageDeterminer();
    }

    @Provides @Singleton
    public DatabaseHelper provideDbHelper(Application application){
        DbOpenHelper dbOpenHelper = new DbOpenHelper(application);
        return new DatabaseHelper(dbOpenHelper);
    }
}
