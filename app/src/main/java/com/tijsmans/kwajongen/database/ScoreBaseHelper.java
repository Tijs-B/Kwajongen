package com.tijsmans.kwajongen.database;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.tijsmans.kwajongen.database.ScoreDbSchema.GameTable;
import com.tijsmans.kwajongen.database.ScoreDbSchema.ScoreTable;

public class ScoreBaseHelper extends SQLiteOpenHelper {
    private static final int VERSION = 1;
    private static final String DATABASE_NAME = "scoreBase.db";

    public ScoreBaseHelper(Context context) {
        super(context, DATABASE_NAME, null, VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table " + GameTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                GameTable.Cols.UUID + ", " +
                GameTable.Cols.DATE + ", " +
                GameTable.Cols.FINISHED +
                ")"
        );
        db.execSQL("create table " + ScoreTable.NAME + "(" +
                "_id integer primary key autoincrement, " +
                ScoreTable.Cols.UUID + ", " +
                ScoreTable.Cols.ZIJSCORE + ", " +
                ScoreTable.Cols.WIJSCORE + ", " +
                ScoreTable.Cols.PASSENSPEL + ", " +
                ScoreTable.Cols.INDEX + ", " +
                ScoreTable.Cols.GAMEID + " references " + GameTable.NAME + " (" + GameTable.Cols.UUID +
                ") on delete cascade" +
                ")"
        );
        db.execSQL("create index " + ScoreTable.GAME_ID_INDEX +
                " on " + ScoreTable.NAME + "(" + ScoreTable.Cols.GAMEID + ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
