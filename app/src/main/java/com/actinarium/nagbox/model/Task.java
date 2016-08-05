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

package com.actinarium.nagbox.model;

import android.content.ContentValues;
import android.os.Parcel;
import android.os.Parcelable;
import com.actinarium.nagbox.database.NagboxContract.TasksTable;

/**
 * An entity object for a task
 *
 * @author Paul Danyliuk
 */
public class Task implements Parcelable {

    /**
     * Indicates that this task is active, and alarm must be scheduled for this task
     */
    public static final int FLAG_ACTIVE = 1;
    /**
     * Indicates that this task already fired and was rescheduled, but the user haven't dismissed the notification yet,
     * so it should be included upon the next alarm.
     */
    public static final int FLAG_NOT_DISMISSED = 2;

    public static final int DEFAULT_INTERVAL = 5;


    public long id;
    public String title;
    /**
     * Interval in minutes
     */
    public int interval = DEFAULT_INTERVAL;
    /**
     * Holds status flags for this task
     *
     * @see #FLAG_ACTIVE
     * @see #FLAG_NOT_DISMISSED
     */
    public int flags;
    /**
     * Timestamp (msec) when this task must fire next time. Holds actual value only when {@link #isActive()} is
     * <code>true</code>, otherwise it can be anything.
     */
    public long nextFireAt;


    public Task() {}

    /**
     * Copy constructor
     *
     * @param source instance to copy fields from
     */
    public Task(Task source) {
        this.id = source.id;
        this.title = source.title;
        this.interval = source.interval;
        this.flags = source.flags;
        this.nextFireAt = source.nextFireAt;
    }

    public boolean isActive() {
        return (flags & FLAG_ACTIVE) != 0;
    }

    // Export into ContentValues for insert/update ops -------------------

    /**
     * Get {@link ContentValues} for this model to feed it to create/update operations
     *
     * @return <code>ContentValues</code> with title, interval, and flags
     * @see #toContentValuesOnStatusChange()
     */
    public ContentValues toContentValues() {
        ContentValues values = new ContentValues(3);
        values.put(TasksTable.COL_TITLE, title);
        values.put(TasksTable.COL_INTERVAL, interval);
        values.put(TasksTable.COL_FLAGS, flags);
        return values;
    }

    /**
     * Get {@link ContentValues} for this model when its status (flags, next fire time) needs to be updated
     *
     * @return <code>ContentValues</code> with flags and nextFireAt
     */
    public ContentValues toContentValuesOnStatusChange() {
        ContentValues values = new ContentValues(2);
        values.put(TasksTable.COL_FLAGS, flags);
        values.put(TasksTable.COL_NEXT_FIRE_AT, nextFireAt);
        return values;
    }

    // Getters/setters for 2-way data binding ----------------------------

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getIntervalAsString() {
        return Integer.toString(interval);
    }

    public void setIntervalAsString(String value) {
        interval = value.isEmpty() ? 0 : Integer.parseInt(value);
    }

    // Auto-generated Parcelable stuff -----------------------------------

    protected Task(Parcel in) {
        id = in.readLong();
        title = in.readString();
        interval = in.readInt();
        flags = in.readInt();
        nextFireAt = in.readLong();
    }

    public static final Creator<Task> CREATOR = new Creator<Task>() {
        @Override
        public Task createFromParcel(Parcel in) {
            return new Task(in);
        }

        @Override
        public Task[] newArray(int size) {
            return new Task[size];
        }
    };

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int i) {
        parcel.writeLong(id);
        parcel.writeString(title);
        parcel.writeInt(interval);
        parcel.writeInt(flags);
        parcel.writeLong(nextFireAt);
    }
}
