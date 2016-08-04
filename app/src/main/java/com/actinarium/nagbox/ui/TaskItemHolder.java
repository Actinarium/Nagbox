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

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.actinarium.nagbox.R;
import com.actinarium.nagbox.databinding.TaskItemBinding;
import com.actinarium.nagbox.model.Task;

/**
 * View holder for a single task item
 *
 * @author Paul Danyliuk
 */
public class TaskItemHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    private final TaskItemBinding mBinding;
    private final Task mTask;
    private final Context mContext;
    private final Host mHost;

    public TaskItemHolder(TaskItemBinding binding, Host host) {
        super(binding.getRoot());
        mBinding = binding;
        mContext = binding.getRoot().getContext();
        mHost = host;

        // Instead of creating new instances of Tasks each time, we will mutate one object
        mTask = new Task();
    }

    public void bind(int position) {
        // todo: replace with actual data
        mTask.title = "Activity #" + position;
        mTask.interval = position + 1;
        mTask.flags = position % 2;

        mBinding.setHost(this);
        mBinding.setTask(mTask);

//         Have to do this because data binding doesn't want to play nice with vector drawable compat
        mBinding.icon.setImageResource(mTask.isActive() ? R.drawable.ic_pause : R.drawable.ic_play);
    }

    @SuppressWarnings("unused")
    public void onClick(View v) {
        Toast.makeText(mContext, "Tile clicked", Toast.LENGTH_SHORT).show();
    }

    @SuppressWarnings("unused")
    public void onMenuClick(View actionMenuBtn) {
        // The action menu icon is the view passed here, so anchor to it
        PopupMenu menu = new PopupMenu(mContext, actionMenuBtn);
        menu.inflate(R.menu.menu_item_actions);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_edit:
                // Passing the copy of this mutable task to be on the safe side
                mHost.onEditTask(new Task(mTask));
                return true;
            case R.id.action_delete:
                // Since we have a Context here, we could call the service directly. But let's keep things consistent.
                mHost.onDeleteTask(new Task(mTask));
                return true;
        }
        return false;
    }

    /**
     * Callbacks to the host (i.e. activity) to handle things triggered from this view item
     */
    public interface Host {
        void onEditTask(Task task);
        void onDeleteTask(Task task);
    }
}
