package rnr.tests.imageloadingpattern;

import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.app.Activity;
import android.content.res.Resources;
import android.graphics.ColorMatrix;
import android.graphics.ColorMatrixColorFilter;
import android.graphics.drawable.BitmapDrawable;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;


public class MainActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Button b = (Button) findViewById(R.id.button);
        //ImageView i = (ImageView) findViewById(R.id.imageView);
        //i.setImageDrawable((BitmapDrawable) getResources().getDrawable(R.drawable.image));
        b.setOnClickListener( new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                testAnimation();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    public void testAnimation() {
        ImageView imageView = (ImageView)findViewById(R.id.imageView);
        final BitmapDrawable drawable = (BitmapDrawable) getResources().getDrawable(R.drawable.image);
        imageView.setImageDrawable(drawable);
        AlphaSatColorMatrixEvaluator evaluator = new AlphaSatColorMatrixEvaluator ();
        final AnimateColorMatrixColorFilter filter = new AnimateColorMatrixColorFilter(evaluator.getColorMatrix());
        drawable.setColorFilter(filter.getColorFilter());

        ObjectAnimator animator = ObjectAnimator.ofObject(filter, "colorMatrix", evaluator,
                evaluator.getColorMatrix());
        animator.addUpdateListener( new ValueAnimator.AnimatorUpdateListener() {
            @Override
            public void onAnimationUpdate(ValueAnimator animation) {
                drawable.setColorFilter (filter.getColorFilter());
            }
        });
        animator.setDuration(1500);
        animator.start();
    }

    /// Thanks to @DavidCrawford \
    /// see http://stackoverflow.com/a/27301389/2573335
    private class AnimateColorMatrixColorFilter {
        private ColorMatrixColorFilter mFilter;
        private ColorMatrix mMatrix;

        public AnimateColorMatrixColorFilter(ColorMatrix matrix) {
            setColorMatrix(matrix);
        }

        public ColorMatrixColorFilter getColorFilter() {
            return mFilter;
        }

        public void setColorMatrix(ColorMatrix matrix) {
            mMatrix = matrix;
            mFilter = new ColorMatrixColorFilter(matrix);
        }

        public ColorMatrix getColorMatrix() {
            return mMatrix;
        }
    }
}
