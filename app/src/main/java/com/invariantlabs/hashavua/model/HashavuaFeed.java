package com.invariantlabs.hashavua.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaFeed {

    @SerializedName("entry")
    @Expose
    private List<HashavuaEntryJson> entries;
}
