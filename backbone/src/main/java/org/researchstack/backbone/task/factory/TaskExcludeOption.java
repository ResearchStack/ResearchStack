package org.researchstack.backbone.task.factory;

/**
 * Created by TheMDP on 2/15/17.
 */

/**
 * The `TaskExcludeOption` enum lets you exclude particular behaviors from the predefined active
 * tasks in the predefined category of `OrderedTask`.
 *
 * By default, all predefined tasks include instructions and conclusion steps, and may also include
 * one or more data collection recorder configurations. Although not all predefined tasks include all
 * of these data collection types, the predefined task enum flags can be used to explicitly specify
 * that a task option not be included.
 */
public enum TaskExcludeOption {
    // Exclude the initial instruction steps.
    INSTRUCTIONS,
    // Exclude the conclusion step.
    CONCLUSION,
    // Exclude accelerometer data collection.
    ACCELEROMETER,
    // Exclude device motion data collection.
    DEVICE_MOTION,
    // Exclude pedometer data collection.
    PEDOMETER,
    // Exclude location data collection.
    LOCATION,
    // Exclude heart rate data collection.
    HEART_RATE,
    // Exclude audio data collection.
    AUDIO
}
