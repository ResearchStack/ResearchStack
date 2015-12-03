package co.touchlab.researchstack.glue.ui.scene;

import android.app.DatePickerDialog;
import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatRadioButton;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
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

import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStackApplication;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.core.result.QuestionResult;
import co.touchlab.researchstack.core.result.StepResult;
import co.touchlab.researchstack.core.step.Step;
import co.touchlab.researchstack.core.ui.scene.Scene;

public class SignUpGeneralInfoScene extends Scene
{
    private Calendar birthdate;
    private AppCompatTextView birthdateTextView;
    private User user;

    public SignUpGeneralInfoScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public void onPreInitialized()
    {
        super.onPreInitialized();
        birthdate = new GregorianCalendar(1985,
                                          Calendar.OCTOBER,
                                          15);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_general_info, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        super.onBodyCreated(body);
        user = ResearchStackApplication.getInstance()
                .getCurrentUser();

        AppCompatEditText name = (AppCompatEditText) body.findViewById(R.id.name);
        name.setText(user.getName());
        RxTextView.textChanges(name)
                .subscribe(charSequence -> user.setName(charSequence.toString()));

        AppCompatEditText email = (AppCompatEditText) body.findViewById(R.id.email);
        email.setText(user.getEmail());
        RxTextView.textChanges(email)
                .subscribe(charSequence -> user.setEmail(charSequence.toString()));

        ImageView profileImage = (ImageView) body.findViewById(R.id.profile_image);
        RxView.clicks(profileImage)
                .subscribe(view -> launchImagePicker());

        AppCompatEditText password = (AppCompatEditText) body.findViewById(R.id.password);
        RxTextView.textChanges(password)
                .subscribe(charSequence -> user.setPassword(charSequence.toString()));

        birthdateTextView = (AppCompatTextView) body.findViewById(R.id.birthdate);
        if (user.getBirthDate() != null)
        {
            birthdate.setTime(user.getBirthDate());
        }
        else
        {
            user.setBirthDate(birthdate.getTime());
        }
        updateBirthDate();
        RxView.clicks(birthdateTextView)
                .subscribe(view -> {
                    showDatePicker();
                });

        // TODO shouldn't use strings like this, let's make a gender class or something
        RadioGroup genderRadioGroup = (RadioGroup) body.findViewById(R.id.gender_radio_group);
        String biologicalSex = user.getBiologicalSex();
        if (biologicalSex != null)
        {
            if (biologicalSex.equals("Male"))
            {
                ((AppCompatRadioButton) body.findViewById(R.id.male_radio)).setChecked(true);
            }
            else if (biologicalSex.equals("Female"))
            {
                ((AppCompatRadioButton) body.findViewById(R.id.female_radio)).setChecked(true);
            }
        }

        RxRadioGroup.checkedChanges(genderRadioGroup)
                .subscribe((checkedId) -> user.setBiologicalSex(checkedId == R.id.male_radio ? "Male" : "Female"));

        AppCompatTextView dataInstructions = (AppCompatTextView) body.findViewById(R.id.data_instructions);
    }

    private void launchImagePicker()
    {
        Toast.makeText(getContext(),
                "TODO: launch image picker",
                Toast.LENGTH_SHORT)
                .show();
    }

    private void showDatePicker()
    {
        new DatePickerDialog(getContext(),
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
        return new StepResult<Boolean>(stepIdentifier);
    }
}
