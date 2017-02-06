package org.researchstack.backbone.ui.step.layout;

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
 *
 */

public class ActiveStepLayout {

}
