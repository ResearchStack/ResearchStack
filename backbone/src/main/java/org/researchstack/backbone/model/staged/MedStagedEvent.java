package org.researchstack.backbone.model.staged;

import java.util.Date;

/**
 * Created by mauriciosouto on 7/9/17.
 */

public class MedStagedEvent {

    private String activity;
    private Date eventStartDate;
    private Date eventEndDate;
    private MedStagedActivityState status;
    private MedStagedEventResult result;

    public MedStagedEvent(Date eventStartDate, Date eventEndDate) {
        this.eventStartDate = eventStartDate;
        this.eventEndDate = eventEndDate;
    }

    public void addResult(MedStagedEventResult result, MedStagedActivityState status) {
        this.result = result;
        this.status = status;
    }
}
