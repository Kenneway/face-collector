package com.rosetta.face.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import java.sql.Date;

@Data
@Entity
public class Record {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("person_id")
    private Integer personId;

    @JsonProperty("camera_id")
    private String cameraId;

    private Date date;

    @JsonProperty("create_time")
    private long createTime;

    public Record() {
    }

}
