package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;

/**
 * TODO Implement Function
 */
public class SignUpPermissionsPrimingScene extends SceneImpl
{

    public SignUpPermissionsPrimingScene(Context context)
    {
        super(context);
    }

    public SignUpPermissionsPrimingScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpPermissionsPrimingScene(Context context, AttributeSet attrs, int defStyleAttr)
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
