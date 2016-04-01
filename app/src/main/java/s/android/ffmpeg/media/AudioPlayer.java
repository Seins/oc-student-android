package s.android.ffmpeg.media;

import android.content.Context;
import android.media.AudioTrack;
import android.util.Log;
import android.widget.Toast;

import java.io.BufferedInputStream;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.net.UnknownHostException;

import s.android.ffmpeg.audio.AudioParam;
import s.android.ffmpeg.util.ConstantUtils;
import s.android.ffmpeg.model.LoginMsgModel;
import s.android.ffmpeg.util.MediaUtils;

public class AudioPlayer {
    public static final String CLASS_TAG = "AUDIO_THREAD";
    private static boolean RUNING = true;

    private Context context;
    private InputStream is = null;
    private DataInputStream dis = null;
    private PrintWriter out = null;
    private Socket sk = null;
    private byte[] sockBuf = null;

    // 网络链接参数
    private String cId;
    private String rId;
    private String rm;

    // 音频参数
    private AudioParam mAudioParam;
    private AudioTrack mAudioTrack;
    private int minNalSize;


    // 线程
    Thread pt = null;
    Thread dt = null;

    public AudioPlayer(Context context, String clientId, String roomId,
                       String realm) {
        this.context = context;
        this.cId = clientId;
        this.rId = roomId;
        this.rm = realm;
        RUNING = true;
        initComponent();
    }

    /**
     * @方法名：initComponent
     * @方法描述：初始化
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-10 上午11:05:10
     */
    private void initComponent() {


    }

    public void getData() {
        Runnable r = new Runnable() {
            @Override
            public void run() {
                SocketAddress address = new InetSocketAddress(ConstantUtils.SERVER_MEDIA_IP, ConstantUtils.SERVER_MEDIA_PORT_AUDIO);
                try {
                    sk = new Socket();
                    sk.connect(address);
                } catch (UnknownHostException e1) {
                    // e1.printStackTrace();
                    Log.d(CLASS_TAG, "服务器异常，连接失败");
                    Toast.makeText(context, "服务器异常，连接失败。", Toast.LENGTH_SHORT).show();
                } catch (IOException e1) {
                    // e1.printStackTrace();
                    Log.d(CLASS_TAG, "网络异常");
                    // Toast.makeText(context, "网络异常",
                    // Toast.LENGTH_SHORT).show();
                }
                if (sk.isConnected()) {
                    try {
                        is = sk.getInputStream();
                        dis = new DataInputStream(new BufferedInputStream(is));
                        out = new PrintWriter(new BufferedWriter(
                                new OutputStreamWriter(sk.getOutputStream())),
                                true);
                    } catch (IOException e2) {
                        e2.printStackTrace();
                    }

//                    char[] pk_head = MessagePackageUtil.getHead();
//                    char[] clientId = cId.toCharArray();
//                    char[] roomId = rId.toCharArray();
//                    char[] realm = rm.toCharArray();
                    try {
                        LoginMsgModel msg = new LoginMsgModel(MediaUtils.LOGIN_USER.getAccountNo(), MediaUtils.LOGIN_USER.getAccountNo());

//                        char[] pk_content = MessagePackageUtil.getContent(
//                                clientId, roomId, realm);
                        out.print(msg.getHead());
                        out.flush();
                        Thread.sleep(300);
                        out.print(msg.getContent());
                        out.flush();
                    } catch (Exception e) {
                        Log.i(CLASS_TAG, e.getMessage());
                    }

                    Log.i(CLASS_TAG, "音频线程开始接收数据......");
                    mAudioParam = MediaUtils.getAudioParam();
                    minNalSize = MediaUtils.getAudioPlaySize(mAudioParam);
                    try {
                        mAudioTrack = MediaUtils.createAudioTrack(mAudioParam);
                        mAudioTrack.play();
                    } catch (Exception e1) {
                        Toast.makeText(context, "音频设备初始化失败,请检查音频设备是否正常工作...",
                                Toast.LENGTH_SHORT).show();
                    }
                    while (RUNING && sk.isConnected()) {
                        sockBuf = new byte[minNalSize];
                        int i = 0;
                        try {
                            while (dis.available() > 0 && i < minNalSize) {
                                sockBuf[i] = dis.readByte();
                                i++;
                            }
                        } catch (IOException e1) {
                            e1.printStackTrace();
                        }
                        mAudioTrack.write(sockBuf, 0, sockBuf.length);
                    }

                    Log.d(CLASS_TAG, "音频传输结束");
                    try {
                        // mAudioTrack.stop();
                        destoryResources();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                } else {
                    Log.d(CLASS_TAG, "音频网络线程断开链接，请检查网络尝试重新开启...");
                }

            }
        };
        dt = new Thread(r);
        dt.start();
        // 开启双缓冲队列数据缓冲线程
        // adbl.check();
    }


    /**
     * @throws IOException
     * @方法名：destoryResources
     * @方法描述：资源回收函数
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午11:36:13
     */
    public void destoryResources() throws IOException {
        // 线程释放
        if (dt != null) {
            dt.interrupt();
            dt = null;
        }
        try {
            sk.close();

        } catch (IOException e1) {
            e1.printStackTrace();
        }
        if (pt != null) {
            pt.interrupt();
            pt = null;
        }

        // 关闭流
        if (dis != null) {
            dis.close();
        }
        if (is != null) {
            is.close();
        }
        Log.i(CLASS_TAG, "音频线程释放资源......结束");
    }

    public static void stop() {
        if (RUNING == true) {
            RUNING = false;
        }
    }
}
