package ru.lazard.learnwords.learn;

import android.content.Context;
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


public class LearnPresenter {
    private final TextToSpeech ttsEn;
    private final LearnFragment fragment;
    private final Context context;
    private Handler handler;
    private boolean isPlay;
    private Settings settings;


    private Runnable playProcess = new Runnable() {
        @Override
        public void run() {

            doStep();

            int delayMillis = settings.delayBetweenWords() * 1000;
            handler.postDelayed(this, delayMillis);
        }
    };

    public void doStep() {
        Model model = InitModel.getModel(fragment.getContext());
        Word randomWord = model.getRandomWord();

        fragment.showWord(randomWord);
        if (settings.isBlinkEnable()) {
            fragment.blink();
        }
        playAudio(randomWord);
    }

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
        pause();
        unbindTTS();
    }

    public void onFloatingActionButtonClick() {
        if (isPlay){
            pause();
            return;
        }
        if (settings.isAutoWordsSwitch()){
            play();
        }else{
            doStep();
        }

    }

    public void play() {
        pause();
        this.isPlay = true;
        handler.post(playProcess);
        fragment.setStatePlay();
    }


    private void unbindTTS() {
        ttsEn.shutdown();
    }

    public void onDetach() {
        pause();

    }

    public void onPause() {
        pause();
    }

    private void pause() {
        this.isPlay = false;
        handler.removeCallbacks(playProcess);
        ttsEn.stop();
        fragment.setStatePause();
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
