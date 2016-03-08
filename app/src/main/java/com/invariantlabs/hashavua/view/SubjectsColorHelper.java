package com.invariantlabs.hashavua.view;

import android.content.Context;
import android.content.res.Resources;
import android.text.TextUtils;

import com.invariantlabs.hashavua.R;

import java.util.HashMap;
import java.util.Map;


public class SubjectsColorHelper {

    private int[] colors;
    private Map<String, Integer> colorsMap;
    private String emptySubjectStr;


    public SubjectsColorHelper(Context context, int colorsResId) {
        colors = context.getResources().getIntArray(colorsResId);
        colorsMap = new HashMap<>();
        Resources resources = context.getResources();
        emptySubjectStr = resources.getString(R.string.no_subject);

    }

    public int getColorForSubject(String subject) {
        if (TextUtils.isEmpty(subject)) {
            subject = emptySubjectStr;
        }
        if (colorsMap.containsKey(subject)) {
            return colorsMap.get(subject);
        } else {
            int index = colorsMap.keySet().size() % colors.length;
            int color = colors[index];
            colorsMap.put(subject, color);
            return color;
        }
    }
}
