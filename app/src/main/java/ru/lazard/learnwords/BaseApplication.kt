package ru.lazard.learnwords

import android.app.Application

import com.google.firebase.analytics.FirebaseAnalytics

import ru.lazard.learnwords.model.Model
import ru.lazard.learnwords.model.db.DbHelper
import ru.lazard.learnwords.ui.fragments.books.readBook.Translate

/**
 * Created by Egor on 07.06.2016.
 */
class BaseApplication : Application() {

    override fun onCreate() {
        instance = this
        DbHelper.init(this)
        Model.init(this)


        super.onCreate()
        FirebaseAnalytics.getInstance(this)
    }

    companion object {
        var instance: BaseApplication? = null
            private set
    }
}
