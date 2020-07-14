package org.researchstack.backbone.ui.task

import android.content.Intent
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

@Deprecated("Deprecated as part of the new handling for the branching logic",
        ReplaceWith("com.medable.axon.ui.taskrunner.NRSTaskModule.nrsTaskActivityModule"))
val taskActivityModule = module {
    viewModel { (intent : Intent) -> TaskViewModel(get(), intent) }
    viewModel { (intent : Intent) -> ReviewStepFullScreenImageViewModel(intent) }
}