package ru.lazard.learnwords.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.annotation.XmlRes;

import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlPullParserException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.ParserConfigurationException;

import ru.lazard.learnwords.BaseApplication;
import ru.lazard.learnwords.R;
import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class DAO {

    public static void addDbWordsFromXml(@XmlRes int xmlResId) throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {
        DBInit.writeWordsToDb(BaseApplication.getInstance(), R.xml.en_ru);
    }

    public static SQLiteDatabase getDb() {
        return DbHelper.getInstance().getReadableDatabase();
    }

    public static Word getRandomWord() {
        Cursor cursor = getDb().rawQuery("SELECT * FROM "+DBContract.Words.TABLE_NAME+" WHERE "+DBContract.Words.COLUMN_NAME_VISIBLE+" = 1 ORDER BY RANDOM() LIMIT 1;", null);
        List<Word> wordsFromCursor = getWordsFromCursor(cursor);
        Word word = wordsFromCursor.size() <= 0 ? null : wordsFromCursor.get(0);
        return word;
    }

    public static Word getWordById(int id) {
        Cursor cursor = getDb().rawQuery("SELECT * FROM "+DBContract.Words.TABLE_NAME+" WHERE "+DBContract.Words._ID+" = "+id, null);
        List<Word> wordsFromCursor = getWordsFromCursor(cursor);
        Word word = wordsFromCursor.size() <= 0 ? null : wordsFromCursor.get(0);
        return word;
    }


    public static int getWordsCount() {
        Cursor cursor = getDb().rawQuery("select count(*) from "+DBContract.Words.TABLE_NAME, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public static List<Word> getAllWords() {
        Cursor cursor = getDb().rawQuery("SELECT * FROM "+DBContract.Words.TABLE_NAME, null);
        return getWordsFromCursor(cursor);
    }

    public static void setVisibleForAllWords(boolean isVisible) {
        getDb().execSQL("UPDATE "+DBContract.Words.TABLE_NAME+" SET "+DBContract.Words.COLUMN_NAME_VISIBLE +" = "+(isVisible?1:0) );
    }

    public static void setSelectionForWord(int id, boolean isVisible) {
        getDb().execSQL("UPDATE "+DBContract.Words.TABLE_NAME+" SET "+DBContract.Words.COLUMN_NAME_VISIBLE +" = "+(isVisible?1:0)+" WHERE "+DBContract.Words._ID+" = "+id );

    }

    private static List<Word> getWordsFromCursor(Cursor cursor) {
        List<Word> list = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int columnIndexWord = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_WORD);
                int columnIndexTranscription = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_TRANSCRIPTION);
                int columnIndexTranslate = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_TRANSLATE);
                int columnIndexViewCount = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_VIEW_COUNT);
                int columnIndexVisible = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_VISIBLE);
                int columnIndexDifficulty = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_DIFFICULTY);
                int columnIndexStatus = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_STATUS);
                int columnIndexId = cursor.getColumnIndex(DBContract.Words._ID);

                do {

                    String englishWord = cursor.getString(columnIndexWord);
                    String transcriptionWord = cursor.getString(columnIndexTranscription);
                    String translateWord = cursor.getString(columnIndexTranslate);
                    int viewCount = cursor.getInt(columnIndexViewCount);
                    int visible = cursor.getInt(columnIndexVisible);
                    int id = cursor.getInt(columnIndexId);
                    int status = cursor.getInt(columnIndexStatus);
                    int difficulty= cursor.getInt(columnIndexDifficulty);

                    Word word = new Word();
                    word.setWord(englishWord);
                    word.setTranscription(transcriptionWord);
                    word.setTranslate(translateWord);
                    word.setId(id);
                    word.setViewCount(viewCount);
                    word.setVisible(visible > 0);
                    word.setStatus(status);
                    word.setDifficulty(difficulty);

                    list.add(word);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}
