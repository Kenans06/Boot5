package com.example.gebruiker.sportapp.Evenementen;

/**
 * @author Boot-05
 */

public class SaveEvent {
    public String title, description, address, category;
    public int hourEnd, minuteEnd, day, month, year, hourBegin, minuteBegin, deelnemers, ingeschreven;

    public SaveEvent(String title, String description, String address, String catergory, int hourEnd, int minuteEnd,
                     int day, int month, int year, int hourBegin, int minuteBegin, int deelnemers, int ingeschreven) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.category = catergory;
        this.hourEnd = hourEnd;
        this.minuteEnd = minuteEnd;
        this.day = day;
        this.month = month;
        this.year = year;
        this.hourBegin = hourBegin;
        this.minuteBegin = minuteBegin;
        this.deelnemers = deelnemers;
        this.ingeschreven = ingeschreven;
    }
}
