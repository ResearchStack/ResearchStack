package org.researchstack.backbone.storage.database;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;

import java.util.List;

/**
 * Whatever database implementation you use should implement these methods for basic saving of
 * {@link TaskResult} and {@link StepResult}. It also provides a method for setting the encryption
 * key of the database, for encrypted databases (do nothing if your database is not encrypted).
 */
public interface AppDatabase {
    /**
     * Saves the TaskResult to the database, along with its child StepResults
     *
     * @param result the task result to save
     */
    void saveTaskResult(TaskResult result);

    /**
     * Loads the latest task result for the given task identifier.
     * <p>
     * This can be used to see when the user last answered the survey, or to get their most recent
     * answer to a survey question.
     *
     * @param taskIdentifier the task identifier
     * @return the latest TaskResult for the given identifier, or none
     */
    TaskResult loadLatestTaskResult(String taskIdentifier);

    /**
     * Returns a list of all TaskResults for the given task identifier.
     *
     * @param taskIdentifier the task identifier
     * @return a list of all TaskResults for the given identifier
     */
    List<TaskResult> loadTaskResults(String taskIdentifier);

    /**
     * Returns a list of all StepResults for the given step identifier.
     *
     * @param stepIdentifier the step identifier
     * @return a list of all StepResults for the given identifier
     */
    List<StepResult> loadStepResults(String stepIdentifier);

    /**
     * Sets the encryption key on the database. If your database doesn't support encryption, make
     * this a no-op.
     *
     * @param key a string key to be used to encrypt the database
     */
    void setEncryptionKey(String key);
}
