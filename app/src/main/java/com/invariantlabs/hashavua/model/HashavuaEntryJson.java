package com.invariantlabs.hashavua.model;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaEntryJson {

    @Expose
    @SerializedName("gsx$כותרת")
    private JsonValue title;
    @Expose
    @SerializedName("gsx$קישורלפוסט")
    private JsonValue url;
    @Expose
    @SerializedName("gsx$נושא")
    private JsonValue subject;
}
