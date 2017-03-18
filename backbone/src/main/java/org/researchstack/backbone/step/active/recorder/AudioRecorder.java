package org.researchstack.backbone.step.active.recorder;

import android.Manifest;
import android.content.Context;
import android.content.pm.PackageManager;
import android.media.MediaRecorder;
import android.os.Build;
import android.os.Handler;
import android.webkit.MimeTypeMap;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.AudioResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.utils.LogExt;

import java.io.File;
import java.io.IOException;
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

    private AudioRecorderListener audioRecorderListener;

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

                    if (audioRecorderListener != null && msBetweenCallbacks > 0) {
                        long now = System.currentTimeMillis();
                        if ((now - timeOfLastCallback) > msBetweenCallbacks) {
                            int averageSample = (int) (sampleSumSinceLastCallback / samplesSinceLastCallback);
                            audioRecorderListener.onAudioSampleRecorded(averageSample, MAX_VOLUME);
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
        AudioResult audioResult = new AudioResult(fileResultIdentifier(), file, mimeType);
        audioResult.setStartDate(new Date(startTime));
        audioResult.setEndDate(new Date());

        double totalAvgIntensityFrom0To1 = ((double)totalRollingAvg / (double)totalRollingAvgSampleCount) / (double)MAX_VOLUME;
        audioResult.setRollingAverageOfVolume(totalAvgIntensityFrom0To1);

        // Return the result to the recorder listener
        onRecorderCompleted(audioResult);
    }

    @Override
    public void cancel() {
        stopSampleMonitoring();

        if (mediaRecorder == null) {
            throw new IllegalStateException("Cannot cancel media recorder since it has not been started");
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
     * @param audioRecorderListener the listener to recieve onAudioSampleRecorded calls
     * @param timeBetweenListenerCalls Duration cannot currently be less than 100 milliseconds
     *                                 the duration in milliseconds that will be in between calls to onAudioSampleRecorded
     */
    public void setAudioRecorderListener(
            AudioRecorderListener audioRecorderListener,
            long timeBetweenListenerCalls)
    {
        this.audioRecorderListener = audioRecorderListener;
        this.msBetweenCallbacks = timeBetweenListenerCalls;
    }

    public interface AudioRecorderListener {
        /**
         * @param averageSampleVolume the current sample's volume
         * @param maxVolume the max volume that the sampleVolume can be
         */
        void onAudioSampleRecorded(int averageSampleVolume, int maxVolume);
    }
}
