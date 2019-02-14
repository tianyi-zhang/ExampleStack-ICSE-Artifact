/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.luksprog.playground.adapter;

import android.view.View;
import android.view.animation.Animation;
import android.view.animation.Transformation;

/**
 * Class copied from
 * http://stackoverflow.com/questions/9248930/android-animate-drop
 * -down-up-view-proper  to have a easy animation.
 * 
 * 
 */
public class ExpandCollapseAnimation extends Animation {
	private View mAnimatedView;
	private int mEndHeight;
	private int mType;

	/**
	 * Initializes expand collapse animation, has two types, collapse (1) and
	 * expand (0).
	 * 
	 * @param view
	 *            The view to animate
	 * @param duration
	 * @param type
	 *            The type of animation: 0 will expand from gone and 0 size to
	 *            visible and layout size defined in xml. 1 will collapse view
	 *            and set to gone
	 */
	public ExpandCollapseAnimation(View view, int duration, int type) {
		setDuration(duration);
		mAnimatedView = view;
		mEndHeight = mAnimatedView.getLayoutParams().height;
		mType = type;
		if (mType == 0) {
			mAnimatedView.getLayoutParams().height = 0;
			mAnimatedView.setVisibility(View.VISIBLE);
		}
	}

	@Override
	protected void applyTransformation(float interpolatedTime, Transformation t) {
		super.applyTransformation(interpolatedTime, t);
		if (interpolatedTime < 1.0f) {
			if (mType == 0) {
				mAnimatedView.getLayoutParams().height = (int) (mEndHeight * interpolatedTime);
			} else {
				mAnimatedView.getLayoutParams().height = mEndHeight
						- (int) (mEndHeight * interpolatedTime);
			}
			mAnimatedView.requestLayout();
		} else {
			if (mType == 0) {
				mAnimatedView.getLayoutParams().height = mEndHeight;
				mAnimatedView.requestLayout();
			} else {
				mAnimatedView.getLayoutParams().height = 0;
				mAnimatedView.setVisibility(View.GONE);
				mAnimatedView.requestLayout();
				mAnimatedView.getLayoutParams().height = mEndHeight;
			}
		}
	}
}
