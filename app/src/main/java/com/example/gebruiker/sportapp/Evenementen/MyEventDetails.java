package com.example.gebruiker.sportapp.Evenementen;

import android.content.DialogInterface;
import android.content.Intent;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

/**
 * @author BOOT-05
 *
 * Hier wordt de infoscherm van de gebruiker aangemaakte evenementen laten zien met bewerk functie
 *
 */

public class MyEventDetails extends AppCompatActivity implements View.OnClickListener {
    private TextView naam, beschrijving, adres, tijd, datum, deelnemers, categorie;
    private Button edit, delete;
    private String title;
    int inschrijving;
    private FirebaseDatabase database;
    private DatabaseReference databaseReference;
    private FirebaseUser user;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_event_details);
        //Initialiseren
        naam = (TextView) findViewById(R.id.myEventNaam);
        beschrijving = (TextView) findViewById(R.id.myEventBeschrijving);
        adres = (TextView) findViewById(R.id.myEventAdres);
        tijd = (TextView) findViewById(R.id.myEventTijd);
        datum = (TextView) findViewById(R.id.myEventDatum);
        deelnemers = (TextView) findViewById(R.id.myEventDeelnemers);
        categorie = (TextView) findViewById(R.id.myEventCategorie);
        edit = (Button) findViewById(R.id.edit);
        edit.setOnClickListener(this);
        delete= (Button) findViewById(R.id.verwijder);
        delete.setOnClickListener(this);

        //Zwevend scherm
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;
        getWindow().setLayout((int) (width * .95), (int) (height * .9));


        title = getIntent().getStringExtra("naam");
        database = FirebaseDatabase.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();

        //Haalt de info uit de MyEventAdapter gehaald
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
        deelnemers.setText(getIntent().getIntExtra("ingeschreven", 0) + "/"
                + getIntent().getIntExtra("deelnemers", 0) + " deelnemers");
        beschrijving.setText(getIntent().getStringExtra("beschrijving"));

    }


    EventModel eventModel = new EventModel();

    public void onClick(View v) {
        //Bij het klikken van bewerken wordt de volgende proces uitgevoerd
        if (v == edit) {
            //Waarschuwingsdialoog
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setIcon(R.drawable.ic_event_available_black_24dp);
            mBuilder.setTitle(R.string.edit_title);
            mBuilder.setMessage(R.string.edit_message);
            //Hier worden de berwerkte informatie veranderd
            mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent intent = new Intent(getBaseContext(), EditEvent.class);
                    intent.putExtra("naam",getIntent().getStringExtra("naam"));
                    intent.putExtra("adres", getIntent().getStringExtra("adres"));
                    intent.putExtra("tijdBeginUur", getIntent().getIntExtra("tijdBeginUur", 0));
                    intent.putExtra("tijdBeginMinuut", getIntent().getIntExtra("tijdBeginMinuut", 0));
                    intent.putExtra("tijdEindUur", getIntent().getIntExtra("tijdEindUur", 0));
                    intent.putExtra("tijdEindMinuut",  getIntent().getIntExtra("tijdEindMinuut", 0));
                    intent.putExtra("dag", getIntent().getIntExtra("dag", 0));
                    intent.putExtra("maand", getIntent().getIntExtra("maand", 0));
                    intent.putExtra("jaar", getIntent().getIntExtra("jaar", 0));
                    intent.putExtra("category", getIntent().getStringExtra("category"));
                    intent.putExtra("ingeschreven",getIntent().getIntExtra("ingeschreven", 0));
                    intent.putExtra("deelnemers", getIntent().getIntExtra("deelnemers", 0));
                    intent.putExtra("beschrijving",getIntent().getStringExtra("beschrijving"));
                    startActivity(intent);


                }
            });
            mBuilder.setNegativeButton(R.string.no, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
        }
        if(v == delete){
            //Gebruiker wordt gewaargeschuwd met een dialoog
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setIcon(R.drawable.ic_event_available_black_24dp);
            mBuilder.setTitle(R.string.deleteEvent);
            mBuilder.setMessage(R.string.deleteEventQuest);
            //Bij Ja wordt het evenement verwijderd
            mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    databaseReference = FirebaseDatabase.getInstance().getReference();
                    //Leest uit de database, als de titel in de database gelijk is aan de titel in de app
                    databaseReference.child("events")
                            .orderByChild("title")
                            .equalTo(title)
                            .addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot snapshot) {
                                    for (DataSnapshot shot : snapshot.getChildren()) {
                                        //Haalt de eventid uit de database
                                        String key = shot.getKey();
                                        //Evenement wordt verwijderd
                                        databaseReference.child("users").child(user.getUid()).child("events").child(key).removeValue();
                                        databaseReference.child("events").child(key).removeValue();
                                        //Succestoast
                                        Toast.makeText(MyEventDetails.this, "Event is succesvol verwijderd",
                                                Toast.LENGTH_LONG).show();
                                    }
                                    finish();
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
}