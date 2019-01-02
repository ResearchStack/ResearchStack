package org.researchstack.foundation.components.survey.step;

import org.researchstack.foundation.components.survey.ui.layout.InstructionStepLayout;
import org.researchstack.foundation.core.models.step.Step;

/**
 * An InstructionStep object gives the participant instructions for a task.
 * <p>
 * You can use instruction steps to present various types of content during a task, such as
 * introductory content, instructions in the middle of a task, or a final message at the completion
 * of a task.
 */
public class InstructionStep extends Step {
    public InstructionStep(String identifier, String title, String detailText) {
        super(identifier, title);
        setText(detailText);
        setOptional(false);
    }

    @Override
    public Class getStepLayoutClass() {
        return InstructionStepLayout.class;
    }
}
