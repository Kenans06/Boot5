package com.example.gebruiker.sportapp.Evenementen;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gebruiker.sportapp.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

/**
 * @author Boot-05
 */

public class MyEvents extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FloatingActionButton fab;
    List<EventModel> eventList;
    MyEventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_my_events, container, false);

        Calendar c = Calendar.getInstance();
        final int CUR_DAY = c.get(Calendar.DAY_OF_MONTH);
        final int CUR_MONTH = c.get(Calendar.MONTH);
        final int CUR_YEAR = c.get(Calendar.YEAR);

        recyclerView = (RecyclerView) v.findViewById(R.id.myView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventList = new ArrayList<>();

        adapter = new MyEventAdapter(eventList, this.getContext());
        recyclerView.setAdapter(adapter);

        fab = (FloatingActionButton) v.findViewById(R.id.fab2);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEvent.class);
                startActivity(intent);
            }
        });

        auth = FirebaseAuth.getInstance();
        database = FirebaseDatabase.getInstance();
        user = auth.getCurrentUser();
        database.getReference("users").child(user.getUid()).child("events").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.removeAll(eventList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventModel events = snapshot.getValue(EventModel.class);
                    //because the count of curDay en curMonth starts at 0 we need to add +1 for the correct month
                    //Deletes event the day after its done
                    if (events.getDay() < CUR_DAY) {
                        database.getReference("users").child(user.getUid()).child("events").child((snapshot.getKey())).removeValue();
                    } else if (events.getMonth() < CUR_MONTH + 1) {
                        database.getReference("users").child(user.getUid()).child("events").child((snapshot.getKey())).removeValue();
                    } else if (events.getYear() < CUR_YEAR) {
                        database.getReference("users").child(user.getUid()).child("events").child((snapshot.getKey())).removeValue();
                    } else {
                        eventList.add(events);
                    }

                }
                adapter.notifyDataSetChanged();
            }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        return v;
    }


}
