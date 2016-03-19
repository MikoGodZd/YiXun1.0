package com.god.collapsingavatartoolbar.sample.View;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.MotionEvent;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import com.sloydev.collapsingavatartoolbar.sample.R;

/**
 * Created by zd on 2016/3/2.
 */
public class SlideMenu extends HorizontalScrollView {

    private LinearLayout mWapper;
    private ViewGroup mMenu;
    private ViewGroup mMain;

    private int mScreenWidth;
    private int mRightPadding = 50;
    private int mMenuWidth;

    private boolean once;
    private boolean isOpen;

    private float xDistance, yDistance, xLast, yLast;

    public SlideMenu(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);

        TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.SlideMenu);
        int count = a.getIndexCount();

        for (int i = 0; i < count; i++) {
            int attr = a.getIndex(i);

            switch (attr) {
                case R.styleable.SlideMenu_leftpadding:
                    mRightPadding = a.getDimensionPixelOffset(attr, (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, 50,
                            getContext().getResources().getDisplayMetrics()));
                    break;
            }
        }

        WindowManager wm = (WindowManager) getContext().
                getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(metrics);
        mScreenWidth = metrics.widthPixels;
        /*
        将dp转化为px
        * */

    }

    public SlideMenu(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public SlideMenu(Context context) {
        this(context, null);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {

        super.onLayout(changed, l, t, r, b);

        if (changed) {
            scrollTo(mMenuWidth, 0);
        }
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {

        if (!once) {
            mWapper = (LinearLayout) getChildAt(0);
            mMenu = (ViewGroup) mWapper.getChildAt(0);
            mMain = (ViewGroup) mWapper.getChildAt(1);
            mMenuWidth = mMenu.getLayoutParams().width = mScreenWidth - mRightPadding;
            mMain.getLayoutParams().width = mScreenWidth;
            once = true;
        }
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {

        int action = ev.getAction();

        switch (action) {
            case MotionEvent.ACTION_UP:
                int scrollX = getScrollX();
                if (isOpen == true) {
                    if (scrollX >= mMenuWidth / 6) {
                        this.smoothScrollTo(mMenuWidth, 0);
                        isOpen = false;
                    } else {
                        this.smoothScrollTo(0, 0);
                        isOpen = true;
                    }
                } else if (isOpen == false) {
                    if (scrollX >= (6 * mMenuWidth) / 7) {
                        this.smoothScrollTo(mMenuWidth, 0);
                        isOpen = false;
                    } else {
                        this.smoothScrollTo(0, 0);
                        isOpen = true;
                    }

                }
                return true;
        }
        return super.onTouchEvent(ev);
    }

    @Override
    public boolean onInterceptTouchEvent(MotionEvent ev) {

        switch (ev.getAction()) {
            case MotionEvent.ACTION_DOWN:
                xDistance = yDistance = 0f;
                xLast = ev.getX();
                yLast = ev.getY();
                break;
            case MotionEvent.ACTION_MOVE:
                final float curX = ev.getX();
                final float curY = ev.getY();

                xDistance += Math.abs(curX - xLast);
                yDistance += Math.abs(curY - yLast);
                xLast = curX;
                yLast = curY;
                if (xDistance < yDistance) {
                    return false;
                }
        }
        return super.onInterceptTouchEvent(ev);
    }

    public void Open() {
        if (!isOpen) {
            this.smoothScrollTo(0, 0);
            isOpen = true;
        } else return;
    }

    public void Close() {
        if (isOpen) {
            this.smoothScrollTo(mMenuWidth, 0);
            isOpen = false;
        } else return;
    }

    public void Toggle() {
        if (isOpen) {
            Close();
        } else Open();
    }


}
