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

package com.actinarium.nagbox.database;

import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import com.actinarium.nagbox.database.NagboxContract.BuildingBlocks;
import com.actinarium.nagbox.database.NagboxContract.TasksTable;
import com.actinarium.nagbox.model.Task;

/**
 * Database operations facade with transaction builder. Here's the place to put all insert/update/delete logic, as well
 * as query logic that's only needed in the service but not in content provider.
 * <p/>
 * In this particular project I made the class a static utility class. Feel free to make it non-static if you may need
 * to swap persistence implementations (e.g. SQLite and Firebase).
 *
 * @author Paul Danyliuk
 */
public final class NagboxDbOps {

    private NagboxDbOps() {}

    /**
     * Get a transaction builder for inserting/updating stuff
     *
     * @param writableDb Database
     * @return Transaction builder and executor
     * @see Transaction
     */
    public static Transaction startTransaction(SQLiteDatabase writableDb) {
        return new Transaction(writableDb);
    }


    /**
     * Database insert/update transaction builder and executor. <b>Note:</b> the commands are actually executed as soon
     * as called, unlike with usual action builders where everything is executed in the end. This is so to make it
     * possible to obtain intermediate results. If an action fails, all calls to make further actions in this
     * transaction are suppressed.
     */
    public static class Transaction {

        private SQLiteDatabase mDatabase;
        private boolean mIsSuccess;

        /**
         * Create a new transaction
         *
         * @param database Writable database obtained from {@link SQLiteOpenHelper#getWritableDatabase()}
         */
        public Transaction(SQLiteDatabase database) {
            mDatabase = database;
            mIsSuccess = true;
            mDatabase.beginTransaction();
        }

        /**
         * Get transaction result: <code>true</code> if all actions within this transaction has been successful so far,
         * and <code>false</code> if at least one has failed.
         *
         * @return intermediate result after the last performed action. Will always return <code>false</code> after the
         * transaction is committed.
         */
        public boolean getIntermediateResult() {
            return mIsSuccess;
        }

        /**
         * Commit the transaction.
         *
         * @return whether the transaction was performed successfully
         */
        public boolean commit() {
            if (mIsSuccess) {
                mDatabase.setTransactionSuccessful();
                mDatabase.endTransaction();
            }
            // Set mIsSuccess to false so that subsequent calls to the transaction are ignored
            boolean isRealSuccess = mIsSuccess;
            mIsSuccess = false;

            return isRealSuccess;
        }

        /**
         * Insert the task into the database.
         *
         * @param task Task to insert. If the operation is successful, {@link Task#id} will be set.
         * @return this for chaining
         */
        public Transaction createTask(Task task) {
            // Suppress the action if transaction is already failed
            if (!mIsSuccess) {
                return this;
            }

            long id = mDatabase.insert(
                    TasksTable.TABLE_NAME,
                    null,
                    task.toContentValues()
            );
            if (id != -1) {
                task.id = id;
            } else {
                mIsSuccess = false;
                mDatabase.endTransaction();
            }

            return this;
        }

        /**
         * Update the task. Only description fields exported in {@link Task#toContentValues()} will be updated.
         *
         * @param task Task to update. Must have {@link Task#id} set.
         * @return this for chaining
         * @see #updateTaskStatus(Task)
         */
        public Transaction updateTask(Task task) {
            if (!mIsSuccess) {
                return this;
            }

            int rowsAffected = mDatabase.update(
                    TasksTable.TABLE_NAME,
                    task.toContentValues(),
                    BuildingBlocks.SELECTION_ID,
                    new String[]{Long.toString(task.id)}
            );
            if (rowsAffected != 1) {
                mIsSuccess = false;
                mDatabase.endTransaction();
            }

            return this;
        }

        /**
         * Update task status, i.e. only status fields exported in {@link Task#toContentValuesOnStatusChange()}.
         *
         * @param task Task whose status to update. Must have {@link Task#id} set.
         * @return this for chaining
         */
        public Transaction updateTaskStatus(Task task) {
            if (!mIsSuccess) {
                return this;
            }

            int rowsAffected = mDatabase.update(
                    TasksTable.TABLE_NAME,
                    task.toContentValuesOnStatusChange(),
                    BuildingBlocks.SELECTION_ID,
                    new String[]{Long.toString(task.id)}
            );
            if (rowsAffected != 1) {
                mIsSuccess = false;
                mDatabase.endTransaction();
            }

            return this;
        }

        /**
         * Delete the task with given ID, or, to be precise, ensure that the task with given ID doesn't exist anymore.
         * This call won't fail even if there's nothing to delete (inspired by HTTP DELETE method behavior).
         *
         * @param taskId id of the {@link Task} to delete
         * @return this for chaining
         */
        public Transaction deleteTask(long taskId) {
            if (!mIsSuccess) {
                return this;
            }

            mDatabase.delete(
                    TasksTable.TABLE_NAME,
                    BuildingBlocks.SELECTION_ID,
                    new String[]{Long.toString(taskId)}
            );

            return this;
        }

        /**
         * Restore the deleted task back into the database. Temporary; will be replaced with create
         *
         * @param task Task to restore. Must already have an ID - this method will put it back where it previously was.
         * @return this for chaining
         */
        public Transaction restoreTask(Task task) {
            // Suppress the action if transaction is already failed
            if (!mIsSuccess) {
                return this;
            }

            long id = mDatabase.insert(
                    TasksTable.TABLE_NAME,
                    null,
                    task.toContentValuesOnRestore()
            );
            if (id != -1) {
                task.id = id;
            } else {
                mIsSuccess = false;
                mDatabase.endTransaction();
            }

            return this;
        }

    }

}
