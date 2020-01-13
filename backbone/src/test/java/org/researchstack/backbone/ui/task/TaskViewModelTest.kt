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

    private lateinit var taskViewModel: TaskViewModel

    private val currentStepMocked = createStep()
    private val stepMocked = createStep()

    @Before
    fun setUp() {
        whenever(intent?.getSerializableExtra(TaskActivity.EXTRA_TASK)).thenReturn(eq(task))

        taskViewModel = spy(createViewModel())
        taskViewModel.currentStep = currentStepMocked
        `when`(taskViewModel.currentTaskResult).thenReturn(currentTaskResult)
    }
    @Test
    fun edit_EditShouldBeTrue_EditStepShouldBeUpdated_CancelMenuShouldBeTrueToHideCancelInTitleBar() {
        // Assemble
        // Act
        taskViewModel.edit(stepMocked)

        // Assert
        Assert.assertEquals(taskViewModel.currentStep, stepMocked)
        Assert.assertEquals(taskViewModel.editStep.value, stepMocked)
        Assert.assertEquals(taskViewModel.editing, true)
        Assert.assertEquals(taskViewModel.hideMenuItemCancel.value, true)
    }

    @Test
    fun nextStep_ShouldSetCurrentStepToNext_andShouldUpdateCurrentStepEventToNextStep() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)

        // Act
        taskViewModel.nextStep()

        // Assert
        Assert.assertEquals(taskViewModel.currentStep, stepMocked)
        Assert.assertEquals(taskViewModel.currentStepEvent.value!!.step, stepMocked)
    }

    @Test
    fun previousStep_ShouldUpdateCurrentStepToPreviousStep_shouldUpdateCurrentStepEventToPreviousStep() {
        // Assemble
        `when`(task!!.getStepBeforeStep(any(), any())).thenReturn(stepMocked)
        // Act
        taskViewModel.previousStep()

        // Assert
        Assert.assertEquals(taskViewModel.currentStep, stepMocked)
        Assert.assertEquals(taskViewModel.currentStepEvent.value!!.step, stepMocked)
    }

    @Test
    fun previousStep_shouldCloseTaskFragmentWhenPreviousIsNull() {
        // Assemble
        `when`(task!!.getStepBeforeStep(any(), any())).thenReturn(null)

        // Act
        taskViewModel.previousStep()

        // Assert
        Assert.assertEquals(taskViewModel.taskCompleted.value, false)
    }

    @Test
    fun nextStep_shouldCloseTaskFragmentWhenNextIsNull() {
        // Assemble
        // Act
        taskViewModel.nextStep()

        // Assert
        Assert.assertEquals(taskViewModel.taskCompleted.value, true)
    }

    @Test
    fun nextStepInEditModeAndNextStepIsReviewStep_EditShouldBeFalse_ShouldMoveToReviewStep() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)
        `when`(taskViewModel.isReviewStep(stepMocked)).thenReturn(true)
        taskViewModel.edit(stepMocked)

        // Act
        taskViewModel.nextStep()

        // Assert
        Assert.assertEquals(taskViewModel.editing, false)
        Assert.assertEquals(taskViewModel.moveReviewStep.value?.step, stepMocked)
    }

    @Test
    fun checkIfAnswersAreNotTheSame_ShouldReturnFalse() {
        // Assemble
        val originalStepResult = createStepResult("tow")
        addStepResultIntoMockedMethods(originalStepResult, originalStepResult)

        // Act
        val actualResult = taskViewModel.checkIfAnswersAreTheSame()

        // Assert
        Assert.assertEquals(actualResult, false)
    }

    @Test
    fun checkIfAnswersAreNotTheSame_ShouldReturnTrue() {
        // Assemble
        createAndFillStepResult("one", "tow")

        // Act
        val actualResult = taskViewModel.checkIfAnswersAreTheSame()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun checkIfNewAnswerIsSkipWhilePreviousAnswersIsNot_ShouldReturnTrue() {
        // Assemble
        val originalStepResult = createStepResult("text")
        val modifiedStepResult = createStepResult(null)

        // Act
        val actualResult = taskViewModel.checkIfNewAnswerIsSkipWhilePreviousIsNot(originalStepResult, modifiedStepResult)

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun checkIfNewAnswerIsSkipWhilePreviousAnswersIsNot_ShouldReturnFalse() {
        // Assemble
        val originalStepResult = createStepResult(null)
        val modifiedStepResult = createStepResult(null)

        // Act
        val actualResult = taskViewModel.checkIfNewAnswerIsSkipWhilePreviousIsNot(originalStepResult, modifiedStepResult)

        // Assert
        Assert.assertEquals(actualResult, false)
    }


    @Test
    fun checkIfCurrentStepIsBranchDecisionStep_ShouldReturnFalse() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)
        `when`(taskViewModel.isReviewStep(stepMocked)).thenReturn(true)

        // Act
        val actualResult = taskViewModel.checkIfCurrentStepIsBranchDecisionStep()

        // Assert
        Assert.assertEquals(actualResult, false)
    }

    @Test
    fun checkIfCurrentStepIsBranchDecisionStep_ShouldReturnTrue() {
        // Assemble
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(stepMocked)
        `when`(taskViewModel.isReviewStep(stepMocked)).thenReturn(false)
        `when`(taskViewModel.currentTaskResult.getStepResult(any())).thenReturn(null)

        // Act
        val actualResult = taskViewModel.checkIfCurrentStepIsBranchDecisionStep()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun whenAnswersAreTheSame_ShouldShowCancelDialog() {
        // Assemble
        doReturn(true).`when`(taskViewModel).checkIfAnswersAreTheSame()

        //act
        taskViewModel.showCancelEditAlert()

        //Assert
        Assert.assertEquals(taskViewModel.showCancelEditDialog.value, true)
    }

    @Test
    fun whenAnswersAreTheSame_ShouldNotShowCancelDialog() {
        // Assemble
        doReturn(false).`when`(taskViewModel).checkIfAnswersAreTheSame()

        //act
        taskViewModel.showCancelEditAlert()

        //Assert
        Assert.assertEquals(taskViewModel.showCancelEditDialog.value, false)
    }

    @Test
    fun whenAnswersAreTheSame_And_CurrentStepIsBranchDecisionStep_ShouldShowSaveDialog() {
        // Assemble
        doReturn(true).`when`(taskViewModel).checkIfAnswersAreTheSame()
        doReturn(true).`when`(taskViewModel).checkIfCurrentStepIsBranchDecisionStep()

        //act
        taskViewModel.checkForSaveDialog()

        //Assert
        Assert.assertEquals(taskViewModel.showSaveEditDialog.value, true)
    }

    @Test
    fun whenAnswersAreTheSame_ShouldGoToNextStep() {
        // Assemble
        doNothing().`when`(taskViewModel).nextStep()
        doReturn(false).`when`(taskViewModel).checkIfAnswersAreTheSame()

        //act
        taskViewModel.checkForSaveDialog()

        //Assert
        verify(taskViewModel).checkForSaveDialog()
        verify(taskViewModel).checkIfAnswersAreTheSame()
        verify(taskViewModel).nextStep()
        verify(taskViewModel).currentStep = any() // called in setUp
        verifyNoMoreInteractions(taskViewModel)
    }

    @Test
    fun whenNewAnswerIsSkipWhilePreviousIsNot_And_StepIsOptional_ShouldShowSkipDialog() {
        // Assemble
        currentStepMocked.isOptional = true
        doReturn(true).`when`(taskViewModel).checkIfNewAnswerIsSkipWhilePreviousIsNot(any(), any())
        val modifiedStepResult = createStepResult(null)
        val originalStepResult = createStepResult("tow")
        addStepResultIntoMockedMethods(originalStepResult, originalStepResult)

        //act
        taskViewModel.checkForSkipDialog(modifiedStepResult)

        //Assert
        Assert.assertEquals(taskViewModel.showSkipEditDialog.value, Pair(true, originalStepResult))
    }

    @Test
    fun whenNewAnswerIsSkipWhilePreviousIsNot_And_StepIsNotOptional_ShouldGoToNextStep() {
        // Assemble
        doNothing().`when`(taskViewModel).nextStep()
        currentStepMocked.isOptional = false
        val modifiedStepResult = createStepResult(null)

        //act
        taskViewModel.checkForSkipDialog(modifiedStepResult)

        //Assert
        verify(taskViewModel).checkForSkipDialog(modifiedStepResult)
        verify(taskViewModel).nextStep()
        verify(taskViewModel).currentStep = any() // called in setUp
        verifyNoMoreInteractions(taskViewModel)
    }

    private fun createViewModel() = TaskViewModel(application!!, intent!!)

    private fun createStep() = Step("step")

    private fun addStepResultIntoMockedMethods(originalStepResult: StepResult<String>, modifiedStepResult: StepResult<String>?) {

        `when`(clonedTaskResultInCaseOfCancel!!.getStepResult(any())).thenReturn(originalStepResult)
        `when`(currentTaskResult!!.getStepResult(any())).thenReturn(modifiedStepResult)

        taskViewModel.clonedTaskResultInCaseOfEdit = clonedTaskResultInCaseOfCancel
        taskViewModel.taskResult = currentTaskResult!!
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
