package com.example.gebruiker.sportapp.Beginscherm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gebruiker.sportapp.*;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.*;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class registration extends AppCompatActivity implements View.OnClickListener {

    private EditText userName;
    private EditText emailInput;
    private EditText passwordInput;
    private EditText passwordVerify;
    private Button confirmation;
    private TextView backToLogin;
    private ProgressDialog progressDialog;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String username, password, email, verify, soortAccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registration);

        progressDialog = new ProgressDialog(this);

        //Haalt Authenicatie op
        mAuth = FirebaseAuth.getInstance();

        //Initaliseren
        userName = (EditText) findViewById(R.id.username);
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        passwordVerify = (EditText) findViewById(R.id.passwordVerify);
        confirmation = (Button) findViewById(R.id.confirm);
        backToLogin = (TextView) findViewById(R.id.backToLogin);

        //Haalt als referentie van database de map users op
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //OnClick methode ophalen
        confirmation.setOnClickListener(this);

        //Terug naar loginscherm
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(registration.this, login.class);
                startActivity(intent);
            }
        });
    }

    /**
     * Registers user
     */
    private void registerUser() {
        //Haalt ingevulde informatie op en set het als een String
        email = emailInput.getText().toString().trim();
        password = passwordInput.getText().toString().trim();
        verify = passwordVerify.getText().toString().trim();
        username = userName.getText().toString().trim();

        //Als username is vergeten, dan krijgt het een waarschuwingstoast, code gaat niet verder
        if(TextUtils.isEmpty(username)){
            Toast.makeText(this, R.string.forgottenUsernameToast, Toast.LENGTH_LONG).show();
            return;
        }
        //Als username te kort is, dan krijgt het een waarschuwingstoast, code gaat niet verder
        if(username.length() < 6 || username.length() >20){
            Toast.makeText(this, R.string.charUsernameToast, Toast.LENGTH_LONG).show();
            return;
        }
        //Als email is vergeten, dan krijgt het een waarschuwingstoast, code gaat niet verder
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.forgottenEmailToast, Toast.LENGTH_LONG).show();
            return;
        }
        //Als wachtwoord is vergeten, dan krijgt het een waarschuwingstoast, code gaat niet verder
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.forgottenPasswordToast, Toast.LENGTH_LONG).show();
            return;
        }
        //Als wachtwoorden niet overeenkomen, dan krijgt het een waarschuwingstoast, code gaat niet verder
        if (!password.equals(verify)) {
            Toast.makeText(this, R.string.diffPasswordToast, Toast.LENGTH_LONG).show();
            return;
        }
        //Als wachtwoord te kort is, dan krijgt het een waarschuwingstoast, code gaat niet verder
        if (password.length() < 6) {
            Toast.makeText(this, R.string.charPasswordToast, Toast.LENGTH_LONG).show();
            return;
        }

        //Laat dialoog zien dat de gebruiker weet dat de registratie bezig is
        progressDialog.setMessage(getString(R.string.registeringToast));
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.show();

        //Maakt een Normaal account aan. ingevulde email en wachtwoord wordt opgehaald
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Bij succes
                        if (task.isSuccessful()) {
                            //Haalt huidige gebruiker op
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Set de nieuwe username van de gebruiker.
                            UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest
                                    .Builder().setDisplayName(username).build();
                            //Hier wordt het geüpdatet
                            user.updateProfile(profileUpdates);
                            //Voor de database
                            soortAccount = "normal";
                            //Haalt de ingevulde info op en uit de model
                            UserInfo userInfo = new UserInfo(username,email,soortAccount);
                            //Slaat het op in de database als //users//(UID)//auth//
                            databaseReference.child(user.getUid()).child("auth").setValue(userInfo);
                            //slaat standaard taal op als engels
                            databaseReference.child(user.getUid()).child("language").setValue("en");
                            //Krijgt een succestoast
                            Toast.makeText(registration.this, R.string.succeededRegToast, Toast.LENGTH_SHORT).show();
                            //Gaat weer terug naar de inlogscherm
                            Intent intent = new Intent(registration.this, login.class);
                            startActivity(intent);
                        } else {
                            //Gebruiker krijgt te weten dat het niet werkt
                            Toast.makeText(registration.this, R.string.failedRegToast, Toast.LENGTH_SHORT).show();
                        }
                        //dialoog verdwijnt
                        progressDialog.dismiss();
                    }
                });

    }


    /**
     * Button functions
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if (v == confirmation) {
            registerUser();
        }

    }


}