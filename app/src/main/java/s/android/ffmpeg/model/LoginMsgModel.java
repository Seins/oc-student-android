package s.android.ffmpeg.model;

import java.util.Arrays;

import s.android.ffmpeg.util.MessagePackageUtil;

/**
 * Created by dengfs on 2015/11/7.
 */
public class LoginMsgModel {

    private Content content;
    private Head head;

    public LoginMsgModel(String roomid, String clientid) {
        this.content = new Content(roomid, clientid);
        this.head = new Head(content.getContent().length);
    }

    public byte[] getContent() {
        return content.getContent();
    }

    public byte[] getHead() {
        return head.getHead();
    }

    class Content {
        byte[] clientid = new byte[20];
        byte[] roomid = new byte[20];
        byte[] role = new byte[20];

        Content(String roomId, String clientId) {
            // Initial content byte arrays
            for (int i = 0; i < 20; i++) {
                this.clientid[i] = '\0';
                this.roomid[i] = '\0';
                this.role[i] = '\0';
            }
            // copy array from params
            System.arraycopy(roomId.getBytes(), 0, this.roomid, 0,
                    roomId.getBytes().length);
            System.arraycopy(clientId.getBytes(), 0, this.clientid, 0,
                    clientId.getBytes().length);
            byte[] roleStr = "student".getBytes();
            System.arraycopy(roleStr, 0, this.role, 0, roleStr.length);
        }

        public byte[] getContent() {
            byte[] content = sysCopy(this.clientid, this.roomid, this.role);
            return content;
        }
    }

    class Head {
        byte[] flag = { 'A', 'D' };
        byte[] size;
        byte[] type = MessagePackageUtil.int2byte(1001);

        public Head(int size) {
            this.size = MessagePackageUtil.int2byte(size);
        }

        public byte[] getHead() {
            return sysCopy(this.flag, this.size, this.type);
        }
    }

    public static byte[] sysCopy(byte[]... srcArrays) {
        int len = 0;
        for (byte[] srcArray : srcArrays) {
            len += srcArray.length;
        }
        byte[] destArray = new byte[len];
        int destLen = 0;
        for (byte[] srcArray : srcArrays) {
            System.arraycopy(srcArray, 0, destArray, destLen, srcArray.length);
            destLen += srcArray.length;
        }
        return destArray;
    }

    @Override
    public String toString() {
        return "[head]"+Arrays.toString(getHead()) + "\r\n [body]"
                + Arrays.toString(getContent());
    }

    public static void main(String[] args) {
       /* Runnable testR = new Runnable() {
            @SuppressWarnings("resource")
            @Override
            public void run() {
                try {
                    Socket socket = new Socket("127.0.0.1", 2233);
                    socket.setKeepAlive(true);
                    if (socket.isConnected()) {
                        DataOutputStream writer = new DataOutputStream(
                                socket.getOutputStream());
                        DataInputStream reader = new DataInputStream(
                                socket.getInputStream());
                        LoginMsgModel msg = new LoginMsgModel("coach",
                                "student");
                        writer.write(msg.getHead());
                        writer.flush();
                        Thread.sleep(300);
                        writer.write(msg.getContent());
                        writer.flush();
                        System.out.println("登录数据输出结束...");
                        System.out.println(msg.toString());
                        writer.close();
                        reader.close();
                        socket.close();
                    } else {
                        throw new Exception("Socket 连接失败!");
                    }
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        };

        Thread socketT = new Thread(testR);
        socketT.start();*/
    }
}
