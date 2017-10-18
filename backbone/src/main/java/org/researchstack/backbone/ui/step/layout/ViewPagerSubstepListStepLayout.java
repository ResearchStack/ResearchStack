package org.researchstack.backbone.ui.step.layout;

import android.content.Context;
import android.os.Parcelable;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.FrameLayout;

import org.researchstack.backbone.result.StepResult;
import org.researchstack.backbone.result.TaskResult;
import org.researchstack.backbone.step.Step;
import org.researchstack.backbone.step.SubstepListStep;
import org.researchstack.backbone.ui.callbacks.StepCallbacks;
import org.researchstack.backbone.ui.views.AlertFrameLayout;
import org.researchstack.backbone.utils.StepLayoutHelper;
import org.researchstack.backbone.utils.StepResultHelper;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 1/16/17.
 */

public class ViewPagerSubstepListStepLayout extends AlertFrameLayout implements StepLayout, StepCallbacks {

    /** Used to save and load the index of the view pager for when this view is destoryed/created */
    private static final String VIEW_PAGER_INDEX_KEY = "ViewPagerIndexKey";

    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    // Communicate w/ host
    //-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=-=
    protected StepCallbacks callbacks;

    protected SubstepListStep substepListStep;

    protected StepResult<StepResult> stepResult;

    protected SwipeDisabledViewPager viewPager;
    protected ViewPagerSubstepStepLayoutAdapter viewPagerAdapter;

    public ViewPagerSubstepListStepLayout(Context context) {
        super(context);
    }

    public ViewPagerSubstepListStepLayout(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    /**
     * Called when the ViewPager tries to move past the total Views
     *
     * Override this in subclasses to perform something on completion
     */
    protected void onComplete() {
        removeViewPagerIndex();
        callbacks.onSaveStep(ACTION_NEXT, substepListStep, stepResult);
    }

    @Override
    public void initialize(Step step, StepResult result) {
        validateStepAndResult(step, result);

        // Adds the view pager, and the view pager will create the substep step layouts
        viewPager = new SwipeDisabledViewPager(getContext());
        viewPagerAdapter = new ViewPagerSubstepStepLayoutAdapter();
        viewPager.setAdapter(viewPagerAdapter);
        addView(viewPager, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        // If there is a cached view pager index, then send the user to that view
        loadViewPagerIndex();
    }

    @Override
    public Parcelable onSaveInstanceState() {
        callbacks.onSaveStep(StepCallbacks.ACTION_NONE, substepListStep, stepResult);
        return super.onSaveInstanceState();
    }

    @SuppressWarnings("unchecked") // StepResult<T> cast
    protected void validateStepAndResult(Step step, StepResult result) {
        if (!(step instanceof SubstepListStep)) {
            throw new IllegalStateException(
                    "ViewPagerStepLayout is only compatible with a SubtaskStep");
        }

        substepListStep = (SubstepListStep) step;

        if (result != null && !result.getResults().isEmpty()) {
            for (Object valuObj : result.getResults().values()) {
                if (valuObj != null && !(valuObj instanceof StepResult)) {
                    throw new IllegalStateException("StepResult only supports StepResult<StepResult> class");
                }
            }
        } else {
            stepResult = null;
        }
        stepResult = result;
        if (stepResult == null) {
            stepResult = new StepResult<>(substepListStep);
        }
    }

    protected void hideKeyboard() {
        InputMethodManager imm = (InputMethodManager)getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(getWindowToken(), 0);
    }

    @Override
    public View getLayout() {
        return this;
    }

    /**
     * Method allowing a step layout to consume a back event.
     * @return a boolean indicating whether the back event is consumed
     */
    @Override
    public boolean isBackEventConsumed() {
        return viewPagerAdapter.stepLayouts.get(viewPager.getCurrentItem()).isBackEventConsumed();
    }

    // This is used to monitor the StepLayout for the Subtask's steps
    @Override
    public void onSaveStep(int action, Step step, StepResult result) {
        switch (action) {
            case ACTION_NONE:
                stepResult.getResults().put(step.getIdentifier(), result);
                callbacks.onSaveStep(ACTION_NONE, substepListStep, stepResult);
                break;
            case ACTION_NEXT:
                stepResult.getResults().put(step.getIdentifier(), result);
                if (!viewPagerAdapter.moveNext()) {
                    onComplete();
                } else {
                    saveViewPagerIndex();
                    callbacks.onSaveStep(ACTION_NONE, substepListStep, stepResult);
                }
                break;
            case ACTION_PREV:
                stepResult.getResults().remove(step.getIdentifier());
                if (!viewPagerAdapter.movePrevious()) {
                    removeViewPagerIndex();
                    callbacks.onSaveStep(ACTION_PREV, substepListStep, stepResult);
                } else {
                    saveViewPagerIndex();
                    callbacks.onSaveStep(ACTION_NONE, substepListStep, stepResult);
                }
                break;
            case ACTION_END:
                removeViewPagerIndex();
                onCancelStep();
                break;
        }
    }

    protected Step getMockViewPagerStep() {
        return new Step(substepListStep.getIdentifier() + "." + VIEW_PAGER_INDEX_KEY);
    }

    protected void saveViewPagerIndex() {
        Step mockStep = getMockViewPagerStep();
        StepResult<Integer> mockStepResult = new StepResult<>(mockStep);
        mockStepResult.setResult(viewPager.getCurrentItem());
        stepResult.getResults().put(mockStep.getIdentifier(), mockStepResult);
    }

    protected void removeViewPagerIndex() {
        stepResult.getResults().remove(getMockViewPagerStep().getIdentifier());
    }

    protected void loadViewPagerIndex() {
        Step mockStep = getMockViewPagerStep();
        StepResult mockStepResult = stepResult.getResults().get(mockStep.getIdentifier());
        if (mockStepResult != null && mockStepResult.getResult() instanceof Integer) {
            viewPager.setCurrentItem((Integer)mockStepResult.getResult());
        }
    }

    @Override
    public void onCancelStep() {
        stepResult.getResults().clear();
        callbacks.onSaveStep(ACTION_END, substepListStep, stepResult);
    }

    public void setCallbacks(StepCallbacks callbacks) {
        this.callbacks = callbacks;
    }

    /**
     * Can only be called after the ViewPager is instantiated and the StepLayouts have been created
     * If you need to access them when they are created, use onStepLayoutCreated method below
     *
     * @param index of StepLayout
     * @return the StepLayout at index in the ViewPager
     */
    protected StepLayout getStepLayout(int index) {
        if (viewPagerAdapter == null ||
            viewPagerAdapter.stepLayouts == null ||
            viewPagerAdapter.stepLayouts.isEmpty() ||
            viewPagerAdapter.stepLayouts.size() <= index)
        {
            return null;
        }
        return viewPagerAdapter.stepLayouts.get(index);
    }

    protected void onStepLayoutCreated(StepLayout stepLayout, int index) {
        // can be implemented by sub-classes
        loadViewPagerIndex();
    }

    protected class ViewPagerSubstepStepLayoutAdapter extends PagerAdapter {

        /** onSavedInstanceState wont be called unless view pager views have a valid id */
        private static final int BASE_VIEW_PAGER_ID = 10;

        List<StepLayout> stepLayouts;

        ViewPagerSubstepStepLayoutAdapter() {
            stepLayouts = new ArrayList<>();
        }

        @Override
        public int getCount() {
            return substepListStep.getStepList().size();
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == object;
        }

        @Override
        public Object instantiateItem(ViewGroup collection, int position) {
            Step step = substepListStep.getStepList().get(position);

            // Build ViewPager views based off of Step's StepLayouts, similar to what ViewTaskActivity does
            StepLayout stepLayout = StepLayoutHelper.createLayoutFromStep(step, getContext());
            StepResult subStepResult = StepResultHelper.findStepResult(stepResult, step.getIdentifier());
            stepLayout.setCallbacks(ViewPagerSubstepListStepLayout.this);
            stepLayout.initialize(step, subStepResult);
            stepLayouts.add(stepLayout);
            stepLayout.getLayout().setId(BASE_VIEW_PAGER_ID + position);

            collection.addView(stepLayout.getLayout(), 0);

            onStepLayoutCreated(stepLayout, position);

            return stepLayout.getLayout();
        }

        @Override
        public void destroyItem(ViewGroup collection, int position, Object view) {
            collection.removeView((View)view);
        }

        boolean moveNext() {
            hideKeyboard();
            int previousIndex = viewPager.getCurrentItem();
            viewPager.setCurrentItem(viewPager.getCurrentItem() + 1);
            return previousIndex != viewPager.getCurrentItem();
        }

        boolean movePrevious() {
            hideKeyboard();
            int previousIndex = viewPager.getCurrentItem();
            viewPager.setCurrentItem(viewPager.getCurrentItem() - 1);
            return previousIndex != viewPager.getCurrentItem();
        }
    }

    protected class SwipeDisabledViewPager extends ViewPager {

        public SwipeDisabledViewPager(Context context) {
            super(context);
        }

        public SwipeDisabledViewPager(Context context, AttributeSet attrs) {
            super(context, attrs);
        }

        @Override
        public boolean onInterceptTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }

        @Override
        public boolean onTouchEvent(MotionEvent event) {
            // Never allow swiping to switch between pages
            return false;
        }

        @Override
        public Parcelable onSaveInstanceState() {
            return super.onSaveInstanceState();
        }
    }
}
