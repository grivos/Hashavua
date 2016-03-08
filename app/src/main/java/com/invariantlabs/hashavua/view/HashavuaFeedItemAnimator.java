package com.invariantlabs.hashavua.view;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.RecyclerView;
import android.view.animation.AnimationUtils;
import android.view.animation.Interpolator;
import android.view.animation.OvershootInterpolator;

import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.main.HashavuaFeedAdapter;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class HashavuaFeedItemAnimator extends DefaultItemAnimator {
    private final Interpolator favoriteInterpolator;
    private final Interpolator watchedInterpolator;

    Map<RecyclerView.ViewHolder, AnimatorSet> watchedAnimationsMap = new HashMap<>();
    Map<RecyclerView.ViewHolder, AnimatorSet> favoriteAnimationsMap = new HashMap<>();

    public HashavuaFeedItemAnimator(Context context) {
        favoriteInterpolator = new OvershootInterpolator(4);
        watchedInterpolator = AnimationUtils.loadInterpolator(context, android.R.interpolator.fast_out_slow_in);
    }


    @Override
    public boolean canReuseUpdatedViewHolder(RecyclerView.ViewHolder viewHolder) {
        return true;
    }

    @NonNull
    @Override
    public ItemHolderInfo recordPreLayoutInformation(@NonNull RecyclerView.State state,
                                                     @NonNull RecyclerView.ViewHolder viewHolder,
                                                     int changeFlags, @NonNull List<Object> payloads) {
        if (changeFlags == FLAG_CHANGED) {
            for (Object payload : payloads) {
                if (payload instanceof String) {
                    return new HashavuaFeedItemHolderInfo((String) payload);
                }
            }
        }

        return super.recordPreLayoutInformation(state, viewHolder, changeFlags, payloads);
    }

    @Override
    public boolean animateChange(@NonNull RecyclerView.ViewHolder oldHolder,
                                 @NonNull RecyclerView.ViewHolder newHolder,
                                 @NonNull ItemHolderInfo preInfo,
                                 @NonNull ItemHolderInfo postInfo) {
        cancelCurrentAnimationIfExists(newHolder);

        if (preInfo instanceof HashavuaFeedItemHolderInfo) {
            HashavuaFeedItemHolderInfo feedItemHolderInfo = (HashavuaFeedItemHolderInfo) preInfo;
            HashavuaFeedAdapter.ViewHolder holder = (HashavuaFeedAdapter.ViewHolder) newHolder;


            if (HashavuaFeedAdapter.ACTION_FAVORITE.equals(feedItemHolderInfo.updateAction)) {
                animateFavoriteButton(holder, R.drawable.ic_favorite_24dp);
            } else if (HashavuaFeedAdapter.ACTION_UNFAVORITE.equals(feedItemHolderInfo.updateAction)) {
                animateFavoriteButton(holder, R.drawable.ic_favorite_outline_24dp);
            } else if (HashavuaFeedAdapter.ACTION_WATCHED.equals(feedItemHolderInfo.updateAction)) {
                animateWatched(holder);
            }
        }

        return false;
    }

    private void cancelCurrentAnimationIfExists(RecyclerView.ViewHolder item) {
        if (watchedAnimationsMap.containsKey(item)) {
            watchedAnimationsMap.get(item).cancel();
        }
        if (favoriteAnimationsMap.containsKey(item)) {
            favoriteAnimationsMap.get(item).cancel();
        }
    }

    private void animateFavoriteButton(final HashavuaFeedAdapter.ViewHolder holder, final int targetResId) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator bounceAnimX = ObjectAnimator.ofFloat(holder.favorite, "scaleX", 0.2f, 1f);
        bounceAnimX.setDuration(300);
        bounceAnimX.setInterpolator(favoriteInterpolator);

        ObjectAnimator bounceAnimY = ObjectAnimator.ofFloat(holder.favorite, "scaleY", 0.2f, 1f);
        bounceAnimY.setDuration(300);
        bounceAnimY.setInterpolator(favoriteInterpolator);
        bounceAnimY.addListener(new AnimatorListenerAdapter() {
            @Override
            public void onAnimationStart(Animator animation) {
                holder.favorite.setImageResource(targetResId);
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                favoriteAnimationsMap.remove(holder);
                dispatchChangeFinishedIfAllAnimationsEnded(holder);
            }
        });

        animatorSet.play(bounceAnimX).with(bounceAnimY);
        animatorSet.start();

        favoriteAnimationsMap.put(holder, animatorSet);
    }

    private void animateWatched(final HashavuaFeedAdapter.ViewHolder holder) {
        AnimatorSet animatorSet = new AnimatorSet();

        ObjectAnimator titleAlpha = ObjectAnimator.ofFloat(holder.title, "alpha", 1.0f, HashavuaFeedAdapter.WATCHED_ALPHA);
        titleAlpha.setDuration(300);
        titleAlpha.setInterpolator(watchedInterpolator);

        ObjectAnimator subjectAlpha = ObjectAnimator.ofFloat(holder.subject, "alpha", 1.0f, HashavuaFeedAdapter.WATCHED_ALPHA);
        subjectAlpha.setDuration(300);
        subjectAlpha.setInterpolator(watchedInterpolator);

        ObjectAnimator mainSubjectAlpha = ObjectAnimator.ofFloat(holder.mainSubject, "alpha", 1.0f, HashavuaFeedAdapter.WATCHED_ALPHA);
        subjectAlpha.setDuration(300);
        subjectAlpha.setInterpolator(watchedInterpolator);

        animatorSet.addListener(new AnimatorListenerAdapter() {

            @Override
            public void onAnimationEnd(Animator animation) {
                dispatchChangeFinishedIfAllAnimationsEnded(holder);
            }
        });

        animatorSet.play(titleAlpha).with(subjectAlpha).with(mainSubjectAlpha);
        animatorSet.start();

        watchedAnimationsMap.put(holder, animatorSet);
    }

    private void dispatchChangeFinishedIfAllAnimationsEnded(HashavuaFeedAdapter.ViewHolder holder) {
        if (watchedAnimationsMap.containsKey(holder) || favoriteAnimationsMap.containsKey(holder)) {
            return;
        }

        dispatchAnimationFinished(holder);
    }

    @Override
    public void endAnimation(RecyclerView.ViewHolder item) {
        super.endAnimation(item);
        cancelCurrentAnimationIfExists(item);
    }

    @Override
    public void endAnimations() {
        super.endAnimations();
        for (AnimatorSet animatorSet : watchedAnimationsMap.values()) {
            animatorSet.cancel();
        }
        for (AnimatorSet animatorSet : favoriteAnimationsMap.values()) {
            animatorSet.cancel();
        }
    }

    public static class HashavuaFeedItemHolderInfo extends ItemHolderInfo {
        public String updateAction;

        public HashavuaFeedItemHolderInfo(String updateAction) {
            this.updateAction = updateAction;
        }
    }
}