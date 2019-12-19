package org.researchstack.backbone.ui.task

import android.app.Application
import android.content.Intent
import com.nhaarman.mockitokotlin2.*

import org.junit.Before
import org.junit.Test
import org.mockito.Mockito
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.task.Task
import org.junit.rules.TestRule
import org.junit.Rule
import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import junit.framework.TestCase.assertEquals
import org.junit.Assert
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.Mockito.`when`
import org.mockito.junit.MockitoJUnitRunner
import org.researchstack.backbone.result.StepResult
import org.researchstack.backbone.result.TaskResult

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

    private lateinit var taskViewModel: TaskViewModel

    @Before
    fun before() {
        whenever(intent?.getSerializableExtra(TaskActivity.EXTRA_TASK)).thenReturn(eq(task))
    }

    @Test
    fun testInitViewModel_TaskShouldNotBeEmpty_EditModeMustBeFalse() {
        // Assemble
        // Act
        taskViewModel = createViewModel()

        // Assert
        assertEquals(taskViewModel.editing, false)
        assertEquals(taskViewModel.task, task)
    }

    @Test
    fun testEdit_editShouldBeTrue_EditStepShouldBeUpdated_CancelMenuShouldBeTrueToHideCancelInTitleBar() {
        // Assemble
        val selectedEditStep = createCurrentStep("selectedEditStep")
        taskViewModel = createViewModel()

        // Act
        taskViewModel.edit(selectedEditStep)

        // Assert
        Assert.assertEquals(taskViewModel.currentStep, selectedEditStep)
        Assert.assertEquals(taskViewModel.editStep.value, selectedEditStep)
        Assert.assertEquals(taskViewModel.editing, true)
        Assert.assertEquals(taskViewModel.hideMenuItemCancel.value, true)
    }


    @Test
    fun testRemoveUpdatedLayout_editShouldBeFalse_EditStepShouldBeUpdated_CancelMenuShouldBeFalseToShowCancelInTitleBar() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val currentStepMocked = createCurrentStep()
        val nextStepMocked = createCurrentStep("nextStepMocked")
        taskViewModel.currentStep = currentStepMocked
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(nextStepMocked)
        `when`(taskViewModel.isReviewStep(nextStepMocked)).thenReturn(true)

        // Act
        taskViewModel.removeUpdatedLayout()

        // Assert
        Assert.assertEquals(taskViewModel.editing, false)
        Assert.assertEquals(taskViewModel.updateCancelEditInLayout.value, true)
        Assert.assertEquals(taskViewModel.hideMenuItemCancel.value, false)
    }

    @Test
    fun testNext_ShouldUpdateCurrentStepToNextStep_shouldUpdateCurrentStepEventToNextStep() {
        // Assemble
        taskViewModel = createViewModel()
        val currentStepMocked = createCurrentStep()
        val nextStepMocked = createCurrentStep("nextStepMocked")
        taskViewModel.currentStep = currentStepMocked
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(nextStepMocked)

        // Act
        taskViewModel.nextStep()

        // Assert
        Assert.assertEquals(taskViewModel.currentStep, nextStepMocked)
        Assert.assertEquals(taskViewModel.currentStepEvent.value!!.step, nextStepMocked)
    }

    @Test
    fun testPrevious_ShouldUpdateCurrentStepToPreviousStep_shouldUpdateCurrentStepEventToPreviousStep() {
        // Assemble
        taskViewModel = createViewModel()

        val currentStepMocked = createCurrentStep()
        val previousStepMocked = createCurrentStep("previousStepMocked")
        taskViewModel.currentStep = currentStepMocked
        `when`(task!!.getStepBeforeStep(any(), any())).thenReturn(previousStepMocked)

        // Act
        taskViewModel.previousStep()

        // Assert
        Assert.assertEquals(taskViewModel.currentStep, previousStepMocked)
        Assert.assertEquals(taskViewModel.currentStepEvent.value!!.step, previousStepMocked)
    }

    @Test
    fun checkPrevious_shouldCloseWhenPreviousIsNull() {
        // Assemble
        taskViewModel = createViewModel()
        `when`(task!!.getStepBeforeStep(any(), any())).thenReturn(null)
        val currentStepMocked = createCurrentStep()
        taskViewModel.currentStep = currentStepMocked

        // Act
        taskViewModel.previousStep()

        // Assert
        Assert.assertEquals(taskViewModel.taskCompleted.value, false)
    }

    @Test
    fun testNext_shouldCloseWhenNextNull() {
        // Assemble
        taskViewModel = spy(createViewModel())

        // Act
        taskViewModel.nextStep()

        // Assert
        Assert.assertEquals(taskViewModel.taskCompleted.value, true)
    }

    @Test
    fun testShowCurrentStep_ifCurrentStepIsNull_ShowGoToNextStep() {
        // Assemble
        taskViewModel = spy(createViewModel())
        // Act
        taskViewModel.showCurrentStep()

        // Assert
        verify(taskViewModel, times(1)).showCurrentStep()
    }

    @Test
    fun testPreviousOnEditMode_shouldCheckForCancelDialog() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val currentStepMocked = createCurrentStep()
        taskViewModel.edit(currentStepMocked)

        // Act
        taskViewModel.previousStep()

        // Assert
        verify(taskViewModel, times(1)).showCancelEditAlert()
    }

    @Test
    fun testNextStepEditMode_WhenNextStepIsReviewStep_EditShouldBeFalse_moveReviewStepShouldGoToReviewStep() {
        // Assemble
        taskViewModel = spy(createViewModel())

        val currentStepMocked = createCurrentStep()
        val nextStepMocked = createCurrentStep("nextStepMocked")
        taskViewModel.currentStep = currentStepMocked

        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(nextStepMocked)
        `when`(taskViewModel.isReviewStep(nextStepMocked)).thenReturn(true)
        taskViewModel.edit(nextStepMocked)

        // Act
        taskViewModel.nextStep()

        // Assert
        Assert.assertEquals(taskViewModel.editing, false)
        Assert.assertEquals(taskViewModel.moveReviewStep.value?.step, nextStepMocked)
    }

    @Test
    fun testCheckIfAnswersAreNotTheSame_false() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val clonedTaskResultInCaseOfCancel = Mockito.mock(TaskResult::class.java)
        val currentTaskResult = Mockito.mock(TaskResult::class.java)

        val nextStepMocked = createCurrentStep("nextStepMocked")
        taskViewModel.currentStep = nextStepMocked

        val originalStepResult = spy(StepResult<String>(nextStepMocked))
        originalStepResult.result = "tow"

        `when`(clonedTaskResultInCaseOfCancel!!.getStepResult(any())).thenReturn(originalStepResult)
        `when`(currentTaskResult!!.getStepResult(any())).thenReturn(originalStepResult)
        `when`(taskViewModel.currentTaskResult).thenReturn(currentTaskResult)
        taskViewModel.clonedTaskResultInCaseOfCancel = clonedTaskResultInCaseOfCancel

        // Act
        val actualResult = taskViewModel.checkIfAnswersAreTheSame()

        // Assert
        Assert.assertEquals(actualResult, false)
    }

    @Test
    fun testCheckIfAnswersAreNotTheSame_true() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val clonedTaskResultInCaseOfCancel = Mockito.mock(TaskResult::class.java)
        val currentTaskResult = Mockito.mock(TaskResult::class.java)

        val nextStepMocked = createCurrentStep("nextStepMocked")
        taskViewModel.currentStep = nextStepMocked

        val originalStepResult = spy(StepResult<String>(nextStepMocked))
        originalStepResult.result = "tow"

        val modifiedStepResult = spy(StepResult<String>(nextStepMocked))
        modifiedStepResult.result = "one"

        `when`(clonedTaskResultInCaseOfCancel!!.getStepResult(any())).thenReturn(originalStepResult)
        `when`(currentTaskResult!!.getStepResult(any())).thenReturn(modifiedStepResult)
        `when`(taskViewModel.currentTaskResult).thenReturn(currentTaskResult)

        taskViewModel.clonedTaskResultInCaseOfCancel = clonedTaskResultInCaseOfCancel
        taskViewModel.taskResult = currentTaskResult

        // Act
        val actualResult = taskViewModel.checkIfAnswersAreTheSame()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun testCheckIfNewAnswerIsSkipWhilePreviousIsNot_true() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val clonedTaskResultInCaseOfCancel = Mockito.mock(TaskResult::class.java)
        val currentTaskResult = Mockito.mock(TaskResult::class.java)
        val nextStepMocked = createCurrentStep("nextStepMocked")
        taskViewModel.currentStep = nextStepMocked

        val originalStepResult = spy(StepResult<String>(nextStepMocked))
        originalStepResult.result = "tow"

        val modifiedStepResult = spy(StepResult<String>(nextStepMocked))
        modifiedStepResult.result = null

        `when`(clonedTaskResultInCaseOfCancel!!.getStepResult(any())).thenReturn(originalStepResult)
        `when`(currentTaskResult!!.getStepResult(any())).thenReturn(modifiedStepResult)
        `when`(taskViewModel.currentTaskResult).thenReturn(currentTaskResult)

        taskViewModel.clonedTaskResultInCaseOfCancel = clonedTaskResultInCaseOfCancel
        taskViewModel.taskResult = currentTaskResult

        // Act
        val actualResult = taskViewModel.checkIfNewAnswerIsSkipWhilePreviousIsNot()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun testCheckIfCurrentStepIsBranchDecisionStep_false() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val currentStep = createCurrentStep()
        taskViewModel.currentStep = currentStep

        val nextStepMocked = createCurrentStep("nextStepMocked")
        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(nextStepMocked)
        `when`(taskViewModel.isReviewStep(nextStepMocked)).thenReturn(true)

        // Act
        val actualResult = taskViewModel.checkIfCurrentStepIsBranchDecisionStep()

        // Assert
        Assert.assertEquals(actualResult, false)
    }

    @Test
    fun testCheckIfCurrentStepIsBranchDecisionStep_true() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val currentTaskResult = Mockito.mock(TaskResult::class.java)

        val currentStep = createCurrentStep()
        taskViewModel.currentStep = currentStep

        val nextStepMocked = createCurrentStep("nextStepMocked")

        `when`(task!!.getStepAfterStep(any(), any())).thenReturn(nextStepMocked)
        `when`(taskViewModel.isReviewStep(nextStepMocked)).thenReturn(false)
        `when`(taskViewModel.currentTaskResult).thenReturn(currentTaskResult)
        `when`(taskViewModel.currentTaskResult.getStepResult(any())).thenReturn(null)

        // Act
        val actualResult = taskViewModel.checkIfCurrentStepIsBranchDecisionStep()

        // Assert
        Assert.assertEquals(actualResult, true)
    }

    @Test
    fun testShowCancelEditAlert_ShouldShowCancelDialog() {
        // Assemble
        taskViewModel = spy(createViewModel())
        doReturn(true).`when`(taskViewModel).checkIfAnswersAreTheSame()

        //act
        taskViewModel.showCancelEditAlert()

        //Assert
        Assert.assertEquals(taskViewModel.showCancelEditDialog.value, true)
    }

    @Test
    fun testShowCancelEditAlert_ShouldNotShowCancelDialog() {
        // Assemble
        taskViewModel = spy(createViewModel())
        doReturn(false).`when`(taskViewModel).checkIfAnswersAreTheSame()

        //act
        taskViewModel.showCancelEditAlert()

        //Assert
        Assert.assertEquals(taskViewModel.showCancelEditDialog.value, false)
    }

    @Test
    fun testCheckForSaveDialog_ShouldShowSaveDialog() {
        // Assemble
        taskViewModel = spy(createViewModel())

        doReturn(true).`when`(taskViewModel).checkIfAnswersAreTheSame()
        doReturn(true).`when`(taskViewModel).checkIfCurrentStepIsBranchDecisionStep()

        //act
        taskViewModel.checkForSaveDialog()

        //Assert
        Assert.assertEquals(taskViewModel.showSaveEditDialog.value, true)
    }

    @Test
    fun testCheckForSaveDialog_ShouldGoToNextStep() {
        // Assemble
        taskViewModel = spy(createViewModel())

        doReturn(false).`when`(taskViewModel).checkIfAnswersAreTheSame()

        //act
        taskViewModel.checkForSaveDialog()

        //Assert
        verify(taskViewModel).nextStep()
    }

    @Test
    fun testCheckForSkipDialog_ShouldShowSkipDialog() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val originalStepResult = Mockito.mock(StepResult::class.java)

        val currentStepMocked = createCurrentStep()
        currentStepMocked.isOptional = true
        taskViewModel.currentStep = currentStepMocked
        doReturn(true).`when`(taskViewModel).checkIfNewAnswerIsSkipWhilePreviousIsNot()

        //act
        taskViewModel.checkForSkipDialog(originalStepResult)

        //Assert
        Assert.assertEquals(taskViewModel.showSkipEditDialog.value, Pair(true, originalStepResult!!))
    }

    @Test
    fun testCheckForSkipDialog_ShouldGoToNextStep() {
        // Assemble
        taskViewModel = spy(createViewModel())
        val originalStepResult = Mockito.mock(StepResult::class.java)

        val currentStepMocked = createCurrentStep()
        currentStepMocked.isOptional = false
        taskViewModel.currentStep = currentStepMocked

        //act
        taskViewModel.checkForSkipDialog(originalStepResult)

        //Assert
        verify(taskViewModel).nextStep()
    }

    private fun createViewModel() = TaskViewModel(application!!, intent!!)

    private fun createCurrentStep(name: String? = "currentStepMocked") = Step(name)

}