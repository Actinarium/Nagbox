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

package com.actinarium.nagbox.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;
import com.actinarium.nagbox.database.AppDbHelper;
import com.actinarium.nagbox.database.NagboxContract.BuildingBlocks;
import com.actinarium.nagbox.database.NagboxContract.TasksTable;
import com.actinarium.nagbox.model.Task;

/**
 * An intent service that handles task operations and alarm management.
 *
 * @author Paul Danyliuk
 */
public class NagboxService extends IntentService {

    private static final String TAG = "NagboxService";

    private static final String ACTION_CREATE_TASK = "com.actinarium.nagbox.intent.action.CREATE_TASK";
    private static final String ACTION_UPDATE_TASK = "com.actinarium.nagbox.intent.action.UPDATE_TASK";
    private static final String ACTION_UPDATE_TASK_STATUS = "com.actinarium.nagbox.intent.action.UPDATE_TASK_STATUS";
    private static final String ACTION_DELETE_TASK = "com.actinarium.nagbox.intent.action.DELETE_TASK";
    private static final String ACTION_RESTORE_TASK = "com.actinarium.nagbox.intent.action.RESTORE_TASK";

    private static final String EXTRA_TASK = "com.actinarium.nagbox.intent.extra.TASK";
    private static final String EXTRA_TASK_ID = "com.actinarium.nagbox.intent.extra.TASK_ID";

    /**
     * Create a new unstarted task. Doesn't trigger rescheduling alarms.
     *
     * @param context context
     * @param task    task to create
     */
    public static void createTask(Context context, Task task) {
        Intent intent = new Intent(context, NagboxService.class);
        intent.setAction(ACTION_CREATE_TASK);
        intent.putExtra(EXTRA_TASK, task);
        context.startService(intent);
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
        Intent intent = new Intent(context, NagboxService.class);
        intent.setAction(ACTION_UPDATE_TASK);
        intent.putExtra(EXTRA_TASK, task);
        context.startService(intent);
    }

    /**
     * Update task status (flags). Use this to start or stop the task. {@link Task#id} must be set. Will result in
     * rescheduling the alarm to closer time if needed.
     *
     * @param context context
     * @param task    task to update its flags
     */
    public static void updateTaskStatus(Context context, Task task) {
        Intent intent = new Intent(context, NagboxService.class);
        intent.setAction(ACTION_UPDATE_TASK_STATUS);
        intent.putExtra(EXTRA_TASK, task);
        context.startService(intent);
    }

    /**
     * Delete the task entirely. Will trigger rescheduling the alarm to later time if needed, or cancelling it.
     *
     * @param context context
     * @param taskId  ID of the task to delete
     */
    public static void deleteTask(Context context, long taskId) {
        Intent intent = new Intent(context, NagboxService.class);
        intent.setAction(ACTION_DELETE_TASK);
        intent.putExtra(EXTRA_TASK_ID, taskId);
        context.startService(intent);
    }

    /**
     * Same as {@link #createTask(Context, Task)}, but will insert the task with its old ID and trigger rescheduling the
     * alarm.
     *
     * @param context context
     * @param task    task to restore
     */
    public static void restoreTask(Context context, Task task) {
        Intent intent = new Intent(context, NagboxService.class);
        intent.setAction(ACTION_RESTORE_TASK);
        intent.putExtra(EXTRA_TASK, task);
        context.startService(intent);
    }


    public NagboxService() {
        super(TAG);
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        if (intent == null) {
            return;
        }

        Task task;
        switch (intent.getAction()) {
            case ACTION_UPDATE_TASK_STATUS:
                task = intent.getParcelableExtra(EXTRA_TASK);
                handleUpdateTaskFlags(task);
                break;
            case ACTION_CREATE_TASK:
                task = intent.getParcelableExtra(EXTRA_TASK);
                handleCreateTask(task);
                break;
            case ACTION_UPDATE_TASK:
                task = intent.getParcelableExtra(EXTRA_TASK);
                handleUpdateTask(task);
                break;
            case ACTION_DELETE_TASK:
                handleDeleteTask(intent.getLongExtra(EXTRA_TASK_ID, Task.NO_ID));
                break;
            case ACTION_RESTORE_TASK:
                task = intent.getParcelableExtra(EXTRA_TASK);
                handleRestoreTask(task);
                break;
        }
    }

    private void handleCreateTask(Task task) {
        // todo: implement with dbops
        long result = AppDbHelper.getInstance(this)
                .getWritableDatabase()
                .insert(TasksTable.TABLE_NAME, null, task.toContentValues());

        if (result != -1) {
            task.id = result;
            // Need to notify the cursor and ultimately the loader that data needs to be reloaded
            getContentResolver().notifyChange(TasksTable.CONTENT_URI, null);
        }
    }

    private void handleUpdateTask(Task task) {
        if (task.id < 0) {
            Log.e(TAG, "Was trying to update task with invalid/unset ID=" + task.id);
            return;
        }

        // todo: implement with dbops
        int result = AppDbHelper.getInstance(this)
                .getWritableDatabase()
                .update(TasksTable.TABLE_NAME, task.toContentValues(), BuildingBlocks.SELECTION_ID, new String[]{Long.toString(task.id)});

        if (result == 1) {
            // Need to notify the cursor and ultimately the loader that data needs to be reloaded
            getContentResolver().notifyChange(TasksTable.getUriForItem(task.id), null);
        }
    }

    private void handleUpdateTaskFlags(Task task) {
        if (task.id < 0) {
            Log.e(TAG, "Was trying to update flags of the task with invalid/unset ID=" + task.id);
            return;
        }

        // todo: implement with dbops
        int result = AppDbHelper.getInstance(this)
                .getWritableDatabase()
                .update(TasksTable.TABLE_NAME, task.toContentValuesOnStatusChange(), BuildingBlocks.SELECTION_ID, new String[]{Long.toString(task.id)});

        if (result == 1) {
            // Need to notify the cursor and ultimately the loader that data needs to be reloaded
            getContentResolver().notifyChange(TasksTable.getUriForItem(task.id), null);
            rescheduleAlarm();
        }
    }

    private void handleDeleteTask(long taskId) {
        if (taskId < 0) {
            Log.e(TAG, "Was trying to delete task with invalid ID=" + taskId);
            return;
        }

        // todo: implement with dbops
        int result = AppDbHelper.getInstance(this)
                .getWritableDatabase()
                .delete(TasksTable.TABLE_NAME, BuildingBlocks.SELECTION_ID, new String[]{Long.toString(taskId)});

        if (result != 0) {
            // Need to notify the cursor and ultimately the loader that data needs to be reloaded
            getContentResolver().notifyChange(TasksTable.getUriForItem(taskId), null);
            rescheduleAlarm();
        }
    }

    private void handleRestoreTask(Task task) {
        // todo: implement with dbops
        long result = AppDbHelper.getInstance(this)
                .getWritableDatabase()
                .insert(TasksTable.TABLE_NAME, null, task.toContentValuesOnRestore());

        if (result != -1) {
            task.id = result;
            // Need to notify the cursor and ultimately the loader that data needs to be reloaded
            getContentResolver().notifyChange(TasksTable.CONTENT_URI, null);
            rescheduleAlarm();
        }
    }

    private void rescheduleAlarm() {
        // todo: determine the closest nag timestamp and set up the alarm
    }

}
