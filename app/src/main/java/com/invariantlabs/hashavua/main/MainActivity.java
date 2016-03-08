package com.invariantlabs.hashavua.main;

import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.base.BaseMvpLceeActivity;
import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.model.HashavuaMainSubject;
import com.invariantlabs.hashavua.model.MainModel;
import com.invariantlabs.hashavua.model.HashavuaSubject;
import com.invariantlabs.hashavua.util.ColorsUtil;
import com.invariantlabs.hashavua.util.Intents;
import com.invariantlabs.hashavua.util.Util;
import com.invariantlabs.hashavua.view.ErrorMessageDeterminer;

import javax.inject.Inject;

import butterknife.Bind;
import de.psdev.licensesdialog.LicensesDialog;
import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.schedulers.Schedulers;

public class MainActivity extends BaseMvpLceeActivity<DrawerLayout, MainModel, MainView, MainPresenter>
        implements MainView, SwipeRefreshLayout.OnRefreshListener,
        HashavuaFeedAdapter.HashavuaFeedCallback, DrawerLayout.DrawerListener, FilterDrawerFragment.FilterFragmentCallback {

    @Inject
    ErrorMessageDeterminer errorMessageDeterminer;

    @Bind(R.id.swipeRefresh)
    SwipeRefreshLayout swipeRefreshLayout;
    @Bind(R.id.toolbar)
    Toolbar toolbar;
    @Bind(R.id.mainDrawer)
    DrawerLayout drawerLayout;

    MainFragment mainFragment;
    FilterDrawerFragment filterFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        component.inject(this);
        setRetainInstance(true);
        setContentView(R.layout.main_activity);
        mainFragment = (MainFragment) getSupportFragmentManager().findFragmentById(R.id.mainFragment);
        filterFragment = (FilterDrawerFragment) getSupportFragmentManager().findFragmentById(R.id.drawerFragment);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        drawerLayout.addDrawerListener(this);
        drawerLayout.setDrawerShadow(R.drawable.drawer_shadow, GravityCompat.END);
        setupSwipeRefreshLayout();
        loadData(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            setStatusBarColor(1f);
        } else {
            setStatusBarColor(0f);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        super.onCreateOptionsMenu(menu);
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.filter: {
                presenter.onFilterClicked();
                return true;
            }
            case R.id.open_source_licenses: {
                presenter.onOpenSourceLicensesClicked();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }

    }

    private void setupSwipeRefreshLayout() {
        swipeRefreshLayout.setOnRefreshListener(this);
        swipeRefreshLayout.setColorSchemeResources(R.color.primary);
        TypedValue tv = new TypedValue();
        getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true);
        int actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());
        int delta = Util.dpToPx(this, 64);
        swipeRefreshLayout.setProgressViewOffset(false, actionBarHeight, actionBarHeight + delta);
    }

    @Override
    protected String getErrorMessage(Throwable e, boolean pullToRefresh) {
        return errorMessageDeterminer.getErrorMessage(e, pullToRefresh, this);
    }

    @NonNull
    @Override
    public MainPresenter createPresenter() {
        return component.mainPresenter();
    }

    @Override
    public void setData(MainModel data) {
        mainFragment.setData(data.getEntries());
        filterFragment.setItemsCount(data.getTotalItems(), data.getFilteredItems());
        filterFragment.setSubjects(data.getSubjects());
        filterFragment.setMainSubjects(data.getMainSubjects());
    }

    @Override
    public void loadData(boolean pullToRefresh) {
        presenter.loadEntries(pullToRefresh);
    }

    @Override
    public void openInBrowser(String url) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(url));
        final boolean success = Intents.maybeStartActivity(this, intent);
        if (!success) {
            getPresenter().onErrorOpeningInBrowser();
        }
    }

    @Override
    public void showErrorNoBrowser() {
        Snackbar.make(contentView, R.string.error_no_browser, Snackbar.LENGTH_LONG)
                .show();
    }

    @Override
    public void showFilterView() {
        drawerLayout.openDrawer(GravityCompat.END);
    }

    @Override
    public void showOpenSourceLicenses() {
        Observable.create(new Observable.OnSubscribe<LicensesDialog>() {
            @Override
            public void call(Subscriber<? super LicensesDialog> subscriber) {
                LicensesDialog dialog = new LicensesDialog.Builder(MainActivity.this)
                        .setNotices(R.raw.notices)
                        .build();
                subscriber.onNext(dialog);
            }
        }).subscribeOn(Schedulers.computation())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<LicensesDialog>() {
                    @Override
                    public void call(LicensesDialog licensesDialog) {
                        licensesDialog.show();
                    }
                });

    }

    @Override
    public void onRefresh() {
        loadData(true);
    }

    @Override public void showError(Throwable e, boolean pullToRefresh) {
        super.showError(e, pullToRefresh);
        swipeRefreshLayout.setRefreshing(false);
        e.printStackTrace();
    }

    @Override public void showContent() {
        super.showContent();
        swipeRefreshLayout.setRefreshing(false);
    }

    @Override public void showLoading(boolean pullToRefresh) {
        super.showLoading(pullToRefresh);
        if (pullToRefresh && !swipeRefreshLayout.isRefreshing()) {
            // Workaround for measure bug: https://code.google.com/p/android/issues/detail?id=77712
            swipeRefreshLayout.post(new Runnable() {
                @Override public void run() {
                    swipeRefreshLayout.setRefreshing(true);
                }
            });
        }
    }

    @Override
    public void onEntryClicked(HashavuaEntry entry) {
        presenter.onEntryClicked(entry);
    }

    @Override
    public void onFavoriteClicked(HashavuaEntry entry) {
        presenter.onFavoriteClicked(entry);
    }

    @Override
    public void onDrawerSlide(View drawerView, float slideOffset) {
        setStatusBarColor(slideOffset);
    }

    @Override
    public void onDrawerOpened(View drawerView) {

    }

    @Override
    public void onDrawerClosed(View drawerView) {
        filterFragment.removeSearchFocus();
    }

    @Override
    public void onDrawerStateChanged(int newState) {

    }

    private void setStatusBarColor(float drawerSlideOffset) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            int primaryDarkColor = getResources().getColor(R.color.primary_dark);
            int accentDarkColor = getResources().getColor(R.color.accent_dark);
            int mergedColor = ColorsUtil.mixColors(accentDarkColor, primaryDarkColor, drawerSlideOffset);
            getWindow().setStatusBarColor(mergedColor);
        }
    }

    @Override
    public void onShowOnlyFavoritesChanged(boolean showOnlyFavorites) {
        presenter.onShowOnlyFavoritesChanged(showOnlyFavorites);
    }

    @Override
    public void onShowOnlyNewChanged(boolean showOnlyFavorites) {
        presenter.onShowOnlyNewChanged(showOnlyFavorites);
    }

    @Override
    public void onSubjectEnabledChanged(HashavuaSubject subject) {
        presenter.onSubjectEnabledChanged(subject);
    }

    @Override
    public void onMainSubjectEnabledChanged(HashavuaMainSubject mainSubject) {
        presenter.onMainSubjectEnabledChanged(mainSubject);
    }

    @Override
    public void onSearchChanged(String query) {
        presenter.onSearchChanged(query);
    }

    @Override
    public void onResetFiltersClicked() {
        presenter.onResetFiltersClicked();
    }

    @Override
    public void setShowOnlyFavorites(boolean showOnlyFavorites) {
        filterFragment.setShowOnlyFavorites(showOnlyFavorites);
    }

    @Override
    public void setShowOnlyNew(boolean showOnlyNew) {
        filterFragment.setShowOnlyNew(showOnlyNew);
    }

    @Override
    public void checkAllSubjects() {
        filterFragment.checkAllSubjects();
    }

    @Override
    public void checkAllMainSubjects() {
        filterFragment.checkAllMainSubjects();
    }

    @Override
    public void setSearchQuery(String query) {
        filterFragment.setSearchQuery(query);
    }

    @Override
    public void onBackPressed() {
        if (drawerLayout.isDrawerOpen(GravityCompat.END)) {
            drawerLayout.closeDrawer(GravityCompat.END);
        } else {
            super.onBackPressed();
        }
    }
}
