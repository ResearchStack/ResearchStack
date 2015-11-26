package co.touchlab.researchstack.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.support.v7.widget.AppCompatTextView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.AppPrefs;
import co.touchlab.researchstack.R;
import co.touchlab.researchstack.ResearchStackApplication;
import co.touchlab.researchstack.common.model.User;
import co.touchlab.researchstack.common.result.QuestionResult;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.common.storage.SecurityProfile;
import co.touchlab.researchstack.common.step.Step;

public class SignUpPasscodeScene extends Scene
{
    private static final int STATE_ENTRY = 0;
    private static final int STATE_CONFIRM = 1;

    private AppCompatTextView instructions;
    private AppCompatEditText passcode;

    private int state;
    private String enteredPasscode;


    public SignUpPasscodeScene(Context context, Step step)
    {
        super(context, step);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_passcode, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        instructions = (AppCompatTextView) body.findViewById(R.id.passcode_instructions);
        passcode = (AppCompatEditText) body.findViewById(R.id.passcode);

        state = STATE_ENTRY;
        updateViews();

        SecurityProfile securityProfile = ResearchStackApplication.getInstance()
                                                                  .getSecurityProfile();

        RxTextView.textChanges(passcode)
                .filter(charSequence -> charSequence.length() == securityProfile.getMinLength())
                .subscribe(this :: handlePasscode);

        hideNextButtons();
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
            ResearchStackApplication researchStackApplication = ResearchStackApplication.getInstance();
            User currentUser = researchStackApplication
                    .getCurrentUser();

            currentUser.setPasscode(enteredPasscode);
            currentUser.setSignedUp(true);
            currentUser.setSignedIn(true);


            researchStackApplication
                    .saveUser(getContext());

            researchStackApplication.setEnteredPin(enteredPasscode);
            AppPrefs.getInstance(getContext()).setAppPinEncoded(true);

            getCallbacks().onNextPressed(getStep());
        }
        else
        {
            // restart pin entry
            enteredPasscode = null;
            state = STATE_ENTRY;
            updateViews();
            Toast.makeText(getContext(),
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
