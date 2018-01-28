package com.example.gebruiker.sportapp.Beginscherm;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gebruiker.sportapp.Homepage.MainActivity;
import com.example.gebruiker.sportapp.R;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInApi;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Locale;


public class login extends AppCompatActivity implements View.OnClickListener {

    private static final int RC_SIGN_IN = 1;
    private static final String TAG = "LOGIN";

    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private EditText emailInput, passwordInput;
    private Button logInNormal, logInAnon, logInGoogle;
    private TextView noAccount, forgotInfo;
    private GoogleApiClient mGoogleApiClient;
    private GoogleSignInApi GoogleSignInApi;
    private ProgressDialog progressDialog;
    private DatabaseReference databaseReference;
    private String email, soortAccount, language;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        //Initialiseren
        noAccount = (TextView) findViewById(R.id.noAccount);
        emailInput = (EditText) findViewById(R.id.emailInput);
        passwordInput = (EditText) findViewById(R.id.passwordInput);
        logInNormal = (Button) findViewById(R.id.loginNormal);
        logInAnon = (Button) findViewById(R.id.loginGuest);
        logInGoogle = (Button) findViewById(R.id.loginGoogle);
        forgotInfo = (TextView) findViewById(R.id.wrongPassword);

        progressDialog = new ProgressDialog(this);
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);

        //Onze Firebase linken en als referentie de map "users" ophalen
        databaseReference = FirebaseDatabase.getInstance().getReference("users");

        //Firebase Authenticatie linken
        mAuth = FirebaseAuth.getInstance();
        //Zorgt ervoor dat het ingelogd blijft als de applicatie wordt afgesloten
        mAuthListener = new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                if (firebaseAuth.getCurrentUser() != null) {
                    //gaat direct naar de Hoofdscherm
                    progressDialog.setMessage(getString(R.string.loginMes));
                    progressDialog.show();
                    FirebaseUser user = mAuth.getCurrentUser();
                    databaseReference.child(user.getUid()).child("language").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            language = dataSnapshot.getValue().toString();
                            setLocale(language);
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }

            }
        };

        //Auto generated
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        /*Laat toast zien als er geen connectie kan worden gemaakt met je google account, return
          statement zorgt ervoor dat de code niet meer verder gaat */
        mGoogleApiClient = new GoogleApiClient.Builder(getApplicationContext()).enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toast.makeText(login.this, R.string.conFail, Toast.LENGTH_SHORT).show();
                return;
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API, gso).build();

        //Luistert naar de onClick methode, 'this' betekent de onClick methode in deze klasse
        logInNormal.setOnClickListener(this);
        logInAnon.setOnClickListener(this);
        logInGoogle.setOnClickListener(this);
        //Dialoog om de verloping te weten


        /*Als de gebruiker nog geen (normaal) account heeft,
         dan klikt de gebruiker deze knop naar de registratiescherm te gaan*/
        noAccount.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, registration.class);
                startActivity(intent);
            }
        });

        /*Als de gebruiker zijn wachtwoord is vergeten,
         dan klikt de gebruiker deze knop naar de resetscherm te gaan*/
        forgotInfo.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(login.this, ResetPage.class);
                startActivity(intent);
            }
        });

    }

    //Bij start van de app, check het of de gebruiker al ingelogd is
    protected void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }


    //Google Login
    private void signIn() {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(mGoogleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }


    //Activity Start voor Google Login
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            GoogleSignInResult result = Auth.GoogleSignInApi.getSignInResultFromIntent(data);
            if (result.isSuccess()) {
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = result.getSignInAccount();

                //Aanroep Methode
                firebaseAuthWithGoogle(account);
            } else {
                Toast.makeText(login.this, R.string.loginFail, Toast.LENGTH_SHORT).show();
                // Google Sign In failed, update UI appropriately
                // ...
            }
        }
    }

    /**
     * logs in with google single sign on
     * @param account account
     */
    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {

        //Laat dialoog zien om de gebruiker te laten weten dat het inloggen bezig is
        progressDialog.setMessage(getString(R.string.loginMes));
        progressDialog.show();

        //Voor google login
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Bij succes
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithCredential:success");

                            //Haalt de gebruiker op
                            FirebaseUser user = mAuth.getCurrentUser();
                            //Haalt de email van de gebruiker op
                            email = user.getEmail();
                            //Voor de database
                            soortAccount = "google";
                            //Haalt uit de model op en get de values
                            UserInfo gUserInfo = new UserInfo(email,soortAccount);
                            //Slaat in de database op als //users//(UID)//auth//
                            databaseReference.child(user.getUid()).child("auth").setValue(gUserInfo);
                            databaseReference.child(user.getUid()).child("language").setValue("en");
                            //Laat een welkom toast zien
                            Toast.makeText(login.this, R.string.welcomeToast,
                                    Toast.LENGTH_SHORT).show();

                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithCredential:failure", task.getException());
                            //Laat een error toast zien
                            Toast.makeText(login.this, R.string.failedLoginToast,
                                    Toast.LENGTH_SHORT).show();

                        }
                        //Sluit het dialoog na het einde van het proces(in elk geval)
                        progressDialog.dismiss();

                        // ...
                    }
                });
    }

    /**
     * Logs in with email address and password
     */

    private void signInNormal() {
        //Haalt de ingevoerde text op van de gebruiker.
        String email = emailInput.getText().toString().trim();
        String password = passwordInput.getText().toString().trim();

        /*Als de gebruiker zijn email niet heeft ingevoerd, dan wordt hij gewaarschuwd en gaat
        de code niet verder*/
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.forgottenEmailToast, Toast.LENGTH_SHORT).show();
            return;
        }
        /*Als de gebruiker zijn wachtwoord niet heeft ingevoerd, dan wordt hij gewaarschuwd en gaat
        de code niet verder*/
        if (TextUtils.isEmpty(password)) {
            Toast.makeText(this, R.string.forgottenPasswordToast, Toast.LENGTH_SHORT).show();
            return;
        }
        /*Als de gebruiker zijn wachtwoord met minder dan zes tekens heeft ingevoerd,
        dan wordt hij gewaarschuwd en gaat de code niet verder*/
        if (password.length() < 6) {
            Toast.makeText(this, R.string.charPasswordToast, Toast.LENGTH_LONG).show();
            return;
        }

        //Laat dialoog zien
        progressDialog.setMessage(getString(R.string.loginMes));
        progressDialog.show();

        //Normaal inlogproces
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Bij succes
                        if (task.isSuccessful()) {
                            //Gaat naar het hoofdscherm
                            Intent intent = new Intent(login.this, MainActivity.class);
                            startActivity(intent);
                            //Een welkomsttoast
                            Toast.makeText(login.this, R.string.welcomeToast, Toast.LENGTH_SHORT).show();
                        } else {
                            //Bij mislukking een toast
                            Toast.makeText(login.this, R.string.failedLoginToast, Toast.LENGTH_SHORT).show();
                        }
                        //Dialoog verdwijnt
                        progressDialog.dismiss();
                    }
                });

    }

    /**
     * Logs in anonymously
     */
    private void anonymousSignIn() {

        //Laat dialoog zien
        progressDialog.setMessage(getString(R.string.loginMes));
        progressDialog.show();

        mAuth.signInAnonymously()
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        //Bij succes
                        if (task.isSuccessful()) {
                            //Gaat verder naar hoofdscherm
                            FirebaseUser user = mAuth.getCurrentUser();
                            databaseReference.child(user.getUid()).child("auth").setValue("guest");
                            databaseReference.child(user.getUid()).child("language").setValue("en");
                            setLocale("en");
                            //Welkomsttoast
                            Toast.makeText(login.this, R.string.annLoginMes, Toast.LENGTH_LONG).show();

                        } else {
                            //Bij mislukking een toast
                            Toast.makeText(login.this, R.string.failedAnonLogin,
                                    Toast.LENGTH_SHORT).show();
                        }
                        //Dialoog verdwijnt
                        progressDialog.dismiss();
                    }
                });
    }

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
        Intent refresh = new Intent(this, MainActivity.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

    @Override
    public void onBackPressed() {
        finish();
    }

    /**
     * Button functions
     * @param v View
     */
    @Override
    public void onClick(View v) {
        if (v == logInNormal) {
            signInNormal();
        }
        if (v == logInAnon) {
            anonymousSignIn();
        }

        if (v == logInGoogle) {
            signIn();
        }

    }
}