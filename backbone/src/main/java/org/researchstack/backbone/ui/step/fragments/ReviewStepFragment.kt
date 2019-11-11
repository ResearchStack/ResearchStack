package org.researchstack.backbone.ui.step.fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.rsb_fragment_review_step_layout.*
import org.researchstack.backbone.R
import org.researchstack.backbone.step.Step
import org.researchstack.backbone.ui.step.body.BodyAnswer

class ReviewStepFragment : Fragment(R.layout.rsb_fragment_review_step_layout) {
    val adapter = ReviewStepAdapter()

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        reviewStepRecyclerView.adapter = adapter
    }
}

abstract class BaseReviewStepViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    abstract fun bind(data: StepAndAnswer)
}

class BooleanStepViewHolder(itemView: View) : BaseReviewStepViewHolder(itemView) {
    private val title: TextView = itemView.findViewById(R.id.booleanStepQuestionTitle)
    private val yesButton: TextView = itemView.findViewById(R.id.answer_yes)
    private val noButton: TextView = itemView.findViewById(R.id.answer_no)
    override fun bind(data: StepAndAnswer) {
        title.text = data.step.question
        //TODO: bind answer
    }
}

//TODO: Are these the types we need?
data class StepAndAnswer(val step: Step, var answer: BodyAnswer)

class ReviewStepAdapter : ListAdapter<StepAndAnswer, RecyclerView.ViewHolder>(DiffUtilCallback()) {
    private val stepTypeResolver = StepTypeResolver()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val inflater = LayoutInflater.from(parent.context)

        return when (viewType) {
            StepType.BOOLEAN.ordinal -> BooleanStepViewHolder(inflater.inflate(R.layout.rsb_boolean_step_layout, parent, false))
            else -> throw IllegalArgumentException("Unrecognized Step Type in ReviewStep Adapter, cannot inflate ViewType")
        }
    }

    override fun getItemViewType(position: Int): Int{
        val step = getItem(position).step

        return stepTypeResolver.getType(step).ordinal
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        (holder as BaseReviewStepViewHolder).bind(getItem(position))
    }
}

internal class DiffUtilCallback : DiffUtil.ItemCallback<StepAndAnswer>() {
    override fun areItemsTheSame(oldItem: StepAndAnswer, newItem: StepAndAnswer): Boolean {
        return oldItem.step.identifier == newItem.step.identifier
    }

    override fun areContentsTheSame(oldItem: StepAndAnswer, newItem: StepAndAnswer): Boolean {
        val oldParams = oldItem.answer.params
        val newParams = newItem.answer.params
        return oldItem.answer.isValid == newItem.answer.isValid
                && oldItem.answer.reason == newItem.answer.reason
                && with(oldParams) { contentEquals(newParams) }
    }
}

class StepTypeResolver {
    fun getType(step: Step): StepType {
        return when (step.stepLayoutClass.javaClass.name) {
            "RSBooleanBody" -> StepType.BOOLEAN
            "RSBooleanBody::class.java" -> StepType.BOOLEAN
            else -> throw IllegalArgumentException("Unrecognized Step Type in ReviewStep Adapter")
        }
    }
}

enum class StepType {
    AUTOCOMPLETE_TEXT,
    BOOLEAN,
    BARCODE,
    CONTINUOUS_SCALE,
    DATETIME,
    DATE,
    EMAIL,
    FORM,
    INTEGER_SCALE,
    LOCATION,
    NUMERIC,
    SECTION,
    TEXT,
    TEXT_CHOICE,
    TEXT_SCALE,
    TIME_INTERVAL,
    TIME_OF_DAY,
    VALUE_PICKER,
    INSTRUCTION,
    IMAGE_CHOICE,
    IMAGE_CAPTURE,
    WEB_VIEW
}


