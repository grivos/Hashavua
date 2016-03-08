package com.invariantlabs.hashavua.model;

import retrofit2.http.GET;
import rx.Observable;

public interface HashavuaSpreadSheetApi {

    String feed01 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/od6/public/values?alt=json";
    String feed02 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/o3782xe/public/full?alt=json";
    String feed03 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/opaec2h/public/full?alt=json";
    String feed04 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/oi1blf0/public/full?alt=json";
    String feed05 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/oq6irj6/public/full?alt=json";
    String feed06 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/ovc6uya/public/full?alt=json";
    String feed07 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/oqe0xvd/public/full?alt=json";
    String feed08 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/oiz5los/public/full?alt=json";
    String feed09 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/ooqsrt3/public/full?alt=json";
    String feed10 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/o9fzhwm/public/full?alt=json";
    String feed11 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/o7s5hk/public/full?alt=json";
    String feed12 = "/feeds/list/1CIc8owLjVQEpxoSqR4IkGmxv0Y9SemVvaO2A2o6pqqI/o1c3ryl/public/full?alt=json";

    @GET(feed01)
    Observable<HashavuaFeedWrapper> getHashavouaFeed01();
    @GET(feed02)
    Observable<HashavuaFeedWrapper> getHashavouaFeed02();
    @GET(feed03)
    Observable<HashavuaFeedWrapper> getHashavouaFeed03();
    @GET(feed04)
    Observable<HashavuaFeedWrapper> getHashavouaFeed04();
    @GET(feed05)
    Observable<HashavuaFeedWrapper> getHashavouaFeed05();
    @GET(feed06)
    Observable<HashavuaFeedWrapper> getHashavouaFeed06();
    @GET(feed07)
    Observable<HashavuaFeedWrapper> getHashavouaFeed07();
    @GET(feed08)
    Observable<HashavuaFeedWrapper> getHashavouaFeed08();
    @GET(feed09)
    Observable<HashavuaFeedWrapper> getHashavouaFeed09();
    @GET(feed10)
    Observable<HashavuaFeedWrapper> getHashavouaFeed10();
    @GET(feed11)
    Observable<HashavuaFeedWrapper> getHashavouaFeed11();
    @GET(feed12)
    Observable<HashavuaFeedWrapper> getHashavouaFeed12();

}
