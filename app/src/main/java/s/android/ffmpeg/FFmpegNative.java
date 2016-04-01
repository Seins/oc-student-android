package s.android.ffmpeg;

public class FFmpegNative {
    static {
        System.loadLibrary("avutil-52");
        System.loadLibrary("avcodec-55");
        System.loadLibrary("swresample-0");
        System.loadLibrary("avformat-55");
        System.loadLibrary("swscale-2");
        System.loadLibrary("avfilter-4");
        System.loadLibrary("avdevice-55");
        System.loadLibrary("ffmpeg_codec");
    }

    public static native int decoderInit(int iwidth, int iheight);

    public static native int decodeData(byte[] in, int insize, byte[] out);

    public static native void releaseDecoder();
}
