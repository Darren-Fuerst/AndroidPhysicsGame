package de.othaw.milab.accelerometerplay;

import android.provider.BaseColumns;

public class SettingsContract {

    private SettingsContract(){}

    /**
     * Settings Table definition for database
     */
    public static class SettingsEntry implements BaseColumns {
        /**
         * Name of the table
         */
        public static final String TABLE_NAME = "settings_table";
        /**
         * Id Column
         */
        public static final String COLUMN_ID = "_id";
        /**
         *  Name of the remote settings column
         */
        public static final String COLUMN_REMOTE = "remote";
        /**
         *  Name of the broker settings column
         */
        public static final String COLUMN_BROKER = "broker";
        /**
         *  Name of the sound settings column
         */
        public static final String COLUMN_SOUND = "sound";

        /**
         * Standard setting for remote column
         */
        public static final int remoteval = 0;

        /**
         * * Standard setting for broker column
         */
        public static final String brokerval = "tcp://192.168.137.2:1883";

        /**
         * Standard setting for sound column
         */
        public  static final int soundval = 1;

        /**
         * Table Creation statement
         */
        public static final String SQL_CREATE_SETTINGSTABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_REMOTE + " INTEGER," +
                        COLUMN_BROKER + " TEXT," +
                        COLUMN_SOUND + " INTEGER"+
                        ")";

        /**
         * Table Deletion Statement
         */
        public static final String SQL_DELETE_SETTINGSTABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
