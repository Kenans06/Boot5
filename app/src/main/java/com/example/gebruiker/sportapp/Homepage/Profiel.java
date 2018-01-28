package com.example.gebruiker.sportapp.Homepage;


import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gebruiker.sportapp.R;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.io.File;

import static android.app.Activity.RESULT_OK;
import static com.facebook.FacebookSdk.getApplicationContext;


/**
 * @author BOOT-05
 *
 *         Laat de gewonnen kortingsbonnen zien wanneer de aantal stappen is bereikt
 */
public class Profiel extends Fragment {


    private ProgressDialog mProgressDialog;

    private ImageView mImageview1, mImageview2, mImageview3;
    private Button mButton1, mButton2, mButton3;

    public Profiel() {
        // Required empty public constructor
    }

    // alle stepcounter variables
    private Handler handler;
    private Thread detectorTimeStampUpdaterThread;
    private boolean isRunning = true;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_profiel, container, false);


        mProgressDialog = new ProgressDialog(getActivity());

        // delete buttons initialiseren
        mButton1 = (Button) v.findViewById(R.id.del1);
        mButton2 = (Button) v.findViewById(R.id.del2);
        mButton3 = (Button) v.findViewById(R.id.del3);

        mImageview1 = (ImageView) v.findViewById(R.id.korting1);
        mImageview2 = (ImageView) v.findViewById(R.id.korting2);
        mImageview3 = (ImageView) v.findViewById(R.id.korting3);

        //Profielfoto verwijderen
        mButton1.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                //Zet het transparant
                mImageview1.setImageResource(android.R.color.transparent);

            }
        });

        mButton2.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {


                mImageview2.setImageResource(android.R.color.transparent);

            }
        });

        mButton3.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {

                mImageview3.setImageResource(android.R.color.transparent);

            }
        });


        registerForSensorEvents();
        setupDetectorTimestampUpdaterThread();

        return v;

    }

    public void registerForSensorEvents() {

        SensorManager sManager = (SensorManager) getActivity().getSystemService(Context.SENSOR_SERVICE);

        // Step Counter
        /**
         * If the sensor is triggered(onSensorChanged method) the movement is converted in steps.
         * These steps can be called throughout the app via the textViewStepcounter.setText() method.
         */
        sManager.registerListener(new SensorEventListener() {
                                      @Override
                                      public void onSensorChanged(SensorEvent event) {
                                          //Aantal stappen tellen in een Array
                                          float steps = event.values[0];

                                          // show the kortingsbon pop-ups
                                          if (steps > 130) {
                                              // getView() for no errors
                                              mImageview1 = (ImageView) getView().findViewById(R.id.korting1);

                                              mImageview1.setImageResource(R.drawable.intersport_kortingsbon);

                                          }
                                          if (steps > 5000) {
                                              mImageview2 = (ImageView) getView().findViewById(R.id.korting2);

                                              mImageview2.setImageResource(R.drawable.footlocker_kortingsbon);

                                          }
                                          if (steps > 50000) {
                                              mImageview3 = (ImageView) getView().findViewById(R.id.korting3);

                                              mImageview3.setImageResource(R.drawable.sport_coupon);

                                          }

                                      }

                                      @Override
                                      public void onAccuracyChanged(Sensor sensor, int accuracy) {

                                      }

                                  },
                //Sensordelay
                sManager.getDefaultSensor(Sensor.TYPE_STEP_COUNTER),
                SensorManager.SENSOR_DELAY_UI);


    }


    /**
     * Keeps the stepcounter running.
     */
    private void setupDetectorTimestampUpdaterThread() {
        handler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);

            }
        };

        detectorTimeStampUpdaterThread = new Thread() {
            @Override
            public void run() {
                while (isRunning) {
                    try {
                        Thread.sleep(5000);
                        handler.sendEmptyMessage(0);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
            }
        };

        detectorTimeStampUpdaterThread.start();

    }


}



