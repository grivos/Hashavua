package com.invariantlabs.hashavua.model.db;

import android.content.ContentValues;
import android.database.Cursor;

import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.model.HashavuaEntryMetaData;

import java.util.Date;

public class Db {

    public Db() { }

    public abstract static class HashavuaEntriesTable {
        public static final String TABLE_NAME = "hashavua_entry";

        public static final String COLUMN_URL = "url";
        public static final String COLUMN_WATCHED = "watched";
        public static final String COLUMN_FAVORITE = "favorite";

        public static final String CREATE =
                "CREATE TABLE " + TABLE_NAME + " (" +
                        COLUMN_URL + " TEXT PRIMARY KEY, " +
                        COLUMN_WATCHED + " INTEGER NOT NULL, " +
                        COLUMN_FAVORITE + " INTEGER NOT NULL" +
                        " ); ";

        public static ContentValues toContentValues(HashavuaEntry entry) {
            ContentValues values = new ContentValues();
            values.put(COLUMN_URL, entry.getUrl());
            values.put(COLUMN_WATCHED, entry.isWatched()? 1 : 0);
            values.put(COLUMN_FAVORITE, entry.isFavorite()? 1 : 0);
            return values;
        }

        public static HashavuaEntryMetaData parseCursor(Cursor cursor) {
            String url = cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_URL));
            boolean isWatched = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_WATCHED)) == 1;
            boolean isFavorite = cursor.getInt(cursor.getColumnIndexOrThrow(COLUMN_FAVORITE)) == 1;
            return new HashavuaEntryMetaData(url, isWatched, isFavorite);
        }
    }
}