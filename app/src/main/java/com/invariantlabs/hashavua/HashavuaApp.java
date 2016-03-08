package com.invariantlabs.hashavua;

import android.app.Application;
import android.content.Context;

import timber.log.Timber;
import timber.log.Timber.DebugTree;
import uk.co.chrisjenx.calligraphy.CalligraphyConfig;

public class HashavuaApp extends Application {

    private HashavuaGraph component;

    @Override public void onCreate() {
        super.onCreate();
        if (BuildConfig.DEBUG) {
            Timber.plant(new DebugTree());
        }
        buildComponentAndInject();
        initCalligraphy();
    }

    private void initCalligraphy() {
        CalligraphyConfig.initDefault(new CalligraphyConfig.Builder()
                        .setDefaultFontPath(getString(R.string.font_regular_path))
                        .setFontAttrId(R.attr.fontPath)
                        .build()
        );
    }

    public void buildComponentAndInject() {
        component = HashavuaComponent.Initializer.init(this);
        component.inject(this);
    }

    public HashavuaGraph component() {
        return component;
    }

    public static HashavuaApp get(Context context) {
        return (HashavuaApp) context.getApplicationContext();
    }
}
