package co.touchlab.researchstack.ui.scene;
import android.content.Context;
import android.content.Intent;
import android.graphics.PorterDuff;
import android.text.TextUtils;

import co.touchlab.researchstack.R;
import co.touchlab.researchstack.common.model.ConsentSection;
import co.touchlab.researchstack.common.result.StepResult;
import co.touchlab.researchstack.ui.ViewWebDocumentActivity;
import co.touchlab.researchstack.utils.ViewUtils;

public class ConsentVisualSectionScene extends Scene
{

    public ConsentVisualSectionScene(Context context, ConsentSection data)
    {
        super(context, null);

        int accentColor = ViewUtils.fetchAccentColor(getContext());

        String title = TextUtils.isEmpty(data.getTitle()) ?
                getResources().getString(data.getType().getTitleResId()) : data.getTitle();
        setTitle(title);

        if (!TextUtils.isEmpty(data.getSummary()))
        {
            setSummary(data.getSummary());
        }

        String imageName = !TextUtils.isEmpty(data.getCustomImageName()) ? data.getCustomImageName() :
                data.getType().getImageName();
        if (!TextUtils.isEmpty(imageName))
        {
            int imageResId = getResources().getIdentifier(imageName, "drawable", getContext().getPackageName());
            setImage(imageResId);
            getImageView().setColorFilter(accentColor, PorterDuff.Mode.ADD);
        }

        setMoreInfo(data.getType().getMoreInfoResId(), v -> {
            String path = data.getHtmlContent();
            String webTitle = getResources().getString(R.string.consent_section_more_info);
            Intent webDoc = ViewWebDocumentActivity.newIntent(getContext(), webTitle, path);
            getContext().startActivity(webDoc);
        });
        getMoreInfo().setTextColor(accentColor);

        setSkip(false);

        //        TODO self.continueSkipContainer.continueEnabled = YES;
        //        TODO [self.continueSkipContainer updateContinueAndSkipEnabled];
    }

    @Override
    public StepResult createNewStepResult(String id)
    {
        // We can ignore this, as we don't return a result for a visual consent scene
        return null;
    }

}
