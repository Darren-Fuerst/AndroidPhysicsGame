package de.othaw.milab.accelerometerplay;

import android.provider.BaseColumns;

public class HighScoreContract {

    private HighScoreContract(){}

    /**
     * Highscore Table definition
     */
    public static class HighScoreEntry implements BaseColumns{
        /**
         * Table name
         */
        public static final String TABLE_NAME = "highscore_table";

        /**
         * Column name of name column
         */
        public static final String COLUMN_NAME = "_id";

        /**
         * Column name of time column
         */
        public static final String COLUMN_TIME = "time";

        /**
         *  Table creation statement
         */
        public static final String SQL_CREATE_HIGHSCORETABLE =
                "CREATE TABLE " + HighScoreEntry.TABLE_NAME + " (" +
                        HighScoreEntry.COLUMN_NAME + " TEXT PRIMARY KEY," +
                        HighScoreEntry.COLUMN_TIME + " REAL)";

        /**
         * Table delete statement
         */
        public static final String SQL_DELETE_HIGHSCORETABLE =
                "DROP TABLE IF EXISTS " + HighScoreEntry.TABLE_NAME;
    }

}


