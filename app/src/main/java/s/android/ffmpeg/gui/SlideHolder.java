package s.android.ffmpeg.gui;

import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.Rect;
import android.graphics.Region.Op;
import android.os.Build;
import android.util.AttributeSet;
import android.view.GestureDetector;
import android.view.GestureDetector.SimpleOnGestureListener;
import android.view.MotionEvent;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.DecelerateInterpolator;
import android.view.animation.Transformation;
import android.widget.FrameLayout;

import java.util.LinkedList;
import java.util.Queue;

public class SlideHolder extends FrameLayout {

    public final static int DIRECTION_LEFT = 1;
    public final static int DIRECTION_RIGHT = -1;

    protected final static int MODE_READY = 0;
    protected final static int MODE_SLIDE = 1;
    protected final static int MODE_FINISHED = 2;

    private Bitmap mCachedBitmap;
    private Canvas mCachedCanvas;
    private Paint mCachedPaint;
    private View mMenuView;

    private int mMode = SlideHolder.MODE_READY;
    /**
     * left 1, right -1
     */
    private int mDirection = SlideHolder.DIRECTION_LEFT;
    private float mSpeed = 1.2f;

    private int mOffset = 0;

    private int mStartOffset;

    private int mEndOffset;

    private boolean mEnabled = true;
    private boolean mInterceptTouch = true;
    private boolean mAlwaysOpened = false;
    private boolean mDispatchWhenOpened = false;

    private Queue<Runnable> mWhenReady = new LinkedList<Runnable>();

    private OnSlideListener mListener;

    private GestureDetector mGestureDetector = new GestureDetector(getContext(), new SildeGestureListener());

    public SlideHolder(Context context) {
        super(context);

        initView();
    }

    public SlideHolder(Context context, AttributeSet attrs) {
        super(context, attrs);

        initView();
    }

    public SlideHolder(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        initView();
    }

    private void initView() {
        mCachedPaint = new Paint(Paint.ANTI_ALIAS_FLAG | Paint.FILTER_BITMAP_FLAG | Paint.DITHER_FLAG);
    }

    @Override
    public void setEnabled(boolean enabled) {
        mEnabled = enabled;
    }

    @Override
    public boolean isEnabled() {
        return mEnabled;
    }

    /**
     * @param direction - direction in which SlideHolder opens. Can be: DIRECTION_LEFT, DIRECTION_RIGHT
     */
    public void setDirection(int direction) {
        closeImmediately();

        mDirection = direction;
    }

    /**
     * @param allow - if false, SlideHolder won't react to swiping gestures (but still will be able to work by manually
     *              invoking mathods)
     */
    public void setAllowInterceptTouch(boolean allow) {
        mInterceptTouch = allow;
    }

    public boolean isAllowedInterceptTouch() {
        return mInterceptTouch;
    }

    /**
     * @param dispatch - if true, in open state SlideHolder will dispatch touch events to main layout (in other words - it
     *                 will be clickable)
     */
    public void setDispatchTouchWhenOpened(boolean dispatch) {
        mDispatchWhenOpened = dispatch;
    }

    public boolean isDispatchTouchWhenOpened() {
        return mDispatchWhenOpened;
    }

    /**
     * @param opened - if true, SlideHolder will always be in opened state (which means that swiping won't work)
     */
    public void setAlwaysOpened(boolean opened) {
        mAlwaysOpened = opened;

        requestLayout();
    }

    public int getMenuOffset() {
        return mOffset;
    }

    public void setOnSlideListener(OnSlideListener lis) {
        mListener = lis;
    }

    public boolean isOpened() {
        return mAlwaysOpened || mMode == SlideHolder.MODE_FINISHED;
    }

    public void toggle(boolean immediately) {
        if (immediately) {
            toggleImmediately();
        } else {
            toggle();
        }
    }

    public void toggle() {
        if (isOpened()) {
            close();
        } else {
            open();
        }
    }

    public void toggleImmediately() {
        if (isOpened()) {
            closeImmediately();
        } else {
            openImmediately();
        }
    }

    public boolean open() {
        if (isOpened() || mAlwaysOpened || mMode == SlideHolder.MODE_SLIDE) {
            return false;
        }

        if (!isReadyForSlide()) {
            mWhenReady.add(new Runnable() {

                @Override
                public void run() {
                    open();
                }
            });

            return true;
        }

        initSlideMode();

        Animation anim = new SlideAnimation(mOffset, mEndOffset, mSpeed);
        anim.setAnimationListener(mOpenListener);
        startAnimation(anim);

        invalidate();

        return true;
    }

    public boolean openImmediately() {
        if (isOpened() || mAlwaysOpened || mMode == SlideHolder.MODE_SLIDE) {
            return false;
        }

        if (!isReadyForSlide()) {
            mWhenReady.add(new Runnable() {

                @Override
                public void run() {
                    openImmediately();
                }
            });

            return true;
        }

        mMenuView.setVisibility(View.VISIBLE);
        mMode = SlideHolder.MODE_FINISHED;
        requestLayout();

        if (mListener != null) {
            mListener.onSlideCompleted(true);
        }

        return true;
    }

    public boolean close() {
        if (!isOpened() || mAlwaysOpened || mMode == SlideHolder.MODE_SLIDE) {
            return false;
        }

        if (!isReadyForSlide()) {
            mWhenReady.add(new Runnable() {

                @Override
                public void run() {
                    close();
                }
            });

            return true;
        }

        initSlideMode();

        Animation anim = new SlideAnimation(mOffset, mEndOffset, mSpeed);
        anim.setAnimationListener(mCloseListener);
        startAnimation(anim);

        invalidate();

        return true;
    }

    public boolean closeImmediately() {
        if (!isOpened() || mAlwaysOpened || mMode == SlideHolder.MODE_SLIDE) {
            return false;
        }

        if (!isReadyForSlide()) {
            mWhenReady.add(new Runnable() {

                @Override
                public void run() {
                    closeImmediately();
                }
            });

            return true;
        }

        mMenuView.setVisibility(View.GONE);
        mMode = SlideHolder.MODE_READY;
        requestLayout();

        if (mListener != null) {
            mListener.onSlideCompleted(false);
        }

        return true;
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        final int parentLeft = 0;
        final int parentTop = 0;
        final int parentRight = r - l;
        final int parentBottom = b - t;

        View menu = getChildAt(0);
        int menuWidth = menu.getMeasuredWidth();

        if (mDirection == SlideHolder.DIRECTION_LEFT) {
            menu.layout(parentLeft, parentTop, parentLeft + menuWidth, parentBottom);
        } else {
            menu.layout(parentRight - menuWidth, parentTop, parentRight, parentBottom);
        }

        if (mAlwaysOpened) {
            if (mDirection == SlideHolder.DIRECTION_LEFT) {
                mOffset = menuWidth;
            } else {
                mOffset = 0;
            }
        } else if (mMode == SlideHolder.MODE_FINISHED) {
            mOffset = mDirection * menuWidth;
        } else if (mMode == SlideHolder.MODE_READY) {
            mOffset = 0;
        }

        View main = getChildAt(1);
        main.layout(parentLeft + mOffset, parentTop, parentLeft + mOffset + main.getMeasuredWidth(), parentBottom);

        invalidate();

        Runnable rn;
        while ((rn = mWhenReady.poll()) != null) {
            rn.run();
        }
    }

    private boolean isReadyForSlide() {
        return getWidth() > 0 && getHeight() > 0;
    }

    @Override
    protected void onMeasure(int wSp, int hSp) {
        mMenuView = getChildAt(0);

        if (mAlwaysOpened) {
            View main = getChildAt(1);

            if (mMenuView != null && main != null) {
                measureChild(mMenuView, wSp, hSp);
                LayoutParams lp = (LayoutParams) main.getLayoutParams();

                if (mDirection == SlideHolder.DIRECTION_LEFT) {
                    lp.leftMargin = mMenuView.getMeasuredWidth();
                } else {
                    lp.rightMargin = mMenuView.getMeasuredWidth();
                }
            }
        }

        super.onMeasure(wSp, hSp);
    }

    private byte mFrame = 0;

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @Override
    protected void dispatchDraw(Canvas canvas) {
        try {
            if (mMode == SlideHolder.MODE_SLIDE) {
                View main = getChildAt(1);
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB) {
                    /*
                     * On new versions we redrawing main layout only if it's marked as dirty
					 */
                    if (main.isDirty()) {
                        mCachedCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                        main.draw(mCachedCanvas);
                    }
                } else {
					/*
					 * On older versions we just redrawing our cache every 5th frame
					 */
                    if (++mFrame % 5 == 0) {
                        mCachedCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
                        main.draw(mCachedCanvas);
                    }
                }

				/*
				 * Draw only visible part of menu
				 */

                View menu = getChildAt(0);
                final int scrollX = menu.getScrollX();
                final int scrollY = menu.getScrollY();

                canvas.save();

                if (mDirection == SlideHolder.DIRECTION_LEFT) {
                    canvas.clipRect(0, 0, mOffset, menu.getHeight(), Op.REPLACE);
                } else {
                    int menuWidth = menu.getWidth();
                    int menuLeft = menu.getLeft();

                    canvas.clipRect(menuLeft + menuWidth + mOffset, 0, menuLeft + menuWidth, menu.getHeight());
                }

                canvas.translate(menu.getLeft(), menu.getTop());
                canvas.translate(-scrollX, -scrollY);

                menu.draw(canvas);

                canvas.restore();

                canvas.drawBitmap(mCachedBitmap, mOffset, 0, mCachedPaint);
            } else {
                if (!mAlwaysOpened && mMode == SlideHolder.MODE_READY) {
                    mMenuView.setVisibility(View.GONE);
                }

                super.dispatchDraw(canvas);
            }
        } catch (IndexOutOfBoundsException e) {
			/*
			 * Possibility of crashes on some devices (especially on Samsung). Usually, when ListView is empty.
			 */
        }
    }

    private boolean mCloseOnRelease = false;

    private boolean mDirectionInitialized = false;
    private boolean mIsHaveFiling = false;
    // true right, false left
    private boolean mFilingDirection = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {

        if ((!mEnabled || !mInterceptTouch) && mMode == SlideHolder.MODE_READY || mAlwaysOpened) {
            return super.dispatchTouchEvent(ev);
        }

        if (mMode != SlideHolder.MODE_FINISHED) {
            onTouchEvent(ev);

            if (mMode == SlideHolder.MODE_SLIDE) {
                MotionEvent cancelEvent = MotionEvent.obtain(ev);
                cancelEvent.setAction(MotionEvent.ACTION_CANCEL);
                super.dispatchTouchEvent(cancelEvent);
                cancelEvent.recycle();
            } else {
                super.dispatchTouchEvent(ev);
            }

            return true;
        } else {
            final int action = ev.getAction();

            Rect rect = new Rect();
            View menu = getChildAt(0);
            menu.getHitRect(rect);

            if (!rect.contains((int) ev.getX(), (int) ev.getY())) {
                if (action == MotionEvent.ACTION_UP && mCloseOnRelease && !mDispatchWhenOpened) {
                    close();
                    mCloseOnRelease = false;
                } else {
                    if (action == MotionEvent.ACTION_DOWN && !mDispatchWhenOpened) {
                        mCloseOnRelease = true;
                    }

                    onTouchEvent(ev);
                }

                if (mDispatchWhenOpened) {
                    super.dispatchTouchEvent(ev);
                }

                return true;
            } else {
                try {
                    onTouchEvent(ev);

                    ev.offsetLocation(-menu.getLeft(), -menu.getTop());
                    menu.dispatchTouchEvent(ev);

                    return true;
                } catch (IllegalArgumentException e) {
					/*
					 * Possibility of crashes on some devices (especially on Samsung).
					 */
                    return super.dispatchTouchEvent(ev);
                }
            }
        }
    }

    private boolean handleTouchEventFixed(MotionEvent event) {
        if (!mEnabled) {
            return false;
        }

        boolean handle = false;
        if (event.getAction() != MotionEvent.ACTION_CANCEL) {
            handle = mGestureDetector.onTouchEvent(event);
        }

        if (event.getAction() == MotionEvent.ACTION_UP) {
            if (mMode == SlideHolder.MODE_SLIDE) {
                if (mIsHaveFiling) {
                    finshSildeWithFiling(mFilingDirection);
                } else {
                    finishSlide();
                }
            }
            mCloseOnRelease = false;
            return false;

        } else {
            return handle;
        }
    }

    @Override
    public boolean onTouchEvent(MotionEvent ev) {
        boolean handled = handleTouchEventFixed(ev);

        invalidate();

        return handled;
    }

    class SildeGestureListener extends SimpleOnGestureListener {

        /*
         * (non-Javadoc)
         * @see android.view.GestureDetector.SimpleOnGestureListener#onDown(android.view.MotionEvent)
         */
        @Override
        public boolean onDown(MotionEvent e) {
            mDirectionInitialized = false;
            mIsHaveFiling = false;
            return true;
        }

        /*
         * (non-Javadoc)
         * @see android.view.GestureDetector.SimpleOnGestureListener#onScroll(android.view.MotionEvent,
         * android.view.MotionEvent, float, float)
         */
        @Override
        public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {

            if ((mMode == SlideHolder.MODE_READY || mMode == SlideHolder.MODE_FINISHED) && !mDirectionInitialized) {
                mDirectionInitialized = true;
                if (Math.abs(distanceX) > Math.abs(distanceY)) {
                    initSlideMode();
                }
            } else if (mMode == SlideHolder.MODE_SLIDE) {
                mOffset -= distanceX;

                if (!isSlideAllowed()) {
                    finishSlide();
                }
            } else {
                return false;
            }

            return mMode == SlideHolder.MODE_SLIDE;
        }

        /*
         * (non-Javadoc)
         * @see android.view.GestureDetector.SimpleOnGestureListener#onFling(android.view.MotionEvent,
         * android.view.MotionEvent, float, float)
         */
        @Override
        public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
            mIsHaveFiling = true;
            if (velocityX > 0) {
                mFilingDirection = true;
            } else {
                mFilingDirection = false;
            }
            return false;
        }
    }

    private void initSlideMode() {
        mCloseOnRelease = false;

        View v = getChildAt(1);

        if (mMode == SlideHolder.MODE_READY) {
            mStartOffset = 0;
            mEndOffset = mDirection * getChildAt(0).getWidth();
        } else {
            mStartOffset = mDirection * getChildAt(0).getWidth();
            mEndOffset = 0;
        }

        mOffset = mStartOffset;

        if (mCachedBitmap == null || mCachedBitmap.isRecycled() || mCachedBitmap.getWidth() != v.getWidth()) {
            mCachedBitmap = Bitmap.createBitmap(v.getWidth(), v.getHeight(), Bitmap.Config.ARGB_8888);
            mCachedCanvas = new Canvas(mCachedBitmap);
        } else {
            mCachedCanvas.drawColor(Color.TRANSPARENT, Mode.CLEAR);
        }

        v.setVisibility(View.VISIBLE);

        mCachedCanvas.translate(-v.getScrollX(), -v.getScrollY());
        v.draw(mCachedCanvas);

        mMode = SlideHolder.MODE_SLIDE;

        mMenuView.setVisibility(View.VISIBLE);
    }

    private boolean isSlideAllowed() {
        return mDirection * mEndOffset > 0 && mDirection * mOffset < mDirection * mEndOffset
                && mDirection * mOffset >= mDirection * mStartOffset || mEndOffset == 0
                && mDirection * mOffset > mDirection * mEndOffset && mDirection * mOffset <= mDirection * mStartOffset;
    }

    private void completeOpening() {
        mOffset = mDirection * mMenuView.getWidth();
        requestLayout();

        post(new Runnable() {

            @Override
            public void run() {
                mMode = SlideHolder.MODE_FINISHED;
                mMenuView.setVisibility(View.VISIBLE);
            }
        });

        if (mListener != null) {
            mListener.onSlideCompleted(true);
        }
    }

    private Animation.AnimationListener mOpenListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            completeOpening();
        }
    };

    private void completeClosing() {
        mOffset = 0;
        requestLayout();

        post(new Runnable() {

            @Override
            public void run() {
                mMode = SlideHolder.MODE_READY;
                mMenuView.setVisibility(View.GONE);
            }
        });

        if (mListener != null) {
            mListener.onSlideCompleted(false);
        }
    }

    private Animation.AnimationListener mCloseListener = new Animation.AnimationListener() {

        @Override
        public void onAnimationStart(Animation animation) {
        }

        @Override
        public void onAnimationRepeat(Animation animation) {
        }

        @Override
        public void onAnimationEnd(Animation animation) {
            completeClosing();
        }
    };

    private void finshSildeWithFiling(boolean direction) {
        if (mDirection * mEndOffset > 0) {
            if (direction) {
                if (mDirection * mOffset > mDirection * mEndOffset) {
                    mOffset = mEndOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mEndOffset, mSpeed);
                anim.setAnimationListener(mOpenListener);
                startAnimation(anim);
            } else {
                if (mDirection * mOffset < mDirection * mStartOffset) {
                    mOffset = mStartOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mStartOffset, mSpeed);
                anim.setAnimationListener(mCloseListener);
                startAnimation(anim);
            }
        } else {
            if (!direction) {
                if (mDirection * mOffset < mDirection * mEndOffset) {
                    mOffset = mEndOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mEndOffset, mSpeed);
                anim.setAnimationListener(mCloseListener);
                startAnimation(anim);
            } else {
                if (mDirection * mOffset > mDirection * mStartOffset) {
                    mOffset = mStartOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mStartOffset, mSpeed);
                anim.setAnimationListener(mOpenListener);
                startAnimation(anim);
            }
        }
    }

    private void finishSlide() {
        // �˵�����ߵ����
        if (mDirection * mEndOffset > 0) {
            // �ƶ������룬�򿪲˵������
            if (mDirection * mOffset > mDirection * mEndOffset / 2) {
                if (mDirection * mOffset > mDirection * mEndOffset) {
                    mOffset = mEndOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mEndOffset, mSpeed);
                anim.setAnimationListener(mOpenListener);
                startAnimation(anim);
            } else {
                if (mDirection * mOffset < mDirection * mStartOffset) {
                    mOffset = mStartOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mStartOffset, mSpeed);
                anim.setAnimationListener(mCloseListener);
                startAnimation(anim);
            }
        } else {
            if (mDirection * mOffset < mDirection * mStartOffset / 2) {
                if (mDirection * mOffset < mDirection * mEndOffset) {
                    mOffset = mEndOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mEndOffset, mSpeed);
                anim.setAnimationListener(mCloseListener);
                startAnimation(anim);
            } else {
                if (mDirection * mOffset > mDirection * mStartOffset) {
                    mOffset = mStartOffset;
                }

                Animation anim = new SlideAnimation(mOffset, mStartOffset, mSpeed);
                anim.setAnimationListener(mOpenListener);
                startAnimation(anim);
            }
        }
    }

    public void setSpeed(float speed) {
        if (speed <= 0) {
            return;
        }
        mSpeed = speed;
    }

    private class SlideAnimation extends Animation {

        private float mStart;
        private float mEnd;

        public SlideAnimation(float fromX, float toX, float speed) {

            mStart = fromX;
            mEnd = toX;

            setInterpolator(new DecelerateInterpolator());

            float duration = Math.abs(mEnd - mStart) / speed;
            setDuration((long) duration);
        }

        @Override
        protected void applyTransformation(float interpolatedTime, Transformation t) {
            super.applyTransformation(interpolatedTime, t);

            float offset = (mEnd - mStart) * interpolatedTime + mStart;
            mOffset = (int) offset;

            postInvalidate();
        }

    }

    public static interface OnSlideListener {
        public void onSlideCompleted(boolean opened);
    }
}
