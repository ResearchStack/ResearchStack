package co.touchlab.touchkit.rk.common.step;
import android.content.res.Resources;
import android.text.TextUtils;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.answerformat.TextChoiceAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.model.DocumentProperties;
import co.touchlab.touchkit.rk.dev.DevUtils;
import co.touchlab.touchkit.rk.ui.scene.ConsentSharingScene;

public class ConsentSharingStep extends QuestionStep
{

    private final String localizedLearnMoreHTMLContent;

    public ConsentSharingStep(Resources r, String identifier, DocumentProperties properties)
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
    public Class getSceneClass()
    {
        return ConsentSharingScene.class;
    }

    public String getLocalizedLearnMoreHTMLContent()
    {
        return localizedLearnMoreHTMLContent;
    }
}
