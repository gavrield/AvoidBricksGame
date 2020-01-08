package com.myapps.avoidingbricksgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
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
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, ChangeListener{

    private final int ELAPSED = 2000;
    private final int COLS = 5;
    private final int MAX_THREADS = 12;
    private boolean[] flags;
    private View[] hearts;
    private View[] player;
    private RelativeLayout[] boxes;
    private Drawable visible;
    private Drawable invisible;
    private Lives lives;
    private Chronometer time;
    //HandlerThread handlerThread = new HandlerThread("backgroundThread");
    private Handler handler;
    private boolean semaphore;
    private ExecutorService pool;
    private ScorsDbHelper dbHelper;

    public MainActivity() {
        handler = new Handler();
        player = new View[COLS];
        boxes = new RelativeLayout[COLS];
        hearts = new View[Lives.getInitLives()];
        flags = new boolean[COLS];
        invisible = new ColorDrawable(0);
        semaphore = false;
        pool = Executors.newFixedThreadPool(MAX_THREADS);
        dbHelper = new ScorsDbHelper(this);
    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        player[0] = findViewById(R.id.mostleftpos);
        player[1] = findViewById(R.id.leftpos);
        player[2] = findViewById(R.id.middlepos);
        player[3] = findViewById(R.id.rightpos);
        player[4] = findViewById(R.id.mostrightpos);


        boxes[0] = findViewById(R.id.box1);
        boxes[1] = findViewById(R.id.box2);
        boxes[2] = findViewById(R.id.box3);
        boxes[3] = findViewById(R.id.box4);
        boxes[4] = findViewById(R.id.box5);

        hearts[0] = findViewById(R.id.leftlife);
        hearts[1] = findViewById(R.id.middlelife);
        hearts[2] = findViewById(R.id.rightlife);

        time = findViewById(R.id.chrono);

    }

    @Override
    protected void onStart(){
        super.onStart();
        initNewGame();
    }

    @Override
    protected void onRestart(){
        super.onRestart();
        for (View heart: hearts) heart.setBackgroundResource(R.drawable.full);
        player[COLS/2 ].setBackground(visible);
        initNewGame();
    }

    private void initNewGame(){
        lives = new Lives();
        lives.subscribe(this);

        for (int i = 0; i < COLS; i++){
            flags[i] = i == COLS/2; // flags[COLS/2 + 1] = true else flags[i] = false
            player[i].setOnClickListener(this);
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visible = new ColorDrawable(getColor(R.color.colorPrimary));
        }
        time.setBase(SystemClock.elapsedRealtime());
        time.refreshDrawableState();
        time.start();

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

        final int rand  = (int)(Math.random()*(COLS)); // random number between 1-3
        //final Handler h2 = new Handler();
        boxes[rand].addView(brick);
        lives.subscribe(brick);
        final View p = player[rand];
        brick.getBrickAnimation().start();
        pool.execute(new Runnable() {
            @Override
            public void run() {
                boolean b;
                try {
                    Thread.sleep((long)(BrickView.DUR*0.75));
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                b = p.getTop() >= brick.getBottom();
                if (b && flags[rand] && !semaphore) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                lives.setNumLives(lives.getNumLives() - 1);
                                semaphore = true;
                            }
                        });
                    for(int i = 0; i < 10; i++)
                    {

                        if (i % 2 == 0)
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    p.setBackground(invisible);
                                }
                            });

                        else
                            handler.post(new Runnable() {
                                @Override
                                public void run() {
                                    p.setBackground(visible);
                                }
                            });
                        try {
                            Thread.sleep(50);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    semaphore = false;
                }

            }
        });
    }

    public void invisible(){
        for (int i = 0; i < COLS; i++){
            player[i].setBackground(invisible);
            flags[i] = false;
        }

    }
    @Override
    public void onClick(View v) {
        if (!semaphore){
            invisible();
            v.setBackground(visible);
            for (int i = 0; i < COLS; i++)
                if (v == player[i])
                    flags[i] = true;
        }

    }

    @Override
    public void stateChanged(LivesEvent e) {
        hearts[e.getNumLivesChange()].setBackgroundResource(R.drawable.border);
        if (e.getNumLivesChange() == 0){
            time.stop();
            try (SQLiteDatabase db = dbHelper.getWritableDatabase()) {
            }

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
                            pool.shutdown();
                            finish();
                        }
                    })
                    .setMessage("your time is " + time.getText())
                    .create()
                    .show();
        }
    }
}

