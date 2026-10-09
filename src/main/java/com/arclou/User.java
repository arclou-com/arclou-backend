package com.arclou;

import java.util.concurrent.atomic.AtomicLong;

public class User {
    private static final AtomicLong count = new AtomicLong(0);

    private final Long id = count.incrementAndGet();
    private final String name;
    private final String email;

    public User(String name, String email) {
        this.name = name;
        this.email = email;
    }

    public Long getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getEmail() {
        return email;
    }

    @Override
    public String toString() {
        return "Nome: " + name + " - " + "E-mail: " + email;
    }
}
