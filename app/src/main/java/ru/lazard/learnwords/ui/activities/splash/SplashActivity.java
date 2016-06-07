package ru.lazard.learnwords.ui.activities.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);


        if (Model.getInstance().getWords().size()<=0) {
            initApplication();
        }else{
            onApplicationInited();
        }

    }

    private void initApplication() {
        new Thread() {
            @Override
            public void run() {
                try {
                    long time = System.currentTimeMillis();
                    int wordsCount = DAO.getWordsCount();
                    if (wordsCount <= 0) {
                        DAO.addDbWordsFromXml(R.xml.en_ru);
                    }
                    if (Model.getInstance().getWords().size()<=0) {
                        Model.getInstance().setWords(DAO.getAllWords());
                    }

                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            if (isAlive()) {
                                onApplicationInited();
                            }
                        }
                    });
                } catch (Throwable e) {
                    e.printStackTrace();
                    onApplicationInited();
                }
            }
        }.start();
    }

    @Override
    protected void onPause() {
        super.onPause();
        finish();
    }

    private void onApplicationInited() {
        MainActivity.show(this);
        finish();
    }
}