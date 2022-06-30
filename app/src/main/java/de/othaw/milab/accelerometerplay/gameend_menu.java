package de.othaw.milab.accelerometerplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import java.text.DecimalFormat;
import java.time.Duration;

/**
 * Handles the menu at the end of a game
 */
public class gameend_menu extends AppCompatActivity {

    // Duration of the game that the menu was called from
    private Duration duration;

    // dbhelper to interface with SQLite
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameend_menu);

        // get duration of MainActivity
        duration = MainActivity.getDuration();

        TextView t = findViewById(R.id.timetextview);

        // Display time in Decimal with 3 spaces
        DecimalFormat twoDForm = new DecimalFormat("#.###");
        float time = Float.parseFloat(twoDForm.format(duration.toMillis() * 0.001f));

        // print the time to screen
        String time_needed = "Your time needed was " +  time   + " seconds!";
        t.setText(time_needed);

    }

    /**
     * Persists the current score and player name in the db and opens up the MainMenu
     * @param v View
     */
    public void submitScore(View v){
        Intent intent = new Intent(this, MainMenu.class);
        // get personname field
        EditText personname = findViewById(R.id.editTextTextPersonName);
        // write personname to the db
        dbHelper.updateScore(personname.getText().toString(), duration.toMillis() * 0.001f);
        startActivity(intent);
    }


}