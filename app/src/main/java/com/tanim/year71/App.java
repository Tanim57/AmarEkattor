package com.tanim.year71;

import android.app.Application;
import android.content.Context;

/**
 * Created by tanim on 3/7/2018.
 */

public class App extends Application {
    private static Context mContext;

    @Override
    public void onCreate() {
        super.onCreate();
        mContext = App.this;
    }
    public static Context getContext()
    {
        return mContext;
    }

}
