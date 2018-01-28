package com.example.gebruiker.sportapp.Homepage.Instellingen.Coupons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gebruiker.sportapp.R;

public class stepPopUp extends AppCompatActivity {

    private ImageView mImageview;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_pop_up);

        //Initialiseren
        mImageview = (ImageView) findViewById(R.id.kortingsbon);

        mImageview.setImageResource(R.drawable.sport_coupon);


        //Na het klikken op OK, sluit de popup
        okButton = (Button) findViewById(R.id.okButton);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }

        });
    }
}
