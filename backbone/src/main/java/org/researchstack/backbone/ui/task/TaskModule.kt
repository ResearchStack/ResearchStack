package org.researchstack.backbone.ui.task

import android.content.Intent
import org.koin.androidx.viewmodel.dsl.viewModel
import org.koin.dsl.module

val taskActivityModule = module {
    viewModel { (intent : Intent) -> TaskViewModel(get(), intent) }
    viewModel { (intent : Intent) -> ReviewStepFullScreenImageViewModel(intent) }
}