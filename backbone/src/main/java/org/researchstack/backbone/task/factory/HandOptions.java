package org.researchstack.backbone.task.factory;

/**
 * Created by TheMDP on 2/24/17.
 */

/**
 * Values that identify the hand(s) to be used in an active task.
 *
 * By default, the participant will be asked to use their most affected hand.
 */
public enum HandOptions {
    // Task should only test the left hand
    LEFT,
    // Task should only test the right hand
    RIGHT,
    // Task should test both left and right hands
    BOTH;
}
