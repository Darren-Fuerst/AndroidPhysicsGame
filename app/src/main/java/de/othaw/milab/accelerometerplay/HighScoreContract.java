package de.othaw.milab.accelerometerplay;

import android.provider.BaseColumns;

public class HighScoreContract {

    private HighScoreContract(){}

    public static class HighScoreEntry implements BaseColumns{
        public static final String TABLE_NAME = "highscore_table";
        public static final String COLUMN_NAME = "_id";
        public static final String COLUMN_TIME = "time";


        public static final String SQL_CREATE_HIGHSCORETABLE =
                "CREATE TABLE " + HighScoreEntry.TABLE_NAME + " (" +
                        HighScoreEntry.COLUMN_NAME + " TEXT PRIMARY KEY," +
                        HighScoreEntry.COLUMN_TIME + " REAL)";

        public static final String SQL_DELETE_HIGHSCORETABLE =
                "DROP TABLE IF EXISTS " + HighScoreEntry.TABLE_NAME;
    }

}


