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

package com.actinarium.nagbox.ui;

import android.database.Cursor;
import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.text.format.DateUtils;
import android.view.View;
import com.actinarium.nagbox.R;
import com.actinarium.nagbox.common.ViewUtils;
import com.actinarium.nagbox.database.NagboxContract;
import com.actinarium.nagbox.database.NagboxContract.TasksTable;
import com.actinarium.nagbox.database.Projection;
import com.actinarium.nagbox.databinding.MainActivityBinding;
import com.actinarium.nagbox.model.Task;
import com.actinarium.nagbox.service.NagboxService;

public class MainActivity extends AppCompatActivity
        implements TaskItemHolder.Host, EditTaskDialogFragment.Host, LoaderManager.LoaderCallbacks<Cursor> {

    private static final int LOADER_TASKS = 1;
    private static final Projection<Task> PROJECTION = NagboxContract.TASK_PROJECTION;

    private MainActivityBinding mBinding;
    private TasksRVAdapter mTasksAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        mBinding.setController(this);
        ViewUtils.setUpToolbar(this, mBinding.getRoot(), R.string.app_name, R.dimen.action_bar_elevation);

        mTasksAdapter = new TasksRVAdapter(this, this);
        mTasksAdapter.setTaskProjection(PROJECTION);
        mBinding.recycler.setAdapter(mTasksAdapter);
        mBinding.recycler.setHasFixedSize(true);

        getSupportLoaderManager().initLoader(LOADER_TASKS, null, this);
    }

    @Override
    public void onToggleTaskStatus(Task task) {
        task.setIsActive(!task.isActive());
        if (task.isActive()) {
            task.nextFireAt = System.currentTimeMillis() + task.interval * DateUtils.MINUTE_IN_MILLIS;
        }
        NagboxService.updateTaskStatus(this, task);
    }

    public void onCreateTask() {
        showCreateEditDialog(null);
    }

    @Override
    public void onEditTask(Task task) {
        showCreateEditDialog(task);
    }

    @Override
    public void onDeleteTask(final Task task) {
        NagboxService.deleteTask(this, task.id);
        Snackbar.make(mBinding.getRoot(), getString(R.string.deleted_message, task.title), Snackbar.LENGTH_SHORT)
                .setAction(R.string.undo, new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        restoreTask(task);
                    }
                })
                .show();
    }

    public void restoreTask(Task task) {
        NagboxService.restoreTask(this, task);
    }

    @Override
    public void saveNewTask(Task task) {
        NagboxService.createTask(this, task);
    }

    @Override
    public void saveEditedTask(Task task) {
        NagboxService.updateTask(this, task);
    }

    /**
     * Show the edit/create task dialog. The dialog will make the appropriate service call on its submit
     *
     * @param task task to edit, or <code>null</code> to make a dialog for creating a new task
     */
    private void showCreateEditDialog(@Nullable Task task) {
        EditTaskDialogFragment fragment = EditTaskDialogFragment.newInstance(task);
        fragment.show(getSupportFragmentManager(), EditTaskDialogFragment.TAG);
    }

    // Loader callbacks -----------------------------------------------------

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        return new CursorLoader(
                this,
                TasksTable.CONTENT_URI,
                PROJECTION.getProjection(),
                null, null, null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
       mTasksAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        mTasksAdapter.swapCursor(null);
    }
}
