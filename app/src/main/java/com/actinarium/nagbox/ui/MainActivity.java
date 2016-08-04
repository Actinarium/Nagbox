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

import android.databinding.DataBindingUtil;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.widget.Toast;
import com.actinarium.nagbox.R;
import com.actinarium.nagbox.common.ViewUtils;
import com.actinarium.nagbox.databinding.MainActivityBinding;
import com.actinarium.nagbox.model.Task;

public class MainActivity extends AppCompatActivity implements TaskItemHolder.Host, EditTaskDialogFragment.Host {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        MainActivityBinding binding = DataBindingUtil.setContentView(this, R.layout.activity_main);
        binding.setController(this);
        ViewUtils.setUpToolbar(this, binding.getRoot(), R.string.app_name, R.dimen.action_bar_elevation);

        binding.recycler.setAdapter(new TasksRVAdapter(this, this));
        binding.recycler.setHasFixedSize(true);
    }

    public void onCreateTask() {
        showCreateEditDialog(null);
    }

    @Override
    public void onEditTask(Task task) {
        showCreateEditDialog(task);
    }

    @Override
    public void onDeleteTask(Task task) {
        // todo: service call to delete the task
        // todo: show snackbar to undo
        Toast.makeText(MainActivity.this, "Task almost deleted", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveNewTask(Task task) {
        // todo: service call to create the task
        Toast.makeText(MainActivity.this, "New task almost saved", Toast.LENGTH_SHORT).show();
    }

    @Override
    public void saveEditedTask(Task task) {
        // todo: service call to update the task
        Toast.makeText(MainActivity.this, "Edited task almost saved", Toast.LENGTH_SHORT).show();
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
}
