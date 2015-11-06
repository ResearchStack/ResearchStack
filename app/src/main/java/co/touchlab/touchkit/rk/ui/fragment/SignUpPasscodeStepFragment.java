package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.touchkit.rk.AppDelegate;
import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.model.User;
import co.touchlab.touchkit.rk.common.result.QuestionResult;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.Step;

public class SignUpPasscodeStepFragment extends StepFragment
{
    private static final int STATE_ENTRY = 0;
    private static final int STATE_CONFIRM = 1;

    private AppCompatTextView instructions;
    private AppCompatEditText passcode;

    private int state;
    private String enteredPasscode;

    public SignUpPasscodeStepFragment()
    {
        super();
    }

    public static Fragment newInstance(Step step)
    {
        SignUpPasscodeStepFragment fragment = new SignUpPasscodeStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP,
                step);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        View root = inflater.inflate(R.layout.item_passcode,
                null);

        instructions = (AppCompatTextView) root.findViewById(R.id.passcode_instructions);
        passcode = (AppCompatEditText) root.findViewById(R.id.passcode);

        state = STATE_ENTRY;
        updateViews();

        RxTextView.textChanges(passcode)
                .filter(charSequence -> charSequence.length() == 4)
                .subscribe(this::handlePasscode);

        hideNextButtons();

        return root;
    }

    private void handlePasscode(CharSequence passcodeSequence)
    {
        if (state == STATE_ENTRY)
        {
            enteredPasscode = passcodeSequence.toString();
            state = STATE_CONFIRM;
            updateViews();
        }
        else if (state == STATE_CONFIRM && enteredPasscode.equals(passcodeSequence.toString()))
        {
            User currentUser = AppDelegate.getInstance()
                    .getCurrentUser();

            currentUser.setPasscode(enteredPasscode);
            currentUser.setSignedUp(true);
            currentUser.setSignedIn(true);


            AppDelegate.getInstance()
                    .saveUser(getContext());
            callbacks.onNextPressed(step);
        }
        else
        {
            // restart pin entry
            enteredPasscode = null;
            state = STATE_ENTRY;
            updateViews();
            Toast.makeText(getActivity(),
                    R.string.passcode_mismatch,
                    Toast.LENGTH_SHORT)
                    .show();
        }

    }

    private void updateViews()
    {
        if (state == STATE_ENTRY)
        {
            instructions.setText(R.string.passcode_instructions);
            passcode.setText("");
        }
        else if (state == STATE_CONFIRM)
        {
            instructions.setText(R.string.passcode_instructions_confirmation);
            passcode.setText("");
        }
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return new StepResult<QuestionResult<Boolean>>(stepIdentifier);
    }
}
