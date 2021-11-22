package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.builder.ActiveSelfieCaptureBuilder;

public class ActiveSelfieCaptureFactory {

    public static Task selfieCaptureTask(String identifier,
                                         Context context,
                                         String infoText,
                                         String infoInstructions,
                                         String captureText,
                                         String captureInstructions,
                                         int waitTimeSeconds) {
        return new ActiveSelfieCaptureBuilder()
                .setIdentifier(identifier)
                .setContext(context)
                .setInfoText(infoText, infoInstructions)
                .setCaptureText(captureText, captureInstructions)
                .setWaitTimeSeconds(waitTimeSeconds)
                .build();
    }
}
