package ru.lazard.learnwords.db;

import android.provider.BaseColumns;

/**
 * Created by Egor on 03.06.2016.
 */
public class DBContract {
    public static abstract class Words implements BaseColumns {
        public static final String TABLE_NAME = "words";
        public static final String COLUMN_NAME_WORD = "word";
        public static final String COLUMN_NAME_TRANSLATE = "translate";
        public static final String COLUMN_NAME_VIEW_COUNT = "view_count";
        public static final String COLUMN_NAME_VISIBLE = "visible";


        private static final String TEXT_TYPE = " TEXT";
        private static final String TEXT_INTEGER = " INTEGER";
        private static final String TEXT_BOOLEAN = " INTEGER";
        private static final String COMMA_SEP = ", ";
        public static final String SQL_DELETE_WORDS =
                "DROP TABLE IF EXISTS " + Words.TABLE_NAME;
        public static final String SQL_CREATE_WORDS =
                //"CREATE VIRTUAL TABLE " + Words.TABLE_NAME + " USING fts4(" +
                        "CREATE TABLE " + Words.TABLE_NAME + " (" +
                        Words._ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        Words.COLUMN_NAME_WORD + TEXT_TYPE + COMMA_SEP +
                        Words.COLUMN_NAME_TRANSLATE + TEXT_TYPE + COMMA_SEP +
                        Words.COLUMN_NAME_VIEW_COUNT + TEXT_INTEGER + COMMA_SEP +
                        Words.COLUMN_NAME_VISIBLE + TEXT_BOOLEAN +
                        " )";
        public static final String SQL_INSERT = "INSERT INTO " + DBContract.Words.TABLE_NAME + " ( " +
                DBContract.Words.COLUMN_NAME_WORD + " , " +
                DBContract.Words.COLUMN_NAME_TRANSLATE + " , " +
                DBContract.Words.COLUMN_NAME_VISIBLE + " , " +
                DBContract.Words.COLUMN_NAME_VIEW_COUNT +
                " ) VALUES (?,?,?,?);";
    }




}
