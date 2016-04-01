package s.android.ffmpeg.util;

import android.util.Log;

import java.util.Arrays;

public class MessagePackageUtil {
    public static final String CLASS_TAG = "MessageUtils";

    /**
     * @param content
     * @return
     * @方法名：getHead
     * @方法描述：包头获取方法
     * @返回类型：char[]
     * @修改人:邓风森
     * @修改时间:2014-11-8 下午9:37:54
     */
    public static char[] getHead(char[] content) {
        char[] pkt_head = new char[10];
        if (content == null) {
            try {
                throw new Exception("参数缺失：content:" + new String(content));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        for (int i = 0; i < pkt_head.length; i++) {
            if (i < content.length) {
                pkt_head[i] = content[i];
            } else {
                pkt_head[i] = '\0';
            }
        }
        return pkt_head;
    }

    public static char[] getHead(int size, int type) {
        char[] content = new char[10];
        content[0] = 'A';
        content[1] = 'D';
        System.arraycopy(int2byte(size), 0, content, 2, int2byte(size).length);
        System.arraycopy(int2byte(type), 0, content, 2 + int2byte(size).length, int2byte(type).length);
        return content;
    }


    /**
     * @param clientId
     * @param roomId
     * @param realm
     * @return
     * @throws Exception
     * @方法名：getContent
     * @方法描述：包体获取方法
     * @返回类型：char[]
     * @修改人:邓风森
     * @修改时间:2014-11-8 下午9:37:50
     */
    public static char[] getContent(char[] clientId, char[] roomId, char[] realm) throws Exception {
        char[] pkt_vs_login = new char[60];
        if (clientId == null || roomId == null || realm == null) {
            throw new Exception("参数缺失：clitenId:" + new String(clientId) + "\r\n roomId:" + new String(roomId) + "\r\n realm:" + new String(realm));
        }
        for (int i = 0; i < pkt_vs_login.length; i++) {
            pkt_vs_login[i] = '\0';
        }
        for (int k = 0; k < clientId.length; k++) {
            pkt_vs_login[k] = clientId[k];
        }
        for (int j = 0; j < roomId.length; j++) {
            pkt_vs_login[j + 20] = roomId[j];
        }
        for (int l = 0; l < realm.length; l++) {
            pkt_vs_login[l + 40] = realm[l];
        }
        return pkt_vs_login;
    }


    public static byte[] getContentByRTP(int clientId, int roomId, int len, byte[] src) throws Exception {
        byte[] data = new byte[1024];
        System.arraycopy(int2byte(clientId), 0, data, 0, int2byte(clientId).length);
        System.arraycopy(int2byte(roomId), 0, data, 4, int2byte(roomId).length);
        System.arraycopy(int2byte(len), 0, data, 8, int2byte(len).length);
        System.arraycopy(src, 0, data, 12, src.length);
        return data;
    }

    public static byte[] getContentByRTP(int roomId) {
        byte[] data = new byte[1024];
        byte[] temp = int2byte(roomId);
        System.arraycopy(temp, 0, data, 0, temp.length);
        for (int i = temp.length; i < 1024; i++) {
            data[i] = '\0';
        }
        Log.d(CLASS_TAG, "RTP Data before send:" + Arrays.toString(data));
        return data;
    }

    public static byte[] int2byte(int res) {
        byte[] targets = new byte[4];

        targets[0] = (byte) (res & 0xff);// 最低位
        targets[1] = (byte) ((res >> 8) & 0xff);// 次低位
        targets[2] = (byte) ((res >> 16) & 0xff);// 次高位
        targets[3] = (byte) (res >>> 24);// 最高位,无符号右移。
        return targets;
    }

    public static int bytesToInt(byte[] bRefArr) {
        int iOutcome = 0;
        byte bLoop;

        for (int i = 0; i < bRefArr.length; i++) {
            bLoop = bRefArr[i];
            iOutcome += (bLoop & 0xFF) << (8 * i);
        }
        return iOutcome;
    }
}
