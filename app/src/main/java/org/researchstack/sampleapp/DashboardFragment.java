package org.researchstack.sampleapp;

import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.Snackbar;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.data.PieData;
import com.github.mikephil.charting.data.PieDataSet;
import com.github.mikephil.charting.interfaces.datasets.IBarDataSet;

import org.researchstack.backbone.ui.graph.BarChartCard;
import org.researchstack.backbone.ui.graph.LineChartCard;
import org.researchstack.backbone.ui.graph.PieChartCard;
import org.researchstack.backbone.ui.graph.ProgressChartCard;
import org.researchstack.backbone.utils.ThemeUtils;
import org.researchstack.skin.R;

import java.text.NumberFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Locale;


public class DashboardFragment extends Fragment
{
    private View emptyView;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        return inflater.inflate(R.layout.rss_fragment_dashboard, container, false);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);

        emptyView = view.findViewById(R.id.dashboard_empty);

        initProgressChart(view);
    }

    private void initProgressChart(View view)
    {
        ProgressChartCard progressCard = (ProgressChartCard) view.findViewById(R.id.dashboard_chart_progress);
        progressCard.setTitle("Amount of pie eaten");
        progressCard.setData(createProgressChartData());
        progressCard.setFinishAction(o -> {
            Snackbar.make(view, "Finish Action", Snackbar.LENGTH_SHORT).show();
        });

        PieChartCard pieCard = (PieChartCard) view.findViewById(R.id.dashboard_chart_pie);
        pieCard.setTitle("Pie Flavors");
        pieCard.setData(createPieChartData());

        BarChartCard barCard = (BarChartCard) view.findViewById(R.id.dashboard_chart_bar);
        barCard.setTitle("Pie Flavors");
        barCard.setData(createBarChartData(), false);
        barCard.setExpandAction(o -> {
            Snackbar.make(view, "Expand Action", Snackbar.LENGTH_SHORT).show();
        });

        BarChartCard barStackedCard = (BarChartCard) view.findViewById(R.id.dashboard_chart_bar_stacked);
        barStackedCard.setTitle("Pie Flavors");
        barStackedCard.setData(createStackedBarChartData(), true);
        barStackedCard.setExpandAction(o -> {
            Snackbar.make(view, "Expand Action", Snackbar.LENGTH_SHORT).show();
        });

        LineChartCard lineCard = (LineChartCard) view.findViewById(R.id.dashboard_chart_line);
        lineCard.setTitle("Daily steps");
        lineCard.setData(createLineChartData());
        lineCard.setExpandAction(o -> {
            Snackbar.make(view, "Expand Action", Snackbar.LENGTH_SHORT).show();
        });
    }

    public List<PieData> createProgressChartData()
    {
        List<PieData> items = new ArrayList<>();
        for(int i = 0, size = 12; i <= size; i++)
        {
            List<Entry> entries = new ArrayList<>();
            entries.add(new Entry(i, 0)); // Complete
            entries.add(new Entry(size - i, 1)); // Incomplete

            Calendar calendar = Calendar.getInstance();
            calendar.add(Calendar.MONTH, - i);

            String month = calendar.getDisplayName(Calendar.MONTH,
                    Calendar.SHORT,
                    Locale.getDefault());

            PieDataSet data = new PieDataSet(entries, month + " '16");
            data.setDrawValues(false);
            data.setColors(new int[] {
                    ThemeUtils.getAccentColor(getContext()), 0xFFe5e5e5
            });

            String[] labels = new String[] {"Incomplete", "Complete"};

            items.add(new PieData(labels, data));
        }
        return items;
    }

    public PieData createPieChartData()
    {
        List<Entry> entries = new ArrayList<>();
        entries.add(new Entry(50, 0));
        entries.add(new Entry(25, 1));
        entries.add(new Entry(12.5f, 2));
        entries.add(new Entry(12.5f, 3));

        PieDataSet data = new PieDataSet(entries, null);
        data.setDrawValues(false);
        data.setColors(new int[] {
                0xFF673ab7, 0xFF2196f3, 0xFF4caf50, 0xFF009688
        });

        String[] labels = new String[] {"Blackberry", "Blueberry", "Green apple", "Seaweed"};

        return new PieData(labels, data);
    }

    public BarData createBarChartData()
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        ArrayList<String> xVals = new ArrayList<>();
        for(int i = 0; i < 12; i++)
        {
            xVals.add(i + "");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for(int i = 0; i < 12; i++)
        {
            float mult = (5 + 1);
            int val = (int) (Math.random() * mult);
            yVals1.add(new BarEntry((val == 0 ? 1 : val), i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setColor(0xFF2196f3);
        set1.setBarSpacePercent(40f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10);
        data.setValueTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> numberFormat.format(
                value));
        return data;
    }

    public BarData createStackedBarChartData()
    {
        NumberFormat numberFormat = NumberFormat.getInstance();
        numberFormat.setMinimumFractionDigits(0);
        numberFormat.setMaximumFractionDigits(2);

        ArrayList<String> xVals = new ArrayList<>();
        for(int i = 0; i < 12; i++)
        {
            xVals.add(i + "");
        }

        ArrayList<BarEntry> yVals1 = new ArrayList<>();

        for(int i = 0; i < 12; i++)
        {
            float mult = (5 + 1);
            int val = (int) (Math.random() * mult);
            yVals1.add(new BarEntry(new float[] {(val == 0 ? 1 : val - 1), 1}, i));
        }

        BarDataSet set1 = new BarDataSet(yVals1, "DataSet");
        set1.setColors(new int[] {0xFF2196f3, 0xFF3f51b5});
        set1.setBarSpacePercent(40f);

        ArrayList<IBarDataSet> dataSets = new ArrayList<>();
        dataSets.add(set1);

        BarData data = new BarData(xVals, dataSets);
        data.setValueTextSize(10);
        data.setValueTypeface(Typeface.create("sans-serif-medium", Typeface.BOLD));
        data.setValueTextColor(Color.WHITE);
        data.setValueFormatter((value, entry, dataSetIndex, viewPortHandler) -> numberFormat.format(
                value));
        return data;
    }

    public LineData createLineChartData()
    {
        ArrayList<String> xValues = new ArrayList<>();
        for(int i = 0; i < 12; i++)
        {
            xValues.add(i + "");
        }

        ArrayList<Entry> entries = new ArrayList<>();

        for(int i = 0; i < 12; i++)
        {
            float mult = (5 + 1);
            int val = (int) (Math.random() * mult) + 1;
            entries.add(new Entry(val, i));
        }

        LineDataSet set = new LineDataSet(entries, "");
        set.setCircleColor(0xFF2196f3);
        set.setCircleRadius(4f);
        set.setDrawCircleHole(false);
        set.setColor(0xFF2196f3);
        set.setLineWidth(2f);
        set.setDrawValues(false);

        return new LineData(xValues, set);
    }
}