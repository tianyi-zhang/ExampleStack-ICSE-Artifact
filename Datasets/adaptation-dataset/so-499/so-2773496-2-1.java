public class foo {
public void fetchDrawableOnThread(final String urlString, final ImageView imageView) {
    SoftReference<Drawable> drawableRef = drawableMap.get(urlString);
    if (drawableRef != null) {
        Drawable drawable = drawableRef.get();
        if (drawable != null) {
            imageView.setImageDrawable(drawableRef.get());
            return;
        }
        // Reference has expired so remove the key from drawableMap
        drawableMap.remove(urlString);
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
            Drawable drawable = fetchDrawable(urlString);
            Message message = handler.obtainMessage(1, drawable);
            handler.sendMessage(message);
        }
    };
    thread.start();
}
}