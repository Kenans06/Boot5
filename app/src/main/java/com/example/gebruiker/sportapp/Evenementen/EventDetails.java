package com.example.gebruiker.sportapp.Evenementen;

import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gebruiker.sportapp.R;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;
import java.util.Objects;

/**
 * @author BOOT-05
 *
 * Laat de Eventdetails zien van de gekozen event
 *
 */

public class EventDetails extends AppCompatActivity implements View.OnClickListener,OnMapReadyCallback{

    private TextView naam, beschrijving, adres, tijd, datum, deelnemer, categorie;
    private Button assign;
    private String title, description, address, category;
    private int inschrijving, day, month, year, hourBegin, minuteBegin, hourEnd,minuteEnd,
            deelnemers, ingeschreven;
    private FirebaseDatabase database;
    private List<Address> newAddress;
    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private GoogleMap mMap;
    private  static Geocoder coder;
    private LatLng p1;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);

        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map2);
        mapFragment.getMapAsync(this);

        //Initialiseren
        naam = (TextView) findViewById(R.id.eventNaam);
        beschrijving = (TextView) findViewById(R.id.eventBeschrijving);
        adres = (TextView) findViewById(R.id.eventAdres);
        tijd = (TextView) findViewById(R.id.eventTijd);
        datum = (TextView) findViewById(R.id.eventDatum);
        deelnemer = (TextView) findViewById(R.id.eventDeelnemers);
        categorie = (TextView) findViewById(R.id.eventCategorie);
        assign = (Button) findViewById(R.id.assign);
        assign.setOnClickListener(this);

        //Zwevend Scherm
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .95), (int) (height * .9));

        coder = new Geocoder(this);
        p1 = null;

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        user = mAuth.getCurrentUser();
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Haalt info op van de Event Adapter
        naam.setText(getIntent().getStringExtra("naam"));
        adres.setText(getIntent().getStringExtra("adres"));
        tijd.setText(getIntent().getIntExtra("tijdBeginUur", 0) + ":"
                + getIntent().getIntExtra("tijdBeginMinuut", 0) + " - "
                + getIntent().getIntExtra("tijdEindUur", 0) + ":"
                + getIntent().getIntExtra("tijdEindMinuut", 0));

        datum.setText(getIntent().getIntExtra("dag", 0) + "/"
                + getIntent().getIntExtra("maand", 0) + "/"
                + getIntent().getIntExtra("jaar", 0));
        categorie.setText(getIntent().getStringExtra("category"));
        deelnemer.setText(getIntent().getIntExtra("ingeschreven", 0) + "/"
                + getIntent().getIntExtra("deelnemers", 0) + " deelnemers");
        beschrijving.setText(getIntent().getStringExtra("beschrijving"));

        //Voor de database
        title = getIntent().getStringExtra("naam");
        description = getIntent().getStringExtra("beschrijving");
        address = getIntent().getStringExtra("adres");
        category = getIntent().getStringExtra("category");
        day = getIntent().getIntExtra("dag", 0);
        month = getIntent().getIntExtra("maand", 0);
        year = getIntent().getIntExtra("jaar", 0);
        hourBegin = getIntent().getIntExtra("tijdBeginUur", 0);
        hourEnd = getIntent().getIntExtra("tijdEindUur", 0);
        minuteBegin = getIntent().getIntExtra("tijdBeginMinuut", 0);
        minuteEnd = getIntent().getIntExtra("tijdEindMinuut",0);
        deelnemers = getIntent().getIntExtra("deelnemers", 0);
        ingeschreven = getIntent().getIntExtra("ingeschreven", 0);
    }

    EventModel eventModel = new EventModel();

    @Override
    public void onClick(View v) {
        //Wanneer er op inschrijven wordt geklikt
        if(v == assign){
            //Gebruiker wordt gewaargeschuwd met een dialoog
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setIcon(R.drawable.ic_event_available_black_24dp);
            mBuilder.setTitle(R.string.enroll);
            mBuilder.setMessage(R.string.enrollQuest);
            //Bij Ja volgt een inschrijvingsproces...
            mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    inschrijving = getIntent().getIntExtra("ingeschreven", 0);
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    //Leest uit de database, als de titel in de database gelijk is aan de titel in de app
                    databaseReference.child("events")
                            .orderByChild("title")
                            .equalTo(title)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot snapshot) {
                            for (DataSnapshot shot: snapshot.getChildren()) {
                                //Haalt de eventid uit de database
                                String key = shot.getKey();
                                //Bij inschrijving wordt "inschrijving met 1 verhoogd
                                databaseReference.child("events").child(key).child("ingeschreven")
                                        .setValue(inschrijving+1);
                                //Intent wordt afgesloten
                                finish();
                                SaveEvent saveEvent = new SaveEvent(title, description, address,category,
                                        hourEnd, minuteEnd, day, month, year, hourBegin, minuteBegin, deelnemers, ingeschreven);
                                //Wordt extra opgeslagen in de database onder de gebruiker
                                databaseReference.child("users").child(user.getUid()).child("enrolledEvents").child(key)
                                        .setValue(saveEvent);
                                //Succestoast
                                Toast.makeText(EventDetails.this, R.string.enrollSuccess,
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                        @Override
                        public void onCancelled(DatabaseError firebaseError) {
                            Log.e("Read failed", firebaseError.getMessage());
                        }
                    });

                }
            });
            //Als er Nee wordt gekozen wordt dialoog gesloten
            mBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            //laat dialoog zien
            alertDialog.show();
        }

    }
    //GMaps laat de locatie van de evenement zien
    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

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
