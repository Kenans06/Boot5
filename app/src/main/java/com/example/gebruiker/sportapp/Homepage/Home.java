package com.example.gebruiker.sportapp.Homepage;


import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.gebruiker.sportapp.Evenementen.AddEvent;
import com.example.gebruiker.sportapp.Evenementen.EventAdapter;
import com.example.gebruiker.sportapp.Evenementen.EventModel;
import com.example.gebruiker.sportapp.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;


/**
 * @author BOOT-05
 *
 */
public class Home extends Fragment {

    private RecyclerView recyclerView;
    private FirebaseDatabase database;

    List<EventModel> eventList;
    private FloatingActionButton fab;

    EventAdapter adapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_home, container, false);

        recyclerView = (RecyclerView) v.findViewById(R.id.recyclerView);
        //Haalt de structuur en info van de Recyclerview uit
        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        eventList = new ArrayList<>();
        adapter = new EventAdapter(eventList, this.getContext());
        recyclerView.setAdapter(adapter);

        //Evenementen toevoegknop
        fab = (FloatingActionButton) v.findViewById(R.id.fab);

        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getActivity(), AddEvent.class);
                startActivity(intent);
            }
        });

        database = FirebaseDatabase.getInstance();
        database.getReference("events").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                eventList.removeAll(eventList);
                for (DataSnapshot snapshot : dataSnapshot.getChildren()) {
                    //Haalt de info uit de model op
                    EventModel events = snapshot.getValue(EventModel.class);
                    eventList.add(events);
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
