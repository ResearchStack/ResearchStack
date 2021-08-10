package org.researchstack.backbone.ui.step.layout;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import androidx.core.content.ContextCompat;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;

import org.researchstack.backbone.R;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.AudioStep;
import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.ui.views.AudioGraphView;

/**
 * Created by TheMDP on 2/27/17.
 *
 * The AudioStepLayout class shows a graph in real-time of the user's microphone data
 * It does this by taking in data from the AudioRecorder and forwarding it to an AudioGraphView
 */

public class AudioStepLayout extends ActiveStepLayout {

    protected AudioStep audioStep;
    protected BroadcastReceiver audioUpdateReciever;

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
    public void setupActiveViews() {
        super.setupActiveViews();

        audioContentLayout = (RelativeLayout)layoutInflater
                .inflate(R.layout.rsb_step_layout_audio, activeStepLayout, false);

        audioGraphView = audioContentLayout.findViewById(R.id.rsb_step_layout_audio_graph);
        int primaryColor = ContextCompat.getColor(getContext(), R.color.rsb_colorPrimary);
        audioGraphView.setGraphColor(primaryColor);

        timerTextview = audioContentLayout.findViewById(R.id.rsb_step_layout_audio_countdown);
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
    protected void registerRecorderBroadcastReceivers(Context appContext) {
        super.registerRecorderBroadcastReceivers(appContext);
        audioUpdateReciever = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                if (AudioRecorder.BROADCAST_SAMPLE_ACTION.equals(intent.getAction())) {
                    AudioRecorder.AverageSampleHolder sampleHolder =
                            AudioRecorder.getAverageSample(intent);
                    if (sampleHolder != null) {
                        if (audioGraphView == null) {
                            // graph not ready yet
                            return;
                        }

                        audioGraphView.setMaxSampleValue(sampleHolder.getMaxVolume());
                        audioGraphView.addSample(sampleHolder.getAverageSampleVolume());
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(AudioRecorder.BROADCAST_SAMPLE_ACTION);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(audioUpdateReciever, intentFilter);
    }

    @Override
    protected void unregisterRecorderBroadcastReceivers() {
        super.unregisterRecorderBroadcastReceivers();
        Context appContext = getContext().getApplicationContext();
        LocalBroadcastManager.getInstance(appContext).unregisterReceiver(audioUpdateReciever);
    }
}
