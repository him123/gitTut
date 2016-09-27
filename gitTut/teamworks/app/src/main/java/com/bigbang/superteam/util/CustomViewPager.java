package com.bigbang.superteam.util;

import android.content.Context;
import android.support.v4.view.ViewPager;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by USER 8 on 5/19/2015.
 */
public class CustomViewPager extends ViewPager {

    private boolean enabled;

    public CustomViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
        this.enabled = true;
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        if (this.enabled && detectSwipeToRight(event)) {
            return super.onTouchEvent(event);
        }

        return false;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent event) {
        if (this.enabled && detectSwipeToRight(event)) {
            return super.onInterceptTouchEvent(event);
        }

        return false;
    }

    // To enable/disable swipe
    public void setPagingEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    // Detects the direction of swipe. Right or left.
// Returns true if swipe is in right direction
    public boolean detectSwipeToRight(MotionEvent event) {

        int initialXValue = 0; // as we have to detect swipe to right
        final int SWIPE_THRESHOLD = 100; // detect swipe
        boolean result = false;

        try {
            float diffX = event.getX() - initialXValue;

            if (Math.abs(diffX) > SWIPE_THRESHOLD) {
                if (diffX > 0) {
                    // swipe from left to right detected ie.SwipeRight
                    result = false;
                } else {
                    // swipe from right to left detected ie.SwipeLeft
                    result = true;
                }
            }
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return result;
    }
}