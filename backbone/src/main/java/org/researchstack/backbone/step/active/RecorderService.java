/*
 *    Copyright 2018 Sage Bionetworks
 *
 *    Licensed under the Apache License, Version 2.0 (the "License");
 *    you may not use this file except in compliance with the License.
 *    You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *    Unless required by applicable law or agreed to in writing, software
 *    distributed under the License is distributed on an "AS IS" BASIS,
 *    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *    See the License for the specific language governing permissions and
 *    limitations under the License.
 *
 */

package org.researchstack.backbone.step.active;

import android.app.Notification;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.support.annotation.DrawableRes;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderListener;
import org.researchstack.backbone.ui.ActiveTaskActivity;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/10/18.
 */

public class RecorderService extends Service implements RecorderListener {

    private static final String LOGGING_TAG = RecorderService.class.getSimpleName();

    private static final String NOTIFICATION_CHANNEL_ID = "RecorderService_NotificationChannel";

    private static final String INTENT_KEY_NOTIFICATION_ICON_RES    = "NotificationIconRes";
    private static final String INTENT_KEY_NOTIFICATION_TITLE       = "NotificationTitle";
    private static final String INTENT_KEY_RECORDER_CONFIG_LIST     = "RecordConfigList";
    private static final String INTENT_KEY_ACTIVE_STEP              = "RecorderActiveStep";
    private static final String INTENT_KEY_OUTPUT_DIRECTORY         = "RecorderOutputDirectory";

    private static final String STATUS_EXTRA = "status";

    public static final String BROADCAST_RECORDER_ERROR                = "RecorderService_Error";
    public static final String BROADCAST_RECORDER_ERROR_MESSAGE_KEY    = "ErrorMessage";

    public static String BROADCAST_RECORDER_COMPLETE            = "RecorderService_RecordingComplete";
    public static String BROADCAST_RECORDER_COMPLETE_RESULTS    = "RecorderService_RecordingResults";

    public static FileResultList getResultList(Intent intent) {
        if (intent == null || intent.getExtras() == null ||
                !intent.getExtras().containsKey(BROADCAST_RECORDER_COMPLETE_RESULTS)) {
            return new FileResultList();
        }
        return (FileResultList) intent.getExtras()
                .getSerializable(BROADCAST_RECORDER_COMPLETE_RESULTS);
    }

    /**
     * @param appContext needed to create the intent
     * @param activeStep currently being run, must contain a valid stepDuration
     * @param recorderConfigList of the recorders to create and run
     * @param outputDirectory for the recorders
     * @return the intent to launch via "appContext.startService()"
     */
    public static Intent startService(Context appContext,
                                      ActiveStep activeStep,
                                      List<RecorderConfig> recorderConfigList,
                                      File outputDirectory) {

        Intent intent = new Intent(appContext, RecorderService.class);
        Bundle extras = new Bundle();

        RecorderConfigList configList = new RecorderConfigList();
        configList.setConfigList(recorderConfigList);
        extras.putSerializable(INTENT_KEY_RECORDER_CONFIG_LIST, configList);

        extras.putSerializable(INTENT_KEY_ACTIVE_STEP, activeStep);
        extras.putSerializable(INTENT_KEY_OUTPUT_DIRECTORY, outputDirectory);

        intent.putExtras(extras);
        return intent;
    }

    private Handler mainHandler;
    private List<Recorder> recorderList;
    private List<FileResult> resultList;

    private Notification foregroundNotification;

    @Override
    public void onCreate() {
        super.onCreate();
        Log.v(LOGGING_TAG, "onCreate");
        // no-op, wait for onStartCommand
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.v(LOGGING_TAG, "onStartCommand");

        mainHandler = new Handler();

        RecorderConfigList configList = new RecorderConfigList();
        File outputDir = null;
        ActiveStep activeStep = null;
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey(INTENT_KEY_RECORDER_CONFIG_LIST)) {
                configList = (RecorderConfigList)bundle
                        .getSerializable(INTENT_KEY_RECORDER_CONFIG_LIST);
            }
            if (bundle.containsKey(INTENT_KEY_ACTIVE_STEP)) {
                activeStep = (ActiveStep)bundle.getSerializable(INTENT_KEY_ACTIVE_STEP);
            }
            if (bundle.containsKey(INTENT_KEY_OUTPUT_DIRECTORY)) {
                outputDir = (File)bundle.getSerializable(INTENT_KEY_OUTPUT_DIRECTORY);
            }
        }

        boolean isConfigListValid = configList != null &&
                configList.getConfigList() != null &&
                !configList.getConfigList().isEmpty();

        if (activeStep != null && activeStep.getStepDuration() > 0 && isConfigListValid) {

            String notificationTitle = getString(R.string.rsb_recorder_notification_title);
            // This will ensure that this service is never destroyed
            showForegroundNotification(R.drawable.rsb_ic_recorder_notification, notificationTitle);

            // Start the recording process
            for (RecorderConfig recorderConfig : configList.getConfigList()) {
                Recorder recorder = recorderConfig.recorderForStep(activeStep, outputDir);
                // recorder can be null if it requires custom setup,
                // but that will require a custom RecorderService to handle
                if (recorder != null) {
                    recorder.setRecorderListener(this);
                    recorderList.add(recorder);
                    recorder.start(getApplicationContext());
                }
            }

            // Now allow the recorder to record for as long as the active step requires
            mainHandler.postDelayed(() -> {
                onRecorderDurationFinished();
            }, activeStep.getStepDuration() * 1000L);

        } else {
            String errorMessage = "Cannot record because ActiveStep is not valid";
            if (!isConfigListValid) {
                errorMessage = "Recorder config list is not valid";
            }
            Log.e(LOGGING_TAG, errorMessage);
            sendRecorderErrorBroadcast(errorMessage);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    public boolean isServiceRecording() {
        return recorderList != null && !recorderList.isEmpty();
    }

    /**
     * This would never be called under normal operation while the recorders are running
     * It will only be called when the user chooses to end the task and discard the results
     * or in special very rare cases like user shut their phone off, serious memory issues, etc
     * It should be treated like a recorder canceled scenario
     */
    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(LOGGING_TAG, "onDestroyed service is stopping");

        mainHandler.removeCallbacksAndMessages(null);
        sendRecorderErrorBroadcast("RecorderService destroyed while recording");
        if (isServiceRecording()) {
            for (Recorder recorder : recorderList) {
                recorder.cancel();
            }
        }
        recorderList.clear();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // no need, pass everything through LocalBroadcastManager and Intents
    }

    private void showForegroundNotification(@DrawableRes int smallIcon, String notificationMessage) {
        Intent notificationIntent = new Intent(this, ActiveTaskActivity.class);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_SINGLE_TOP);
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, 0);

        String msg = getString(R.string.rsb_recording);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setSmallIcon(smallIcon)
                        .setContentTitle(notificationMessage)
                        .setContentText(msg)
                        .setContentIntent(pendingIntent);

        foregroundNotification = notificationBuilder.build();
        startForeground(1, foregroundNotification);
    }

    private void sendRecorderErrorBroadcast(String errorMessage) {
        Intent broadcastIntent = new Intent(BROADCAST_RECORDER_ERROR);
        Bundle intentData = new Bundle();
        intentData.putString(BROADCAST_RECORDER_ERROR_MESSAGE_KEY, errorMessage);
        broadcastIntent.putExtras(intentData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void sendRecorderCompleteBroadcast() {
        Intent broadcastIntent = new Intent(BROADCAST_RECORDER_COMPLETE);
        Bundle intentData = new Bundle();
        broadcastIntent.putExtras(intentData);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void onRecorderDurationFinished() {

    }

    /**
     * RecorderListener callback
     * @param recorder        The generating recorder object.
     * @param result          The generated result.
     */
    @Override
    public void onComplete(Recorder recorder, Result result) {

    }

    /**
     * RecorderListener callback
     * @param recorder        The generating recorder object.
     * @param error           The error that occurred.
     */
    @Override
    public void onFail(Recorder recorder, Throwable error) {

    }

    /**
     * Holder class for encapsulation serializable list data sent through intents
     */
    public static class FileResultList implements Serializable {
        private List<FileResult> resultList;

        public FileResultList() {
            super();
            resultList = new ArrayList<>();
        }

        public List<FileResult> getResultList() {
            return resultList;
        }

        public void setResultList(List<FileResult> resultList) {
            this.resultList = resultList;
        }
    }

    /**
     * Holder class for encapsulation serializable list data sent through intents
     */
    protected static class RecorderConfigList implements Serializable {
        private List<RecorderConfig> configList;

        public RecorderConfigList() {
            super();
            configList = new ArrayList<>();
        }

        public List<RecorderConfig> getConfigList() {
            return configList;
        }

        public void setConfigList(List<RecorderConfig> configList) {
            this.configList = configList;
        }
    }
}
