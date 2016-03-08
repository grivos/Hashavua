package com.invariantlabs.hashavua.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class HashavuaSubject implements Comparable<HashavuaSubject>, Filterable{

    private String value;
    private boolean isEnabled;

    @Override
    public int compareTo(HashavuaSubject hashavuaSubject) {
        return hashCode() - hashavuaSubject.hashCode();
    }
}
