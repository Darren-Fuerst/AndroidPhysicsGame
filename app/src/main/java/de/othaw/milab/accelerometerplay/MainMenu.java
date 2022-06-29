package de.othaw.milab.accelerometerplay;

import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    HighScoreEntryDBHelper dbHelper = new HighScoreEntryDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);

        Cursor cursor = dbHelper.getBestScores();

        ListView scorelist = findViewById(R.id.list_view);

        String[] cols = new String[] {HighScoreContract.HighScoreEntry.COLUMN_NAME, HighScoreContract.HighScoreEntry.COLUMN_TIME};
        int[] displayviews = new int[] {R.id.name_in_list, R.id.time_in_list};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_high_score_list_layout, cursor, cols, displayviews, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
        adapter.setViewBinder((view, cursor1, columnindex) ->{
            return false;
        } );


        scorelist.setAdapter(adapter);
    }

    public void openGameActivity(View v){
        Intent intent = new Intent(this, AccelerometerPlayActivity.class);
        startActivity(intent);
    }


}

