/************************************************************
 * @版权所有 [(c)2014 Chan]<p>
 * @项目名称 [LMS视讯平台]<p>
 * @包名称 [cn.edu.fjnu.cse.lms.video.client.utils]<p>
 * @文件名 [MD5Util.java]<p>
 * @功能描述 []<p>
 * @创建日期 [2014-5-17 上午8:40:07]<p>
 * @作者 [李笃昌]<p>
 * @修改日期 [2014-5-17 上午8:40:07]<p>
 * @版本号 [v1.0]<p>
 ************************************************************/
package s.android.ffmpeg.util;

import java.security.MessageDigest;

/************************************************************
 * @版权所有 [(c)2014 Chan]<p>
 * @文件描述 []<p>
 * @创建日期 [2014-5-17 上午8:40:07]<p>
 * @作者 [李笃昌]<p>
 * @版本号 [v1.0]<p>
 ************************************************************/
public final class MD5Util {
    /**
     * @创建日期 [2014-5-17 上午8:47:17]<p>
     * @作者 [李笃昌]<p>
     * @功能描述 [MD5加码 生成32位md5码]<p>
     * @参数及返回值说明
     *  	@param str
     *  	@return []
     */
    public static String string2MD5(String str) {
        MessageDigest md5 = null;
        try {
            md5 = MessageDigest.getInstance("MD5");
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
            return "";
        }
        char[] charArray = str.toCharArray();
        byte[] byteArray = new byte[charArray.length];

        for (int i = 0; i < charArray.length; i++)
            byteArray[i] = (byte) charArray[i];
        byte[] md5Bytes = md5.digest(byteArray);
        StringBuffer hexValue = new StringBuffer();
        for (int i = 0; i < md5Bytes.length; i++) {
            int val = (md5Bytes[i]) & 0xff;
            if (val < 16)
                hexValue.append("0");
            hexValue.append(Integer.toHexString(val));
        }
        return hexValue.toString();
    }
}
