package org.researchstack.backbone.ui.task

import android.app.Application
import android.content.Intent
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import com.nhaarman.mockitokotlin2.*
import org.junit.Assert
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.rules.TestRule
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.Task

@RunWith(MockitoJUnitRunner::class)
class TaskViewModelTest {

    @Rule
    @JvmField
    val rule: TestRule = InstantTaskExecutorRule()

    @Mock
    private var application: Application? = null
    @Mock
    private var intent: Intent? = null
    @Mock
    private var task: Task? = null
    @Mock
    private var currentTaskResult: TaskResult? = null
    @Mock
    private var clonedTaskResultInCaseOfCancel: TaskResult? = null
    @Mock
    private var originalStepResult: StepResult<String>? = null

    // private lateinit var taskViewModel: TaskViewModel
    private lateinit var spyTaskViewModel: TaskViewModel

    private val currentStepMocked = createStep()
    private val stepMocked = createStep()
    @Before
    fun before() {
        whenever(intent?.getSerializableExtra(TaskActivity.EXTRA_TASK)).thenReturn(eq(task))

       // taskViewModel = createViewModel()

        spyTaskViewModel = spy(createViewModel())
        spyTaskViewModel.currentStep = currentStepMocked
        `when`(spyTaskViewModel.currentTaskResult).thenReturn(currentTaskResult)
    }
    @Test
    fun testEditShouldBeTrue_EditStepShouldBeUpdated_CancelMenuShouldBeTrueToHideCancelInTitleBar() {
        // Assemble
        // Act
        spyTaskViewModel.edit(stepMocked)

        // Assert
        Assert.assertEquals(spyTaskViewModel.currentStep, stepMocked)
        Assert.assertEquals(spyTaskViewModel.editStep.value, stepMocked)
        Assert.assertEquals(spyTaskViewModel.editing, true)
        Assert.assertEquals(spyTaskViewModel.hideMenuItemCancel.value, true)
    }

    @Test
    fun testRemoveUpdatedLayout_editShouldBeFalse_EditStepShouldBeUpdated_CancelMenuShouldBeFalseToShowCancelInTitleBar() {
        // Assemble
        `when`(spyTaskViewModel.isReviewStep(stepMocked)).thenReturn(true)

        // Act
        spyTaskViewModel.removeUpdatedLayout()

        // Assert
        Assert.assertEquals(spyTaskViewModel.editing, false)
        Assert.assertEquals(spyTaskViewModel.updateCancelEditInLayout.value, true)
        Assert.assertEquals(spyTaskViewModel.hideMenuItemCancel.value, false)
    }

    @Test
    fun testNextStep_shouldSetCurrentStepToNext_andShouldUpdateCurrentStepEventToNextStep() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)

        // Act
        spyTaskViewModel.nextStep()

        // Assert
        Assert.assertEquals(spyTaskViewModel.currentStep, stepMocked)
        Assert.assertEquals(spyTaskViewModel.currentStepEvent.value!!.step, stepMocked)
    }

    @Test
    fun testPrevious_ShouldUpdateCurrentStepToPreviousStep_shouldUpdateCurrentStepEventToPreviousStep() {
        // Assemble
        `when`(task!!.getStepBeforeStep(any(), any())).thenReturn(stepMocked)
        // Act
        spyTaskViewModel.previousStep()

        // Assert
        Assert.assertEquals(spyTaskViewModel.currentStep, stepMocked)
        Assert.assertEquals(spyTaskViewModel.currentStepEvent.value!!.step, stepMocked)
    }

    @Test
    fun testPrevious_shouldCloseTaskFragmentWhenPreviousIsNull() {
        // Assemble
        `when`(task!!.getStepBeforeStep(any(), any())).thenReturn(null)

        // Act
        spyTaskViewModel.previousStep()

        // Assert
        Assert.assertEquals(spyTaskViewModel.taskCompleted.value, false)
    }

    @Test
    fun testNext_shouldCloseTaskFragmentWhenNextIsNull() {
        // Assemble
        // Act
        spyTaskViewModel.nextStep()

        // Assert
        Assert.assertEquals(spyTaskViewModel.taskCompleted.value, true)
    }


    @Test
    fun testWhenEditingAStep_previousStep_shouldCallCancelDialog() {
        // Assemble
        spyTaskViewModel.edit(currentStepMocked)

        // Act
        spyTaskViewModel.previousStep()

        // Assert
        verify(spyTaskViewModel, times(1)).showCancelEditAlert()
    }

    @Test
    fun testNextStepEditMode_WhenNextStepIsReviewStep_EditShouldBeFalse_moveReviewStepShouldGoToReviewStep() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)
        `when`(spyTaskViewModel.isReviewStep(stepMocked)).thenReturn(true)
        spyTaskViewModel.edit(stepMocked)

        // Act
        spyTaskViewModel.nextStep()

        // Assert
        Assert.assertEquals(spyTaskViewModel.editing, false)
        Assert.assertEquals(spyTaskViewModel.moveReviewStep.value?.step, stepMocked)
    }

    @Test
    fun testCheckIfAnswersAreNotTheSame_ShouldReturnFalse() {
        // Assemble
        val originalStepResult = createStepResult("tow")
        addStepResultIntoMockedMethods(originalStepResult, originalStepResult)

        // Act
        val actualResult = spyTaskViewModel.checkIfAnswersAreTheSame()

        // Assert
        Assert.assertEquals(actualResult, false)
    }

    @Test
    fun testCheckIfAnswersAreNotTheSame_ShouldReturnTrue() {
        // Assemble
        createAndFillStepResult("one", "tow")

        // Act
        val actualResult = spyTaskViewModel.checkIfAnswersAreTheSame()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun testCheckIfNewAnswerIsSkipWhilePreviousAnswersIsNot_ShouldReturnTrue() {
        // Assemble
        createAndFillStepResult("one", null)

        // Act
        val actualResult = spyTaskViewModel.checkIfNewAnswerIsSkipWhilePreviousIsNot()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun testCheckIfNewAnswerIsSkipWhilePreviousAnswersIsNot_ShouldReturnFalse() {
        // Assemble
        createAndFillStepResult("one", "tow")

        // Act
        val actualResult = spyTaskViewModel.checkIfNewAnswerIsSkipWhilePreviousIsNot()

        // Assert
        Assert.assertEquals(actualResult, false)
    }


    @Test
    fun testCheckIfCurrentStepIsBranchDecisionStep_ShouldReturnFalse() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)
        `when`(spyTaskViewModel.isReviewStep(stepMocked)).thenReturn(true)

        // Act
        val actualResult = spyTaskViewModel.checkIfCurrentStepIsBranchDecisionStep()

        // Assert
        Assert.assertEquals(actualResult, false)
    }

    @Test
    fun testCheckIfCurrentStepIsBranchDecisionStep_ShouldReturnTrue() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)
        `when`(spyTaskViewModel.isReviewStep(stepMocked)).thenReturn(false)
        `when`(spyTaskViewModel.currentTaskResult.getStepResult(any())).thenReturn(null)

        // Act
        val actualResult = spyTaskViewModel.checkIfCurrentStepIsBranchDecisionStep()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun testShowCancelEditAlert_ShouldShowCancelDialog() {
        // Assemble
        doReturn(true).`when`(spyTaskViewModel).checkIfAnswersAreTheSame()

        //act
        spyTaskViewModel.showCancelEditAlert()

        //Assert
        Assert.assertEquals(spyTaskViewModel.showCancelEditDialog.value, true)
    }

    @Test
    fun testShowCancelEditAlert_ShouldNotShowCancelDialog() {
        // Assemble
        doReturn(false).`when`(spyTaskViewModel).checkIfAnswersAreTheSame()

        //act
        spyTaskViewModel.showCancelEditAlert()

        //Assert
        Assert.assertEquals(spyTaskViewModel.showCancelEditDialog.value, false)
    }

    @Test
    fun testCheckForSaveDialog_ShouldShowSaveDialog() {
        // Assemble
        doReturn(true).`when`(spyTaskViewModel).checkIfAnswersAreTheSame()
        doReturn(true).`when`(spyTaskViewModel).checkIfCurrentStepIsBranchDecisionStep()

        //act
        spyTaskViewModel.checkForSaveDialog()

        //Assert
        Assert.assertEquals(spyTaskViewModel.showSaveEditDialog.value, true)
    }

    @Test
    fun testCheckForSaveDialog_ShouldGoToNextStep() {
        // Assemble
        doReturn(false).`when`(spyTaskViewModel).checkIfAnswersAreTheSame()

        //act
        spyTaskViewModel.checkForSaveDialog()

        //Assert
        verify(spyTaskViewModel).nextStep()
    }

    @Test
    fun testCheckForSkipDialog_ShouldShowSkipDialog() {
        // Assemble
        currentStepMocked.isOptional = true
        doReturn(true).`when`(spyTaskViewModel).checkIfNewAnswerIsSkipWhilePreviousIsNot()

        //act
        spyTaskViewModel.checkForSkipDialog(originalStepResult)

        //Assert
        Assert.assertEquals(spyTaskViewModel.showSkipEditDialog.value, Pair(true, originalStepResult!!))
    }

    @Test
    fun testCheckForSkipDialog_ShouldGoToNextStep() {
        // Assemble
        currentStepMocked.isOptional = false
        //act
        spyTaskViewModel.checkForSkipDialog(originalStepResult)

        //Assert
        verify(spyTaskViewModel).nextStep()
    }

    private fun createViewModel() = TaskViewModel(application!!, intent!!)

    private fun createStep() = Step("step")

    private fun addStepResultIntoMockedMethods(originalStepResult: StepResult<String>, modifiedStepResult: StepResult<String>?) {

        `when`(clonedTaskResultInCaseOfCancel!!.getStepResult(any())).thenReturn(originalStepResult)
        `when`(currentTaskResult!!.getStepResult(any())).thenReturn(modifiedStepResult)

        spyTaskViewModel.clonedTaskResultInCaseOfCancel = clonedTaskResultInCaseOfCancel
        spyTaskViewModel.taskResult = currentTaskResult!!
    }

    private fun createStepResult(answer: String?): StepResult<String> {
        val originalStepResult = spy(StepResult<String>(stepMocked))
        originalStepResult.result = answer
        return originalStepResult
    }

    private fun createAndFillStepResult(originalResult: String, modifiedResult: String?) {
        val originalStepResult = createStepResult(originalResult)
        val modifiedStepResult = createStepResult(modifiedResult)
        addStepResultIntoMockedMethods(originalStepResult, modifiedStepResult)
    }
}
