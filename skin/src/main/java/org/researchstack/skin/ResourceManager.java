package org.researchstack.skin;
import android.content.Context;
import android.content.res.AssetManager;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import org.researchstack.backbone.ResourcePathManager;
import org.researchstack.backbone.utils.LogExt;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;

/**
 * This class is responsible for returning paths of resources defined in the assets folder. This
 * class has more structure and expects certain assets to be defined for use within the framework.
 */
public abstract class ResourceManager extends ResourcePathManager
{
    /**
     * @return A singleton static instance of the ResourceManager class
     */
    public static ResourceManager getInstance()
    {
        return (ResourceManager) ResourcePathManager.getInstance();
    }

    /**
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * the StudyOverview file.
     */
    public abstract Resource getStudyOverview();

    /**
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * an HTML version of the Consent file.
     */
    public abstract Resource getConsentHtml();

    /**
     * This is currently unused but will be when share-pdf feature is implemented
     *
     * @return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * an PDF version of the Consent file.
     */
    public abstract Resource getConsentPDF();

    /**
     * The consent section differs from ResearchKit™ as ResearchStack includes extra
     * documentProperties along with support for quiz steps
     *
     * @return Return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * a consent section file
     */
    public abstract Resource getConsentSections();

    /**
     * @return Return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * a learn-sections file
     */
    public abstract Resource getLearnSections();

    /**
     * @return Return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * the privacy policy
     */
    public abstract Resource getPrivacyPolicy();

    /**
     * @return Return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * the software notices
     */
    public abstract Resource getSoftwareNotices();

    /**
     * @return Return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * the tasks and schedules file
     */
    public abstract Resource getTasksAndSchedules();

    /**
     * @return Return a {@link org.researchstack.backbone.ResourcePathManager.Resource} representing
     * a individual task file
     */
    public abstract Resource getTask(String taskFileName);

}
