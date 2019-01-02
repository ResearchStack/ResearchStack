package org.researchstack.backbone.ui.step.body;

import android.content.Context;
import android.support.annotation.StringRes;

import org.researchstack.backbone.R;

public class BodyAnswer {
    public static final BodyAnswer VALID = new BodyAnswer(true, 0);
    public static final BodyAnswer INVALID = new BodyAnswer(false,
            R.string.rsb_invalid_answer_default);

    private boolean isValid;
    private int reason;
    private String[] params;

    public BodyAnswer(boolean isValid, @StringRes int reason, String... params) {
        this.isValid = isValid;
        this.reason = reason;
        this.params = params;
    }

    public boolean isValid() {
        return isValid;
    }

    @StringRes
    public int getReason() {
        return reason;
    }

    public String[] getParams() {
        return params;
    }

    public String getString(Context context) {
        if (getParams().length == 0) {
            return context.getString(getReason());
        } else {
            return context.getString(getReason(), getParams());
        }
    }
}
