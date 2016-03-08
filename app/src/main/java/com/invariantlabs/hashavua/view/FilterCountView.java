package com.invariantlabs.hashavua.view;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Color;
import android.os.Build;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.widget.LinearLayout;
import android.widget.TextSwitcher;
import android.widget.TextView;
import android.widget.ViewSwitcher;

import com.invariantlabs.hashavua.R;

import butterknife.Bind;
import butterknife.ButterKnife;
import timber.log.Timber;

public class FilterCountView extends LinearLayout {

    @Bind(R.id.totalSwitcher)
    TextSwitcher totalSwitcher;
    @Bind(R.id.filteredSwitcher)
    TextSwitcher filteredSwitcher;

    private int filtered;
    private int total;

    public FilterCountView(Context context) {
        super(context);
    }

    public FilterCountView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public FilterCountView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterCountView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context);
    }

    private void init(Context context) {
        setOrientation(HORIZONTAL);
        LayoutInflater.from(context).inflate(R.layout.filter_count, this);
        ButterKnife.bind(this, this);
        setupSwitcher(totalSwitcher);
        setupSwitcher(filteredSwitcher);
        total = 0;
        filtered = 0;
    }

    private void setupSwitcher(final TextSwitcher switcher) {
        switcher.setInAnimation(getContext(), R.anim.slide_in_top);
        switcher.setOutAnimation(getContext(), R.anim.slide_out_bottom);
        switcher.getOutAnimation().setAnimationListener(new Animation.AnimationListener() {
            @Override
            public void onAnimationStart(Animation animation) {

            }

            @Override
            public void onAnimationEnd(Animation animation) {
                ((TextView)switcher.getNextView()).setText("");
            }

            @Override
            public void onAnimationRepeat(Animation animation) {

            }
        });
        switcher.setText("0");
    }

    public void setCount(int total, int filtered) {
        if (this.total != total) {
            this.total = total;
            totalSwitcher.setText(String.valueOf(total));
        }
        if (this.filtered != filtered) {
            this.filtered = filtered;
            filteredSwitcher.setText(String.valueOf(filtered));
        }
    }

}
