public class foo {
        @Override
        public void onChanged()
        {
            List<View> oldViews = new ArrayList<View>(context.getChildCount());

            for (int i = 0; i < context.getChildCount(); i++)
                oldViews.add(context.getChildAt(i));

            Iterator<View> iter = oldViews.iterator();

            context.removeAllViews();

            for (int i = 0; i < context.adapter.getCount(); i++)
            {
                View convertView = iter.hasNext() ? iter.next() : null;
                context.addView(context.adapter.getView(i, convertView, context));
            }
            super.onChanged();
        }
}