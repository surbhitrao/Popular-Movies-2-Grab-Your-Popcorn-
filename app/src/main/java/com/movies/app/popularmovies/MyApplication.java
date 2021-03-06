package com.movies.app.popularmovies;
import android.app.Application;

import com.facebook.stetho.Stetho;

/**
 * Created by Surbhit
 * */
public class MyApplication extends Application
{
    @Override
    public void onCreate() {
        super.onCreate();

        Stetho.InitializerBuilder initializerBuilder=Stetho.newInitializerBuilder(this);


        initializerBuilder.enableWebKitInspector(Stetho.defaultInspectorModulesProvider(this));


        initializerBuilder.enableDumpapp(Stetho.defaultDumperPluginsProvider(this));



        Stetho.Initializer initializer=initializerBuilder.build();


        Stetho.initialize(initializer);

    }
}
