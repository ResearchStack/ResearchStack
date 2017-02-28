package org.researchstack.backbone.step.active.recorder;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.MediaRecorder;

import java.io.Serializable;

/**
 * Created by TheMDP on 2/26/17.
 *
 * This class encapsulates the different audio recording settings that can be used
 * with the AudioRecorder class
 */

public class AudioRecorderSettings implements Serializable {

    /**
     * The default sample rates ordered in priority from highest sample rates to lowest
     */
    public static final int[] DEFAULT_SAMPLE_RATES = new int[] { 44100, 22050, 16000, 11025, 8000 };
    /**
     * On Android, not all sample rates are available for all devices,
     * so it is customary to provide a range of sample rates to choose from
     */
    private int[] possibleSampleRate;

    public static final int AUDIO_CHANNELS_MONO   = 1;
    public static final int AUDIO_CHANNELS_STEREO = 2;
    /**
     * 1 for MONO, 2 for STEREO
     */
    private int audioChannels;

    /**
     * Can be any value in MediaRecorder.OutputFormat
     */
    private int outputFormat;

    /**
     * Can be any value in MediaRecorder.AudioSource
     */
    private int audioSource;

    /**
     * Can be any value in MediaRecorder.AudioEncoder
     */
    private int audioEncoder;

    /** Default constructor used for serialization/deserialization */
    AudioRecorderSettings() {
        super();
    }

    /**
     * Create the settings that the audio recorder will use while recording audio
     *
     * @param audioSource         The audio source value in MediaRecorder.AudioSource
     * @param audioEncoder        The audio source value in MediaRecorder.AudioEncoder
     * @param outputFormat        The audio source value in MediaRecorder.OutputFormat
     * @param audioChannels       The number of audio channels, use
     *                            AUDIO_CHANNELS_MONO or AUDIO_CHANNELS_STEREO
     * @param possibleSampleRates An array of possible sample rates that you desire, ordered by priority
     *                            See DEFAULT_SAMPLE_RATES as an example
     *                            On Android, not all sample rates are available for all devices,
     *                            so it is customary to provide a range of sample rates to choose from
     */
    public AudioRecorderSettings(
        int audioSource,
        int audioEncoder,
        int outputFormat,
        int audioChannels,
        int[] possibleSampleRates)
    {
        this.audioSource    = audioSource;
        this.audioEncoder   = audioEncoder;
        this.outputFormat   = outputFormat;
        this.audioChannels  = audioChannels;
        this.possibleSampleRate = possibleSampleRates;
    }

    /**
     * @return  default settings for recording audio, which is...
     *          MediaRecorder.AudioSource.MIC
     *          MediaRecorder.AudioEncoder.AAC
     *          MediaRecorder.OutputFormat.MPEG_4
     *          AUDIO_CHANNELS_STEREO
     *          DEFAULT_SAMPLE_RATES - first priority 44.1k, 22k, 16k, 11k, 8k
     */
    public static AudioRecorderSettings defaultSettings() {
        return new AudioRecorderSettings(
                MediaRecorder.AudioSource.MIC,
                MediaRecorder.AudioEncoder.AAC,
                MediaRecorder.OutputFormat.MPEG_4,
                AUDIO_CHANNELS_STEREO,
                DEFAULT_SAMPLE_RATES);
    }

    private static int getDesiredSampleRate(int[] sampleRates) {
        // add the rates you wish to check against, sticking with the main one
        for (int rate : sampleRates) {
            int bufferSize = AudioRecord.getMinBufferSize(rate,
                    AudioFormat.CHANNEL_IN_DEFAULT, AudioFormat.ENCODING_PCM_16BIT);
            if (bufferSize > 0) {
                // buffer size is valid, Sample rate supported
                return rate;
            }
        }
        return sampleRates[sampleRates.length-1];
    }

    public int getAudioChannels() {
        return audioChannels;
    }

    public void setAudioChannels(int audioChannels) {
        this.audioChannels = audioChannels;
    }

    public int getOutputFormat() {
        return outputFormat;
    }

    public void setOutputFormat(int outputFormat) {
        this.outputFormat = outputFormat;
    }

    public int getAudioSource() {
        return audioSource;
    }

    public void setAudioSource(int audioSource) {
        this.audioSource = audioSource;
    }

    public int getAudioEncoder() {
        return audioEncoder;
    }

    public void setAudioEncoder(int audioEncoder) {
        this.audioEncoder = audioEncoder;
    }

    public int getSampleRate() {
        return getDesiredSampleRate(possibleSampleRate);
    }

    public void setSampleRate(int[] possibleSampleRates) {
        this.possibleSampleRate = possibleSampleRates;
    }
}
