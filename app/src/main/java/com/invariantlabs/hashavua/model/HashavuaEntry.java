package com.invariantlabs.hashavua.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaEntry {

    private String title;
    private String url;
    private String subject;
    private String mainSunject;
    private boolean isWatched;
    private boolean isFavorite;

    public HashavuaEntry(String title, String url, String subject, String mainSunject) {
        this(title, url, subject, mainSunject, false, false);
    }

}
