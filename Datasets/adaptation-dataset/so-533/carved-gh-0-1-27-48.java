public class foo{
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        RecyclerView view = new RecyclerView(this);
        setContentView(view);

        view.setLayoutManager(new LinearLayoutManager(this));

        Firebase.setAndroidContext(this);
        Firebase ref = new Firebase("https://stackoverflow.firebaseio.com/36299197").child("subscriptions/obama@gmsil,com");

        FirebaseRecyclerAdapter<Program, ProgramVH> adapter = new FirebaseRecyclerAdapter<Program, ProgramVH>(Program.class, android.R.layout.two_line_list_item, ProgramVH.class, ref) {
            @Override
            public void populateViewHolder(final ProgramVH programViewHolder, Program program, int position) {
                System.out.println("populateViewHolder for position "+position+" with program "+program);
                programViewHolder.title.setText(program.getTitle());
                programViewHolder.level.setText(""+program.getLength()); // coerce to string to prevent android.content.res.Resources$NotFoundException
            }
        };
        view.setAdapter(adapter);
    }
}