package org.researchstack.backbone.ui.step.body;

import android.content.Context;
import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.ContextCompat;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.MultipleDropDownAnswerFormat;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.StringTokenizer;

import static android.widget.LinearLayout.MarginLayoutParams;
import static android.widget.LinearLayout.VERTICAL;

/**
 * Implementation of the question type "duration".
 * <p>
 * This is an example of the JSON used in the definition of the question
 * "constraints":{
 * "durationUnit":"minutes",
 * "step":15,
 * "dataType":"duration",
 * "type":"DurationConstraints"
 * }
 * the "durationUnit" and "step" are not used in this implementation.
 * <p>
 * The answer to this question (the StepResult) is given as the total number of minutes.
 */
public class MultipleDropDownQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    //Total number of minutes
    private StepResult<String> result;
    private String[] strList1;
    private String[] strList2;
    private String[] strList3;
    private MultipleDropDownAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int viewType;
    private Spinner spValues;
    // private StringBuilder currentSelected = new StringBuilder("");
    HashMap<Integer, String> selectedValues = new HashMap<>();
    private boolean isFirst = true;

    public MultipleDropDownQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (MultipleDropDownAnswerFormat) this.step.getAnswerFormat();
        this.strList1 = format.getList1();
        this.strList2 = format.getList2();
        this.strList3 = format.getList3();
        // Restore results
//        String resultValue = this.result.getResult();
//        if (resultValue != null) {
//            currentSelected.append(resultValue);
//        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        this.viewType = viewType;

        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        layoutParams.leftMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_left);
        layoutParams.rightMargin = res.getDimensionPixelSize(R.dimen.rsb_margin_right);
        view.setLayoutParams(layoutParams);

        return view;
    }

    private View getViewForType(int viewType, LayoutInflater inflater, ViewGroup parent) {
        if ((viewType == VIEW_TYPE_DEFAULT) || (viewType == VIEW_TYPE_COMPACT)) {
            return initView(inflater, parent);
        } else {
            throw new IllegalArgumentException("Invalid View Type");
        }
    }

    private View initView(LayoutInflater inflater, ViewGroup parent) {
        //View v = inflater.inflate(R.layout.rsb_item_drop_down, parent, false);
        LinearLayout linearParent = new LinearLayout(inflater.getContext());
        linearParent.setLayoutParams(new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT));
        linearParent.setOrientation(VERTICAL);

        LinearLayout linearLayout = new LinearLayout(inflater.getContext());
        linearLayout.setWeightSum(1);
        int first = strList1.length;
        int second = strList2.length;
        int third = strList3.length;

        if (first > 0 && second > 0 && third > 0) {
            linearLayout.addView(getSpinnerViews(inflater.getContext(), strList1, 1, 0.33f));
            linearLayout.addView(getSpinnerViews(inflater.getContext(), strList2, 2, 0.33f));
            linearLayout.addView(getSpinnerViews(inflater.getContext(), strList3, 3, 0.33f));
        } else {
            linearLayout.addView(getSpinnerViews(inflater.getContext(), strList1, 1, 0.5f));
            linearLayout.addView(getSpinnerViews(inflater.getContext(), strList2, 2, 0.5f));
        }

        TextView title = new TextView(inflater.getContext());
        title.setTextColor(ContextCompat.getColor(inflater.getContext(), R.color.rsb_colorAccent));
        title.setTextSize(TypedValue.COMPLEX_UNIT_DIP, 16);
        if (viewType == VIEW_TYPE_COMPACT) {
            if (step.isOptional()) {
                title.setText(step.getTitle());
            } else {
                title.setText(step.getTitle() + " *");
            }
        } else {
            title.setVisibility(View.GONE);
        }
        linearParent.addView(title);
        linearParent.addView(linearLayout);
        return linearParent;
    }


    private View getSpinnerViews(Context context, String[] strList, int index, float v) {
        LayoutInflater vi = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = vi.inflate(R.layout.rsb_item_multiple_drop_down, null);

        LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT, v);
        layoutParams.rightMargin = 20;
        ArrayAdapter<String> hoursChoices = new ArrayAdapter<String>(context, android.R.layout.simple_spinner_item, strList) {
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return textView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if (position == 0) {
                    textView.setTextColor(Color.GRAY);
                } else {
                    textView.setTextColor(Color.BLACK);
                }
                return textView;
            }
        };
        hoursChoices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spValues = (Spinner) view.findViewById(R.id.spValues);
        spValues.setAdapter(hoursChoices);


        String result = this.result.getResult();
        if (result != null) {
            int ind = 1;
            StringTokenizer st2 = new StringTokenizer(result, ",");
            while (st2.hasMoreElements()) {
                String element = st2.nextElement().toString();
                if (ind == index) {
                    spValues.setSelection(getIndex(spValues, element));
                    break;
                }
                ind++;
            }
        }


        spValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if (i == 0) {
                } else {
                    selectedValues.put(index, adapterView.getItemAtPosition(i).toString());
                }

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        view.setLayoutParams(layoutParams);
        return view;
    }

    private int getIndex(Spinner spinner, String myString) {
        for (int i = 0; i < spinner.getCount(); i++) {
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)) {
                return i;
            }
        }

        return 0;
    }

    @Override
    public StepResult getStepResult(boolean skipped) {
        if (skipped) {
            result.setResult(null);
        } else {
            // currentSelected.append(spValues.getSelectedItem().toString());
            if (selectedValues.size() > 1) {
                entriesSortedByValues(selectedValues);
                Iterator myVeryOwnIterator = selectedValues.values().iterator();
                StringBuilder stringBuilder = new StringBuilder();
                while (myVeryOwnIterator.hasNext()) {
                    stringBuilder.append(myVeryOwnIterator.next() + ",");
                }
                String str = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                result.setResult(str);
            } else {
                if (step.isOptional()) {
                    result.setResult("");
                } else {
                    if (selectedValues.size() > 1) {
                        entriesSortedByValues(selectedValues);
                        Iterator myVeryOwnIterator = selectedValues.values().iterator();
                        StringBuilder stringBuilder = new StringBuilder();
                        while (myVeryOwnIterator.hasNext()) {
                            stringBuilder.append(myVeryOwnIterator.next() + ",");
                        }
                        String str = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);
                        result.setResult(str);
                    }
                }
            }
        }

        return result;
    }

    static <K, V extends Comparable<? super V>> List<Map.Entry<Integer, String>> entriesSortedByValues(Map<Integer, String> map) {

        List<Map.Entry<Integer, String>> sortedEntries = new ArrayList<>(map.entrySet());

        Collections.sort(sortedEntries, (e1, e2) -> e2.getKey().compareTo(e1.getKey()));

        return sortedEntries;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {
        Iterator myVeryOwnIterator = selectedValues.values().iterator();
        StringBuilder stringBuilder = new StringBuilder();
        while (myVeryOwnIterator.hasNext()) {
            stringBuilder.append(myVeryOwnIterator.next() + ",");
        }
        if (stringBuilder.toString().equals("")) {
            if (step.isOptional()) {
                return BodyAnswer.VALID;
            } else {
                return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
            }

        } else {
            String str = stringBuilder.toString().substring(0, stringBuilder.toString().length() - 1);

            int ind = 0;
            StringTokenizer st2 = new StringTokenizer(str, ",");
            while (st2.hasMoreElements()) {
                String element = st2.nextElement().toString();
                if (!element.equals("")) {
                    ind++;
                }
            }

            int l1 = strList1.length;
            int l2 = strList2.length;
            int l3 = strList3.length;

            if (l1 > 0 && l2 > 0 && l3 > 0) {
                if (ind == 3) {
                    return BodyAnswer.VALID;
                } else {
                    if (step.isOptional()) {
                        return BodyAnswer.VALID;
                    } else {
                        return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
                    }
                }
            } else if (l1 > 0 && l2 > 0) {
                if (ind == 2) {
                    return BodyAnswer.VALID;
                } else {
                    if (step.isOptional()) {
                        return BodyAnswer.VALID;
                    } else {
                        return new BodyAnswer(false, R.string.rsb_invalid_answer_choice);
                    }
                }
            }


            return BodyAnswer.VALID;
        }
    }

}
