package com.game;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.View;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

public class MainActivity extends AppCompatActivity {

    private boolean isMute;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN,WindowManager.LayoutParams.FLAG_FULLSCREEN);

        findViewById(R.id.play).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,GameActivity.class));
            }
        });

        TextView highscoreText=findViewById(R.id.highscoreText);
        SharedPreferences preferences=getSharedPreferences("game", Context.MODE_PRIVATE);
        highscoreText.setText("HighScore: "+preferences.getInt("highscore",0));

        isMute=preferences.getBoolean("isMute",false);
       final ImageView volCntrl=findViewById(R.id.volume);

        if(isMute)
            volCntrl.setImageResource(R.drawable.ic_baseline_volume_off_24);
        else
            volCntrl.setImageResource(R.drawable.ic_baseline_volume_up_24);

        volCntrl.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                isMute=!isMute;

                if(isMute)
                    volCntrl.setImageResource(R.drawable.ic_baseline_volume_off_24);
                else
                    volCntrl.setImageResource(R.drawable.ic_baseline_volume_up_24);

                SharedPreferences.Editor editor=preferences.edit();
                editor.putBoolean("isMute",isMute);
                editor.apply();
            }
        });
    }
}