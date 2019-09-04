package com.chxip.campusinfo.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.MotionEvent;

/**
 * Created by 陈湘平 on 2018/2/7.
 */
public class ViewPager extends android.support.v4.view.ViewPager {

    //是否可以进行滑动
    private boolean isSlide = false;

    public void setSlide(boolean slide) {
        isSlide = slide;
    }


    public ViewPager(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ViewPager(Context context) {
        super(context);
    }

    @Override
    public boolean onTouchEvent(MotionEvent arg0) {
        return isSlide;
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent arg0) {
        return isSlide;
    }

    @Override
    public void setCurrentItem(int item, boolean smoothScroll) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, smoothScroll);
    }

    @Override
    public void setCurrentItem(int item) {
        // TODO Auto-generated method stub
        super.setCurrentItem(item, false);
    }
}
