public class foo {
    protected void populateViewHolder(final ItemViewHolder viewHolder, Boolean model, int position) {
        String key = this.getRef(position).getKey();
        ref.child("items").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String date = dataSnapshot.getValue(String.class);
                ((TextView)viewHolder.itemView.findViewById(android.R.id.text1)).setText(date);
            }

            public void onCancelled(FirebaseError firebaseError) { }
        });
    }
}