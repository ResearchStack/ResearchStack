package org.researchstack.foundation.components.presentation.interfaces

import android.support.v4.app.Fragment
import org.researchstack.foundation.components.common.ui.callbacks.StepCallbacks
import org.researchstack.foundation.core.interfaces.IResult
import org.researchstack.foundation.core.interfaces.IStep

interface IStepFragment {
    fun initialize(step: IStep, result: IResult?)
    //TODO: Understand if we need to change StepCallbacks
    fun setCallbacks(callbacks: StepCallbacks)
    fun onBackPressed()
    val fragment: Fragment
}