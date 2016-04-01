package s.android.ffmpeg.gui.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.util.Log;
import android.view.GestureDetector;
import android.view.KeyEvent;
import android.view.MotionEvent;

import java.util.List;

import s.android.ffmpeg.R;

/**
 * 手势操作工具类
 */
public class FlingUIOperatorUtils extends FragmentActivity implements GestureDetector.OnGestureListener {
    private static final Integer FLING_DISTANCE = 150;
    private static final Integer FLING_VELOCITY = 50;
    private GestureDetector gestureDetector;

    private Fragment navigationFragment;

    List<Fragment> fragments = null;

    private FragmentManager manager;

    private FragmentTransaction ft;

    private int curSite = 0;
    private int fragmentSize = 0;

    public void init(Fragment navigationFragment, List<Fragment> fragments, FragmentManager manager, int curSite) {
        this.navigationFragment = navigationFragment;
        this.fragments = fragments;
        this.manager = manager;
        this.curSite = curSite;
        fragmentSize = fragments.size();
        fragments.add(navigationFragment);
    }

    @Override
    public boolean onDown(MotionEvent e) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent e) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent e) {
        return false;
    }

    @Override
    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    @Override
    public void onLongPress(MotionEvent e) {

    }

    @Override
    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {

        if (e1.getY() - e2.getY() > FLING_DISTANCE && velocityY > FLING_VELOCITY) {
            Log.d(FlingUIOperatorUtils.class.getSimpleName(), "弹出导航");
            ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            //纵向滑动 优先弹出菜单
            getByPosition(fragmentSize);
        } else {
            if (curSite == fragmentSize) {
                Log.d(FlingUIOperatorUtils.class.getSimpleName(), "退出导航");
                ft = manager.beginTransaction();
                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
                //在导航界面 退回首个fragment
                if (e1.getX() - e2.getX() > FLING_DISTANCE || e2.getX() - e1.getX() > FLING_DISTANCE) {
                    getByPosition(0);
                }
            } else {
                Log.d(FlingUIOperatorUtils.class.getSimpleName(), "页面切换");
                ft = manager.beginTransaction();
                ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
                if (e1.getX() - e2.getX() > FLING_DISTANCE) {
                    getNext();
                } else if (e2.getX() - e1.getX() > FLING_DISTANCE) {
                    getPre();
                }
            }
        }
        ft.commit();
        return false;
    }

    private void getNext() {
        ft.addToBackStack(curSite + "");
        if (curSite + 1 < fragmentSize) {
            curSite = curSite + 1;
            ft.replace(R.id.contentFg, fragments.get(curSite));
        } else {
            //最后一个跳转到第一个
            curSite = 0;
            ft.replace(R.id.contentFg, fragments.get(curSite));
            ft.addToBackStack(curSite + "");
        }
    }

    private void getPre() {
        ft.addToBackStack(curSite + "");
        if (curSite - 1 >= 0) {
            curSite = curSite - 1;
            ft.replace(R.id.contentFg, fragments.get(curSite));
        } else {
            //第一个 跳转到最后一个
            curSite = fragmentSize - 1;
            ft.replace(R.id.contentFg, fragments.get(curSite));
        }
    }


    private void getByPosition(int position) {
        ft.replace(R.id.contentFg, fragments.get(position));
        ft.addToBackStack(curSite + "");
        curSite = position;
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {

        if (manager.getBackStackEntryCount() > 0) {
            ft = manager.beginTransaction();
            ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
            manager.popBackStack();
            if (curSite - 1 >= 0) {
                curSite -= 1;
            }
            return true;
        } else {
            return super.onKeyUp(keyCode, event);
        }
    }
}
