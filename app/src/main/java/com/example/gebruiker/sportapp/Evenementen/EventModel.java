package com.example.gebruiker.sportapp.Evenementen;

/**
 * @author BOOT-05
 *         Model for Events
 */

public class EventModel {
    private String title, description, address, category;
    int hourBegin, minuteBegin, hourEnd, minuteEnd, day, month, year, deelnemers, ingeschreven;


    public EventModel() {
        //required public constructor
    }

    public EventModel(String title, String description, String address, String category, int hourBegin, int minuteBegin, int hourEnd, int minuteEnd, int day, int month, int year, int deelnemers, int ingeschreven) {
        this.title = title;
        this.description = description;
        this.address = address;
        this.category = category;
        this.hourBegin = hourBegin;
        this.minuteBegin = minuteBegin;
        this.hourEnd = hourEnd;
        this.minuteEnd = minuteEnd;
        this.day = day;
        this.month = month;
        this.year = year;
        this.deelnemers = deelnemers;
        this.ingeschreven = ingeschreven;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getAddress() {
        return address;
    }

    public void setAddress(String address) {
        this.address = address;
    }

    public int getHourBegin() {
        return hourBegin;
    }

    public void setHourBegin(int hourBegin) {
        this.hourBegin = hourBegin;
    }

    public int getMinuteBegin() {
        return minuteBegin;
    }

    public void setMinuteBegin(int minuteBegin) {
        this.minuteBegin = minuteBegin;
    }

    public int getHourEnd() {
        return hourEnd;
    }

    public void setHourEnd(int hourEnd) {
        this.hourEnd = hourEnd;
    }

    public int getMinuteEnd() {
        return minuteEnd;
    }

    public void setMinuteEnd(int minuteEnd) {
        this.minuteEnd = minuteEnd;
    }

    public int getDay() {
        return day;
    }

    public void setDay(int day) {
        this.day = day;
    }

    public int getMonth() {
        return month;
    }

    public void setMonth(int month) {
        this.month = month;
    }

    public int getYear() {
        return year;
    }

    public void setYear(int year) {
        this.year = year;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public int getDeelnemers() {
        return deelnemers;
    }

    public void setDeelnemers(int deelnemers) {
        this.deelnemers = deelnemers;
    }

    public int getIngeschreven() {
        return ingeschreven;
    }

    public void setIngeschreven(int ingeschreven) {
        this.ingeschreven = ingeschreven;
    }
}