package co.touchlab.touchkit.rk.ui.fragment;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RadioGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.jakewharton.rxbinding.widget.RxRadioGroup;
import com.jakewharton.rxbinding.widget.RxTextView;

import java.text.DateFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.User;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpGeneralInfoStepFragment extends StepFragment
{
    private Calendar birthdate = new GregorianCalendar(1985,
            Calendar.OCTOBER,
            15);
    private AppCompatTextView birthdateTextView;
    private User user;

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

        user = AppDelegate.getInstance()
                .getCurrentUser();

        AppCompatEditText name = (AppCompatEditText) root.findViewById(R.id.name);
        name.setText(user.getName());
        RxTextView.textChanges(name)
                .subscribe(charSequence -> user.setName(charSequence.toString()));

        AppCompatEditText email = (AppCompatEditText) root.findViewById(R.id.email);
        email.setText(user.getEmail());
        RxTextView.textChanges(email)
                .subscribe(charSequence -> user.setEmail(charSequence.toString()));

        ImageView profileImage = (ImageView) root.findViewById(R.id.profile_image);
        RxView.clicks(profileImage)
                .subscribe(view -> launchImagePicker());

        AppCompatEditText password = (AppCompatEditText) root.findViewById(R.id.password);
        RxTextView.textChanges(password)
                .subscribe(charSequence -> user.setPassword(charSequence.toString()));

        birthdateTextView = (AppCompatTextView) root.findViewById(R.id.birthdate);
        if (user.getBirthDate() != null)
        {
            birthdate.setTime(user.getBirthDate());
        }
        updateBirthDate();
        RxView.clicks(birthdateTextView)
                .subscribe(view -> {
                    showDatePicker();
                });

        // TODO shouldn't use strings like this, let's make a gender class or something
        RadioGroup genderRadioGroup = (RadioGroup) root.findViewById(R.id.gender_radio_group);
        String biologicalSex = user.getBiologicalSex();
        if (biologicalSex != null)
        {
            if (biologicalSex.equals("Male"))
            {
                ((AppCompatRadioButton) root.findViewById(R.id.male_radio)).setChecked(true);
            }
            else if (biologicalSex.equals("Female"))
            {
                ((AppCompatRadioButton) root.findViewById(R.id.female_radio)).setChecked(true);
            }
        }

        RxRadioGroup.checkedChanges(genderRadioGroup)
                .subscribe((checkedId) -> user.setBiologicalSex(checkedId == R.id.male_radio ? "Male" : "Female"));

        AppCompatTextView dataInstructions = (AppCompatTextView) root.findViewById(R.id.data_instructions);


        return root;
    }

    private void launchImagePicker()
    {
        Toast.makeText(getActivity(),
                "TODO: launch image picker",
                Toast.LENGTH_SHORT)
                .show();
    }

    private void showDatePicker()
    {
        new DatePickerDialog(getActivity(),
                (datePickerView, year, monthOfYear, dayOfMonth) -> {
                    birthdate.set(year,
                            monthOfYear,
                            dayOfMonth);
                    user.setBirthDate(birthdate.getTime());
                    updateBirthDate();
                },
                birthdate.get(Calendar.YEAR),
                birthdate.get(Calendar.MONTH),
                birthdate.get(Calendar.DAY_OF_MONTH))
                .show();
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
