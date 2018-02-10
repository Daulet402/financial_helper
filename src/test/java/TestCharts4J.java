import com.google.common.collect.Lists;
import com.googlecode.charts4j.*;

import java.util.Arrays;

import static com.googlecode.charts4j.Color.*;

public class TestCharts4J {
    public static void main(String[] args) {
        Slice bitcoinSlice = Slice.newSlice(47, "Bitcoin");
        Slice rippleSlice = Slice.newSlice(3, "Ripple");
        Slice etherSlice = Slice.newSlice(30, "Ether");
        Slice moneroSlice = Slice.newSlice(20, "Monero");

        PieChart pieChart = GCharts.newPieChart(Lists.newArrayList(
                bitcoinSlice,
                rippleSlice,
                etherSlice,
                moneroSlice));


        pieChart.setTitle("Crypto Coins");
        pieChart.setSize(200, 200);

        BarChartPlot plot = Plots.newBarChartPlot(Data.newData(55d));
        BarChart chart = GCharts.newBarChart(plot);

       // exampleCharts();
        System.out.println("Stop Point");
    }


    public static String threeDPieChart(){
        Slice s1 = Slice.newSlice(30, Color.newColor("CACACA"), "Safari", "Apple");
        Slice s2 = Slice.newSlice(30, Color.newColor("DF7417"), "Firefox", "Mozilla");
        Slice s3 = Slice.newSlice(130, Color.newColor("951800"), "Chrome", "Google");
        Slice s4 = Slice.newSlice(150, Color.newColor("01A1DB"), "Internet Explorer", "Microsoft");

        PieChart chart = GCharts.newPieChart( s3, s4);
        chart.setTitle("A Better Web", BLACK, 16);
        chart.setSize(500, 200);
        chart.setThreeD(true);
        return chart.toURLString();
    }

    public static String twoDPieChart(){
        Slice s1 = Slice.newSlice(90, YELLOW, "Ms. Pac-Man");
        Slice s2 = Slice.newSlice(10, RED, "Red Lips");

        PieChart chart = GCharts.newPieChart(s1, s2);
        chart.setTitle("2D Pie Chart", BLACK, 16);
        chart.setSize(500, 200);
        String url = chart.toURLString();

        return chart.toURLString();
    }

    public static void exampleCharts() {
        // EXAMPLE CODE START
        Plot plot = Plots.newPlot(Data.newData(0, 10, 20, 30, 40, 50, 60, 70, 80, 90));
        plot.addShapeMarkers(Shape.DIAMOND, BLUE, 12);

        //Same data expressed in different charts.
        LineChart lineChart = GCharts.newLineChart(plot);
        lineChart.setSize(400, 200);
        BarChart barChart = GCharts.newBarChart(plot);
        barChart.setSize(400, 200);
        RadarChart radarChart = GCharts.newRadarChart(plot);
        radarChart.setSize(400, 200);
        ScatterPlot  scatterChart = GCharts.newScatterPlot(plot);
        scatterChart.setSize(400, 200);
        XYLineChart  xyLineChart = GCharts.newXYLineChart(plot);
        xyLineChart.setSize(400, 200);

        System.out.println(lineChart.toURLString());
        System.out.println(barChart.toURLString());
        System.out.println(radarChart.toURLString());
        System.out.println(scatterChart.toURLString());
        System.out.println(xyLineChart.toURLString());
    }


    public static String exampleBarChart2() {
        // EXAMPLE CODE START
        // Defining data series.
        final int MAX_MEDALS = 51;
        Data goldData= DataUtil.scaleWithinRange(0, MAX_MEDALS, Arrays.asList(MAX_MEDALS, 36, 23, 19, 16));
        Data silverData= DataUtil.scaleWithinRange(0, MAX_MEDALS, Arrays.asList(21, 38, 21, 13, 10));
        Data bronzeData= DataUtil.scaleWithinRange(0, MAX_MEDALS, Arrays.asList(28, 36, 28, 15, 15));
        BarChartPlot gold = Plots.newBarChartPlot(goldData, GOLD, "Gold");
        BarChartPlot silver = Plots.newBarChartPlot(silverData, SILVER, "Silver");
        BarChartPlot bronze = Plots.newBarChartPlot(bronzeData, Color.BROWN, "Bronze");
        BarChart chart = GCharts.newBarChart(gold, silver,  bronze);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(BLACK, 13, AxisTextAlignment.CENTER);
        AxisLabels country = AxisLabelsFactory.newAxisLabels("Country", 50.0);
        country.setAxisStyle(axisStyle);
        AxisLabels countries = AxisLabelsFactory.newAxisLabels("Germany", "United Kingdom", "Russia", "USA", "China");
        countries.setAxisStyle(axisStyle);
        AxisLabels medals = AxisLabelsFactory.newAxisLabels("Medals", 50.0);
        medals.setAxisStyle(axisStyle);
        AxisLabels medalCount = AxisLabelsFactory.newNumericRangeAxisLabels(0, MAX_MEDALS);
        medalCount.setAxisStyle(axisStyle);


        // Adding axis info to chart.
        chart.addXAxisLabels(medalCount);
        chart.addXAxisLabels(medals);
        chart.addYAxisLabels(countries);
        chart.addYAxisLabels(country);
        chart.addTopAxisLabels(medalCount);
        chart.setHorizontal(true);
        chart.setSize(450, 650);
        chart.setSpaceBetweenGroupsOfBars(30);

        chart.setTitle("2008 Beijing Olympics Medal Count", BLACK, 16);
        ///51 is the max number of medals.
        chart.setGrid((50.0/MAX_MEDALS)*20, 600, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(LIGHTGREY));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.newColor("E37600"), 100);
        fill.addColorAndOffset(Color.newColor("DC4800"), 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();
        return url;
    }

    public static String exampleBarChart() {
        // EXAMPLE CODE START
        // Defining data plots.
        BarChartPlot team1 = Plots.newBarChartPlot(Data.newData(25, 43, 12, 30), Color.BLUEVIOLET, "Team A");
        BarChartPlot team2 = Plots.newBarChartPlot(Data.newData(8, 35, 11, 5), Color.ORANGERED, "Team B");
        BarChartPlot team3 = Plots.newBarChartPlot(Data.newData(10, 20, 30, 30), Color.LIMEGREEN, "Team C");

        // Instantiating chart.
        BarChart chart = GCharts.newBarChart(team1, team2, team3);

        // Defining axis info and styles
        AxisStyle axisStyle = AxisStyle.newAxisStyle(Color.BLACK, 13, AxisTextAlignment.CENTER);
        AxisLabels score = AxisLabelsFactory.newAxisLabels("Score", 50.0);
        score.setAxisStyle(axisStyle);
        AxisLabels year = AxisLabelsFactory.newAxisLabels("Year", 50.0);
        year.setAxisStyle(axisStyle);

        // Adding axis info to chart.
        chart.addXAxisLabels(AxisLabelsFactory.newAxisLabels("2002", "2003", "2004", "2005"));
        chart.addYAxisLabels(AxisLabelsFactory.newNumericRangeAxisLabels(0, 100));
        chart.addYAxisLabels(score);
        chart.addXAxisLabels(year);

        chart.setSize(600, 450);
        chart.setBarWidth(100);
        chart.setSpaceWithinGroupsOfBars(20);
        chart.setDataStacked(true);
        chart.setTitle("Team Scores", Color.BLACK, 16);
        chart.setGrid(100, 10, 3, 2);
        chart.setBackgroundFill(Fills.newSolidFill(Color.ALICEBLUE));
        LinearGradientFill fill = Fills.newLinearGradientFill(0, Color.LAVENDER, 100);
        fill.addColorAndOffset(Color.WHITE, 0);
        chart.setAreaFill(fill);
        String url = chart.toURLString();
        return url;
    }
}