/*
 * Copyright (C) 2016 Actinarium
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.actinarium.nagbox.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.support.annotation.RawRes;
import android.text.TextUtils;
import com.actinarium.nagbox.R;
import com.actinarium.nagbox.common.ReaderUtils;
import com.actinarium.nagbox.model.Task;

/**
 * A standard DB open helper class, as per Udacity course / Android docs. Singleton.
 *
 * @author Paul Danyliuk
 */
public class AppDbHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "app.db";
    private static final int DATABASE_VERSION = 1;

    private static volatile AppDbHelper sInstance;

    private final Context mContext;

    private AppDbHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    public static AppDbHelper getInstance(Context context) {
        AppDbHelper localInstance = sInstance;
        if (localInstance == null) {
            synchronized (AppDbHelper.class) {
                localInstance = sInstance;
                if (localInstance == null) {
                    sInstance = localInstance = new AppDbHelper(context);
                }
            }
        }
        return localInstance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        execFile(db, R.raw.schema_v1);
        importInitialData(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int from, int to) {
        // no-op, nothing to upgrade at the moment
    }

    /**
     * Execute all SQL instructions from the specified text file. The instructions must be separated by semicolons (;)
     *
     * @param db         Database instance
     * @param sqlFileRes ID of the file with SQL queries placed in /res/raw
     */
    private void execFile(SQLiteDatabase db, @RawRes int sqlFileRes) {
        final String[] queries = TextUtils.join("", ReaderUtils.readLines(mContext, sqlFileRes)).split(";");
        for (String query : queries) {
            db.execSQL(query);
        }
    }

    /**
     * Import starter data into the database
     *
     * @param db Database instance
     */
    private void importInitialData(SQLiteDatabase db) {
        String[] starterTaskTitles = mContext.getResources().getStringArray(R.array.starter_tasks);

        NagboxDbOps.Transaction transaction = NagboxDbOps.startTransaction(db);
        Task reusableTask = new Task();
        for (String title : starterTaskTitles) {
            reusableTask.title = title;
            transaction.createTask(reusableTask);
        }
        transaction.commit();
    }

}
