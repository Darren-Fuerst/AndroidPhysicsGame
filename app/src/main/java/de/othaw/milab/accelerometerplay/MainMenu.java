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

    public void openScoresView(View v){
        Intent intent = new Intent(this, ListHighScores.class);
        startActivity(intent);
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

