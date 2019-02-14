public class foo{
    public void fetchDrawableOnThread(final DrawableProducer dp, 
    	final ImageView imageView) {
    	
    	String key = dp.getKey();
    	if (drawableMap.containsKey(key)) {
    		Drawable d = drawableMap.get(key);
    		if (d != null) {
    			imageView.setImageDrawable(drawableMap.get(key));
    		}
    		return;
    	}

    	final Handler handler = new Handler() {
    		@Override
    		public void handleMessage(Message message) {
    			imageView.setImageDrawable((Drawable) message.obj);
    		}
    	};

    	Thread thread = new Thread() {
    		@Override
    		public void run() {
    			//TODO : set imageView to a "pending" image
    			Drawable drawable = fetchDrawable(dp);
    			if (drawable != null) {
	    			Message message = handler.obtainMessage(1, drawable);
	    			handler.sendMessage(message);
    			}
    		}
    	};
    	thread.start();
    }
}