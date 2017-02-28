package org.researchstack.backbone.ui.views;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.DashPathEffect;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

import org.researchstack.backbone.step.active.recorder.AudioRecorder;
import org.researchstack.backbone.utils.ViewUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by TheMDP on 2/27/17.
 *
 * The AudioGraphView is a vertically centered bar graph that begins drawing on the right side
 * of a view and old samples are pushed to the left so as to appear the audio
 * is moving across the graph as you continue to make calls to addSample()
 */

public class AudioGraphView extends View {

    private static final int DEFAULT_SAMPLE_WIDTH = 24;
    private int sampleWidthInPx;

    private static final int DEFAULT_MAX_SAMPLE_VALUE = AudioRecorder.MAX_VOLUME;
    private int maxSampleValue;

    // Last-in-first-out for samples, which will be drawn on the graph from right to left
    private List<Integer> sampleList;

    /**
     * The style to draw the dashes in between the value bars
     */
    private Paint dashPaint;

    private static final int DEFAULT_GRAPH_COLOR = Color.BLACK;
    private int graphColor;

    /**
     * The style to draw the value bars in between the dashes
     */
    private Paint barPaint;

    public AudioGraphView(Context context) {
        super(context);
        commonInit();
    }

    public AudioGraphView(Context context, AttributeSet attrs) {
        super(context, attrs);
        commonInit();
    }

    public AudioGraphView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        commonInit();
    }

    @TargetApi(21)
    public AudioGraphView(Context context, AttributeSet attrs, int defStyleAttr, int defStyleRes) {
        super(context, attrs, defStyleAttr, defStyleRes);
        commonInit();
    }

    private void commonInit() {
        sampleList = new ArrayList<>();

        maxSampleValue = DEFAULT_MAX_SAMPLE_VALUE;
        setSampleWidthInPx(DEFAULT_SAMPLE_WIDTH);
        graphColor = DEFAULT_GRAPH_COLOR;

        refreshDashPaint();
        refreshBarPaint();
    }

    private void refreshDashPaint() {
        dashPaint = new Paint();
        dashPaint.setColor(graphColor);
        dashPaint.setStyle(Paint.Style.FILL_AND_STROKE);

        // Some devices' like Samsung disable dash path effect with hardware acceleration
        setLayerType(View.LAYER_TYPE_SOFTWARE, null);

        int pathDashWidths = getSampleWidthInPx();
        dashPaint.setStrokeWidth(pathDashWidths / 2);
        dashPaint.setPathEffect(new DashPathEffect(new float[] {pathDashWidths, pathDashWidths}, 0));
    }

    private void setSampleWidthInPx(int sampleWidthInDp) {
        int pathDashWidths = ViewUtils.dpToPx(getContext(), sampleWidthInDp);
        if (pathDashWidths % 2 != 0) {
            pathDashWidths++;
        }
        sampleWidthInPx = pathDashWidths;
    }

    /**
     * @return width of paths, will always be made divisible by 2
     */
    public int getSampleWidthInPx() {
        return sampleWidthInPx;
    }

    private void refreshBarPaint() {
        barPaint = new Paint();
        barPaint.setColor(graphColor);
        barPaint.setStyle(Paint.Style.FILL_AND_STROKE);
        barPaint.setStrokeCap(Paint.Cap.ROUND);

        float strokeWidth = getSampleWidthInPx();
        barPaint.setStrokeWidth(strokeWidth);
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        int midY = canvas.getHeight() / 2;
        int maxY = canvas.getHeight();
        int maxX = canvas.getWidth();

        // Draw the dash line in the middle first
        int pathDashWidth = getSampleWidthInPx();
        int remainderOfDash = maxY % pathDashWidth;
        canvas.drawLine(maxX, midY, remainderOfDash, midY, dashPaint);

        // Draw the graph data next
        int currentX = maxX - pathDashWidth; // start first sample on empty dash
        for (int i = (sampleList.size() - 1); i >= 0; i--) {
            if (currentX < remainderOfDash) {
                i = -1; // exit loop
            } else {
                int sample = sampleList.get(i);

                float heightFactor = (float)sample / (float)maxSampleValue;
                int lineHeight = (int)(canvas.getHeight() * heightFactor);
                int yOffset = (canvas.getHeight() - lineHeight) / 2;
                int halfWidth = pathDashWidth / 2;
                int x = currentX - halfWidth;

                // Draw the vertical line
                canvas.drawLine(x, yOffset, x, yOffset + lineHeight, barPaint);

                // Move to next sample line
                currentX -= (pathDashWidth * 2);
            }
        }
    }

    /**
     * @param sampleWidthInDp the sample width that will be converted from dp to px
     */
    public void setSampleWidthInDp(int sampleWidthInDp) {
        setSampleWidthInPx(sampleWidthInDp);
        refreshDashPaint();
        refreshBarPaint();
        invalidate();
    }

    public int getGraphColor() {
        return graphColor;
    }

    public void setGraphColor(int graphColor) {
        this.graphColor = graphColor;
        refreshDashPaint();
        refreshBarPaint();
        invalidate();
    }

    public int getMaxSampleValue() {
        return maxSampleValue;
    }

    /**
     * @param maxSampleValue controls the scale for the graph, the value will be displayed at 100% height
     */
    public void setMaxSampleValue(int maxSampleValue) {
        this.maxSampleValue = maxSampleValue;
    }

    /**
     * Adds a sample value, and then redraws the graph to include the new value
     * @param sampleValue to add to the graph
     */
    public void addSample(int sampleValue) {
        sampleList.add(sampleValue);

        // Limit max number of samples to be all that can fit in the view
        int viewWidth = getWidth();
        if (viewWidth > 0) {
            int maxSamples = viewWidth / (getSampleWidthInPx() * 2);
            if (sampleList.size() > maxSamples) {
                sampleList.remove(0);
            }
        }

        invalidate();
    }

    /**
     * Clears out all old samples, and then redraws the graph as blank
     */
    public void clearSamples() {
        sampleList.clear();
        invalidate();
    }
}
