package co.touchlab.researchstack.core.step;
import android.content.res.Resources;
import android.text.TextUtils;

import co.touchlab.researchstack.core.R;
import co.touchlab.researchstack.core.answerformat.AnswerFormat;
import co.touchlab.researchstack.core.answerformat.TextChoiceAnswerFormat;
import co.touchlab.researchstack.core.dev.DevUtils;
import co.touchlab.researchstack.core.model.DocumentProperties;
import co.touchlab.researchstack.core.model.TextChoice;
import co.touchlab.researchstack.core.ui.scene.ConsentSharingScene;

public class ConsentSharingStep extends QuestionStep
{

    private final String localizedLearnMoreHTMLContent;

    /**
     *
     * @param r TODO passing in resources makes me uneasy
     */
    public ConsentSharingStep(String identifier, Resources r, DocumentProperties properties)
    {
        super(identifier);
        super.setOptional(false);
        super.setShowsProgress(false);
        super.setUseSurveyMode(false);

        String investigatorShortDescription = properties.getInvestigatorShortDescription();
        if (TextUtils.isEmpty(investigatorShortDescription)){
            DevUtils.throwIllegalArgumentException();
        }

        String investigatorLongDescription = properties.getInvestigatorLongDescription();
        if (TextUtils.isEmpty(investigatorLongDescription)){
            DevUtils.throwIllegalArgumentException();
        }

        localizedLearnMoreHTMLContent = properties.getHtmlContent();
        if (TextUtils.isEmpty(localizedLearnMoreHTMLContent)){
            DevUtils.throwIllegalArgumentException();
        }

        super.setAnswerFormat(
                new TextChoiceAnswerFormat(AnswerFormat.ChoiceAnswerStyle.SingleChoice,
                                           new TextChoice[] {new TextChoice(
                                                   r.getString(R.string.consent_share_widely,
                                                               investigatorLongDescription), true,
                                                   null), new TextChoice(
                                                   r.getString(R.string.consent_share_only,
                                                               investigatorShortDescription), false,
                                                   null)

                                           }));

        super.setTitle(r.getString(R.string.consent_share_title));
        super.setText(r.getString(R.string.consent_share_description, investigatorLongDescription));
    }

    @Override
    public int getSceneTitle()
    {
        return R.string.consent;
    }

    @Override
    public Class getSceneClass()
    {
        return ConsentSharingScene.class;
    }

    public String getLocalizedLearnMoreHTMLContent()
    {
        return localizedLearnMoreHTMLContent;
    }
}
