package com.rosetta.face.dto;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.rosetta.face.domain.Face;
import lombok.Data;

import javax.persistence.Transient;
import java.util.List;

@Data
public class FrontFace {

    @JsonProperty("image_base64")
    private String imageBase64;

    @JsonProperty("feature_json")
    private String featureJson;

    @JsonProperty("person_id")
    private Integer personId;

    private String name;

    @JsonProperty("favicon_url")
    private String faviconUrl;

    @JsonProperty("camera_id")
    private String cameraId;

    @Transient
    private List<Float> feature;

    public FrontFace() {
    }

    public FrontFace(Face face) {
        this.imageBase64 = face.getImageBase64();
        this.featureJson = face.getFeatureJson();
        this.personId = face.getPersonId();
        this.name = face.getName();
        this.faviconUrl = face.getFaviconUrl();
        this.cameraId = face.getCameraId();
    }

    public List<Float> getFeature() {
        if (feature != null && feature.size() == 1024) {
            return feature;
        } else if(featureJson != null) {
            return JSON.parseArray(featureJson, Float.class);
        } else {
            return null;
        }
    }

    public void setFeature(List<Float> feature) {
        this.feature = feature;
        if (feature == null) {
            this.featureJson = null;
        } else {
            this.featureJson = JSON.toJSONString(feature);
        }
    }

}
