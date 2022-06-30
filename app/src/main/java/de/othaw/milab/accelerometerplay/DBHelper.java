package de.othaw.milab.accelerometerplay;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

import java.text.DecimalFormat;

/**
 * Manages all operations regarding reading and writing to the database
 */
public class DBHelper extends SQLiteOpenHelper {

    /**
     * Databse Versioning
     */
    private static final int DATABASE_VERSION = 21;

    /**
     * DataBaseName
     */
    private static final String DATABASE_NAME = "HighScoreEntry.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(@NonNull SQLiteDatabase db) {

        // create the tables
        db.execSQL(HighScoreContract.HighScoreEntry.SQL_CREATE_HIGHSCORETABLE);
        db.execSQL(SettingsContract.SettingsEntry.SQL_CREATE_SETTINGSTABLE);

        // insert base settings
        ContentValues values = new ContentValues();
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, SettingsContract.SettingsEntry.remoteval);
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, SettingsContract.SettingsEntry.brokerval);
        values.put(SettingsContract.SettingsEntry.COLUMN_SOUND, SettingsContract.SettingsEntry.soundval);
        db.insert(
                SettingsContract.SettingsEntry.TABLE_NAME, null, values);
    }

    /**
     * Handles Upgrading the database Version
     *
     * @param db SQLITE Database
     * @param oldVersion Oldversion of the database
     * @param newVersion Newversion of the database
     */
    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HighScoreContract.HighScoreEntry.SQL_DELETE_HIGHSCORETABLE);
        db.execSQL(SettingsContract.SettingsEntry.SQL_DELETE_SETTINGSTABLE);
        onCreate(db);
    }

    /**
     * Handles downgrading of the database version
     * @param db SQLITE Database
     * @param oldVersion Oldversion of the database
     * @param newVersion Newversion of the database
     */
    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    /**
     * Updates the Players Score or inserts a new player with his score into the database
     *
     * @param name  Name of the Player
     * @param time  Duration of the game the player took
     */
    public void updateScore(String name, float time){

        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // trim whitespace
        name = name.trim();

        // check if left empty
        if (name.length() < 1){
            name = "Anonymous";
        }

        // 3 decimal places only
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        time = Float.parseFloat(twoDForm.format(time));


        // check if entry for given name exists already
        Float curtime = getPlayerTime(name);
        // persist the smallest amount of time
        if (curtime != null) {
           time = Math.min(curtime, time);
        }

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        values.put(HighScoreContract.HighScoreEntry.COLUMN_NAME, name);
        values.put(HighScoreContract.HighScoreEntry.COLUMN_TIME, time);

        // Insert or update the row
        db.insertWithOnConflict(HighScoreContract.HighScoreEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);
    }

    /**
     * Gets the time of a Player
     *
     * @param name Name of the player
     * @return null if the player doesn't exist , float: time if he does
     */
    public Float getPlayerTime(String name){

        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        //  will actually be used after this query.
        String[] projection = {
                HighScoreContract.HighScoreEntry.COLUMN_TIME
        };

        String selection = HighScoreContract.HighScoreEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(
                HighScoreContract.HighScoreEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
            selectionArgs,               // The values for the WHERE clause
        null,                   // don't group the rows
            null,                 // don't filter by row groups
                null             // The sort order
        );

        try {
            // get the first item and return the float
            // we only look at the first because the Player Name is unique
            cursor.moveToNext();
            Float current_time = cursor.getFloat(cursor.getColumnIndex(HighScoreContract.HighScoreEntry.COLUMN_TIME));
            cursor.close();
            return current_time;
        }
        catch(Exception e) {
            // if there is no player under that name return null
            return null;
        }
        
    }

    /**
     * Used to get all Players along with their time sorted by best time
     *
     * @return Cursor with all Player, time values ordered in Ascending Order (Best time to worst)
     */
    public Cursor getBestScores(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + HighScoreContract.HighScoreEntry.COLUMN_NAME +"," + HighScoreContract.HighScoreEntry.COLUMN_TIME + " FROM " + HighScoreContract.HighScoreEntry.TABLE_NAME +" ORDER BY " + HighScoreContract.HighScoreEntry.COLUMN_TIME + " ASC";
        return db.rawQuery(query, null);

    }

    /**
     *  Gets the broker ip in the settings table
     *
     * @return broker ip as string
     */
    public String getBroker() {

        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT " + SettingsContract.SettingsEntry.COLUMN_BROKER + " FROM " + SettingsContract.SettingsEntry.TABLE_NAME, null);

        try {
            cursor.moveToNext();
        } catch (Exception e) {
            return null;
        }

        return cursor.getString(0);
    }

    /**
     * Gets the remote setting that was last set
     *
     * @return 0 if false, 1 if mqtt broker was last set to be used, null if no settings were found
     */
    public Integer getRemote() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT " + SettingsContract.SettingsEntry.COLUMN_REMOTE + " FROM " + SettingsContract.SettingsEntry.TABLE_NAME, null);

        try {
            cursor.moveToNext();
        } catch (Exception e) {
            return null;
        }

        return cursor.getInt(0);
    }

    /**
     * Persists a given broker ip into the settings table
     *
     * @param brokerip broker ip value without leading tcp://
     */
    public void settBroker(String brokerip) {
        SQLiteDatabase db = getWritableDatabase();
        String ip = "tcp://" + brokerip;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // we only ever want one row of settings
        values.put(SettingsContract.SettingsEntry.COLUMN_ID, 1);
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, ip);
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, getRemote());
        values.put(SettingsContract.SettingsEntry.COLUMN_SOUND, getSound());

        db.insertWithOnConflict(SettingsContract.SettingsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);


    }

    /**
     * Persists a given remote setting into the settings table
     *
     * @param i 0 if no mqtt broker is to be used, 1 if mqtt is to be activated
     */
    public void setRemote(int i) {
        assert (i == 0 || i == 1);
        SQLiteDatabase db = getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // we only ever want one row of settings
        values.put(SettingsContract.SettingsEntry.COLUMN_ID, 1);
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, i);
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, getBroker());
        values.put(SettingsContract.SettingsEntry.COLUMN_SOUND, getSound());

        db.insertWithOnConflict(SettingsContract.SettingsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }

    /**
     * Gets the current sound setting from the settings table
     *
     * @return 0 if sound is off, 1 if sound is on, null if no settings were found
     */
    public Integer getSound() {
        SQLiteDatabase db = getReadableDatabase();
        @SuppressLint("Recycle") Cursor cursor = db.rawQuery("SELECT " + SettingsContract.SettingsEntry.COLUMN_SOUND + " FROM " + SettingsContract.SettingsEntry.TABLE_NAME, null);

        try {
            cursor.moveToNext();
        } catch (Exception e) {
            return null;
        }

        return cursor.getInt(0);
    }


    /**
     * Persists a given sound setting into the settings table
     *
     * @param i 0 if no sound  is to be played, 1 if sound is to be activated
     */
    public void setSound(int i) {

        assert (i == 0 || i == 1);
        SQLiteDatabase db = getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // we only ever want one row of settings
        values.put(SettingsContract.SettingsEntry.COLUMN_ID, 1);
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, getRemote());
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, getBroker());
        values.put(SettingsContract.SettingsEntry.COLUMN_SOUND, i);

        db.insertWithOnConflict(SettingsContract.SettingsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }
}
