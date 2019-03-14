package com.tijsmans.kwajongen;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

import com.tijsmans.kwajongen.database.GameCursorWrapper;
import com.tijsmans.kwajongen.database.ScoreBaseHelper;
import com.tijsmans.kwajongen.database.ScoreCursorWrapper;
import com.tijsmans.kwajongen.database.ScoreDbSchema.GameTable;
import com.tijsmans.kwajongen.database.ScoreDbSchema.ScoreTable;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.UUID;

public class ScoreLab {

    private static final String TAG = "ScoreLab";

    private static ScoreLab sScoreLab;

    private Context mContext;
    private SQLiteDatabase mDatabase;

    public static ScoreLab get(Context context) {
        if (sScoreLab == null) {
            sScoreLab = new ScoreLab(context);
        }
        return sScoreLab;
    }

    private ScoreLab(Context context) {
        mContext = context.getApplicationContext();
        mDatabase = new ScoreBaseHelper(mContext).getWritableDatabase();
    }

    public List<Game> getGames() {
        List<Game> games = new ArrayList<>();
        GameCursorWrapper cursor = queryGames(
                null,
                null,
                null,
                null,
                null,
                null
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                games.add(cursor.getGame());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }
        return games;
    }

    public Game getLatestGame() {
        GameCursorWrapper cursor = queryGames(
                GameTable.Cols.FINISHED + " = 0",
                null,
                null,
                null,
                GameTable.Cols.DATE + " DESC",
                "1"
        );

        try {
            if (cursor.getCount() == 0)
                return null;
            cursor.moveToFirst();
            return cursor.getGame();
        } finally {
            cursor.close();
        }
    }

    public List<Score> getScoresOfGame(Game game) {
        List<Score> scores = new ArrayList<>();
        ScoreCursorWrapper cursor = queryScores(
                ScoreTable.Cols.GAMEID + " = ?",
                new String[]{game.getId().toString()},
                null,
                null,
                null,
                null
        );

        try {
            cursor.moveToFirst();
            while (!cursor.isAfterLast()) {
                scores.add(cursor.getScore());
                cursor.moveToNext();
            }
        } finally {
            cursor.close();
        }

        return scores;
    }

    public int getNumberOfScores(UUID gameId) {
        Cursor cursor = mDatabase.query(
                ScoreTable.NAME,
                new String[]{"COUNT(*) AS \"nb_scores\""},
                ScoreTable.Cols.GAMEID + " = ?",
                new String[]{gameId.toString()},
                null,
                null,
                null
        );

        try {
            cursor.moveToFirst();
            return cursor.getInt(cursor.getColumnIndex("nb_scores"));
        } finally {
            cursor.close();
        }
    }

    public void addScore(Score score) {
        score.setIndex(getNumberOfScores(score.getGameId()));
        ContentValues values = getContentValues(score);
        mDatabase.insert(ScoreTable.NAME, null, values);
    }

    public void addGame(Game game) {
        ContentValues values = getContentValues(game);
        mDatabase.insert(GameTable.NAME, null, values);
    }

    public Game newGame(int score) {
        Score newScore = new Score();
        Game newGame = new Game();
        newScore.setGameId(newGame.getId());
        newScore.setWijScore(score);
        newScore.setZijScore(score);
        newScore.setIndex(0);

        addScore(newScore);
        addGame(newGame);
        return newGame;
    }

    public void deleteScore(Score score) {
        mDatabase.delete(
                ScoreTable.NAME,
                ScoreTable.Cols.UUID + " = ?",
                new String[]{score.getId().toString()}
        );
    }

    public void deleteLatestScore(Game game) {
        mDatabase.execSQL(
                "DELETE FROM " + ScoreTable.NAME + "\n"
                        + "WHERE " + ScoreTable.Cols.UUID + " IN \n"
                        + "( SELECT " + ScoreTable.Cols.UUID + " FROM " + ScoreTable.NAME + "\n"
                        + "WHERE " + ScoreTable.Cols.INDEX + " != 0 \n"
                        + "AND " + ScoreTable.Cols.GAMEID + " = \"" + game.getId().toString() + "\" \n"
                        + "ORDER BY " + ScoreTable.Cols.INDEX + " DESC \n"
                        + "LIMIT 1)"
        );
    }

    public Score getLatestScore(Game game) {
        ScoreCursorWrapper cursor = queryScores(
                ScoreTable.Cols.GAMEID + " = ?",
                new String[]{game.getId().toString()},
                null,
                null,
                ScoreTable.Cols.INDEX + " DESC",
                "1"
        );

        try {
            cursor.moveToFirst();
            return cursor.getScore();
        } finally {
            cursor.close();
        }
    }

    private static ContentValues getContentValues(Score score) {
        ContentValues values = new ContentValues();

        values.put(ScoreTable.Cols.UUID, score.getId().toString());
        values.put(ScoreTable.Cols.ZIJSCORE, score.getZijScore());
        values.put(ScoreTable.Cols.WIJSCORE, score.getWijScore());
        values.put(ScoreTable.Cols.PASSENSPEL, score.isPassenspel() ? 1 : 0);
        values.put(ScoreTable.Cols.GAMEID, score.getGameId().toString());
        values.put(ScoreTable.Cols.INDEX, score.getIndex());

        return values;
    }

    private static ContentValues getContentValues(Game game) {
        ContentValues values = new ContentValues();

        values.put(GameTable.Cols.UUID, game.getId().toString());
        values.put(GameTable.Cols.FINISHED, game.isFinished() ? 1 : 0);
        values.put(GameTable.Cols.DATE, game.getDate().getTime());

        return values;
    }

    private ScoreCursorWrapper queryScores(String whereClause, String[] whereArgs, String groupBy,
                                           String having, String orderBy, String limit) {
        Cursor cursor = mDatabase.query(
                ScoreTable.NAME,
                null,
                whereClause,
                whereArgs,
                groupBy,
                having,
                orderBy,
                limit
        );

        return new ScoreCursorWrapper(cursor);
    }

    private GameCursorWrapper queryGames(String whereClause, String[] whereArgs, String groupBy,
                                         String having, String orderBy, String limit) {
        Cursor cursor = mDatabase.query(
                GameTable.NAME,
                null,
                whereClause,
                whereArgs,
                groupBy,
                having,
                orderBy,
                limit
        );

        return new GameCursorWrapper(cursor);
    }
}
