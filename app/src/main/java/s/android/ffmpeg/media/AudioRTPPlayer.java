package s.android.ffmpeg.media;

import android.content.Context;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.Toast;

import java.net.DatagramSocket;

import s.android.ffmpeg.audio.AudioParam;
import s.android.ffmpeg.util.ConstantUtils;
import s.android.ffmpeg.model.LoginMsgModel;
import s.android.ffmpeg.rtp.DataFrame;
import s.android.ffmpeg.rtp.Participant;
import s.android.ffmpeg.rtp.RTPAppIntf;
import s.android.ffmpeg.rtp.RTPSession;
import s.android.ffmpeg.util.MediaUtils;

/**
 * 作者： roean
 * 创建时间： 2015/3/19 22:13.
 */
public class AudioRTPPlayer implements RTPAppIntf {
    public static final String CLASS_TAG = "AUDIO_THREAD";
    public static boolean RUNING = true;
    private static final int CACHE_SIZE = 8192;
    private static final int AUDIO_DATA_LENGTH = 1016;
    private static final int AUDIO_DATA_CACHE_SIZE = 4096;
    //    private static FFmpegNative FFMPEG;
    private Context context;

    // 音频参数
    private AudioParam mAudioParam;
    private AudioTrack mAudioTrack;
    private int minNalSize;

    //aac音频文件解码参数
//    private Register register;
//    private Decoder aacDecoder;


    private byte[] nalBuf = new byte[CACHE_SIZE];
    private int useNal = 0;

    // 线程
    Thread dt = null;

    public AudioRTPPlayer(Context context) {
        this.context = context;
    }


    public void start() {
        RUNING = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                try {
                    if (rtpSocket == null) {
                        initComponent();
                    } else {
                        context.getMainLooper().prepare();
                        Toast.makeText(context, "音频线程启动失败，无法链接Socket!", Toast.LENGTH_SHORT).show();
                        Log.e(CLASS_TAG, "Connect to Audio Server Failed,Socket was NUL嗯!");
                    }
                } catch (Exception ex) {
                    Log.e(CLASS_TAG, "Connect to Audio Server Failed!", ex);
                    ex.printStackTrace();
                }
            }
        };
        dt = new Thread(r);
        dt.start();
    }


    private static DatagramSocket rtpSocket;
    private static DatagramSocket rtcpSocket = null;
    private static RTPSession rtpSession;
    private static Participant participant;


    /**
     * @方法名：initComponent
     * @方法描述：初始化
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-10 上午11:05:10
     */
    private void initComponent() throws Exception {
        Log.d(CLASS_TAG, "Initial Audio Player!");
        rtpSocket = new DatagramSocket(ConstantUtils.SERVER_MEDIA_PORT_AUDIO_RTP);
        rtcpSocket = new DatagramSocket(ConstantUtils.SERVER_MEDIA_PORT_AUDIO_RTCP);
        rtpSession = new RTPSession(rtpSocket, rtpSocket);
        participant = new Participant(ConstantUtils.SERVER_MEDIA_IP, ConstantUtils.SERVER_MEDIA_PORT_AUDIO_RTP, ConstantUtils.SERVER_MEDIA_PORT_AUDIO_RTCP);
        rtpSession.addParticipant(participant);
        rtpSession.naivePktReception(true);
        rtpSession.RTPSessionRegister(this, null, null);
        /*链接协议数据发送*/
//        byte[] data = MessagePackageUtil.getContentByRTP(Integer.parseInt(cId), Integer.parseInt(rId), 0, new byte[0]);
        LoginMsgModel msg = new LoginMsgModel("coach", MediaUtils.LOGIN_USER.getAccountNo());

        rtpSession.sendData(msg.getHead());
        rtpSession.sendData(msg.getContent());
        Log.d(CLASS_TAG, "Connect to Audio Server ,Send Data 【" + msg.toString() + "】");

        /*aac音频解码器初始化*/
//        register = new Register();
//        aacDecoder = new Decoder();
//        FFMPEG = new FFmpegNative();
//        if (FFMPEG.audioDecoderInit() < 0) {
//            throw new Exception("FFMPEG AAC Decoder Initial  FAILED...");
//        }
//        if(!aacDecoder.open()){
//            Log.d(CLASS_TAG,"aac音频解码器启动失败!");
//            aacDecoder = null;
//            throw new Exception("aac decoder open failed!");
//        }

         /*播放器初始化*/
        mAudioParam = MediaUtils.getAudioParam();
        minNalSize = MediaUtils.getAudioPlaySize(mAudioParam);
        mAudioTrack = MediaUtils.createAudioTrack(mAudioParam);
        Log.d(CLASS_TAG, "Initial AudioPlayer Finish!");
    }

    public static boolean playFlag = false;

    /*RTP接受到数据*/
    @Override
    public void receiveData(DataFrame frame, Participant participant) {
        Log.d(CLASS_TAG, "接收音频数据成功，开始解码");
        if (mAudioTrack != null) {
            byte[] data = frame.getConcatenatedData();
            /*剥离出音频数据*/
            byte[] audioData = new byte[AUDIO_DATA_LENGTH];
            System.arraycopy(data, 8, audioData, 0, AUDIO_DATA_LENGTH);
            /*拼凑8092*/
            int readCount;
            int usedAD = 0;
            while (usedAD < AUDIO_DATA_LENGTH) {
                readCount = mergeAudioNal(nalBuf, audioData, useNal, usedAD, AUDIO_DATA_CACHE_SIZE);
                useNal += readCount;
                usedAD += readCount;
                if (useNal >= AUDIO_DATA_CACHE_SIZE) {
//                    System.arraycopy(nalBuf, 0, aacDecoder.inbuf, 0, AUDIO_DATA_CACHE_SIZE);
//                    int n2 = aacDecoder.decode(AUDIO_DATA_CACHE_SIZE);
                    byte[] outData = new byte[AUDIO_DATA_CACHE_SIZE * 2 * 2];
//                    int n2 = FFMPEG.decodeAudioData(nalBuf, AUDIO_DATA_CACHE_SIZE, outData);
//                    if (n2 < 0) {
//                        Log.d(CLASS_TAG, "Decoder AAC Data Failed【" + n2 + "】");
//                    } else {
//                        if (playFlag == false) {
//                            mAudioTrack.play();
//                            playFlag = true;
//                        }
//                        Log.d(CLASS_TAG, "Decoder AAC Data length：" + n2);
//                        mAudioTrack.write(outData, 0, n2);
//                    }
                    //清空总计数器和数组
                    useNal = 0;
                    nalBuf = new byte[8092];
                }
            }
        }
    }

    @Override
    public void userEvent(int type, Participant[] participant) {

    }

    @Override
    public int frameSize(int payloadType) {
        return 1;
    }

    /**
     * @param nal        音频最小单元块
     * @param src        接受的数据单元块
     * @param nalCurSite 当前单元块已经填充的位置
     * @param srcCurSite 当前网络数据流已被取出的位置
     * @return
     */
    public int mergeAudioNal(byte[] nal, byte[] src, int nalCurSite, int srcCurSite, int maxLength) {
        int i = 0;
        while (nalCurSite < maxLength) {
            if (srcCurSite < src.length) {
                nal[nalCurSite] = src[srcCurSite];
                nalCurSite += 1;
                srcCurSite += 1;
                i = i + 1;
            } else {
                break;
            }
        }
        return i;
    }

    public static void stop() {
        Log.d(CLASS_TAG, "Release AudioPlayer Sources!");
        if (rtpSession != null) {
//            rtpSession.removeParticipant(participant);
//            participant = null;
            rtpSession.endSession();
        }
//        if (FFMPEG != null) {
//            FFMPEG.releaseAudioDecoder();
//        }
        Log.d(CLASS_TAG, "Release AudioPlayer Sources【FINISH】");
    }
}
