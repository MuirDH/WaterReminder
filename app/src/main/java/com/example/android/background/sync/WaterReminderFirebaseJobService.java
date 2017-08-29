/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.android.background.sync;

import android.content.Context;
import android.os.AsyncTask;

import com.firebase.jobdispatcher.Job;
import com.firebase.jobdispatcher.JobParameters;
import com.firebase.jobdispatcher.JobService;
import com.firebase.jobdispatcher.RetryStrategy;

// WaterReminderFirebaseJobService should extend from JobService
public class WaterReminderFirebaseJobService extends JobService{

    private AsyncTask mBackgroundTask;

    // Override onStartJob
    @Override
    public boolean onStartJob(final JobParameters job) {

        // By default, jobs are executed on the main thread, so make an anonymous class extending
        //  AsyncTask called mBackgroundTask. Here we make an AsyncTask so that this is no longer on
        // the main thread.
        mBackgroundTask = new AsyncTask() {

            // Override doInBackground
            @Override
            protected Object doInBackground(Object[] objects) {

                // Use ReminderTasks to execute the new charging reminder task you made, use
                // this service as the context (WaterReminderFirebaseJobService.this) and return null
                // when finished.
                Context context = WaterReminderFirebaseJobService.this;
                ReminderTasks.executeTask(context, ReminderTasks.ACTION_CHARGING_REMINDER);
                return null;

            }

            // Override onPostExecute and called jobFinished. Pass the job parameters
            // and false to jobFinished. This will inform the JobManager that your job is done
            // and that you do not want to reschedule the job.

            @Override
            protected void onPostExecute(Object o) {

                /*
                 * Once the AsyncTask is finished, the job is finished. To inform JobManager that
                 * you're done, you call jobFinished with the JobParameters that were passed to your
                 * job and a boolean representing whether the job needs to be rescheduled. This is
                 * usually if something didn't work and you want the job to try running again.
                 */
                jobFinished(job, false);

            }

        };

        // Execute the AsyncTask
        mBackgroundTask.execute();
        // Return true
        return true;

    }

    // Override onStopJob

    /**
     * Called when the scheduling engine has decide dto interrupt the execution of a running job,
     * most likely because the runtime constraints associated with the job are no longer satisfied.
     * @see Job.Builder#setRetryStrategy(RetryStrategy)
     * @see RetryStrategy
     * @return whether the job should be retried
     */
    @Override
    public boolean onStopJob(JobParameters job) {

        // If mBackgroundTask is valid, cancel it
        if (mBackgroundTask != null) mBackgroundTask.cancel(true);
        // Return true to signify the job should be retried
        return true;

    }

}
