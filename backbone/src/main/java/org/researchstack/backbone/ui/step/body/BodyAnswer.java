package org.researchstack.backbone.ui.step.body;

import android.content.Context;
import androidx.annotation.StringRes;

import org.researchstack.backbone.R;
import org.researchstack.backbone.utils.LocalizationUtils;

public class BodyAnswer {
    public static final BodyAnswer VALID = new BodyAnswer(true, 0);
    public static final BodyAnswer INVALID = new BodyAnswer(false, R.string.rsb_invalid_answer_default);

    private boolean isValid;
    private int reason;
    private String reasonStr;
    private String[] params;

    public BodyAnswer(boolean isValid, @StringRes int reason, String... params) {
        this.isValid = isValid;
        this.reason = reason;
        this.params = params;
    }

    public BodyAnswer(boolean isValid, @StringRes int reason) {
        this.isValid = isValid;
        this.reason = reason;
        this.params = new String[0];
    }

    public BodyAnswer(boolean isValid, String reason) {
        this.isValid = isValid;
        this.reasonStr = reason;
        this.params = new String[0];
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
        if (reasonStr != null && !reasonStr.isEmpty()) {
            return reasonStr;
        } else if (getParams().length == 0) {
            return LocalizationUtils.getLocalizedString(context, getReason());
        } else {
            return LocalizationUtils.getLocalizedString(context, getReason(), (Object[]) getParams());
        }
    }
}
