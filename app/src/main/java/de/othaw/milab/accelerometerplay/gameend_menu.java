package de.othaw.milab.accelerometerplay;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import java.time.Duration;

public class gameend_menu extends AppCompatActivity {

    private Duration duration;
    HighScoreEntryDBHelper dbHelper = new HighScoreEntryDBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gameend_menu);

        duration = AccelerometerPlayActivity.getDuration();

        TextView t = findViewById(R.id.timetextview);

        t.setText("Your time needed was " +  duration.toMillis() * 0.001f + " seconds!");

    }

    public void submitScore(View v){
        Intent intent = new Intent(this, MainMenu.class);
        EditText personname = findViewById(R.id.editTextTextPersonName);
        dbHelper.updateScore(personname.getText().toString(), duration.toMillis() * 0.001f);

        System.out.println("-----------: " + dbHelper.readTime(personname.getText().toString()));
        startActivity(intent);
    }


}