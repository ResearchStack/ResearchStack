package org.researchstack.backbone.ui.step.layout;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.media.AudioManager;
import android.media.ToneGenerator;
import android.os.Build;
import android.os.Handler;
import android.os.Vibrator;
import android.speech.tts.TextToSpeech;
import android.support.annotation.CallSuper;
import android.support.annotation.RequiresPermission;
import android.support.v4.content.LocalBroadcastManager;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.result.FileResult;
import org.researchstack.backbone.result.Result;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.ActiveStep;
import org.researchstack.backbone.step.active.ActiveTaskAndResultListener;
import org.researchstack.backbone.step.active.RecorderService;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.FixedSubmitBarLayout;
import org.researchstack.backbone.utils.LogExt;
import org.researchstack.backbone.utils.ResUtils;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import static org.researchstack.backbone.step.active.RecorderService.DEFAULT_VIBRATION_AND_SOUND_DURATION;

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
        implements StepLayout, TextToSpeech.OnInitListener {

    /**
     * When this is true, files will be saved externally so you can read them
     * since reading internal files requires root access.
     * You must add WRITE_EXTERNAL_STORAGE permission to manifest as well
     */
    private static final boolean DEBUG_SAVE_FILES_EXTERNALLY = false;

    private TextToSpeech tts;
    protected String textToSpeakOnInit;

    protected StepCallbacks callbacks;

    private BroadcastReceiver recorderServiceReceiver;

    protected StepResult<Result> stepResult;
    protected Handler  mainHandler;
    protected Runnable animationRunnable;
    protected long startTime;

    protected int  secondsLeft;

    protected ActiveStep activeStep;
    protected LinearLayout activeStepLayout;
    protected boolean isDetached;

    public LinearLayout getActiveStepLayout() {
        return activeStepLayout;
    }
    protected TextView titleTextview;
    protected TextView textTextview;
    protected TextView timerTextview;
    protected ProgressBar progressBar;
    protected ProgressBar progressBarHorizontal;

    protected ImageView imageView;

    protected ActiveTaskAndResultListener taskAndResultListener;
    public void setTaskAndResultListener(ActiveTaskAndResultListener listener) {
        taskAndResultListener = listener;
    }

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

        if (!checkForAndLoadExistingState()) {
            if (activeStep.getShouldStartTimerAutomatically()) {
                start();
            }
        }
    }

    /**
     * This is called when the activity containing this active step layout
     * has previously moved to onPause and is coming back to the foreground with onResume
     * This will only happen when the user actually leaves the app, or shuts off the screen, then returns
     */
    public void resumeActiveStepLayout() {
        if (isDetached) {  // we can only resume if this view were previously detached
            checkForAndLoadExistingState();
        }
    }

    /**
     * This is called when the activity containing this active step layout
     * has moved to onPause and we should stop responding to broadcasts and anything else UI related
     */
    public void pauseActiveStepLayout() {
        LogExt.d(ActiveStepLayout.class, "pauseActiveStepLayout()");
        removeUiRelatedItemsAndCallbacks();
    }

    /**
     * This is called when the activity containing this active step layout
     * has moved to onCreate to provide a hook point for when the activity is initialised.
     */
    public void createActiveStepLayout() {
        LogExt.d(ActiveStepLayout.class, "createActiveStepLayout()");
    }

    protected void removeUiRelatedItemsAndCallbacks() {
        LogExt.d(ActiveStepLayout.class, "removeUiRelatedItemsAndCallbacks()");
        isDetached = true;
        mainHandler.removeCallbacksAndMessages(null);
        if (tts != null) {
            tts.shutdown();
            tts = null;
        }
        unregisterRecorderBroadcastReceivers();
    }

    protected boolean checkForAndLoadExistingState() {
        isDetached = false;
        Context appContext = getContext().getApplicationContext();

        // Check if this view is being re-created after it was destroyed while recording finished
        RecorderService.ResultHolder resultHolder =
                RecorderService.consumeSavedResultList(appContext, activeStep);
        if (resultHolder != null) {
            // This view was destroyed while we were running the recorders in
            // the background in the RecorderService, and now we are being re-created
            // after the recorder has finished
            stepLayoutWasResumedInFinishedState(resultHolder);
            return true;
        }

        // Check if this view was destroyed and re-created while the recorder was still running
        Long recorderStartTime = RecorderService.getStartTime(appContext, activeStep);
        if (recorderStartTime != null) {
            stepLayoutWasResumedInRecordingState(recorderStartTime);
            return true;
        }
        return false;
    }

    protected void setupSubmitBar() {
        if (submitBar == null) {
            return;  // some custom UI implementations don't use the submit bar
        }

        if (activeStep.isOptional()) {
            submitBar.getNegativeActionView().setVisibility(View.VISIBLE);
            submitBar.setNegativeAction(o -> skip());
        } else {
            submitBar.getNegativeActionView().setVisibility(View.GONE);
        }

        if (activeStep.getShouldStartTimerAutomatically()) {
            submitBar.setPositiveActionViewEnabled(false);
        } else {
            submitBar.setPositiveTitle(R.string.rsb_BUTTON_GET_STARTED);
            submitBar.setPositiveAction(o -> {
                submitBar.setPositiveTitle(R.string.rsb_BUTTON_NEXT);
                start();
            });
        }
    }
    
    /**
     * This method will be called when we were recording in the background with RecorderService
     * and this step layout was destroyed and then re-created before the recording finished
     */
    protected void stepLayoutWasResumedInRecordingState(long recordingStartTime) {
        LogExt.d(ActiveStepLayout.class, "stepLayoutWasResumedInRecordingState()");
        // can be implemented by sub-class to resume it's UI
        startTime = recordingStartTime;
        startAnimation();

        // Since we are not finished recording, we should re-register for recorder broadcasts
        Context appContext = getContext().getApplicationContext();
        registerRecorderBroadcastReceivers(appContext);
    }

    /**
     * Should be implemented by sub-class to resume it's UI in the finished state
     * This method will be called when we were recording in the background with RecorderService
     * and this step layout was destroyed by the OS and then re-created after recording has finished
     */
    protected void stepLayoutWasResumedInFinishedState(RecorderService.ResultHolder resultHolder) {
        LogExt.d(ActiveStepLayout.class, "stepLayoutWasCreatedInFinishedState()");
        processRecorderServiceResults(resultHolder, true);
    }

    protected void registerRecorderBroadcastReceivers(Context appContext) {
        LogExt.d(ActiveStepLayout.class, "registerRecorderBroadcastReceivers()");
        recorderServiceReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                if (intent == null || intent.getAction() == null) {
                    return;
                }
                if (RecorderService.ACTION_BROADCAST_RECORDER_COMPLETE.equals(intent.getAction())) {
                    LogExt.d(ActiveStepLayout.class, "RecorderService complete broadcast received");
                    RecorderService.ResultHolder resultHolder =
                            RecorderService.consumeSavedResultList(appContext, activeStep);
                    if (resultHolder == null) {
                        showOkAlertDialog("Critical error, no recorder results", (dialogInterface, i) -> {
                            callbacks.onSaveStep(StepCallbacks.ACTION_END, activeStep, null);
                        });
                    } else {
                        processRecorderServiceResults(resultHolder, false);
                    }
                } else if (RecorderService.ACTION_BROADCAST_RECORDER_METRONOME.equals(intent.getAction())) {
                    if (intent.hasExtra(RecorderService.BROADCAST_RECORDER_METRONOME_CTR)) {
                        recorderServiceMetronomeAction(intent.getIntExtra(
                                RecorderService.BROADCAST_RECORDER_METRONOME_CTR, 0));
                    }
                } else if (RecorderService.ACTION_BROADCAST_RECORDER_SPOKEN_TEXT.equals(intent.getAction())) {
                    if (intent.hasExtra(RecorderService.BROADCAST_RECORDER_SPOKEN_TEXT)) {
                        recorderServiceSpokeText(intent.getStringExtra(
                                RecorderService.BROADCAST_RECORDER_SPOKEN_TEXT));
                    }
                }
            }
        };
        IntentFilter intentFilter = new IntentFilter(RecorderService.ACTION_BROADCAST_RECORDER_COMPLETE);
        intentFilter.addAction(RecorderService.ACTION_BROADCAST_RECORDER_METRONOME);
        intentFilter.addAction(RecorderService.ACTION_BROADCAST_RECORDER_SPOKEN_TEXT);
        LocalBroadcastManager.getInstance(appContext)
                .registerReceiver(recorderServiceReceiver, intentFilter);
    }

    protected void unregisterRecorderBroadcastReceivers() {
        LogExt.d(ActiveStepLayout.class, "unregisterRecorderBroadcastReceivers()");
        // Remove the recorder receiver, we will check if it completed when this view is re-created
        if (recorderServiceReceiver != null) {
            Context appContext = getContext().getApplicationContext();
            LocalBroadcastManager.getInstance(appContext).unregisterReceiver(recorderServiceReceiver);
        }
    }

    @CallSuper
    @RequiresPermission(value = Manifest.permission.VIBRATE, conditional = true)
    public void start() {
        LogExt.d(ActiveStepLayout.class, "start()");
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
            startTime = System.currentTimeMillis();
            startAnimation();
        }

        startBackgroundRecorderService();
    }

    protected void startBackgroundRecorderService() {
        LogExt.d(ActiveStepLayout.class, "startBackgroundRecorderService()");
        Context appContext = getContext().getApplicationContext();
        if (taskAndResultListener == null) {
            throw new IllegalStateException("taskAndResultListener cant be null +" +
                    "this should be set through the ActiveTaskActivity");
        }
        RecorderService.startService(
                appContext, getOutputDirectory(appContext), activeStep,
                taskAndResultListener.activeTaskActivityGetTask(),
                taskAndResultListener.activeTaskActivityResult());
        registerRecorderBroadcastReceivers(appContext);
    }

    protected void stopRecordingService() {
        LogExt.d(ActiveStepLayout.class, "stopRecordingService()");
        getContext().stopService(new Intent(getContext(), RecorderService.class));
    }

    protected void processRecorderServiceResults(
            final RecorderService.ResultHolder resultHolder, boolean delayOperation) {

        long delay = delayOperation ? 100 : 0;
        // We need to delay the callback.onSaveStep() to proceed to the next step slightly,
        // this gives the activity time to finish setting up this StepLayout before moving to the next
        mainHandler.postDelayed(() -> {
            if (resultHolder.getErrorMessage() != null) {
                LogExt.d(ActiveStepLayout.class, "RecorderService complete error message received");
                showOkAlertDialog(resultHolder.getErrorMessage(), (dialogInterface, i) -> {
                    callbacks.onSaveStep(StepCallbacks.ACTION_END, activeStep, null);
                });
            } else {
                LogExt.d(ActiveStepLayout.class, "RecorderService complete success");
                List<FileResult> recorderResults = resultHolder.getResultList();
                for (Result result : recorderResults) {
                    stepResult.setResultForIdentifier(result.getIdentifier(), result);
                }
                stop();
                stepResultFinished();
            }
        }, delay);
    }

    /**
     * @param ctx can be app or activity, used to get files directory
     * @return directory for outputting data logger files
     */
    public static File getOutputDirectory(Context ctx) {
        File outputDir = ctx.getFilesDir();
        if (DEBUG_SAVE_FILES_EXTERNALLY) {
            outputDir = ctx.getExternalFilesDir(null);
        }
        return outputDir;
    }

    @RequiresPermission(value = Manifest.permission.VIBRATE, conditional = true)
    public void stop() {
        LogExt.d(ActiveStepLayout.class, "stop()");
        mainHandler.removeCallbacksAndMessages(null);
        if (!activeStep.getShouldContinueOnFinish()) {
            if (submitBar != null) {
                submitBar.setPositiveActionViewEnabled(true);
                submitBar.setPositiveAction(o ->
                        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult));
            }
        } else {
            callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, stepResult);
        }
    }

    /**
     * A force stop should be called when this step layout is being cancelled
     */
    public void forceStop() {
        LogExt.d(ActiveStepLayout.class, "forceStop()");
        stopRecordingService();
    }

    public void skip() {
        LogExt.d(ActiveStepLayout.class, "skip()");
        pauseActiveStepLayout();
        forceStop();
        callbacks.onSaveStep(StepCallbacks.ACTION_NEXT, activeStep, null);
    }

    protected void startAnimation() {
        LogExt.d(ActiveStepLayout.class, "startAnimation()");
        // Start animation may not be called when the recording starts
        // so calculate how many seconds have gone by
        long durationInMs = activeStep.getStepDuration() * 1000L;
        long elapsedTimeInMs = System.currentTimeMillis() - startTime;
        secondsLeft = (int)((durationInMs - elapsedTimeInMs) / 1000);

        animationRunnable = () -> {
            doUIAnimationPerSecond();

            secondsLeft--;

            // These calculations will remove any lag from the seconds timer
            long timeToPast = (activeStep.getStepDuration() - secondsLeft) * 1000;
            long nextSecond = startTime + timeToPast;
            long timeUntilNextSecond = nextSecond - System.currentTimeMillis();

            if (secondsLeft >= 0) {
                mainHandler.postDelayed(animationRunnable, timeUntilNextSecond);
            }
        };
        mainHandler.removeCallbacks(animationRunnable);
        mainHandler.post(animationRunnable);
    }

    public void doUIAnimationPerSecond() {
        if (timerTextview != null) {
            timerTextview.setText(toMinuteSecondsString(secondsLeft));
        }
    }

    private String toMinuteSecondsString(int seconds) {
        int mins = seconds / 60;
        int secs = seconds - mins * 60;
        return String.format(Locale.getDefault(), "%02d:%02d", mins, secs);
    }

    public void setupActiveViews() {
        titleTextview = contentContainer.findViewById(R.id.rsb_active_step_layout_title);
        if (titleTextview != null) {
            titleTextview.setText(activeStep.getTitle());
            titleTextview.setVisibility(activeStep.getTitle() == null ? View.GONE : View.VISIBLE);
        }

        textTextview = contentContainer.findViewById(R.id.rsb_active_step_layout_text);
        if (textTextview != null) {
            textTextview.setText(activeStep.getText());
            textTextview.setVisibility(activeStep.getText() == null ? View.GONE : View.VISIBLE);
        }

        timerTextview = contentContainer.findViewById(R.id.rsb_active_step_layout_countdown);

        progressBar = contentContainer.findViewById(R.id.rsb_active_step_layout_progress);
        progressBarHorizontal = contentContainer.findViewById(R.id.rsb_active_step_layout_progress_horizontal);

        imageView = contentContainer.findViewById(R.id.rsb_image_view);
        if (imageView != null) {
            if (activeStep.getImageResName() != null) {
                int drawableInt = ResUtils.getDrawableResourceId(getContext(), activeStep.getImageResName());
                if (drawableInt != 0) {
                    imageView.setImageResource(drawableInt);
                    imageView.setVisibility(View.VISIBLE);
                }
            } else {
                imageView.setVisibility(View.GONE);
            }
        }

        activeStepLayout = contentContainer.findViewById(R.id.rsb_step_layout_active_layout);

        if (timerTextview != null) {
            if (activeStep.hasCountDown()) {
                timerTextview.setVisibility(View.VISIBLE);
            } else {
                timerTextview.setVisibility(View.GONE);
            }
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
        LogExt.d(ActiveStepLayout.class, "onDetachedFromWindow()");
        removeUiRelatedItemsAndCallbacks();
    }

    @Override
    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    @RequiresPermission(Manifest.permission.VIBRATE)
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

    protected void stepResultFinished() {
        // To be implemented by sub-classes that need to save more info to step result
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
                } else if (textToSpeakOnInit != null) {
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

    protected void recorderServiceMetronomeAction(int metronomeCtr) {
        // Can be implemented by sub-class to do UI events on metronome sound
    }

    protected void recorderServiceSpokeText(String spokenText) {
        // Can be implemented by sub-class to also show text in the UI
    }
}
