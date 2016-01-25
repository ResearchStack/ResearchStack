package co.touchlab.researchstack.core.ui.views;
import android.content.Context;
import android.support.annotation.CallSuper;
import android.text.InputFilter;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.util.Arrays;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.StorageManager;
import co.touchlab.researchstack.core.storage.file.auth.AuthDataAccess;
import co.touchlab.researchstack.core.storage.file.auth.PinCodeConfig;
import co.touchlab.researchstack.core.utils.ViewUtils;

public class PinCodeLayout extends RelativeLayout
{
    protected InputMethodManager imm;
    protected PinCodeConfig      config;

    protected TextView summary;
    protected TextView title;
    protected EditText editText;

    public PinCodeLayout(Context context)
    {
        super(context);
        init();
    }

    public PinCodeLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
        init();
    }

    public PinCodeLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        init();
    }

    @CallSuper
    protected void init()
    {
        config = ((AuthDataAccess) StorageManager.getFileAccess()).getPinCodeConfig();
        imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);

        LayoutInflater.from(getContext()).inflate(R.layout.step_layout_pincode, this, true);

        title = (TextView) findViewById(R.id.title);
        title.setText(R.string.rsc_pincode_enter_title);

        summary = (TextView) findViewById(R.id.text);
        summary.setText(R.string.rsc_pincode_enter_summary);

        editText = (EditText) findViewById(R.id.pincode);
        editText.requestFocus();

        PinCodeConfig.Type pinType = config.getPinType();
        editText.setInputType(pinType.getInputType() | pinType.getVisibleVariationType(false));
        editText.setKeyListener(pinType.getDigitsKeyListener());

        char[] chars = new char[config.getPinLength()];
        Arrays.fill(chars, '◦');
        editText.setHint(new String(chars));

        // Must come after pin-type code as calling setKeyListener(null) sets InputFilters to null
        InputFilter[] filters = ViewUtils.addFilter(editText.getFilters(),
                new InputFilter.LengthFilter(config.getPinLength()));
        editText.setFilters(filters);
    }

}
