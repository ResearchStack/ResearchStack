package co.touchlab.researchstack.glue.ui.scene;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.jakewharton.rxbinding.widget.RxTextView;

import co.touchlab.researchstack.core.ui.scene.SceneImpl;
import co.touchlab.researchstack.glue.R;
import co.touchlab.researchstack.glue.ResearchStack;
import co.touchlab.researchstack.glue.model.User;
import co.touchlab.researchstack.glue.ui.views.HeightPicker;

public class SignUpAdditionalInfoScene extends SceneImpl
{
    private User user;

    public SignUpAdditionalInfoScene(Context context)
    {
        super(context);
    }

    public SignUpAdditionalInfoScene(Context context, AttributeSet attrs)
    {
        super(context, attrs);
    }

    public SignUpAdditionalInfoScene(Context context, AttributeSet attrs, int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
    }

    @Override
    public View onCreateBody(LayoutInflater inflater, ViewGroup parent)
    {
        return inflater.inflate(R.layout.item_additional_info, parent, false);
    }

    @Override
    public void onBodyCreated(View body)
    {
        super.onBodyCreated(body);

        user = ResearchStack.getInstance()
                .getCurrentUser();

        HeightPicker height = (HeightPicker) body.findViewById(R.id.height);
        height.setUserHeight(user.getHeight());
        height.getObservable()
                .subscribe(user::setHeight);

        AppCompatEditText weight = (AppCompatEditText) body.findViewById(R.id.weight);
        int userWeight = user.getWeight();
        if(userWeight > 0)
        {
            weight.setText(String.valueOf(userWeight));
        }
        RxTextView.textChanges(weight)
                .filter(input -> input.length() > 0)
                .map(charSequence -> Integer.valueOf(charSequence.toString()))
                .subscribe(user::setWeight);
    }

}
