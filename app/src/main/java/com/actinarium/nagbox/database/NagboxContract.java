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

import android.database.Cursor;
import android.net.Uri;
import android.provider.BaseColumns;
import android.support.annotation.Nullable;
import com.actinarium.nagbox.model.Task;

/**
 * Nagbox database contract class
 *
 * @author Paul Danyliuk
 */
public final class NagboxContract {

    private NagboxContract() {}

    public static final String CONTENT_AUTHORITY = "com.actinarium.nagbox.provider";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);

    public static final String PATH_TASKS = "tasks";

    // Table mappings ------------------------------------

    public static class TasksTable implements BaseColumns {
        // Database stuff
        public static final String TABLE_NAME = "tasks";

        public static final String COL_TITLE = "title";
        public static final String COL_INTERVAL = "interval";
        public static final String COL_FLAGS = "flags";
        public static final String COL_NEXT_FIRE_AT = "next_fire_at";

        // Content provider stuff
        public static final Uri CONTENT_URI = BASE_CONTENT_URI.buildUpon().appendPath(PATH_TASKS).build();
        public static final String CONTENT_TYPE_DIR = "vnd.android.cursor.dir/" + CONTENT_AUTHORITY + '.' + PATH_TASKS;
    }

    // Building blocks -----------------------------------
    // Selection and sorting expressions, join clauses etc belong here

    // Projections ---------------------------------------

    public static final TaskProjection TASK_PROJECTION = new TaskProjection();

    /**
     * A projection to get all Task fields.
     */
    public static final class TaskProjection implements Projection<Task> {

        private static final String[] COLUMNS = {
                TasksTable._ID,
                TasksTable.COL_TITLE,
                TasksTable.COL_INTERVAL,
                TasksTable.COL_FLAGS,
                TasksTable.COL_NEXT_FIRE_AT
        };

        @Override
        public String[] getProjection() {
            return COLUMNS;
        }

        @Override
        public Task mapCursorToModel(Cursor cursor, @Nullable Task task) {
            if (task == null) {
                task = new Task();
            }

            task.id = cursor.getLong(0);
            task.title = cursor.getString(1);
            task.interval = cursor.getInt(2);
            task.flags = cursor.getInt(3);
            task.nextFireAt = cursor.getLong(4);

            return task;
        }

        @Override
        public long getId(Cursor cursor) {
            return cursor.getLong(0);
        }
    }

}
