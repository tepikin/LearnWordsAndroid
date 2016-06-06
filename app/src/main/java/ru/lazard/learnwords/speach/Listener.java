package ru.lazard.learnwords.speach;

import android.speech.tts.UtteranceProgressListener;

/**
 * Created by Egor on 06.06.2016.
 */
public abstract class Listener extends UtteranceProgressListener {

    public abstract void onFinish(String utteranceId) ;

    @Override
    public void onStart(String utteranceId) {

    }

    @Override
    public void onError(String utteranceId, int errorCode) {
        super.onError(utteranceId, errorCode);
    }

    @Override
    public void onDone(String utteranceId) {
        onFinish(utteranceId);
    }

    @Override
    public void onError(String utteranceId) {
        onFinish(utteranceId);
    }
    @Override
    public void onStop(String utteranceId, boolean interrupted) {
        onFinish(utteranceId);
    }
}
