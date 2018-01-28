package com.example.gebruiker.sportapp.Evenementen;

import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.RequiresApi;
import android.support.v7.app.AppCompatActivity;
import android.util.DisplayMetrics;
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
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.io.IOException;
import java.util.List;


/**
 * @author BOOT-05
 */
public class AddEvent extends AppCompatActivity implements AdapterView.OnItemSelectedListener{


    private Button addLocation;
    private Spinner categorieSpinner;
    private NumberPicker deelnemersPicker;
    private String title, description, address, category;
    private EditText editTitle, editDescription, editAddress;
    private FirebaseAuth mAuth;
    private int day, month, year, hourBegin, minuteBegin, hourEnd,minuteEnd, deelnemers, ingeschreven;
    private DatePicker datePicker;
    private TimePicker timePickerBegin, timePickerEnd;
    private DatabaseReference databaseReference;
    private FirebaseUser user;



    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_event);

        //Initialiseren
        datePicker = (DatePicker) findViewById(R.id.simpleDatePicker);
        timePickerBegin = (TimePicker) findViewById(R.id.timeSetBegin);
        timePickerEnd = (TimePicker) findViewById(R.id.timeSetEnd);
        categorieSpinner = (Spinner) findViewById(R.id.categories);
        deelnemersPicker = (NumberPicker) findViewById(R.id.Aantal_deelnemers);
        addLocation = (Button)findViewById(R.id.addLocation);
        editTitle = (EditText)findViewById(R.id.editTitle);
        editDescription = (EditText)findViewById(R.id.editDescription);
        editAddress = (EditText)findViewById(R.id.editAddress);

        //Array voor de keuzebox(Spinner). Haalt uit de stringrescoure de lijst uit
        ArrayAdapter spinnerAdapter = ArrayAdapter.createFromResource(this,R.array.Categorie,R.layout.support_simple_spinner_dropdown_item);
        categorieSpinner.setAdapter(spinnerAdapter);
        categorieSpinner.setOnItemSelectedListener(this);

        //Dit zorgt ervoor dat het een "zwevend" scherm is
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


        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        //Als er op de voeg toe knop wordt geklikt:
        addLocation.setOnClickListener(new View.OnClickListener() {

            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(View v) {

                //Get de huidige gebruiker
                user = mAuth.getCurrentUser();

                //Maakt een willekeurige evenement id aan
                String id = databaseReference.push().getKey();

                //Haalt alle info op en set het als strings
                title = editTitle.getText().toString();
                description = editDescription.getText().toString();
                address = editAddress.getText().toString();
                day = datePicker.getDayOfMonth();
                month = (datePicker.getMonth()+1);
                year = datePicker.getYear();
                hourBegin = timePickerBegin.getHour();
                minuteBegin = timePickerBegin.getMinute();
                hourEnd = timePickerEnd.getHour();
                minuteEnd = timePickerEnd.getMinute();
                category = categorieSpinner.getSelectedItem().toString();
                deelnemers = deelnemersPicker.getValue();

                //Voor de map
                Geocoder geoCoder = new Geocoder(AddEvent.this);
                double lat = 0;
                double lon = 0;

                //Zet het ingevulde adres op het kaart
                if(address.trim().length() > 0) {
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

                //als adres niet bekend is, dan gaat het niet door
                if(title.trim().length() > 0 && description.trim().length() > 0 && address.trim().length() > 0){
                    if(lat == 0||lon == 0){
                        Toast.makeText(AddEvent.this, R.string.addressError, Toast.LENGTH_LONG).show();
                    } else {

                        user = mAuth.getCurrentUser();

                        //Haalt uit de modelclass de info uit
                        SaveEvent saveEvent = new SaveEvent(title, description, address,category,
                                hourEnd, minuteEnd, day, month, year, hourBegin, minuteBegin, deelnemers, ingeschreven);


                        //Slaat op in database als //users//(UID)//events//(eventid)//
                        databaseReference.child("users").child(user.getUid()).child("events").child(id).setValue(saveEvent);
                        //Slaat op in database als //events/(eventid)//
                        databaseReference.child("events").child(id).setValue(saveEvent);

                        Intent intent = new Intent();
                        intent.putExtra("Title", title);
                        intent.putExtra("Description", description);
                        intent.putExtra("Address", address);
                        intent.setClass(AddEvent.this, GPS.class);
                        startActivity(intent);
                        //Laat toast zien bij succes
                        Toast.makeText(getBaseContext(), R.string.eventSuccess, Toast.LENGTH_SHORT).show();
                        //sluit de pagina af
                        finish();
                    }
                } else {
                    //Als er wat vergeten is, dan wordt er een waarschuwingstoast laten zien
                    Toast.makeText(AddEvent.this, R.string.registrationError, Toast.LENGTH_LONG).show();
                }
            }


        });
    }

    //Automatisch gegenereed en verplicht
    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}
