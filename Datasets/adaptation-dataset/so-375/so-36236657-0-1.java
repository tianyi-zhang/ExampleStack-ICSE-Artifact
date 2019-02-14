public class foo {
    protected void populateViewHolder(final ItemViewHolder viewHolder, Boolean model, int position) {
        String key = this.getRef(position).getKey();
        ref.child("users").child(key).addListenerForSingleValueEvent(new ValueEventListener() {
            public void onDataChange(DataSnapshot dataSnapshot) {
                String name = dataSnapshot.child("name").getValue(String.class);
                ((TextView)viewHolder.itemView.findViewById(android.R.id.text1)).setText(name);
            }

            public void onCancelled(FirebaseError firebaseError) { }
        });
    }
}