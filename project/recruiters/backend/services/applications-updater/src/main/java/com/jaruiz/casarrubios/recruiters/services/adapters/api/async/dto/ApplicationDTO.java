package com.jaruiz.casarrubios.recruiters.services.adapters.api.async.dto;

import java.io.Serial;
import java.io.Serializable;

public class ApplicationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private String id;
    private String name;
    private String surname;
    private String email;
    private String phone;
    private String cv;
    private long created_at;

    public ApplicationDTO() { }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getSurname() {
        return surname;
    }

    public void setSurname(String surname) {
        this.surname = surname;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getCv() {
        return cv;
    }

    public void setCv(String cv) {
        this.cv = cv;
    }

    public long getCreated_at() {
        return created_at;
    }

    public void setCreated_at(long created_at) {
        this.created_at = created_at;
    }

    @Override public String toString() {
        return "ApplicationDTO{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", surname='" + surname + '\'' + ", email='" + email + '\'' + ", phone='" + phone + '\'' + ", cv='" + cv + '\'' + ", created_at=" + created_at + '}';
    }
}
