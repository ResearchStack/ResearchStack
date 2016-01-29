package co.touchlab.researchstack.core.ui.step.body;

import android.app.AlertDialog;
import android.content.res.Resources;
import android.support.v7.widget.AppCompatCheckBox;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioGroup;
import android.widget.TextView;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.model.Choice;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.QuestionStep;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.views.NoShowImeEditText;
import co.touchlab.researchstack.core.utils.ViewUtils;

public class MultiChoiceQuestionBody <T> implements StepBody
{
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep    step;
    private StepResult<T[]> result;
    private ChoiceAnswerFormat format;
    private Choice<T>[]        choices;
    private Set<T>          currentSelected;

    public MultiChoiceQuestionBody(Step step, StepResult result)
    {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step.getIdentifier()) : result;
        this.format = (ChoiceAnswerFormat) this.step.getAnswerFormat();
        this.choices = format.getChoices();

        // Restore results
        currentSelected = new HashSet<>();

        T[] resultArray = this.result.getResult();
        if(resultArray != null && resultArray.length > 0)
        {
            currentSelected.addAll(Arrays.asList(resultArray));
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent)
    {
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
        RadioGroup radioGroup = new RadioGroup(inflater.getContext());

        for(int i = 0; i < choices.length; i++)
        {
            Choice<T> item = choices[i];

            // Create & add the View to our body-view
            AppCompatCheckBox checkBox = (AppCompatCheckBox) inflater.inflate(R.layout.item_checkbox,
                    radioGroup,
                    false);
            checkBox.setText(item.getText());
            checkBox.setId(i);
            radioGroup.addView(checkBox);

            // Set initial state
            if(currentSelected.contains(item.getValue()))
            {
                checkBox.setChecked(true);
            }

            // Update result when value changes
            checkBox.setOnCheckedChangeListener((buttonView, isChecked) -> {

                if(isChecked)
                {
                    currentSelected.add(item.getValue());
                }
                else
                {
                    currentSelected.remove(item.getValue());
                }
            });
        }

        return radioGroup;
    }

    private View initViewCompact(LayoutInflater inflater, ViewGroup parent)
    {
        View compactView = inflater.inflate(R.layout.item_edit_text, parent, false);

        TextView label = (TextView) compactView.findViewById(R.id.text);
        label.setVisibility(View.VISIBLE);
        label.setText(step.getTitle());

        NoShowImeEditText editText = (NoShowImeEditText) compactView.findViewById(R.id.value);
        editText.setOnFocusChangeListener((v, hasFocus) -> {
            if(hasFocus)
            {
                ViewUtils.hideSoftInputMethod(editText.getContext());

                showDialog(editText, step.getTitle());
            }
        });


        editText.setIsTextEdittingEnalbed(false);

        return compactView;
    }

    @Override
    public StepResult getStepResult()
    {
        result.setResult((T[]) currentSelected.toArray());
        return result;
    }

    @Override
    public boolean isAnswerValid()
    {
        return ! currentSelected.isEmpty();
    }

    private void showDialog(EditText editText, String title)
    {
        // TODO use same view as getBodyView() and just set the dialog's view to it?
        // TODO use current result to precheck items
        // TODO improve this whole result/dialog logic
        boolean[] checkedItems = new boolean[format.getChoices().length];
        new AlertDialog.Builder(editText.getContext()).setMultiChoiceItems(format.getTextChoiceNames(),
                checkedItems,
                (dialog, which, isChecked) -> {
                    checkedItems[which] = isChecked;
                }).setTitle(title).setPositiveButton(R.string.src_ok, (dialog, which) -> {
            currentSelected.clear();
            for(int i = 0; i < checkedItems.length; i++)
            {
                if(checkedItems[i])
                {
                    Choice<T> choice = choices[i];
                    currentSelected.add(choice.getValue());
                }
            }

            //TODO format from strings.xml
            // Set result to our text view
            editText.setText(currentSelected.size() + " options picked");

            // Search for next focusable view request focus
            View next = editText.getParent().focusSearch(editText, View.FOCUS_DOWN);
            if(next != null)
            {
                next.requestFocus();
            }
            else
            {
                ViewUtils.hideSoftInputMethod(editText.getContext());
            }

        }).setNegativeButton(R.string.src_cancel, null).show();
    }
}