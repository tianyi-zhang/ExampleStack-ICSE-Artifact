public class foo{
			@Override
			public boolean onTouch(View v, MotionEvent event) {
				int eid = event.getAction();
				switch (eid) {
				case MotionEvent.ACTION_MOVE:
					PointF mv = new PointF(event.getX() - downPT.x, event.getY() - downPT.y);
					v.setX((int) (startPT.x + mv.x));
					v.setY((int) (startPT.y + mv.y));
					startPT = new PointF(v.getX(), v.getY());
					break;
				case MotionEvent.ACTION_DOWN:
					downPT.x = event.getX();
					downPT.y = event.getY();
					startPT = new PointF(v.getX(), v.getY());
					break;
				case MotionEvent.ACTION_UP:
					// Nothing have to do
					break;
				default:
					break;
				}
				return true;
			}
}