package com.andrew749.tutorialcardlibrary;

import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.os.Bundle;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.ImageSwitcher;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.ViewSwitcher;

import java.util.ArrayList;


public class TutorialsCardActivity extends Activity implements View.OnClickListener {
    ImageSwitcher switcher;
    Button next, previous;
    int index = 0;
    TextView descriptionText;
    ArrayList<TutorialEntry> entries;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setTheme(R.style.CustomTutorialTheme);
        setContentView(R.layout.tutorials_card);
        this.entries = (ArrayList<TutorialEntry>) getIntent().getSerializableExtra("entries");
        switcher = (ImageSwitcher) findViewById(R.id.imageSwitcher);
        next = (Button) findViewById(R.id.nextButton);
        descriptionText = (TextView) findViewById(R.id.descriptionText);
        previous = (Button) findViewById(R.id.previousButton);
        previous.setVisibility(View.INVISIBLE);
        next.setOnClickListener(this);
        previous.setOnClickListener(this);
        switcher.setFactory(new ViewSwitcher.ViewFactory() {
            @Override
            public View makeView() {
                ImageView myView = new ImageView(getApplicationContext());
                myView.setScaleType(ImageView.ScaleType.FIT_CENTER);
                myView.setLayoutParams(new ImageSwitcher.LayoutParams(LayoutParams.
                        FILL_PARENT, ViewGroup.LayoutParams.FILL_PARENT));
                myView.setImageResource(entries.get(index).image);
                return myView;
            }
        });

        // Gesture detection
        gestureDetector = new GestureDetector(this, new SwipeGestureDetector());
        gestureListener = new View.OnTouchListener() {
            public boolean onTouch(View v, MotionEvent event) {
                return gestureDetector.onTouchEvent(event);
            }
        };
        descriptionText.setText(entries.get(index).description);

        // Do this for each view added to the grid
        switcher.setOnTouchListener(gestureListener);
    }

    public void nextImage() {
        previous.setVisibility(View.VISIBLE);
        Animation in = AnimationUtils.makeInAnimation(getApplicationContext(), false);
        Animation out = AnimationUtils.makeOutAnimation(getApplicationContext(), false);
        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);
        if (index < entries.size() - 1) {
            switcher.setImageResource(entries.get(++index).image);
            descriptionText.setText(entries.get(index).description);
        } else finish();
        if (index == entries.size() - 1)
            next.setText("Finish");
    }

    @Override
    public void finish() {
        super.finish();
    }

    public void previousImage() {
        if(index==0)
            previous.setVisibility(View.INVISIBLE);
        Animation in = AnimationUtils.makeInAnimation(getApplicationContext(), true);
        Animation out = AnimationUtils.makeOutAnimation(getApplicationContext(), true);
        switcher.setInAnimation(in);
        switcher.setOutAnimation(out);
        if (index > 0) {
            switcher.setImageResource(entries.get(--index).image);
            descriptionText.setText(entries.get(index).description);
        }
        if (index < entries.size() - 1) {
            next.setText("Next");
        }


    }


    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.nextButton)
            nextImage();
        else if (id == R.id.previousButton)
            previousImage();
    }

    //From https://stackoverflow.com/questions/937313/android-basic-gesture-detection
    private static final int SWIPE_MIN_DISTANCE = 120;
    private static final int SWIPE_MAX_OFF_PATH = 250;
    private static final int SWIPE_THRESHOLD_VELOCITY = 200;
    private GestureDetector gestureDetector;
    View.OnTouchListener gestureListener;

    class SwipeGestureDetector extends GestureDetector.SimpleOnGestureListener {
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            try {
                if (Math.abs(e1.getY() - e2.getY()) > SWIPE_MAX_OFF_PATH)
                    return false;
                // right to left swipe
                if (e1.getX() - e2.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    nextImage();
                } else if (e2.getX() - e1.getX() > SWIPE_MIN_DISTANCE && Math.abs(velocityX) > SWIPE_THRESHOLD_VELOCITY) {
                    previousImage();
                }
            } catch (Exception e) {
                // nothing
            }
            return false;
        }

        @Override
        public boolean onDown(MotionEvent e) {
            return true;
        }
    }
}
