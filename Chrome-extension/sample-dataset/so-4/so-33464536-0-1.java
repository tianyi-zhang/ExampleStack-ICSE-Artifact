public class foo {
        @Override
        protected void onCreate(Bundle savedInstanceState) {
            // TODO Auto-generated method stub
            super.onCreate(savedInstanceState);
            setContentView(R.layout.activity_fadein);

            txtMessage = (TextView) findViewById(R.id.txtMessage);
            btnStart = (Button) findViewById(R.id.btnStart);

            // load the animation
            animSlideUp = AnimationUtils.loadAnimation(getApplicationContext(),
                    R.anim.slide_up);

            // set animation listener
            animSlideUp.setAnimationListener(this);

            // button click event
            btnStart.setOnClickListener(new View.OnClickListener() {

                @Override
                public void onClick(View v) {
                    txtMessage.setVisibility(View.VISIBLE);

                    // start the animation
                    txtMessage.startAnimation(animSlideUp);
                }
            });

        }
}
