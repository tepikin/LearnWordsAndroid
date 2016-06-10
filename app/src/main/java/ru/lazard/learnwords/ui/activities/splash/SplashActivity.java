package ru.lazard.learnwords.ui.activities.splash;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;

import com.crashlytics.android.Crashlytics;
import io.fabric.sdk.android.Fabric;
import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.ui.activities.main.MainActivity;

public class SplashActivity extends AppCompatActivity {


    private boolean isAlive = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Fabric.with(this, new Crashlytics());
        setContentView(R.layout.activity_splash);

        if (Model.getInstance().getWords().size() <= 0) {
            initApplication();
        } else {
            onApplicationInited();
        }
    }


    @Override
    protected void onDestroy() {
        super.onDestroy();
        isAlive = false;

    }

    @Override
    protected void onResume() {
        super.onResume();
        isAlive = true;
    }

    private void initApplication() {
        new Thread() {
            @Override
            public void run() {
                try {
                    Model.getInstance().initDatabase();
                } catch (Throwable e) {
                    new Handler(Looper.getMainLooper()).post(new Runnable() {
                        @Override
                        public void run() {
                            try {
                                onNotInited();
                            } catch (Throwable e) {
                                e.printStackTrace();
                            }
                        }
                    });
                }
                new Handler(Looper.getMainLooper()).post(new Runnable() {
                    @Override
                    public void run() {
                        try {
                            onApplicationInited();
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
                });
            }
        }.start();
    }

    private void onNotInited() {
        Toast.makeText(SplashActivity.this, R.string.splash_error_cantCreateDb, Toast.LENGTH_LONG).show();
        finish();
    }

    private void onApplicationInited() {
        if (isAlive) {
            MainActivity.show(this);
        }
        finish();
    }
}
