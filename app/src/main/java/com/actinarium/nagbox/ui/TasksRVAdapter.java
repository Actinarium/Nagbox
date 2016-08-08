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
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.actinarium.nagbox.database.Projection;
import com.actinarium.nagbox.databinding.TaskItemBinding;
import com.actinarium.nagbox.model.Task;

/**
 * A recycler view adapter for a list of tasks
 *
 * @author Paul Danyliuk
 */
public class TasksRVAdapter extends RecyclerView.Adapter<TaskItemHolder> {

    private LayoutInflater mInflater;
    private TaskItemHolder.Host mHost;
    private Cursor mCursor;
    private Projection<Task> mTaskProjection;

    public TasksRVAdapter(Context context, TaskItemHolder.Host host) {
        mInflater = LayoutInflater.from(context);
        mHost = host;
        setHasStableIds(true);
    }

    @Override
    public TaskItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemBinding binding = TaskItemBinding.inflate(mInflater, parent, false);
        return new TaskItemHolder(binding, mHost);
    }

    @Override
    public void onBindViewHolder(TaskItemHolder holder, int position) {
        mCursor.moveToPosition(position);
        holder.bind(mCursor, mTaskProjection);
    }

    @Override
    public int getItemCount() {
        return mCursor == null ? 0 : mCursor.getCount();
    }

    @Override
    public long getItemId(int position) {
        // No need to check for cursor nullity, as this will never be called when there are 0 items
        mCursor.moveToPosition(position);
        return mTaskProjection.getId(mCursor);
    }

    /**
     * Swap in a new cursor, returning the old cursor. The returned old cursor is <b>not closed.</b>
     *
     * @param newCursor The new cursor to be used.
     * @return Returns the previously set cursor, or null if there wasn't one. If the given new cursor is the same
     * instance is the previously set cursor, null is also returned.
     */
    public Cursor swapCursor(Cursor newCursor) {
        if (newCursor == mCursor) {
            return null;
        }
        Cursor oldCursor = mCursor;
        mCursor = newCursor;
        notifyDataSetChanged();
        return oldCursor;
    }

    public void setTaskProjection(Projection<Task> taskProjection) {
        mTaskProjection = taskProjection;
    }
}
