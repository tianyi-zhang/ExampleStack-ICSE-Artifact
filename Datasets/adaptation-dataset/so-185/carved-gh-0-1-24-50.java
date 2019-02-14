public class foo{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        ListView answersList = new ListView(this);

        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://stackoverflow.firebaseio.com/36160819/answers");

        Query queryRef = ref.orderByChild("questionid").equalTo("-KDi2YLc2nzdvJXvntYC");
        FirebaseListAdapter<DataSnapshot> adapter = new FirebaseListAdapter<DataSnapshot>(this, DataSnapshot.class,
                android.R.layout.two_line_list_item, queryRef) {
            @Override
            protected DataSnapshot parseSnapshot(DataSnapshot snapshot) {
                return snapshot;
            }

            @Override
            protected void populateView (View v, DataSnapshot answer, int position) {
                ((TextView)v.findViewById(android.R.id.text1)).setText(answer.child("authorid").getValue().toString());
                ((TextView)v.findViewById(android.R.id.text2)).setText(answer.child("answer").getValue().toString());
            }
        };
        answersList.setAdapter(adapter);

        setContentView(answersList);
    }
}