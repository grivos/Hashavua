package com.invariantlabs.hashavua.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaFeedWrapper {

    @SerializedName("feed")
    @Expose
    private HashavuaFeed feed;


}
