package co.touchlab.touchkit.rk.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.RadioGroup;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpGeneralInfoStepFragment extends StepFragment
{
    private Calendar birthdate = new GregorianCalendar(1985, Calendar.OCTOBER, 15);
    private AppCompatTextView birthdateTextView;

    public SignUpGeneralInfoStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpGeneralInfoStepFragment fragment = new SignUpGeneralInfoStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_general_info,
                null);

        AppCompatEditText name = (AppCompatEditText) root.findViewById(R.id.name);
        AppCompatEditText email = (AppCompatEditText) root.findViewById(R.id.email);
        ImageView profileImage = (ImageView) root.findViewById(R.id.profile_image);
        AppCompatEditText password = (AppCompatEditText) root.findViewById(R.id.password);
        birthdateTextView = (AppCompatTextView) root.findViewById(R.id.birthdate);
        updateBirthDate();
        birthdateTextView.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v)
            {
                new DatePickerDialog(getActivity(),
                        new DatePickerDialog.OnDateSetListener()
                        {
                            @Override
                            public void onDateSet(DatePicker view, int year, int monthOfYear, int dayOfMonth)
                            {
                                birthdate.set(year,
                                        monthOfYear,
                                        dayOfMonth);
                                updateBirthDate();
                            }
                        },
                        birthdate.get(Calendar.YEAR),
                        birthdate.get(Calendar.MONTH),
                        birthdate.get(Calendar.DAY_OF_MONTH))
                    .show();
            }
        });
        RadioGroup genderRadioGroup = (RadioGroup) root.findViewById(R.id.gender_radio_group);
        AppCompatTextView dataInstructions = (AppCompatTextView) root.findViewById(R.id.data_instructions);


        return root;
    }

    private void updateBirthDate()
    {
        DateFormat format = DateFormat.getDateInstance(DateFormat.MEDIUM,
                Locale.getDefault());
        birthdateTextView.setText(format.format(birthdate.getTime()));
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
