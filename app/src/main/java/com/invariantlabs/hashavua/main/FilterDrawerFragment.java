package com.invariantlabs.hashavua.main;

import android.content.Context;
import android.os.Bundle;
import android.support.v7.widget.SwitchCompat;
import android.support.v7.widget.Toolbar;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;

import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.base.BaseFragment;
import com.invariantlabs.hashavua.model.Filterable;
import com.invariantlabs.hashavua.model.HashavuaMainSubject;
import com.invariantlabs.hashavua.model.HashavuaSubject;
import com.invariantlabs.hashavua.util.Util;
import com.invariantlabs.hashavua.view.FilterCountView;
import com.invariantlabs.hashavua.view.FilterablesView;
import com.invariantlabs.hashavua.view.SubjectsColorHelper;
import com.jakewharton.rxbinding.widget.RxTextView;
import com.jakewharton.rxbinding.widget.TextViewAfterTextChangeEvent;

import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.OnClick;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;

public class FilterDrawerFragment extends BaseFragment implements Toolbar.OnMenuItemClickListener, CompoundButton.OnCheckedChangeListener, FilterablesView.FilterableViewCallback {

    @Bind(R.id.filterToolbar)
    Toolbar filterToolbar;
    @Bind(R.id.onlyFavorites)
    SwitchCompat onlyFavorites;
    @Bind(R.id.onlyNew)
    SwitchCompat onlyNew;
    @Bind(R.id.toolbar_title)
    TextView toolbarTitle;
    @Bind(R.id.subjects)
    FilterablesView subjectsView;
    @Bind(R.id.mainSubjects)
    FilterablesView mainSubjectsView;
    @Bind(R.id.input_search)
    EditText search;
    @Bind(R.id.filterCount)
    FilterCountView filterCount;

    @Inject
    @Named("subjects")
    SubjectsColorHelper subjectsColorHelper;
    @Inject
    @Named("main_subjects")
    SubjectsColorHelper mainSubjectsColorHelper;

    private FilterFragmentCallback callback;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return getActivity().getLayoutInflater().inflate(R.layout.filter_drawer_fragment, container, false);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        filterToolbar.inflateMenu(R.menu.filter_menu);
        filterToolbar.setOnMenuItemClickListener(this);
        onlyFavorites.setOnCheckedChangeListener(this);
        onlyNew.setOnCheckedChangeListener(this);
        subjectsView.setFilterablesViewCallback(this);
        mainSubjectsView.setFilterablesViewCallback(this);
        RxTextView.afterTextChangeEvents(search)
                .subscribeOn(AndroidSchedulers.mainThread())
                .skip(1)
                .subscribe(new Action1<TextViewAfterTextChangeEvent>() {
                    @Override
                    public void call(TextViewAfterTextChangeEvent textViewAfterTextChangeEvent) {
                        callback.onSearchChanged(textViewAfterTextChangeEvent.editable().toString().trim());
                    }
                });
        search.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {

                if (actionId == EditorInfo.IME_ACTION_DONE) {
                    Util.hideKeyboard(getContext(), v);
                    v.clearFocus();
                    return true;
                } else {
                    return false;
                }
            }
        });
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (!(context instanceof FilterFragmentCallback)) {
            throw new RuntimeException("Activity must implement FilterFragmentCallback");
        }
        callback = ((FilterFragmentCallback) context);
    }

    @Override
    public boolean onMenuItemClick(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.reset: {
                callback.onResetFiltersClicked();
                return true;
            }
            default: return super.onOptionsItemSelected(item);
        }
    }

    public void setShowOnlyFavorites(boolean showOnlyFavorites) {
        onlyFavorites.setChecked(showOnlyFavorites);
    }

    public void setShowOnlyNew(boolean showOnlyNew) {
        onlyNew.setChecked(showOnlyNew);
    }

    public void checkAllSubjects() {
        subjectsView.checkAllFilterables();
    }

    public void checkAllMainSubjects() {
        mainSubjectsView.checkAllFilterables();
    }

    public void setSearchQuery(String query) {
        search.setText(query);
        removeSearchFocus();
    }

    public void setItemsCount(int total, int filtered) {
        filterCount.setCount(total, filtered);
    }

    public void setSubjects(List<HashavuaSubject> subjects) {
        subjectsView.setFilterables(subjects);
    }

    public void setMainSubjects(List<HashavuaMainSubject> mainSubjects) {
        mainSubjectsView.setFilterables(mainSubjects);
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        switch (compoundButton.getId()) {
            case R.id.onlyFavorites:
                callback.onShowOnlyFavoritesChanged(b);
                removeSearchFocus();
                break;
            case R.id.onlyNew:
                callback.onShowOnlyNewChanged(b);
                removeSearchFocus();
                break;
        }
    }

    @Override
    public void onFilterableEnabledChanged(FilterablesView view, Filterable filterable) {
        if (view == subjectsView) {
            callback.onSubjectEnabledChanged((HashavuaSubject) filterable);
            removeSearchFocus();
        } else {
            callback.onMainSubjectEnabledChanged((HashavuaMainSubject) filterable);
            removeSearchFocus();
        }
    }

    public void removeSearchFocus() {
        Util.hideKeyboard(getContext(), search);
        search.clearFocus();
    }

    @OnClick(R.id.onlyNewLabel)
    public void onClickedOnlyNewLabel() {
        onlyNew.toggle();
    }

    @OnClick(R.id.onlyFavoritesLabel)
    public void onClickedOnlyFavoritesLabel() {
        onlyFavorites.toggle();
    }

    public interface FilterFragmentCallback {
        void onResetFiltersClicked();
        void onShowOnlyFavoritesChanged(boolean showOnlyFavorites);
        void onShowOnlyNewChanged(boolean showOnlyFavorites);
        void onSubjectEnabledChanged(HashavuaSubject subject);
        void onMainSubjectEnabledChanged(HashavuaMainSubject mainSubject);
        void onSearchChanged(String query);
    }

}
