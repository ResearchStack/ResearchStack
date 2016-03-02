package co.touchlab.researchstack.sampleapp.network.body;

import java.util.Date;
import java.util.List;

import co.touchlab.researchstack.backbone.utils.FormatHelper;

public final class SurveyResponse
{

    private final String             identifier;
    private final String             surveyGuid;
    private final String             surveyCreatedOn;
    private final String             startedOn;
    private final String             completedOn;
    private final Status             status;
    //    private final Survey survey;
    private final List<SurveyAnswer> answers;

    public SurveyResponse(String identifier, Date startedOn, Date completedOn, String surveyGuid, String surveyCreatedOn, Status status, List<SurveyAnswer> answers)
    {
        this.identifier = identifier;
        this.surveyGuid = surveyGuid;
        this.surveyCreatedOn = surveyCreatedOn;
        this.startedOn = FormatHelper.SIMPLE_FORMAT_DATE.format(startedOn);
        this.completedOn = FormatHelper.SIMPLE_FORMAT_DATE.format(completedOn);
        this.status = status;
        this.answers = answers;
    }


    public enum Status
    {
        UNSTARTED,
        IN_PROGRESS,
        FINISHED
    }
}
