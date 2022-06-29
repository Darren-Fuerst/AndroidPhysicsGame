package de.othaw.milab.accelerometerplay;

import android.provider.BaseColumns;

public class SettingsContract {

    private SettingsContract(){}

    public static class SettingsEntry implements BaseColumns {
        public static final String TABLE_NAME = "settings_table";
        public static final String COLUMN_ID = "_id";
        public static final String COLUMN_REMOTE = "remote";
        public static final String COLUMN_BROKER = "broker";
        public static final String COLUMN_SOUND = "sound";

        public static final int remoteval = 0;
        public static final String brokerval = "tcp://192.168.137.2:1883";
        public  static final int soundval = 1;

        public static final String SQL_CREATE_SETTINGSTABLE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT," +
                        COLUMN_REMOTE + " INTEGER," +
                        COLUMN_BROKER + " TEXT," +
                        COLUMN_SOUND + " INTEGER"+
                        ")";


        public static final String SQL_DELETE_SETTINGSTABLE =
                "DROP TABLE IF EXISTS " + TABLE_NAME;
    }
}
