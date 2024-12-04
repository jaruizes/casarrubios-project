package com.jaruiz.casarrubios.recruiters.services.business.model;

import java.util.UUID;

public class Application {

    private final UUID id;
    private final String name;
    private final String surname;
    private final String email;
    private final String phone;
    private final String cv;
    private final long createdAt;

    public Application(UUID id, String name, String surname, String email, String phone, String cv, long createdAt) {
        this.id = id;
        this.name = name;
        this.surname = surname;
        this.email = email;
        this.phone = phone;
        this.cv = cv;
        this.createdAt = createdAt;
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getSurname() {
        return surname;
    }

    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }

    public String getCv() {
        return cv;
    }

    public long getCreatedAt() {
        return createdAt;
    }
}
