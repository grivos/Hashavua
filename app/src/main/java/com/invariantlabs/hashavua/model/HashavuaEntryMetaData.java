package com.invariantlabs.hashavua.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaEntryMetaData {

    private String url;
    private boolean isWatched;
    private boolean isFavorite;

}
