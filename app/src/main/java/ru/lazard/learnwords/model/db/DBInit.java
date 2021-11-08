package ru.lazard.learnwords.model.db;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import androidx.annotation.XmlRes;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Dictionary;

/**
 * Created by Egor on 03.06.2016.
 */
public class DBInit {

    public interface XmlParseListener {
        void onWordParsed(int dictionaryId, String transcription, String translate, String word);
    }

    public static void parseXml(Context context, @XmlRes int xmlResId, XmlParseListener listener) throws ParserConfigurationException, SAXException, XmlPullParserException, IOException {
        if (context == null) throw new IllegalArgumentException("context is null");
        if (listener == null) throw new IllegalArgumentException("listener is null");
        XmlResourceParser xml = context.getResources().getXml(R.xml.words);
        xml.getName();
        int eventType = xml.getEventType();
        while (eventType != XmlPullParser.END_DOCUMENT) {
            if (eventType == XmlPullParser.START_TAG && "word".equalsIgnoreCase(xml.getName())) {
                String dictionaryId = xml.getAttributeValue(null, "dictionaryId");
                String transcription = xml.getAttributeValue(null, "desc");
                String translate = xml.getAttributeValue(null, "translate");
                String word = xml.getAttributeValue(null, "word");
                listener.onWordParsed(Integer.parseInt(dictionaryId), transcription, translate, word);
            }
            eventType = xml.next();
        }
        xml.close();
    }

    public static void writeDictionariesToDb() throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {

        List<Dictionary> dictionaries = new ArrayList<>();
        dictionaries.add(new Dictionary("Элементарный уровень",0));
        dictionaries.add(new Dictionary("Базовый уровень",1));
        dictionaries.add(new Dictionary("Продвинутый уровень",2));
        dictionaries.add(new Dictionary("Профессиональный уровень",3));
        dictionaries.add(new Dictionary("Неправильные глаголы",4));
        dictionaries.add(new Dictionary("Основные неправильные глаголы",5));
        dictionaries.add(new Dictionary("Из книг",6));

        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();
        final SQLiteStatement statement = db.compileStatement(DBContract.Dictionaries.SQL_INSERT);
        db.beginTransaction();

        for (Dictionary dictionary : dictionaries) {
            statement.clearBindings();
            statement.bindLong(1, dictionary.getId()); // 1 _ID
            statement.bindString(2, dictionary.getName());// 2 NAME
            statement.execute();
        }


        db.setTransactionSuccessful();
        db.endTransaction();
    }


    public static void writeWordsToDb(Context context, @XmlRes int xmlResId) throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {
        SQLiteDatabase db = DbHelper.getInstance().getWritableDatabase();

        final SQLiteStatement statement = db.compileStatement(DBContract.Words.SQL_INSERT);
        db.beginTransaction();

        parseXml(context, xmlResId, new XmlParseListener() {
            @Override
            public void onWordParsed(int dictionaryId, String transcription, String translate, String word) {
                statement.clearBindings();
                statement.bindString(1, word); // 1 WORD
                statement.bindString(2, translate);// 2 TRANSLATE
                statement.bindString(3, transcription);// 3 TRANSCRIPTION
                statement.bindLong(4, dictionaryId == 0 ? 1 : 0);// 4 STATUS
                statement.bindLong(5, dictionaryId);// 5 DIFFICULTY
                statement.bindLong(6, 0);// 6 VIEW_COUNT
                statement.execute();
            }
        });

        db.setTransactionSuccessful();
        db.endTransaction();
    }
}
