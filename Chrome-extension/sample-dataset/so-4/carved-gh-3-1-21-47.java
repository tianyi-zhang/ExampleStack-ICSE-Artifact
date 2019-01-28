public class foo{
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_slide_up);

		imgPoster = (ImageView) findViewById(R.id.imgLogo);
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
				// start the animation
				imgPoster.setVisibility(View.VISIBLE);
				imgPoster.startAnimation(animSlideUp);
			}
		});

	}
}