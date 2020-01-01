package com.myapps.avoidingbricksgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.SystemClock;
import android.view.View;

import android.widget.Chronometer;
import android.widget.RelativeLayout;




public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, ChangeListener{

    private final int ELAPSED = 2000;
    private final int COLS = 3;
    private boolean[] flags = new boolean[COLS];
    private View[] hearts = new View[3];
    private View[] player;
    private Drawable visible;
    private Drawable invisible = new ColorDrawable(0);
    private Lives lives;
    private Chronometer time;
    //HandlerThread handlerThread = new HandlerThread("backgroundThread");
    private Handler handler;

    public MainActivity() {
        handler = new Handler();
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        player = new View[COLS];
        player[0] = findViewById(R.id.leftpos);
        player[1] = findViewById(R.id.middlepos);
        player[2] = findViewById(R.id.rightpos);
        hearts[0] = findViewById(R.id.leftlife);
        hearts[1] = findViewById(R.id.middlelife);
        hearts[2] = findViewById(R.id.rightlife);
        time = findViewById(R.id.chrono);
       initNewGame();
    }


    @Override
    protected void onRestart(){
        super.onRestart();
        for (View heart: hearts) heart.setBackgroundResource(R.drawable.full);
        invisible();
        player[COLS/2 + 1].setBackground(visible);
        flags[COLS/2 + 1] = true;
        initNewGame();
    }

    private void initNewGame(){
        lives = new Lives();
        lives.subscribe(this);

        flags[COLS/2 + 1] = true;

        for (int i = 0; i < COLS; i++)
            player[i].setOnClickListener(this);
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visible = new ColorDrawable(getColor(R.color.colorPrimary));
        }
        time.setBase(SystemClock.elapsedRealtime());
        time.refreshDrawableState();
        time.start();
//        handlerThread.start();
//        Handler handler2 = new Handler(handlerThread.getLooper());
//
//        handler2.post(new Runnable() {
//            @Override
//            public void run() {
//                while(lives.getNumLives() > 0){
//                    try {
//                        Thread.sleep(ELAPSED);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                gen();
//            }
//        });

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(lives.getNumLives() > 0){
                    try {
                        Thread.sleep(ELAPSED);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            gen();
                        }
                    });
                }
            }
        }).start();
    }
    private void gen(){
        final BrickView brick = new BrickView(this);
        RelativeLayout box;
        final int rand  = (int)(Math.random()*(3) + 1); // random number between 1-3
        //final Handler h2 = new Handler();
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
                default:
                    break;
        }
        lives.subscribe(brick);
        final View p = player[rand - 1];
        brick.getBrickAnimation().start();
        new Thread(new Runnable() {
            @Override
            public void run() {
                boolean b;
                try {
                    Thread.sleep((long)(BrickView.DUR*0.75));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                b = p.getTop() >= brick.getBottom();
                if (b && flags[rand - 1]) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                lives.setNumLives(lives.getNumLives() - 1);
                            }
                        });
                }

            }
        }).start();
    }

    public void invisible(){
        for (int i = 0; i < COLS; i++){
            player[i].setBackground(invisible);
            flags[i] = false;
        }

    }
    @Override
    public void onClick(View v) {
        invisible();
        v.setBackground(visible);
        for (int i = 0; i < COLS; i++)
            if (v == player[i])
                flags[i] = true;
    }

    @Override
    public void stateChanged(LivesEvent e) {
        hearts[e.getNumLivesChange()].setBackgroundResource(R.drawable.border);
        if (e.getNumLivesChange() == 0){
            time.stop();
            final AlertDialog.Builder builder = new AlertDialog.Builder(this);
            builder
                    .setTitle(R.string.game_over)
                    .setPositiveButton(R.string.start_new_game, new DialogInterface.OnClickListener(){
                        public void onClick(DialogInterface dialog, int id) {
                            onRestart();
                        }
                    })
                    .setNegativeButton(R.string.exit, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            finish();
                        }
                    })
                    .setMessage("your time is " + time.getText())
                    .create()
                    .show();
        }
    }
}

