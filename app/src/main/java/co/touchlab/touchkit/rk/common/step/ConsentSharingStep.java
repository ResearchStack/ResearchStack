package co.touchlab.touchkit.rk.common.step;
import android.content.res.Resources;
import android.text.TextUtils;

import co.touchlab.touchkit.rk.R;
import co.touchlab.touchkit.rk.common.answerformat.AnswerFormat;
import co.touchlab.touchkit.rk.common.answerformat.TextChoiceAnswerFormat;
import co.touchlab.touchkit.rk.common.helpers.TextChoice;
import co.touchlab.touchkit.rk.common.model.DocumentProperties;
import co.touchlab.touchkit.rk.dev.DevUtils;

public class ConsentSharingStep extends QuestionStep
{

    private final String investigatorShortDescription;
    private final String investigatorLongDescription;
    private final String localizedLearnMoreHTMLContent;

    public ConsentSharingStep(Resources r, String identifier, DocumentProperties properties)
    {
        super(identifier);
        super.setOptional(false);
        super.setShowsProgress(false);
        super.setUseSurveyMode(false);

        this.investigatorShortDescription = properties.getInvestigatorShortDescription();
        if (TextUtils.isEmpty(investigatorShortDescription)){
            DevUtils.throwIllegalArgumentException();
        }

        this.investigatorLongDescription = properties.getInvestigatorLongDescription();
        if (TextUtils.isEmpty(investigatorLongDescription)){
            DevUtils.throwIllegalArgumentException();
        }

        this.localizedLearnMoreHTMLContent = properties.getHtmlContent();
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
}
