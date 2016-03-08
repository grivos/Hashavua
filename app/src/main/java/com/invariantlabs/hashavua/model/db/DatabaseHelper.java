package com.invariantlabs.hashavua.model.db;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.invariantlabs.hashavua.model.HashavuaEntry;
import com.invariantlabs.hashavua.model.HashavuaEntryMetaData;
import com.squareup.sqlbrite.BriteDatabase;
import com.squareup.sqlbrite.SqlBrite;

import java.util.Collection;
import java.util.List;
import java.util.Map;

import javax.inject.Inject;
import javax.inject.Singleton;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Func1;
import rx.schedulers.Schedulers;

@Singleton
public class DatabaseHelper {

    private final BriteDatabase mDb;

    @Inject
    public DatabaseHelper(DbOpenHelper dbOpenHelper) {
        mDb = SqlBrite.create().wrapDatabaseHelper(dbOpenHelper, Schedulers.io());
    }

    public void insertOrUpdateEntry(HashavuaEntry entry) {
        mDb.insert(Db.HashavuaEntriesTable.TABLE_NAME, Db.HashavuaEntriesTable.toContentValues(entry), SQLiteDatabase.CONFLICT_REPLACE);
    }

    public Observable<List<HashavuaEntryMetaData>> getEntriesMetaDataMap() {
        return mDb.createQuery(Db.HashavuaEntriesTable.TABLE_NAME,
                "SELECT * FROM " + Db.HashavuaEntriesTable.TABLE_NAME)
                .mapToList(new Func1<Cursor, HashavuaEntryMetaData>() {
                    @Override
                    public HashavuaEntryMetaData call(Cursor cursor) {
                        return Db.HashavuaEntriesTable.parseCursor(cursor);
                    }
                });
    }

}