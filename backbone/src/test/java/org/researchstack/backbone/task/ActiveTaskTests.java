package org.researchstack.backbone.task;

import com.google.gson.Gson;

import org.junit.Before;
import org.junit.Test;
import org.researchstack.backbone.model.SchedulesAndTasksModel;

/**
 * Created by TheMDP on 3/6/17.
 */

public class ActiveTaskTests {

    private Gson gson;

    @Before
    public void setUp() throws Exception {
        gson = new Gson();
    }

    @Test
    public void testTappingTask() {
        String inputTaskString = "{\"taskIdentifier\":\"1-Tapping-ABCD-1234\",\"schemaIdentifier\":\"Tapping Activity\",\"taskType\":\"tapping\",\"intendedUseDescription\":\"intended Use Description Text\",\"taskOptions\":{\"duration\":12.0,\"handOptions\":\"right\"},\"localizedSteps\":[{\"identifier\":\"conclusion\",\"title\":\"Title 123\",\"text\":\"Text 123\",\"detailText\":\"Detail Text 123\"}]}";
        SchedulesAndTasksModel.TaskScheduleModel taskModel = gson.fromJson(inputTaskString, SchedulesAndTasksModel.TaskScheduleModel.class);
        int i = 0;
    }

//    func testTappingTask() {
//
//        let inputTask: NSDictionary = [
//        "taskIdentifier"            : "1-Tapping-ABCD-1234",
//                "schemaIdentifier"          : "Tapping Activity",
//                "taskType"                  : "tapping",
//                "intendedUseDescription"    : "intended Use Description Text",
//                "taskOptions"               : [
//        "duration"      : 12.0,
//                "handOptions"   : "right"
//        ],
//        "localizedSteps"               : [[
//        "identifier" : "conclusion",
//                "title"      : "Title 123",
//                "text"       : "Text 123",
//                "detailText" : "Detail Text 123"
//        ]
//        ]
//        ]
//
//        let result = inputTask.createORKTask()
//        XCTAssertNotNil(result)
//        XCTAssertEqual(result?.identifier, "Tapping Activity")
//
//        guard let task = result as? ORKOrderedTask else {
//            XCTAssert(false, "\(result) not of expect class")
//            return
//        }
//
//        let expectedCount = 4
//        XCTAssertEqual(task.steps.count, expectedCount, "\(task.steps)")
//        guard task.steps.count == expectedCount else { return }
//
//        // Step 1 - Overview
//        guard let instructionStep = task.steps.first as? ORKInstructionStep else {
//            XCTAssert(false, "\(task.steps.first) not of expect class")
//            return
//        }
//        XCTAssertEqual(instructionStep.identifier, "instruction")
//        XCTAssertEqual(instructionStep.text, "intended Use Description Text")
//
//        // Step 2 - Right Hand Tapping Instruction
//        guard let rightInstructionStep = task.steps[1] as? ORKInstructionStep else {
//            XCTAssert(false, "\(task.steps[1]) not of expect class")
//            return
//        }
//        XCTAssertEqual(rightInstructionStep.identifier, "instruction1.right")
//
//        // Step 3 - Right Hand Tapping
//        guard let rightTappingStep = task.steps[2] as? ORKTappingIntervalStep else {
//            XCTAssert(false, "\(task.steps[2]) not of expect class")
//            return
//        }
//        XCTAssertEqual(rightTappingStep.identifier, "tapping.right")
//        XCTAssertEqual(rightTappingStep.stepDuration, 12.0)
//
//        // Step 4 - Completion
//        guard let completionStep = task.steps.last as? ORKCompletionStep else {
//            XCTAssert(false, "\(task.steps.last) not of expect class")
//            return
//        }
//        XCTAssertEqual(completionStep.identifier, "conclusion")
//        XCTAssertEqual(completionStep.title, "Title 123")
//        XCTAssertEqual(completionStep.text, "Text 123")
//        XCTAssertEqual(completionStep.detailText, "Detail Text 123")
//    }
}
