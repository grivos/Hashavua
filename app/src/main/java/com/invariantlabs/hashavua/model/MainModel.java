package com.invariantlabs.hashavua.model;

import java.util.List;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor(suppressConstructorProperties=true)
@NoArgsConstructor
public class MainModel {

    private List<HashavuaEntry> entries;
    private List<HashavuaSubject> subjects;
    private List<HashavuaMainSubject> mainSubjects;
    private int totalItems;
    private int filteredItems;


}
