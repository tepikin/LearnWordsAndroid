package ru.lazard.learnwords.ui;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;

import java.util.ArrayList;
import java.util.List;


public class BaseActivity extends AppCompatActivity {
    public interface BackPressListener {

        boolean onBackPressed();
    }

    public interface OnActivityPauseListener {

        void onPause();
    }

    public interface OnActivityDestroyListener {

        void onDestroy();
    }

    public interface OnActivityResultListener {

        void onActivityResult(int requestCode, int resultCode, Intent data);
    }

    public interface OnActivityResumeListener {

        void onResume();
    }
    private static final String ACTION_CLOSE_ALL_BASE_ACTIVITIES = "ru.beliked.ui.BaseActivity.ACTION_CLOSE_ALL_BASE_ACTIVITIES";
    protected boolean isRessumedNow = true;
    private List<OnActivityDestroyListener> activityDestroyListeners = new ArrayList<OnActivityDestroyListener>();
    private List<OnActivityPauseListener> activityPauseListeners = new ArrayList<OnActivityPauseListener>();
    private List<OnActivityResultListener> activityResultListeners = new ArrayList<OnActivityResultListener>();
    private List<OnActivityResumeListener> activityResumeListeners = new ArrayList<OnActivityResumeListener>();
    private BroadcastReceiver closeReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            try {
                finish();
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }
    };
    private boolean isDestroed = false;

    public static void closeForce(Class clazz, Context context) {
        context.sendBroadcast(new Intent(className(clazz)));
    }

    public static void closeForceAll(Context context) {
        context.sendBroadcast(new Intent(ACTION_CLOSE_ALL_BASE_ACTIVITIES));
    }

    public void addOnActivityDestroyListener(OnActivityDestroyListener listener) {
        activityDestroyListeners.add(listener);
    }

    public void addOnActivityPauseListener(OnActivityPauseListener listener) {
        activityPauseListeners.add(listener);
    }

    public void addOnActivityResult(
            OnActivityResultListener onActivityResultListener) {
        activityResultListeners.add(onActivityResultListener);
    }

    public void addOnActivityResumeListener(OnActivityResumeListener listener) {
        activityResumeListeners.add(listener);
    }

    public <T> T findById(int id) {
        return (T) super.findViewById(id);
    }

    public boolean isDestroed() {
        return isDestroed;
    }

    public boolean isRessumedNow() {
        return isRessumedNow;
    }

    @Override
    public void onStart() {
        super.onStart();
//        BaseApplication.getInstance().getActivityResumeAdListener()
//                .onStartBase(this);
    }

    @Override
    public void onStop() {
        super.onStop();
//        BaseApplication.getInstance().getActivityResumeAdListener()
//                .onStopBase(this);
    }

    public void removeOnActivityResult(
            OnActivityResultListener onActivityResultListener) {
        activityResultListeners.remove(onActivityResultListener);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        for (OnActivityResultListener activityResultListener : activityResultListeners) {
            activityResultListener.onActivityResult(requestCode, resultCode,
                    data);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        try {
            Class.forName("android.os.AsyncTask");
        } catch (Throwable ignore) {
            // ignored
        }

        super.onCreate(savedInstanceState);

        try {
            IntentFilter intentFilter = new IntentFilter(className(getClass()));
            intentFilter.addAction(ACTION_CLOSE_ALL_BASE_ACTIVITIES);
            registerReceiver(closeReceiver, intentFilter);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {

        for (OnActivityDestroyListener listener : activityDestroyListeners) {
            if (listener != null) {
                listener.onDestroy();
            }
        }
        try {
            unregisterReceiver(closeReceiver);
        } catch (Throwable e) {
            e.printStackTrace();
        }
        super.onDestroy();
        isDestroed = true;
    }

    @Override
    protected void onPause() {
        isRessumedNow = false;
        super.onPause();
        for (OnActivityPauseListener listener : activityPauseListeners) {
            if (listener != null) {
                listener.onPause();
            }
        }
    }

    @Override
    protected void onResume() {
        isRessumedNow = true;
        // BaseApplication.getInstance().get

        super.onResume();

        for (OnActivityResumeListener listener : activityResumeListeners) {
            if (listener != null) {
                listener.onResume();
            }
        }

        // showAdsOnRessume();

    }

    @NonNull
    private static String className(Class clazz) {
        return "close_action" + clazz.getName();
    }
}
