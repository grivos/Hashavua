package com.invariantlabs.hashavua.view;


import android.annotation.TargetApi;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Build;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.invariantlabs.hashavua.HashavuaApp;
import com.invariantlabs.hashavua.HashavuaGraph;
import com.invariantlabs.hashavua.R;
import com.invariantlabs.hashavua.model.Filterable;
import com.invariantlabs.hashavua.util.Util;

import java.util.ArrayList;
import java.util.List;

public class FilterablesView extends LinearLayout implements CompoundButton.OnCheckedChangeListener {

    private SubjectsColorHelper colorHelper;
    private LayoutInflater inflater;
    private FilterableViewCallback callback;
    private int cornerSize;
    private List<CheckBox> checkBoxes;
    private List<? extends Filterable> filterables = new ArrayList<>();
    private boolean shouldDrawBackground;

    public FilterablesView(Context context) {
        super(context);
    }

    public FilterablesView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context, attrs);
    }

    public FilterablesView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context, attrs);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    public FilterablesView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        init(context, attrs);
    }

    private void init(Context context, AttributeSet attrs) {
        HashavuaGraph graph = HashavuaApp.get(context).component();
        graph.inject(this);
        cornerSize = context.getResources().getDimensionPixelSize(R.dimen.corner_radius);
        inflater = LayoutInflater.from(context);
        setOrientation(VERTICAL);
        setDividerDrawable(getContext().getResources().getDrawable(R.drawable.empty_divider));
        setShowDividers(SHOW_DIVIDER_MIDDLE);
        checkBoxes = new ArrayList<>();
        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.FilterablesView);
        shouldDrawBackground = a.getBoolean(R.styleable.FilterablesView_shouldDrawBackground, true);
        int colorHelperIndex = a.getInt(R.styleable.FilterablesView_colorHelperName, 1);
        colorHelper = ColorHelperType.getColorHelperType(colorHelperIndex).getColorHelper(graph);
        a.recycle();

    }

    public void setFilterables(List<? extends Filterable> filterables) {
        if (!Util.isEqualData(this.filterables, filterables)) {
            removeAllViews();
            this.filterables = filterables;
            checkBoxes.clear();
            for (Filterable filterable : filterables) {
                addFilterable(filterable);
            }
        }
    }

    private void addFilterable(Filterable filterable) {
        View view = inflater.inflate(R.layout.item_filterable, this, false);
        TextView titleView = (TextView) view.findViewById(R.id.title);
        final CheckBox checkBox = (CheckBox) view.findViewById(R.id.checkbox);
        titleView.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View view) {
                checkOnly(checkBox);
            }
        });
        String title = TextUtils.isEmpty(filterable.getValue())? getContext().getString(R.string.no_subject) : filterable.getValue();
        titleView.setText(title);
        checkBox.setChecked(filterable.isEnabled());
        checkBox.setTag(filterable);
        checkBox.setOnCheckedChangeListener(this);
        if (shouldDrawBackground) {
            titleView.setTextColor(Color.WHITE);
            RoundedColorDrawable drawable = new RoundedColorDrawable(cornerSize, true, true, true, true);
            drawable.setColor(colorHelper.getColorForSubject(filterable.getValue()));
            titleView.setBackgroundDrawable(drawable);
        }
        checkBoxes.add(checkBox);
        addView(view);
    }

    private void checkOnly(CheckBox checkBox) {
        for (CheckBox box : checkBoxes) {
            box.setChecked(checkBox == box);
        }
    }

    @Override
    public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
        Filterable filterable = (Filterable) compoundButton.getTag();
        filterable.setEnabled(b);
        callback.onFilterableEnabledChanged(this, filterable);
    }

    public void setFilterablesViewCallback(FilterableViewCallback callback) {
        this.callback = callback;
    }

    public void checkAllFilterables() {
        for (int i = 0; i < checkBoxes.size(); i++) {
            CheckBox checkBox = checkBoxes.get(i);
            checkBox.setChecked(true);
        }
    }

    public interface FilterableViewCallback {
        void onFilterableEnabledChanged(FilterablesView view, Filterable filterable);
    }

    private enum ColorHelperType {
        SUBJECTS(1),
        MAIN_SUBJECTS(2);

        private final int index;
        private ColorHelperType(int index) {
            this.index = index;
        }

        public static ColorHelperType getColorHelperType(int index) {
            for (ColorHelperType colorHelperType : ColorHelperType.values()) {
                if (colorHelperType.index == index) {
                    return colorHelperType;
                }
            }
            return null;
        }

        public SubjectsColorHelper getColorHelper(HashavuaGraph graph) {
            switch (this) {
                case SUBJECTS: return graph.subjectsColorHelper();
                case MAIN_SUBJECTS: return graph.mainSubjectsColorHelper();
                default: return null;
            }
        }
    }
}
