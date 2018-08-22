package com.akshara.assessment.a3;

import android.app.Application;
import android.content.Context;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.database.CursorWindow;
import android.net.ConnectivityManager;
import android.os.Build;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import com.akshara.assessment.a3.db.KontactDatabase;
import com.crashlytics.android.Crashlytics;
import com.crashlytics.android.core.CrashlyticsCore;
import com.google.firebase.analytics.FirebaseAnalytics;

import java.lang.reflect.Field;
import java.util.Locale;

import io.fabric.sdk.android.Fabric;


/**
 * Created by shridhar .
 */
public class A3Application extends Application {
    KontactDatabase db;
    private static FirebaseAnalytics mFirebaseAnalytics;

    @Override
    public void onCreate() {
        super.onCreate();

//test
    /*   this. registerReceiver(new NetworkChangeReceiver(),
                new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION));
*/
      //  Fabric.with(this, new Crashlytics.Builder().core(new CrashlyticsCore.Builder().disabled(BuildConfig.DEBUG).build()).build());
     //   fix();

        initSingletons();
        updateLanguage(this);
        mFirebaseAnalytics = FirebaseAnalytics.getInstance(this);

    }

    public static void fix() {
      /*  try {
            Field field = CursorWindow.class.getDeclaredField("sCursorWindowSize");
            field.setAccessible(true);
            field.set(null, 102400 * 1024); //the 102400 is the new size added
        } catch (Exception e) {
            e.printStackTrace();
        }*/
    }

    public static FirebaseAnalytics getAnalyticsObject()
    {

        return  mFirebaseAnalytics;
    }
    private void initSingletons() {
        db = new KontactDatabase(this);
    }

    public KontactDatabase getDb() {
        return db;
    }


    public static Context setLanguage(Context ctx, String language) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        SharedPreferences.Editor editor = prefs.edit();
        editor.putString("user_locale", language);
        editor.commit();
        return updateLanguage(ctx, language);
    }


    public static Context updateLanguage(Context ctx) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(ctx);
        String lang = prefs.getString("user_locale", "En");
        return updateLanguage(ctx, lang);
    }

    public static Context updateLanguage(Context ctx, String lang) {
     /*   Configuration cfg = new Configuration();
        if (!TextUtils.isEmpty(lang))
            cfg.locale = new Locale(lang);
        else
            cfg.locale = Locale.getDefault();

        ctx.getResources().updateConfiguration(cfg, null);*/

        Context context = ctx;

        Resources rs = context.getResources();
        Configuration config = rs.getConfiguration();

    /*    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            sysLocale = config.getLocales().get(0);
        } else {
            sysLocale = config.locale;
        }*/
        if (!TextUtils.isEmpty(lang)) {
            Locale locale = new Locale(lang);
            Locale.setDefault(locale);
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                config.setLocale(locale);
            } else {
                config.locale = locale;
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N_MR1) {
                context = context.createConfigurationContext(config);
            } else {
                ctx.getResources().updateConfiguration(config, rs.getDisplayMetrics());
            }
        }

        return context;


    }


    @Override
    protected void attachBaseContext(Context newBase) {

        Context context = updateLanguage(newBase);
        super.attachBaseContext(context);

    }


}
