package de.othaw.milab.accelerometerplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Switch;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.SwitchCompat;

public class MainMenu extends AppCompatActivity {

    /**
     * DBHelper to interface with db
     */
    private final DBHelper dbHelper = new DBHelper(this);

    /**
     * Switch turning mqtt on or off
     */
    private Switch mqttswitch;

    /**
     * Switch turning sound on or off
     */
    private Switch soundswitch;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);

        // get and set state of mqtt switch
        mqttswitch = findViewById(R.id.mqtt_switch);
        mqttswitch.setChecked(dbHelper.getRemote() == 1);

        // get and set state of sound switch
        soundswitch = findViewById(R.id.sound_switch);
        soundswitch.setChecked(dbHelper.getSound() == 1);
    }

    /**
     * Sends the current state of the mqtt switch to the database
     */
    private void updateRemoteFlag(){
        if (mqttswitch.isChecked()){
            dbHelper.setRemote(1);
        }else{
            dbHelper.setRemote(0);
        }
    }

    /**
     * Sends the current state of the sounds switch to the database
     */
    private void updateSoundFlag(){
        if (soundswitch.isChecked()){
            dbHelper.setSound(1);
        }else{
            dbHelper.setSound(0);
        }
    }

    /**
     * Opens the HighScore Activity
     * @param v View
     */
    public void openScoresView(View v){
        Intent intent = new Intent(this, ListHighScores.class);
        startActivity(intent);
    }

    /**
     * Opens the Game/ Main Activity
     * @param v View
     */
    public void openGameActivity(View v){
        updateRemoteFlag();
        updateSoundFlag();
        Intent intent = new Intent(this, MainActivity.class);
        startActivity(intent);

    }
    /**
     * Opens the MQTT Settings Activity
     * @param v View
     */
    public void openSettingsActivity(View v){
        Intent intent = new Intent(this, ConnectMQTT.class);
        startActivity(intent);
    }


}

