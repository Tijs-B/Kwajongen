package com.tijsmans.kwajongen.database;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.tijsmans.kwajongen.Score;
import com.tijsmans.kwajongen.database.ScoreDbSchema.ScoreTable;

import java.util.UUID;

public class ScoreCursorWrapper extends CursorWrapper {
    public ScoreCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Score getScore() {
        String uuidString = getString(getColumnIndex(ScoreTable.Cols.UUID));
        int zijScore = getInt(getColumnIndex(ScoreTable.Cols.ZIJSCORE));
        int wijScore = getInt(getColumnIndex(ScoreTable.Cols.WIJSCORE));
        int passenspel = getInt(getColumnIndex(ScoreTable.Cols.PASSENSPEL));
        String gameId = getString(getColumnIndex(ScoreTable.Cols.GAMEID));
        int index = getInt(getColumnIndex(ScoreTable.Cols.INDEX));

        Score score = new Score(UUID.fromString(uuidString));
        score.setZijScore(zijScore);
        score.setWijScore(wijScore);
        score.setPassenspel(passenspel != 0);
        score.setGameId(UUID.fromString(gameId));
        score.setIndex(index);

        return score;
    }
}
