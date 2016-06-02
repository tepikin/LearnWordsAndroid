package ru.lazard.learnwords;

import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import java.util.HashMap;
import java.util.Locale;

import ru.lazard.learnwords.model.InitModel;
import ru.lazard.learnwords.model.Model;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.preferences.Settings;

/**
 * Created by Egor on 02.06.2016.
 */
public class MainPresenter {
    private final TextToSpeech ttsEn;
    private final MainActivity activity;
    private Handler handler;
    private boolean isPlay;
    private Settings settings;
    private float speechRateEnglish = 0.3f;
    private float speechRateNational = 1f;
    private long wordsDelay = 10000;
    private Runnable playProcess = new Runnable() {
        @Override
        public void run() {

            Model model = InitModel.getModel(activity);
            Word randomWord = model.getRandomWord();

            activity.showWord(randomWord);
            if (settings.isBlinkEnable()) {
                activity.blink();
            }
            playAudio(randomWord);

            handler.postDelayed(this, wordsDelay);
        }
    };

    public MainPresenter(MainActivity mainActivity) {
        this.activity = mainActivity;
        settings = new Settings(activity);
        handler = new Handler(Looper.getMainLooper());
        ttsEn = new TextToSpeech(activity, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttsEn.setLanguage(Locale.ENGLISH);
                    ttsEn.setOnUtteranceProgressListener(new UtteranceProgressListener() {
                        @Override
                        public void onDone(String utteranceId) {
                            if (!settings.isReadTranslate())return;
                            ttsEn.setSpeechRate(speechRateNational);
                            ttsEn.setLanguage(Locale.getDefault());
                            ttsEn.speak(utteranceId, TextToSpeech.QUEUE_FLUSH, null);
                        }

                        @Override
                        public void onError(String utteranceId) {
                        }

                        @Override
                        public void onStart(String utteranceId) {
                        }
                    });
                }
            }
        });
    }

    public void onDestroy() {
        setPlay(false);
    }



    public void setPlay(boolean isPlay) {
        this.isPlay = isPlay;
        handler.removeCallbacks(playProcess);
        if (isPlay) {
            handler.post(playProcess);
        }
    }


    private void playAudio(Word randomWord) {
        if (!settings.isReadWords()) return;
        try {
            ttsEn.setSpeechRate(speechRateEnglish);
            ttsEn.setLanguage(Locale.ENGLISH);
            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, randomWord.getTranslate());
            ttsEn.speak(randomWord.getWord(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
