package de.othaw.milab.accelerometerplay;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.text.Editable;

public class HighScoreEntryDBHelper extends SQLiteOpenHelper {

    public static final int DATABASE_VERSION = 3;
    public static final String DATABASE_NAME = "HighScoreEntry.db";

    public HighScoreEntryDBHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    public void onCreate(SQLiteDatabase db) {
        db.execSQL(HighScoreContract.HighScoreEntry.SQL_CREATE_ENTRIES);
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL(HighScoreContract.HighScoreEntry.SQL_DELETE_ENTRIES);
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
}
