package com.rosetta.face.dto;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

@Data
public class FrontResult {

    private Integer code;

    private String msg;

    private Object data;

}
