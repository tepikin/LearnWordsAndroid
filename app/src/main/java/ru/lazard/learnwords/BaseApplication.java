package ru.lazard.learnwords;

import android.app.Application;

import com.google.firebase.analytics.FirebaseAnalytics;

import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.db.DbHelper;

/**
 * Created by Egor on 07.06.2016.
 */
public class BaseApplication extends Application{
    private static BaseApplication instance;

    public static BaseApplication getInstance() {
        return instance;
    }

    @Override
    public void onCreate() {
        instance = this;
        DbHelper.init(this);
        Model.init(this);

        super.onCreate();
        FirebaseAnalytics.getInstance(this);
    }
}
