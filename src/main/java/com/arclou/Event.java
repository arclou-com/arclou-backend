package com.arclou;

import java.util.concurrent.atomic.AtomicLong;

public class Event {
    private static final AtomicLong count = new AtomicLong(0);

    private final Long id = count.incrementAndGet();
    private final String name;
    private final String date;
    private final String local;

    private final User organizer;

    public Event(String name, String date, String local, User organizer) {
        this.name = name;
        this.date = date;
        this.local = local;
        this.organizer = organizer;
    }

    public Long getId() {
        return id;
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
