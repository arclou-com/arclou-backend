package com.arclou;

public class Event {
    private String name;
    private String date;
    private String local;

    private User user;

    public Event(String name, String date, String local, User user) {
        this.name = name;
        this.date = date;
        this.local = local;
        this.user = user;
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

    public User getUser() {
        return user;
    }
}
