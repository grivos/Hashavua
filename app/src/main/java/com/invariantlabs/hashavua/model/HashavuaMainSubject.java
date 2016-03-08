package com.invariantlabs.hashavua.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaMainSubject implements Comparable<HashavuaMainSubject>, Filterable{

    private String value;
    private boolean isEnabled;

    @Override
    public int compareTo(HashavuaMainSubject hashavuaMainSubject) {
        return hashCode() - hashavuaMainSubject.hashCode();
    }
}