package org.researchstack.backbone.ui.graph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.graphics.drawable.Drawable;
import android.support.annotation.StringRes;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.LineData;
import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;

import rx.Subscription;
import rx.functions.Action1;

public class LineChartCard extends CardView {
    private TextView titleTextView;
    private ImageView expand;
    private Subscription expandSub;
    private LineChart chart;

    private String titleText;
    private int titleTextColor;
    private float titleTextSize;
    private String titleTextTypeface;
    private int chartXAxisTextColor;
    private float chartXAxisTextSize;
    private String chartXAxisTextTypeface;
    private int chartYAxisTextColor;
    private float chartYAxisTextSize;
    private String chartYAxisTextTypeface;
    private int expandTintColor;

    public LineChartCard(Context context) {
        super(context);
        initializeRoot(null, R.attr.lineChartCardStyle);
        initializeViews();
    }

    public LineChartCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRoot(attrs, R.attr.lineChartCardStyle);
        initializeViews();
    }

    public LineChartCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRoot(attrs, defStyleAttr);
        initializeViews();
    }

    private void initializeRoot(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_chart_line, this, true);

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.LineChartCard,
                defStyleAttr,
                R.style.Widget_Backbone_Chart_Line);

        titleText = a.getString(R.styleable.LineChartCard_titleText);
        titleTextColor = a.getColor(R.styleable.LineChartCard_titleTextColor, 0);
        titleTextSize = a.getDimension(R.styleable.LineChartCard_titleTextSize, 0);
        titleTextTypeface = a.getString(R.styleable.LineChartCard_titleTextTypeface);
        chartXAxisTextColor = a.getColor(R.styleable.LineChartCard_chartXAxisTextColor, 0);
        chartXAxisTextSize = a.getDimension(R.styleable.LineChartCard_chartXAxisTextSize, 0) /
                getResources().getDisplayMetrics().density;
        chartXAxisTextTypeface = a.getString(R.styleable.LineChartCard_chartXAxisTextTypeface);
        chartYAxisTextColor = a.getColor(R.styleable.LineChartCard_chartYAxisTextColor, 0);
        chartYAxisTextSize = a.getDimension(R.styleable.LineChartCard_chartYAxisTextSize, 0) /
                getResources().getDisplayMetrics().density;
        chartYAxisTextTypeface = a.getString(R.styleable.LineChartCard_chartYAxisTextTypeface);
        expandTintColor = a.getColor(R.styleable.LineChartCard_expandTintColor, 0);

        a.recycle();
    }

    private void initializeViews() {
        titleTextView = (TextView) findViewById(R.id.view_chart_line_title);
        titleTextView.setText(titleText);
        titleTextView.setTextColor(titleTextColor);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        titleTextView.setTypeface(Typeface.create(titleTextTypeface, Typeface.NORMAL));

        expand = (ImageView) findViewById(R.id.view_chart_line_expand);
        if (expandTintColor != 0) {
            Drawable drawable = expand.getDrawable();
            drawable = DrawableCompat.wrap(drawable);
            DrawableCompat.setTint(drawable, expandTintColor);
            expand.setImageDrawable(drawable);
        }

        chart = (LineChart) findViewById(R.id.view_chart_line);
        chart.getLegend().setEnabled(false);
        chart.setDescription("");
        chart.setDrawBorders(false);
        chart.setDrawGridBackground(false);
        chart.setPinchZoom(false);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);
        chart.setExtraLeftOffset(0);
        chart.setExtraBottomOffset(8);
        chart.setExtraTopOffset(0);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(0);
        xAxis.setXOffset(16);
        xAxis.setTextSize(chartXAxisTextSize);
        xAxis.setTextColor(chartXAxisTextColor);
        xAxis.setTypeface(Typeface.create(chartXAxisTextTypeface, Typeface.NORMAL));

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLabels(true);
        yAxisLeft.setShowOnlyMinMax(true);
        yAxisLeft.setXOffset(16);
        yAxisLeft.setTextSize(chartYAxisTextSize);
        yAxisLeft.setTextColor(chartYAxisTextColor);
        yAxisLeft.setTypeface(Typeface.create(chartYAxisTextTypeface, Typeface.NORMAL));

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawZeroLine(false);
        yAxisRight.setDrawLabels(false);
        yAxisRight.setSpaceTop(0);
    }

    public void setTitle(@StringRes int titleResId) {
        String title = getContext().getString(titleResId);
        setTitle(title);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setExpandAction(Action1<Object> action) {
        expand.setVisibility(action == null ? View.GONE : View.VISIBLE);

        if (expandSub != null) {
            expandSub.unsubscribe();
        }

        if (action != null) {
            expandSub = RxView.clicks(expand).subscribe(action);
        }
    }

    public void setData(LineData data) {
        setData(data, 0, 0);
    }

    public void setData(LineData data, int viewportStart, int viewPortEnd) {
        float maxOffset = data.getYMax() + (data.getYMax() * .05f);

        chart.setData(data);
        chart.getAxisLeft().setAxisMaxValue(maxOffset);
        chart.getAxisRight().setAxisMaxValue(maxOffset);

        if (viewportStart != viewPortEnd) {
            chart.setVisibleXRange(viewportStart, viewPortEnd);
        }

        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public LineChart getChart() {
        return chart;
    }
}
