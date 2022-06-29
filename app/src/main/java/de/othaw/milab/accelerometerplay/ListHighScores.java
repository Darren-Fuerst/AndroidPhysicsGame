package de.othaw.milab.accelerometerplay;

import androidx.appcompat.app.AppCompatActivity;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

public class ListHighScores extends AppCompatActivity {

    private final DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_high_scores);

        Cursor cursor = dbHelper.getBestScores();

        ListView scorelist = findViewById(R.id.list_view);

        String[] cols = new String[] {HighScoreContract.HighScoreEntry.COLUMN_NAME, HighScoreContract.HighScoreEntry.COLUMN_TIME};
        int[] displayviews = new int[] {R.id.name_in_list, R.id.time_in_list};

        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_high_score_list_layout, cursor, cols, displayviews, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
        adapter.setViewBinder((view, cursor1, columnindex) -> false);
        scorelist.setAdapter(adapter);

    }

    public void closeMenu(View v){
        finish();
    }
}