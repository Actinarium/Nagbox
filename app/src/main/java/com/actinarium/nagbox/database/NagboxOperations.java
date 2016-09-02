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
import android.os.AsyncTask;
import android.util.Log;
import com.actinarium.nagbox.model.Task;
import com.actinarium.nagbox.service.NagboxService;

/**
 * An umbrella class for all {@link AsyncTask AsyncTasks} for submitting the data into the database
 *
 * @author Paul Danyliuk
 */
public class NagboxOperations {

    static final String TAG = "NagboxOperations";

    /**
     * Create a new unstarted task. Doesn't trigger rescheduling alarms.
     *
     * @param context context
     * @param task    task to create
     */
    public static void createTask(Context context, Task task) {
        new CreateTask(context, task).doInBackground();
    }

    /**
     * Update task description. Doesn't update the flags (i.e. doesn't start or stop the task), and as of now it doesn't
     * reschedule next alarm either. If you need to update task status, use {@link #updateTaskStatus(Context, Task)}.
     * {@link Task#id} must be set.
     *
     * @param context context
     * @param task    task to update
     */
    public static void updateTask(Context context, Task task) {
        new UpdateTask(context, task).doInBackground();
    }

    /**
     * Update task status (flags). Use this to start or stop the task. {@link Task#id} must be set. Will result in
     * rescheduling the alarm to closer time if needed.
     *
     * @param context context
     * @param task    task to update its flags
     */
    public static void updateTaskStatus(Context context, Task task) {
        new UpdateTaskStatus(context, task).doInBackground();
    }

    /**
     * Delete the task entirely. Will trigger rescheduling the alarm to later time if needed, or cancelling it.
     *
     * @param context context
     * @param taskId  ID of the task to delete
     */
    public static void deleteTask(Context context, long taskId) {
        new DeleteTask(context, taskId).doInBackground();
    }

    /**
     * Same as {@link #createTask(Context, Task)}, but will insert the task with its old ID and trigger rescheduling the
     * alarm.
     *
     * @param context context
     * @param task    task to restore
     */
    public static void restoreTask(Context context, Task task) {
        new RestoreTask(context, task).doInBackground();
    }

    // Internals ---------------------------------------------------------

    private abstract static class DatabaseOperationAsyncTask extends AsyncTask<Void, Void, Void> {
        protected Context mContext;
        protected SQLiteDatabase mDatabase;

        public DatabaseOperationAsyncTask(Context context) {
            mContext = context.getApplicationContext();
            mDatabase = NagboxDbHelper.getInstance(mContext).getWritableDatabase();
        }
    }

    private static class CreateTask extends DatabaseOperationAsyncTask {
        private Task mTask;
        public CreateTask(Context context, Task task) {
            super(context);
            mTask = task;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Our app must ensure that task order is correct and unique. So assign the order = max(order) + 1
            // We could (and should) do this atomically using INSERT with sub-query, but that's not trivial with given APIs.
            int maxOrder = NagboxDbOps.getMaxTaskOrder(mDatabase);
            mTask.displayOrder = maxOrder + 1;

            // In the end of the method we put everything into the DB using DbOps.Transaction
            boolean isSuccess = NagboxDbOps.startTransaction(mDatabase)
                    .createTask(mTask)
                    .commit();

            // Process transaction result.
            // If successful, you still need to notify the cursor so that any loaders that listen to this data would reload
            if (isSuccess) {
                mContext.getContentResolver().notifyChange(NagboxContract.TasksTable.CONTENT_URI, null);
            } else {
                Log.e(TAG, "Couldn't create task " + mTask);
            }

            return null;
        }
    }

    private static class UpdateTask extends DatabaseOperationAsyncTask {
        private Task mTask;
        public UpdateTask(Context context, Task task) {
            super(context);
            mTask = task;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mTask.id < 0) {
                Log.e(TAG, "Was trying to update task with invalid/unset ID=" + mTask.id);
                return null;
            }

            boolean isSuccess = NagboxDbOps.startTransaction(mDatabase)
                    .updateTask(mTask)
                    .commit();

            if (isSuccess) {
                // Even though our content provider doesn't know about a single item URI yet, won't hurt to do it right
                mContext.getContentResolver().notifyChange(NagboxContract.TasksTable.getUriForItem(mTask.id), null);
            } else {
                Log.e(TAG, "Couldn't update task " + mTask);
            }

            return null;
        }
    }

    private static class UpdateTaskStatus extends DatabaseOperationAsyncTask {
        private Task mTask;
        public UpdateTaskStatus(Context context, Task task) {
            super(context);
            mTask = task;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mTask.id < 0) {
                Log.e(TAG, "Was trying to update flags of the task with invalid/unset ID=" + mTask.id);
                return null;
            }

            boolean isSuccess = NagboxDbOps.startTransaction(mDatabase)
                    .updateTaskStatus(mTask)
                    .commit();

            if (isSuccess) {
                mContext.getContentResolver().notifyChange(NagboxContract.TasksTable.getUriForItem(mTask.id), null);
                NagboxService.requestAlarmUpdate(mContext);
            } else {
                Log.e(TAG, "Couldn't update status of task " + mTask);
            }

            return null;
        }
    }

    private static class DeleteTask extends DatabaseOperationAsyncTask {
        private long mTaskId;
        public DeleteTask(Context context, long taskId) {
            super(context);
            mTaskId = taskId;
        }

        @Override
        protected Void doInBackground(Void... params) {
            if (mTaskId < 0) {
                Log.e(TAG, "Was trying to delete task with invalid ID=" + mTaskId);
                return null;
            }

            boolean isSuccess = NagboxDbOps.startTransaction(mDatabase)
                    .deleteTask(mTaskId)
                    .commit();

            if (isSuccess) {
                mContext.getContentResolver().notifyChange(NagboxContract.TasksTable.getUriForItem(mTaskId), null);
                NagboxService.requestAlarmUpdate(mContext);
            } else {
                Log.e(TAG, "Couldn't delete task with ID " + mTaskId);
            }

            return null;
        }
    }

    private static class RestoreTask extends DatabaseOperationAsyncTask {
        private Task mTask;
        public RestoreTask(Context context, Task task) {
            super(context);
            mTask = task;
        }

        @Override
        protected Void doInBackground(Void... params) {
            // Restoring is the same as creating, and our task already has an order field set correctly, and an ID to notify
            boolean isSuccess = NagboxDbOps.startTransaction(mDatabase)
                    .createTask(mTask)
                    .commit();

            if (isSuccess) {
                mContext.getContentResolver().notifyChange(NagboxContract.TasksTable.getUriForItem(mTask.id), null);
                NagboxService.requestAlarmUpdate(mContext);
            } else {
                Log.e(TAG, "Couldn't restore task " + mTask);
            }

            return null;
        }
    }
}
