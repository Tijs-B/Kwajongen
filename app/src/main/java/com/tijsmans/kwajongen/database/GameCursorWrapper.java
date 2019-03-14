package com.tijsmans.kwajongen.database;


import android.database.Cursor;
import android.database.CursorWrapper;

import com.tijsmans.kwajongen.Game;
import com.tijsmans.kwajongen.database.ScoreDbSchema.GameTable;

import java.util.Date;
import java.util.UUID;

public class GameCursorWrapper extends CursorWrapper {
    public GameCursorWrapper(Cursor cursor) {
        super(cursor);
    }

    public Game getGame() {
        String uuidString = getString(getColumnIndex(GameTable.Cols.UUID));
        long date = getLong(getColumnIndex(GameTable.Cols.DATE));
        int finished = getInt(getColumnIndex(GameTable.Cols.FINISHED));

        Game game = new Game(UUID.fromString(uuidString));
        game.setDate(new Date(date));
        game.setFinished(finished != 0);

        return game;
    }
}
