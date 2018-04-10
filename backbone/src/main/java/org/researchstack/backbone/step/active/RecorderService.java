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

import android.Manifest;
import android.annotation.TargetApi;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.Nullable;
import android.support.annotation.RawRes;
import android.support.annotation.RequiresPermission;
import android.support.v4.app.NotificationCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.gson.Gson;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderListener;
import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.ui.ViewTaskActivity;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ResUtils;

import java.io.File;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.researchstack.backbone.ui.ViewTaskActivity.EXTRA_STEP;
import static org.researchstack.backbone.ui.ViewTaskActivity.EXTRA_TASK;
import static org.researchstack.backbone.ui.ViewTaskActivity.EXTRA_TASK_RESULT;

/**
 * Created by TheMDP on 1/10/18.
 */

public class RecorderService extends Service implements RecorderListener, TextToSpeech.OnInitListener {

    public static final String RECORDER_PREFS_KEY = "RecorderServicePrefs";
    public static final String RECORDER_PREFS_RESULTS_KEY       = "Results";
    public static final String RECORDER_PREFS_START_TIME_KEY    = "StartTime";

    public static final int DEFAULT_VIBRATION_AND_SOUND_DURATION = 500; // in milliseconds
    private static final String NOTIFICATION_CHANNEL_ID         = "RecorderService_NotificationChannel";
    private static final String NOTIFICATION_CHANNEL_TITLE         = "Study in-activity progress tracker";
    private static final String NOTIFICATION_CHANNEL_DESC         = "Records and shows your progress during an "
            + "activity.";

    public static final String INTENT_ACTION_RECORDER_RESUME    = "INTENT_ACTION_RECORDER_RESUME";

    private static final String INTENT_KEY_OUTPUT_DIRECTORY     = "RecorderOutputDirectory";

    public static String ACTION_BROADCAST_RECORDER_COMPLETE     = "RecorderService_RecordingComplete";
    public static String ACTION_BROADCAST_RECORDER_METRONOME    = "RecorderService_MetronomeBroadcast";
    public static String ACTION_BROADCAST_RECORDER_SPOKEN_TEXT  = "RecorderService_SpokenTextBroadcast";

    public static String BROADCAST_RECORDER_METRONOME_CTR       = "RecorderService_MetronomeCtr";
    public static String BROADCAST_RECORDER_SPOKEN_TEXT         = "RecorderService_SpokenText";

    // keys associated with spokenInstructions json recorder configs
    public static final String TEXT_TO_SPEECH_END_KEY = "end";
    public static final String TEXT_TO_SPEECH_COUNTDOWN_KEY = "countdown";
    public static final String TEXT_TO_SPEECH_METRONOME_KEY = "metronome";

    protected long startTime;
    protected Handler mainHandler;

    protected List<Recorder> recorderList;
    protected List<FileResult> resultList;
    protected ActiveStep activeStep;
    protected Task task;
    protected TaskResult taskResult;

    protected Notification foregroundNotification;

    protected MediaPlayer mediaPlayer;
    protected TextToSpeech tts;
    protected String textToSpeakOnInit;
    protected boolean isWaitingToComplete;
    protected boolean isServiceRunning;
    protected boolean shouldCancelRecordersOnDestroy;

    /**
     * @param appContext
     * @return the saved result list, if this active step has already completed recording
     */
    public static ResultHolder consumeSavedResultList(Context appContext, ActiveStep activeStep) {
        SharedPreferences prefs = appContext.getSharedPreferences(RECORDER_PREFS_KEY, MODE_PRIVATE);
        String key = activeStep.getRecordingUuid().toString() + RECORDER_PREFS_RESULTS_KEY;
        if (!prefs.contains(key)) {
            return null;
        }
        String resultHolderJson = prefs.getString(key, null);
        if (resultHolderJson == null) {
            return null;
        }
        ResultHolder resultHolder = new Gson().fromJson(resultHolderJson, ResultHolder.class);
        if (resultHolder == null) {
            return null;
        }
        // This was a valid recorder result that was just read, so consume it
        prefs.edit().remove(key).apply();
        return resultHolder;
    }

    /**
     * @param appContext
     * @param activeStep to use to search for a previous start time
     * @return null if the active step recording has not been started, null otherwise
     */
    public static Long getStartTime(Context appContext, ActiveStep activeStep) {
        SharedPreferences prefs = appContext.getSharedPreferences(RECORDER_PREFS_KEY, MODE_PRIVATE);
        String key = activeStep.getRecordingUuid().toString() + RECORDER_PREFS_START_TIME_KEY;
        if (!prefs.contains(key)) {
            return null;
        }
        long startTime = prefs.getLong(key, -1);
        if (startTime < 0) {
            return null;
        }
        return startTime;
    }

    protected static void setStartTime(Context appContext, ActiveStep activeStep, long startTime) {
        LogExt.d(RecorderService.class, "setStartTime() " + startTime);
        SharedPreferences prefs = appContext.getSharedPreferences(RECORDER_PREFS_KEY, MODE_PRIVATE);
        String key = activeStep.getRecordingUuid().toString() + RECORDER_PREFS_START_TIME_KEY;
        prefs.edit().putLong(key, startTime).apply();
    }

    protected static void removeStartTime(Context appContext, ActiveStep activeStep) {
        LogExt.d(RecorderService.class, "removeStartTime()");
        SharedPreferences prefs = appContext.getSharedPreferences(RECORDER_PREFS_KEY, MODE_PRIVATE);
        String key = activeStep.getRecordingUuid().toString() + RECORDER_PREFS_START_TIME_KEY;
        prefs.edit().remove(key).apply();
    }

    /**
     * @param appContext
     * @return the saved result list, if this active step has already completed recording
     */
    protected static void setSavedResultList(Context appContext,
                                             ActiveStep activeStep,
                                             ResultHolder resultHolder) {

        SharedPreferences prefs = appContext.getSharedPreferences(RECORDER_PREFS_KEY, MODE_PRIVATE);
        String key = activeStep.getRecordingUuid().toString() + RECORDER_PREFS_RESULTS_KEY;
        String resultListHolderJson = new Gson().toJson(resultHolder);
        // commit needed since this may be read immediately
        prefs.edit().putString(key, resultListHolderJson).commit();
        // Once we have stored the result, we should remove the startTime pref
        removeStartTime(appContext, activeStep);
    }

    /**
     * @param appContext needed to create the intent
     * @param activeStep currently being run, must contain a valid stepDuration
     * @param outputDirectory for the recorders
     * @param task the task for this activeStep, used in the Notification PendingIntent
     * @param taskResult the current TaskResult, used in the Notification PendingIntent
     * @return the intent to launch via "appContext.startService()"
     */
    public static void startService(Context appContext,
                                    File outputDirectory,
                                    ActiveStep activeStep,
                                    Task task,
                                    TaskResult taskResult) {

        Intent intent = new Intent(appContext, RecorderService.class);
        Bundle extras = new Bundle();

        extras.putSerializable(EXTRA_STEP, activeStep);
        extras.putSerializable(ViewTaskActivity.EXTRA_TASK, task);
        extras.putSerializable(ViewTaskActivity.EXTRA_TASK_RESULT, taskResult);
        extras.putSerializable(INTENT_KEY_OUTPUT_DIRECTORY, outputDirectory);

        intent.putExtras(extras);
        appContext.startService(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        LogExt.d(RecorderService.class, "onCreate");
        
        // no-op, wait for onStartCommand
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        LogExt.d(RecorderService.class, "onStartCommand");

        if (isServiceRunning) {
            LogExt.e(RecorderService.class, "RecorderService already running, " +
                    "ignoring duplicate start command");
            return START_NOT_STICKY;
        }

        isServiceRunning = true;
        shouldCancelRecordersOnDestroy = true;
        mainHandler = new Handler();
        resultList = new ArrayList<>();
        recorderList = new ArrayList<>();
        startTime = System.currentTimeMillis();
        isWaitingToComplete = false;
    
        // Starting with API 26, notifications must be contained in a channel
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) this.getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_TITLE,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(NOTIFICATION_CHANNEL_DESC);
                notificationManager.createNotificationChannel(channel);
            }
        }

        File outputDir = null;
        if (intent != null && intent.getExtras() != null) {
            Bundle bundle = intent.getExtras();
            if (bundle.containsKey(EXTRA_STEP)) {
                activeStep = (ActiveStep)bundle.getSerializable(EXTRA_STEP);
            }
            if (bundle.containsKey(EXTRA_TASK)) {
                task = (Task)bundle.getSerializable(EXTRA_TASK);
            }
            if (bundle.containsKey(EXTRA_TASK_RESULT)) {
                taskResult = (TaskResult)bundle.getSerializable(EXTRA_TASK_RESULT);
                if (taskResult == null && task != null) {  // it may be that there is no results yet
                    taskResult = new TaskResult(task.getIdentifier());
                }
            }
            if (bundle.containsKey(INTENT_KEY_OUTPUT_DIRECTORY)) {
                outputDir = (File)bundle.getSerializable(INTENT_KEY_OUTPUT_DIRECTORY);
            }
        }

        if (activeStep != null && activeStep.getStepDuration() > 0 &&
                task != null && taskResult != null) {

            LogExt.d(RecorderService.class, "Valid active step found, starting service");

            if (activeStep.hasVoice()) {
                tts = new TextToSpeech(this, this);
            }

            if (activeStep.getSoundRes() != null) {
                @RawRes int soundRes = ResUtils
                        .getRawResourceId(this, activeStep.getSoundRes());
                if (soundRes == 0) {
                    LogExt.e(RecorderService.class,
                            "Error finding ActiveStep's sound " + activeStep.getSoundRes());
                } else {
                    mediaPlayer = MediaPlayer.create(this, soundRes);
                    mediaPlayer.start();
                }
            }

            setStartTime(getApplicationContext(), activeStep, startTime);

            String notificationTitle = getString(R.string.rsb_recorder_notification_title);
            // This will ensure that this service is never destroyed
            showForegroundNotification(notificationTitle);

            // Start the recording process
            if (activeStep.getRecorderConfigurationList() != null) {
                for (RecorderConfig recorderConfig : activeStep.getRecorderConfigurationList()) {
                    Recorder recorder = recorderConfig.recorderForStep(activeStep, outputDir);
                    // recorder can be null if it requires custom setup,
                    // but that will require a custom RecorderService to handle
                    if (recorder != null) {
                        recorder.setRecorderListener(this);
                        recorderList.add(recorder);
                        recorder.start(getApplicationContext());
                        LogExt.d(RecorderService.class, "recorder started " + recorder.getIdentifier());
                    }
                }
            }

            // Start the delayed operations that will happen during the life-cycle of the service
            startDelayedOperations();

        } else {
            String errorMessage = "ActiveStep is null or does not have a valid step duration";
            LogExt.e(RecorderService.class, errorMessage);
            sendRecorderErrorBroadcast(errorMessage);
            stopSelf();
        }

        return START_NOT_STICKY;
    }

    private void startDelayedOperations() {
        // Now allow the recorder to record for as long as the active step requires
        mainHandler.postDelayed(this::onRecorderDurationFinished,
                activeStep.getStepDuration() * 1000L);

        startSpeechToTextMap();
    }

    protected void startSpeechToTextMap() {

        Map<String, String> speechToTextMap = activeStep.getSpokenInstructionMap();
        if (speechToTextMap != null) {
            for (String speechKey : speechToTextMap.keySet()) {

                // Check for special case "end" key that speaks after the step duration
                if (TEXT_TO_SPEECH_END_KEY.equals(speechKey)) {
                    final String endSpeechText = speechToTextMap.get(speechKey);
                    mainHandler.postDelayed(() -> speakTextAndUpdateNotification(endSpeechText),
                            activeStep.getStepDuration() * 1000L);

                    // Check for special case "countdown" key that speaks a verbal seconds countdown
                } else if (TEXT_TO_SPEECH_COUNTDOWN_KEY.equals(speechKey)) {
                    try {
                        int countdownTime = Integer.parseInt(speechToTextMap.get(speechKey));
                        for (int i = countdownTime; i > 0; i--) {
                            final String countDownStr = String.valueOf(i);
                            mainHandler.postDelayed(() -> speakText(countDownStr),
                                    (activeStep.getStepDuration() - i) * 1000L);
                        }
                    } catch (NumberFormatException e) {
                        LogExt.e(RecorderService.class, e.getLocalizedMessage());
                    }
                    // All other cases will speak the text at the seconds time of the speechKey
                } else if (TEXT_TO_SPEECH_METRONOME_KEY.equals(speechKey)) {
                    try {
                        double metronomeIntervalInSec =
                                Double.parseDouble(speechToTextMap.get(speechKey));
                        long metronomeIntervalInMs = (long)(metronomeIntervalInSec * 1000L);
                        long stopTimeInMs = activeStep.getStepDuration() * 1000L;
                        long metronomeTimeInMs = metronomeIntervalInMs;
                        final ToneGenerator tockSound =
                                new ToneGenerator(AudioManager.STREAM_MUSIC, 60);
                        int metronomeCtr = 0;
                        while (metronomeTimeInMs < stopTimeInMs) {
                            final int metronomeCounter = metronomeCtr;
                            mainHandler.postDelayed(() -> {
                                    tockSound.startTone(ToneGenerator.TONE_CDMA_PIP, 100);
                                    sendMetronomeBroadcast(metronomeCounter);
                            }, metronomeTimeInMs);
                            metronomeTimeInMs += metronomeIntervalInMs;
                            metronomeCtr++;
                        }
                    } catch (NumberFormatException e) {
                        LogExt.e(RecorderService.class, e.getLocalizedMessage());
                    }
                    // All other cases will speak the text at the seconds time of the speechKey
                } else {
                    try {
                        double triggerTime = Double.parseDouble(speechKey);
                        final String speechText = speechToTextMap.get(speechKey);
                        mainHandler.postDelayed(() -> speakTextAndUpdateNotification(speechText),
                                (long)(triggerTime * 1000L));
                    } catch (NumberFormatException e) {
                        LogExt.e(RecorderService.class, e.getLocalizedMessage());
                    }
                }
            }
        }
    }

    protected void speakTextAndUpdateNotification(String message) {
        speakText(message);
        showForegroundNotification(message);
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
        LogExt.d(RecorderService.class, "onDestroyed service is stopping");

        shutDownTts();
        shutDownMediaPlayer();
        mainHandler.removeCallbacksAndMessages(null);
        if (isServiceRunning && shouldCancelRecordersOnDestroy) {
            LogExt.d(RecorderService.class, "cancelling all recorders");
            removeStartTime(getApplicationContext(), activeStep);
            for (Recorder recorder : recorderList) {
                recorder.cancel();
            }
        }
        isServiceRunning = false;
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;  // no need, pass everything through LocalBroadcastManager and Intents
    }

    private void showForegroundNotification(String notificationMessage) {

        // Starting with API 26, notifications must be contained in a channel
        if (Build.VERSION.SDK_INT >= 26) {
            NotificationManager notificationManager =
                    (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
            if (notificationManager != null) {
                NotificationChannel channel = new NotificationChannel(
                        NOTIFICATION_CHANNEL_ID,
                        NOTIFICATION_CHANNEL_TITLE,
                        NotificationManager.IMPORTANCE_DEFAULT);
                channel.setDescription(NOTIFICATION_CHANNEL_DESC);
                notificationManager.createNotificationChannel(channel);
            }
        }

        LogExt.d(RecorderService.class, "showForegroundNotification(" + notificationMessage + ")");
        Intent notificationIntent = new Intent(this, activeStep.getActivityClazz());

        // These will guarantee the activity is re-created at the same step as we were running
        notificationIntent.putExtra(ViewTaskActivity.EXTRA_TASK, task);
        notificationIntent.putExtra(ViewTaskActivity.EXTRA_TASK_RESULT, taskResult);
        notificationIntent.putExtra(ViewTaskActivity.EXTRA_STEP, activeStep);

        notificationIntent.setAction(INTENT_ACTION_RECORDER_RESUME);
        notificationIntent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK);
        PendingIntent pendingIntent = PendingIntent.getActivity(
                this, 0, notificationIntent, 0);

        String msg = getString(R.string.rsb_recording);

        NotificationCompat.Builder notificationBuilder =
                new NotificationCompat.Builder(this, NOTIFICATION_CHANNEL_ID)
                        .setContentTitle(task.getIdentifier() + " " + msg)
                        .setContentText(notificationMessage)
                        .setContentIntent(pendingIntent);

        // vector drawable crash
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            notificationBuilder.setSmallIcon(R.drawable.rsb_ic_recorder_notification);
        }
        foregroundNotification = notificationBuilder.build();
        startForeground(1, foregroundNotification);
    }

    private void sendRecorderErrorBroadcast(String errorMessage) {
        LogExt.d(RecorderService.class, "sendRecorderErrorBroadcast()");
        Intent broadcastIntent = new Intent(ACTION_BROADCAST_RECORDER_COMPLETE);
        ResultHolder resultHolder = new ResultHolder();
        resultHolder.setErrorMessage(errorMessage);
        resultHolder.setStartTime(startTime);
        setSavedResultList(getApplicationContext(), activeStep, resultHolder);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    private void sendRecorderCompleteBroadcast() {
        LogExt.d(RecorderService.class, "sendRecorderCompleteBroadcast()");
        Intent broadcastIntent = new Intent(ACTION_BROADCAST_RECORDER_COMPLETE);
        ResultHolder resultHolder = new ResultHolder();
        resultHolder.setResultList(resultList);
        resultHolder.setStartTime(startTime);
        setSavedResultList(getApplicationContext(), activeStep, resultHolder);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    protected void sendMetronomeBroadcast(int ctr) {
        Intent broadcastIntent = new Intent(ACTION_BROADCAST_RECORDER_METRONOME);
        broadcastIntent.putExtra(BROADCAST_RECORDER_METRONOME_CTR, ctr);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    protected void sendSpokenTextBroadcast(String spokenText) {
        Intent broadcastIntent = new Intent(ACTION_BROADCAST_RECORDER_SPOKEN_TEXT);
        broadcastIntent.putExtra(BROADCAST_RECORDER_SPOKEN_TEXT, spokenText);
        LocalBroadcastManager.getInstance(this).sendBroadcast(broadcastIntent);
    }

    @RequiresPermission(value = Manifest.permission.VIBRATE, conditional = true)
    private void onRecorderDurationFinished() {
        LogExt.d(RecorderService.class, "onRecorderDurationFinished()");
        isWaitingToComplete = true;

        if (activeStep.getShouldVibrateOnFinish()) {
            vibrate();
        }

        if (activeStep.getShouldPlaySoundOnFinish()) {
            playSound();
        }

        if (activeStep.getFinishedSpokenInstruction() != null) {
            speakText(activeStep.getFinishedSpokenInstruction());
        }

        // copy list to avoid any concurrent modifications with calling stop on each recorder
        List<Recorder> copiedList = new ArrayList<>(recorderList);
        for (Recorder recorder : copiedList) {
            recorder.stop();  // this will trigger a call to onComplete below
            LogExt.d(RecorderService.class, "recorder stopped " + recorder.getIdentifier());
        }
        // The continueOnFinishDelay allows the TTS to flush,
        // and for the recorders to finish up before leaving the screen
        mainHandler.postDelayed(() -> {
            isWaitingToComplete = false;
            if (!recorderList.isEmpty()) {
                // continue to wait for recorders to finish before sending the complete broadcast
            } else {
                sendCompleteBroadcastAndFinish();
            }
        }, activeStep.getEstimateTimeInMsToSpeakEndInstruction());
    }

    /**
     * RecorderListener callback
     * @param recorder        The generating recorder object.
     * @param result          The generated result.
     */
    @Override
    public void onComplete(Recorder recorder, Result result) {
        if (!(result instanceof FileResult)) {
            // Due to the RecorderService having to store results in a SharedPreferences file
            // We do not allow any Results other than FileResults
            throw new IllegalStateException("RecorderService only works " +
                    "with Recorders that return FileResults");
        }
        FileResult fileResult = (FileResult)result;
        LogExt.d(RecorderService.class, "recorder onComplete() " + result.getIdentifier());
        recorderList.remove(recorder);
        resultList.add(fileResult);
        if (!isWaitingToComplete && recorderList.isEmpty()) {
            sendCompleteBroadcastAndFinish();
        }
    }

    protected void sendCompleteBroadcastAndFinish() {
        shouldCancelRecordersOnDestroy = false;
        sendRecorderCompleteBroadcast();
        stopSelf();
    }

    /**
     * RecorderListener callback
     * @param recorder        The generating recorder object.
     * @param error           The error that occurred.
     */
    @Override
    public void onFail(Recorder recorder, Throwable error) {
        shutDownTts();
        shutDownMediaPlayer();
        sendRecorderErrorBroadcast(error.getLocalizedMessage());
        stopSelf();
    }

    protected void shutDownTts() {
        if (tts != null) {
            if (tts.isSpeaking()) {
                tts.stop();
            }
            tts.shutdown();
            tts = null;
        }
    }

    protected void shutDownMediaPlayer() {
        if (mediaPlayer != null) {
            mediaPlayer.release();
            mediaPlayer = null;
        }
    }

    @Nullable
    @Override
    public Context getBroadcastContext() {
        return this;
    }

    // TextToSpeech initialization
    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int languageAvailable = tts.isLanguageAvailable(Locale.getDefault());
            // >= 0 means LANG_AVAILABLE, LANG_COUNTRY_AVAILABLE, or LANG_COUNTRY_VAR_AVAILABLE
            if (languageAvailable >= 0) {
                tts.setLanguage(Locale.getDefault());
                if (textToSpeakOnInit != null) {
                    speakText(textToSpeakOnInit);
                }
            } else {
                tts = null;
            }
        } else {
            Log.e(getClass().getCanonicalName(), "Failed to initialize TTS with error code " + i);
            tts = null;
        }
    }

    protected void speakText(String text) {
        // Setting this will guarantee the text gets spoken in the case that tts isn't set up yet
        textToSpeakOnInit = text;
        if (tts == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
        sendSpokenTextBroadcast(text);
    }

    @SuppressWarnings("deprecation")
    private void ttsUnder20(String text) {
        HashMap<String, String> map = new HashMap<>();
        map.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID, "MessageId");
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, map);
    }

    @TargetApi(Build.VERSION_CODES.LOLLIPOP)
    private void ttsGreater21(String text) {
        String utteranceId = String.valueOf(hashCode());
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
    private void vibrate() {
        Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(DEFAULT_VIBRATION_AND_SOUND_DURATION);
    }

    protected void playSound() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50); // 50 = half volume
        // Play a low and high tone for 500 ms at full volume
        toneG.startTone(ToneGenerator.TONE_CDMA_LOW_L, DEFAULT_VIBRATION_AND_SOUND_DURATION);
        toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_L, DEFAULT_VIBRATION_AND_SOUND_DURATION);
    }

    /**
     * Holder class for encapsulation serializable list data sent through intents
     */
    public static class ResultHolder implements Serializable {
        private long startTime;
        private String errorMessage;
        private List<FileResult> resultList;

        public ResultHolder() {
            super();
            resultList = new ArrayList<>();
        }

        public List<FileResult> getResultList() {
            return resultList;
        }

        public void setResultList(List<FileResult> resultList) {
            this.resultList = resultList;
        }

        public String getErrorMessage() {
            return errorMessage;
        }

        public void setErrorMessage(String errorMessage) {
            this.errorMessage = errorMessage;
        }

        public long getStartTime() {
            return startTime;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }
    }
}
