package com.example.gebruiker.sportapp.Homepage.Instellingen.Coupons;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.ImageView;

import com.example.gebruiker.sportapp.R;

public class stepPopUp2 extends AppCompatActivity {

    private ImageView mImageview;
    private Button okButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_step_pop_up2);

        mImageview = (ImageView) findViewById(R.id.kortingsbon2);

        mImageview.setImageResource(R.drawable.intersport_kortingsbon);


        okButton = (Button)findViewById(R.id.okButton2);
        okButton.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();

            }

        });

    }
}
