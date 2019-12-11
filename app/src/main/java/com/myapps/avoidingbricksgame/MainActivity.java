package com.myapps.avoidingbricksgame;

import androidx.appcompat.app.AppCompatActivity;

import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import android.widget.GridLayout;
import android.widget.RelativeLayout;

import java.util.Timer;
import java.util.TimerTask;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    private boolean[] flags = new boolean[3];
    private View p1;
    private View p2;
    private View p3;
    Drawable visible;
    Drawable invisible = new ColorDrawable(0);
    private int lives;
    private Timer timer;
    private TimerTask gen;
    final Handler handler = new Handler();
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        lives = 3;
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        p1 = findViewById(R.id.leftpos);
        p2 = findViewById(R.id.middlepos);
        p3 = findViewById(R.id.rightpos);
        p1.setOnClickListener(this); p2.setOnClickListener(this); p3.setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visible = new ColorDrawable(getColor(R.color.colorPrimary));
        }
        timer = new Timer();
        gen = new TimerTask() {
            @Override
            public void run() {
                handler.post(new Runnable() {
                    @Override
                    public void run() {
                        gen();
                    }
                });
            }
        };
        timer.schedule(gen, 500, 2000);
        new Thread(new Runnable(){
            @Override
            public void run() {
                check();
            }
        }).start();
    }

    private void gen(){
        final BrickView brick = new BrickView(this);
        RelativeLayout box;
        final int rand  = (int)(Math.random()*(3) + 1); // random number between 1-3

        switch (rand) {
            case 1:
                box =  findViewById(R.id.box1);
                box.addView(brick);
                break;
            case 2:
                box =  findViewById(R.id.box2);
                box.addView(brick);
                break;
            case 3:
                box =  findViewById(R.id.box3);
                box.addView(brick);
                break;
        }
        brick.animation.start();

        new Thread(new Runnable(){
            @Override
            public void run() {
                while (brick.getBottom() <= 430)
                    flags[rand - 1] = false;
                flags[rand - 1] = true;
                if (brick.getTop() >= 500)
                    flags[rand - 1] = false;
            }
        }).start();

    }

    public void invisible(){
        p1.setBackground(invisible);
        p2.setBackground(invisible);
        p3.setBackground(invisible);
    }
    @Override
    public void onClick(View v) {
        invisible();
        v.setBackground(visible);
    }
    private void check(){
        while (lives > 0){
            if (p1.getBackground() == visible && flags[0]){
                lives -= 1;
                Log.d("p1", "death");
            }

            if (p2.getBackground() == visible && flags[1])
            {
                lives -= 1;
                Log.d("p2", "death");
            }
            if (p3.getBackground() == visible && flags[2])
            {
                lives -= 1;
                Log.d("p3", "death");
            }
        }
        timer.cancel();
    }
}
