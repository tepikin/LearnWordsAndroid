package ru.lazard.learnwords.ui.fragments.learn;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.speech.tts.TextToSpeech;
import android.support.annotation.FloatRange;
import android.text.TextUtils;

import java.util.HashMap;
import java.util.Locale;
import java.util.concurrent.atomic.AtomicBoolean;

import ru.lazard.learnwords.db.DAO;
import ru.lazard.learnwords.model.Word;
import ru.lazard.learnwords.speach.Listener;
import ru.lazard.learnwords.ui.fragments.preferences.Settings;


public class LearnPresenter {
    private final TextToSpeech ttsEn;
    private final LearnFragment fragment;
    private final Context context;
    private Handler handler;
    private boolean isPlay;
    private Word randomWord;
    private Settings settings;
    private AtomicBoolean cancelSync = new AtomicBoolean(false);


    private Runnable playProcess = new Runnable() {
        @Override
        public void run() {

            doStep();
        }
    };

    public LearnPresenter(LearnFragment mainActivity) {
        this.fragment = mainActivity;
        context = fragment.getContext();
        settings = new Settings(context);
        handler = new Handler(Looper.getMainLooper());
        ttsEn = new TextToSpeech(context, new TextToSpeech.OnInitListener() {
            @Override
            public void onInit(int status) {
                if (status != TextToSpeech.ERROR) {
                    ttsEn.setLanguage(Locale.ENGLISH);
                }
            }
        });
        randomWord = DAO.getRandomWord();
        fragment.showWord(randomWord);
    }

    public void doStep() {

        randomWord = DAO.getRandomWord();

        startWord(randomWord);
    }

    private void startWord(Word randomWord) {
        fragment.showWord(randomWord);
        if (settings.isBlinkEnable()) {
            fragment.blink();
        }
        if (settings.isReadWords()) {
            playWord(randomWord, new Runnable() {
                @Override
                public void run() {
                    onStepDone();
                }
            });
        } else {
            onStepDone();
        }
    }

    public void onDestroy() {
        pause();
        unbindTTS();
    }

    public void onDetach() {
        pause();
    }

    public void onFloatingActionButtonClick() {
        if (isPlay) {
            pause();
            return;
        }
        if (settings.isAutoWordsSwitch()) {
            play();
        } else {
            doStep();
        }
    }

    public void onPause() {
        pause();
    }

    public void play() {
        pause();
        this.isPlay = true;
        startWord(randomWord);//doStep();

        fragment.setStatePlay();
    }

    private void onStepDone() {
        if (isPlay) {
            int delayMillis = settings.delayBetweenWords() * 1000 + 500;
            handler.postDelayed(playProcess, delayMillis);
        }
    }

    private void pause() {
        this.isPlay = false;
        cancelSync.set(true);
        cancelSync=new AtomicBoolean(false);
        handler.removeCallbacks(playProcess);
        ttsEn.stop();
        fragment.setStatePause();
    }

    private void playText(String text, @FloatRange(from = 0, to = 2) float speechRate, Locale locale, final Runnable callback) {
        if (TextUtils.isEmpty(text)) {
            if (callback != null) callback.run();
            return;
        }
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
            ttsEn.setSpeechRate(speechRate);
            ttsEn.setLanguage(locale);
            ttsEn.setOnUtteranceProgressListener(new Listener() {
                @Override
                public void onFinish(String utteranceId) {
                    if (callback != null) callback.run();
                }

            });

            HashMap<String, String> myHashAlarm = new HashMap<String, String>();
            myHashAlarm.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "1");
            ttsEn.speak(text, TextToSpeech.QUEUE_FLUSH, myHashAlarm);

//            ttsEn.speak(text, TextToSpeech.QUEUE_FLUSH, null);

        } catch (Throwable e) {
            e.printStackTrace();
            callback.run();
        }
    }


    private void playWord(final Word randomWord, final Runnable callback) {
        if (randomWord == null) {
            if (callback != null) callback.run();
            return;
        }
        final AtomicBoolean cancel = cancelSync;
        playText(randomWord.getWord(), settings.speedReadWords(), Locale.ENGLISH, new Runnable() {
            @Override
            public void run() {
                if (cancel.get()){  if (callback != null) callback.run();
                    return;
                }
                if (!settings.isReadTranslate()) {
                    if (callback != null) callback.run();
                    return;
                }
                playText(randomWord.getTranslate(), settings.speedReadTranslate(), null, callback);
            }
        });
    }

    private void unbindTTS() {
        ttsEn.shutdown();
    }
}
