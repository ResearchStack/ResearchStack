package co.touchlab.researchstack.core.ui.scene;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

@Deprecated
public class NotImplementedScene extends SceneImpl<String>
{

    public NotImplementedScene(Context context)
    {
        super(context);
    }

    public NotImplementedScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public NotImplementedScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateScene(LayoutInflater inflater, ViewGroup parent)
    {
        TextView textView = new TextView(getContext());
        textView.setText("Not Implemented: " + getStep().getIdentifier());
        return textView;
    }

    @Override
    public void onSceneCreated(View scene)
    {
        //Do Nothing
    }

}
