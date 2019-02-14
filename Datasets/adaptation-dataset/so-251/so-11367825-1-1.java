public class foo {
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View rowView = convertView;
        if (rowView == null) {
            LayoutInflater inflater = act.getLayoutInflater();
            rowView = inflater.inflate(R.layout.menu_listitem, null);
            MenuItem viewHolder = new MenuItem();
            viewHolder.label = (TextView) rowView.findViewById(R.id.menu_label);
            viewHolder.icon = (ImageView) rowView.findViewById(R.id.menu_icon);
            rowView.setTag(viewHolder);
        }

        MenuItem holder = (MenuItem) rowView.getTag();
        String s = items[position].label;
        holder.label.setText(s);
        holder.icon.setImageResource(items[position].icon);

        return rowView;
    }
}