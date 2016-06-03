package ru.lazard.learnwords.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.ArrayList;
import java.util.List;

import ru.lazard.learnwords.model.Word;

/**
 * Created by Egor on 03.06.2016.
 */
public class DAO {

    public static SQLiteDatabase getDb() {
        return DbHelper.getInstance().getReadableDatabase();
    }

    public static Word getRandomWord() {
        Cursor cursor = getDb().rawQuery("SELECT * FROM "+DBContract.Words.TABLE_NAME+" ORDER BY RANDOM() LIMIT 1;", null);
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

    private static List<Word> getWordsFromCursor(Cursor cursor) {
        List<Word> list = new ArrayList<>();
        if (cursor != null) {
            if (cursor.moveToFirst()) {

                int columnIndexWord = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_WORD);
                int columnIndexTranslate = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_TRANSLATE);
                int columnIndexViewCount = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_VIEW_COUNT);
                int columnIndexVisible = cursor.getColumnIndex(DBContract.Words.COLUMN_NAME_VISIBLE);
                int columnIndexId = cursor.getColumnIndex(DBContract.Words._ID);

                do {

                    String englishWord = cursor.getString(columnIndexWord);
                    String translateWord = cursor.getString(columnIndexTranslate);
                    int viewCount = cursor.getInt(columnIndexViewCount);
                    int visible = cursor.getInt(columnIndexVisible);
                    int id = cursor.getInt(columnIndexId);

                    Word word = new Word();
                    word.setWord(englishWord);
                    word.setTranslate(translateWord);
                    word.setId(id);
                    word.setViewCount(viewCount);
                    word.setVisible(visible > 0);

                    list.add(word);
                } while (cursor.moveToNext());
            }
            cursor.close();
        }
        return list;
    }
}
