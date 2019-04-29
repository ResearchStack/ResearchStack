package org.researchstack.foundation.components.presentation

import org.researchstack.foundation.core.interfaces.ITask

interface ITaskProvider {
    fun task(identifier: String): ITask?
}