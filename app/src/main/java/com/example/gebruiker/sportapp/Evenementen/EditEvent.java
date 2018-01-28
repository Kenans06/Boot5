package com.example.gebruiker.sportapp.Evenementen;

import android.content.DialogInterface;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.NumberPicker;
import android.widget.Spinner;
import android.widget.TimePicker;
import android.widget.Toast;

import com.example.gebruiker.sportapp.Homepage.GPS;
import com.example.gebruiker.sportapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.Query;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.List;

public class EditEvent
        extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    Button edit;
    Spinner categorieSpinner;
    NumberPicker deelnemersPicker;
    String title, description, address, category;
    EditText editTitle, editDescription, editAddress;
    private FirebaseAuth mAuth;
    private int day, month, year, hourBegin, minuteBegin, hourEnd, minuteEnd, deelnemers, ingeschreven;
    private DatePicker datePicker;
    private TimePicker timePickerBegin, timePickerEnd;
    private DatabaseReference databaseReference;
    private FirebaseDatabase databaseFirebase;
    private FirebaseUser user;
    private String eventId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_event);

        //Initialiseren
        datePicker = (DatePicker) findViewById(R.id.simpleDatePickerEdit);
        timePickerBegin = (TimePicker) findViewById(R.id.timeSetBeginEdit);
        timePickerEnd = (TimePicker) findViewById(R.id.timeSetEndEdit);
        categorieSpinner = (Spinner) findViewById(R.id.categoriesEdit);
        editTitle = (EditText) findViewById(R.id.naamEdit);
        editDescription = (EditText) findViewById(R.id.descriptionEdit);
        editAddress = (EditText) findViewById(R.id.adresEdit);
        edit = (Button) findViewById(R.id.bewerk);
        deelnemersPicker = (NumberPicker) findViewById(R.id.Aantal_deelnemersEdit);



        //Haalt uit stringresource de array met een lijst van categorieën
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this, R.array.Categorie,
                R.layout.support_simple_spinner_dropdown_item);
        categorieSpinner.setAdapter(spinnerAdapter);
        categorieSpinner.setOnItemSelectedListener(this);

        //Zwevend scherm effect
        DisplayMetrics dm = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(dm);
        int width = dm.widthPixels;
        int height = dm.heightPixels;

        getWindow().setLayout((int) (width * .95), (int) (height * .9));

        //Zorgt ervoor dat je niet uit het verleden een datum kan kiezen
        datePicker.setMinDate(System.currentTimeMillis() - 1000);

        //Zet de beide TimePickers als 24uur modus
        timePickerBegin.setIs24HourView(true);
        timePickerEnd.setIs24HourView(true);

        //Zorgt ervoor dat je uit de deelnemerPicker alleen 1 t/m 100 deelnemers kan kiezen
        deelnemersPicker.setMinValue(1);
        deelnemersPicker.setMaxValue(100);

        //Haalt alle info van de event op en zet het in de edittexts en in de pickers
        editTitle.setText(getIntent().getStringExtra("naam"));
        editDescription.setText(getIntent().getStringExtra("beschrijving"));
        editAddress.setText(getIntent().getStringExtra("adres"));
        datePicker.updateDate(getIntent().getIntExtra("jaar", 0), getIntent().getIntExtra("maand", 0),
                getIntent().getIntExtra("dag", 0));
        timePickerBegin.setHour(getIntent().getIntExtra("tijdBeginUur", 0));
        timePickerBegin.setMinute(getIntent().getIntExtra("tijdBeginMinuut", 0));
        timePickerEnd.setHour(getIntent().getIntExtra("tijdEindUur", 0));
        timePickerEnd.setMinute(getIntent().getIntExtra("tijdEindMinuut", 0));
        deelnemersPicker.setValue(getIntent().getIntExtra("deelnemers", 0));

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();



        //Als bewerking voltooid is, volgt hetzelfde proces als bij het toevoegen van een evenement
        edit.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                user = mAuth.getCurrentUser();

                String id = databaseReference.push().getKey();

                title = editTitle.getText().toString();
                description = editDescription.getText().toString();
                address = editAddress.getText().toString();
                day = datePicker.getDayOfMonth();
                month = (datePicker.getMonth() + 1);
                year = datePicker.getYear();
                hourBegin = timePickerBegin.getHour();
                minuteBegin = timePickerBegin.getMinute();
                hourEnd = timePickerEnd.getHour();
                minuteEnd = timePickerEnd.getMinute();
                category = categorieSpinner.getSelectedItem().toString();
                deelnemers = deelnemersPicker.getValue();


                Geocoder geoCoder = new Geocoder(EditEvent.this);
                double lat = 0;
                double lon = 0;

                if (address.trim().length() > 0) {
                    try {
                        List<Address> addresses =
                                geoCoder.getFromLocationName(address, 1);
                        if (addresses.size() > 0) {
                            lat = addresses.get(0).getLatitude();
                            lon = addresses.get(0).getLongitude();
                        }

                    } catch (IOException e) { // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }

                if (title.trim().length() > 0 && description.trim().length() > 0 && address.trim().length() > 0) {
                    if (lat == 0 || lon == 0) {
                        Toast.makeText(EditEvent.this, R.string.addressError, Toast.LENGTH_LONG).show();
                    } else {
                        user = mAuth.getCurrentUser();
                        databaseFirebase = FirebaseDatabase.getInstance();
                        Query eventQuery = databaseReference.child("users").child(user.getUid()).child("events").orderByChild("title").equalTo(getIntent().getStringExtra("naam"));

                        eventQuery.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot eventSnapshot : dataSnapshot.getChildren()) {
                                    eventSnapshot.getRef().removeValue();
                                    databaseFirebase.getReference("events").child(eventSnapshot.getKey()).removeValue();
                                    Toast.makeText(EditEvent.this, R.string.editEvent, Toast.LENGTH_SHORT).show();
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });


                        SaveEvent saveEvent = new SaveEvent(title, description, address, category,
                                hourEnd, minuteEnd, day, month, year, hourBegin, minuteBegin, deelnemers, ingeschreven);


                        databaseReference.child("users").child(user.getUid()).child("events").child(id).setValue(saveEvent);
                        databaseReference.child("events").child(id).setValue(saveEvent);

                        Intent intent = new Intent();
                        intent.putExtra("Title", title);
                        intent.putExtra("Description", description);
                        intent.putExtra("Address", address);
                        intent.setClass(EditEvent.this, GPS.class);
                        startActivity(intent);
                        Toast.makeText(getBaseContext(), R.string.eventEditSuccess, Toast.LENGTH_SHORT).show();
                        finish();
                    }
                } else {
                    Toast.makeText(EditEvent.this, R.string.registrationError, Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }
    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}


   

