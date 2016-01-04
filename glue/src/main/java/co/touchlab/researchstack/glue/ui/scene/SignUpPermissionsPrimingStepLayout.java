package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.ui.step.layout.StepLayoutImpl;
import co.touchlab.researchstack.glue.R;

/**
 * TODO Implement Function
 */
public class SignUpPermissionsPrimingStepLayout extends StepLayoutImpl
{

    public SignUpPermissionsPrimingStepLayout(Context context)
    {
        super(context);
    }

    public SignUpPermissionsPrimingStepLayout(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpPermissionsPrimingStepLayout(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_permission_priming, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        TextView priming = (TextView) body.findViewById(R.id.priming_body);
        //        body.setSummary();
    }

}
