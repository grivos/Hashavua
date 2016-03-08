package com.invariantlabs.hashavua.model;

import android.net.Uri;
import android.text.TextUtils;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import rx.Observable;
import rx.Scheduler;
import rx.functions.Func1;
import rx.functions.FuncN;
import rx.schedulers.Schedulers;

public class HashavuaApiImpl implements HashavuaApi {

    final HashavuaSpreadSheetApi innerApi;

    @Inject
    public HashavuaApiImpl(HashavuaSpreadSheetApi innerApi) {
        this.innerApi = innerApi;
    }

    @Override
    public Observable<List<HashavuaEntry>> getEntries() {
        List<Observable<List<HashavuaEntry>>> feedsList = new ArrayList<>();

        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed01(), "מוצר"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed02(), "שיווק"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed03(), "גיוס כספים"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed04(), "כוח אדם"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed05(), "טכנולוגי"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed06(), "מכירות"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed07(), "משפטי"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed08(), "עיצוב וחוויית משתמש"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed09(), "מודל עיסקי"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed10(), "שירות לקוחות"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed11(), "שאלו אותי הכל"));
        feedsList.add(getEntriesFromFeed(innerApi.getHashavouaFeed12(), "מומלץ לקריאה"));
        return Observable.zip(feedsList, new FuncN<List<HashavuaEntry>>() {
            @Override
            public List<HashavuaEntry> call(Object... args) {
                List<HashavuaEntry> result = new ArrayList<>();
                for (Object arg : args) {
                    List<HashavuaEntry> feed = (List<HashavuaEntry>) arg;
                    result.addAll(feed);
                }
                return result;
            }
        });
    }

    private Observable<List<HashavuaEntry>> getEntriesFromFeed(Observable<HashavuaFeedWrapper> feedWrapper, final String mainSunject) {
        return feedWrapper.subscribeOn(Schedulers.io())
                .flatMap(new Func1<HashavuaFeedWrapper, Observable<HashavuaEntryJson>>() {
                    @Override
                    public Observable<HashavuaEntryJson> call(HashavuaFeedWrapper hashavuaFeedWrapper) {
                        return Observable.from(hashavuaFeedWrapper.getFeed().getEntries());
                    }
                }).filter(new Func1<HashavuaEntryJson, Boolean>() {
            @Override
            public Boolean call(HashavuaEntryJson hashavuaEntryJson) {
                return isFieldSet(hashavuaEntryJson.getTitle())
                        && isFieldSet(hashavuaEntryJson.getUrl());
            }
        }).map(new Func1<HashavuaEntryJson, HashavuaEntry>() {
            @Override
            public HashavuaEntry call(HashavuaEntryJson json) {
                String subject = isFieldSet(json.getSubject())? json.getSubject().getValue() : "";
                String url = fixUrl(json.getUrl().getValue());
                return new HashavuaEntry(json.getTitle().getValue(), url, subject, mainSunject);
            }
        }).toList();
    }

    private String fixUrl(String url) {
        // The Facebook app seems to give error for urls such as:
        // https://www.facebook.com/434228236734415/posts/POST_ID/
        // so we'll change it to
        // https://www.facebook.com/groups/Hashavua/permalink/POST_ID/
        if (url.contains("groups/Hashavua/permalink")) {
            return url;
        } else {
            String postId = Uri.parse(url).getLastPathSegment();
            return String.format("https://www.facebook.com/groups/Hashavua/permalink/%s/", postId);
        }
    }

    private boolean isFieldSet(JsonValue value) {
        return value != null && !TextUtils.isEmpty(value.getValue());
    }
}
