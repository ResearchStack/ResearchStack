package org.researchstack.backbone.ui.step.body;

import android.content.res.Resources;
import android.graphics.Color;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.LinearLayout;
import android.widget.Spinner;
import android.widget.TextView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.answerformat.DropDownAnswerFormat;
import org.researchstack.backbone.answerformat.DurationAnswerFormat;
import org.researchstack.backbone.model.Choice;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.QuestionStep;
import org.researchstack.backbone.step.Step;

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
public class DropDownQuestionBody implements StepBody {
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // Constructor Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private QuestionStep step;
    //Total number of minutes
    private StepResult<String> result;
    private String[] strList;
    private DropDownAnswerFormat format;

    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    // View Fields
    //-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*-*
    private int viewType;
    private Spinner spValues;
    private String currentSelected="";
    private boolean isFirst=true;

    public DropDownQuestionBody(Step step, StepResult result) {
        this.step = (QuestionStep) step;
        this.result = result == null ? new StepResult<>(step) : result;
        this.format = (DropDownAnswerFormat) this.step.getAnswerFormat();
        this.strList = format.getList();

        // Restore results
        String resultValue = this.result.getResult();
        if (resultValue != null) {
            for (int i=0;i<strList.length;i++) {
                if (strList[i].equals(resultValue)) {
                    currentSelected = strList[i];
                }
            }
        }
    }

    @Override
    public View getBodyView(int viewType, LayoutInflater inflater, ViewGroup parent) {
        this.viewType = viewType;

        View view = getViewForType(viewType, inflater, parent);

        Resources res = parent.getResources();
        LinearLayout.MarginLayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
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
        View v = inflater.inflate(R.layout.rsb_item_drop_down, parent, false);

        ArrayAdapter<String> hoursChoices = new ArrayAdapter<String>(inflater.getContext(), android.R.layout.simple_spinner_item, strList){
            @Override
            public View getDropDownView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
                TextView textView = (TextView) super.getDropDownView(position, convertView, parent);
                if(position==0) {
                    textView.setTextColor(Color.GRAY);
                }else{
                    textView.setTextColor(Color.BLACK);
                }
                return  textView;
            }

            @Override
            public View getView(int position, View convertView, ViewGroup parent) {
                TextView textView = (TextView) super.getView(position, convertView, parent);
                if(position==0) {
                    textView.setTextColor(Color.GRAY);
                }else{
                    textView.setTextColor(Color.BLACK);
                }
                return  textView;
            }
        };
        hoursChoices.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spValues = (Spinner) v.findViewById(R.id.spValues);
//        spValues.setFocusable(true);
//        spValues.setFocusableInTouchMode(true);
//        spValues.requestFocus();
        spValues.setAdapter(hoursChoices);


//        spValues.setOnFocusChangeListener(new View.OnFocusChangeListener() {
//
//            @Override
//            public void onFocusChange(View v, boolean hasFocus) {
//                if(hasFocus)
//                   spValues.performClick();
//            }
//
//        });


        TextView title = (TextView) v.findViewById(R.id.label);

        if (viewType == VIEW_TYPE_COMPACT) {
            if(step.isOptional()){
                title.setText(step.getTitle());
            }else {
                title.setText(step.getTitle()+" *");
            }
        } else {
            title.setVisibility(View.GONE);
        }


        String result = this.result.getResult();
        if (result != null) {
            spValues.setSelection(getIndex(spValues,result));
        }


        spValues.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                if(i==0) {
                    currentSelected = "";
                }else{
                    currentSelected = adapterView.getItemAtPosition(i).toString();
                }

//                parent.post(new Runnable() {
//                    @Override
//                    public void run() {
//                        spValues.requestFocusFromTouch();
//                    }
//                });

            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });
        return v;
    }

    private int getIndex(Spinner spinner, String myString){
        for (int i=0;i<spinner.getCount();i++){
            if (spinner.getItemAtPosition(i).toString().equalsIgnoreCase(myString)){
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
            currentSelected = spValues.getSelectedItem().toString();
            result.setResult(currentSelected);
        }

        return result;
    }

    @Override
    public BodyAnswer getBodyAnswerState() {

        if (currentSelected.equals("")) {
            if(step.isOptional()){
                return BodyAnswer.VALID; 
            }else {
                return new BodyAnswer(false, R.string.rsb_invalid_answer_choice); 
            }
            
        } else {
            return BodyAnswer.VALID;
        }
    }

}
