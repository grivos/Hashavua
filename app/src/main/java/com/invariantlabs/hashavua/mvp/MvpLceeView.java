package com.invariantlabs.hashavua.mvp;

import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;

public interface MvpLceeView<M> extends MvpLceView<M> {

    /**
     * Show the empty view.
     *
     * <b>The content view must have the id = R.id.emptyView</b>
     */
    public void showEmpty();
}
