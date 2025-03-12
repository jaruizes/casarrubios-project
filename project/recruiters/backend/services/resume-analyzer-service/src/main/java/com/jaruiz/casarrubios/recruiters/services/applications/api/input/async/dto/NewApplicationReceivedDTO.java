package com.jaruiz.casarrubios.recruiters.services.applications.api.input.async.dto;

import java.io.Serial;
import java.io.Serializable;
import java.util.UUID;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class NewApplicationReceivedDTO implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;
    private UUID id;
    @JsonProperty("position_id")
    private Long positionId;
    private String name;
    private String email;
    private String phone;
    private String cv;
    @JsonProperty("created_at")
    private Long createdAt;
}
