package de.othaw.milab.accelerometerplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainMenu extends AppCompatActivity {

    private final DBHelper dbHelper = new DBHelper(this);
    private Switch mqttswitch;
    private Switch soundswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);

        mqttswitch = findViewById(R.id.mqtt_switch);
        mqttswitch.setChecked(dbHelper.getRemote() == 1);

        soundswitch = findViewById(R.id.sound_switch);
        soundswitch.setChecked(dbHelper.getSound() == 1);
    }

    private void updateRemoteFlag(){
        if (mqttswitch.isChecked()){
            dbHelper.setRemote(1);
        }else{
            dbHelper.setRemote(0);
        }
    }

    private void updateSoundFlag(){
        if (soundswitch.isChecked()){
            dbHelper.setSound(1);
        }else{
            dbHelper.setSound(0);
        }
    }

    public void openScoresView(View v){
        Intent intent = new Intent(this, ListHighScores.class);
        startActivity(intent);
    }

    public void openGameActivity(View v){
        updateRemoteFlag();
        updateSoundFlag();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }

    public void openSettingsActivity(View v){
        Intent intent = new Intent(this, ConnectMQTT.class);
        startActivity(intent);
    }


}

