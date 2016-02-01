package co.touchlab.researchstack.sampleapp.network.body;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
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
        DateFormat format = new SimpleDateFormat("yyyy-MM-dd");
        this.answeredOn = format.format(answeredOn);
        this.answers = answers;
    }
}
