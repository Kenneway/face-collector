package com.rosetta.face.domain;

import com.alibaba.fastjson.JSON;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Data;

import javax.persistence.*;
import java.util.List;

@Data
@Entity
public class Face {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private long id;

    @JsonProperty("feature_json")
    @Lob
    @Column(columnDefinition="TEXT")
    private String featureJson;

    @JsonProperty("image_url")
    private String imageUrl;

    @JsonProperty("camera_id")
    private String cameraId;

    @JsonProperty("person_id")
    private Integer personId;

    private Integer status;

    @JsonProperty("create_time")
    private Long createTime;

    @JsonProperty("update_time")
    private Long updateTime;

    /******************************************************************************************************************/

    @Transient
    private List<Integer> rect;

    @Transient
    private List<Float> feature;

    @Transient
    @JsonProperty("image_base64")
    private String imageBase64;

    @Transient
    private String name;

    @Transient
    @JsonProperty("favicon_url")
    private String faviconUrl;

    public Face() {
    }

    /******************************************************************************************************************/

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
