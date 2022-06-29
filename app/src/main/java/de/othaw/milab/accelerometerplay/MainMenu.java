package de.othaw.milab.accelerometerplay;

import android.content.Intent;
import android.database.Cursor;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CursorAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    private DBHelper dbHelper = new DBHelper(this);
    private CheckBox mqttbox;

    public int REMOTEFLAG;


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

        mqttbox = findViewById(R.id.mqtt_checkbox);
        if (dbHelper.getRemote() == 1){
            mqttbox.setChecked(true);
            REMOTEFLAG = 1;
        }else{
            mqttbox.setChecked(false);
            REMOTEFLAG = 0;
        }
    }

    private void updateRemoteFlag(){
        if (mqttbox.isChecked()){
            dbHelper.setRemote(1);
            REMOTEFLAG = 1;
        }else{
            dbHelper.setRemote(0);
            REMOTEFLAG = 0;
        }
    }

    public void openGameActivity(View v){
        updateRemoteFlag();

        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void openSettingsActivity(View v){
        Intent intent = new Intent(this, ConnectMQTT.class);
        startActivity(intent);
    }


}

