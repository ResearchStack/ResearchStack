package co.touchlab.researchstack.core.ui.scene;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.app.AlertDialog;
import android.text.InputType;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.DateAnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextAnswerFormat;
import co.touchlab.researchstack.core.answerformat.ChoiceAnswerFormat;
import co.touchlab.researchstack.core.result.FormResult;
import co.touchlab.researchstack.core.step.FormStep;
import rx.Observable;

public class FormScene extends SceneImpl<FormResult>
{


    public FormScene(Context context)
    {
        super(context);
    }

    public FormScene(Context context, AttributeSet attrs)
    {
        super(context,
                attrs);
    }

    public FormScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context,
                attrs,
                defStyleAttr);
    }

    @Override
    public void initializeScene()
    {
        super.initializeScene();

        setTitle(getStep().getTitle());
        setSummary(getStep().getText());
        setSkip(getStep().isOptional());
    }

    @Override
    public void onSceneCreated(View scene)
    {
        super.onSceneCreated(scene);

        LinearLayout stepViewContainer = (LinearLayout) findViewById(R.id.content_container);

        int startIndex = getPositionToInsertBody();
        List<FormItem> items = ((FormStep) getStep()).getFormItems();
        for (int i = 0, size = items.size(); i < size; i++)
        {
            FormItem item = items.get(i);

            View formItem = LayoutInflater.from(getContext())
                    .inflate(R.layout.fragment_step_form,
                            this,
                            false);

            TextView label = (TextView) formItem.findViewById(R.id.text);
            label.setText(item.text);

            FormResult result = initAndGetResult(item,
                    formItem,
                    i == size - 1);

            stepViewContainer.addView(formItem,
                    startIndex + i);
            getStepResult().setResultForIdentifier(item.identifier,
                    result);
        }
    }

    private FormResult initAndGetResult(FormItem item, View formItem, boolean lastItem)
    {
        FormResult result;
        EditText editText = (EditText) formItem.findViewById(R.id.value);
        editText.setHint(item.placeholder);
        Observable<CharSequence> editTextChanges = RxTextView.textChanges(editText);
        Observable<Object> editTextClicks = RxView.clicks(editText);

        // header
        if (item.format == null)
        {
            editText.setFocusable(false);
            editText.setVisibility(GONE);
            return new FormResult<Void>(item.identifier);
        }


        switch (item.format.getQuestionType())
        {
            case Text:
                result = new FormResult<String>(item.identifier);
                TextAnswerFormat format = (TextAnswerFormat) item.format;
                editText.setSingleLine(!format.isMultipleLines());
                editText.setFilters(format.getInputFilters());
                final FormResult finalResult = result;
                editTextChanges.subscribe(charSequence -> finalResult.setAnswer(charSequence.toString()));
                break;

            case Integer:
                editText.setInputType(InputType.TYPE_CLASS_NUMBER);
                result = new FormResult<Integer>(item.identifier);
                final FormResult finalResult1 = result;
                editTextChanges
                        .filter(charSequence -> charSequence.length() != 0)
                        .map(charSequence -> Integer.valueOf(charSequence.toString()))
                        .subscribe(number -> finalResult1.setAnswer(number));
                break;

            case MultipleChoice:
                ChoiceAnswerFormat choiceFormat = (ChoiceAnswerFormat) item.format;
                result = new FormResult<Integer[]>(item.identifier);
                editText.setFocusable(false);
                editText.setHint(item.placeholder);
                final FormResult<Integer[]> finalResult2 = result;
                boolean[] checkedItems = new boolean[choiceFormat.getChoices().length];
                editTextClicks.subscribe(click -> {
                    new AlertDialog.Builder(getContext())
                            .setMultiChoiceItems(choiceFormat.getTextChoiceNames(),
                                    checkedItems,
                                    (dialog, which, isChecked) -> {
                                        checkedItems[which] = isChecked;
                                    })
                            .setTitle(item.text)
                            .setPositiveButton(R.string.ok,
                                    (dialog, which) -> {
                                        ArrayList<Integer> checkedValues = new ArrayList<Integer>();
                                        for (int i = 0; i < checkedItems.length; i++)
                                        {
                                            if (checkedItems[i])
                                            {
                                                checkedValues.add(i);
                                            }
                                        }
                                        finalResult2.setAnswer(checkedValues.toArray(new Integer[checkedValues.size()]));
                                    })
                            .setNegativeButton(R.string.cancel,
                                    null)
                            .show();
                });
                break;

            case SingleChoice:
                ChoiceAnswerFormat singleChoiceFormat = (ChoiceAnswerFormat) item.format;
                result = new FormResult<Integer[]>(item.identifier);
                editText.setFocusable(false);
                editText.setHint(item.placeholder);
                final FormResult finalResult3 = result;
                int[] checked = new int[1];
                editTextClicks.subscribe(click -> {
                    new AlertDialog.Builder(getContext())
                            .setSingleChoiceItems(singleChoiceFormat.getTextChoiceNames(),
                                    0,
                                    (dialog, which) -> {
                                        checked[0] = which;
                                    })
                            .setTitle(item.text)
                            .setPositiveButton(R.string.ok,
                                    (dialog, which) -> {
                                        finalResult3.setAnswer(checked[0]);
                                    })
                            .setNegativeButton(R.string.cancel,
                                    null)
                            .show();
                });
                break;

            case Date:
                DateAnswerFormat dateFormat = (DateAnswerFormat) item.format;
                result = new FormResult<Date>(item.identifier);
                editText.setFocusable(false);
                editText.setHint(item.placeholder);
                Calendar calendar = new GregorianCalendar();
                editTextClicks.subscribe(click -> {
                    new DatePickerDialog(getContext(),
                            (view, year, monthOfYear, dayOfMonth) -> {
                                calendar.set(year,
                                        monthOfYear,
                                        dayOfMonth);
                                result.setAnswer(calendar.getTime());
                            },
                            calendar.get(Calendar.YEAR),
                            calendar.get(Calendar.MONTH),
                            calendar.get(Calendar.DAY_OF_MONTH)).show();

                });
                break;

            default:
            {
                result = new FormResult<Void>(item.identifier);
                editText.setFocusable(false);
                editText.setHint("Not implemented");
                editTextClicks.subscribe(click -> {
                    Toast.makeText(getContext(),
                            "Need to implement dialog",
                            Toast.LENGTH_SHORT)
                            .show();
                });
            }

        }

        if (lastItem)
        {
            editText.setOnKeyListener((v, keyCode, event) -> {
                if (event != null && event.getAction() == KeyEvent.ACTION_UP && keyCode == KeyEvent.KEYCODE_ENTER)
                {
                    onNextClicked();
                    return true;
                }
                return false;
            });
        }
        return result;
    }

    @Override
    public boolean isAnswerValid()
    {
        boolean isValid = true;

        List<FormItem> items = ((FormStep) getStep()).getFormItems();
        for (FormItem item : items)
        {
            FormResult<String> result = getStepResult()
                    .getResultForIdentifier(item.identifier);
//            String answer = result.getAnswer();
//            if (!item.format.isAnswerValidWithString(answer))
//            {
//                //TODO Move message into xml/strings.xml
//                //TODO Throw dialog instead of toast
//                Toast.makeText(getContext(), "Invalid answer, double check your answers!", Toast.LENGTH_SHORT).show();
//                return false;
//            }
        }

        return isValid && super.isAnswerValid();
    }

    public static class FormItem implements Serializable
    {
        private final String identifier;
        private final String text;
        private final AnswerFormat format;
        private final String placeholder;
        private final boolean optional;

        public FormItem(String identifier, String text, AnswerFormat format, String placeholder, boolean optional)
        {
            this.identifier = identifier;
            this.text = text;
            this.format = format;
            this.placeholder = placeholder;
            this.optional = optional;
        }
    }

}
