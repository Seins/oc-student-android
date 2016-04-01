package s.android.ffmpeg.adapter;

import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.BaseAdapter;
import android.widget.TextView;

import s.android.ffmpeg.R;

/**
 * Created by  CodingMates on 2015/2/3.
 */
public class MenuListAdapter extends BaseAdapter {
    String[] menus = {"在线教室", "课程管理", "上课记录", "个人信息", "其它配置", "退出"};
    Integer[] icons = {R.drawable.icon_online_class, R.drawable.icon_curriculum, R.drawable.icon_history, R.drawable.icon_user, R.drawable.icon_settings, R.drawable.icon_log_out};
    private Context context;

    public MenuListAdapter(Context context) {
        this.context = context;
    }

    public int getCount() {
        return menus.length;
    }

    public Object getItem(int position) {
        return menus[position];
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        TextView textView = new TextView(context);
        AbsListView.LayoutParams lp = new AbsListView.LayoutParams(parent.getWidth(), AbsListView.LayoutParams.WRAP_CONTENT);
        textView.setText(menus[position]);
        textView.setPadding(20, 5, 0, 5);
        textView.setLayoutParams(lp);
        textView.setGravity(Gravity.CENTER);

        Drawable drawable = context.getResources().getDrawable(icons[position]);
        /// 这一步必须要做,否则不会显示.
        drawable.setBounds(0, 0, 32, 32);
        textView.setCompoundDrawables(drawable, null, null, null);
        if (position == icons.length) {
            textView.setTextColor(context.getResources().getColor(R.color.text_red));
        } else {
            textView.setTextColor(context.getResources().getColor(R.color.text_menu_gray));
        }
        return textView;
    }
}
