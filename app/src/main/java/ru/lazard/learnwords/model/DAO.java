package ru.lazard.learnwords.model;

import android.content.ContentValues;
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
import ru.lazard.learnwords.model.db.DBContract;
import ru.lazard.learnwords.model.db.DBInit;
import ru.lazard.learnwords.model.db.DbHelper;

/**
 * Created by Egor on 03.06.2016.
 */
class DAO {

    public static void addDbWordsFromXml(@XmlRes int xmlResId) throws ParserConfigurationException, XmlPullParserException, SAXException, IOException {
        DBInit.writeWordsToDb(BaseApplication.getInstance(), R.xml.en_ru);
    }

    public static List<Word> getAllWords() {
        Cursor cursor = getDb().rawQuery("SELECT * FROM " + DBContract.Words.TABLE_NAME, null);
        return getWordsFromCursor(cursor);
    }

    public static SQLiteDatabase getDb() {
        return DbHelper.getInstance().getReadableDatabase();
    }

    public static Word getWordById(int id) {
        Cursor cursor = getDb().rawQuery("SELECT * FROM " + DBContract.Words.TABLE_NAME + " WHERE " + DBContract.Words._ID + " = " + id, null);
        List<Word> wordsFromCursor = getWordsFromCursor(cursor);
        Word word = wordsFromCursor.size() <= 0 ? null : wordsFromCursor.get(0);
        return word;
    }

    public static int getWordsCount() {
        Cursor cursor = getDb().rawQuery("select count(*) from " + DBContract.Words.TABLE_NAME, null);
        int count = 0;
        if (cursor != null) {
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        }
        return count;
    }

    public static void updateWord(Word word) {
        if (word == null) return;
        ContentValues values=new ContentValues();
        values.put(DBContract.Words.COLUMN_NAME_DIFFICULTY,word.getDifficulty());
        values.put(DBContract.Words.COLUMN_NAME_STATUS,word.getStatus());
        values.put(DBContract.Words.COLUMN_NAME_TRANSCRIPTION,word.getTranscription());
        values.put(DBContract.Words.COLUMN_NAME_TRANSLATE,word.getTranslate());
        values.put(DBContract.Words.COLUMN_NAME_VIEW_COUNT,word.getViewCount());
        values.put(DBContract.Words.COLUMN_NAME_WORD,word.getWord());
        getDb().update(DBContract.Words.TABLE_NAME,values,DBContract.Words._ID+" = ?",new String[]{String.valueOf(word.getId())} );
    }


    public static void insertWord(Word word) {
        if (word == null) return;
        //WORD TRANSLATE TRANSCRIPTION STATUS DIFFICULTY VIEW_COUNT
        getDb().execSQL(DBContract.Words.SQL_INSERT, new Object[]{word.getWord(),word.getTranslate(),word.getTranscription(),word.getStatus(),word.getDifficulty(),word.getViewCount()});
    }

    public static void setLearnWordsListAll() {
        getDb().execSQL("UPDATE " + DBContract.Words.TABLE_NAME + " SET " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_LEARN + " WHERE " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_NONE + " | " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_LEARN);
    }

    public static void setLearnWordsListByDifficult(int difficulty) {
        setLearnWordsListClear();
        getDb().execSQL("UPDATE " + DBContract.Words.TABLE_NAME + " SET " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_LEARN + " WHERE (" + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_NONE + " | " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_LEARN + ") AND " + DBContract.Words.COLUMN_NAME_DIFFICULTY + " = " + difficulty);
    }

    public static void setLearnWordsListClear() {
        getDb().execSQL("UPDATE " + DBContract.Words.TABLE_NAME + " SET " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_NONE + " WHERE " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_NONE + " | " + DBContract.Words.COLUMN_NAME_STATUS + " = " + Word.STATUS_LEARN);
    }
    
    public static void setStatusForAllWords(int status) {
        getDb().execSQL("UPDATE " + DBContract.Words.TABLE_NAME + " SET " + DBContract.Words.COLUMN_NAME_STATUS + " = " + status);
    }

    public static void setStatusForWord(int id, int status) {
        getDb().execSQL("UPDATE " + DBContract.Words.TABLE_NAME + " SET " + DBContract.Words.COLUMN_NAME_STATUS + " = " + status + " WHERE " + DBContract.Words._ID + " = " + id);
    }

    public static void setStatusForWords(int status, List<Word> randomSubItems) {
        if (randomSubItems == null) return;
        if (randomSubItems.size() <= 0) return;

        getDb().beginTransaction();
        for (Word randomSubItem : randomSubItems) {
            setStatusForWord(randomSubItem.getId(), status);
        }
        getDb().setTransactionSuccessful();
        getDb().endTransaction();
    }

    public static void removeById(int id) {
        getDb().delete(DBContract.Words.TABLE_NAME,DBContract.Words._ID+" = ?",new String[]{String.valueOf(id)});
    }
    
    private static List<Word> getWordsFromCursor(Cursor cursor) {
        List<Word> list = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int columnIndexWord = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_WORD);
                int columnIndexTranscription = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_TRANSCRIPTION);
                int columnIndexTranslate = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_TRANSLATE);
                int columnIndexViewCount = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_VIEW_COUNT);
                int columnIndexDifficulty = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_DIFFICULTY);
                int columnIndexStatus = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_STATUS);
                int columnIndexId = cursor.getColumnIndex(DBContract.Words._ID);

                do {

                    String englishWord = cursor.getString(columnIndexWord);
                    String transcriptionWord = cursor.getString(columnIndexTranscription);
                    String translateWord = cursor.getString(columnIndexTranslate);
                    int viewCount = cursor.getInt(columnIndexViewCount);
                    int id = cursor.getInt(columnIndexId);
                    int status = cursor.getInt(columnIndexStatus);
                    int difficulty = cursor.getInt(columnIndexDifficulty);

                    Word word = new Word();
                    word.setWord(englishWord);
                    word.setTranscription(transcriptionWord);
                    word.setTranslate(translateWord);
                    word.setId(id);
                    word.setViewCount(viewCount);
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
