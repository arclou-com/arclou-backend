package com.arclou;

public class Event {
    private String name;
    private String date;
    private String local;

    private User organizer;

    public Event(String name, String date, String local, User organizer) {
        this.name = name;
        this.date = date;
        this.local = local;
        this.organizer = organizer;
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

    public User getOrganizer() {
        return organizer;
    }
}
