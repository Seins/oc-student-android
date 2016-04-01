package s.android.ffmpeg.util;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioTrack;
import android.util.Log;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import s.android.ffmpeg.audio.AudioParam;
import s.android.ffmpeg.media.AudioRTPPlayer;
import s.android.ffmpeg.media.VideoPlayer;
import s.android.ffmpeg.model.TMemberModel;

public class MediaUtils {
    public static final String CLASS_TAG = "Common_Utils";

    public static TMemberModel LOGIN_USER = new TMemberModel();

    public static final String[] USERT_REAML = {"student"};

    public static final List<String> USER_MENU = new ArrayList<String>();

    public static AudioRTPPlayer T_AUDIO = null;
    public static VideoPlayer T_VIDEO = null;

    // 音频较优播放块大小
    public static int mPrimePlaySize = 0;
    public static AudioTrack mAudioTrack = null;

    /**
     * @方法描述：初始化同步主线程
     * @返回类型：int 1：成功 2：线程存在 3线程残留 0初始化异常
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午10:59:29
     */
    public static synchronized void initialMainThread(AudioRTPPlayer al, VideoPlayer vl) throws Exception
    {
        T_AUDIO = al;
        T_VIDEO = vl;
        Log.d(CLASS_TAG, "初始化线程...");
    }

    /**
     * @throws IOException
     * @throws Exception
     * @方法名：startMainThread
     * @方法描述：启动主线程
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午11:15:32
     */
    public static synchronized void startMainThread() throws IOException
    {
        T_AUDIO.start();
        T_VIDEO.start();
    }

    /**
     * @return
     * @throws IOException
     * @方法名：destoryMainThread
     * @方法描述：
     * @返回类型：int 0:异常 1：成功
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午11:08:32
     */
    public static synchronized int destoryMainThread()
    {
        VideoPlayer.stop();
        AudioRTPPlayer.stop();
        if (mAudioTrack != null) {
            MediaUtils.releaseAudioTrack();
        }
        Log.d(CLASS_TAG, "回收同步线程...");
        return 1;

    }

    /**
     * @return
     * @方法名：getAudioParam
     * @方法描述：获取音频播放器参数
     * @返回类型：AudioParam
     * @修改人:邓风森
     * @修改时间:2014-12-1 下午11:44:57
     */
    public static synchronized AudioParam getAudioParam()
    {
        AudioParam audioParam = new AudioParam();
        audioParam.mFrequency = 44100;
        audioParam.mChannel = AudioFormat.CHANNEL_IN_STEREO;
        audioParam.mSampBit = AudioFormat.ENCODING_PCM_16BIT;
        return audioParam;
    }

    /**
     * @param mAudioParam
     * @return
     * @throws Exception
     * @方法名：createAudioTrack
     * @方法描述：音频播放器单例获取
     * @返回类型：AudioTrack
     * @修改人:邓风森
     * @修改时间:2014-11-9 上午12:46:10
     */
    public static synchronized AudioTrack createAudioTrack(AudioParam mAudioParam) throws Exception
    {
        if (mAudioTrack != null) {
            return mAudioTrack;
        } else {
            int minBufSize = AudioTrack.getMinBufferSize(
                    mAudioParam.mFrequency, mAudioParam.mChannel,
                    mAudioParam.mSampBit);
            mAudioTrack = new AudioTrack(AudioManager.STREAM_MUSIC,
                    mAudioParam.mFrequency, mAudioParam.mChannel,
                    mAudioParam.mSampBit, minBufSize, AudioTrack.MODE_STREAM);
            return mAudioTrack;
        }

    }

    /**
     * @param mAudioParam
     * @return
     * @方法名：getAudioPlaySize
     * @方法描述：根据参数获取每秒播放音频区块大小
     * @返回类型：int
     * @修改人:邓风森
     * @修改时间:2014-12-1 下午11:45:18
     */
    public static synchronized int getAudioPlaySize(AudioParam mAudioParam)
    {
        int minBufSize = AudioTrack.getMinBufferSize(mAudioParam.mFrequency,
                mAudioParam.mChannel, mAudioParam.mSampBit);
        return mPrimePlaySize = minBufSize * 2;
    }

    /**
     * @方法名：releaseAudioTrack
     * @方法描述：音频播放器释放
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-9 上午12:46:38
     */
    public static synchronized void releaseAudioTrack()
    {
        if (mAudioTrack != null) {
            mAudioTrack.stop();
            mAudioTrack.release();
            mAudioTrack = null;
        }
    }
}
