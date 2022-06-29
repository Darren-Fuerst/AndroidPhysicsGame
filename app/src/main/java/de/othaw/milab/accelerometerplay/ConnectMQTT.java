package de.othaw.milab.accelerometerplay;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;

public class ConnectMQTT extends AppCompatActivity {

    DBHelper dbHelper = new DBHelper(this);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_connect_mqtt);

        EditText brokerfield = findViewById(R.id.mqttbrokerip);
        String[] ip = dbHelper.getBroker().split("/");
        brokerfield.setText(ip[ip.length -1]);
    }

    public void persistSettings(View v){
        EditText brokerfield = findViewById(R.id.mqttbrokerip);
        dbHelper.settBroker(brokerfield.getText().toString());

        finish();
    }
}