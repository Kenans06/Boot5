package com.example.gebruiker.sportapp.Beginscherm;

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

import com.example.gebruiker.sportapp.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;

/**
 * @author BOOT-05
 */
public class ResetPage extends AppCompatActivity implements View.OnClickListener {
    private FirebaseAuth mAuth;
    private EditText emailInput;
    private Button confirm;
    private TextView backToLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reset_page);

        //Haalt de authorisatie van Firebase project op
        mAuth = FirebaseAuth.getInstance();

        //Initialiseren
        emailInput = (EditText) findViewById(R.id.emailInput);
        confirm = (Button) findViewById(R.id.confirm);
        backToLogin = (TextView) findViewById(R.id.backToLogin);

        //Haalt de OnClick methode op
        confirm.setOnClickListener(this);

        //Gaat terug naar de loginscherm
        backToLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(ResetPage.this, login.class);
                startActivity(intent);
            }
        });

    }

    @Override
    public void onClick(View v) {
        if (v == confirm) {
            passwordReset();
        }
    }

    /**
     * @author BOOT-05
     *
     * Sends email with passwordreset link
     */
    private void passwordReset() {
        //Haalt de ingevoerde email op en set het als een String
        String email = emailInput.getText().toString().trim();

        //Als er niks wordt ingevuld, dan komt er een waarschuwingstoast. Code gaat niet verder.
        if (TextUtils.isEmpty(email)) {
            Toast.makeText(this, R.string.forgottenEmailToast, Toast.LENGTH_SHORT).show();
            return;
        }

        //Verzendingsproces
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Bij succes wordt er een wachtwoordreset verzonden naar mail
                        if (task.isSuccessful()) {
                            Toast.makeText(ResetPage.this, R.string.sentPasswResetToast,
                                    Toast.LENGTH_SHORT).show();
                            //Gaat weer terug naar inlogscherm
                            Intent intent = new Intent(ResetPage.this, login.class);
                            startActivity(intent);
                        } else {
                            //Als het niet bekend is in de database, dan krijgt de gebruiker deze toast
                            Toast.makeText(ResetPage.this, R.string.failedResetToast,
                                    Toast.LENGTH_SHORT).show();
                        }

                    }
                });
    }
}

