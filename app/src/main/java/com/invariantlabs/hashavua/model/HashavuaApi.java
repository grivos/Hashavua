package com.invariantlabs.hashavua.model;

import java.util.List;

import rx.Observable;

public interface HashavuaApi {

    Observable<List<HashavuaEntry>> getEntries();
}
