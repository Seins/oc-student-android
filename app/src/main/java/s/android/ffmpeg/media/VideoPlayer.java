package s.android.ffmpeg.media;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Rect;
import android.util.Log;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Toast;

import java.io.DataOutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;
import java.net.Socket;
import java.net.SocketAddress;
import java.nio.ByteBuffer;

import s.android.ffmpeg.FFmpegNative;
import s.android.ffmpeg.R;
import s.android.ffmpeg.util.ConstantUtils;
import s.android.ffmpeg.gui.MainActivity;
import s.android.ffmpeg.model.LoginMsgModel;
import s.android.ffmpeg.util.MediaUtils;

public class VideoPlayer extends View implements View.OnTouchListener, GestureDetector.OnGestureListener {
    static final String CLASS_TAG = "VIDEO_THREAD";
    private static boolean RUNNING_FLAG = true;

    //decoder params
    public static final double SCALE = 1.0d;
    private static final FFmpegNative FFMPEG_DECODER = new FFmpegNative();
    private int width = 0;
    private int height = 0;
    private byte[] mPixel;
    private ByteBuffer buffer;
    private Bitmap bitmap = null;
    private int mTrans;
    //stream params
    private InputStream is = null;
    private Socket sk = null;
    private Thread dt = null;
    //touch listener params
    private GestureDetector gestureDetector;


    /**
     * 构造函数
     *
     * @param context
     * @param width
     * @param height
     */
    public VideoPlayer(Context context, int width, int height) {
        super(context);
        setBackgroundColor(getResources().getColor(R.color.white));
        setFocusable(true);

        //initial params
        paramsInit(width, height);

        //initial pixel arrays
        for (int i = 0; i < mPixel.length; i++) {
            mPixel[i] = (byte) 0x00;
        }

        //add voice and light listener
        gestureDetector = new GestureDetector(this);
        gestureDetector.setIsLongpressEnabled(true);
        this.setOnTouchListener(this);
        this.setClickable(true);
        this.setLongClickable(true);
    }

    /**
     * @param width
     * @param height
     * @方法名：paramsInit
     * @方法描述：参数初始化
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午11:35:04
     */
    private void paramsInit(int width, int height) {
        this.width = (int) (width * SCALE);
        this.height = (int) (height * SCALE);
        mPixel = new byte[this.width * this.height * 3];
        buffer = ByteBuffer.wrap(mPixel);
        bitmap = Bitmap.createBitmap(this.width, this.height, Config.RGB_565);
        mTrans = 0x0F0F0F0F;
    }

    /**
     * 初始化连接
     *
     * @throws Exception
     */
    private void initConnect() throws Exception {
        SocketAddress address = new InetSocketAddress("192.168.137.1", ConstantUtils.SERVER_MEDIA_PORT_VIDEO);
        sk = new Socket();
        sk.connect(address);
        if (sk.isConnected()) {
            is = sk.getInputStream();
            DataOutputStream writer = new DataOutputStream(sk.getOutputStream());
            LoginMsgModel msg = new LoginMsgModel("coach", MediaUtils.LOGIN_USER.getAccountNo());
            Log.d(CLASS_TAG, "Video Socket Send Data【" + msg.toString() + "】");
            writer.write(msg.getHead());
            writer.flush();
            Thread.sleep(200);
            writer.write(msg.getContent());
            writer.flush();
        }
        Log.d(CLASS_TAG, "Video InputStream Connection Success!");
    }

    /**
     * 启动视频线程
     */
    public void start() {
        RUNNING_FLAG = true;
        Runnable r = new Runnable() {
            @Override
            public void run() {
                Log.d(CLASS_TAG, "Start Video Thread!");
                int bytesRead;
                int iTemp;
                int nalLen;
                int NalBufUsed = 0;
                boolean bFirst = true;
                byte[] NalBuf = new byte[1024 * 1024];
                byte[] SockBuf = new byte[2048];
                try {
                    initConnect();
                    //初始化jni解码器
                    if (FFMPEG_DECODER.decoderInit(width, height) < 0) {
                        Log.e(CLASS_TAG, "Initial FFMPEG Decoder Failed!");
                        getContext().getMainLooper().prepare();
                        Toast.makeText(getContext(), "初始化解码器失败！", Toast.LENGTH_SHORT).show();
                        clean();
                        return;
                    }
                    //开始取流解码
                    while (RUNNING_FLAG && sk.isConnected()) {
                        if ((bytesRead = is.read(SockBuf, 0, 2048)) > 0) {
                            int SockBufUsed = 0;
                            while (bytesRead - SockBufUsed > 0) {
                                //拼帧
                                nalLen = MergeBuffer(NalBuf, NalBufUsed, SockBuf, SockBufUsed, bytesRead - SockBufUsed);
                                NalBufUsed += nalLen;
                                SockBufUsed += nalLen;

                                //找到帧头
                                while (mTrans == 1) {
                                    mTrans = 0xFFFFFFFF;
                                    if (bFirst == true) {
                                        //第一帧
                                        bFirst = false;
                                        SockBuf = new byte[2048];
                                        NalBuf = new byte[1024 * 1024];
                                        SockBufUsed = 0;
                                        NalBufUsed = 0;
                                    } else {
                                        //其他帧
                                        iTemp = FFMPEG_DECODER.decodeData(NalBuf, NalBufUsed - 4, mPixel);
                                        if (iTemp > 0) {
                                            //更新画面，自动调用onDraw()一次
                                            postInvalidate();
                                        }
                                    }
                                    NalBuf[0] = 0;
                                    NalBuf[1] = 0;
                                    NalBuf[2] = 0;
                                    NalBuf[3] = 1;
                                    NalBufUsed = 4;
                                }
                            }
                        }
                    }
                    Log.i(CLASS_TAG, "Finish Class ,Release Resource And close Room!Thread runing Status 【" + RUNNING_FLAG + "】Socket Status【" + sk.isConnected() + "】");
                    clean();
                } catch (Exception ex) {
                    Log.e(CLASS_TAG, "Video Runing Occured An Error!", ex);
                    try {
                        clean();
                    } catch (Exception e) {
                        Log.e(CLASS_TAG, "Release Video Resources Failed!", ex);
                        e.printStackTrace();
                    }
                    ex.printStackTrace();
                }
            }


        };
        dt = new Thread(r);
        dt.start();
    }

    /**
     * 视图重绘方法，在此处做解析后的视频数据的绘图
     *
     * @param canvas
     */
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        bitmap.copyPixelsFromBuffer(buffer);
        buffer.position(0);
        Matrix matrix = new Matrix();
//        matrix.sc
        //以图片中心作为旋转中心，旋转180°
//        matrix.postScale(1, 1);
//        matrix.setRotate(180, width / 2, height / 2);
//        matrix.postScale(-1, 1);
        Bitmap result = Bitmap.createBitmap(bitmap, 0, 0, this.width, this.height, matrix, true);
        canvas.drawBitmap(result, new Rect(0, 0, result.getWidth(), result.getHeight()), new Rect(0, 0, width, height), null);
        try {
            MainActivity.updatePlayTime();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * @param NalBuf
     * @param NalBufUsed
     * @param SockBuf
     * @param SockBufUsed
     * @param SockRemain
     * @return
     * @方法名：MergeBuffer
     * @方法描述：合并帧（寻找I帧）
     * @返回类型：int 读取数据单元长度
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午11:35:50
     */
    public int MergeBuffer(byte[] NalBuf, int NalBufUsed, byte[] SockBuf, int SockBufUsed, int SockRemain) {
        int i;
        for (i = 0; i < SockRemain; i++) {
            byte Temp = SockBuf[i + SockBufUsed];
            NalBuf[i + NalBufUsed] = Temp;
            mTrans <<= 8;
            mTrans |= Temp;
            if (mTrans == 1) {
                i++;
                break;
            }
        }
        return i;
    }

    /**
     * @方法名：destoryResources
     * @方法描述：资源回收函数
     * @返回类型：void
     * @修改人:邓风森
     * @修改时间:2014-11-3 下午11:36:13
     */
    public void clean() throws Exception {
        Log.d(CLASS_TAG, "Release Video Sources......");
        RUNNING_FLAG = false;
        Thread.sleep(300);
        if (dt != null) {
            dt.interrupt();
            dt = null;
        }
        if (is != null) {
            is.close();
        }
        sk.close();
        FFMPEG_DECODER.releaseDecoder();
        Log.d(CLASS_TAG, "Release Video Sources Success!");
    }

    public boolean onTouch(View v, MotionEvent event) {
        return gestureDetector.onTouchEvent(event);
    }

    public boolean onDown(MotionEvent e) {
        return false;
    }

    public void onShowPress(MotionEvent e) {

    }

    public boolean onSingleTapUp(MotionEvent e) {
        // Log.d(CLASS_TAG, "onSingleTapUp");
        return false;
    }

    public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
        return false;
    }

    public void onLongPress(MotionEvent e) {
        MainActivity.changeHoverStatus();
    }

    public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
        int mid = width / 2;
        final int FLING_MIN_DISTANCE = 20, FLING_MIN_VELOCITY = 40;
        int v_c_r = (int) (e1.getY() - e2.getY()) / 100;
        if (e1.getX() <= mid) {
            //fling in left area
            if (e1.getY() - e2.getY() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                // Fling up
                // Log.d(CLASS_TAG, "亮度增加");
            } else if (e2.getY() - e1.getY() > FLING_MIN_DISTANCE && Math.abs(velocityX) > FLING_MIN_VELOCITY) {
                // Fling down
                //Log.d(CLASS_TAG, "亮度减少");
            }
        } else {
            //fling in right area
            MainActivity.changeVolume(v_c_r);
        }
        return false;
    }

    /**
     * 停止视频线程，并且停止当前activity
     */
    public static void stop() {
        if (RUNNING_FLAG == true) {
            RUNNING_FLAG = false;
        }
    }

}
