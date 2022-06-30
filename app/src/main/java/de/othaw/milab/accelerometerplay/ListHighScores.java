package de.othaw.milab.accelerometerplay;

import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

/**
 * Implements the HighScoreList Activity displaying all the scores from best to worst
 */
public class ListHighScores extends AppCompatActivity {

    //DBHelper to interface with db
    private final DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list_high_scores);

        // get BestScores cursor
        Cursor cursor = dbHelper.getBestScores();

        // get the ListView
        ListView scorelist = findViewById(R.id.list_view);

        // define the needed columns
        String[] cols = new String[] {HighScoreContract.HighScoreEntry.COLUMN_NAME, HighScoreContract.HighScoreEntry.COLUMN_TIME};

        // define the needed views to be displayed inside the List View
        int[] displayviews = new int[] {R.id.name_in_list, R.id.time_in_list};

        // Instantiate the Adapter needed to fill the Listview
        SimpleCursorAdapter adapter = new SimpleCursorAdapter(this, R.layout.activity_high_score_list_layout, cursor, cols, displayviews, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );
        adapter.setViewBinder((view, cursor1, columnindex) -> false);
        scorelist.setAdapter(adapter);
    }

    /**
     * Closes the Highscore menu
     * @param v View
     */
    public void closeMenu(View v){
        finish();
    }
}