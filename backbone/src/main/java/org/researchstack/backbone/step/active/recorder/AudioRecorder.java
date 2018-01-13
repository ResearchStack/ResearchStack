package org.researchstack.backbone.step.active.recorder;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.content.LocalBroadcastManager;
import android.webkit.MimeTypeMap;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.AudioResult;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.Date;

/**
 * Created by TheMDP on 2/26/17.
 *
 * The AudioRecorder records audio in a format specified by the AudioRecorderSettings
 * It bundles the result in an AudioResult object in which it returns in the recorder listener
 */

public class AudioRecorder extends Recorder {

    private static final double VOLUME_CLAMP_IN_DECIBELS = 60.0;
    public static final int MAX_VOLUME = 32767;

    /**
     * This is the duration in between checking for the max sample amplitude of the audio recorder
     * As of now, 100 ms is sufficient to check if the audio is "too loud"
     */
    private static final int AVERAGE_MAX_VOLUME_DURATION = 100;

    public static final String BROADCAST_SAMPLE_ACTION  = "AudioRecorder_BroadcastSample";
    private static final String BROADCAST_SAMPLE_KEY    = "BroadcastSample";

    /**
     * Used to check amplitude of the sample at a desired frequency
     */
    private Handler  mainHandler;
    private Runnable sampleMonitorRunnable;

    /**
     * The AudioRecorder can give average sample callbacks at any desired frequency
     * These variables keep track of the running average per callback window
     */
    private long sampleSumSinceLastCallback;
    private int samplesSinceLastCallback;
    private long timeOfLastCallback;
    private long msBetweenCallbacks;

    /**
     * These keep track of the entire rolling average for the entire life-cycle of AudioRecorder
     * It can be used to get an overall picture of the audio's background noise
     */
    private long totalRollingAvg;
    private int totalRollingAvgSampleCount;

    private AudioRecorderSettings settings;
    private MediaRecorder mediaRecorder;
    private long startTime;

    private static double sLastSampleAvg;
    /**
     * @return The last audio sample recorder, the total average volume from 0 - 1
     *         The is most useful for determining if an area is too loud or noisy
     */
    public static double getLastTotalSampleAvg() {
       return sLastSampleAvg;
    }
    public static void setLastTotalSampleAvg(double totalAvg) {
        sLastSampleAvg = totalAvg;
    }

    AudioRecorder(AudioRecorderSettings settings,
                  String identifier,
                  Step step,
                  File outputDirectory)
    {
        super(identifier, step, outputDirectory);
        this.settings = settings;
        mainHandler = new Handler();
    }

    @Override
    public void start(Context context) {
        if (mediaRecorder != null) {
            throw new IllegalStateException("Cannot start media recorder since it has already been started");
        }

        if (settings == null) {
            throw new IllegalStateException("Cannot start media recorder since settings is null");
        }

        // In-app permissions were added in Android 6.0
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            PackageManager pm = context.getPackageManager();
            int hasPerm = pm.checkPermission(Manifest.permission.RECORD_AUDIO, context.getPackageName());
            if (hasPerm != PackageManager.PERMISSION_GRANTED) {
                if (getRecorderListener() != null) {
                    String errorMsg = context.getString(R.string.rsb_permission_microphone_desc);
                    getRecorderListener().onFail(this, new IllegalStateException(errorMsg));
                }
                return;
            }
        }

        mediaRecorder = new MediaRecorder();

        mediaRecorder.setAudioSource(settings.getAudioSource());
        mediaRecorder.setOutputFormat(settings.getOutputFormat());
        mediaRecorder.setAudioEncoder(settings.getAudioEncoder());
        mediaRecorder.setAudioEncodingBitRate(settings.getSampleRate());
        mediaRecorder.setAudioChannels(settings.getAudioChannels());
        mediaRecorder.setAudioSamplingRate(settings.getSampleRate());
        mediaRecorder.setOutputFile(fullFilePath());

        try {
            mediaRecorder.prepare();
        } catch (IOException e) {
            LogExt.e(getClass(), e.getMessage());
            onRecorderFailed(e);
            mediaRecorder = null;
            return;
        }

        // Audio is recording
        mediaRecorder.start();

        // MediaRecorder doesn't support callbacks each time a sample is recorded,
        // However, it does support querying for the most recent maximum amplitude
        // So we will use a re-curring check to do a running average of the samples
        startSampleMonitoring();

        startTime = System.currentTimeMillis();

        // Note: if the developer needs higher level audio analysis, they must either read
        // the audio file afterwords, or use another method of audio recording
    }

    private String fullFilePath() {
        return getOutputDirectory() + File.separator + uniqueFilename
                + getFileExtensionForOutputFormat(settings.getOutputFormat());
    }

    private void refreshCallbackVariables() {
        sampleSumSinceLastCallback = 0;
        samplesSinceLastCallback = 0;
        timeOfLastCallback = System.currentTimeMillis();
    }

    private void startSampleMonitoring() {
        totalRollingAvg = 0;
        totalRollingAvgSampleCount = 0;

        refreshCallbackVariables();

        sampleMonitorRunnable = new Runnable() {
            @Override
            public void run() {
                if (mediaRecorder != null) {
                    // Every time you query getMaxAmplitude, it gets reset,
                    // so that the next time will be relative to the last time you called the method
                    int currentIntensity = mediaRecorder.getMaxAmplitude();

                    // Compute the new rolling average
                    addSampleToRollingAverages(currentIntensity);

                    if (msBetweenCallbacks > 0) {
                        long now = System.currentTimeMillis();
                        if ((now - timeOfLastCallback) > msBetweenCallbacks) {
                            int averageSample = (int) (sampleSumSinceLastCallback / samplesSinceLastCallback);
                            sendAverageSampleBroadcast(averageSample);
                            refreshCallbackVariables();
                        }
                    }
                }
                mainHandler.postDelayed(sampleMonitorRunnable, AVERAGE_MAX_VOLUME_DURATION);
            }
        };
        // Smallest possible delay to get the most information about the audio recording
        mainHandler.postDelayed(sampleMonitorRunnable, AVERAGE_MAX_VOLUME_DURATION);
    }

    private void sendAverageSampleBroadcast(int averageSample) {
        AverageSampleHolder sampleHolder = new AverageSampleHolder();
        sampleHolder.averageSampleVolume = averageSample;
        sampleHolder.maxVolume = MAX_VOLUME;
        Bundle bundle = new Bundle();
        bundle.putSerializable(BROADCAST_SAMPLE_KEY, sampleHolder);
        Intent intent = new Intent(BROADCAST_SAMPLE_ACTION);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    /**
     * @param intent must have action of BROADCAST_SAMPLE_ACTION
     * @return the AverageSampleHolder contained in the broadcast
     */
    public static AverageSampleHolder getAverageSample(Intent intent) {
        if (intent.getAction() == null ||
                !intent.getAction().equals(BROADCAST_SAMPLE_ACTION) ||
                intent.getExtras() == null ||
                !intent.getExtras().containsKey(BROADCAST_SAMPLE_KEY)) {
            return null;
        }
        return (AverageSampleHolder) intent.getExtras().getSerializable(BROADCAST_SAMPLE_KEY);
    }

    private void stopSampleMonitoring() {
        mainHandler.removeCallbacksAndMessages(null);
    }

    private void addSampleToRollingAverages(int sampleIntensity) {
        LogExt.i(getClass(), "Sample intensity " + sampleIntensity);

        // Add to the running average for this period
        sampleSumSinceLastCallback += sampleIntensity;
        samplesSinceLastCallback++;

        totalRollingAvg += sampleIntensity;
        totalRollingAvgSampleCount++;

        // iOS does this
        // Convert to decibels and add it to the full normalized running average value
//        double db = 20 * Math.log10(Math.abs(sampleIntensity) / MAX_VOLUME);
//        double clampedValue = Math.max(db / VOLUME_CLAMP_IN_DECIBELS, -1) + 1;
//        totalRollingAvgSampleCount++;
//        totalRollingAvg = (totalRollingAvg * (totalRollingAvgSampleCount - 1) + clampedValue) / totalRollingAvgSampleCount;
    }

    @Override
    public void stop() {
        stopSampleMonitoring();

        if (mediaRecorder == null) {
            throw new IllegalStateException("Cannot stop media recorder since it has not been started");
        }

        stopAndReleaseMediaRecorder();

        // Build the file result and fill it with the collected data
        String filepath = fullFilePath();
        File file = new File(filepath);
        String mimeType = getMimeType(filepath);
        FileResult fileResult = new FileResult(fileResultIdentifier(), file, mimeType);
        fileResult.setStartDate(new Date(startTime));
        fileResult.setEndDate(new Date());

        double totalAvgIntensityFrom0To1 = ((double)totalRollingAvg /
                (double)totalRollingAvgSampleCount) / (double)MAX_VOLUME;
        setLastTotalSampleAvg(totalAvgIntensityFrom0To1);

        // Return the result to the recorder listener
        onRecorderCompleted(fileResult);
    }

    @Override
    public void cancel() {
        stopSampleMonitoring();

        if (mediaRecorder == null) {
            return; // no reason to cancel anything, since the mediaRecorder never started
        }

        stopAndReleaseMediaRecorder();

        // Delete the file that was created
        String filepath = getOutputDirectory() + File.separator + uniqueFilename;
        File file = new File(filepath);
        boolean deletedSuccessfully = file.delete();
        if (!deletedSuccessfully) {
            LogExt.e(getClass(), "File was not deleted when recorder was cancelled " + file.toString());
        }
    }

    private void stopAndReleaseMediaRecorder() {
        // Stop the media recorder and release all the resources it was using
        mediaRecorder.stop();
        mediaRecorder.reset();
        mediaRecorder.release();
        mediaRecorder = null;
    }

    private static String getMimeType(String filePath) {
        String type = null;
        String extension = MimeTypeMap.getFileExtensionFromUrl(filePath);
        if (extension != null) {
            type = MimeTypeMap.getSingleton().getMimeTypeFromExtension(extension);
        }
        return type;
    }

    private String getFileExtensionForOutputFormat(int outputFormat) {
        switch (outputFormat) {
            case MediaRecorder.OutputFormat.MPEG_4:
                return ".m4a";
            default:
                return "";  // needs implemented for other file types
        }
    }

    /**
     * @param timeBetweenListenerCalls Duration cannot currently be less than 100 milliseconds
     *                                 the duration in milliseconds that will be in between calls to onAudioSampleRecorded
     */
    public void setAudioRecorderBroadcastInterval(long timeBetweenListenerCalls)
    {
        this.msBetweenCallbacks = timeBetweenListenerCalls;
    }

    public static class AverageSampleHolder implements Serializable {
        private int averageSampleVolume;
        private int maxVolume;

        public AverageSampleHolder() {
            super();
        }

        public int getAverageSampleVolume() {
            return averageSampleVolume;
        }

        public void setAverageSampleVolume(int averageSampleVolume) {
            this.averageSampleVolume = averageSampleVolume;
        }

        public int getMaxVolume() {
            return maxVolume;
        }

        public void setMaxVolume(int maxVolume) {
            this.maxVolume = maxVolume;
        }
    }
}
