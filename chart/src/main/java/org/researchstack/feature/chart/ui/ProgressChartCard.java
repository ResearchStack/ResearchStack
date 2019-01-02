package org.researchstack.backbone.ui.graph;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.Typeface;
import android.support.annotation.StringRes;
import android.support.design.widget.TabLayout;
import android.support.v7.widget.CardView;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.github.mikephil.charting.charts.PieChart;
import com.github.mikephil.charting.data.PieData;
import com.jakewharton.rxbinding.view.RxView;

import org.researchstack.backbone.R;
import org.researchstack.backbone.ui.views.IconTabLayout;

import java.text.NumberFormat;
import java.util.List;

import rx.Subscription;
import rx.functions.Action1;

public class ProgressChartCard extends CardView {
    private PieChart chart;
    private TabLayout tabLayout;
    private TextView titleTextView;
    private TextView finishView;
    private Subscription finishSub;

    private String titleText;
    private int titleTextColor;
    private float titleTextSize;
    private String titleTextTypeface;
    private String finishText;
    private int finishTextColor;
    private String centerTextFormat;
    private int centerTextColor;
    private float centerTextSize;
    private String centerTextTypeface;
    private int tabIndicatorColor;
    private int tabTextColor;
    private int tabSelectedTextColor;

    private NumberFormat numberFormat;

    public ProgressChartCard(Context context) {
        super(context);
        initializeRoot(null, R.attr.progressChartCardStyle);
        initializeViews();
    }

    public ProgressChartCard(Context context, AttributeSet attrs) {
        super(context, attrs);
        initializeRoot(attrs, R.attr.progressChartCardStyle);
        initializeViews();
    }

    public ProgressChartCard(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initializeRoot(attrs, defStyleAttr);
        initializeViews();
    }

    private void initializeRoot(AttributeSet attrs, int defStyleAttr) {
        LayoutInflater.from(getContext()).inflate(R.layout.rsb_view_chart_progress, this, true);

        numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        final TypedArray a = getContext().obtainStyledAttributes(attrs,
                R.styleable.ProgressChartCard,
                defStyleAttr,
                R.style.Widget_Backbone_Chart_Progress);

        titleText = a.getString(R.styleable.ProgressChartCard_titleText);
        titleTextColor = a.getColor(R.styleable.ProgressChartCard_titleTextColor, 0);
        titleTextSize = a.getDimension(R.styleable.ProgressChartCard_titleTextSize, 0);
        titleTextTypeface = a.getString(R.styleable.ProgressChartCard_titleTextTypeface);
        finishText = a.getString(R.styleable.ProgressChartCard_finishText);
        finishTextColor = a.getColor(R.styleable.ProgressChartCard_finishTextColor, 0);
        tabIndicatorColor = a.getColor(R.styleable.ProgressChartCard_tabIndicatorColor, 0);
        tabTextColor = a.getColor(R.styleable.ProgressChartCard_tabTextColor, 0);
        tabSelectedTextColor = a.getColor(R.styleable.ProgressChartCard_tabSelectedTextColor, 0);
        centerTextFormat = a.getString(R.styleable.ProgressChartCard_centerTextFormat);
        centerTextColor = a.getColor(R.styleable.ProgressChartCard_centerTextColor, 0);
        centerTextSize = a.getDimension(R.styleable.ProgressChartCard_centerTextSize, 0) /
                getResources().getDisplayMetrics().density;
        centerTextTypeface = a.getString(R.styleable.ProgressChartCard_centerTextTypeface);

        a.recycle();
    }

    private void initializeViews() {
        titleTextView = (TextView) findViewById(R.id.view_chart_progress_title);
        titleTextView.setText(titleText);
        titleTextView.setTextColor(titleTextColor);
        titleTextView.setTextSize(TypedValue.COMPLEX_UNIT_PX, titleTextSize);
        titleTextView.setTypeface(Typeface.create(titleTextTypeface, Typeface.NORMAL));

        finishView = (TextView) findViewById(R.id.view_chart_progress_finish);
        finishView.setText(finishText);
        finishView.setTextColor(finishTextColor);

        tabLayout = (TabLayout) findViewById(R.id.view_chart_progress_tabs);
        tabLayout.setSelectedTabIndicatorColor(tabIndicatorColor);
        tabLayout.setTabTextColors(tabTextColor, tabSelectedTextColor);

        chart = (PieChart) findViewById(R.id.view_chart_progress_chart);
        chart.setDrawSliceText(false);
        chart.setTouchEnabled(false);
        chart.setHoleColor(Color.TRANSPARENT);
        chart.setHoleRadius(95f);
        chart.getLegend().setEnabled(false);
        chart.setDescription("");
        chart.setCenterTextColor(centerTextColor);
        chart.setCenterTextSize(centerTextSize);
        chart.setCenterTextTypeface(Typeface.create(centerTextTypeface, Typeface.NORMAL));
    }

    public void setTitle(@StringRes int titleResId) {
        String title = getContext().getString(titleResId);
        setTitle(title);
    }

    public void setTitle(String title) {
        titleTextView.setText(title);
    }

    public void setFinishAction(Action1<Object> action) {
        finishView.setVisibility(action == null ? View.GONE : View.VISIBLE);

        if (finishSub != null) {
            finishSub.unsubscribe();
        }

        if (action != null) {
            finishSub = RxView.clicks(finishView).subscribe(action);
        }
    }

    public void setData(List<PieData> dataSet) {
        tabLayout.removeAllTabs();

        for (int i = 0, size = dataSet.size(); i < size; i++) {
            PieData data = dataSet.get(i);
            TabLayout.Tab newTab = tabLayout.newTab();
            newTab.setText(data.getDataSet().getLabel());
            newTab.setTag(data.getDataSet().getEntryForIndex(0).getVal());
            tabLayout.addTab(newTab, 0);

            if (i == size - 1) {
                post(() -> {
                    int lastIndex = tabLayout.getTabCount() - 1;
                    int right = ((ViewGroup) tabLayout.getChildAt(0)).getChildAt(lastIndex)
                            .getRight();
                    tabLayout.scrollTo(right, 0);
                    tabLayout.getTabAt(lastIndex).select();
                });
            }
        }

        tabLayout.setOnTabSelectedListener(new IconTabLayout.OnTabSelectedListenerAdapter() {
            @Override
            public void onTabSelected(TabLayout.Tab tab) {
                PieData data = dataSet.get(tab.getPosition());
                float complete = data.getDataSet().getEntryForIndex(0).getVal();
                float incomplete = data.getDataSet().getEntryForIndex(1).getVal();
                chart.setData(data);
                chart.setCenterText(String.format(centerTextFormat,
                        numberFormat.format(complete),
                        numberFormat.format(complete + incomplete)));
                chart.notifyDataSetChanged();
                chart.invalidate();
            }

            @Override
            public void onTabReselected(TabLayout.Tab tab) {
                super.onTabReselected(tab);
                onTabSelected(tab);
            }
        });
    }

    public PieChart getChart() {
        return chart;
    }
}
