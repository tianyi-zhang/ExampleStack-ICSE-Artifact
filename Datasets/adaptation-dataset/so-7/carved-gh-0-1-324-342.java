public class foo{
		private void initInfoPerResource(int row, int col) {
			colours.clear();
			tooltips.clear();
			
			Color c = (Color) super.getItemPaint(row, col);
			float[] a = new float[3];
			Color.RGBtoHSB(c.getRed(), c.getGreen(), c.getBlue(), a);
			TaskSeries series = (TaskSeries) model.getRowKeys().get(row);
			List<Task> tasks = series.getTasks();
			Task resource = tasks.get(col);
			int taskCount = resource.getSubtaskCount();
			taskCount = Math.max(1, taskCount);
			for (int i = 0; i < taskCount; i++) {
				MySubtask subtask = (MySubtask)resource.getSubtask(i);
				Color colour = colourMap.get(subtask.getItem().getActivityId());
				colours.add(colour);
				tooltips.add(subtask.getItem().getName());
			}
		}
}