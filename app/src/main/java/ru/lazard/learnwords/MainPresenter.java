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


public class MainPresenter {
    private final TextToSpeech ttsEn;
    private final MainActivity activity;
    private Handler handler;
    private boolean isPlay;
    private Settings settings;


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

            int delayMillis = settings.delayBetweenWords() * 1000;
            handler.postDelayed(this, delayMillis);
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
                            float speechRate = settings.speedReadTranslate();
                            ttsEn.setSpeechRate(speechRate);
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
            float speechRate =  settings.speedReadWords() ;
            ttsEn.setSpeechRate(speechRate);
            ttsEn.setLanguage(Locale.ENGLISH);
            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, randomWord.getTranslate());
            ttsEn.speak(randomWord.getWord(), TextToSpeech.QUEUE_FLUSH, myHashAlarm);
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }
}
