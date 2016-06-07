package ru.lazard.learnwords;

import android.app.Application;

import ru.lazard.learnwords.db.DbHelper;
import ru.lazard.learnwords.model.Model;

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
    }
}
