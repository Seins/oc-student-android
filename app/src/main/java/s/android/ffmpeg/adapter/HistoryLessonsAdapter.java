package s.android.ffmpeg.adapter;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.List;

import s.android.ffmpeg.R;
import s.android.ffmpeg.model.LessonDto;

public class HistoryLessonsAdapter extends BaseAdapter {
    private Context context;
    private List<LessonDto> curriculumList;
    Integer[] statusBackground = {R.drawable.item_curriculum_top_line_before, R.drawable.item_curriculum_top_line_ing, R.drawable.item_curriculum_top_line_end};

    public HistoryLessonsAdapter(Context context,
                                 List<LessonDto> curriculumList) {
        super();
        this.context = context;
        this.curriculumList = curriculumList;
    }

    public int getCount() {
        return curriculumList.size();
    }

    public Object getItem(int arg0) {
        return curriculumList.get(arg0);
    }

    public long getItemId(int arg0) {
        return curriculumList.get(arg0).getId();
    }

    public View getView(int position, View arg1, ViewGroup arg2) {
        final View itemView = LayoutInflater.from(context).inflate(R.layout.item_curriculum_list, null);
        TextView lessonTime = (TextView) itemView.findViewById(R.id.lessonTime);
        TextView lessonName = (TextView) itemView.findViewById(R.id.lessonName);
        TextView lessonCoachName = (TextView) itemView.findViewById(R.id.lessonCoachName);
        TextView lessonContent = (TextView) itemView.findViewById(R.id.lessonContent);
        TextView lastTime = (TextView) itemView.findViewById(R.id.lastTime);
        TextView remark = (TextView) itemView.findViewById(R.id.remark);
        ImageView lessonCoachIcon = (ImageView) itemView.findViewById(R.id.lessonCoachIcon);
        //ImageButton enterBtn=(ImageButton) itemView.findViewById(R.id.enterBtn);
        ImageView detailImg = (ImageView) itemView.findViewById(R.id.curriculum_detail_img);
        LessonDto vmc = curriculumList.get(position);
        byte[] iconSrc = vmc.getCoachPhoto();
        if (iconSrc != null) {
            Bitmap iconBmp = BitmapFactory.decodeByteArray(vmc.getCoachPhoto(), 0, vmc.getCoachPhoto().length);
            lessonCoachIcon.setImageBitmap(iconBmp);
        } else {
            lessonCoachIcon.setImageDrawable(itemView.getResources().getDrawable(R.drawable.icon_user));
        }
        DateFormat sdf = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
        lessonTime.setText(sdf.format(vmc.getLessonTime()));
        lessonName.setText(vmc.getCourseName());
        lessonContent.setText(vmc.getLessonContent());
        lastTime.setText(vmc.getLastTime() + "分钟");
        remark.setText(vmc.getRemark());
        RelativeLayout titleLy = (RelativeLayout) itemView.findViewById(R.id.item_title);
        titleLy.setBackgroundDrawable(context.getResources().getDrawable(statusBackground[Integer.parseInt(vmc.getLessonStatus())]));
        detailImg.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                LinearLayout ly = (LinearLayout) itemView.findViewById(R.id.item_info);
                if (ly.getVisibility() == View.GONE) {
                    ly.setVisibility(View.VISIBLE);
                } else {
                    ly.setVisibility(View.GONE);
                }
            }
        });
        return itemView;
    }
}
