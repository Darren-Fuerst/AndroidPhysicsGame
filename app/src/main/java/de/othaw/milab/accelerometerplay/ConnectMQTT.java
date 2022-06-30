package de.othaw.milab.accelerometerplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConnectMQTT extends AppCompatActivity {

    /**
     * DBHelper to interface with SQLite
     */
    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_mqtt);

        // get broker edit field
        EditText brokerfield = findViewById(R.id.mqttbrokerip);

        // get broker splitting of tcp:// from the ip
        String[] ip = dbHelper.getBroker().split("/");

        // get the ip from the ip text field
        brokerfield.setText(ip[ip.length -1]);
    }

    public void persistSettings(View v){
        // get brokerfield
        EditText brokerfield = findViewById(R.id.mqttbrokerip);

        // set broker in db to the input
        dbHelper.settBroker(brokerfield.getText().toString());

        // close menu
        finish();
    }
}