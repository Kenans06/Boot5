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
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;

import java.util.Calendar;
import java.util.List;

/**
 * @author BOOT-05
 *
 * Zorgt ervoor dat beperkte informatie over evenementen van vandaag laat zien in RecyclerView
 */

public class EventAdapter extends RecyclerView.Adapter<EventAdapter.EventviewHolder> {

    private Calendar c;
    List<EventModel> eventList;
    Context ctx;

    //Automatisch gegenereerd
    public EventAdapter(List<EventModel> eventList, Context ctx) {
        this.eventList = eventList;
        this.ctx = ctx;
    }

    @Override
    public EventviewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item, parent, false);
        EventviewHolder holder = new EventviewHolder(v, ctx, eventList);
        return holder;
    }

    //Hier wordt de RecyclerView bewerkt
    @Override
    public void onBindViewHolder(EventviewHolder holder, int position) {
        //Neemt van de model zijn positie en voegt het toe in de ArrayList
        EventModel eventModel = eventList.get(position);

        //Neemt de huidige datum en tijd van de toestel mee
        c = Calendar.getInstance();
        //initialiseren van huidige dag, maand en jaar
        int curDay = c.get(Calendar.DAY_OF_MONTH);
        int curMonth = c.get(Calendar.MONTH);
        int curYear = c.get(Calendar.YEAR);

        //Als het de huidige dag is, zijn de events zichtbaar
        if (eventModel.getDay() == curDay && eventModel.getMonth() == curMonth + 1
                && eventModel.getYear() == curYear) {
            //Set de titel van de evenement
            holder.eventName.setText(eventModel.getTitle());
            holder.eventCards.setVisibility(View.VISIBLE);

            //Als de evenement vol is, dan is de eventkaart donkergrijs
            if (eventModel.getIngeschreven() == eventModel.getDeelnemers()) {
                holder.eventCards.setCardBackgroundColor(Color.parseColor("#c9252525"));
            }
            //Als de evenement nog 5 of minder plaatsen heeft, dan is de eventkaart geel
            else if (eventModel.getIngeschreven() >= eventModel.getDeelnemers() - 5 &&
                    eventModel.getIngeschreven() <= eventModel.getDeelnemers() - 1) {
                holder.eventCards.setCardBackgroundColor(Color.parseColor("#c9fff700"));
            }
            //Anders, is de eventkaart wit
            else {
                holder.eventCards.setCardBackgroundColor(Color.parseColor("#c9ffffff"));
            }
            //Dit fixt het eentallige minuut dat de tijd niet bijv. 10:3 laat zien ipv 10:03
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

            //Set het adres van de evenement
            holder.eventLocation.setText(eventModel.getAddress());


        } else {
            //Als de evenement niet vandaag is, dan wordt de eventkaart niet zichtbaar
            holder.eventCards.setVisibility(View.GONE);
        }

    }

    @Override
    public int getItemCount() {
        //haalt grootte van Arraylist op
        return eventList.size();
    }


    public static class EventviewHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        TextView eventName, eventTime, eventLocation;
        CardView eventCards;
        List<EventModel> eventList;
        Context ctx;

        //Hier worden ze geïnitialiseerd
        public EventviewHolder(View v, Context ctx, List<EventModel> eventList) {
            super(v);
            this.eventList = eventList;
            this.ctx = ctx;
            //haalt OnClick methode op
            v.setOnClickListener(this);
            eventName = (TextView) v.findViewById(R.id.eventTitle);
            eventLocation = (TextView) v.findViewById(R.id.eventLocation);
            eventTime = (TextView) v.findViewById(R.id.eventTime);
            eventCards = (CardView) v.findViewById(R.id.Event);


        }

        //Hier wordt de proces gehandeld wanneer er op een eventkaart wordt gekilkt
        @Override
        public void onClick(View v) {
            int position = getAdapterPosition();
            EventModel eventModel = this.eventList.get(position);
            //De context wordt van de EventDetails klasse opgehaald
            Intent intent = new Intent(this.ctx, EventDetails.class);

            //Als een evenement vol is, dan wordt er een toast laten zien en komt er geen nieuw scherm
            if (eventModel.getIngeschreven() == eventModel.getDeelnemers()) {
                Toast.makeText(ctx, R.string.eventFull, Toast.LENGTH_LONG).show();
            } else {
                //Als een evenement bijna vol is, dan wordt er een toast laten zien en open er meer info
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
