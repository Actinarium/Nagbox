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

/**
 * An entity object for a task
 *
 * @author Paul Danyliuk
 */
public class Task {

    /**
     * Indicates that this task is active, and alarm must be scheduled for this task
     */
    public static final int FLAG_ACTIVE = 1;
    /**
     * Indicates that this task already fired and was rescheduled, but the user haven't dismissed the notification yet,
     * so it should be included upon the next alarm.
     */
    public static final int FLAG_NOT_DISMISSED = 2;


    public String title;
    /**
     * Interval in minutes
     */
    public int interval;
    /**
     * Timestamp (msec) when this task must fire next time. Holds actual value only when {@link #isActive()} is
     * <code>true</code>, otherwise it can be anything.
     */
    public long nextFireAt;
    /**
     * Holds status flags for this task
     *
     * @see #FLAG_ACTIVE
     * @see #FLAG_NOT_DISMISSED
     */
    public int flags;


    public Task() {}

    /**
     * Copy constructor
     *
     * @param source instance to copy fields from
     */
    public Task(Task source) {
        this.title = source.title;
        this.interval = source.interval;
        this.flags = source.flags;
    }

    public boolean isActive() {
        return (flags & FLAG_ACTIVE) != 0;
    }

}
