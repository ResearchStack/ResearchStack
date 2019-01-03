package org.researchstack.feature.authentication.pincode.ui;

import android.content.Context;
import android.support.annotation.CallSuper;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import org.researchstack.feature.authentication.R;
import org.researchstack.feature.authentication.pincode.PinCodeConfig;
import org.researchstack.feature.authentication.pincode.PinCodeConfigProvider;
import org.researchstack.foundation.components.utils.ViewUtils;

import java.util.Arrays;

public class PinCodeLayout extends RelativeLayout {
    protected InputMethodManager imm;
    protected PinCodeConfig config;

    protected TextView summary;
    protected TextView title;
    protected EditText editText;
    protected View progress;

    public PinCodeLayout(Context context) {
        super(context);
        init();
    }

    public PinCodeLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public PinCodeLayout(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init();
    }

    @CallSuper
    protected void init() {
        config = PinCodeConfigProvider.Companion.getPinCodeConfig();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        LayoutInflater.from(getContext()).inflate(R.layout.rsfa_step_layout_pincode, this, true);

        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.rsfa_pincode_enter_title);

        resetSummaryText();

        editText = (EditText) findViewById(R.id.pincode);
        editText.requestFocus();

        PinCodeConfig.Type pinType = config.getPinType();
        editText.setInputType(pinType.getInputType() | pinType.getVisibleVariationType(false));

        char[] chars = new char[config.getPinLength()];
        Arrays.fill(chars, '◦');
        editText.setHint(new String(chars));

        InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(),
                new InputFilter.LengthFilter(config.getPinLength()));
        filters = ViewUtils.addFilter(filters, config.getPinType().getInputFilter());
        editText.setFilters(filters);

        progress = findViewById(R.id.progress);
    }

    public void resetSummaryText() {
        summary = (TextView) findViewById(R.id.text);
        String characterType = getContext().getString(config.getPinType().getInputTypeStringId());
        String pinCodeInstructions = getContext().getString(R.string.rsfa_pincode_enter_summary,
                config.getPinLength(),
                characterType);
        summary.setText(pinCodeInstructions);
    }

    public void showProgress(boolean show) {
        progress.setVisibility(show ? VISIBLE : GONE);
    }

}
