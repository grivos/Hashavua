package com.invariantlabs.hashavua.main;

import com.invariantlabs.hashavua.model.MainModel;
import com.invariantlabs.hashavua.mvp.MvpLceeView;

public interface MainView extends MvpLceeView<MainModel> {

    void openInBrowser(String url);

    void showErrorNoBrowser();

    void showFilterView();

    void setShowOnlyFavorites(boolean showOnlyFavorites);

    void setShowOnlyNew(boolean b);

    void checkAllSubjects();

    void checkAllMainSubjects();

    void setSearchQuery(String query);

    void showOpenSourceLicenses();

}
