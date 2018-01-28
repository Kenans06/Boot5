package com.example.gebruiker.sportapp.Homepage;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.example.gebruiker.sportapp.Beginscherm.login;
import com.example.gebruiker.sportapp.Evenementen.Events;
import com.example.gebruiker.sportapp.Evenementen.MyEvents;
import com.example.gebruiker.sportapp.R;
import com.example.gebruiker.sportapp.Homepage.Instellingen.Settings;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.Locale;

/**
 * @author BOOT-05
 *
 * Dit is de hele app die ervoor zorgt dat bij elk keuzemenu een andere fragment laat zien
 * Hier wordt gebruik van de navigation drawer gemaakt
 */
public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private TextView username, email;
    private FirebaseUser mUser;
    private FirebaseAuth mAuth;
    private DatabaseReference databaseReference;
    private String language;

    private ImageButton profilePic;
    private StorageReference mStorage;
    private ProgressDialog mProgressDialog;
    private static final int GALLERY_INTENT = 2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);



        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        mAuth = FirebaseAuth.getInstance();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        mUser = mAuth.getCurrentUser();
        mStorage = FirebaseStorage.getInstance().getReference();
        mProgressDialog = new ProgressDialog(this);

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        View headerView = navigationView.getHeaderView(0);

        //initialiseren van de gebruikersinfo in de header van de Navigatiedrawer
        username = (TextView) headerView.findViewById(R.id.usernameNav);
        email = (TextView) headerView.findViewById(R.id.emailNav);
        profilePic = (ImageButton) headerView.findViewById(R.id.androidPic);

        //Veranderen van profielfoto
        profilePic.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(Intent.ACTION_PICK);

                intent.setType("image/*");

                startActivityForResult(intent, GALLERY_INTENT);
            }

        });

        //Haalt van de huidige gebruiker de username en email op en voety
        username.setText(mUser.getDisplayName());
        email.setText(mUser.getEmail());

        //Zorgt ervoor dat het eerst begint met de homepage
        Fragment fragment = null;
        Class fragmentClass = null;
        fragmentClass = Home.class;
        try {
            fragment = (Fragment) fragmentClass.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }

        FragmentManager fragmentManager = getSupportFragmentManager();
        fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
        //set de titel van de toolbar
        getSupportActionBar().setTitle(R.string.nav_home);

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.setDrawerListener(toggle);
        toggle.syncState();

        loadProfilePic();

        navigationView.setNavigationItemSelectedListener(this);
    }

    //Hoe de profielfoto wordt upgeload in de firebase Storage en hoe het linkt met de gebruiker
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == GALLERY_INTENT && resultCode == RESULT_OK) {

            mProgressDialog.setMessage(getResources().getString(R.string.uploadMes));
            mProgressDialog.show();

            //Net gekozen foto wordt in uri opgeslagen
            Uri uri = data.getData();

            // in firebase database > storage een 'photos' folder aanmaken
            StorageReference filePath = mStorage.child("photos").child(mUser.getUid()).child("profilepic");
            filePath.putFile(uri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(MainActivity.this, R.string.uploadSuc, Toast.LENGTH_LONG).show();
                    mProgressDialog.dismiss();

                    @SuppressWarnings("VisibleForTests") Uri downloadUri = taskSnapshot.getDownloadUrl();
                    Picasso.with(MainActivity.this).load(downloadUri).fit().centerCrop().into(profilePic);

                }
            });

        }
    }

    //Laad uit de storage de profielfoto d.m.v. ophalen van de URL en het laden van de URL
    public void loadProfilePic() {
        StorageReference filePath = mStorage.child("photos").child(mUser.getUid()).child("profilepic");
        filePath.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                Glide
                        .with(getApplicationContext())
                        .load(uri)
                        .centerCrop()
                        .into(profilePic);
            }
        });

    }

    //Als er op de terug knop wordt gedrukt terwijl de drawer open is, dan wordt de drawer gesloten
    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        //Dit is voor de info over de homepage
        if (id == R.id.infoHome) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setIcon(R.drawable.ic_info_black_24dp);
            mBuilder.setTitle(R.string.nav_InfoHome);
            mBuilder.setMessage(R.string.navInfoMes);
            mBuilder.setPositiveButton(R.string.nextMes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    AlertDialog.Builder mBuilder = new AlertDialog.Builder(MainActivity.this);
                    mBuilder.setIcon(R.drawable.ic_info_black_24dp);
                    mBuilder.setTitle(R.string.nav_InfoHome);
                    mBuilder.setMessage(R.string.navInfoMes2);
                    mBuilder.setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            dialog.dismiss();
                        }
                    });
                    AlertDialog alertDialog = mBuilder.create();
                    alertDialog.show();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        //Initialiseren van de Fragment/Fragmentclass
        Fragment fragment = null;
        Class fragmentClass = null;

        //Hier worden alle functies van de keuzemenu gestart

        if (id == R.id.nav_home) {
            fragmentClass = Home.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle(R.string.nav_home);
        } else if (id == R.id.nav_map) {
            Intent intent = new Intent(this, GPS.class);
            startActivity(intent);
        } else if (id == R.id.nav_event) {
            fragmentClass = Events.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle(R.string.nav_events);
        } else if (id == R.id.nav_settings) {
            fragmentClass = Settings.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle(R.string.nav_settings);
        } else if (id == R.id.nav_help) {
            fragmentClass = hulpEnInfo.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle(R.string.nav_faq);

        } else if (id == R.id.nav_logout) {
            AlertDialog.Builder mBuilder = new AlertDialog.Builder(this);
            mBuilder.setIcon(R.drawable.ic_phonelink_erase_black_24dp);
            mBuilder.setTitle(R.string.logout);
            mBuilder.setMessage(R.string.logoutMes);
            mBuilder.setPositiveButton(R.string.yes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialogInterface, int i) {
                    databaseReference.child("users").child(mUser.getUid()).child("language").addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {
                            language = dataSnapshot.getKey().toString();
                            setLocale(language);
                            mAuth.signOut();
                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });
                }
            });

            mBuilder.setNegativeButton(R.string.noMes, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            });
            AlertDialog alertDialog = mBuilder.create();
            alertDialog.show();

        } else if (id == R.id.nav_profile) {
            fragmentClass = Profiel.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle(R.string.nav_profile);
        } else if (id == R.id.nav_my_events) {
            fragmentClass = MyEvents.class;
            try {
                fragment = (Fragment) fragmentClass.newInstance();
            } catch (Exception e) {
                e.printStackTrace();
            }

            FragmentManager fragmentManager = getSupportFragmentManager();
            fragmentManager.beginTransaction().replace(R.id.flContent, fragment).commit();
            getSupportActionBar().setTitle(R.string.nav_myEvents);

        }

        //Hiermee wordt de drawer bij elke keuze uit de drawer gesloten.
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
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
        Intent refresh = new Intent(this,login.class);
        refresh.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
        startActivity(refresh);
    }

}
