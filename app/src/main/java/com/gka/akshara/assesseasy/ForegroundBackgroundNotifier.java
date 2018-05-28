package com.gka.akshara.assesseasy;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;

/**
 * Created by suresh on 24-02-2018.
 * Ref: http://steveliles.github.io/is_my_android_app_currently_foreground_or_background.html
 */

public class ForegroundBackgroundNotifier implements Application.ActivityLifecycleCallbacks {


    @Override
    public void onActivityResumed(Activity activity) { }

    @Override
    public void onActivityPaused(Activity activity) { }

    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {}

    @Override
    public void onActivityStarted(Activity activity) {}
    @Override
    public void onActivityStopped(Activity activity) {}
    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {}
    @Override
    public void onActivityDestroyed(Activity activity) {}
}
