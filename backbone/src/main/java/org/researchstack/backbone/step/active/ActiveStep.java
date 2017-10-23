package org.researchstack.backbone.step.active;

import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.active.recorder.RecorderConfig;
import org.researchstack.backbone.ui.step.layout.ActiveStepLayout;

import java.util.List;

/**
 * Created by TheMDP on 2/4/17.
 *
 * The `ORKActiveStep` class is the base class for steps in active tasks, which
 * are steps that collect sensor data in a semi-controlled environment, as opposed
 * to the more subjective data collected when users fill in surveys.
 *
 * In addition to the behaviors of `Step`, active steps have the concept of
 * life cycle, which includes a defined start and finish.
 *
 * The ResearchStack library provides built-in behaviors that allow active steps
 * to play voice prompts, speak a count down, and have a defined duration.
 *
 * To present an active step in your app, it's likely that you will subclass `ActiveStep` and
 * `ActiveStepLayout` to present custom UI and custom
 * prompts. For example subclasses, see `SpatialSpanMemoryStep` or `FitnessStep`.
 *
 * Active steps may also need `StepResult` subclasses to record their results
 * if these don't come purely from recorders.
 *
 * If you develop a new active step subclass, consider contributing your
 * code to the ResearchStack project so that it's available for others to use in
 * their studies.
 */

public class ActiveStep extends Step {

    /**
     * The duration of the step in seconds.
     *
     * If the step duration is greater than zero, a built-in timer starts when the
     * step starts. If `shouldStartTimerAutomatically` is set, the timer
     * starts when the step's view appears. When the timer expires, a sound or
     * vibration may be played. If `shouldContinueOnFinish` is set, the step
     * automatically navigates forward when the timer expires.
     *
     * The default value of this property is `0`, which disables the built-in timer.
     */
    private int stepDuration = 0;

    /**
     * A Boolean value indicating whether to show a view with a default timer.
     *
     * The default timer UI is not used in any of the current predefined tasks,
     * but it can be displayed in a simple active task that does not require custom
     * UI and needs only a count down timer on screen during data collection.
     *
     * Note that this property is ignored if `stepDuration` is `0`.
     *
     * The default value of this property is true.
     */
    private boolean shouldShowDefaultTimer = true;

    /**
     * A Boolean value indicating whether to speak the last few seconds in the count down of the
     * duration of a timed step.
     *
     * When the value of this property is `true`, `TextToSpeech` is used to synthesize the countdown.
     * Note that this member variable is ignored if VoiceOver is enabled.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldSpeakCountDown = false;

    /**
     * A Boolean value indicating whether to speak the halfway point in the count down of the
     * duration of a timed step.
     *
     * When the value of this property is `true`, `TextToSpeech` is used to synthesize the countdown.
     * Note that this member variable is ignored if VoiceOver is enabled.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldSpeakRemainingTimeAtHalfway = false;

    /**
     * A Boolean value indicating whether to start the count down timer automatically when the step starts, or
     * require the user to take some explicit action to start the step, such as tapping a button.
     *
     * Usually the explicit action needs to come from custom UI in an `ActiveStepLayout` subclass.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldStartTimerAutomatically = false;

    /**
     * A Boolean value indicating whether to play a default sound when the step starts.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldPlaySoundOnStart = false;

    /**
     * A Boolean value indicating whether to play a default sound when the step finishes.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldPlaySoundOnFinish = false;

    /**
     * A Boolean value indicating whether to vibrate when the step starts.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldVibrateOnStart = false;

    /**
     * A Boolean value indicating whether to vibrate when the step finishes.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldVibrateOnFinish = false;

    /**
     * A Boolean value indicating whether the Next button should double as a skip action before
     * the step finishes.
     *
     * When the value of this property is `true`, the ResearchStack library hides the skip button and
     * makes the Next button function as a skip button when the step has not yet finished.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldUseNextAsSkipButton = false;

    /**
     * A Boolean value indicating whether to transition automatically when the step finishes.
     *
     * When the value of this property is `true`, the active step layout automatically performs the
     * continue action when the `[ActiveStepLayout finish]` method
     * is called.
     *
     * The default value of this property is `false`.
     */
    private boolean shouldContinueOnFinish = false;

    /**
     * Localized text that represents an instructional voice prompt.
     *
     * Instructional speech begins when the step starts. If VoiceOver is active,
     * the instruction is spoken by VoiceOver.
     */
    private String spokenInstruction;

    /**
     * Localized text that represents an instructional voice prompt for when the step finishes.
     *
     * Instructional speech begins when the step finishes. If VoiceOver is active,
     * the instruction is spoken by VoiceOver.
     */
    private String finishedSpokenInstruction;

    /**
     * An array of recorder configurations that define the parameters for recorders to be
     * run during a step to collect sensor or other data.
     *
     * If you want to collect data from sensors while the step is in progress,
     * add one or more recorder configurations to the array. The active step view
     * controller instantiates recorders and collates their results as children
     * of the step result.
     *
     * The set of recorder configurations is scanned when populating the
     * `requestedHealthKitTypesForReading` and `requestedPermissions` properties.
     *
     * See also: `ORKRecorderConfiguration` and `ORKRecorder`.
     */
    private List<RecorderConfig> recorderConfigurationList;

    /**
     * An image to be displayed below the instructions for the step.
     *
     * It will be loaded from resources with the value of this variable
     */
    private String imageResName;

    /* Default constructor needed for serialization/deserialization of object */
    ActiveStep() {
        super();
    }

    public ActiveStep(String identifier) {
        super(identifier);
        setOptional(false);
    }

    public ActiveStep(String identifier, String title, String detailText) {
        super(identifier, title);
        setText(detailText);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return ActiveStepLayout.class;
    }

    public void setStepDuration(int stepDuration) {
        this.stepDuration = stepDuration;
    }

    public int getStepDuration() {
        return stepDuration;
    }

    public void setShouldShowDefaultTimer(boolean shouldShowDefaultTimer) {
        this.shouldShowDefaultTimer = shouldShowDefaultTimer;
    }

    public boolean getShouldShowDefaultTimer() {
        return shouldShowDefaultTimer;
    }

    public void setShouldSpeakCountDown(boolean shouldSpeakCountDown) {
        this.shouldSpeakCountDown = shouldSpeakCountDown;
    }

    public boolean getShouldSpeakCountDown() {
        return shouldSpeakCountDown;
    }

    public void setShouldSpeakRemainingTimeAtHalfway(boolean shouldSpeakRemainingTimeAtHalfway) {
        this.shouldSpeakRemainingTimeAtHalfway = shouldSpeakRemainingTimeAtHalfway;
    }

    public boolean getShouldSpeakRemainingTimeAtHalfway() {
        return shouldSpeakRemainingTimeAtHalfway;
    }

    public void setShouldStartTimerAutomatically(boolean shouldStartTimerAutomatically) {
        this.shouldStartTimerAutomatically = shouldStartTimerAutomatically;
    }

    public boolean getShouldStartTimerAutomatically() {
        return shouldStartTimerAutomatically;
    }

    public boolean getShouldPlaySoundOnStart() {
        return shouldPlaySoundOnStart;
    }

    public void setShouldPlaySoundOnStart(boolean shouldPlaySoundOnStart) {
        this.shouldPlaySoundOnStart = shouldPlaySoundOnStart;
    }

    public boolean getShouldPlaySoundOnFinish() {
        return shouldPlaySoundOnFinish;
    }

    public void setShouldPlaySoundOnFinish(boolean shouldPlaySoundOnFinish) {
        this.shouldPlaySoundOnFinish = shouldPlaySoundOnFinish;
    }

    public boolean getShouldVibrateOnStart() {
        return shouldVibrateOnStart;
    }

    public void setShouldVibrateOnStart(boolean shouldVibrateOnStart) {
        this.shouldVibrateOnStart = shouldVibrateOnStart;
    }

    public boolean getShouldVibrateOnFinish() {
        return shouldVibrateOnFinish;
    }

    public void setShouldVibrateOnFinish(boolean shouldVibrateOnFinish) {
        this.shouldVibrateOnFinish = shouldVibrateOnFinish;
    }

    public boolean getShouldUseNextAsSkipButton() {
        return shouldUseNextAsSkipButton;
    }

    public void setShouldUseNextAsSkipButton(boolean shouldUseNextAsSkipButton) {
        this.shouldUseNextAsSkipButton = shouldUseNextAsSkipButton;
    }

    public boolean getShouldContinueOnFinish() {
        return shouldContinueOnFinish;
    }

    public void setShouldContinueOnFinish(boolean shouldContinueOnFinish) {
        this.shouldContinueOnFinish = shouldContinueOnFinish;
    }

    public String getSpokenInstruction() {
        return spokenInstruction;
    }

    public void setSpokenInstruction(String spokenInstruction) {
        this.spokenInstruction = spokenInstruction;
    }

    public String getFinishedSpokenInstruction() {
        return finishedSpokenInstruction;
    }

    public void setFinishedSpokenInstruction(String finishedSpokenInstruction) {
        this.finishedSpokenInstruction = finishedSpokenInstruction;
    }

    public boolean startsFinished() {
        return stepDuration == 0;
    }

    public boolean hasCountDown() {
        return (stepDuration > 0) && shouldShowDefaultTimer;
    }

    public boolean hasVoice() {
        boolean hasSpokenInstruction = spokenInstruction != null && !spokenInstruction.isEmpty();
        boolean hasFinishedSpokenInstruction = finishedSpokenInstruction != null && !finishedSpokenInstruction.isEmpty();
        return (hasSpokenInstruction || hasFinishedSpokenInstruction);
    }

    public List<RecorderConfig> getRecorderConfigurationList() {
        return recorderConfigurationList;
    }

    public void setRecorderConfigurationList(List<RecorderConfig> recorderConfigurationList) {
        this.recorderConfigurationList = recorderConfigurationList;
    }

    public String getImageResName() {
        return imageResName;
    }

    public void setImageResName(String imageResName) {
        this.imageResName = imageResName;
    }
}
