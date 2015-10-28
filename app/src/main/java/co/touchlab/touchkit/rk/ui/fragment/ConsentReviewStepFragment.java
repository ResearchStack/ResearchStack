package co.touchlab.touchkit.rk.ui.fragment;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import com.jakewharton.rxbinding.view.RxView;
import com.joanzapata.pdfview.PDFView;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.result.StepResult;
import co.touchlab.touchkit.rk.common.step.ConsentReviewStep;

public class ConsentReviewStepFragment extends StepFragment
{

    public ConsentReviewStepFragment()
    {
        super();
    }

    public static Fragment newInstance(ConsentReviewStep step)
    {
        ConsentReviewStepFragment fragment = new ConsentReviewStepFragment();
        Bundle args = new Bundle();
        args.putSerializable(KEY_QUESTION_STEP, step);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.fragment_step_consent_review, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        //TODO Add Review title
        //TODO Add Review intruction

        PDFView pdfView = (PDFView) view.findViewById(R.id.pdfview);
        pdfView.fromAsset("study_overview_consent_form.pdf").load();        //TODO Point pdf to App-delegate

        View agree = view.findViewById(R.id.agree);
        RxView.clicks(agree).subscribe(v -> showConfirmationDialog());

        View disagree = view.findViewById(R.id.disagree);
        RxView.clicks(disagree).subscribe(v -> closeToWelcomeFlow());
    }

    //TODO Clear activity stack up until OnboardingActivity
    private void closeToWelcomeFlow()
    {
        Toast.makeText(getContext(), "Close and show welcome screen", Toast.LENGTH_SHORT).show();
    }

    private void showConfirmationDialog()
    {
        ConsentReviewStep step = (ConsentReviewStep) getStep();

        new AlertDialog.Builder(getContext(), R.style.AppTheme_Dialog)
            .setTitle(R.string.consent_review_alert_title)
            .setMessage(step.getReasonForConsent()).setCancelable(false)
                .setPositiveButton(R.string.agree, (dialog, which) -> {
                    callbacks.onNextPressed(getStep());
            }).setNegativeButton(R.string.cancel, null)
            .show();
    }

    @Override
    public StepResult createNewStepResult(String stepIdentifier)
    {
        return null;
    }

    @Override
    public View getBodyView(LayoutInflater inflater)
    {
        return null;
    }
}
