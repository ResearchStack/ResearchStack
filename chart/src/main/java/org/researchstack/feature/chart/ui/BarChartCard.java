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

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;

import rx.Subscription;
import rx.functions.Action1;

public class BarChartCard extends CardView {
    private TextView titleTextView;
    private ImageView expand;
    private Subscription expandSub;
    private BarChart chart;

    private String titleText;
    private int titleTextColor;
    private float titleTextSize;
    private String titleTextTypeface;
    private float chartXAxisTextSize;
    private int chartXAxisTextColor;
    private String chartXAxisTextTypeface;
    private int expandTintColor;

    public BarChartCard(Context context) {
        super(context);
        initializeRoot(null, R.attr.barChartCardStyle);
        initializeViews();
    }

    public BarChartCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRoot(attrs, R.attr.barChartCardStyle);
        initializeViews();
    }

    public BarChartCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRoot(attrs, defStyleAttr);
        initializeViews();
    }

    private void initializeRoot(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_chart_bar, this, true);

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.BarChartCard,
                defStyleAttr,
                R.style.Widget_Backbone_Chart_Bar);

        titleText = a.getString(R.styleable.BarChartCard_titleText);
        titleTextColor = a.getColor(R.styleable.BarChartCard_titleTextColor, 0);
        titleTextSize = a.getDimension(R.styleable.BarChartCard_titleTextSize, 0);
        titleTextTypeface = a.getString(R.styleable.BarChartCard_titleTextTypeface);
        chartXAxisTextColor = a.getColor(R.styleable.BarChartCard_chartXAxisTextColor, 0);
        chartXAxisTextSize = a.getDimension(R.styleable.BarChartCard_chartXAxisTextSize, 0) /
                getResources().getDisplayMetrics().density;
        chartXAxisTextTypeface = a.getString(R.styleable.BarChartCard_chartXAxisTextTypeface);
        expandTintColor = a.getColor(R.styleable.BarChartCard_expandTintColor, 0);

        a.recycle();
    }

    private void initializeViews() {
        titleTextView = (TextView) findViewById(R.id.view_chart_bar_title);
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

        chart = (BarChart) findViewById(R.id.view_chart_bar);
        chart.getLegend().setEnabled(false);
        chart.setDescription("");
        chart.setDrawBorders(false);
        chart.setDrawValueAboveBar(false);
        chart.setDrawGridBackground(false);
        chart.setDrawBarShadow(false);
        chart.setDrawHighlightArrow(false);
        chart.setPinchZoom(false);
        chart.setExtraLeftOffset(0);
        chart.setExtraRightOffset(0);
        chart.setExtraBottomOffset(8);
        chart.setExtraTopOffset(0);
        chart.setTouchEnabled(true);
        chart.setDragEnabled(true);

        XAxis xAxis = chart.getXAxis();
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setDrawAxisLine(false);
        xAxis.setYOffset(16);
        xAxis.setDrawGridLines(false);
        xAxis.setLabelsToSkip(0);
        xAxis.setTextSize(chartXAxisTextSize);
        xAxis.setTextColor(chartXAxisTextColor);
        xAxis.setTypeface(Typeface.create(chartXAxisTextTypeface, Typeface.NORMAL));

        YAxis yAxisLeft = chart.getAxisLeft();
        yAxisLeft.setDrawAxisLine(false);
        yAxisLeft.setDrawGridLines(false);
        yAxisLeft.setDrawZeroLine(false);
        yAxisLeft.setDrawLabels(false);

        YAxis yAxisRight = chart.getAxisRight();
        yAxisRight.setDrawAxisLine(false);
        yAxisRight.setDrawGridLines(false);
        yAxisRight.setDrawZeroLine(false);
        yAxisRight.setDrawLabels(false);
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

    /**
     * @param data    data to show on screen
     * @param stacked work around for setting yOffset of xAxis. Having a stacked dataset adds stack
     *                label spacing. To prevent this, we set 0 offset if dataset is stacked
     */
    public void setData(BarData data, boolean stacked) {
        float maxOffset = data.getYMax() + (data.getYMax() * .05f);

        chart.setData(data);
        chart.getAxisLeft().setAxisMaxValue(maxOffset);
        chart.getAxisRight().setAxisMaxValue(maxOffset);
        chart.getXAxis().setYOffset(stacked ? 0 : 16);
        chart.notifyDataSetChanged();
        chart.invalidate();
    }

    public BarChart getChart() {
        return chart;
    }
}
