package com.invariantlabs.hashavua.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.Data;

@Data
public class JsonValue {

    @SerializedName("$t")
    @Expose
    private String value;
}
