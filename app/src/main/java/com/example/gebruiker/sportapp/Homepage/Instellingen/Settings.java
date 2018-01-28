package com.example.gebruiker.sportapp.Homepage.Instellingen;


import android.content.Intent;
import android.content.res.Configuration;
import android.os.Bundle;
import android.support.design.internal.NavigationMenu;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatActivity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.Toast;
import android.content.res.Resources;
import android.util.DisplayMetrics;
import com.example.gebruiker.sportapp.Homepage.MainActivity;
import com.example.gebruiker.sportapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Locale;

/**
 * @author BOOT-05
 *         A simple {@link Fragment} subclass.
 */
public class Settings extends Fragment {

    private DatabaseReference databaseReference;
    private FirebaseUser user;
    private FirebaseAuth mAuth;
    private Button savePreferences, dutchTrans, englishTrans;
    private CheckBox checkBox1, checkBox2, checkBox3, checkBox4, checkBox5, checkBox6, checkBox7, checkBox8, checkBox9;
    private boolean cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9;


    public Settings() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_settings, container, false);

        //initialiseren van de Checkboxes
        checkBox1 = (CheckBox) v.findViewById(R.id.etenBox);
        checkBox2 = (CheckBox) v.findViewById(R.id.muziekBox);
        checkBox3 = (CheckBox) v.findViewById(R.id.sportenBox);
        checkBox4 = (CheckBox) v.findViewById(R.id.gamingBox);
        checkBox5 = (CheckBox) v.findViewById(R.id.dierenBox);
        checkBox6 = (CheckBox) v.findViewById(R.id.schrijvenBox);
        checkBox7 = (CheckBox) v.findViewById(R.id.winkelenBox);
        checkBox8 = (CheckBox) v.findViewById(R.id.familieBox);
        checkBox9 = (CheckBox) v.findViewById(R.id.vriendenBox);

        //Firebase authenticatie met onze project
        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        user = mAuth.getCurrentUser();

        savePreferences = (Button) v.findViewById(R.id.savePreferences);

        dutchTrans = (Button) v.findViewById(R.id.nl);

        englishTrans = (Button) v.findViewById(R.id.eng);

        dutchTrans.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setLocale("nl");
                databaseReference.child("users").child(user.getUid()).child("language").setValue("nl");
            }
        });

        englishTrans.setOnClickListener(new View.OnClickListener(){

            @Override
            public void onClick(View view) {
                setLocale("en");
                databaseReference.child("users").child(user.getUid()).child("language").setValue("en");
            }
        });

        //Als er niks wordt aangeklikt, dan krijg je een toast en gaat de code niet verder.
        savePreferences.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!checkBox1.isChecked() && !checkBox2.isChecked() && !checkBox3.isChecked()
                        && !checkBox4.isChecked() && !checkBox5.isChecked()
                        && !checkBox6.isChecked() && !checkBox7.isChecked()
                        && !checkBox8.isChecked() && !checkBox9.isChecked()) {
                    Toast.makeText(getActivity(), R.string.sportFail, Toast.LENGTH_LONG).show();
                    return;
                }

                //Als het gechecked is, dan returned het true en anders false
                if (checkBox1.isChecked()) {
                    cb1 = true;
                } else {
                    cb1 = false;
                }

                if (checkBox2.isChecked()) {
                    cb2 = true;
                } else {
                    cb2 = false;
                }

                if (checkBox3.isChecked()) {
                    cb3 = true;
                } else {
                    cb3 = false;
                }

                if (checkBox4.isChecked()) {
                    cb4 = true;
                } else {
                    cb4 = false;
                }

                if (checkBox5.isChecked()) {
                    cb5 = true;
                } else {
                    cb5 = false;
                }

                if (checkBox6.isChecked()) {
                    cb6 = true;
                } else {
                    cb6 = false;
                }

                if (checkBox7.isChecked()) {
                    cb7 = true;
                } else {
                    cb7 = false;
                }

                if (checkBox8.isChecked()) {
                    cb8 = true;
                } else {
                    cb8 = false;
                }

                if (checkBox9.isChecked()) {
                    cb9 = true;
                } else {
                    cb9 = false;
                }

                //Haalt uit de andere class
                sUserPreferences suserPreferences = new sUserPreferences(cb1, cb2, cb3, cb4, cb5, cb6, cb7, cb8, cb9);

                //Gaat terug naar Mainactivity(dus homepage)
                Intent intent = new Intent(getActivity(), MainActivity.class);
                startActivity(intent);

                //Haalt gebruiker op
                user = mAuth.getCurrentUser();

                //Slaat op in de database als users//(Uid)//preferences//
                databaseReference.child("users").child(user.getUid()).child("preferences").setValue(suserPreferences);

                //Toast voor bevestiging
                Toast.makeText(getActivity(), R.string.saveSuccess, Toast.LENGTH_LONG).show();

            }
        });

        return v;
    }

    /**
     *
     * @param lang afkorting van resourcebundle
     */
    public void setLocale(String lang) {
        //maakt nieuwe locale met string afkorting
        Locale myLocale = new Locale(lang);
        //maakt resources op (strings)
        Resources res = getResources();
        //haalt resouces op
        DisplayMetrics dm = res.getDisplayMetrics();
        //zorgt dat de juiste resources opgehaalt worden
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
        //refesht app om de juiste locale op te halen -> taal
        Intent refresh = new Intent(getActivity(), MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }
}

