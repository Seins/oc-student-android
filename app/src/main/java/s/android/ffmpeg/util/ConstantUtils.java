package s.android.ffmpeg.util;

/**
 * Created by CodingMates on 2015/1/28.
 */
public abstract class ConstantUtils {

    //MEDIA服务器ip
    public static final String SERVER_MEDIA_IP = "192.168.137.1";
    //视频端口
    public static final Integer SERVER_MEDIA_PORT_VIDEO = 2233;
    //音频端口
    public static final Integer SERVER_MEDIA_PORT_AUDIO = 2244;

    public static final Integer SERVER_MEDIA_PORT_AUDIO_RTP = 62000;
    public static final Integer SERVER_MEDIA_PORT_AUDIO_RTCP = 62001;

    //WEB业务服务器ip
    public static final String SERVER_WEB_IP = "192.168.137.1";
    //端口
    public static final Integer SERVER_WEB_PORT = 8080;
    //登陆
    public static final String URL_LOGIN = "student/login.do";
    //即将上课
    public static final String URL_COURSE_WILL = "student/course/art/will.do";

    public static final String URL_COURSE_HISTORY = "student/course/history.do";
    //修改个人信息
    public static final String URL_MODIFY_INFO = "member/modify.do";
    //获取个人信息
    public static final String URL_GET_INFO = "student/info.do";

    /**
     * 获取完整url
     *
     * @param ip
     * @param port
     * @param uri
     * @return
     */
    public static String getURL(String ip, Integer port, String uri) {
        return "http://" + ip + ":" + port + "/OCSS/" + uri;
    }
}
