package ru.lazard.learnwords.model.db;

import android.content.Context;
import android.content.res.XmlResourceParser;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.support.annotation.XmlRes;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import ru.lazard.learnwords.R;

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
                String dictionaryId = xml.getAttributeValue(null, "rating");
                String transcription = xml.getAttributeValue(null, "desc");
                String translate = xml.getAttributeValue(null, "translate");
                String word = xml.getAttributeValue(null, "word");
                listener.onWordParsed(Integer.parseInt(dictionaryId), transcription, translate, word);
            }
            eventType = xml.next();
        }
        xml.close();
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
