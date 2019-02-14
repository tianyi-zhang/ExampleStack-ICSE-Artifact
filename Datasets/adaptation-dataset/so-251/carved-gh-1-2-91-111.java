public class foo{
		@Override
		public View getView(int position, View convertView, ViewGroup parent) {
			View rowView = convertView;
			if (rowView == null) {
				LayoutInflater inflater = act.getLayoutInflater();
				rowView = inflater.inflate(R.layout.slidemenu_listitem, null);
				MenuItemHolder viewHolder = new MenuItemHolder();
				viewHolder.label = (TextView) rowView.findViewById(R.id.menu_label);
				if(itemFont != null)
					viewHolder.label.setTypeface(itemFont);
				viewHolder.icon = (ImageView) rowView.findViewById(R.id.menu_icon);
				rowView.setTag(viewHolder);
			}

			MenuItemHolder holder = (MenuItemHolder) rowView.getTag();
			String s = items[position].label;
			holder.label.setText(s);
			holder.icon.setImageDrawable(items[position].icon);

			return rowView;
		}
}