package com.example.gebruiker.sportapp.Homepage;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.gebruiker.sportapp.Evenementen.AddEvent;
import com.example.gebruiker.sportapp.Evenementen.EventModel;
import com.example.gebruiker.sportapp.R;
import com.example.gebruiker.sportapp.Homepage.Instellingen.Coupons.stepPopUp;
import com.example.gebruiker.sportapp.Homepage.Instellingen.Coupons.stepPopUp2;
import com.example.gebruiker.sportapp.Homepage.Instellingen.Coupons.stepPopUp3;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

/**
 * @author BOOT-05
 *
 * GPS Map met stappenteller, kortingsbonnen en evenementen toevoegen.
 * Mogelijk om te navigeren met de ingebouwde Google Maps API
 */

public class GPS extends FragmentActivity implements OnMapReadyCallback {

    private FirebaseDatabase database;

    private GoogleMap mMap;
    private static Geocoder coder;
    private LatLng p1;

    private Button addEvent;
    private Button backToHome;
    private List<Address> newAddress;
    private List<Address> oldAddress;
    private String title;
    private String description;
    private String address;

    private TextView textViewStepCounter;
    private Thread detectorTimeStampUpdaterThread;
    private Handler handler;
    private boolean isRunning = true;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_gps);

        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        textViewStepCounter = (TextView) findViewById(R.id.stepcounter);
        backToHome = (Button) findViewById(R.id.backToHome);

        registerForSensorEvents();

        setupDetectorTimestampUpdaterThread();

        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .95), (int) (height * .9));


        addEvent = (Button) findViewById(R.id.add);

        addEvent.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GPS.this, AddEvent.class);
                startActivity(intent);
            }
        });

        backToHome.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(GPS.this, MainActivity.class);
                startActivity(intent);
            }
        });

        Intent intent = getIntent();
        title = intent.getStringExtra("Title");
        description = intent.getStringExtra("Description");
        address = intent.getStringExtra("Address");


        coder = new Geocoder(this);
        p1 = null;


        database = FirebaseDatabase.getInstance();
        database.getReference("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventModel events = snapshot.getValue(EventModel.class);

                    try {
                        oldAddress = coder.getFromLocationName(events.getAddress(), 5);
                        Address location = oldAddress.get(0);
                        location.getLatitude();
                        location.getLongitude();
                        p1 = new LatLng((float) (location.getLatitude()),
                                (float) (location.getLongitude()));

                        float latValue = (float) p1.latitude;
                        float longValue = (float) p1.longitude;

                        LatLng newLocation = new LatLng(latValue, longValue);
                        mMap.addMarker(new MarkerOptions().position(newLocation).title(events.getTitle()).snippet(events.getDescription()));
                    } catch (IOException e) {
                        e.printStackTrace();
                    }


                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public void registerForSensorEvents() {
        SensorManager sManager = (SensorManager) getSystemService(Context.SENSOR_SERVICE);

        // Step Counter
        /**
         * If the sensor is triggered(onSensorChanged method) the movement is converted in steps.
         * These steps can be called throughout the app via the textViewStepcounter.setText() method.
         */
        sManager.registerListener(new SensorEventListener() {
                                      @Override
                                      public void onSensorChanged(SensorEvent event) {
                                          float steps = event.values[0];
                                          textViewStepCounter.setText(R.string.stepsMade + (int) steps
                                                  + "");

                                          // show the kortingsbon pop-ups
                                          if (steps > 130 && steps < 133) {

                                              Intent intent = new Intent(GPS.this, stepPopUp2.class);
                                              startActivity(intent);
                                          }
                                          if (steps > 5000 && steps < 5002) {

                                              Intent intent = new Intent(GPS.this, stepPopUp3.class);
                                              startActivity(intent);
                                          }
                                          if (steps > 50000 && steps < 50002) {

                                              Intent intent = new Intent(GPS.this, stepPopUp.class);
                                              startActivity(intent);
                                          }

                                      }


                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }

                                  }, sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);

    }

    /**
     * Keeps the stepcounter running.
     */
    private void setupDetectorTimestampUpdaterThread() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };

        detectorTimeStampUpdaterThread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        detectorTimeStampUpdaterThread.start();

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

        // Move camera to nieuw west and zoom in
        LatLng nieuwWest = new LatLng(52.3662, 4.8041);
        mMap.moveCamera(CameraUpdateFactory.newLatLng(nieuwWest));
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(nieuwWest, 15));

        if (address == null) {


        } else {

            try {
                newAddress = coder.getFromLocationName(address, 5);
                Address location = newAddress.get(0);
                location.getLatitude();
                location.getLongitude();

                p1 = new LatLng((float) (location.getLatitude()),
                        (float) (location.getLongitude()));

                //Turning the coordinates into a string
                float latValue = (float) p1.latitude;
                float longValue = (float) p1.longitude;

                // Add a marker to entered address and move the camera
                LatLng newLocation = new LatLng(latValue, longValue);
                mMap.addMarker(new MarkerOptions().position(newLocation).title(title).snippet(description));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(newLocation));
                mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 15));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
