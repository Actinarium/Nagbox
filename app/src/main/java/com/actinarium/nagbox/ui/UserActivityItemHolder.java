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
import android.support.graphics.drawable.VectorDrawableCompat;
import android.support.v7.widget.RecyclerView;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupMenu;
import android.widget.Toast;
import com.actinarium.nagbox.R;
import com.actinarium.nagbox.databinding.UserActivityItemBinding;

/**
 * View holder for user activity list item
 *
 * @author Paul Danyliuk
 */
public class UserActivityItemHolder extends RecyclerView.ViewHolder implements PopupMenu.OnMenuItemClickListener {

    private final UserActivityItemBinding mBinding;
    private final Context mContext;

    public UserActivityItemHolder(UserActivityItemBinding binding) {
        super(binding.getRoot());
        mBinding = binding;
        mContext = binding.getRoot().getContext();
    }

    public void bind(int position) {
        // Temporary
        mBinding.setHost(this);

        mBinding.userActivityTitle.setText("Activity #" + position);
        mBinding.userActivitySubtext.setText("Nag every " + (position + 1) + " minutes");
        mBinding.userActivityIndicator.setImageDrawable(VectorDrawableCompat.create(
                mContext.getResources(), R.drawable.ic_play, null
        ));
        mBinding.userActivityIndicator.setContentDescription(mContext.getString(R.string.a11y_start_activity));
    }

    public void onClick(View v) {
        Toast.makeText(mContext, "Tile clicked", Toast.LENGTH_SHORT).show();
    }

    public void onMenuClick(View v) {
        PopupMenu menu = new PopupMenu(mContext, mBinding.userActivityActionsButton);
        menu.inflate(R.menu.menu_item_actions);
        menu.setOnMenuItemClickListener(this);
        menu.show();
    }

    @Override
    public boolean onMenuItemClick(MenuItem menuItem) {
        switch (menuItem.getItemId()) {
            case R.id.action_edit:
                Toast.makeText(mContext, "Edit clicked", Toast.LENGTH_SHORT).show();
                return true;
            case R.id.action_delete:
                Toast.makeText(mContext, "Delete clicked", Toast.LENGTH_SHORT).show();
                return true;
        }
        return false;
    }
}
