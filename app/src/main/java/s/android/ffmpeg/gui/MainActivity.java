package s.android.ffmpeg.gui;

import android.app.Activity;
import android.content.Context;
import android.media.AudioManager;
import android.os.Bundle;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.view.WindowManager;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.Date;

import s.android.ffmpeg.R;
import s.android.ffmpeg.media.AudioRTPPlayer;
import s.android.ffmpeg.media.VideoPlayer;
import s.android.ffmpeg.util.MediaUtils;

@SuppressWarnings("deprecation")
public class MainActivity extends Activity {
    static final String CLASS_TAG = "Main_Activity";
    public static Date startTime;
    public static TextView timeTv;

    private static RelativeLayout timeTask;
    private static RelativeLayout courseTitle;
    private static SeekBar volumeSb;
    private static AudioManager audioManager;
    private static int curVolume;
    private static SimpleDateFormat dfs;

    private TextView courseName;
    private RelativeLayout contentRl;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON, WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
        //设置窗体背景模糊
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND, WindowManager.LayoutParams.FLAG_BLUR_BEHIND);
        setContentView(R.layout.activity_main);
        dfs = new SimpleDateFormat("HH:mm:ss");
        initComponent();
        joinClass();
    }

    private void initComponent() {
        contentRl = (RelativeLayout) this.findViewById(R.id.contentRL);
        timeTask = (RelativeLayout) this.findViewById(R.id.time_task);
        volumeSb = (SeekBar) this.findViewById(R.id.volumeSb);
        timeTv = (TextView) this.findViewById(R.id.timeTv);
        courseTitle = (RelativeLayout) this.findViewById(R.id.courseTitle);
        courseName = (TextView) this.findViewById(R.id.courseName);

        //获取系统音量控制器
        audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        curVolume = audioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        volumeSb.setMax(maxVolume);
        volumeSb.setProgress(curVolume);

        volumeSb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                changeVolume(progress - curVolume);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
        });
    }

    @Override
    protected void onPause() {
        MediaUtils.destoryMainThread();
        Toast.makeText(getApplicationContext(), "退出在线教室成功！", Toast.LENGTH_SHORT).show();
        this.finish();
        super.onPause();
    }

    protected void joinClass() {

        //获取设备屏幕分辨率
        Display mDisplay = MainActivity.this.getWindowManager().getDefaultDisplay();
        int width = mDisplay.getWidth();
        int height = mDisplay.getHeight();

        //初始化音频与视频线程
        VideoPlayer videoPlayer = new VideoPlayer(getApplicationContext(), width, height);
        AudioRTPPlayer audioRTPPlayer = new AudioRTPPlayer(getApplicationContext());

        //初始化界面元素
        //contentLL.removeAllViews();
        LayoutParams lp = new LayoutParams(width, height);
        contentRl.addView(videoPlayer, lp);
        //将计时器与标题透明浮层放置到视图上层
        timeTask.bringToFront();
        courseTitle.bringToFront();
        //启用线程
        try {
            MediaUtils.initialMainThread(audioRTPPlayer, videoPlayer);
            MediaUtils.startMainThread();
        } catch (Exception e) {
            Log.e(CLASS_TAG, "Start Main Thread failed!");
            e.printStackTrace();
        }
        startTime = new Date();
    }

    /**
     * 更新时间进度
     */
    public static void updatePlayTime() throws Exception{
        timeTv.setText(getPlayTime());
    }

    /**
     * 变更覆盖层状态 隐藏或者显示
     */
    public static void changeHoverStatus() {
        if (timeTask.getVisibility() == View.VISIBLE) {
            timeTask.setVisibility(View.GONE);
        } else {
            timeTask.setVisibility(View.VISIBLE);
        }

        if (courseTitle.getVisibility() == View.VISIBLE) {
            courseTitle.setVisibility(View.GONE);
        } else {
            courseTitle.setVisibility(View.VISIBLE);
        }
    }

    /**
     * 获取进入教室后的参与课堂的时间
     *
     * @return
     */
    public static String getPlayTime() throws Exception {
        Date curTime = new Date();
        Date rst = dfs.parse("00:00:00");
        String timeStr = "上课时间 " + dfs.format(new Date(rst.getTime() + (curTime.getTime() - startTime.getTime())));
        return timeStr;
    }

    public static void changeVolume(int volume) {
        Log.d(CLASS_TAG, "音量变更度：" + volume);
        curVolume = audioManager.getStreamVolume(audioManager.STREAM_MUSIC);
        int maxVolume = audioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);
        if (volume > 0) {
            curVolume = curVolume + volume > maxVolume ? maxVolume : curVolume + volume;
        } else {
            curVolume = curVolume + volume < 1 ? 1 : curVolume + volume;
        }
        audioManager.setStreamVolume(audioManager.STREAM_MUSIC, curVolume, 0);
        volumeSb.setProgress(curVolume);
    }

}
