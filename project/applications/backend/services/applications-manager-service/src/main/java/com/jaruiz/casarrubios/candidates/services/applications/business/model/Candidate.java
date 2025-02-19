package com.jaruiz.casarrubios.candidates.services.applications.business.model;

public class Candidate {
    private final String name;
    private final String email;
    private final String phone;

    public Candidate(String name, String email, String phone) {
        this.name = name;
        this.email = email;
        this.phone = phone;
    }

    public boolean isComplete() {
        return name != null && email != null && phone != null;
    }

    public String getName() {
        return name;
    }


    public String getEmail() {
        return email;
    }

    public String getPhone() {
        return phone;
    }
}
