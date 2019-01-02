package org.researchstack.foundation.components.presentation.interfaces

import android.content.Context
import org.researchstack.foundation.core.interfaces.IStep

interface IStepFragmentProvider {
    fun stepFragment(context: Context, step: IStep): IStepFragment?
}