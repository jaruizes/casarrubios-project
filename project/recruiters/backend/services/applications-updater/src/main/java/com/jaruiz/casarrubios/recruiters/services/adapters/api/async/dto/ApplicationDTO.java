package com.jaruiz.casarrubios.recruiters.services.adapters.api.async.dto;

import java.io.Serial;
import java.io.Serializable;

public class ApplicationDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private Long id;
    private Long positionId;
    private String name;
    private String email;
    private String phone;
    private String cv;
    private long created_at;

    public ApplicationDTO() { }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Long getPositionId() {
        return positionId;
    }

    public void setPositionId(Long positionId) {
        this.positionId = positionId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
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
}
