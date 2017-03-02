package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.support.v4.content.ContextCompat;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.AudioStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.ui.views.AudioGraphView;

/**
 * Created by TheMDP on 2/27/17.
 *
 * The AudioStepLayout class shows a graph in real-time of the user's microphone data
 * It does this by taking in data from the AudioRecorder and forwarding it to an AudioGraphView
 */

public class AudioStepLayout extends ActiveStepLayout implements AudioRecorder.AudioRecorderListener {

    private static final long DURATION_BETWEEN_GRAPH_UPDATES = 180;
    private long durationBetweenGraphUpdates = DURATION_BETWEEN_GRAPH_UPDATES;

    protected AudioStep audioStep;

    protected RelativeLayout audioContentLayout;
    protected AudioGraphView audioGraphView;

    public AudioStepLayout(Context context) {
        super(context);
    }

    public AudioStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AudioStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    public AudioStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    protected void start() {
        super.start();

        if (recorderList != null) {
            for (Recorder recorder : recorderList) {
                if (recorder instanceof AudioRecorder) {
                    ((AudioRecorder)recorder).setAudioRecorderListener(this, durationBetweenGraphUpdates);
                }
            }
        }
    }

    @Override
    protected void setupActiveViews() {
        super.setupActiveViews();

        audioContentLayout = (RelativeLayout)layoutInflater.inflate(R.layout.rsb_step_layout_audio, activeStepLayout, false);

        audioGraphView = (AudioGraphView) audioContentLayout.findViewById(R.id.rsb_step_layout_audio_graph);
        int primaryColor = ContextCompat.getColor(getContext(), R.color.rsb_colorPrimary);
        audioGraphView.setGraphColor(primaryColor);

        timerTextview = (TextView) audioContentLayout.findViewById(R.id.rsb_step_layout_audio_countdown);
        timerTextview.setTextColor(primaryColor);

        activeStepLayout.addView(audioContentLayout, new LinearLayout.LayoutParams(
                ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    @Override
    protected void validateStep(Step step) {
        super.validateStep(step);

        if (!(step instanceof AudioStep)) {
            throw new IllegalStateException("AudioStepLayout must have an AudioStep");
        }
        audioStep = (AudioStep)step;
    }

    @Override
    public void onAudioSampleRecorded(int averageSampleVolume, int maxVolume) {
        if (audioGraphView == null) {
            // graph not ready yet
            return;
        }

        audioGraphView.setMaxSampleValue(maxVolume);
        audioGraphView.addSample(averageSampleVolume);
    }

    public long getDurationBetweenGraphUpdates() {
        return durationBetweenGraphUpdates;
    }

    public void setDurationBetweenGraphUpdates(long durationBetweenGraphUpdates) {
        this.durationBetweenGraphUpdates = durationBetweenGraphUpdates;
    }
}
