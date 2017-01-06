package org.researchstack.backbone.step.navigation;

import org.researchstack.backbone.answerformat.AnswerFormat;
import org.researchstack.backbone.model.survey.NavigationStep;
import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.QuestionStep;

import java.util.List;

/**
 * Created by TheMDP on 12/31/16.
 */

public class NavigationQuestionStep extends QuestionStep implements NavigationStep {

    String skipToStepIdentifier;
    boolean skipIfPassed;

    public NavigationQuestionStep(String identifier) {
        super(identifier);
    }

    public NavigationQuestionStep(String identifier, String title) {
        super(identifier, title);
    }

    public NavigationQuestionStep(String identifier, String title, AnswerFormat format) {
        super(identifier, title, format);
    }

    @Override
    public String getNextStepIdentifier(TaskResult result, List<TaskResult> additionalTaskResults) {
        return null;
    }

    @Override
    public QuestionStep matchingSurveyStep(StepResult result) {
        if (result.getIdentifier().equals(getIdentifier())) {
            return this;
        }
        return null;
    }

    @Override
    public String getSkipToStepIdentifier() {
        return skipToStepIdentifier;
    }

    @Override
    public void setSkipToStepIdentifier(String identifier) {
        skipToStepIdentifier = identifier;
    }

    @Override
    public boolean getSkipIfPassed() {
        return skipIfPassed;
    }

    @Override
    public void setSkipIfPassed(boolean skipIfPassed) {
        this.skipIfPassed = skipIfPassed;
    }

// TODO: do we need this?
//
//    // MARK: NSCopying
//
//    override public func copy(with zone: NSZone? = nil) -> Any {
//        let copy = super.copy(with: zone) as! SBANavigationQuestionStep
//        copy.rulePredicate = self.rulePredicate
//        return self.sharedCopying(copy)
//    }
//
//    // MARK: NSSecureCoding
//
//    required public init(coder aDecoder: NSCoder) {
//        super.init(coder: aDecoder);
//        self.sharedDecoding(coder: aDecoder)
//        self.rulePredicate = aDecoder.decodeObject(forKey: #keyPath(rulePredicate)) as? NSPredicate
//    }
//
//    override public func encode(with aCoder: NSCoder){
//        super.encode(with: aCoder)
//        self.sharedEncoding(aCoder)
//        aCoder.encode(self.rulePredicate, forKey: #keyPath(rulePredicate))
//    }
//
//    // MARK: Equality
//
//    override public func isEqual(_ object: Any?) -> Bool {
//        guard let castObject = object as? SBANavigationQuestionStep else { return false }
//        return super.isEqual(object) &&
//                sharedEquality(object) &&
//                SBAObjectEquality(castObject.rulePredicate, self.rulePredicate)
//    }
//
//    override public var hash: Int {
//        return super.hash ^ sharedHash() ^ SBAObjectHash(self.rulePredicate)
//    }
}
