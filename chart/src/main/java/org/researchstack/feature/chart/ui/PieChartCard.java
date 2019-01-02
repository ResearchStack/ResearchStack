package org.researchstack.backbone.ui.graph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;

import org.researchstack.backbone.R;

import java.text.NumberFormat;

public class PieChartCard extends CardView {
    private TextView titleTextView;
    private PieChart chart;
    private LinearLayout rowContainer;

    private NumberFormat numberFormat;

    private int valueTextFormat;
    private String titleText;
    private int titleTextColor;
    private float titleTextSize;
    private String titleTextTypeface;

    public PieChartCard(Context context) {
        super(context);
        initializeRoot(null, R.attr.pieChartCardStyle);
        initializeViews();
    }

    public PieChartCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRoot(attrs, R.attr.pieChartCardStyle);
        initializeViews();
    }

    public PieChartCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRoot(attrs, defStyleAttr);
        initializeViews();
    }

    private void initializeRoot(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_chart_pie, this, true);

        numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.PieChartCard,
                defStyleAttr,
                R.style.Widget_Backbone_Chart_Pie);

        titleText = a.getString(R.styleable.PieChartCard_titleText);
        titleTextColor = a.getColor(R.styleable.PieChartCard_titleTextColor, 0);
        titleTextSize = a.getDimension(R.styleable.PieChartCard_titleTextSize, 0);
        titleTextTypeface = a.getString(R.styleable.PieChartCard_titleTextTypeface);
        valueTextFormat = a.getResourceId(R.styleable.PieChartCard_valueTextFormat, 0);

        a.recycle();
    }

    private void initializeViews() {
        titleTextView = (TextView) findViewById(R.id.view_chart_pie_title);
        titleTextView.setText(titleText);
        titleTextView.setTextColor(titleTextColor);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        titleTextView.setTypeface(Typeface.create(titleTextTypeface, Typeface.NORMAL));

        chart = (PieChart) findViewById(R.id.view_chart_pie);
        chart.setDrawSliceText(false);
        chart.setTouchEnabled(false);
        chart.setHoleRadius(0);
        chart.setTransparentCircleRadius(0);
        chart.getLegend().setEnabled(false);
        chart.setDescription("");
        chart.setDrawCenterText(false);

        rowContainer = (LinearLayout) findViewById(R.id.view_chart_pie_rows);
    }

    public void setTitle(@StringRes int titleResId) {
        String title = getContext().getString(titleResId);
        setTitle(title);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setData(PieData data) {
        chart.setData(data);
        chart.notifyDataSetChanged();
        chart.invalidate();

        rowContainer.removeAllViews();
        LayoutInflater inflater = LayoutInflater.from(getContext());

        for (int i = 0, size = data.getXVals().size(); i < size; i++) {
            String xVal = data.getXVals().get(i);
            float entryValue = data.getDataSet().getEntryForIndex(i).getVal();

            View row = inflater.inflate(R.layout.rsb_item_chart_pie, rowContainer, false);

            TextView label = (TextView) row.findViewById(R.id.item_chart_pie_label);
            label.setText(xVal);
            label.setTextColor(data.getDataSet().getColor(i));

            TextView value = (TextView) row.findViewById(R.id.item_chart_pie_value);
            value.setText(getContext().getString(valueTextFormat, numberFormat.format(entryValue)));
            value.setTextColor(data.getDataSet().getColor(i));

            rowContainer.addView(row);
        }
    }

    public PieChart getChart() {
        return chart;
    }
}
