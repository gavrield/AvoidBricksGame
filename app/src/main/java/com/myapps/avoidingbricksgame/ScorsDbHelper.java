package com.myapps.avoidingbricksgame;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class ScorsDbHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "Score.db";
    private static final String SQL_CREATE_ENTRIES =
            "CREATE TABLE " + ScoresContract.Scores.TABLE_NAME + " (" +
                    ScoresContract.Scores._ID + " INTEGER PRIMARY KEY," +
                    ScoresContract.Scores.COLUMN_NAME_NAME + " TEXT," +
                    ScoresContract.Scores.COLUMN_NAME_SCORE + " INTEGER," +
                    ScoresContract.Scores.COLUMN_NAME_LAT + " DOUBLE," +
                    ScoresContract.Scores.COLUMN_NAME_LONG + " DOUBLE)";

    private static final String SQL_DELETE_ENTRIES =
            "DROP TABLE IF EXISTS " + ScoresContract.Scores.TABLE_NAME;

    public ScorsDbHelper(Context context){
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(SQL_CREATE_ENTRIES);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(SQL_DELETE_ENTRIES);
        onCreate(db);
    }
}
