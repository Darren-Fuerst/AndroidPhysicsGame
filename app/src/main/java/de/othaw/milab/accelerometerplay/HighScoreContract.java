package de.othaw.milab.accelerometerplay;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;

public class HighScoreContract {

    private HighScoreContract(){}

    public static class HighScoreEntry implements BaseColumns{
        public static final String TABLE_NAME = "highscore_table";
        public static final String COLUMN_NAME = "name";
        public static final String COLUMN_TIME = "time";


        public static final String SQL_CREATE_ENTRIES =
                "CREATE TABLE " + HighScoreEntry.TABLE_NAME + " (" +
                        HighScoreEntry.COLUMN_NAME + " TEXT PRIMARY KEY," +
                        HighScoreEntry.COLUMN_TIME + " REAL)";

        public static final String SQL_DELETE_ENTRIES =
                "DROP TABLE IF EXISTS " + HighScoreEntry.TABLE_NAME;
    }

}


