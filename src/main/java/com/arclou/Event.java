package com.arclou;

public class Event {
    private String name;
    private String date;
    private String local;

    public Event(String name, String date, String local) {
        this.name = name;
        this.date = date;
        this.local = local;
    }

    public String getName() {
        return name;
    }

    public String getDate() {
        return date;
    }

    public String getLocal() {
        return local;
    }
}
