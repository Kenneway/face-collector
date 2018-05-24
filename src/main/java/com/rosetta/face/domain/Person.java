package com.rosetta.face.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;

@Data
@Entity
public class Person {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @JsonProperty("person_id")
    private Integer personId;

    private String name;

    private String duty;

    @JsonProperty("favicon_url")
    private String faviconUrl;

    private int status;

    @JsonProperty("create_time")
    private long createTime;

    @JsonProperty("update_time")
    private long updateTime;

    public Person() {
    }

}
