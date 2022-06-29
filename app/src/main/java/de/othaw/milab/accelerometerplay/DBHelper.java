package de.othaw.milab.accelerometerplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import androidx.annotation.NonNull;

public class DBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 18;
    public static final String DATABASE_NAME = "HighScoreEntry.db";

    public DBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(@NonNull SQLiteDatabase db) {
        db.execSQL(HighScoreContract.HighScoreEntry.SQL_CREATE_HIGHSCORETABLE);
        db.execSQL(SettingsContract.SettingsEntry.SQL_CREATE_SETTINGSTABLE);


        ContentValues values = new ContentValues();
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, SettingsContract.SettingsEntry.remoteval);
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, SettingsContract.SettingsEntry.brokerval);

        db.insert(
                SettingsContract.SettingsEntry.TABLE_NAME, null, values);
    }

    public void onUpgrade(@NonNull SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HighScoreContract.HighScoreEntry.SQL_DELETE_HIGHSCORETABLE);
        db.execSQL(SettingsContract.SettingsEntry.SQL_DELETE_SETTINGSTABLE);
        onCreate(db);
    }

    public void onDowngrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        onUpgrade(db, oldVersion, newVersion);
    }

    public void updateScore(String name, float time){
        // Gets the data repository in write mode
        SQLiteDatabase db = getWritableDatabase();

        // check if entry for given name exists already
        Float curtime = readTime(name);
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

    public Float readTime(String name){

        SQLiteDatabase db = getReadableDatabase();

        // Define a projection that specifies which columns from the database
        // you will actually use after this query.
        String[] projection = {
                HighScoreContract.HighScoreEntry.COLUMN_TIME
        };

        String selection = HighScoreContract.HighScoreEntry.COLUMN_NAME + " = ?";
        String[] selectionArgs = { name };

        Cursor cursor = db.query(
                HighScoreContract.HighScoreEntry.TABLE_NAME,   // The table to query
                projection,             // The array of columns to return (pass null to get all)
                selection,              // The columns for the WHERE clause
            selectionArgs,          // The values for the WHERE clause
        null,                   // don't group the rows
            null,                   // don't filter by row groups
                null               // The sort order
        );

        try {
            cursor.moveToNext();
            Float current_time = cursor.getFloat(cursor.getColumnIndex(HighScoreContract.HighScoreEntry.COLUMN_TIME));
            return current_time;
        }
        catch(Exception e) {
            return null;
        }
        
    }

    public Cursor getBestScores(){
        SQLiteDatabase db = getReadableDatabase();

        String query = "SELECT " + HighScoreContract.HighScoreEntry.COLUMN_NAME +"," + HighScoreContract.HighScoreEntry.COLUMN_TIME + " FROM " + HighScoreContract.HighScoreEntry.TABLE_NAME +" ORDER BY " + HighScoreContract.HighScoreEntry.COLUMN_TIME + " ASC";
        return db.rawQuery(query, null);

    }


    public String getBroker() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + SettingsContract.SettingsEntry.COLUMN_BROKER + " FROM " + SettingsContract.SettingsEntry.TABLE_NAME, null);

        try {
            cursor.moveToNext();
        } catch (Exception e) {
            return null;
        }

        return cursor.getString(0);
    }

    public Integer getRemote() {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + SettingsContract.SettingsEntry.COLUMN_REMOTE + " FROM " + SettingsContract.SettingsEntry.TABLE_NAME, null);

        try {
            cursor.moveToNext();
        } catch (Exception e) {
            return null;
        }

        return cursor.getInt(0);
    }

    public void settBroker(String brokerip) {
        SQLiteDatabase db = getWritableDatabase();
        String ip = "tcp://" + brokerip;

        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // we only ever want one row of settings
        values.put(SettingsContract.SettingsEntry.COLUMN_ID, 1);
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, ip);
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, getRemote());

        db.insertWithOnConflict(SettingsContract.SettingsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);


    }

    public void setRemote(int i) {

        assert (i == 0 || i == 1);
        SQLiteDatabase db = getWritableDatabase();
        // Create a new map of values, where column names are the keys
        ContentValues values = new ContentValues();
        // we only ever want one row of settings
        values.put(SettingsContract.SettingsEntry.COLUMN_ID, 1);
        values.put(SettingsContract.SettingsEntry.COLUMN_REMOTE, i);
        values.put(SettingsContract.SettingsEntry.COLUMN_BROKER, getBroker());

        db.insertWithOnConflict(SettingsContract.SettingsEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_REPLACE);

    }
}
