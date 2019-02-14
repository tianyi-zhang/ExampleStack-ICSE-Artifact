public class foo {
    private void initClut(int row, int col) {
        clut.clear();
        Color c = (Color) super.getItemPaint(row, col);
        float[] a = new float[3];
        Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), a);
        TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
        List<Task> tasks = series.getTasks(); // unchecked
        int taskCount = tasks.get(col).getSubtaskCount();
        taskCount = Math.max(1, taskCount);
        for (int i = 0; i < taskCount; i++) {
            clut.add(Color.getHSBColor(a[0], a[1] / i, a[2]));
        }
    }
}