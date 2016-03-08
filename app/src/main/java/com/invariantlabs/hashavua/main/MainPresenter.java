package com.invariantlabs.hashavua.main;

import android.text.TextUtils;

import com.invariantlabs.hashavua.base.BaseMvpLceeRxPresenter;
import com.invariantlabs.hashavua.model.HashavuaApi;
import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.model.HashavuaEntryMetaData;
import com.invariantlabs.hashavua.model.HashavuaMainSubject;
import com.invariantlabs.hashavua.model.MainModel;
import com.invariantlabs.hashavua.model.db.DatabaseHelper;
import com.invariantlabs.hashavua.model.HashavuaSubject;
import com.invariantlabs.hashavua.view.SubjectsColorHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.inject.Inject;
import javax.inject.Named;

import rx.Observable;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;

public class MainPresenter extends BaseMvpLceeRxPresenter<MainView, MainModel> {

    private final HashavuaApi hashavuaApi;
    private final DatabaseHelper dbHelper;
    private final EntriesFilter filter = new EntriesFilter();
    private final SubjectsColorHelper subjectsColorHelper;
    private final SubjectsColorHelper mainSubjectsColorHelper;
    private Subscription dbSubscription;

    @Inject
    public MainPresenter(HashavuaApi hashavuaApi, DatabaseHelper dbHelper, @Named("subjects")SubjectsColorHelper subjectsColorHelper, @Named("main_subjects")SubjectsColorHelper mainSubjectsColorHelper) {
        this.hashavuaApi = hashavuaApi;
        this.dbHelper = dbHelper;
        this.subjectsColorHelper = subjectsColorHelper;
        this.mainSubjectsColorHelper = mainSubjectsColorHelper;
        dbSubscription = dbHelper.getEntriesMetaDataMap().observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<List<HashavuaEntryMetaData>>() {
                    @Override
                    public void call(List<HashavuaEntryMetaData> metadata) {
                        if (data != null) {
                            applyMetaData(data.getEntries(), metadata);
                            updateViewData();
                        }
                    }
                });
    }

    public void loadEntries(boolean pullToRefresh) {
        if (data != null && !pullToRefresh) {
            if (isViewAttached()) {
                updateViewData();
            }
        } else {
            Observable<MainModel> observable = hashavuaApi.getEntries()
                    .zipWith(dbHelper.getEntriesMetaDataMap(), new Func2<List<HashavuaEntry>, List<HashavuaEntryMetaData>, List<HashavuaEntry>>() {
                        @Override
                        public List<HashavuaEntry> call(List<HashavuaEntry> hashavuaEntries, List<HashavuaEntryMetaData> metadata) {
                            applyMetaData(hashavuaEntries, metadata);
                            return hashavuaEntries;
                        }
                    })
                    .map(new Func1<List<HashavuaEntry>, MainModel>() {
                        @Override
                        public MainModel call(List<HashavuaEntry> hashavuaEntries) {
                            boolean hasEmptySubject = false;
                            Set<HashavuaSubject> subjects = new LinkedHashSet<>();
                            Set<HashavuaMainSubject> mainSubjects = new LinkedHashSet<>();
                            for (HashavuaEntry hashavuaEntry : hashavuaEntries) {
                                String subject = hashavuaEntry.getSubject();
                                if (TextUtils.isEmpty(subject)) {
                                    hasEmptySubject = true;
                                } else {
                                    subjects.add(new HashavuaSubject(subject, true));
                                }
                                mainSubjects.add(new HashavuaMainSubject(hashavuaEntry.getMainSunject(), true));
                            }
                            if (hasEmptySubject) {
                                subjects.add(new HashavuaSubject("", true));
                            }
                            filter.addSubjects(subjects);
                            filter.addMainSubjects(mainSubjects);
                            for (HashavuaSubject subject : subjects) {
                                // to keep subjects colors the same each time
                                subjectsColorHelper.getColorForSubject(subject.getValue());
                            }
                            for (HashavuaMainSubject mainSubject : mainSubjects) {
                                // to keep subjects colors the same each time
                                mainSubjectsColorHelper.getColorForSubject(mainSubject.getValue());
                            }
                            return new MainModel(hashavuaEntries, new ArrayList<>(subjects), new ArrayList<>(mainSubjects), 0, 0);
                        }
                    });
            subscribe(observable, pullToRefresh);
        }
    }

    private void applyMetaData(List<HashavuaEntry> entries, List<HashavuaEntryMetaData> metadata) {
        Map<String, HashavuaEntryMetaData> map = new HashMap<>();
        for (HashavuaEntryMetaData metaData : metadata) {
            map.put(metaData.getUrl(), metaData);
        }
        for (HashavuaEntry entry : entries) {
            if (map.containsKey(entry.getUrl())) {
                HashavuaEntryMetaData metaData = map.get(entry.getUrl());
                entry.setWatched(metaData.isWatched());
                entry.setFavorite(metaData.isFavorite());
            }
        }
    }

    public void onEntryClicked(HashavuaEntry entry) {
        if (isViewAttached()) {
            entry.setWatched(true);
            dbHelper.insertOrUpdateEntry(entry);
            getView().openInBrowser(entry.getUrl());
        }
    }

    public void onFavoriteClicked(HashavuaEntry entry) {
        if (isViewAttached()) {
            dbHelper.insertOrUpdateEntry(entry);
        }
    }

    public void onErrorOpeningInBrowser() {
        if (isViewAttached()) {
            getView().showErrorNoBrowser();
        }
    }

    public void onFilterClicked() {
        if (isViewAttached()) {
            getView().showFilterView();
        }
    }


    public void onShowOnlyFavoritesChanged(boolean showOnlyFavorites) {
        filter.setShowOnlyFavorites(showOnlyFavorites);
        updateViewData();
    }

    @Override
    protected boolean isEmpty(MainModel data) {
        return data == null || data.getEntries().isEmpty();
    }

    @Override
    protected MainModel filterData(MainModel data) {
        List<HashavuaEntry> filtered = new ArrayList<>();
        if (data == null) return new MainModel(filtered, new ArrayList<HashavuaSubject>(), new ArrayList<HashavuaMainSubject>(), 0, 0);
        for (HashavuaEntry entry : data.getEntries()) {
            if (filter.filter(entry)) {
                filtered.add(entry);
            }
        }
        return new MainModel(filtered, data.getSubjects(), data.getMainSubjects(), data.getEntries().size(), filtered.size());
    }

    public void onResetFiltersClicked() {
        if (isViewAttached()) {
            getView().setShowOnlyFavorites(false);
            getView().setShowOnlyNew(false);
            getView().checkAllSubjects();
            getView().checkAllMainSubjects();
            getView().setSearchQuery("");
        }
    }

    public void onShowOnlyNewChanged(boolean showOnlyNew) {
        filter.setShowOnlyNew(showOnlyNew);
        updateViewData();
    }

    public void onSubjectEnabledChanged(HashavuaSubject subject) {
        filter.updateSubject(subject);
        updateViewData();
    }

    public void onMainSubjectEnabledChanged(HashavuaMainSubject mainSubject) {
        filter.updateMainSubject(mainSubject);
        updateViewData();
    }

    public void onSearchChanged(String query) {
        filter.setSearchQuery(query);
        updateViewData();
    }

    public void onOpenSourceLicensesClicked() {
        if (isViewAttached()) {
            getView().showOpenSourceLicenses();
        }
    }

    @Override
    public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            if (dbSubscription != null && !dbSubscription.isUnsubscribed()) {
                dbSubscription.unsubscribe();
            }

            dbSubscription = null;
        }
    }
}
