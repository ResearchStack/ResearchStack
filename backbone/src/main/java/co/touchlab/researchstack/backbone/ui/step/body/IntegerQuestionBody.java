package co.touchlab.researchstack.backbone.ui.step.body;

import android.content.Context;
import android.content.res.Resources;
import android.text.InputFilter;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import co.touchlab.researchstack.backbone.R;
import co.touchlab.researchstack.backbone.answerformat.IntegerAnswerFormat;
import co.touchlab.researchstack.backbone.result.StepResult;
import co.touchlab.researchstack.backbone.step.QuestionStep;
import co.touchlab.researchstack.backbone.step.Step;
import co.touchlab.researchstack.backbone.utils.ViewUtils;

public class IntegerQuestionBody implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep        step;
    private StepResult<Integer> result;
    private IntegerAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int      viewType;
    private EditText editText;

    public IntegerQuestionBody(Step step, StepResult result)
    {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
        this.format = (IntegerAnswerFormat) this.step.getAnswerFormat();
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        this.viewType = viewType;

        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsc_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsc_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
        if(viewType == VIEW_TYPE_DEFAULT)
        {
            return initViewDefault(inflater, parent);
        }
        else if(viewType == VIEW_TYPE_COMPACT)
        {
            return initViewCompact(inflater, parent);
        }
        else
        {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initViewDefault(LayoutInflater inflater, ViewGroup parent)
    {
        editText = (EditText) inflater.inflate(R.layout.item_edit_text, parent, false);

        setFilters(parent.getContext());

        return editText;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent)
    {
        View formItemView = inflater.inflate(R.layout.compact_item_edit_text, parent, false);

        TextView title = (TextView) formItemView.findViewById(R.id.label);
        title.setText(step.getTitle());

        editText = (EditText) formItemView.findViewById(R.id.value);
        setFilters(parent.getContext());

        return formItemView;
    }

    private void setFilters(Context context)
    {
        editText.setSingleLine(true);
        final int minValue = format.getMinValue();
        // allow any positive int if no max value is specified
        final int maxValue = format.getMaxValue() == 0 ? Integer.MAX_VALUE : format.getMaxValue();

        if(maxValue == Integer.MAX_VALUE)
        {
            editText.setHint(context.getString(R.string.rsc_hint_step_body_int_no_max));
        }
        else
        {
            editText.setHint(context.getString(R.string.rsc_hint_step_body_int,
                    minValue,
                    maxValue));
        }

        editText.setInputType(EditorInfo.TYPE_CLASS_NUMBER);

        if(result.getResult() != null)
        {
            editText.setText(String.valueOf(result.getResult()));
        }

        String minStr = Integer.toString(minValue);
        String maxStr = Integer.toString(maxValue);
        int maxLength = maxStr.length() >= minStr.length() ? maxStr.length() : minStr.length();
        InputFilter.LengthFilter maxLengthFilter = new InputFilter.LengthFilter(maxLength);
        InputFilter[] newFilters = ViewUtils.addFilter(editText.getFilters(), maxLengthFilter);
        editText.setFilters(newFilters);

        // If we have a range, set a range filter
        if(maxValue - minValue > 0)
        {
            InputFilter rangeFilter = (source, start, end, dest, dstart, dend) -> {

                // If the source its empty, just continue, its probably a backspace
                if(TextUtils.isEmpty(source.toString()))
                {
                    return source;
                }

                // If the dest is empty and the incoming char isn't a digit, let it pass. Its
                // probably a negative sign
                if(dest.length() == 0 && ! TextUtils.isDigitsOnly(source))
                {
                    return source;
                }

                // Append source to dest and check the range.
                String valueStr = new StringBuilder(dest).append(source)
                        .toString()
                        .replaceAll("\\D", "");
                int value = Integer.parseInt(valueStr);

                if(value > maxValue || value < minValue)
                {
                    return "";
                }
                else
                {
                    return source;
                }
            };

            newFilters = ViewUtils.addFilter(editText.getFilters(), rangeFilter);
            editText.setFilters(newFilters);
        }
    }

    @Override
    public StepResult getStepResult()
    {
        String numString = editText.getText().toString();
        if(! TextUtils.isEmpty(numString))
        {
            result.setResult(Integer.valueOf(editText.getText().toString()));
        }
        return result;
    }

    @Override
    public boolean isAnswerValid()
    {
        Integer result = null;

        if(editText != null && editText.getText().length() > 0)
        {
            result = Integer.valueOf(editText.getText().toString());
        }

        if(result == null)
        {
            return false;
        }

        return result >= format.getMinValue() && result <= format.getMaxValue();
    }

}
