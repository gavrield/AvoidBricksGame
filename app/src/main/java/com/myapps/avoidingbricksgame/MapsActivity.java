package com.myapps.avoidingbricksgame;

import androidx.fragment.app.FragmentActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {

    private  final int ROWS = 10;
    private GoogleMap mMap;
    private TableRow[] tableRows;
    private TextView[] names;
    private TextView[] scores;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_maps);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        createArrays();
    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        // Add a marker in Sydney and move the camera
        LatLng sydney = new LatLng(-34, 151);
        mMap.addMarker(new MarkerOptions().position(sydney).title("Marker in Sydney"));
        mMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    private void createArrays(){
        tableRows = new TableRow[ROWS];
        tableRows[0] = findViewById(R.id.row0);
        tableRows[1] = findViewById(R.id.row1);
        tableRows[2] = findViewById(R.id.row2);
        tableRows[3] = findViewById(R.id.row3);
        tableRows[4] = findViewById(R.id.row4);
        tableRows[5] = findViewById(R.id.row5);
        tableRows[6] = findViewById(R.id.row6);
        tableRows[7] = findViewById(R.id.row7);
        tableRows[8] = findViewById(R.id.row8);
        tableRows[9] = findViewById(R.id.row9);

        names = new TextView[ROWS];
        names[0] = findViewById(R.id.name0);
        names[1] = findViewById(R.id.name1);
        names[2] = findViewById(R.id.name2);
        names[3] = findViewById(R.id.name3);
        names[4] = findViewById(R.id.name4);
        names[5] = findViewById(R.id.name5);
        names[6] = findViewById(R.id.name6);
        names[7] = findViewById(R.id.name7);
        names[8] = findViewById(R.id.name8);
        names[9] = findViewById(R.id.name9);

        scores = new TextView[ROWS];
        scores[0] = findViewById(R.id.score0);
        scores[1] = findViewById(R.id.score1);
        scores[2] = findViewById(R.id.score2);
        scores[3] = findViewById(R.id.score3);
        scores[4] = findViewById(R.id.score4);
        scores[5] = findViewById(R.id.score5);
        scores[6] = findViewById(R.id.score6);
        scores[7] = findViewById(R.id.score7);
        scores[8] = findViewById(R.id.score8);
        scores[9] = findViewById(R.id.score9);
    }
}
