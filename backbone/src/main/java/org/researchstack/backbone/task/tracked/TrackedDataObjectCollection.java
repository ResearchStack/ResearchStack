package org.researchstack.backbone.task.tracked;

import org.researchstack.backbone.StorageAccess;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.tracked.TrackedDataObject;

import java.io.Serializable;
import java.util.Date;
import java.util.List;

/**
 * Created by TheMDP on 3/21/17.
 */

public class TrackedDataObjectCollection implements Serializable {

    /**
     * Timestamp for the last time the tracked data survey questions were asked.
     * (ex. What medication, etc.)
     */
    private Date lastTrackingSurveyDate;

    /**
     * Timestamp for the last time the "Moment in Day" survey questions were asked.
     */
    private Date lastCompletionDate;

    /**
     * Selected items from the tracked data survey questions. Assumes only one set of items.
     */
    private List<TrackedDataObject> selectedItems;

    /**
     * Items from the tracked data survey questions that are *tracked* with "Moment in Day"
     * follow-up. Assumes only one set of items. This is a subset of the selected items that includes
     * only the selected items that are tracked with a follow-up question.
     */
    private List<TrackedDataObject> trackedItems;

    /**
     * Steps that map to the "Moment in Day" step results. These are used to determine the default
     * result for the case where there are no selected items.
     */
    private List<Step> momentInDaySteps;

    /**
     * Results that map to "Moment in Day" steps. These results are stored in memory only.
     */
    private List<StepResult> momentInDayResults;

    /**
     * Update the "Moment in Day" result set.
     * @param stepResult The step result to add/replace in the "Moment in Day" result set.
     */
    public void updateMomentInDay(StepResult stepResult) {

    }

    /**
     * Update the tracked data result set. If this is recognized as including the `selectedItems`
     * then that property will be updated from this result.
     * @param     stepResult  The step result to use to add/replace the tracked data set
     */
    public void updateTrackedData(StepResult stepResult) {

    }

    /**
     * Return the step result that is associated with a given step.
     * @param     step    The step for which a result is requested.
     * @return            The step result for this step (if found in the data store)
     */
    public StepResult getStepResult(Step step) {
        return null;
    }

    /**
     * Are there changes that need to be committed to the StorageAccess?
     */
    private boolean hasChanges;

    /**
     * Commit changes to the StorageAccess
     */
    public void commitChanges() {

    }

    /**
     * Reset the changes without committing them.
     */
    public TrackedDataObjectCollection newInstance() {
        return 
    }

    /**
     * Loads
     */
    private static loadSavedCollection() {

    }

/**
 Initialize with a user defaults that has a suite name (for sharing defaults across different apps)
 @param suiteName   Optional suite name for the user defaults (if nil, standard defaults are used)
 @return            Tracked data store
 */
//    - (instancetype)initWithUserDefaultsWithSuiteName:(NSString * _Nullable)suiteName;


//    - (void)commitChanges;

/**
 Reset the changes without commiting them.
 */
//    - (void)reset;

/**
 @Deprecated Use sharedStore instead
 */
//    + (instancetype)defaultStore __deprecated;

// Keys exposed to keep compatibility with existing implementations
//    + (NSString *)keyPrefix;
//    + (NSString *)lastTrackingSurveyDateKey;
//    + (NSString *)selectedItemsKey;
//    + (NSString *)resultsKey;
//

    public List<TrackedDataObject> getTrackedDataObjectList() {
        return trackedDataObjectList;
    }

    public void setTrackedDataObjectList(List<TrackedDataObject> trackedDataObjectList) {
        this.trackedDataObjectList = trackedDataObjectList;
    }
}
