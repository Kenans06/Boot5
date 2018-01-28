package com.example.gebruiker.sportapp.Evenementen;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.support.v7.widget.CardView;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.gebruiker.sportapp.R;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Calendar;
import java.util.List;

/**
 * @author BOOT-05
 *
 * Adapter om de gebruiker aangemaakte evenementen te laten zien
 */

public class MyEventAdapter extends RecyclerView.Adapter<MyEventAdapter.EventviewHolder>{

    private Calendar c;
    private FirebaseDatabase database;

    private String eventId;
    List<EventModel> eventList;
    Context ctx;

    public MyEventAdapter(List<EventModel> eventList, Context ctx) {
        this.eventList = eventList;
        this.ctx = ctx;
    }

    @Override
    public MyEventAdapter.EventviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.fullevent_list, parent, false);
        MyEventAdapter.EventviewHolder holder = new MyEventAdapter.EventviewHolder(v,ctx,eventList);
        return holder;
    }

    @Override
    public void onBindViewHolder(MyEventAdapter.EventviewHolder holder, int position) {
        EventModel eventModel = eventList.get(position);

        c = Calendar.getInstance();

        holder.eventCards.setCardBackgroundColor(Color.parseColor("#c9ffffff"));

        holder.eventName.setText(eventModel.getTitle());
        holder.eventDate.setText(eventModel.getDay() + "/" + eventModel.getMonth() + "/" + eventModel.getYear());
        if (eventModel.getMinuteBegin() == 0 || eventModel.getMinuteBegin() == 1 || eventModel.getMinuteBegin() == 2 ||
                eventModel.getMinuteBegin() == 3 || eventModel.getMinuteBegin() == 4 || eventModel.getMinuteBegin() == 5 ||
                eventModel.getMinuteBegin() == 6 || eventModel.getMinuteBegin() == 7 ||
                eventModel.getMinuteBegin() == 8 || eventModel.getMinuteBegin() == 9 &&
                eventModel.getMinuteEnd() == 0 || eventModel.getMinuteEnd() == 1 || eventModel.getMinuteEnd() == 2 ||
                eventModel.getMinuteEnd() == 3 || eventModel.getMinuteEnd() == 4 || eventModel.getMinuteEnd() == 5 ||
                eventModel.getMinuteEnd() == 6 || eventModel.getMinuteEnd() == 7 || eventModel.getMinuteEnd() == 8 ||
                eventModel.getMinuteEnd() == 9) {
            holder.eventTime.setText(eventModel.getHourBegin() + ":0" + eventModel.getMinuteBegin()
                    + " - " + eventModel.getHourEnd() + ":0" + eventModel.getMinuteEnd());

        } else if (eventModel.getMinuteBegin() == 0 || eventModel.getMinuteBegin() == 1 || eventModel.getMinuteBegin() == 2 ||
                eventModel.getMinuteBegin() == 3 || eventModel.getMinuteBegin() == 4 || eventModel.getMinuteBegin() == 5 ||
                eventModel.getMinuteBegin() == 6 || eventModel.getMinuteBegin() == 7 ||
                eventModel.getMinuteBegin() == 8 || eventModel.getMinuteBegin() == 9) {
            holder.eventTime.setText(eventModel.getHourBegin() + ":0" + eventModel.getMinuteBegin()
                    + " - " + eventModel.getHourEnd() + ":" + eventModel.getMinuteEnd());

        } else if (eventModel.getMinuteEnd() == 0 || eventModel.getMinuteEnd() == 1 || eventModel.getMinuteEnd() == 2 ||
                eventModel.getMinuteEnd() == 3 || eventModel.getMinuteEnd() == 4 || eventModel.getMinuteEnd() == 5 ||
                eventModel.getMinuteEnd() == 6 || eventModel.getMinuteEnd() == 7 ||
                eventModel.getMinuteEnd() == 8 || eventModel.getMinuteEnd() == 9) {
            holder.eventTime.setText(eventModel.getHourBegin() + ":" + eventModel.getMinuteBegin()
                    + " - " + eventModel.getHourEnd() + ":0" + eventModel.getMinuteEnd());

        } else {
            holder.eventTime.setText(eventModel.getHourBegin() + ":" + eventModel.getMinuteBegin()
                    + " - " + eventModel.getHourEnd() + ":" + eventModel.getMinuteEnd());

        }
        holder.eventLocation.setText(eventModel.getAddress());

        if(eventModel.getIngeschreven() == eventModel.getDeelnemers()) {
            holder.eventCards.setCardBackgroundColor(Color.parseColor("#c9252525"));
        }
        if(eventModel.getIngeschreven() >= eventModel.getDeelnemers()-5 &&
                eventModel.getIngeschreven() <= eventModel.getDeelnemers()-1) {
            holder.eventCards.setCardBackgroundColor(Color.parseColor("#c9fff700"));
        }
    }

    @Override
    public int getItemCount() {
        return eventList.size();
    }

    public static class EventviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener{

        TextView eventName, eventTime, eventLocation, eventDate;
        CardView eventCards;
        List<EventModel> eventList;
        Context ctx;

        public EventviewHolder(View v,Context ctx, List<EventModel> eventList){
            super(v);
            this.eventList = eventList;
            this.ctx = ctx;
            v.setOnClickListener(this);
            eventName = (TextView) v.findViewById(R.id.eventTitle);
            eventLocation = (TextView) v.findViewById(R.id.eventLocation);
            eventTime = (TextView) v.findViewById(R.id.eventTime);
            eventDate = (TextView) v.findViewById(R.id.eventDate);
            eventCards = (CardView) v.findViewById(R.id.Event);

        }

        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            EventModel eventModel = this.eventList.get(position);
            Intent intent = new Intent(this.ctx, MyEventDetails.class);
            if (eventModel.getIngeschreven() == eventModel.getDeelnemers()) {
                Toast.makeText(ctx, R.string.eventFull, Toast.LENGTH_LONG).show();
            } else {
                if (eventModel.getIngeschreven() >= eventModel.getDeelnemers() - 5 &&
                        eventModel.getIngeschreven() <= eventModel.getDeelnemers() - 1) {
                    Toast.makeText(ctx, R.string.eventAlmostFull, Toast.LENGTH_LONG).show();
                }
                intent.putExtra("naam", eventModel.getTitle());
                intent.putExtra("adres", eventModel.getAddress());
                intent.putExtra("tijdBeginUur", eventModel.getHourBegin());
                intent.putExtra("tijdBeginMinuut", eventModel.getMinuteBegin());
                intent.putExtra("tijdEindUur", eventModel.getHourEnd());
                intent.putExtra("tijdEindMinuut", eventModel.getMinuteEnd());
                intent.putExtra("dag", eventModel.getDay());
                intent.putExtra("maand", eventModel.getMonth());
                intent.putExtra("jaar", eventModel.getYear());
                intent.putExtra("category", eventModel.getCategory());
                intent.putExtra("ingeschreven", eventModel.getIngeschreven());
                intent.putExtra("deelnemers", eventModel.getDeelnemers());
                intent.putExtra("beschrijving", eventModel.getDescription());
                this.ctx.startActivity(intent);
            }
        }
    }

}