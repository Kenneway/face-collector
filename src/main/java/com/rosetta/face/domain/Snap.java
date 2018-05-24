package com.rosetta.face.domain;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

@Data
public class Snap {

    @JsonProperty("camera_id")
    private String cameraId;

    @JsonProperty("snap_time")
    private Long snapTime;

}
