package ru.lazard.learnwords.model.db;

import android.provider.BaseColumns;

/**
 * Created by Egor on 03.06.2016.
 */
public class DBContract {
    private static final String TYPE_TEXT = " TEXT";
    private static final String TYPE_INTEGER = " INTEGER";
    private static final String TYPE_BOOLEAN = " INTEGER";
    private static final String COMMA_SEP = ", ";

    public static abstract class Dictionaries implements BaseColumns {
        public static final String TABLE_NAME = "dictionaries";
        public static final String COLUMN_NAME = "name";
        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + Dictionaries.TABLE_NAME;
        public static final String SQL_CREATE_TABLE =
                "CREATE TABLE " + Dictionaries.TABLE_NAME + " (" +
                        Dictionaries._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Dictionaries.COLUMN_NAME + TYPE_TEXT + " )";
        public static final String SQL_INSERT = "INSERT INTO " + DBContract.Dictionaries.TABLE_NAME + " ( " +
                Dictionaries._ID+COMMA_SEP + DBContract.Dictionaries.COLUMN_NAME + " ) VALUES (?,?);";
    }

    public static abstract class Words implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_TRANSCRIPTION = "transcription";
        public static final String COLUMN_NAME_TRANSLATE = "translate";
        public static final String COLUMN_NAME_VIEW_COUNT = "view_count";
        public static final String COLUMN_NAME_STATUS = "status";
        public static final String COLUMN_NAME_DICTIONARY_ID = "dictionary_id";


        public static final String SQL_DELETE_TABLE =
                "DROP TABLE IF EXISTS " + Words.TABLE_NAME;
        public static final String SQL_CREATE_TABLE =
                //"CREATE VIRTUAL TABLE " + Words.TABLE_NAME + " USING fts4(" +
                "CREATE TABLE " + Words.TABLE_NAME + " (" +
                        Words._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Words.COLUMN_NAME_WORD + TYPE_TEXT + COMMA_SEP +
                        Words.COLUMN_NAME_TRANSCRIPTION + TYPE_TEXT + COMMA_SEP +
                        Words.COLUMN_NAME_TRANSLATE + TYPE_TEXT + COMMA_SEP +
                        Words.COLUMN_NAME_VIEW_COUNT + TYPE_INTEGER + COMMA_SEP +
                        Words.COLUMN_NAME_STATUS + TYPE_INTEGER + COMMA_SEP +
                        Words.COLUMN_NAME_DICTIONARY_ID + TYPE_INTEGER +
                        " )";
        public static final String SQL_INSERT = "INSERT INTO " + DBContract.Words.TABLE_NAME + " ( " +
                DBContract.Words.COLUMN_NAME_WORD + " , " +
                DBContract.Words.COLUMN_NAME_TRANSLATE + " , " +
                DBContract.Words.COLUMN_NAME_TRANSCRIPTION + " , " +
                DBContract.Words.COLUMN_NAME_STATUS + " , " +
                DBContract.Words.COLUMN_NAME_DICTIONARY_ID + " , " +
                DBContract.Words.COLUMN_NAME_VIEW_COUNT +
                " ) VALUES (?,?,?,?,?,?);";
    }


}
