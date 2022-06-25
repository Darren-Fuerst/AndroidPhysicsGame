package de.othaw.milab.accelerometerplay;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu_main);
    }

    public void openGameActivity(View v){
        Intent intent = new Intent(this, AccelerometerPlayActivity.class);
        startActivity(intent);
    }


}

