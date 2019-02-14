public class foo{
	// based off http://stackoverflow.com/a/13345781/431535
	private static void notSoUglyPlease(JFreeChart chart) {
		String fontName = "Lucida Sans";

		StandardChartTheme theme = (StandardChartTheme)org.jfree.chart.StandardChartTheme.createJFreeTheme();

		theme.setTitlePaint( Color.decode("#4572a7") );
		theme.setExtraLargeFont(new Font(fontName, Font.BOLD, 14)); //title
		theme.setLargeFont(new Font(fontName, Font.BOLD, 15)); //axis-title
		theme.setRegularFont(new Font(fontName, Font.PLAIN, 11));
		theme.setRangeGridlinePaint(Color.decode("#C0C0C0"));
		theme.setPlotBackgroundPaint(Color.white);
		theme.setChartBackgroundPaint(Color.white);
		theme.setGridBandPaint(Color.red);
		theme.setAxisOffset(new RectangleInsets(0, 0, 0, 0));
		theme.setBarPainter(new StandardBarPainter());
		theme.setAxisLabelPaint(Color.decode("#666666"));
		theme.apply(chart);
		chart.getCategoryPlot().setOutlineVisible(false);
		chart.getCategoryPlot().getRangeAxis().setAxisLineVisible(false);
		chart.getCategoryPlot().getRangeAxis().setTickMarksVisible(false);
		chart.getCategoryPlot().setRangeGridlineStroke(new BasicStroke());
		chart.getCategoryPlot().getRangeAxis().setTickLabelPaint(Color.decode("#666666"));
		chart.getCategoryPlot().getDomainAxis().setTickLabelPaint(Color.decode("#666666"));
		chart.setTextAntiAlias(true);
		chart.setAntiAlias(true);
		BarRenderer rend = (BarRenderer) chart.getCategoryPlot().getRenderer();
		rend.setShadowVisible(true);
		rend.setShadowXOffset(2);
		rend.setShadowYOffset(0);
		rend.setShadowPaint(Color.decode("#C0C0C0"));
		rend.setMaximumBarWidth(0.1);
	}
}