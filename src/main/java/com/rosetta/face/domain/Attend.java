package com.rosetta.face.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.sql.Date;

@Data
@Entity
public class Attend {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("person_id")
    private Integer personId;

    private Date date;

    @JsonProperty("start_time")
    private long startTime;

    @JsonProperty("end_time")
    private long endTime;

    @JsonProperty("track_log")
    @Lob
    @Column(columnDefinition="TEXT")
    private String trackLog;

    @JsonProperty("create_time")
    private long createTime;

    @JsonProperty("update_time")
    private long updateTime;
}
