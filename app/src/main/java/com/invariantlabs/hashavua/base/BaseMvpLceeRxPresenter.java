package com.invariantlabs.hashavua.base;

import com.invariantlabs.hashavua.model.MainModel;
import com.invariantlabs.hashavua.mvp.MvpLceeView;

import java.util.Collection;

import rx.Observable;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public abstract class BaseMvpLceeRxPresenter<V extends MvpLceeView<M>, M>
        extends com.hannesdorfmann.mosby.mvp.MvpBasePresenter<V>
        implements com.hannesdorfmann.mosby.mvp.MvpPresenter<V> {

    protected Subscriber<M> subscriber;
    protected M data;
    protected Throwable error;

    /**
     * Unsubscribes the subscriber and set it to null
     */
    protected void unsubscribe() {
        if (subscriber != null && !subscriber.isUnsubscribed()) {
            subscriber.unsubscribe();
        }

        subscriber = null;
    }

    /**
     * Subscribes the presenter himself as subscriber on the observable
     *
     * @param observable The observable to subscribe
     * @param pullToRefresh Pull to refresh?
     */
    public void subscribe(Observable<M> observable, final boolean pullToRefresh) {

        if (isViewAttached()) {
            getView().showLoading(pullToRefresh);
        }

        unsubscribe();

        subscriber = new Subscriber<M>() {
            private boolean ptr = pullToRefresh;

            @Override public void onCompleted() {
                BaseMvpLceeRxPresenter.this.onCompleted();
            }

            @Override public void onError(Throwable e) {
                BaseMvpLceeRxPresenter.this.error = e;
                BaseMvpLceeRxPresenter.this.onError(e, ptr);
            }

            @Override public void onNext(M m) {
                BaseMvpLceeRxPresenter.this.error = null;
                BaseMvpLceeRxPresenter.this.onNext(m);
            }
        };

        observable.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(subscriber);
    }

    protected void onCompleted() {
        if (isViewAttached()) {
            if (isEmpty(filterData(data))) {
                getView().showEmpty();
            } else {
                getView().showContent();
            }
        }
        unsubscribe();
    }

    protected void onError(Throwable e, boolean pullToRefresh) {
        if (isViewAttached()) {
            data = null;
            getView().showError(e, pullToRefresh);
        }
        unsubscribe();
    }

    protected void onNext(M data) {
        if (isViewAttached()) {
            this.data = data;
            updateViewData();
        }
    }

    protected void updateViewData() {
        if (isViewAttached()) {
            M filteredData = filterData(this.data);
            getView().setData(filteredData);
            if (error != null) {
                getView().showError(error, false);
            }
            if (isEmpty(filteredData)) {
                getView().showEmpty();
            } else {
                getView().showContent();
            }
        }
    }

    @Override public void detachView(boolean retainInstance) {
        super.detachView(retainInstance);
        if (!retainInstance) {
            unsubscribe();
        }
    }

    protected abstract M filterData(M data);

    protected abstract boolean isEmpty(M data);

}