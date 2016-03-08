package com.invariantlabs.hashavua.main;

import android.content.Context;
import android.graphics.Rect;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.TouchDelegate;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.invariantlabs.hashavua.HashavuaApp;
import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.util.Util;
import com.invariantlabs.hashavua.view.SubjectsColorHelper;
import com.invariantlabs.hashavua.view.RoundedColorDrawable;

import java.util.Collections;
import java.util.List;

import javax.inject.Inject;
import javax.inject.Named;

import butterknife.Bind;
import butterknife.ButterKnife;
import butterknife.OnClick;

public class HashavuaFeedAdapter extends RecyclerView.Adapter<HashavuaFeedAdapter.ViewHolder> {

    public static final String ACTION_FAVORITE = "action_favorite";
    public static final String ACTION_UNFAVORITE = "action_unfavorited";
    public static final String ACTION_WATCHED = "action_watched";
    public static final float WATCHED_ALPHA = 0.4f;

    private final HashavuaFeedCallback clickListener;
    private final int cornerSize;
    private List<HashavuaEntry> entries = Collections.emptyList();

    @Inject
    @Named("subjects")
    SubjectsColorHelper subjectsColorHelper;
    @Inject
    @Named("main_subjects")
    SubjectsColorHelper mainSubjectsColorHelper;

    public HashavuaFeedAdapter(Context context, HashavuaFeedCallback clickListener) {
        HashavuaApp.get(context).component().inject(this);
        this.clickListener = clickListener;
        cornerSize = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);
    }

    public void setEntries(List<HashavuaEntry> entries) {
        if (Util.isEqualData(this.entries, entries)) {
            return;
        }
        if (entries == null) {
            this.entries = Collections.emptyList();
        } else {
            this.entries = entries;
        }
        notifyDataSetChanged();
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.main_feed_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        HashavuaEntry entry = entries.get(position);
        holder.title.setText(entry.getTitle());
        holder.subject.setText(entry.getSubject());
        holder.mainSubject.setText(entry.getMainSunject());
        holder.subjectDrawable.setColor(subjectsColorHelper.getColorForSubject(entry.getSubject()));
        holder.mainSubjectDrawable.setColor(mainSubjectsColorHelper.getColorForSubject(entry.getMainSunject()));
        if (entry.isWatched()) {
            holder.title.setAlpha(WATCHED_ALPHA);
            holder.subject.setAlpha(WATCHED_ALPHA);
            holder.mainSubject.setAlpha(WATCHED_ALPHA);
        } else {
            holder.title.setAlpha(1f);
            holder.subject.setAlpha(1f);
            holder.mainSubject.setAlpha(1f);
        }
        boolean isSubjectEmpty = TextUtils.isEmpty(entry.getSubject());
        holder.subject.setVisibility(isSubjectEmpty? View.GONE : View.VISIBLE);
        int imageResId = entry.isFavorite()? R.drawable.ic_favorite_24dp : R.drawable.ic_favorite_outline_24dp;
        holder.favorite.setImageResource(imageResId);
    }

    @Override
    public int getItemCount() {
        return entries.size();
    }

    public final class ViewHolder extends RecyclerView.ViewHolder {

        @Bind(R.id.title)
        public TextView title;
        @Bind(R.id.subject)
        public TextView subject;
        @Bind(R.id.mainSubject)
        public TextView mainSubject;
        @Bind((R.id.favorite))
        public ImageView favorite;

        public final RoundedColorDrawable subjectDrawable;
        public final RoundedColorDrawable mainSubjectDrawable;

        public ViewHolder(final View itemView) {
            super(itemView);
            ButterKnife.bind(this, itemView);
            subjectDrawable = new RoundedColorDrawable(cornerSize, true, true, true, true);
            subject.setBackgroundDrawable(subjectDrawable);
            mainSubjectDrawable = new RoundedColorDrawable(cornerSize, true, true, true, true);
            mainSubject.setBackgroundDrawable(mainSubjectDrawable);
            this.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    HashavuaEntry entry = entries.get(getAdapterPosition());
                    if (!entry.isWatched()) {
                        entry.setWatched(true);
                        int adapterPosition = getAdapterPosition();
                        notifyItemChanged(adapterPosition, ACTION_WATCHED);
                    }
                    clickListener.onEntryClicked(entry);
                }
            });
            // increase button touch area
            itemView.post(new Runnable() {
                public void run() {
                    Rect delegateArea = new Rect();
                    View delegate = favorite;
                    delegate.getHitRect(delegateArea);
                    delegateArea.top -= 80;
                    delegateArea.bottom += 80;
                    delegateArea.left -= 80;
                    delegateArea.right += 80;
                    TouchDelegate expandedArea = new TouchDelegate(delegateArea,
                            delegate);
                    itemView.setTouchDelegate(expandedArea);
                }

                ;
            });
        }

        @OnClick(R.id.favorite)
        public void onClickedFavorite() {
            HashavuaEntry entry = entries.get(getAdapterPosition());
            entry.setFavorite(!entry.isFavorite());
            int adapterPosition = getAdapterPosition();
            String action = entry.isFavorite()? ACTION_FAVORITE : ACTION_UNFAVORITE;
            notifyItemChanged(adapterPosition, action);
            clickListener.onFavoriteClicked(entry);
        }

    }

    public interface HashavuaFeedCallback {
        void onEntryClicked(HashavuaEntry entry);
        void onFavoriteClicked(HashavuaEntry entry);
    }
}
