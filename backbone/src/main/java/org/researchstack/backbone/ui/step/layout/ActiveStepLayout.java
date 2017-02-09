package org.researchstack.backbone.ui.step.layout;

import android.annotation.TargetApi;
import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.util.AttributeSet;
import android.util.Log;
import android.view.Surface;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.logger.DataLogger;
import org.researchstack.backbone.result.logger.DataLoggerManager;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.Recorder;
import org.researchstack.backbone.step.active.RecorderConfig;
import org.researchstack.backbone.step.active.RecorderListener;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;

import java.io.File;
import java.lang.ref.WeakReference;
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
 * subclasses. The predefined active tasks defined in `OrderedTaskFactory` all make use
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

public class ActiveStepLayout extends FixedSubmitBarLayout implements StepLayout, RecorderListener, TextToSpeech.OnInitListener {

    private static final int DEFAULT_VIBRATION_AND_SOUND_DURATION = 500; // in milliseconds

    /**
     * When this is true, files will be saved externally so you can read them
     * Reading internal files requires root access
     * You must add WRITE_EXTERNAL_STORAGE permission to manifest as well
     */
    private static final boolean DEBUG_SAVE_FILES_EXTERNALLY = false;

    private TextToSpeech tts;

    private WeakReference<Activity> weakActivity;
    private StepCallbacks callbacks;

    private List<Recorder> recorderList;

    private StepResult<Result> stepResult;

    private Handler  mainHandler;
    private Runnable animationRunnable;
    private long startTime;
    private int  secondsLeft;

    private ActiveStep step;

    private TextView titleTextview;
    private TextView textTextview;
    private TextView timerTextview;

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

        setupViews();
        setupSubmitBar();

        stepResult = new StepResult<>(step);

        if (this.step.hasVoice()) {
            tts = new TextToSpeech(getContext(), this);
        }

        if (this.step.getShouldStartTimerAutomatically()) {
            start();
        }
    }

    protected void setupSubmitBar() {
        if (step.isOptional()) {
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

        if (this.step.getShouldStartTimerAutomatically()) {
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
        if (step.startsFinished()) {
            return;
        }

        if (step.getShouldVibrateOnStart()) {
            vibrate();
        }

        if (step.getShouldPlaySoundOnStart()) {
            playSound();
        }

        if (step.hasCountDown()) {
            timerTextview.setVisibility(View.VISIBLE);
            startAnimation();
        } else {
            timerTextview.setVisibility(View.GONE);
        }

        recorderList = new ArrayList<>();
        File outputDir = getContext().getFilesDir();
        if (DEBUG_SAVE_FILES_EXTERNALLY) {
            outputDir = getContext().getExternalFilesDir(null);
        }
        for (RecorderConfig config : step.getRecorderConfigurationList()) {
            Recorder recorder = config.recorderForStep(step, outputDir);
            recorder.setRecorderListener(this);
            recorderList.add(recorder);
            recorder.start(getContext());
        }
    }

    protected void stop() {
        if (step.getShouldVibrateOnFinish()) {
            vibrate();
        }

        if (step.getShouldPlaySoundOnFinish()) {
            playSound();
        }

        if (step.getFinishedSpokenInstruction() != null) {
            speakText(step.getFinishedSpokenInstruction());
        }

        for (Recorder recorder : recorderList) {
            recorder.stop();
        }

        if (!step.getShouldContinueOnFinish()) {
            submitBar.setPositiveActionViewEnabled(true);
            submitBar.setPositiveAction(new Action1() {
                @Override
                public void call(Object o) {
                    callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
                }
            });
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
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, null);
    }

    private void startAnimation() {
        mainHandler = new Handler();
        startTime = System.currentTimeMillis();
        secondsLeft = step.getStepDuration();

        animationRunnable = new Runnable() {
            @Override
            public void run() {
                timerTextview.setText(toMinuteSecondsString(secondsLeft));
                secondsLeft--;

                // These calculations will remove any lag from the seconds timer
                long timeToPast = (step.getStepDuration() - secondsLeft) * 1000;
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

    private String toMinuteSecondsString(int seconds) {
        int mins = seconds / 60;
        int secs = seconds - mins * 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    private void setupViews() {
        titleTextview = (TextView) contentContainer.findViewById(R.id.rsb_active_step_layout_title);
        titleTextview.setText(step.getTitle());
        titleTextview.setVisibility(step.getTitle() == null ? View.GONE : View.VISIBLE);

        textTextview = (TextView) contentContainer.findViewById(R.id.rsb_active_step_layout_text);
        textTextview.setText(step.getText());
        textTextview.setVisibility(step.getText() == null ? View.GONE : View.VISIBLE);

        timerTextview = (TextView) contentContainer.findViewById(R.id.rsb_active_step_layout_countdown);
    }

    protected void validateStep(Step step) {
        if (!(step instanceof ActiveStep)) {
            throw new IllegalStateException("ActiveStepLayout must have an ActiveStep");
        }
        this.step = (ActiveStep)step;
    }

    @Override
    public View getLayout() {
        return this;
    }

    @Override
    public boolean isBackEventConsumed() {
        // You cannot go back during an active step, you can only cancel
        return true;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mainHandler.removeCallbacks(animationRunnable);
        unlockOrientation();
        unlockScreenOn();
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        if (callbacks instanceof Activity) {
            weakActivity = new WeakReference<>((Activity)callbacks);
            lockOrientation();
            lockScreenOn();
        } else {
            throw new IllegalStateException("ActiveStepLayout requires the callbacks to be an Activity" +
                    "this is so it can lock the screen orientation and keep the screen on");
        }
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

    /**
     * Active Steps lock screen to on so it can avoid any interruptions during data logging
     */
    private void lockScreenOn() {
        if (weakActivity.get() == null) {
            return;
        }
        weakActivity.get().getWindow().addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    private void unlockScreenOn() {
        if (weakActivity.get() == null) {
            return;
        }
        weakActivity.get().getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
    }

    /**
     * Active Steps lock orientation so it can avoid any interruptions during data logging
     */
    private void lockOrientation() {
        if (weakActivity.get() == null) {
            return;
        }
        int orientation;
        int rotation = ((WindowManager) weakActivity.get().getSystemService(
                Context.WINDOW_SERVICE)).getDefaultDisplay().getRotation();
        switch (rotation) {
            case Surface.ROTATION_0:
                orientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
                break;
            case Surface.ROTATION_90:
                orientation = ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE;
                break;
            case Surface.ROTATION_180:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_PORTRAIT;
                break;
            default:
                orientation = ActivityInfo.SCREEN_ORIENTATION_REVERSE_LANDSCAPE;
                break;
        }
        weakActivity.get().setRequestedOrientation(orientation);
    }

    private void unlockOrientation() {
        if (weakActivity.get() == null) {
            return;
        }
        weakActivity.get().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED);
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
        String utteranceId = this.hashCode() + "";
        tts.speak(text, TextToSpeech.QUEUE_FLUSH, null, utteranceId);
    }

    @Override
    public void onComplete(Recorder recorder, Result result) {
        stepResult.setResultForIdentifier(recorder.getIdentifier(), result);
        recorderList.remove(recorder);
        if (recorderList.isEmpty()) {
            if (step.getShouldContinueOnFinish()) {
                callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
            } else {
                submitBar.getPositiveActionView().setEnabled(true);
                submitBar.setPositiveAction(new Action1() {
                    @Override
                    public void call(Object o) {
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, step, stepResult);
                    }
                });
            }
        }
    }

    @Override
    public void onFail(Recorder recorder, Throwable error) {
        super.showOkAlertDialog(error.getMessage());
    }

    // TextToSpeech initialization
    @Override
    public void onInit(int i) {
        if (i == TextToSpeech.SUCCESS) {
            int languageAvailable = tts.isLanguageAvailable(Locale.getDefault());
            // >= 0 means LANG_AVAILABLE, LANG_COUNTRY_AVAILABLE, or LANG_COUNTRY_VAR_AVAILABLE
            if (languageAvailable >= 0) {
                tts.setLanguage(Locale.getDefault());
                if (ActiveStepLayout.this.step.getSpokenInstruction() != null) {
                    speakText(ActiveStepLayout.this.step.getSpokenInstruction());
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
