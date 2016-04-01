package s.android.ffmpeg.util;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.TypeReference;

;

public class FastJsonUtil {

    /**
     * 函数名称 : getObject
     * 功能描述 : 解析成JSON对象
     * 参数及返回值说明：
     *
     * @param jsonString
     * @param cls
     * @return 修改记录：
     * 日期 ：2014-4-16 下午7:46:56	修改人：李笃昌
     * 描述	：
     */
    public static <T> T getObject(String jsonString, Class<T> cls) {
        T t = null;
        try {
            t = JSON.parseObject(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return t;
    }

    /**
     * 函数名称 : getObjects
     * 功能描述 : 解析成对象链表
     * 参数及返回值说明：
     *
     * @param jsonString
     * @param cls
     * @return 修改记录：
     * 日期 ：2014-4-16 下午7:47:18	修改人：李笃昌
     * 描述	：
     */
    public static <T> List<T> getObjects(String jsonString, Class<T> cls) {
        List<T> list = new ArrayList<T>();
        try {
            list = JSON.parseArray(jsonString, cls);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 函数名称 : listKeyMaps
     * 功能描述 : List<Map<String, Object>>
     * 参数及返回值说明：
     *
     * @param jsonString
     * @return 修改记录：
     * 日期 ：2014-4-16 下午7:47:43	修改人：李笃昌
     * 描述	：
     */
    public static List<Map<String, Object>> listKeyMaps(String jsonString) {
        List<Map<String, Object>> list = new ArrayList<Map<String, Object>>();
        try {
            list = JSON.parseObject(jsonString,
                    new TypeReference<List<Map<String, Object>>>() {
                    });
        } catch (Exception e) {
            e.printStackTrace();
        }
        return list;
    }

    /**
     * 函数名称 : toJSONString
     * 功能描述 : 转成Json字符串
     * 参数及返回值说明：
     *
     * @param object
     * @return 修改记录：
     * 日期 ：2014-4-16 下午7:42:07	修改人：李笃昌
     * 描述	：
     */
    public static String toJSONString(Object object) {
        return JSON.toJSONString(object).toString();
    }
}
