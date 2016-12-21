package ru.lazard.learnwords.model.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by Egor on 03.06.2016.
 */
public class DbHelper extends SQLiteOpenHelper {
    public static final int DATABASE_VERSION = 1;
    public static final String DATABASE_NAME = "words.db";
    private static DbHelper instance;
private Context context;
    public DbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.context = context;
    }

    public static final DbHelper getInstance() {
        if (instance == null) throw new IllegalStateException("call DbHelper.init(); first;");
        return instance;
    }

    public static void init(Context context) {
        if (instance!=null)return;
        instance = new DbHelper(context);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(DBContract.Dictionaries.SQL_CREATE_TABLE);
        db.execSQL(DBContract.Words.SQL_CREATE_TABLE);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(DBContract.Words.SQL_DELETE_TABLE);
//        onCreate(db);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
//        db.execSQL(DBContract.Words.SQL_DELETE_TABLE);
//        onCreate(db);
    }
}