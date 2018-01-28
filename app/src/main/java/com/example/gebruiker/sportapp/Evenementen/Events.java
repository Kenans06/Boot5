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
 * @author BOOT-05
 *         Hier worden alle Evenementen geladen en oude evenementen verwijderd
 */
public class Events extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private FloatingActionButton fab;
    List<EventModel> eventList;
    FullEventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_events, container, false);

        Calendar c = Calendar.getInstance();
        final int CUR_DAY = c.get(Calendar.DAY_OF_MONTH);
        final int CUR_MONTH = c.get(Calendar.MONTH);
        final int CUR_YEAR = c.get(Calendar.YEAR);

        recyclerView = (RecyclerView) v.findViewById(R.id.eventView);
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventList = new ArrayList<>();

        adapter = new FullEventAdapter(eventList, this.getContext());
        recyclerView.setAdapter(adapter);

        //Evenementen toevoegknop
        fab = (FloatingActionButton) v.findViewById(R.id.fab1);
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
        database.getReference("events").addValueEventListener(new ValueEventListener() {
            @Override
            //Hier worden verouderde evenementen verwijderd
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.removeAll(eventList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    EventModel events = snapshot.getValue(EventModel.class);
                    if (events.getDay() < CUR_DAY) {
                        database.getReference("events").child(snapshot.getKey()).removeValue();
                    } else if (events.getMonth() < CUR_MONTH + 1) {
                        database.getReference("events").child(snapshot.getKey()).removeValue();
                    } else if (events.getYear() < CUR_YEAR) {
                        database.getReference("events").child(snapshot.getKey()).removeValue();
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
