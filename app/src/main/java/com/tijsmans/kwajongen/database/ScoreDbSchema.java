package com.tijsmans.kwajongen.database;


public class ScoreDbSchema {

    public static final class ScoreTable {
        public static final String NAME = "scores";
        public static final String GAME_ID_INDEX = "game_id_index";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String ZIJSCORE = "zijscore";
            public static final String WIJSCORE = "wijscore";
            public static final String PASSENSPEL = "passenspel";
            public static final String GAMEID = "game_id";
            public static final String INDEX = "game_index";
        }
    }

    public static final class GameTable {
        public static final String NAME = "games";

        public static final class Cols {
            public static final String UUID = "uuid";
            public static final String DATE = "date";
            public static final String FINISHED = "finished";
        }
    }
}
