package org.researchstack.backbone.model.survey;

import com.google.gson.annotations.SerializedName;

/**
 * Created by TheMDP on 12/31/16.
 */

public class QuestionSurveyItem<T extends Object> extends SurveyItem<T> {

    @SerializedName("questionStyle")
    public boolean questionStyle;
    @SerializedName("placeholderText")
    public String placeholderText;
    @SerializedName("optional")
    public boolean optional;
    @SerializedName("range")
    public RangeSurveyItem range;

    @SerializedName("expectedAnswer")
    public boolean expectedAnswer; // Does this need to be a generic type?

    public boolean isValidQuestionItem() {
        return identifier != null && type.isQuestionSubtype();
    }

    public boolean isBooleanToggle() {
        return type == SurveyItemType.QUESTION_TOGGLE;
    }

    public boolean isCompoundStep() {
        return isBooleanToggle() || type == SurveyItemType.QUESTION_COMPOUND;
    }

    public boolean usesNavigation() {
        if (skipIdentifier != null || rulePredicate != null) {
            return true;
        }
        return false;
    }

//    func mapStepValues(with step: ORKStep) {
//        step.title = self.stepTitle?.trim()
//        step.text = self.stepText?.trim()
//        step.isOptional = self.optional
//        if let formStep = step as? ORKFormStep {
//            formStep.footnote = self.stepFootnote
//        }
//    }
//
//    func buildFormItems(with step: SBAFormProtocol, isSubtaskStep: Bool, factory: SBASurveyFactory? = nil) {
//
//        if self.isCompoundStep {
//            let factory = factory ?? SBASurveyFactory()
//            step.formItems = self.items?.map({
//            return factory.createFormItem($0 as! SBAFormStepSurveyItem)
//            })
//        }
//        else {
//            let subtype = self.surveyItemType.formSubtype()
//            step.formItems = [self.createFormItem(text: nil, subtype: subtype, factory: factory)]
//        }
//    }
//
//    func createFormItem(text: String?, subtype: SBASurveyItemType.FormSubtype?, factory: SBASurveyFactory? = nil) -> ORKFormItem {
//        let answerFormat = factory?.createAnswerFormat(self, subtype: subtype) ?? self.createAnswerFormat(subtype)
//        if let rulePredicate = self.rulePredicate {
//            // If there is a rule predicate then return a survey form item
//            let formItem = SBANavigationFormItem(identifier: self.identifier, text: text, answerFormat: answerFormat, optional: self.optional)
//            formItem.rulePredicate = rulePredicate
//            return formItem
//        }
//        else {
//            // Otherwise, return a form item
//            return ORKFormItem(identifier: self.identifier, text: text, answerFormat: answerFormat, optional: self.optional)
//        }
//    }
//
//    func createAnswerFormat(_ subtype: SBASurveyItemType.FormSubtype?) -> ORKAnswerFormat? {
//        let subtype = subtype ?? SBASurveyItemType.FormSubtype.boolean
//        switch(subtype) {
//            case .boolean:
//            return ORKBooleanAnswerFormat()
//            case .text:
//            return ORKTextAnswerFormat()
//            case .singleChoice, .multipleChoice:
//            guard let textChoices = self.items?.map({createTextChoice(from: $0)}) else { return nil }
//            let style: ORKChoiceAnswerStyle = (subtype == .singleChoice) ? .singleChoice : .multipleChoice
//            return ORKTextChoiceAnswerFormat(style: style, textChoices: textChoices)
//            case .date, .dateTime:
//            let style: ORKDateAnswerStyle = (subtype == .date) ? .date : .dateAndTime
//            let range = self.range as? SBADateRange
//            return ORKDateAnswerFormat(style: style, defaultDate: nil, minimumDate: range?.minDate as Date?, maximumDate: range?.maxDate as Date?, calendar: nil)
//            case .time:
//            return ORKTimeOfDayAnswerFormat()
//            case .duration:
//            return ORKTimeIntervalAnswerFormat()
//            case .integer, .decimal, .scale:
//            guard let range = self.range as? SBANumberRange else {
//                assertionFailure("\(subtype) requires a valid number range")
//                return nil
//            }
//            return range.createAnswerFormat(with: subtype)
//            case .timingRange:
//            guard let textChoices = self.items?.mapAndFilter({ (obj) -> ORKTextChoice? in
//                    guard let item = obj as? SBANumberRange else { return nil }
//            return item.createORKTextChoice()
//            }) else { return nil }
//            let notSure = ORKTextChoice(text: Localization.localizedString("SBA_NOT_SURE_CHOICE"), value: "Not sure" as NSString)
//            return ORKTextChoiceAnswerFormat(style: .singleChoice, textChoices: textChoices + [notSure])
//            case .compound, .toggle:
//            assertionFailure("Form item question type .compound or .toggle is not supported as an answer format")
//            return nil
//        }
//    }
//
//    func createTextChoice(from obj: Any) -> ORKTextChoice {
//        guard let textChoice = obj as? SBATextChoice else {
//            assertionFailure("Passing object \(obj) does not match expected protocol SBATextChoice")
//            return ORKTextChoice(text: "", detailText: nil, value: NSNull(), exclusive: false)
//        }
//        return textChoice.createORKTextChoice()
//    }
//

}
