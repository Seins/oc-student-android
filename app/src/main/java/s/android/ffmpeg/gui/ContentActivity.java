package s.android.ffmpeg.gui;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import s.android.ffmpeg.R;
import s.android.ffmpeg.adapter.MenuListAdapter;
import s.android.ffmpeg.gui.fragment.CurriculumFragment;
import s.android.ffmpeg.gui.fragment.HistoryFragment;
import s.android.ffmpeg.gui.fragment.OnlineRoomFragment;
import s.android.ffmpeg.gui.fragment.UserInfoFragment;

public class ContentActivity extends FragmentActivity {
    //菜单列表
    private ListView menuList;
    private MenuListAdapter menuListAdapter;
    private FragmentManager manager;
    private FragmentTransaction ft;
    //主页面
    private Fragment contentFg;
    private ImageView menu;
    private View menuFg;
    //标记当前的fragment
    public int MARK = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_content);
        menuFg = findViewById(R.id.menuFg);
        manager = getSupportFragmentManager();
        contentFg = manager.findFragmentById(R.id.contentFg);
        menuList = (ListView) findViewById(R.id.menu_main_list);
        menuListAdapter = new MenuListAdapter(getApplicationContext());
        menuList.setAdapter(menuListAdapter);
        menuList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                menuSwitch(position);
            }
        });

        menu = (ImageView) findViewById(R.id.common_top_menu);
        menu.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (menuFg.getVisibility() == View.GONE) {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_in);
                    menuFg.startAnimation(animation);
                    menuFg.setVisibility(View.VISIBLE);
                } else {
                    Animation animation = AnimationUtils.loadAnimation(getApplicationContext(), R.anim.push_left_out);
                    menuFg.startAnimation(animation);
                    menuFg.setVisibility(View.GONE);
                }
            }
        });
    }

    /**
     * @param refId
     */
    public void menuSwitch(int refId) {
        ft = manager.beginTransaction();
        ft.remove(contentFg);
        ft.setCustomAnimations(R.anim.push_right_in, R.anim.push_right_out);
        switch (refId) {
            case 0:
                if (MARK != 0) {
                    ft.replace(R.id.contentFg, new OnlineRoomFragment());
                    setPageTitle("在线教室");
                    MARK = 0;
                }
                break;
            case 1:
                if (MARK != 1) {
                    ft.replace(R.id.contentFg, new CurriculumFragment());
                    setPageTitle("课程管理");
                    MARK = 1;
                }
                break;
            case 2:
                if (MARK != 2) {
                    ft.replace(R.id.contentFg, new HistoryFragment());
                    setPageTitle("上课记录");
                    MARK = 2;
                }
                break;
            case 3:
                if (MARK != 3) {
                    ft.replace(R.id.contentFg, new UserInfoFragment());
                    setPageTitle("个人信息");
                    MARK = 3;
                }
                break;
            case 5:
                finish();
                break;
            default:
                Toast.makeText(getApplicationContext(), menuListAdapter.getItem(refId) + "界面尚未开发完成，敬请期待!", Toast.LENGTH_SHORT).show();
                break;
        }
        ft.setCustomAnimations(R.anim.push_left_in, R.anim.push_left_out);
        ft.commit();
    }

    /**
     * 设置页面标题
     *
     * @param title
     */
    public void setPageTitle(String title) {
        TextView pageTitle = (TextView) findViewById(R.id.page_title);
        pageTitle.setText(title);
    }

    /**
     * 页面重新调起，默认选择第一个fragment【在线教室页面】，后续版本中取消此页面，改为【课表界面】
     */
    protected void onResume() {
        menuSwitch(0);
        super.onResume();
    }
}
