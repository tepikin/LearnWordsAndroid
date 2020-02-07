package ru.lazard.learnwords.speach;

import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.speech.tts.TextToSpeech;
import android.support.annotation.FloatRange;
import android.support.design.widget.Snackbar;
import android.text.TextUtils;
import android.view.View;

import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import kotlin.text.Regex;
import ru.lazard.learnwords.R;
import ru.lazard.learnwords.ui.BaseActivity;
import ru.lazard.learnwords.ui.activities.main.MainActivity;
import ru.lazard.learnwords.ui.fragments.preferences.Settings;
import ru.lazard.learnwords.utils.Utils;

/**
 * Created by Egor on 08.06.2016.
 */
public class TTS implements BaseActivity.OnActivityResultListener, BaseActivity.OnActivityDestroyListener, TextToSpeech.OnInitListener {
    private static final int KEY_CHECK_TTS = 867;
    private final MainActivity activity;
    private TextToSpeech mTts;
    private Settings settings;

    public TTS(MainActivity activity) {
        this.activity = activity;
        settings = new Settings(activity);
        activity.addOnActivityResult(this);
        activity.addOnActivityDestroyListener(this);
    }

    public void checkTTS() {

        Intent checkTTSIntent = new Intent();
        checkTTSIntent.setAction(TextToSpeech.Engine.ACTION_CHECK_TTS_DATA);

        List<ResolveInfo> resInfo = activity.getPackageManager()
                .queryIntentActivities(checkTTSIntent, 0);
        if (resInfo.isEmpty()) {
            onNoEnginesInstalled();
            return;
        }

        activity.startActivityForResult(checkTTSIntent, KEY_CHECK_TTS);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == KEY_CHECK_TTS) {
            if (resultCode == TextToSpeech.Engine.CHECK_VOICE_DATA_PASS) {
                mTts = new TextToSpeech(activity, this);
            } else {
                onNoTtsVoicesInstalled();
            }
        }
    }

    @Override
    public void onDestroy() {
        if (mTts != null) mTts.shutdown();
    }

    @Override
    public void onInit(int status) {
        if (status == TextToSpeech.SUCCESS) {
            //do nothing
            onTtsCreated();
        } else if (status == TextToSpeech.ERROR) {
            onTtsNotCreated();
        }
    }

    public void speak(final String text, final Runnable callback) {
        if (TextUtils.isEmpty(text)) {
            if (callback != null) callback.run();
            return;
        }
        boolean isEnglishLanguage = Utils.isTextEn(text);
        if (isEnglishLanguage){
            speak(text,settings.getSpeedReadWords() , Locale.ENGLISH, callback);
        }else{
            speak(text,settings.getSpeedReadTranslate() , Locale.getDefault(), callback);
        }

    }

    public void speak(final String textIn, @FloatRange(from = 0, to = 2) float speechRate, Locale locale, final Runnable callback) {
        if (mTts == null) {
            if (callback != null) callback.run();
            return;
        }
        if (TextUtils.isEmpty(textIn)) {
            if (callback != null) callback.run();
            return;
        }
        if (textIn.replaceAll("[^\\w]","").length()==0) {
            if (callback != null) callback.run();
            return;
        }
        final String text = textIn.replaceAll("^[^\\w]","");

        if (speechRate < 0) {
            speechRate = 0;
            return;
        }
        if (speechRate > 2) {
            speechRate = 2;
            return;
        }
        if (locale == null) {
            locale = Locale.getDefault();
        }
        try {

            mTts.setSpeechRate(speechRate);
            mTts.setLanguage(locale);
            mTts.setOnUtteranceProgressListener(new Listener() {
                @Override
                public void onError(String utteranceId, int errorCode) {
                    super.onError(utteranceId, errorCode);
                    onSpeakError(new TtsError(errorCode, utteranceId));
                }

                @Override
                public void onFinish(String utteranceId) {
                    if (utteranceId.equals(text)) {
                        if (callback != null) callback.run();
                    }
                }
            });

            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, text);
            mTts.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        } catch (Throwable e) {
            onSpeakError(new TtsError(e));
            callback.run();
        }
    }

    public void stop() {
        if (mTts != null) {
            mTts.stop();
        }
    }

    private void onNoEnginesInstalled() {
        mTts = null;
        Snackbar snackbar = Snackbar.make(activity.getCoordinatorLayout(), R.string.mainActivity_tts_noTtsEnginesInstalled, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.mainActivity_tts_noTtsEnginesInstalled_install, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showMarketPage("com.google.android.tts", activity, activity.getString(R.string.navigation_menu_rate_exception_marketNotExist));
            }
        });
        snackbar.show();
    }

    private void onNoTtsVoicesInstalled() {
        mTts = null;
        Snackbar snackbar = Snackbar.make(activity.getCoordinatorLayout(), R.string.mainActivity_tts_noTtsVoicesInstalled, Snackbar.LENGTH_LONG);
        snackbar.setAction(R.string.mainActivity_tts_noTtsVoicesInstalled_install, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent installIntent = new Intent();
                installIntent.setAction(TextToSpeech.Engine.ACTION_INSTALL_TTS_DATA);

                List<ResolveInfo> resInfo = activity.getPackageManager()
                        .queryIntentActivities(installIntent, 0);
                if (resInfo.isEmpty()) {
                    Utils.showMarketPage("com.google.android.tts", activity, activity.getString(R.string.navigation_menu_rate_exception_marketNotExist));
                    return;
                }

                activity.startActivity(installIntent);
            }
        });
        snackbar.show();
    }

    private void onSpeakError(TtsError ttsError) {
        if (ttsError != null) {
            ttsError.printStackTrace();
            String message = TtsError.getMessageByCode(ttsError.getCode());

            Snackbar snackbar = Snackbar.make(activity.getCoordinatorLayout(), message, Snackbar.LENGTH_LONG);
            snackbar.setAction(R.string.mainActivity_tts_cantInitTTS_retry, new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new Settings(activity).setReadWords(false);
                }
            });
            snackbar.show();
        }
    }

    private void onTtsCreated() {
        if (mTts == null) {
            onTtsNotCreated();
            return;
        }
//        List<TextToSpeech.EngineInfo> engines = mTts.getEngines();
//        if (engines == null || engines.size() <= 0) {
//            mTts = null;
//            onNoEnginesInstalled();
//            return;
//        }
        // TODO un implemented yaeat

    }

    private void onTtsNotCreated() {
        mTts = null;
        Snackbar snackbar = Snackbar.make(activity.getCoordinatorLayout(), R.string.mainActivity_tts_cantInitTTS, Snackbar.LENGTH_INDEFINITE);
        snackbar.setAction(R.string.mainActivity_tts_cantInitTTS_retry, new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                checkTTS();
            }
        });
        snackbar.show();
    }
}
