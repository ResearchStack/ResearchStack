package org.researchstack.backbone.task.factory;

import android.content.Context;

import org.researchstack.backbone.task.Task;
import org.researchstack.backbone.task.builder.ActiveAudioCaptureBuilder;

public class ActiveAudioCaptureFactory {

    public static Task audioCaptureTask(String identifier,
                                        Context context,
                                        String infoText,
                                        String infoInstructions,
                                        String captureText,
                                        String captureInstructions,
                                        int durationSeconds) {
        return new ActiveAudioCaptureBuilder()
                .setIdentifier(identifier)
                .setContext(context)
                .setInfoText(infoText, infoInstructions)
                .setCaptureText(captureText, captureInstructions)
                .setDurationSeconds(durationSeconds)
                .build();
    }
}
