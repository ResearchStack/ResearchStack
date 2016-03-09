package org.researchstack.bridge.body;

import org.researchstack.backbone.utils.FormatHelper;

import java.util.Date;
import java.util.List;

public final class SurveyAnswer
{

    private String       questionGuid;
    private boolean      declined;
    private String       client;
    private String       answeredOn;
    private List<String> answers;


    public SurveyAnswer(String questionGuid, boolean declined, String client, Date answeredOn, List<String> answers)
    {
        this.questionGuid = questionGuid;
        this.declined = declined;
        this.client = client;
        this.answeredOn = FormatHelper.SIMPLE_FORMAT_DATE.format(answeredOn);
        this.answers = answers;
    }
}
