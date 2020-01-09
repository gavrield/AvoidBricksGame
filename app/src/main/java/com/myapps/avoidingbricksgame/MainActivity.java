package com.myapps.avoidingbricksgame;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.Manifest;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.drawable.ColorDrawable;
import android.graphics.drawable.Drawable;

import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.SystemClock;
import android.view.View;

import android.widget.Chronometer;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;




public class MainActivity
        extends AppCompatActivity
        implements View.OnClickListener, ChangeListener, LocationListener{

    private static final int LOCATION_PERMISSION_REQUEST_CODE = -100;
    private final int ELAPSED = 2000;
    private final int COLS = 5;
    private final int MAX_THREADS = 12;
    private final int AVOID_POINTS = 1;
    private boolean[] flags;
    private View[] hearts;
    private View[] player;
    private RelativeLayout[] boxes;
    private Drawable visible;
    private Drawable invisible;
    private Lives lives;
    private Chronometer time;
    private TextView pointsView;
    private Integer points;
    private boolean ongoingGame;
    private Handler handler;
    private boolean semaphore;
    private ExecutorService pool;
    private ScorsDbHelper dbHelper;
    private Location currentLocation = null;
    private LocationManager locationManager;
    private boolean didAlreadyRequestLocationPermission;
    private SharedPreferences preferences;

    public MainActivity() {
        handler = new Handler();
        player = new View[COLS];
        boxes = new RelativeLayout[COLS];
        hearts = new View[Lives.getInitLives()];
        flags = new boolean[COLS];
        invisible = new ColorDrawable(0);
        semaphore = false;
        dbHelper = new ScorsDbHelper(this);
        locationManager = (LocationManager) getApplicationContext().getSystemService(LOCATION_SERVICE);
        preferences = getApplication().getSharedPreferences(getString(R.string.app_name), Context.MODE_PRIVATE);
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

        pointsView = findViewById(R.id.points);

    }

    @Override
    protected void onStart(){
        super.onStart();
        getCurrentLocation();
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
        pool = Executors.newFixedThreadPool(MAX_THREADS);
        for (int i = 0; i < COLS; i++){
            flags[i] = i == COLS/2; // flags[COLS/2 + 1] = true else flags[i] = false
            player[i].setOnClickListener(this);
        }
        points = 0;
        pointsView.setText(points.toString());
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            visible = new ColorDrawable(getColor(R.color.colorPrimary));
        }
        time.setBase(SystemClock.elapsedRealtime());
        time.refreshDrawableState();
        time.start();
        ongoingGame = true;

        new Thread(new Runnable() {
            @Override
            public void run() {
                while(lives.getNumLives() > 0 && ongoingGame){
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
        if (lives.getNumLives() == 0)
            return;
        final BrickView brick = new BrickView(this);

        final int rand  = (int)(Math.random()*(COLS)); // random number between 0-4

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
                b = p.getTop() >= brick.getBottom()
                    && p.getBottom() >= brick.getTop(); // collision
                if (b && flags[rand] && !semaphore) {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                lives.setNumLives(lives.getNumLives() - 1);
                                semaphore = true;
                            }
                        });
                    for(int i = 0; i < 10; i++) //blinking animation
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
                } else
                {
                    if (lives.getNumLives() > 0)
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                updatePoints(AVOID_POINTS);
                            }
                        });

                }

            }
        });
    }
    @Override
    public void onPause() {
        time.stop();
        ongoingGame = false;
        super.onPause();
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
                ContentValues values = new ContentValues();
                values.put
                        (ScoresContract.Scores.COLUMN_NAME_NAME,
                                preferences.getString(getString(R.string.nickname),null));
                values.put(ScoresContract.Scores.COLUMN_NAME_SCORE, points);
                values.put(ScoresContract.Scores.COLUMN_NAME_LAT, currentLocation.getLatitude());
                values.put(ScoresContract.Scores.COLUMN_NAME_LONG, currentLocation.getLongitude());
                db.insert(ScoresContract.Scores.TABLE_NAME, null, values);
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

                            finish();
                        }
                    })
                    .setMessage(getString(R.string.your_score_is)+ " " + points.toString())
                    .create()
                    .show();
        }

    }

    private synchronized void updatePoints(int addedPoints){
        points += addedPoints;
        pointsView.setText(points.toString());
    }

    private void getCurrentLocation() {
        boolean isAccessGranted;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            String fineLocationPermission = Manifest.permission.ACCESS_FINE_LOCATION;
            String coarseLocationPermission = Manifest.permission.ACCESS_COARSE_LOCATION;
            if (getApplicationContext().checkSelfPermission(fineLocationPermission) != PackageManager.PERMISSION_GRANTED ||
                    getApplicationContext().checkSelfPermission(coarseLocationPermission) != PackageManager.PERMISSION_GRANTED) {
                // The user blocked the location services of THIS app / not yet approved
                isAccessGranted = false;
                if (!didAlreadyRequestLocationPermission) {
                    didAlreadyRequestLocationPermission = true;
                    String[] permissionsToAsk = new String[]{fineLocationPermission, coarseLocationPermission};
                    requestPermissions(permissionsToAsk, LOCATION_PERMISSION_REQUEST_CODE);
                }
            } else {
                // Because the user's permissions started only from Android M and on...
                isAccessGranted = true;
            }

            if (currentLocation == null) {
                currentLocation = locationManager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }


            if (isAccessGranted) {
                float metersToUpdate = 1;
                long intervalMilliseconds = 1000;
                locationManager.
                        requestLocationUpdates
                                (LocationManager.GPS_PROVIDER,
                                        intervalMilliseconds,
                                        metersToUpdate, this);
            }
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        currentLocation = location;
    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {

    }

    @Override
    public void onProviderEnabled(String provider) {

    }

    @Override
    public void onProviderDisabled(String provider) {

    }
}

