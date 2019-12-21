package org.researchstack.backbone.ui

interface ConfirmExitListener {
        fun discardResultsAndFinish()
        fun saveAndFinish(boolean: Boolean)
    }