package com.invariantlabs.hashavua.base;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.hannesdorfmann.mosby.mvp.MvpPresenter;
import com.hannesdorfmann.mosby.mvp.lce.MvpLceView;
import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.mvp.MvpLceeView;
import com.invariantlabs.hashavua.util.LceeAnimator;

import butterknife.Bind;

public abstract class BaseMvpLceeFragment<CV extends View, M, V extends MvpLceView<M>, P extends MvpPresenter<V>>
        extends BaseMvpFragment<V, P> implements MvpLceeView<M> {

    @Bind(R.id.emptyView) protected View emptyView;
    @Bind(R.id.loadingView) protected View loadingView;
    @Bind(R.id.contentView) protected CV contentView;
    @Bind(R.id.errorView) protected TextView errorView;

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        errorView.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                onErrorViewClicked();
            }
        });
    }

    @Override public void showLoading(boolean pullToRefresh) {

        if (!pullToRefresh) {
            animateLoadingViewIn();
        }

        // otherwise the pull to refresh widget will already display a loading animation
    }

    /**
     * Override this method if you want to provide your own animation for showing the loading view
     */
    protected void animateLoadingViewIn() {
        LceeAnimator.showLoading(loadingView, contentView, errorView, emptyView);
    }

    @Override public void showContent() {
        animateContentViewIn();
    }

    /**
     * Called to animate from loading view to content view
     */
    protected void animateContentViewIn() {
        LceeAnimator.showContent(loadingView, contentView, errorView, emptyView);
    }

    @Override
    public void showEmpty() {
        animateEmptyViewIn();
    }

    /**
     * Called to animate from loading view to empty view
     */
    protected void animateEmptyViewIn() {
        LceeAnimator.showContent(loadingView, contentView, errorView, emptyView);
    }

    /**
     * Get the error message for a certain Exception that will be shown on {@link
     * #showError(Throwable, boolean)}
     */
    protected abstract String getErrorMessage(Throwable e, boolean pullToRefresh);

    /**
     * The default behaviour is to display a toast message as light error (i.e. pull-to-refresh
     * error).
     * Override this method if you want to display the light error in another way (like crouton).
     */
    protected void showLightError(String msg) {
        if (getActivity() != null) {
            Toast.makeText(getActivity(), msg, Toast.LENGTH_SHORT).show();
        }
    }

    /**
     * Called if the error view has been clicked. To disable clicking on the errorView use
     * <code>errorView.setClickable(false)</code>
     */
    protected void onErrorViewClicked() {
        loadData(false);
    }

    @Override public void showError(Throwable e, boolean pullToRefresh) {

        String errorMsg = getErrorMessage(e, pullToRefresh);

        if (pullToRefresh) {
            showLightError(errorMsg);
        } else {
            errorView.setText(errorMsg);
            animateErrorViewIn();
        }
    }

    /**
     * Animates the error view in (instead of displaying content view / loading view)
     */
    protected void animateErrorViewIn() {
        LceeAnimator.showErrorView(loadingView, contentView, errorView, emptyView);
    }

    @Override public void onDestroyView() {
        super.onDestroyView();
        loadingView = null;
        contentView = null;
        errorView = null;
    }

}
