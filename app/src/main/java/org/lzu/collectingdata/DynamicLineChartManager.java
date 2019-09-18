package org.lzu.collectingdata;

import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.components.AxisBase;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.LimitLine;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IAxisValueFormatter;

import java.util.ArrayList;
import java.util.List;

public class DynamicLineChartManager {
    private LineChart lineChart;
    private YAxis leftAxis;
    private YAxis rightAxis;
    private XAxis xAxis;
    private LineData lineData;
    private LineDataSet lineDataSet;
    private List<Integer> timeList = new ArrayList<>(); //存储x轴的时间
    private List<Integer> colors = new ArrayList<>(); //颜色
    private Integer currentTimeMillis = 0;

    //一条曲线
    public DynamicLineChartManager(LineChart mLineChart) {
        this.lineChart = mLineChart;
        lineChart.setBackgroundColor(MyApplication.context.getResources().getColor(R.color.color_bar_bg));
        leftAxis = lineChart.getAxisLeft();
        rightAxis = lineChart.getAxisRight();
        xAxis = lineChart.getXAxis();
        initLineChart();
        initLineDataSet();
    }

    /**
     * 初始化LineChar
     */
    private void initLineChart() {
        lineChart.setDrawGridBackground(false);
        //显示边界
        lineChart.setDrawBorders(true);
        //折线图例 标签 设置
        Legend legend = lineChart.getLegend();
        legend.setEnabled(false);
        MyMarkerView myMarkerView = new MyMarkerView(MyApplication.context);
        myMarkerView.setChartView(lineChart);
        lineChart.setMarker(myMarkerView);
        //X轴设置显示位置在底部
        xAxis.setPosition(XAxis.XAxisPosition.BOTTOM);
        xAxis.setGranularity(1f);
        xAxis.setLabelCount(4);
        xAxis.setValueFormatter(new IAxisValueFormatter() {
            @Override
            public String getFormattedValue(float value, AxisBase axis) {
                int i = (int) value % timeList.size();
                if ((timeList.get(i) - timeList.get(0)) % 10 == 0) {
                    return timeList.get(i).toString();
                } else {
                    return "";
                }
            }
        });

        //保证Y轴从0开始，不然会上移一点
        leftAxis.setAxisMinimum(0f);
        rightAxis.setAxisMinimum(0f);
    }

    /**
     * 初始化折线(一条线)
     */
    private void initLineDataSet() {
        lineDataSet = new LineDataSet(null, "");
        lineDataSet.setLineWidth(1.0f);
        lineDataSet.setCircleRadius(2.0f);
        lineDataSet.setDrawCircleHole(false);
        //设置曲线填充
        lineDataSet.setDrawFilled(false);
        lineDataSet.setAxisDependency(YAxis.AxisDependency.LEFT);
        lineDataSet.setDrawValues(false);
        //添加一个空的 LineData
        lineData = new LineData();
        lineChart.setData(lineData);
        lineChart.invalidate();
    }


    /**
     * 动态添加数据（一条折线图）
     * @param index
     * @param number
     */
    public void addEntry(int index, int number) {
        if(index >= 66 && index <= 112){
            colors.add(MyApplication.context.getResources().getColor(R.color.color_red));
        } else {
            colors.add(MyApplication.context.getResources().getColor(R.color.color_green));
        }
        lineDataSet.setColors(colors);
        lineDataSet.setCircleColors(colors);
        //最开始的时候才添加 lineDataSet（一个lineDataSet 代表一条线）
        if (lineDataSet.getEntryCount() == 0) {
            lineData.addDataSet(lineDataSet);
        }
        lineChart.setData(lineData);
        timeList.add(currentTimeMillis);
        Entry entry = new Entry(lineDataSet.getEntryCount(), number);
        lineData.addEntry(entry, 0);
        //通知数据已经改变
        lineData.notifyDataChanged();
        lineChart.notifyDataSetChanged();
        //设置在曲线图中显示的最大数量
        lineChart.setVisibleXRangeMaximum(50);
        //移到某个位置
        lineChart.moveViewToX(lineData.getEntryCount() - 1);
        currentTimeMillis = currentTimeMillis + 1;
    }

    /**
     * 设置Y轴值
     *
     * @param max
     * @param min
     * @param labelCount
     */
    public void setYAxis(float max, float min, int labelCount) {
        if (max < min) {
            return;
        }
        leftAxis.setAxisMaximum(max);
        leftAxis.setAxisMinimum(min);
        leftAxis.setLabelCount(labelCount, false);
        rightAxis.setEnabled(false); //右侧Y轴不显示
        lineChart.invalidate();
    }

    /**
     * 设置高限制线
     *
     * @param high
     * @param name
     */
    public void setHightLimitLine(float high, String name, int color) {
        if (name == null) {
            name = "高限制线";
        }
        LimitLine hightLimit = new LimitLine(high, name);
        hightLimit.setLineWidth(4f);
        hightLimit.setTextSize(10f);
        hightLimit.setLineColor(color);
        hightLimit.setTextColor(color);
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置低限制线
     *
     * @param low
     * @param name
     */
    public void setLowLimitLine(int low, String name) {
        if (name == null) {
            name = "低限制线";
        }
        LimitLine hightLimit = new LimitLine(low, name);
        hightLimit.setLineWidth(1f);
        hightLimit.setTextSize(12f);
        hightLimit.setTextColor(MyApplication.context.getResources().getColor(R.color.color_gray));
        hightLimit.setLineColor(MyApplication.context.getResources().getColor(R.color.color_yellow));
        leftAxis.addLimitLine(hightLimit);
        lineChart.invalidate();
    }

    /**
     * 设置描述信息
     *
     * @param str
     */
    public void setDescription(String str) {
        Description description = new Description();
        description.setText(str);
        lineChart.setDescription(description);
        lineChart.invalidate();
    }
}
