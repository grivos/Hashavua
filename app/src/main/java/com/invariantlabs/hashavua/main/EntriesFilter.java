package com.invariantlabs.hashavua.main;

import android.text.TextUtils;

import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.model.HashavuaMainSubject;
import com.invariantlabs.hashavua.model.HashavuaSubject;

import java.util.HashMap;
import java.util.Set;
import java.util.regex.Pattern;

import lombok.Data;

@Data
public class EntriesFilter {

    private boolean showOnlyFavorites;
    private boolean showOnlyNew;
    String searchQuery;
    private HashMap<String, HashavuaSubject> subjectMap = new HashMap<>();
    private HashMap<String, HashavuaMainSubject> mainSubjectMap = new HashMap<>();

    public boolean filter(HashavuaEntry entry) {
        if (showOnlyFavorites && !entry.isFavorite()) return false;
        if (showOnlyNew && entry.isWatched()) return false;
        HashavuaSubject subject = subjectMap.get(entry.getSubject());
        if (subject == null || !subject.isEnabled()) return false;
        HashavuaMainSubject mainSubject = mainSubjectMap.get(entry.getMainSunject());
        if (mainSubject == null || !mainSubject.isEnabled()) return false;
        if (!TextUtils.isEmpty(searchQuery)) {
            boolean contains = Pattern.compile(Pattern.quote(searchQuery), Pattern.CASE_INSENSITIVE).matcher(entry.getTitle()).find();
            if (!contains) {
                return false;
            }
        }
        return true;
    }

    public void updateSubject(HashavuaSubject subject) {
        subjectMap.put(subject.getValue(), subject);
    }

    public void updateMainSubject(HashavuaMainSubject mainSubject) {
        mainSubjectMap.put(mainSubject.getValue(), mainSubject);
    }

    public void addSubjects(Set<HashavuaSubject> subjects) {
        for (HashavuaSubject subject : subjects) {
            if (!subjectMap.containsKey(subject.getValue())) {
                subjectMap.put(subject.getValue(), subject);
            }
        }
    }

    public void addMainSubjects(Set<HashavuaMainSubject> mainSubjects) {
        for (HashavuaMainSubject mainSubject : mainSubjects) {
            if (!mainSubjectMap.containsKey(mainSubject.getValue())) {
                mainSubjectMap.put(mainSubject.getValue(), mainSubject);
            }
        }
    }
}
