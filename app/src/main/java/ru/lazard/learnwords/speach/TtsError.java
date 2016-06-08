package ru.lazard.learnwords.speach;

import android.speech.tts.TextToSpeech;

/**
 * Created by Egor on 08.06.2016.
 */
public class TtsError extends Exception {
    private int code;
    private String message;

    public int getCode() {
        return code;
    }

    public TtsError() {
    }

    public TtsError(int code) {
        this(code, "");
    }

    public TtsError(int code, String message) {
        super(getMessageByCode(code) + " code = " + code + "  message=\"" + message + "\" ");
        this.code = code;
        this.message = message;
    }

    public TtsError(Throwable e) {
        super(e);
    }

    public static String getMessageByCode(int code) {
        switch (code) {
            case TextToSpeech.ERROR_INVALID_REQUEST:
                return "Invalid request";
            case TextToSpeech.ERROR_NETWORK:
                return "Network connectivity problems";
            case TextToSpeech.ERROR_NETWORK_TIMEOUT:
                return "Network timeout";
            case TextToSpeech.ERROR_NOT_INSTALLED_YET:
                return "Unfinished download of the voice data";
            case TextToSpeech.ERROR_OUTPUT:
                return "Failure related to the output (audio device or a file)";
            case TextToSpeech.ERROR_SERVICE:
                return "Failure of a TTS service";
            case TextToSpeech.ERROR_SYNTHESIS:
                return "Failure of a TTS engine to synthesize the given input";
        }
        return "";
    }

    @Override
    public String toString() {
        return super.toString();
    }
}
