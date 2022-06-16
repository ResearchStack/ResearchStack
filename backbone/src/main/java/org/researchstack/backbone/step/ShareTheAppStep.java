package org.researchstack.backbone.step;

import org.researchstack.backbone.ui.step.layout.ShareTheAppStepLayout;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Created by TheMDP on 1/26/17.
 */

public class ShareTheAppStep extends InstructionStep {

    private static final String TWITTER_ID  = "twitter";
    private static final String FACEBOOK_ID = "facebook";
    private static final String EMAIL_ID    = "email";
    private static final String SMS_ID      = "sms";

    private List<ShareType> shareTypeList = new ArrayList<>();

    /* Default constructor needed for serilization/deserialization of object */
    ShareTheAppStep() {
        super();
    }

    /**
     * Default share type list will be made from all the enum values
     * @param identifier of step
     * @param title of step
     * @param text of step
     */
    public ShareTheAppStep(String identifier, String title, String text) {
        super(identifier, title, text);
        shareTypeList = Arrays.asList(ShareType.values());
    }

    /**
     * @param identifier of step
     * @param title of step
     * @param text of step
     * @param shareTypeList list of share type to be included in step layout
     */
    public ShareTheAppStep(String identifier, String title, String text, List<ShareType> shareTypeList) {
        super(identifier, title, text);
        this.shareTypeList = shareTypeList;
    }

    public List<ShareType> getShareTypeList() {
        return shareTypeList;
    }

    public void setShareTypeList(List<ShareType> shareTypeList) {
        this.shareTypeList = shareTypeList;
    }

    @Override
    public Class getStepLayoutClass() {
        return ShareTheAppStepLayout.class;
    }

    public enum ShareType {

        TWITTER (TWITTER_ID),
        FACEBOOK(FACEBOOK_ID),
        EMAIL   (EMAIL_ID),
        SMS     (SMS_ID);

        private String identifier;

        ShareType(String identifier) {
            this.identifier = identifier;
        }

        public static List<ShareType> toShareTypeList(List<String> idList) {
            List<ShareType> shareTypeList = new ArrayList<>();
            if (idList != null) {
                for (String id : idList) {
                    for (ShareType shareType : ShareType.values()) {
                        if (id.equals(shareType.identifier)) {
                            shareTypeList.add(shareType);
                        }
                    }
                }
            }
            return shareTypeList;
        }
    }
}
