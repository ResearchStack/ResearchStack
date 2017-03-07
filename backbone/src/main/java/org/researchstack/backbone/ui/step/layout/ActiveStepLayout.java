package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.content.Context;
import android.content.DialogInterface;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.recorder.Recorder;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.step.active.recorder.RecorderListener;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.utils.ResUtils;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import rx.functions.Action1;

/**
 * Created by TheMDP on 2/4/17.
 *
 * /**
 * The `ActiveStepLayout` class is the base class for displaying `ActiveStep`
 * subclasses. The predefined active tasks defined in `TremorTaskFactory` all make use
 * of subclasses of `ActiveStep`, paired with `ActiveStepLayout` subclasses.
 *
 * Active steps generally include some form of sensor-driven data collection,
 * or involve some highly interactive content, such as a cognitive task or game.
 *
 * Examples of active step layout subclasses include `WalkingTaskStepLayout`,
 * `CountdownStepLayout`, `SpatialSpanMemoryLayout`, `FitnessStepLayout`, and `AudioStepLayout`.
 *
 * The primary feature that active step layouts enable is recorder life cycle.
 * After an active step is presented, it can be started to start a timer. When the timer expires, the
 * step is  considered finished. Some steps may have the concept of suspend and resume, such as when
 * the app is put in the background, and during which data recording is temporarily paused.
 * These life cycle methods generally apply to any recorders being used to record
 * data from the device's sensors, but they should also be applied to any UI
 * being displayed to clearly indicate when data is being collected
 * for the task.
 *
 * When you develop a new active step, you should subclass `ActiveStepLayout`
 * and define your specific UI. When subclassing, pay special attention to the life cycle
 * methods, `start`, `finish`, `suspend`, and `resume`. Also, be sure to test for
 * the expected behavior when the user suspends and resumes the app, during task
 * save and restore, and during UIKit's UI state restoration.
 */

public class ActiveStepLayout extends FixedSubmitBarLayout
        implements StepLayout, RecorderListener, TextToSpeech.OnInitListener {

    private static final int DEFAULT_VIBRATION_AND_SOUND_DURATION = 500; // in milliseconds

    /**
     * When this is true, files will be saved externally so you can read them
     * since reading internal files requires root access.
     * You must add WRITE_EXTERNAL_STORAGE permission to manifest as well
     */
    private static final boolean DEBUG_SAVE_FILES_EXTERNALLY = false;

    private TextToSpeech tts;

    protected StepCallbacks callbacks;

    protected List<Recorder> recorderList;

    protected StepResult<Result> stepResult;

    protected Handler  mainHandler;
    protected Runnable animationRunnable;
    protected long startTime;
    protected int  secondsLeft;

    protected ActiveStep activeStep;

    protected LinearLayout activeStepLayout;
    protected TextView titleTextview;
    protected TextView textTextview;
    protected TextView timerTextview;
    protected ProgressBar progressBar;
    protected ProgressBar progressBarHorizontal;
    protected ImageView imageView;

    public ActiveStepLayout(Context context) {
        super(context);
    }

    public ActiveStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ActiveStepLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    @TargetApi(21)
    public ActiveStepLayout(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
    }

    @Override
    public int getContentResourceId() {
        return R.layout.rsb_step_layout_active_step;
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateStep(step);

        mainHandler = new Handler();
        setupActiveViews();
        setupSubmitBar();

        // We don't allow this activeStep to have it's state saved
        stepResult = new StepResult<>(step);

        if (activeStep.hasVoice()) {
            tts = new TextToSpeech(getContext(), this);
        }

        if (activeStep.getShouldStartTimerAutomatically()) {
            start();
        }
    }

    protected void setupSubmitBar() {
        if (activeStep.isOptional()) {
            submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
            submitBar.setNegativeAction(new Action1() {
                @Override
                public void call(Object o) {
                    skip();
                }
            });
        } else {
            submitBar.getNegativeActionView().setVisibility(View.GONE);
        }

        if (activeStep.getShouldStartTimerAutomatically()) {
            submitBar.setPositiveActionViewEnabled(false);
        } else {
            submitBar.setPositiveTitle(R.string.rsb_BUTTON_GET_STARTED);
            submitBar.setPositiveAction(new Action1() {
                @Override
                public void call(Object o) {
                    submitBar.setPositiveTitle(R.string.rsb_BUTTON_NEXT);
                    start();
                }
            });
        }
    }

    protected void start() {
        if (activeStep.startsFinished()) {
            return;
        }

        if (activeStep.getShouldVibrateOnStart()) {
            vibrate();
        }

        if (activeStep.getShouldPlaySoundOnStart()) {
            playSound();
        }

        if (activeStep.getStepDuration() > 0) {
            startAnimation();
        }

        recorderList = new ArrayList<>();
        File outputDir = getContext().getFilesDir();
        if (DEBUG_SAVE_FILES_EXTERNALLY) {
            outputDir = getContext().getExternalFilesDir(null);
        }

        if (activeStep.getRecorderConfigurationList() != null) {
            for (RecorderConfig config : activeStep.getRecorderConfigurationList()) {
                Recorder recorder = config.recorderForStep(activeStep, outputDir);
                recorder.setRecorderListener(this);
                recorderList.add(recorder);
                recorder.start(getContext());
            }
        }
    }

    protected void stop() {
        if (activeStep.getShouldVibrateOnFinish()) {
            vibrate();
        }

        if (activeStep.getShouldPlaySoundOnFinish()) {
            playSound();
        }

        if (activeStep.getFinishedSpokenInstruction() != null) {
            speakText(activeStep.getFinishedSpokenInstruction());
        }

        boolean noRecordersActive = recorderList.isEmpty();

        for (Recorder recorder : recorderList) {
            recorder.stop();
        }

        if (!activeStep.getShouldContinueOnFinish()) {
            submitBar.setPositiveActionViewEnabled(true);
            submitBar.setPositiveAction(new Action1() {
                @Override
                public void call(Object o) {
                    callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
                }
            });
        } else if (noRecordersActive) {
            // There will be no recorders onComplete callbacks to wait for, so just go to next activeStep
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
        }

        mainHandler.removeCallbacksAndMessages(null);
    }

    /**
     * A force stop should be called when this step layout is being cancelled
     */
    public void forceStop() {
        if (recorderList != null) {
            for (Recorder recorder : recorderList) {
                recorder.cancel();
            }
        }
    }

    protected void skip() {
        for (Recorder recorder : recorderList) {
            recorder.setRecorderListener(new RecorderListener() {
                @Override
                public void onComplete(Recorder recorder, Result result) {
                    // no-op
                }

                @Override
                public void onFail(Recorder recorder, Throwable error) {
                    // no-op
                }
            });
            recorder.stop();
        }
        recorderList.clear();
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, null);
    }

    protected void startAnimation() {
        startTime = System.currentTimeMillis();
        secondsLeft = activeStep.getStepDuration();

        animationRunnable = new Runnable() {
            @Override
            public void run() {
                doUIAnimationPerSecond();

                secondsLeft--;

                // These calculations will remove any lag from the seconds timer
                long timeToPast = (activeStep.getStepDuration() - secondsLeft) * 1000;
                long nextSecond = startTime + timeToPast;
                long timeUntilNextSecond = nextSecond - System.currentTimeMillis();

                if (secondsLeft < 0) {
                    stop();
                } else {
                    mainHandler.postDelayed(animationRunnable, timeUntilNextSecond);
                }
            }
        };
        mainHandler.post(animationRunnable);
    }

    protected void doUIAnimationPerSecond() {
        timerTextview.setText(toMinuteSecondsString(secondsLeft));
    }

    private String toMinuteSecondsString(int seconds) {
        int mins = seconds / 60;
        int secs = seconds - mins * 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    protected void setupActiveViews() {
        titleTextview = (TextView) contentContainer.findViewById(R.id.rsb_active_step_layout_title);
        titleTextview.setText(activeStep.getTitle());
        titleTextview.setVisibility(activeStep.getTitle() == null ? View.GONE : View.VISIBLE);

        textTextview = (TextView) contentContainer.findViewById(R.id.rsb_active_step_layout_text);
        textTextview.setText(activeStep.getText());
        textTextview.setVisibility(activeStep.getText() == null ? View.GONE : View.VISIBLE);

        timerTextview = (TextView) contentContainer.findViewById(R.id.rsb_active_step_layout_countdown);

        progressBar = (ProgressBar) contentContainer.findViewById(R.id.rsb_active_step_layout_progress);
        progressBarHorizontal = (ProgressBar) contentContainer.findViewById(R.id.rsb_active_step_layout_progress_horizontal);

        imageView = (ImageView) contentContainer.findViewById(R.id.rsb_image_view);
        if (activeStep.getImageResName() != null) {
            int drawableInt = ResUtils.getDrawableResourceId(getContext(), activeStep.getImageResName());
            if (drawableInt != 0) {
                imageView.setImageResource(drawableInt);
                imageView.setVisibility(View.VISIBLE);
            }
        } else {
            imageView.setVisibility(View.GONE);
        }

        activeStepLayout = (LinearLayout) contentContainer.findViewById(R.id.rsb_step_layout_active_layout);

        if (activeStep.hasCountDown()) {
            timerTextview.setVisibility(View.VISIBLE);
        } else {
            timerTextview.setVisibility(View.GONE);
        }
    }

    protected void validateStep(Step step) {
        if (!(step instanceof ActiveStep)) {
            throw new IllegalStateException("ActiveStepLayout must have an ActiveStep");
        }
        activeStep = (ActiveStep)step;
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        // You cannot go back during an active activeStep, you can only cancel
        return true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mainHandler.removeCallbacksAndMessages(null);
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    private void vibrate() {
        Vibrator v = (Vibrator) getContext().getSystemService(Context.VIBRATOR_SERVICE);
        v.vibrate(DEFAULT_VIBRATION_AND_SOUND_DURATION);
    }

    protected void playSound() {
        ToneGenerator toneG = new ToneGenerator(AudioManager.STREAM_ALARM, 50); // 50 = half volume
        // Play a low and high tone for 500 ms at full volume
        toneG.startTone(ToneGenerator.TONE_CDMA_LOW_L, DEFAULT_VIBRATION_AND_SOUND_DURATION);
        toneG.startTone(ToneGenerator.TONE_CDMA_HIGH_L, DEFAULT_VIBRATION_AND_SOUND_DURATION);
    }

    protected void speakText(String text) {
        if (tts == null) {
            return;
        }
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            ttsGreater21(text);
        } else {
            ttsUnder20(text);
        }
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

    @Override
    public void onComplete(Recorder recorder, Result result) {
        stepResult.setResultForIdentifier(recorder.getIdentifier(), result);
        recorderList.remove(recorder);
        if (recorderList.isEmpty()) {
            stepResultFinished();
            if (activeStep.getShouldContinueOnFinish()) {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
            } else {
                submitBar.getPositiveActionView().setEnabled(true);
                submitBar.setPositiveAction(new Action1() {
                    @Override
                    public void call(Object o) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
                    }
                });
            }
        }
    }

    protected void stepResultFinished() {
        // To be implemented by sub-classes that need to save more info to step result
    }

    @Override
    public void onFail(Recorder recorder, Throwable error) {
        if (tts != null && tts.isSpeaking()) {
            tts.stop();
        }
        super.showOkAlertDialog(error.getMessage(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                callbacks.onSaveStep(StepCallbacks.ACTION_END, activeStep, null);
            }
        });
    }

    // TextToSpeech initialization
    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int languageAvailable = tts.isLanguageAvailable(Locale.getDefault());
            // >= 0 means LANG_AVAILABLE, LANG_COUNTRY_AVAILABLE, or LANG_COUNTRY_VAR_AVAILABLE
            if (languageAvailable >= 0) {
                tts.setLanguage(Locale.getDefault());
                if (ActiveStepLayout.this.activeStep.getSpokenInstruction() != null) {
                    speakText(ActiveStepLayout.this.activeStep.getSpokenInstruction());
                }
            } else {
                tts = null;
            }
        } else {
            Log.e(getClass().getCanonicalName(), "Failed to initialize TTS with error code " + i);
            tts = null;
        }
    }
}
