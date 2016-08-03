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
import android.view.LayoutInflater;
import android.view.ViewGroup;
import com.actinarium.nagbox.databinding.TaskItemBinding;

/**
 * A recycler view for a list of tasks
 *
 * @author Paul Danyliuk
 */
public class TasksRVAdapter extends RecyclerView.Adapter<TaskItemHolder> {

    private LayoutInflater mInflater;

    public TasksRVAdapter(Context context) {
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public TaskItemHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        TaskItemBinding binding = TaskItemBinding.inflate(mInflater, parent, false);
        return new TaskItemHolder(binding);
    }

    @Override
    public void onBindViewHolder(TaskItemHolder holder, int position) {
        holder.bind(position);
    }

    @Override
    public int getItemCount() {
        return 10;
    }

}
