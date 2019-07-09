package org.researchstack.foundation.components.presentation.interfaces

import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep

interface IStepFragment {
    @Deprecated("deprecated")
    fun initialize(step: IStep, result: IResult?)

    //TODO: JDK - 4/13/19 - Understand if we need to change StepCallbacks
    //This interface may change depending on work on Actions
    @Deprecated("deprecated")
    fun setCallbacks(callbacks: StepCallbacks)

    @Deprecated("deprecated")
    fun onBackPressed()

    @Deprecated("deprecated")
    val fragment: androidx.fragment.app.Fragment
}