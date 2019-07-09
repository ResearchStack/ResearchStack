package org.researchstack.foundation.components.presentation.interfaces

import android.content.Context
import androidx.fragment.app.Fragment
import org.researchstack.foundation.core.interfaces.IStep

interface IStepFragmentProvider {
    fun stepFragment(context: Context, step: IStep): Fragment?
}