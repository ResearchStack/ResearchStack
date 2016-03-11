package org.researchstack.backbone.ui.step.body;
import android.support.annotation.StringRes;

import org.researchstack.backbone.R;

public class BodyAnswer
{
    public static final BodyAnswer VALID   = new BodyAnswer(true, 0);
    public static final BodyAnswer INVALID = new BodyAnswer(false,  R.string.rsb_invalid_answer_default);

    private boolean isValid;
    private int     reason;

    private BodyAnswer()
    {
    }

    public BodyAnswer(boolean isValid, @StringRes int reason)
    {
        this.isValid = isValid;
        this.reason = reason;
    }

    public boolean isValid()
    {
        return isValid;
    }

    @StringRes
    public int getReason()
    {
        return reason;
    }
}
