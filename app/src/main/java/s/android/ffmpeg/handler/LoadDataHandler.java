package s.android.ffmpeg.handler;

/**
 * @类名称 ：LoadDataHandler.java
 * @类描述 ：
 * @修改记录：
 * @日期 ：2014-4-18 上午11:43:07 @作者：sein
 * @描述 ：
 * @日期 ：2014-4-18 上午11:43:07 @修改人：
 **/

public interface LoadDataHandler {
    /**
     * @return void
     * @throws
     * @函数功能说明:加载数据时调用
     * @修改者名字:Seins
     * @修改日期:2014-3-30
     * @修改内容:
     * @参数：
     */
    void onStart();

    /**
     * @return void
     * @throws
     * @函数功能说明:加载数据调用,得到缓存数据
     * @修改者名字:Seins
     * @修改日期:2014-3-30
     * @修改内容:
     * @参数： @param data
     */
    void onLoadCaches(String data);

    /**
     * @return void
     * @throws
     * @函数功能说明: 当调用服务器接口成功获取数据时, 调用这个方法
     * @修改者名字:Seins
     * @修改日期:2014-3-30
     * @修改内容:
     * @参数： @param data
     */
    void onSuccess(String data);

    /**
     * @return void
     * @throws
     * @函数功能说明:当调用服务器接口获取数据失败时,调用这个方法
     * @修改者名字:Seins
     * @修改日期:2014-3-30
     * @修改内容:
     * @参数： @param error 出错原因
     * @参数： @param message 原因描述
     */
    void onFailure(String error, String message);

    /**
     * @return void
     * @throws
     * @函数功能说明:加载完成时调用
     * @修改者名字:Seins
     * @修改日期:2014-3-30
     * @修改内容:
     * @参数：
     */
    void onFinish();
}
