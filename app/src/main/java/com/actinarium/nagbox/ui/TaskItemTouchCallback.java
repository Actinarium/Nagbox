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

import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.util.Log;
import com.actinarium.nagbox.model.Task;

/**
 * <p></p>
 *
 * @author Paul Danyliuk
 */
public class TaskItemTouchCallback extends ItemTouchHelper.SimpleCallback {

    private static final String TAG = "TaskItemTouchCallback";

    private final Host mHost;

    public TaskItemTouchCallback(Host host) {
        super(ItemTouchHelper.UP | ItemTouchHelper.DOWN, ItemTouchHelper.LEFT | ItemTouchHelper.RIGHT);
        mHost = host;
    }

    @Override
    public boolean onMove(RecyclerView recyclerView, RecyclerView.ViewHolder viewHolder, RecyclerView.ViewHolder target) {
        Log.d(TAG, "VH " + viewHolder.getAdapterPosition() + " moved over " + target.getAdapterPosition());
        return true;
    }

    @Override
    public void onSwiped(RecyclerView.ViewHolder viewHolder, int direction) {
        mHost.onDeleteTask(((TaskItemHolder) viewHolder).getTask());
    }

    /**
     * Callbacks to the host (i.e. activity) to handle things triggered from the processed interaction
     */
    public interface Host {
//        void onSwapTaskOrder(Task dragged, int orderBefore);
        void onDeleteTask(Task task);
    }
}
