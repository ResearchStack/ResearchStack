
package com.spineapp;

import org.researchstack.backbone.step.active.ActiveStep;

public class TouchAnywhereStep extends ActiveStep
{

  /*Default constructor needed for serilization/deserialization of object*/

  //public TouchAnywhereStep() { //TODO: not sure why this method creates an error
  //super();
  //}

  public TouchAnywhereStep(String identifier) {
    super(identifier);
    commonInit();
  }

  public TouchAnywhereStep(String identifier, String title, String detailText) {
    super(identifier, title, detailText);
    commonInit();
  }

  private void commonInit() {
    setOptional(false);
    setShouldVibrateOnStart(true);
    setShouldPlaySoundOnStart(true);
    setShouldVibrateOnFinish(true);
    setShouldPlaySoundOnFinish(true);
    setShouldStartTimerAutomatically(true);
    setShouldShowDefaultTimer(false);
    setShouldContinueOnFinish(true);
    setEstimateTimeInMsToSpeakEndInstruction(0); // do not wait to proceed
  }

  @Override
  public Class getStepLayoutClass() {
    return TouchAnywhereStepLayout.class;
  }
}
